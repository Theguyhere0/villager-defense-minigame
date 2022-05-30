package me.theguyhere.villagerdefense.plugin;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Log;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.commands.CommandTab;
import me.theguyhere.villagerdefense.plugin.commands.Commands;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLanguageKeyException;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.listeners.*;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
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
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {
	// Singleton instance
	public static Main plugin;

	// Yaml file managers
	private DataManager arenaData;
	private DataManager playerData;
	private DataManager customEffects;

	// Global instance variables
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();
	private boolean loaded = false;
	private final List<String> unloadedWorlds = new ArrayList<>();

	// Global state variables
	private static boolean outdated = false; // DO NOT CHANGE
	public static final boolean releaseMode = false;
	public static final int configVersion = 8;
	public static final int arenaDataVersion = 6;
	public static final int playerDataVersion = 2;
	public static final int spawnTableVersion = 1;
	public static final int languageFileVersion = 16;
	public static final int defaultSpawnVersion = 2;
	public static final int customEffectsVersion = 1;

	@Override
	public void onEnable() {
		Main.plugin = this;

		arenaData = new DataManager("arenaData.yml");
		playerData = new DataManager("playerData.yml");
		customEffects = new DataManager("customEffects.yml");
		DataManager languageData = new DataManager("languages/" + getConfig().getString("locale") +
				".yml");

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

		// Check if customEffects.yml is outdated
		if (getConfig().getInt("customEffects") < customEffectsVersion) {
			urgentConsoleWarning("Your customEffects.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer player data to version " + ChatColor.BLUE +
					customEffectsVersion + ChatColor.BLUE + ".");
			urgentConsoleWarning("Please do not update your config.yml until your customEffects.yml has been " +
					"updated.");
			outdated = true;
		}

		// Set up commands and tab complete
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist").setExecutor(new Commands());
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist")
				.setTabCompleter(new CommandTab());

		// Set up initial classes
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		try {
			LanguageManager.init(languageData.getConfig());
		} catch (InvalidLanguageKeyException e) {
			e.printStackTrace();
		}
		GameItems.init();

		// Register event listeners
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new JoinListener(), this);
		pm.registerEvents(new ClickPortalListener(), this);
		pm.registerEvents(new GameListener(), this);
		pm.registerEvents(new ArenaListener(), this);
		pm.registerEvents(new AbilityListener(), this);
		pm.registerEvents(new ChallengeListener(), this);
		pm.registerEvents(new WorldListener(), this);
		pm.registerEvents(new BonusListener(), this);
		pm.registerEvents(new CustomEffectsListener(), this);

		// Add packet listeners for online players
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.injectPacketListener(player, new PacketListenerImp());

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

		// Relevant worlds from arenas + check for duplicate arena names
		AtomicBoolean duplicate = new AtomicBoolean(false);
		List<String> arenaNames = new ArrayList<>();
		section = getArenaData().getConfigurationSection("arena");
		if (section != null)
			section.getKeys(false)
					.forEach(id -> {
						String path = "arena." + id;

						// Check for name in list
						if (arenaNames.contains(getArenaData().getString(path + ".name")))
							duplicate.set(true);
						else arenaNames.add(getArenaData().getString(path + ".name"));

						// Arena board world
						checkAddUnloadedWorld(getArenaData().getString(path + ".arenaBoard.world"));

						// Arena world
						checkAddUnloadedWorld(getArenaData().getString(path + ".spawn.world"));

						// Portal world
						checkAddUnloadedWorld(getArenaData().getString(path + ".portal.world"));
					});

		if (duplicate.get()) {
			urgentConsoleWarning("Some of your arenas have duplicate names! That is not allowed :(");
			urgentConsoleWarning("Shutting down plugin to protect your data. Please fix and restart server.");
			Main plugin = this;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this,
					() -> getServer().getPluginManager().disablePlugin(plugin), 0);
		}

		// Relevant worlds from info boards
		section = getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section.getKeys(false)
					.forEach(id ->
							checkAddUnloadedWorld(getArenaData().getString("infoBoard." + id + ".world")));

		// Relevant worlds from leaderboards
		section = getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section.getKeys(false)
					.forEach(id ->
							checkAddUnloadedWorld(getArenaData().getString("leaderboard." + id + ".world")));

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

		arenaData = new DataManager("arenaData.yml");
		playerData = new DataManager("playerData.yml");
		customEffects = new DataManager("customEffects.yml");
		DataManager languageData = new DataManager("languages/" + getConfig().getString("locale") +
				".yml");
		try {
			LanguageManager.init(languageData.getConfig());
		} catch (InvalidLanguageKeyException e) {
			e.printStackTrace();
		}

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

		// Check if customEffects.yml is outdated
		if (getConfig().getInt("customEffects") < customEffectsVersion) {
			urgentConsoleWarning("Your customEffects.yml is no longer supported with this version!");
			urgentConsoleWarning("Please transfer player data to version " + ChatColor.BLUE +
					customEffectsVersion + ChatColor.BLUE + ".");
			urgentConsoleWarning("Please do not update your config.yml until your customEffects.yml has been " +
					"updated.");
			outdated = true;
		}

		// Set as unloaded while reloading
		setLoaded(false);

		// Gather unloaded world list
		ConfigurationSection section;

		// Relevant worlds from arenas + check for duplicate arena names
		AtomicBoolean duplicate = new AtomicBoolean(false);
		List<String> arenaNames = new ArrayList<>();
		section = getArenaData().getConfigurationSection("arena");
		if (section != null)
			section.getKeys(false)
					.forEach(id -> {
						String path = "arena." + id;

						// Check for name in list
						if (arenaNames.contains(getArenaData().getString(path + ".name")))
							duplicate.set(true);
						else arenaNames.add(getArenaData().getString(path + ".name"));

						// Arena board world
						checkAddUnloadedWorld(getArenaData().getString(path + ".arenaBoard.world"));

						// Arena world
						checkAddUnloadedWorld(getArenaData().getString(path + ".spawn.world"));

						// Portal world
						checkAddUnloadedWorld(getArenaData().getString(path + ".portal.world"));
					});

		if (duplicate.get()) {
			urgentConsoleWarning("Some of your arenas have duplicate names! That is not allowed :(");
			urgentConsoleWarning("Shutting down plugin to protect your data. Please fix and restart server.");
			Main plugin = this;
			Bukkit.getScheduler().scheduleSyncDelayedTask(this,
					() -> getServer().getPluginManager().disablePlugin(plugin), 0);
		}

		// Relevant worlds from info boards
		section = getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section.getKeys(false)
					.forEach(id ->
							checkAddUnloadedWorld(getArenaData().getString("infoBoard." + id + ".world")));

		// Relevant worlds from leaderboards
		section = getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section.getKeys(false)
					.forEach(id ->
							checkAddUnloadedWorld(getArenaData().getString("leaderboard." + id + ".world")));

		// Lobby world
		checkAddUnloadedWorld(getArenaData().getString("lobby.world"));

		// Set GameManager
		resetGameManager();
	}

	public void resetGameManager() {
		GameManager.init();

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

	// Returns custom effects
	public FileConfiguration getCustomEffects() {
		return customEffects.getConfig();
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
