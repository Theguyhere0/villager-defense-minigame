package me.theguyhere.villagerdefense.plugin;

import lombok.Getter;
import lombok.Setter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.data.*;
import me.theguyhere.villagerdefense.plugin.game.achievements.listeners.BonusListener;
import me.theguyhere.villagerdefense.plugin.game.listeners.ArenaListener;
import me.theguyhere.villagerdefense.plugin.data.listeners.*;
import me.theguyhere.villagerdefense.plugin.game.challenges.listeners.ChallengeListener;
import me.theguyhere.villagerdefense.plugin.commands.VDTabCompleter;
import me.theguyhere.villagerdefense.plugin.commands.VDCommandExecutor;
import me.theguyhere.villagerdefense.plugin.data.exceptions.InvalidLanguageKeyException;
import me.theguyhere.villagerdefense.plugin.structures.listeners.InteractionListener;
import me.theguyhere.villagerdefense.plugin.game.listeners.GameListener;
import me.theguyhere.villagerdefense.plugin.items.GameItems;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.visuals.listeners.InventoryListener;
import me.theguyhere.villagerdefense.plugin.game.kits.listeners.KitAbilityListener;
import me.theguyhere.villagerdefense.plugin.structures.listeners.UpdateListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {
	// Singleton instance
	public static Main plugin;

	// SQL database manager
	private static DatabaseManager database;

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
	public static final boolean releaseMode = false;
	public static final int configVersion = 9;
	public static final int gameDataVersion = 1;
	public static final int arenaDataVersion = 7;
	public static final int playerDataVersion = 2;
	public static final int spawnTableVersion = 1;
	public static final int languageFileVersion = 19;
	public static final int defaultSpawnVersion = 2;

	@Override
	public void onEnable() {
		Main.plugin = this;

		// Set up data managers
		GameDataManager.init();
		ArenaDataManager.init();
		try {
			LanguageManager.init();
		} catch (InvalidLanguageKeyException e) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, e.getMessage(), true, e);
		}
		PlayerDataManager.init();

		// Check if file versions need to be updated
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
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "This build is not meant for release! Testing code may still be active."
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
			);
		}

		// Remind if default debug level is not normal in release mode
		if (releaseMode && CommunicationManager.getDebugLevel() != CommunicationManager.DebugLevel.NORMAL) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Default debug level should be set to normal!"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
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

		// Reset up data managers
		GameDataManager.init();
		ArenaDataManager.init();
		try {
			LanguageManager.init();
		} catch (InvalidLanguageKeyException e) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, e.getMessage(), true, e);
		}
		PlayerDataManager.init();

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
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "Plugin not properly initialized! The following worlds are not " +
				"loaded yet: " + unloadedWorlds);
		}
		else CommunicationManager.debugConfirm(
			CommunicationManager.DebugLevel.QUIET, "All worlds fully loaded. The plugin is properly initialized."
		);
	}

    // Quick way to send test messages to console but remembering to take them down before release
	@SuppressWarnings("unused")
	public static void testInfo(String msg, boolean stackTrace) {
		if (releaseMode) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "This should not be here!");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "");
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !"
			);
		}

		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, msg);

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
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, OUTDATED, "config.yml");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, UPDATE,
				Integer.toString(configVersion)
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Please only update AFTER updating all other data files."
			);
			outdated = true;
		}
		else if (getConfig().getInt("version") > configVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, FUTURE, "config.yml");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, REVERT,
				Integer.toString(configVersion)
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Please only update AFTER updating all other data files."
			);
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, OUTDATED,
				"arenaData.yml"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, UPDATE,
				Integer.toString(arenaDataVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING,
				"arenaData.yml"
			);
			outdated = true;
		}
		else if (getConfig().getInt("arenaData") > arenaDataVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, FUTURE, "arenaData.yml");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, REVERT,
				Integer.toString(arenaDataVersion)
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Please only update AFTER updating all other data files."
			);
			outdated = true;

		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, OUTDATED,
				"playerData.yml"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, UPDATE,
				Integer.toString(playerDataVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING,
				"playerData.yml"
			);
			outdated = true;
		}
		else if (getConfig().getInt("playerData") > playerDataVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, FUTURE,
				"playerData.yml"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, REVERT,
				Integer.toString(playerDataVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING,
				"playerData.yml"
			);
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, OUTDATED,
				"spawn tables"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, UPDATE,
				Integer.toString(spawnTableVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING,
				"spawn tables"
			);
			outdated = true;
		}
		else if (getConfig().getInt("spawnTableStructure") > spawnTableVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, FUTURE,
				"spawn tables"
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, REVERT,
				Integer.toString(spawnTableVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING,
				"spawn tables"
			);
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.NORMAL, "The %s spawn table has been updated!",
				"default.yml"
			);
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.NORMAL, "Updating to version %s is optional but recommended.",
				Integer.toString(defaultSpawnVersion)
			);
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, CONFIG_WARNING,
				"default.yml"
			);
		}
		else if (getConfig().getInt("spawnTableDefault") > defaultSpawnVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, FUTURE,
				"default.yml"
			);
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.NORMAL, "Reverting to version %s is optional but recommended.",
				Integer.toString(defaultSpawnVersion)
			);
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, CONFIG_WARNING,
				"default.yml"
			);
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, OUTDATED, "language files");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, UPDATE,
				Integer.toString(languageFileVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING, "language files");
			outdated = true;
		}
		else if (getConfig().getInt("languageFile") > languageFileVersion) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, FUTURE, "language files");
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, REVERT,
				Integer.toString(languageFileVersion)
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, CONFIG_WARNING, "language files");
			outdated = true;
		}
	}

	private void checkArenaNameAndGatherUnloadedWorlds() {
		// Check for duplicate arena names
		List<String> arenaNames = ArenaDataManager.getArenaNames();
		if (arenaNames.size() > new HashSet<>(arenaNames).size()) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Some of your arenas have duplicate names! That is not allowed :("
			);
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, "Shutting down plugin to protect your data. Please fix and restart " +
				"server.");
			Bukkit.getScheduler().scheduleSyncDelayedTask(this,
					() -> getServer().getPluginManager().disablePlugin(this), 0);
		}

		// Relevant worlds from arenas, info boards, leaderboards, and lobby
		ArenaDataManager.getArenaWorlds().forEach(this::checkAddUnloadedWorld);
		GameDataManager.getInfoBoardWorlds().forEach(this::checkAddUnloadedWorld);
		GameDataManager.getLeaderboardWorlds().forEach(this::checkAddUnloadedWorld);
		checkAddUnloadedWorld(GameDataManager.getLobbyWorldName());

		// Set GameManager
		resetGameManager();
	}
}
