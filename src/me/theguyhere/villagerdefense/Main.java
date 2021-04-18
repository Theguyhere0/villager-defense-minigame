package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.GUI.InventoryEvents;
import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.listeners.AbilityEvents;
import me.theguyhere.villagerdefense.game.listeners.ArenaEvents;
import me.theguyhere.villagerdefense.game.listeners.ClickPortalEvents;
import me.theguyhere.villagerdefense.game.listeners.GameEvents;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.genListeners.CommandTab;
import me.theguyhere.villagerdefense.genListeners.Commands;
import me.theguyhere.villagerdefense.genListeners.Death;
import me.theguyhere.villagerdefense.genListeners.Join;
import me.theguyhere.villagerdefense.tools.DataManager;
import me.theguyhere.villagerdefense.tools.PacketReader;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private final DataManager arenaData = new DataManager(this, "arenaData.yml");
	private final DataManager playerData = new DataManager(this, "playerData.yml");
	private final Portal portal = new Portal(this);
	private final Leaderboard leaderboard = new Leaderboard(this);
	private final InfoBoard infoBoard = new InfoBoard(this);
	private final InventoryItems ii = new InventoryItems();
	private final Utils utils = new Utils(this);
	private PacketReader reader;
	private Game game;

	// Runs when enabling plugin
	@Override
	public void onEnable() {
		saveDefaultConfig();

		game = new Game(this, portal);
		Inventories inventories = new Inventories(this, game, ii);
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
		getCommand("vd").setTabCompleter(new CommandTab());

		// Register event listeners
		pm.registerEvents(new InventoryEvents(this, game, inventories, portal, leaderboard, infoBoard,
				arenaBoard), this);
		pm.registerEvents(new Join(this, portal, reader, game), this);
		pm.registerEvents(new Death(portal, reader), this);
		pm.registerEvents(new ClickPortalEvents(game, portal, inventories), this);
		pm.registerEvents(new GameEvents(this, game), this);
		pm.registerEvents(new ArenaEvents(this, game, portal, leaderboard, arenaBoard, inventories), this);
		pm.registerEvents(new AbilityEvents(this, game), this);

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
		int arenaDataVersion = 2;
		int playerDataVersion = 1;
		int spawnTableVersion = 1;
		int defaultSpawnVersion = 1;

		// Check config version
		if (getConfig().getInt("version") < configVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your config.yml is outdated!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update to the latest version (" + configVersion + ") to ensure compatibility.");
		}

		// Check if arenaData.yml is outdated
		if (getConfig().getInt("arenaData") < arenaDataVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your arenaData.yml is no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer arena data to version " + arenaDataVersion + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your arenaData.yml has been updated.");
		}

		// Check if playerData.yml is outdated
		if (getConfig().getInt("playerData") < playerDataVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your playerData.yml is no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer player data to version " + playerDataVersion + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your playerData.yml has been updated.");
		}

		// Check if spawn tables are outdated
		if (getConfig().getInt("spawnTableStructure") < spawnTableVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your spawn tables are no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer spawn table data to version " + spawnTableVersion + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your spawn tables have been updated.");
		}

		// Check if default spawn table has been updated
		if (getConfig().getInt("spawnTableDefault") < defaultSpawnVersion) {
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"The default.yml spawn table has been updated!");
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Updating is optional but recommended.");
			getServer().getConsoleSender().sendMessage("[VillagerDefense] " +
					"Please do not update your config.yml unless your default.yml has been updated.");
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

	// Load portals
	public void loadPortals() {
		getArenaData().getConfigurationSection("portal").getKeys(false).forEach(portal -> {
			Location location = utils.getConfigLocationNoPitch("portal." + portal);
			if (location != null)
				this.portal.loadPortal(location, Integer.parseInt(portal), game);
		});
	}
}
