package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.events.ClickNPC;
import me.theguyhere.villagerdefense.events.Death;
import me.theguyhere.villagerdefense.events.InventoryEvents;
import me.theguyhere.villagerdefense.events.Join;
import me.theguyhere.villagerdefense.game.Game;
import me.theguyhere.villagerdefense.game.GameEvents;
import me.theguyhere.villagerdefense.game.GameItems;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityVillager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private final GameItems gi = new GameItems();
	private final InventoryItems ii = new InventoryItems();
	private final Inventories inventories = new Inventories(this, gi, ii);
	private final NPC npc = new NPC(this);
	private final DynamicHolo holo = new DynamicHolo(this);
	private PacketReader reader;
	private final Game game = new Game(this, gi, inventories);
	private final Commands commands = new Commands(this, inventories, game);
	private DataManager data;

	// Runs when enabling plugin
	@Override
	public void onEnable() {
		saveDefaultConfig();

		reader = new PacketReader(npc);
		PluginManager pm = getServer().getPluginManager();
		data = new DataManager(this);

		// Set up commands and tab complete
		getCommand("vd").setExecutor(commands);
		getCommand("vd").setTabCompleter(new CommandTab());

		// Register event listeners
		pm.registerEvents(new InventoryEvents(this, inventories, npc, holo, reader), this);
		pm.registerEvents(new Join(npc, holo, reader, game), this);
		pm.registerEvents(new Death(npc, reader), this);
		pm.registerEvents(new ClickNPC(this, game), this);
		pm.registerEvents(new GameEvents(this, game, gi), this);

		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}

		// Spawn in portals
		if (getData().contains("portal"))
			loadPortals();

		// Check config version
		if (getConfig().getInt("version") < 1)
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "Your config.yml is outdated! "
					+ "Please update to the latest version to ensure compatibility.");

		// Check if data.yml is outdated
		if (getConfig().getInt("data") < 1)
			getServer().getConsoleSender().sendMessage(ChatColor.RED +
					"Your data.yml is no longer supported with this version! " +
					"Please manually transfer arena data. " +
					"Please do not update your config.yml until your data.yml has been updated.");

		// Notify successful load
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Villager Defense has been loaded and enabled!");
	}

//	Runs when disabling plugin
	@Override
	public void onDisable() {
		// Remove portals
		for (Player player : Bukkit.getOnlinePlayers()) {
			reader.uninject(player);
			for (EntityVillager NPC : npc.getNPCs())
				npc.removeNPC(player, NPC);
			holo.getHolos().forEach((k, v) -> holo.removeHolo(player, k));
		}
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "Villager Defense has been unloaded and disabled!");
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

				npc.LoadNPC(location);
				holo.LoadHolo(location, Integer.parseInt(portal));
			} catch (Exception ignored) {
			}
		});
	}
}
