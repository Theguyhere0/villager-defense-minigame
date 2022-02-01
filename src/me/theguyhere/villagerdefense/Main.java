package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.listeners.*;
import me.theguyhere.villagerdefense.packets.MetadataHelper;
import me.theguyhere.villagerdefense.packets.PacketReader;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
import me.theguyhere.villagerdefense.tools.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Objects;

public class Main extends JavaPlugin {
	// Yaml file managers
	private final DataManager arenaData = new DataManager(this, "arenaData.yml");
	private final DataManager playerData = new DataManager(this, "playerData.yml");
	private final DataManager languageData = new DataManager(this, "languages/" +
			getConfig().getString("locale") + ".yml");

	// Global instance variables
	private final PacketReader reader = new PacketReader();
	private ArenaManager arenaManager;
	private boolean loaded = false;

	private boolean outdated = false;
	int configVersion = 6;
	int arenaDataVersion = 4;
	int playerDataVersion = 1;
	int spawnTableVersion = 1;
	int languageFileVersion = 11;
	int defaultSpawnVersion = 2;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		MetadataHelper.init();
		arenaManager = new ArenaManager(this);

		Commands commands = new Commands(this);

		checkArenas();

		// Set up commands and tab complete
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist").setExecutor(commands);
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist")
				.setTabCompleter(new CommandTab());

		// Register event listeners
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new JoinListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		pm.registerEvents(new ClickPortalListener(), this);
		pm.registerEvents(new GameListener(this), this);
		pm.registerEvents(new ArenaListener(this), this);
		pm.registerEvents(new AbilityListener(this), this);
		pm.registerEvents(new ChallengeListener(this), this);
		pm.registerEvents(new WorldListener(this), this);

		// Inject online players into packet reader
		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			CommunicationManager.debugError("Your config.yml is outdated!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update to the latest version (" + ChatColor.BLUE + configVersion + ChatColor.RED +
					") to ensure compatibility.");
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			CommunicationManager.debugError("Your arenaData.yml is no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your arenaData.yml has been updated.",
					0);
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			CommunicationManager.debugError("Your playerData.yml is no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your playerData.yml has been updated.",
					0);
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			CommunicationManager.debugError("Your spawn tables are no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer spawn table data to version " + ChatColor.BLUE + spawnTableVersion +
					ChatColor.RED + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your spawn tables have been updated.",
					0);
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			CommunicationManager.debugInfo("The default.yml spawn table has been updated!", 0);
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Updating to version" + ChatColor.BLUE + defaultSpawnVersion + ChatColor.WHITE +
					" is optional but recommended.");
			CommunicationManager.debugInfo("Please do not update your config.yml unless your default.yml has been updated.",
					0);
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			CommunicationManager.debugError("You language files are no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update en_US.yml and update any other language files to version " + ChatColor.BLUE +
					languageFileVersion + ChatColor.RED + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your language files have been updated.",
					0);
			outdated = true;
		}

		// Set teams
		if (Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam("monsters") == null) {
			Team monsters = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard()
					.registerNewTeam("monsters");
			monsters.setColor(ChatColor.RED);
			monsters.setDisplayName(ChatColor.RED + "Monsters");
		}
		if (Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam("villagers") == null) {
			Team villagers = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard()
					.registerNewTeam("villagers");
			villagers.setColor(ChatColor.GREEN);
			villagers.setDisplayName(ChatColor.GREEN + "Villagers");
		}
	}

	@Override
	public void onDisable() {
		// Remove uninject players
		for (Player player : Bukkit.getOnlinePlayers())
			reader.uninject(player);

		// Clear every valid arena and remove all portals
		ArenaManager.cleanAll();
		ArenaManager.removePortals();
	}

	public PacketReader getReader() {
		return reader;
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	// Returns arena data
	public FileConfiguration getArenaData() {
		return arenaData.getConfig();
	}

	// Saves arena data changes
	public void saveArenaData() {
		arenaData.saveConfig();
	}

	// Returns player data
	public FileConfiguration getPlayerData() {
		return playerData.getConfig();
	}

	// Saves arena data changes
	public void savePlayerData() {
		playerData.saveConfig();
	}

	public FileConfiguration getLanguageData() {
		return languageData.getConfig();
	}

	// Check arenas for close
	private void checkArenas() {
		Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).forEach(Arena::checkClose);
	}

	public boolean isOutdated() {
		return outdated;
	}

	public void setLoaded() {
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
