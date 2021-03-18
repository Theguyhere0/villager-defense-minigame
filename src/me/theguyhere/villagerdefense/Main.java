package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.events.ClickNPC;
import me.theguyhere.villagerdefense.events.Death;
import me.theguyhere.villagerdefense.events.InventoryEvents;
import me.theguyhere.villagerdefense.events.Join;
import me.theguyhere.villagerdefense.game.Game;
import me.theguyhere.villagerdefense.game.GameEvents;
import me.theguyhere.villagerdefense.game.GameItems;
import me.theguyhere.villagerdefense.tools.DataManager;
import me.theguyhere.villagerdefense.tools.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private final Portal portal = new Portal(this);
	private final GameItems gi = new GameItems();
	private final InventoryItems ii = new InventoryItems();
	private final Inventories inventories = new Inventories(this, gi, ii);
	private PacketReader reader;
	private final Game game = new Game(this, gi, inventories, portal);
	private final Commands commands = new Commands(this, inventories, game);
	private DataManager data;

	// Runs when enabling plugin
	@Override
	public void onEnable() {
		saveDefaultConfig();

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
		data = new DataManager(this);

		// Set up commands and tab complete
		getCommand("vd").setExecutor(commands);
		getCommand("vd").setTabCompleter(new CommandTab());

		// Register event listeners
		pm.registerEvents(new InventoryEvents(this, inventories, portal), this);
		pm.registerEvents(new Join(portal, reader, game), this);
		pm.registerEvents(new Death(portal, reader), this);
		pm.registerEvents(new ClickNPC(this, game, portal), this);
		pm.registerEvents(new GameEvents(this, game, gi), this);

		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		// Spawn in portals
		if (getData().contains("portal"))
			loadPortals();

		int currentCVersion = 3;
		int currentDVersion = 3;
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

//	Runs when disabling plugin
	@Override
	public void onDisable() {
		// Remove uninject players
		for (Player player : Bukkit.getOnlinePlayers())
			reader.uninject(player);

		// Remove portals
		portal.removeAll();
	}

//	Returns data.yml data
	public FileConfiguration getData() {
		return data.getConfig();
	}

//	Saves data.yml changes
	public void saveData() {
		data.saveConfig();
	}

//	Load saved NPCs
	public void loadPortals() {
		getData().getConfigurationSection("portal").getKeys(false).forEach(portal -> {
			try {
				Location location = new Location(Bukkit.getWorld(getData().getString("portal." + portal + ".world")),
						getData().getDouble("portal." + portal + ".x"),
						getData().getDouble("portal." + portal + ".y"),
						getData().getDouble("portal." + portal + ".z"));
				location.setYaw((float) getData().getDouble("portal." + portal + ".yaw"));

				this.portal.loadPortal(location, Integer.parseInt(portal));
			} catch (Exception ignored) {
			}
		});
	}
}
