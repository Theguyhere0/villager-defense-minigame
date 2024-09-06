package me.theguyhere.villagerdefense.plugin;

import lombok.Getter;
import lombok.Setter;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.achievements.AchievementListener;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaListener;
import me.theguyhere.villagerdefense.plugin.background.*;
import me.theguyhere.villagerdefense.plugin.challenges.ChallengeListener;
import me.theguyhere.villagerdefense.plugin.commands.CommandExecImp;
import me.theguyhere.villagerdefense.plugin.commands.TabCompleterImp;
import me.theguyhere.villagerdefense.plugin.displays.ClickPortalListener;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.GameListener;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.guis.InventoryListener;
import me.theguyhere.villagerdefense.plugin.kits.KitAbilityListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
	private final NMSManager nmsManager = NMSVersion
		.getCurrent()
		.getNmsManager();
	@Setter
	@Getter
	private static boolean loaded = false;
	@Getter
	private static final List<String> unloadedWorlds = new ArrayList<>();
	@Getter
	private static Economy economy;
	private static VDExpansion expansion;
	@Getter
	private static Scoreboard vdBoard;

	// Global state variables
	@Getter
	private static boolean outdated = false; // DO NOT CHANGE
	public static final boolean releaseMode = false;
	public static final int configVersion = 10;
	public static final int arenaDataVersion = 9;
	public static final int playerDataVersion = 3;
	public static final int spawnTableVersion = 4;
	public static final int languageFileVersion = 33;
	public static final int defaultSpawnVersion = 6;
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
		Objects
			.requireNonNull(getCommand("vd"), "'vd' command should exist")
			.setExecutor(new CommandExecImp());
		Objects
			.requireNonNull(getCommand("vd"), "'vd' command should exist")
			.setTabCompleter(new TabCompleterImp());

		// Schedule to register PAPI expansion
		expansion = new VDExpansion();
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(this, () -> {
				if (Bukkit
					.getPluginManager()
					.getPlugin("PlaceholderAPI") != null)
					expansion.register();
			}, Calculator.secondsToTicks(1));

		// Try finding economy plugin
		setupEconomy();

		// Set up initial classes
		saveDefaultConfig();
		PluginManager pm = getServer().getPluginManager();
		try {
			LanguageManager.init(languageData.getConfig());
		}
		catch (InvalidLanguageKeyException e) {
			e.printStackTrace();
		}
		CommunicationManager.setDisplayPluginTag(getConfig().getBoolean("displayPluginTag"));
		vdBoard = Objects
			.requireNonNull(Bukkit.getScoreboardManager())
			.getNewScoreboard();
		vdBoard.registerNewTeam("monsters").setColor(ChatColor.RED);
		vdBoard.registerNewTeam("villagers").setColor(ChatColor.GREEN);

		// Register event listeners
		pm.registerEvents(new AchievementListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new BackgroundListener(), this);
		pm.registerEvents(new DebugListener(), this);
		pm.registerEvents(new ClickPortalListener(), this);
		pm.registerEvents(new GameListener(), this);
		pm.registerEvents(new ArenaListener(), this);
		pm.registerEvents(new KitAbilityListener(), this);
		pm.registerEvents(new ChallengeListener(), this);

		// Add packet listeners for online players
		for (Player player : Bukkit.getOnlinePlayers())
			nmsManager.injectPacketListener(player, new BackgroundListener());

		checkArenaNameAndGatherUnloadedWorlds();

		// Remind if this build is not meant for release
		if (!releaseMode) {
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError(
				"This build is not meant for release! Testing code may still be active.",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
		}

		// Remind if default debug level is not normal in release mode
		if (releaseMode && CommunicationManager.getDebugLevel() != CommunicationManager.DebugLevel.NORMAL) {
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError(
				"Default debug level should be set to normal!",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
		}
	}

	@Override
	public void onDisable() {
		// Wipe every valid arena
		GameController.wipeArenas();
	}

	public void reload(Player player) {
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
		}
		catch (InvalidLanguageKeyException e) {
			e.printStackTrace();
		}
		CommunicationManager.setDisplayPluginTag(getConfig().getBoolean("displayPluginTag"));
		checkFileVersions();

		// Set as unloaded while reloading
		setLoaded(false);
		checkArenaNameAndGatherUnloadedWorlds();

		// Re-register expansion
		if (Bukkit
			.getPluginManager()
			.getPlugin("PlaceholderAPI") != null) {
			expansion.unregister();
			expansion.register();
		}

		// Try finding economy plugin again
		setupEconomy();

		// Notify of successful reload
		if (player != null)
			PlayerManager.notifySuccess(player, "Plugin data reloaded successfully!");
		else
			CommunicationManager.debugConfirm(
				"Plugin data reloaded successfully!",
				CommunicationManager.DebugLevel.QUIET
			);
	}

	public static void resetGameManager() {
		GameController.init();

		// Check for proper initialization with worlds
		if (!unloadedWorlds.isEmpty()) {
			CommunicationManager.debugError("Plugin not properly initialized! The following worlds are not " +
				"loaded yet: " + unloadedWorlds, CommunicationManager.DebugLevel.QUIET);
		}
		else CommunicationManager.debugConfirm(
			"All worlds fully loaded. The plugin is properly initialized.",
			CommunicationManager.DebugLevel.QUIET
		);
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
		return plugin
			.getConfig()
			.getBoolean("vaultEconomy") && economy != null;
	}

	public static Scoreboard getVdBoard() {
		return vdBoard;
	}

	public static Team getMonstersTeam() {
		return vdBoard.getTeam("monsters");
	}

	public static Team getVillagersTeam() {
		return vdBoard.getTeam("villagers");
	}

	// Quick way to send test messages to console but remembering to take them down before release
	@SuppressWarnings("unused")
	public static void testInfo(String msg, boolean stackTrace) {
		if (releaseMode) {
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError("This should not be here!", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError("", CommunicationManager.DebugLevel.QUIET);
			CommunicationManager.debugError(
				"! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !",
				CommunicationManager.DebugLevel.QUIET
			);
		}

		CommunicationManager.debugInfo(msg, CommunicationManager.DebugLevel.QUIET);

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
		if (getServer()
			.getPluginManager()
			.getPlugin("Vault") == null)
			return;

		RegisteredServiceProvider<Economy> rsp = getServer()
			.getServicesManager()
			.getRegistration(Economy.class);
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
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET, "config.yml");
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(configVersion)
			);
			CommunicationManager.debugError(
				"Please only update AFTER updating all other data files.",
				CommunicationManager.DebugLevel.QUIET
			);
			outdated = true;
		}
		else if (getConfig().getInt("version") > configVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET, "config.yml");
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(configVersion)
			);
			CommunicationManager.debugError(
				"Please only update AFTER updating all other data files.",
				CommunicationManager.DebugLevel.QUIET
			);
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET,
				"arenaData.yml"
			);
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(arenaDataVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET,
				"arenaData.yml"
			);
			outdated = true;
		}
		else if (getConfig().getInt("arenaData") > arenaDataVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET, "arenaData.yml");
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(arenaDataVersion)
			);
			CommunicationManager.debugError(
				"Please only update AFTER updating all other data files.",
				CommunicationManager.DebugLevel.QUIET
			);
			outdated = true;

		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET,
				"playerData.yml"
			);
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(playerDataVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET,
				"playerData.yml"
			);
			outdated = true;
		}
		else if (getConfig().getInt("playerData") > playerDataVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET,
				"playerData.yml"
			);
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(playerDataVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET,
				"playerData.yml"
			);
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET,
				"spawn tables"
			);
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(spawnTableVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET,
				"spawn tables"
			);
			outdated = true;
		}
		else if (getConfig().getInt("spawnTableStructure") > spawnTableVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET,
				"spawn tables"
			);
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(spawnTableVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET,
				"spawn tables"
			);
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			CommunicationManager.debugInfo(
				"The %s spawn table has been updated!",
				CommunicationManager.DebugLevel.NORMAL,
				"default.yml"
			);
			CommunicationManager.debugInfo(
				"Updating to version %s is optional but recommended.",
				CommunicationManager.DebugLevel.NORMAL,
				Integer.toString(defaultSpawnVersion)
			);
			CommunicationManager.debugInfo(CONFIG_WARNING, CommunicationManager.DebugLevel.NORMAL,
				"default.yml"
			);
		}
		else if (getConfig().getInt("spawnTableDefault") > defaultSpawnVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.NORMAL,
				"default.yml"
			);
			CommunicationManager.debugInfo(
				"Reverting to version %s is optional but recommended.",
				CommunicationManager.DebugLevel.NORMAL,
				Integer.toString(defaultSpawnVersion)
			);
			CommunicationManager.debugInfo(CONFIG_WARNING, CommunicationManager.DebugLevel.NORMAL,
				"default.yml"
			);
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET, "language files");
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(languageFileVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET, "language files");
			outdated = true;
		}
		else if (getConfig().getInt("languageFile") > languageFileVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET, "language files");
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(languageFileVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET, "language files");
			outdated = true;
		}

		// Check if customEffects.yml is outdated
		if (getConfig().getInt("customEffects") < customEffectsVersion) {
			CommunicationManager.debugError(OUTDATED, CommunicationManager.DebugLevel.QUIET, "customEffects.yml");
			CommunicationManager.debugError(UPDATE, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(customEffectsVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET, "customEffects" +
				".yml");
			outdated = true;
		}
		else if (getConfig().getInt("customEffects") > customEffectsVersion) {
			CommunicationManager.debugError(FUTURE, CommunicationManager.DebugLevel.QUIET, "customEffects.yml");
			CommunicationManager.debugError(REVERT, CommunicationManager.DebugLevel.QUIET,
				Integer.toString(customEffectsVersion)
			);
			CommunicationManager.debugError(CONFIG_WARNING, CommunicationManager.DebugLevel.QUIET, "customEffects" +
				".yml");
			outdated = true;
		}

		// Close all arenas if outdated
		if (outdated)
			GameController.closeArenas();
	}

	private void checkArenaNameAndGatherUnloadedWorlds() {
		// Gather unloaded world list
		ConfigurationSection section;

		// Relevant worlds from arenas + check for duplicate arena names
		AtomicBoolean duplicate = new AtomicBoolean(false);
		List<String> arenaNames = new ArrayList<>();
		section = getArenaData().getConfigurationSection("arena");
		if (section != null)
			section
				.getKeys(false)
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
			CommunicationManager.debugError(
				"Some of your arenas have duplicate names! That is not allowed :(",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("Shutting down plugin to protect your data. Please fix and restart " +
				"server.", CommunicationManager.DebugLevel.QUIET);
			Bukkit
				.getScheduler()
				.scheduleSyncDelayedTask(this,
					() -> getServer()
						.getPluginManager()
						.disablePlugin(this), 0
				);
		}

		// Relevant worlds from info boards
		section = getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section
				.getKeys(false)
				.forEach(id ->
					checkAddUnloadedWorld(getArenaData().getString("infoBoard." + id + ".world")));

		// Relevant worlds from leaderboards
		section = getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section
				.getKeys(false)
				.forEach(id ->
					checkAddUnloadedWorld(getArenaData().getString("leaderboard." + id + ".world")));

		// Lobby world
		checkAddUnloadedWorld(getArenaData().getString("lobby.world"));

		// Set GameController
		resetGameManager();
	}
}
