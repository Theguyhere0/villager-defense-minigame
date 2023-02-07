package me.theguyhere.villagerdefense.plugin;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.commands.CommandExecImp;
import me.theguyhere.villagerdefense.plugin.commands.TabCompleterImp;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLanguageKeyException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.listeners.*;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main extends JavaPlugin {
	// Singleton instance
	public static Main plugin;

	// Yaml file managers
	private static DataManager arenaData;
	private static DataManager playerData;
	private static DataManager customEffects;

	// Global instance variables
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();
	private static boolean loaded = false;
	private static final List<String> unloadedWorlds = new ArrayList<>();
	private static Economy economy;

	// Global state variables
	private static boolean outdated = false; // DO NOT CHANGE
	public static final boolean releaseMode = false;
	public static final int configVersion = 9;
	public static final int arenaDataVersion = 8;
	public static final int playerDataVersion = 3;
	public static final int spawnTableVersion = 3;
	public static final int languageFileVersion = 28;
	public static final int defaultSpawnVersion = 4;
	public static final int customEffectsVersion = 3;

	@Override
	public void onEnable() {
		Main.plugin = this;

		arenaData = new DataManager("arenaData.yml");
		playerData = new DataManager("playerData.yml");
		customEffects = new DataManager("customEffects.yml");
		DataManager languageData = new DataManager("languages/" + getConfig().getString("locale") +
				".yml");

		checkFileVersions();

		// Set up commands and tab complete
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist").setExecutor(new CommandExecImp());
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist")
				.setTabCompleter(new TabCompleterImp());

		// Schedule to register PAPI expansion
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
				new VDExpansion().register();
			}, Utils.secondsToTicks(1));

		// Try finding economy plugin
		setupEconomy();

		// Set up initial classes
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		try {
			LanguageManager.init(languageData.getConfig());
		} catch (InvalidLanguageKeyException e) {
			e.printStackTrace();
		}

		// Register event listeners
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new JoinListener(), this);
		pm.registerEvents(new ClickPortalListener(), this);
		pm.registerEvents(new GameListener(), this);
		pm.registerEvents(new ArenaListener(), this);
		pm.registerEvents(new AbilityListener(), this);
		pm.registerEvents(new ChallengeListener(), this);
		pm.registerEvents(new WorldListener(), this);

		// Add packet listeners for online players
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.injectPacketListener(player, new PacketListenerImp());

		checkArenaNameAndGatherUnloadedWorlds();

		// Remind if this build is not meant for release
		if (!releaseMode) {
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("This build is not meant for release! Testing code may still be active.", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
		}

		// Remind if default debug level is greater than 1 in release mode
		if (releaseMode && CommunicationManager.getDebugLevel() > 1) {
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("Default debug level should be set to 0 or 1!", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
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

		// Gather and check data and managers
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
		checkFileVersions();

		// Set as unloaded while reloading
		setLoaded(false);
		checkArenaNameAndGatherUnloadedWorlds();

		// Register expansion again
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			new VDExpansion().register();

		// Try finding economy plugin again
		setupEconomy();
	}

	public static void resetGameManager() {
		GameManager.init();

		// Check for proper initialization with worlds
		if (unloadedWorlds.size() > 0) {
			CommunicationManager.debugError("Plugin not properly initialized! The following worlds are not " +
					"loaded yet: " + unloadedWorlds, 0);
		} else CommunicationManager.debugConfirm("All worlds fully loaded. The plugin is properly initialized.",
				0);
	}

	// Returns arena data
	public static FileConfiguration getArenaData() {
		return arenaData.getConfig();
	}

	// Saves arena data changes
	public static void saveArenaData() {
		arenaData.saveConfig();
	}

	// Returns player data
	public static FileConfiguration getPlayerData() {
		return playerData.getConfig();
	}

	// Saves arena data changes
	public static void savePlayerData() {
		playerData.saveConfig();
	}

	// Returns custom effects
	public static FileConfiguration getCustomEffects() {
		return customEffects.getConfig();
	}

	// Saves custom effects data changes
	public static void saveCustomEffects() {
		customEffects.saveConfig();
	}

	public static boolean isOutdated() {
		return outdated;
	}

	public static void setLoaded(boolean state) {
		loaded = state;
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static Economy getEconomy() {
		return economy;
	}

	public static boolean hasCustomEconomy() {
		return plugin.getConfig().getBoolean("vaultEconomy") && economy != null;
	}

	// Quick way to send test messages to console but remembering to take them down before release
	@SuppressWarnings("unused")
	public static void testInfo(String msg, boolean stackTrace) {
		if (releaseMode) {
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("This should not be here!", 0);
			CommunicationManager.debugError("", 0);
			CommunicationManager.debugError("! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !", 0);
		}

		CommunicationManager.debugInfo(msg, 0);

		if (stackTrace || releaseMode)
			Thread.dumpStack();
	}

	public static List<String> getUnloadedWorlds() {
		return unloadedWorlds;
	}

	public static void loadWorld(String worldName) {
		unloadedWorlds.remove(worldName);
	}

	private static void checkAddUnloadedWorld(String worldName) {
		if (worldName == null)
			return;

		if (unloadedWorlds.contains(worldName))
			return;

		if (Bukkit.getWorld(worldName) != null)
			return;

		unloadedWorlds.add(worldName);
	}

	private void setupEconomy() {
		// Check for Vault plugin
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return;

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return;
		economy = rsp.getProvider();
	}

	private void checkFileVersions() {
		final String OUTDATED = "Your %s is/are no longer supported with this version!";
		final String FUTURE = "Your %s is/ar too futuristic!";
		final String UPDATE = "Please update to version %s to ensure compatibility.";
		final String REVERT = "Please revert to version %s to ensure compatibility.";
		final String CONFIG_WARNING = "Please do not update your config.yml until your %s has/have been updated.";

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "config.yml");
			CommunicationManager.debugError(UPDATE, 0, Integer.toString(configVersion));
			CommunicationManager.debugError("Please only update AFTER updating all other data files.",
					0);
			outdated = true;
		} else if (getConfig().getInt("version") > configVersion) {
			CommunicationManager.debugError(FUTURE, 0, "config.yml");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(configVersion));
			CommunicationManager.debugError("Please only update AFTER updating all other data files.",
					0);
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "arenaData.yml");
			CommunicationManager.debugError(UPDATE, 0 , Integer.toString(arenaDataVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "arenaData.yml");
			outdated = true;
		} else if (getConfig().getInt("arenaData") > arenaDataVersion) {
			CommunicationManager.debugError(FUTURE, 0, "arenaData.yml");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(arenaDataVersion));
			CommunicationManager.debugError("Please only update AFTER updating all other data files.",
					0);
			outdated = true;

		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "playerData.yml");
			CommunicationManager.debugError(UPDATE, 0 , Integer.toString(playerDataVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "playerData.yml");
			outdated = true;
		} else if (getConfig().getInt("playerData") > playerDataVersion) {
			CommunicationManager.debugError(FUTURE, 0, "playerData.yml");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(playerDataVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "playerData.yml");
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "spawn tables");
			CommunicationManager.debugError(UPDATE, 0 , Integer.toString(spawnTableVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "spawn tables");
			outdated = true;
		} else if (getConfig().getInt("spawnTableStructure") > spawnTableVersion) {
			CommunicationManager.debugError(FUTURE, 0, "spawn tables");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(spawnTableVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "spawn tables");
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			CommunicationManager.debugInfo("The %s spawn table has been updated!", 0,
					"default.yml");
			CommunicationManager.debugInfo("Updating to version %s is optional but recommended.", 0,
					Integer.toString(defaultSpawnVersion));
			CommunicationManager.debugInfo(CONFIG_WARNING, 0, "default.yml");
		} else if (getConfig().getInt("spawnTableDefault") > defaultSpawnVersion) {
			CommunicationManager.debugError(FUTURE, 0, "default.yml");
			CommunicationManager.debugInfo("Reverting to version %s is optional but recommended.", 0,
					Integer.toString(defaultSpawnVersion));
			CommunicationManager.debugInfo(CONFIG_WARNING, 0, "default.yml");
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "language files");
			CommunicationManager.debugError(UPDATE, 0 , Integer.toString(languageFileVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "language files");
			outdated = true;
		} else if (getConfig().getInt("languageFile") > languageFileVersion) {
			CommunicationManager.debugError(FUTURE, 0, "language files");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(languageFileVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "language files");
			outdated = true;
		}

		// Check if customEffects.yml is outdated
		if (getConfig().getInt("customEffects") < customEffectsVersion) {
			CommunicationManager.debugError(OUTDATED, 0, "customEffects.yml");
			CommunicationManager.debugError(UPDATE, 0 , Integer.toString(customEffectsVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "customEffects.yml");
			outdated = true;
		} else if (getConfig().getInt("customEffects") > customEffectsVersion) {
			CommunicationManager.debugError(FUTURE, 0, "customEffects.yml");
			CommunicationManager.debugError(REVERT, 0, Integer.toString(customEffectsVersion));
			CommunicationManager.debugError(CONFIG_WARNING, 0, "customEffects.yml");
			outdated = true;
		}

		// Close all arenas if outdated
		if (outdated)
			GameManager.closeArenas();
	}

	private void checkArenaNameAndGatherUnloadedWorlds() {
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
			CommunicationManager.debugError("Some of your arenas have duplicate names! That is not allowed :(",
					0);
			CommunicationManager.debugError("Shutting down plugin to protect your data. Please fix and restart " +
					"server.", 0);
			Bukkit.getScheduler().scheduleSyncDelayedTask(this,
					() -> getServer().getPluginManager().disablePlugin(this), 0);
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
}
