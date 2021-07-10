package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.listeners.InventoryEventsListener;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.listeners.AbilityEventsListener;
import me.theguyhere.villagerdefense.listeners.ArenaEventsListener;
import me.theguyhere.villagerdefense.listeners.ClickPortalEventsListener;
import me.theguyhere.villagerdefense.listeners.GameEventsListener;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.listeners.DeathListener;
import me.theguyhere.villagerdefense.listeners.JoinListener;
import me.theguyhere.villagerdefense.tools.DataManager;
import me.theguyhere.villagerdefense.tools.PacketReader;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {
	private final DataManager arenaData = new DataManager(this, "arenaData.yml");
	private final DataManager playerData = new DataManager(this, "playerData.yml");
	private final DataManager languageData = new DataManager(this, "languages/" +
			getConfig().getString("locale") + ".yml");
	private final Portal portal = new Portal(this);
	private final Leaderboard leaderboard = new Leaderboard(this);
	private final InfoBoard infoBoard = new InfoBoard(this);
	private PacketReader reader;
	private Game game;

	private boolean outdated;

	// Runs when enabling plugin
	@Override
	public void onEnable() {
		saveDefaultConfig();

		game = new Game(this, portal);
		checkArenas();
		Inventories inventories = new Inventories(this, game);
		Commands commands = new Commands(this, inventories, game);
		ArenaBoard arenaBoard = new ArenaBoard(this, game);

		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"HolographicDisplays is not installed or not enabled.");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"This plugin will be disabled.");
			this.setEnabled(false);
			return;
		}

		reader = new PacketReader(portal);
		PluginManager pm = getServer().getPluginManager();

		// Set up commands and tab complete
		getCommand("vd").setExecutor(commands);
		getCommand("vd").setTabCompleter(new CommandTab(game));

		// Register event listeners
		pm.registerEvents(new InventoryEventsListener(this, game, inventories, portal, leaderboard, infoBoard,
				arenaBoard), this);
		pm.registerEvents(new JoinListener(this, portal, reader, game), this);
		pm.registerEvents(new DeathListener(portal, reader), this);
		pm.registerEvents(new ClickPortalEventsListener(game, portal, inventories), this);
		pm.registerEvents(new GameEventsListener(this, game), this);
		pm.registerEvents(new ArenaEventsListener(this, game, portal, leaderboard, arenaBoard, inventories), this);
		pm.registerEvents(new AbilityEventsListener(this, game), this);

		// Inject online players into packet reader
		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		// Spawn in portals
		if (getArenaData().contains("portal"))
			loadPortals();
		leaderboard.loadLeaderboards();
		infoBoard.loadInfoBoards();
		arenaBoard.loadArenaBoards();

		int configVersion = 6;
		int arenaDataVersion = 3;
		int playerDataVersion = 1;
		int spawnTableVersion = 1;
		int languageFileVersion = 6;
		int defaultSpawnVersion = 2;
		outdated = false;

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your config.yml is outdated!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update to the latest version (" + ChatColor.BLUE + configVersion + ChatColor.RED +
					") to ensure compatibility.");
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your arenaData.yml is no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your arenaData.yml has been updated.");
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your playerData.yml is no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your playerData.yml has been updated.");
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your spawn tables are no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer spawn table data to version " + ChatColor.BLUE + spawnTableVersion +
					ChatColor.RED + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your spawn tables have been updated.");
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"The default.yml spawn table has been updated!");
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Updating to version" + ChatColor.BLUE + defaultSpawnVersion + ChatColor.WHITE +
					" is optional but recommended.");
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Please do not update your config.yml unless your default.yml has been updated.");
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"You language files are no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update en_US.yml and update any other language files to version " + ChatColor.BLUE +
					languageFileVersion + ChatColor.RED + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your language files have been updated.");
			outdated = true;
		}
	}

	// Runs when disabling plugin
	@Override
	public void onDisable() {
		// Remove uninject players
		for (Player player : Bukkit.getOnlinePlayers())
			reader.uninject(player);

		// Remove portals
		portal.removeAll();

		game.arenas.stream().filter(Objects::nonNull).filter(arena -> !arena.isClosed())
				.forEach(arena -> Utils.clear(arena.getCorner1(), arena.getCorner2()));
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

	// Load portals
	private void loadPortals() {
		getArenaData().getConfigurationSection("portal").getKeys(false).forEach(portal ->
				this.portal.loadPortal(Integer.parseInt(portal), game));
	}

	// Check arenas for close
	private void checkArenas() {
		game.arenas.stream().filter(Objects::nonNull).forEach(Arena::checkClose);
	}

	public boolean isOutdated() {
		return outdated;
	}

	public void debugError(String msg) {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " + msg);
	}

	public void debugInfo(String msg) {
		getServer().getConsoleSender().sendMessage("[VillagerDefense] " + msg);
	}
}
