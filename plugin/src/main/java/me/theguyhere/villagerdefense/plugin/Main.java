package me.theguyhere.villagerdefense.plugin;

import me.theguyhere.villagerdefense.common.Log;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.commands.CommandTab;
import me.theguyhere.villagerdefense.plugin.commands.Commands;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.listeners.*;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class Main extends JavaPlugin {
	// Yaml file managers
	private final DataManager arenaData = new DataManager(this, "arenaData.yml");
	private final DataManager playerData = new DataManager(this, "playerData.yml");
	private final DataManager languageData = new DataManager(this, "languages/" +
			getConfig().getString("locale") + ".yml");

	// Global instance variables
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();
	private GameManager gameManager;
	private boolean loaded = false;

	// Global state variables
	private boolean outdated = false;
	public static final boolean releaseMode = true;
	public int configVersion = 6;
	public int arenaDataVersion = 4;
	public int playerDataVersion = 1;
	public int spawnTableVersion = 1;
	public int languageFileVersion = 11;
	public int defaultSpawnVersion = 2;

	@Override
	public void onEnable() {
		// Set up initial classes
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		Commands commands = new Commands(this);

		// Set up commands and tab complete
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist").setExecutor(commands);
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist")
				.setTabCompleter(new CommandTab());

		// Register event listeners
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new JoinListener(this), this);
		pm.registerEvents(new ClickPortalListener(), this);
		pm.registerEvents(new GameListener(this), this);
		pm.registerEvents(new ArenaListener(this), this);
		pm.registerEvents(new AbilityListener(this), this);
		pm.registerEvents(new ChallengeListener(this), this);
		pm.registerEvents(new WorldListener(this), this);

		// Add packet listeners for online players
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.injectPacketListener(player, new PacketListenerImp());

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
					"Please transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your arenaData.yml has been updated.",
					0);
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			CommunicationManager.debugError("Your playerData.yml is no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			CommunicationManager.debugError("Please do not update your config.yml until your playerData.yml has been updated.",
					0);
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			CommunicationManager.debugError("Your spawn tables are no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please transfer spawn table data to version " + ChatColor.BLUE + spawnTableVersion +
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

		// Set ArenaManager
		gameManager = new GameManager(this);

		// Remind if this build is release
		if (!releaseMode) {
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			Log.warning("");
			Log.warning("This build is not meant for release! Testing code may still be active.");
			Log.warning("");
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}

		// Check default debug level
		if (releaseMode && CommunicationManager.getDebugLevel() > 1) {
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			Log.warning("");
			Log.warning("Default debug level should be set to 0 or 1!");
			Log.warning("");
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}
	}

	@Override
	public void onDisable() {
		// Clear packet listeners
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.uninjectPacketListener(player);

		// Clear every valid arena and remove all portals
		GameManager.cleanAll();
		GameManager.removePortals();
	}

	public GameManager getGameManager() {
		return gameManager;
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

	public boolean isOutdated() {
		return outdated;
	}

	public void setLoaded() {
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// Quick way to send test messages to console but remembering to take them down before release
	public static void testInfo(String msg, boolean stackTrace) {
		if (releaseMode) {
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			Log.warning("");
			Log.warning("This should not be here!");
			Log.warning("");
			Log.warning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}

		Log.info(msg);

		if (stackTrace || releaseMode)
			Thread.dumpStack();

	}
}
