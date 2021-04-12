package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.GUI.InventoryEvents;
import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.game.*;
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
	private final DataManager data = new DataManager(this);
	private final Portal portal = new Portal(this);
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
		pm.registerEvents(new InventoryEvents(this, game, inventories, portal), this);
		pm.registerEvents(new Join(this, portal, reader, game), this);
		pm.registerEvents(new Death(portal, reader), this);
		pm.registerEvents(new ClickPortalEvents(game, portal), this);
		pm.registerEvents(new GameEvents(this, game), this);
		pm.registerEvents(new ArenaEvents(this, game, portal), this);

		// Inject online players into packet reader
		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		// Spawn in portals
		if (getData().contains("portal"))
			loadPortals();

		int currentCVersion = 4;
		int currentDVersion = 4;

		// Check config version
		if (getConfig().getInt("version") < currentCVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your config.yml is outdated!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please update to the latest version (" + currentCVersion + ") to ensure compatibility.");
		}

		// Check if data.yml is outdated
		if (getConfig().getInt("data") < currentDVersion) {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Your data.yml is no longer supported with this version!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[VillagerDefense] " +
					"Please manually transfer arena data to version " + currentDVersion + ".");
			getServer().getConsoleSender().sendMessage(ChatColor.RED +  "[VillagerDefense] " +
					"Please do not update your config.yml until your data.yml has been updated.");
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

	// Returns data.yml data
	public FileConfiguration getData() {
		return data.getConfig();
	}

	// Saves data.yml changes
	public void saveData() {
		data.saveConfig();
	}

	// Load saved NPCs
	public void loadPortals() {
		getData().getConfigurationSection("portal").getKeys(false).forEach(portal -> {
			Location location = utils.getConfigLocationNoPitch("portal." + portal);
			if (location != null)
				this.portal.loadPortal(location, Integer.parseInt(portal), game);
		});
	}
}
