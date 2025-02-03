package me.theguyhere.villagerdefense.plugin;

import lombok.Getter;
import lombok.Setter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.game.achievements.listeners.BonusListener;
import me.theguyhere.villagerdefense.plugin.game.listeners.ArenaListener;
import me.theguyhere.villagerdefense.plugin.data.VDExpansion;
import me.theguyhere.villagerdefense.plugin.data.listeners.*;
import me.theguyhere.villagerdefense.plugin.game.challenges.listeners.ChallengeListener;
import me.theguyhere.villagerdefense.plugin.commands.VDTabCompleter;
import me.theguyhere.villagerdefense.plugin.commands.VDCommandExecutor;
import me.theguyhere.villagerdefense.plugin.data.exceptions.InvalidLanguageKeyException;
import me.theguyhere.villagerdefense.plugin.mechanics.effects.listeners.CustomEffectsListener;
import me.theguyhere.villagerdefense.plugin.structures.listeners.InteractionListener;
import me.theguyhere.villagerdefense.plugin.game.listeners.GameListener;
import me.theguyhere.villagerdefense.plugin.items.GameItems;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.visuals.listeners.InventoryListener;
import me.theguyhere.villagerdefense.plugin.game.kits.listeners.KitAbilityListener;
import me.theguyhere.villagerdefense.plugin.data.DataManager;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.data.NMSVersion;
import me.theguyhere.villagerdefense.plugin.structures.listeners.UpdateListener;
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

@SuppressWarnings("CallToPrintStackTrace")
public class Main extends JavaPlugin {
	// Singleton instance
	public static Main plugin;

	// Yaml file managers
	private static DataManager arenaData;
	private static DataManager playerData;
	private static DataManager customEffects;

	// Global instance variables
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();
	@Getter
    @Setter
    private boolean loaded = false;
	@Getter
    private final List<String> unloadedWorlds = new ArrayList<>();

	// Global state variables
	@Getter
    private static boolean outdated = false; // DO NOT CHANGE
	public static final boolean releaseMode = true;
	public static final int configVersion = 8;
	public static final int arenaDataVersion = 6;
	public static final int playerDataVersion = 2;
	public static final int spawnTableVersion = 1;
	public static final int languageFileVersion = 19;
	public static final int defaultSpawnVersion = 2;
	public static final int customEffectsVersion = 2;

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
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist").setExecutor(new VDCommandExecutor());
		Objects.requireNonNull(getCommand("vd"), "'vd' command should exist")
				.setTabCompleter(new VDTabCompleter());

		// Schedule to register PAPI expansion
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
				new VDExpansion().register();
			}, Calculator.secondsToTicks(1));

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
		pm.registerEvents(new InteractionListener(), this);
		pm.registerEvents(new UpdateListener(), this);
		pm.registerEvents(new GameListener(), this);
		pm.registerEvents(new ArenaListener(), this);
		pm.registerEvents(new KitAbilityListener(), this);
		pm.registerEvents(new ChallengeListener(), this);
		pm.registerEvents(new WorldListener(), this);
		pm.registerEvents(new BonusListener(), this);
		pm.registerEvents(new CustomEffectsListener(), this);
		pm.registerEvents(new ChatListener(), this);

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

		checkFileVersions();

		// Set as unloaded while reloading
		setLoaded(false);

		// Check worlds again
		checkArenaNameAndGatherUnloadedWorlds();

		// Remove active chat tasks
		ChatListener.wipeTasks();

		// Register expansion again
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			new VDExpansion().register();
	}

	public void resetGameManager() {
		GameManager.init();

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
			CommunicationManager.debugError(
				"Some of your arenas have duplicate names! That is not allowed :(",
				CommunicationManager.DebugLevel.QUIET
			);
			CommunicationManager.debugError("Shutting down plugin to protect your data. Please fix and restart " +
				"server.", CommunicationManager.DebugLevel.QUIET);
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
