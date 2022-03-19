package me.theguyhere.villagerdefense.plugin;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Log;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.GUI.Inventories;
import me.theguyhere.villagerdefense.plugin.commands.CommandTab;
import me.theguyhere.villagerdefense.plugin.commands.Commands;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.EnchantingBook;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.listeners.*;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
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
	private final List<String> unloadedWorlds = new ArrayList<>();

	// Global state variables
	private static boolean outdated = false; // DO NOT CHANGE
	public static final boolean releaseMode = true;
	public static final int configVersion = 7;
	public static final int arenaDataVersion = 4;
	public static final int playerDataVersion = 1;
	public static final int spawnTableVersion = 1;
	public static final int languageFileVersion = 12;
	public static final int defaultSpawnVersion = 2;

	@Override
	public void onEnable() {
		// Set up initial classes
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		Commands commands = new Commands(this);
		Kit.setPlugin(this);
		Challenge.setPlugin(this);
		EnchantingBook.setPlugin(this);
		GameItems.setPlugin(this);
		Inventories.setPlugin(this);

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
			urgentConsoleWarning("Your config.yml is outdated!");
			urgentConsoleWarning("Please update to the latest version (" + ChatColor.BLUE + configVersion +
					ChatColor.RED + ") to ensure compatibility.");
			urgentConsoleWarning("Please only update AFTER updating all other data files.");
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			urgentConsoleWarning("Your arenaData.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your arenaData.yml has been " +
					"updated.");
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			urgentConsoleWarning("Your playerData.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			urgentConsoleWarning("Please do not update your config.yml until your playerData.yml has been " +
					"updated.");
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			urgentConsoleWarning("Your spawn tables are no longer supported with this version!");
			urgentConsoleWarning("Please transfer spawn table data to version " + ChatColor.BLUE +
					spawnTableVersion + ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your spawn tables have been " +
					"updated.");
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			consoleMessage("The default.yml spawn table has been updated!");
			consoleMessage("Updating to version" + ChatColor.BLUE + defaultSpawnVersion + ChatColor.WHITE +
					" is optional but recommended.");
			consoleMessage("Please do not update your config.yml unless your default.yml has been updated.");
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			urgentConsoleWarning("You language files are no longer supported with this version!");
			urgentConsoleWarning("Please update en_US.yml and update any other language files to version " +
					ChatColor.BLUE + languageFileVersion + ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your language files have been " +
					"updated.");
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

		// Gather unloaded world list
		ConfigurationSection section;

		// Relevant worlds from arenas
		section = getArenaData().getConfigurationSection("");
		if (section != null)
			section.getKeys(false)
					.forEach(path -> {
						if (path.charAt(0) == 'a' && path.length() < 4) {
							// Arena board world
							checkAddUnloadedWorld(getArenaData().getString(path + ".arenaBoard.world"));

							// Arena world
							checkAddUnloadedWorld(getArenaData().getString(path + ".spawn.world"));

							// Portal world
							checkAddUnloadedWorld(getArenaData().getString(path + ".portal.world"));
						}
					});

		// Relevant worlds from info boards
		section = getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section.getKeys(false)
					.forEach(path ->
							checkAddUnloadedWorld(getArenaData().getString("infoBoard." + path + ".world")));

		// Relevant worlds from leaderboards
		section = getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section.getKeys(false)
					.forEach(path ->
							checkAddUnloadedWorld(getArenaData().getString("leaderboard." + path + ".world")));

		// Lobby world
		checkAddUnloadedWorld(getArenaData().getString("lobby.world"));

		// Set GameManager
		resetGameManager();

		// Remind if this build is release
		if (!releaseMode) {
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			urgentConsoleWarning("");
			urgentConsoleWarning("This build is not meant for release! Testing code may still be active.");
			urgentConsoleWarning("");
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}

		// Check default debug level
		if (releaseMode && CommunicationManager.getDebugLevel() > 1) {
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			urgentConsoleWarning("");
			urgentConsoleWarning("Default debug level should be set to 0 or 1!");
			urgentConsoleWarning("");
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}
	}

	@Override
	public void onDisable() {
		// Clear packet listeners
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.uninjectPacketListener(player);

		// Wipe every valid arena
		GameManager.wipeArenas();
	}

	public void reload() {
		// Once again check for config
		saveDefaultConfig();

		// Reset "outdated" flag
		outdated = false;

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			urgentConsoleWarning("Your config.yml is outdated!");
			urgentConsoleWarning("Please update to the latest version (" + ChatColor.BLUE + configVersion +
					ChatColor.RED + ") to ensure compatibility.");
			urgentConsoleWarning("Please only update AFTER updating all other data files.");
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			urgentConsoleWarning("Your arenaData.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your arenaData.yml has been " +
					"updated.");
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			urgentConsoleWarning("Your playerData.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			urgentConsoleWarning("Please do not update your config.yml until your playerData.yml has been " +
					"updated.");
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			urgentConsoleWarning("Your spawn tables are no longer supported with this version!");
			urgentConsoleWarning("Please transfer spawn table data to version " + ChatColor.BLUE +
					spawnTableVersion + ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your spawn tables have been " +
					"updated.");
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			consoleMessage("The default.yml spawn table has been updated!");
			consoleMessage("Updating to version" + ChatColor.BLUE + defaultSpawnVersion + ChatColor.WHITE +
					" is optional but recommended.");
			consoleMessage("Please do not update your config.yml unless your default.yml has been updated.");
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			urgentConsoleWarning("You language files are no longer supported with this version!");
			urgentConsoleWarning("Please update en_US.yml and update any other language files to version " +
					ChatColor.BLUE + languageFileVersion + ChatColor.RED + ".");
			urgentConsoleWarning("Please do not update your config.yml until your language files have been " +
					"updated.");
			outdated = true;
		}

		// Set as unloaded while reloading
		setLoaded(false);

		// Gather unloaded world list
		ConfigurationSection section;

		// Relevant worlds from arenas
		section = getArenaData().getConfigurationSection("");
		if (section != null)
			section.getKeys(false)
					.forEach(path -> {
						if (path.charAt(0) == 'a' && path.length() < 4) {
							// Arena board world
							checkAddUnloadedWorld(getArenaData().getString(path + ".arenaBoard.world"));

							// Arena world
							checkAddUnloadedWorld(getArenaData().getString(path + ".spawn.world"));

							// Portal world
							checkAddUnloadedWorld(getArenaData().getString(path + ".portal.world"));
						}
					});

		// Relevant worlds from info boards
		section = getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section.getKeys(false)
					.forEach(path ->
							checkAddUnloadedWorld(getArenaData().getString("infoBoard." + path + ".world")));

		// Relevant worlds from leaderboards
		section = getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section.getKeys(false)
					.forEach(path ->
							checkAddUnloadedWorld(getArenaData().getString("leaderboard." + path + ".world")));

		// Lobby world
		checkAddUnloadedWorld(getArenaData().getString("lobby.world"));

		// Set GameManager
		resetGameManager();
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public void resetGameManager() {
		gameManager = new GameManager(this);

		// Check for proper initialization with worlds
		if (unloadedWorlds.size() > 0) {
			urgentConsoleWarning("Plugin not properly initialized! The following worlds are not loaded yet: " +
					unloadedWorlds);
		} else consoleMessage(ChatColor.GREEN + "All worlds fully loaded. The plugin is properly initialized.");
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

	public String getLanguageString(String path) {
		if (!languageData.getConfig().contains(path))
			CommunicationManager.debugError("The key '" + path + "' is either missing or corrupt in the active " +
					"language file", 0, true);
		return languageData.getConfig().getString(path);
	}

	public String getLanguageStringFormatted(String path, String replacement) {
		return String.format(getLanguageString(path), replacement);
	}

	public String getLanguageStringFormatted(String path, String replace1, String replace2) {
		return String.format(getLanguageString(path), replace1, replace2);
	}

	public static boolean isOutdated() {
		return outdated;
	}

	public void setLoaded(boolean state) {
		loaded = state;
	}

	public boolean isLoaded() {
		return loaded;
	}

	// Quick way to send test messages to console but remembering to take them down before release
	public static void testInfo(String msg, boolean stackTrace) {
		if (releaseMode) {
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
			urgentConsoleWarning("");
			urgentConsoleWarning("This should not be here!");
			urgentConsoleWarning("");
			urgentConsoleWarning("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
		}

		Log.info(msg);

		if (stackTrace || releaseMode)
			Thread.dumpStack();

	}

	private static void urgentConsoleWarning(String msg) {
		Bukkit.getConsoleSender().sendMessage("[VillagerDefense] " + ChatColor.RED + msg);
	}

	private static void consoleMessage(String msg) {
		Bukkit.getConsoleSender().sendMessage("[VillagerDefense] " + msg);
	}
	
	public List<String> getUnloadedWorlds() {
		return unloadedWorlds;
	}

	public void loadWorld(String worldName) {
		unloadedWorlds.remove(worldName);
	}

	private void checkAddUnloadedWorld(String worldName) {
		if (worldName == null)
			return;

		if (unloadedWorlds.contains(worldName))
			return;

		if (Bukkit.getWorld(worldName) != null)
			return;

		unloadedWorlds.add(worldName);
	}
}
