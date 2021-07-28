package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.listeners.*;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
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
	private Inventories inventories;
	private Commands commands;
	private ArenaBoard arenaBoard;

	/**
	 * The amount of debug information to display in the console.
	 *
	 * 3 (Override) - All errors and information tracked will be displayed. Certain behavior will be overridden.
	 * 2 (Verbose) - All errors and information tracked will be displayed.
	 * 1 (Normal) - Errors that drastically reduce performance and important information will be displayed.
	 * 0 (Quiet) - Only the most urgent error messages will be displayed.
	 */
	private final int debugLevel = 3;
	private boolean outdated;

	// Runs when enabling plugin
	@Override
	public void onEnable() {
		saveDefaultConfig();

		reader = new PacketReader(portal);
		PluginManager pm = getServer().getPluginManager();
		game = new Game(this);
		inventories = new Inventories(this);
		commands = new Commands(this, inventories, game);
		arenaBoard = new ArenaBoard(this, game);

		checkArenas();

		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			debugError("HolographicDisplays is not installed or not enabled.", 0);
			debugError("This plugin will be disabled.", 0);
			this.setEnabled(false);
			return;
		}

		// Set up commands and tab complete
		getCommand("vd").setExecutor(commands);
		getCommand("vd").setTabCompleter(new CommandTab(game));

		// Register event listeners
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new JoinListener(this), this);
		pm.registerEvents(new DeathListener(this), this);
		pm.registerEvents(new ClickPortalListener(this), this);
		pm.registerEvents(new GameListener(this), this);
		pm.registerEvents(new ArenaListener(this), this);
		pm.registerEvents(new AbilityListener(this), this);
		pm.registerEvents(new WorldListener(this), this);

		// Inject online players into packet reader
		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		int configVersion = 6;
		int arenaDataVersion = 3;
		int playerDataVersion = 1;
		int spawnTableVersion = 1;
		int languageFileVersion = 6;
		int defaultSpawnVersion = 2;
		outdated = false;

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			debugError("Your config.yml is outdated!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update to the latest version (" + ChatColor.BLUE + configVersion + ChatColor.RED +
					") to ensure compatibility.");
			outdated = true;
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			debugError("Your arenaData.yml is no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer arena data to version " + ChatColor.BLUE + arenaDataVersion +
					ChatColor.RED + ".");
			debugError("Please do not update your config.yml until your arenaData.yml has been updated.", 0);
			outdated = true;
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			debugError("Your playerData.yml is no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer player data to version " + ChatColor.BLUE + playerDataVersion +
					ChatColor.BLUE + ".");
			debugError("Please do not update your config.yml until your playerData.yml has been updated.", 0);
			outdated = true;
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			debugError("Your spawn tables are no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer spawn table data to version " + ChatColor.BLUE + spawnTableVersion +
					ChatColor.RED + ".");
			debugError("Please do not update your config.yml until your spawn tables have been updated.", 0);
			outdated = true;
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			debugInfo("The default.yml spawn table has been updated!", 0);
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Updating to version" + ChatColor.BLUE + defaultSpawnVersion + ChatColor.WHITE +
					" is optional but recommended.");
			debugInfo("Please do not update your config.yml unless your default.yml has been updated.", 0);
		}

		// Check if language files are outdated
		if (getConfig().getInt("languageFile") < languageFileVersion) {
			debugError("You language files are no longer supported with this version!", 0);
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update en_US.yml and update any other language files to version " + ChatColor.BLUE +
					languageFileVersion + ChatColor.RED + ".");
			debugError("Please do not update your config.yml until your language files have been updated.", 0);
			outdated = true;
		}

		// Spawn in portals
		loadPortals();
		leaderboard.loadLeaderboards();
		infoBoard.loadInfoBoards();
		arenaBoard.loadArenaBoards();
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

	public Game getGame() {
		return game;
	}

	public Inventories getInventories() {
		return inventories;
	}

	public InfoBoard getInfoBoard() {
		return infoBoard;
	}

	public Portal getPortal() {
		return portal;
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	public PacketReader getReader() {
		return reader;
	}

	public Commands getCommands() {
		return commands;
	}

	public ArenaBoard getArenaBoard() {
		return arenaBoard;
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
	public void loadPortals() {
		if (getArenaData().contains("portal"))
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

	public void debugError(String msg, int debugLevel) {
		if (this.debugLevel >= debugLevel)
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " + msg);
	}

	public void debugInfo(String msg, int debugLevel) {
		if (this.debugLevel >= debugLevel)
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " + msg);
	}

	public int getDebugLevel() {
		return debugLevel;
	}
}
