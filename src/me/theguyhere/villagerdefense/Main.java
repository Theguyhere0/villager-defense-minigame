package me.theguyhere.villagerdefense;

import java.util.UUID;

import net.minecraft.server.v1_16_R3.EntityPlayer;
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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.theguyhere.villagerdefense.events.ClickNPC;
import me.theguyhere.villagerdefense.events.Death;
import me.theguyhere.villagerdefense.events.InventoryEvents;
import me.theguyhere.villagerdefense.events.Join;
import me.theguyhere.villagerdefense.game.Game;
import me.theguyhere.villagerdefense.game.GameEvents;
import me.theguyhere.villagerdefense.game.GameItems;

public class Main extends JavaPlugin {
	private final GameItems gi = new GameItems();
	private final InventoryItems ii = new InventoryItems();
	private final Inventories inventories = new Inventories(this, gi, ii);
	private final NPC npc = new NPC(this);
	private PacketReader reader;
	private final Game game = new Game(this, gi, inventories);
	private final Commands commands = new Commands(this, inventories, game);
	private DataManager data;

	//	Runs when enabling plugin
	@Override
	public void onEnable() {
		this.saveDefaultConfig();

		reader = new PacketReader(npc);
		PluginManager pm = this.getServer().getPluginManager();
		data = new DataManager(this);

		getCommand("vd").setExecutor(commands);
		pm.registerEvents(new InventoryEvents(this, inventories, npc, reader), this);
		pm.registerEvents(new Join(npc, reader, game), this);
		pm.registerEvents(new Death(npc, reader), this);
		pm.registerEvents(new ClickNPC(this, game), this);
		pm.registerEvents(new GameEvents(this, game, gi), this);

		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player player : Bukkit.getOnlinePlayers()) {
				reader.inject(player);
			}
		
		if (getData().contains("data.portal")) {
			loadNPC();
			getData().getConfigurationSection("data.portal").getKeys(false).forEach(this::spawnHolo);
		}
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Villager Defense has been loaded and enabled!");
	}

//	Runs when disabling plugin
	@Override
	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			reader.uninject(player);
			for ( EntityPlayer NPC : npc.getNPCs())
				npc.removeNPC(player, NPC);
		}
		if (getData().contains("data.portal")) {
			getData().getConfigurationSection("data.portal").getKeys(false).forEach(this::removeHolo);
		}
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "Villager Defense has been unloaded and disabled!");
	}

//	Returns data.yml dat
	public FileConfiguration getData() {
		return data.getConfig();
	}

//	Saves data.yml changes
	public void saveData() {
		data.saveConfig();
	}

//	Load saved NPCs
	public void loadNPC() {
		getData().getConfigurationSection("data.portal").getKeys(false).forEach(portal -> {
			Location location = new Location(Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")),
					getData().getDouble("data.portal." + portal + ".x"), getData().getDouble("data.portal." + portal + ".y"),
					getData().getDouble("data.portal." + portal + ".z"));
			location.setPitch((float) getData().getDouble("data.portal." + portal + ".p"));
			location.setYaw((float) getData().getDouble("data.portal." + portal + ".yaw"));

			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), portal);
			gameProfile.getProperties().put("textures", new Property("textures", getData().getString("data.portal." + portal + ".text"),
					getData().getString("data.portal." + portal + ".signature")));

			npc.LoadNPC(location, gameProfile);
		});
	}

//	Spawn holograms
	public void spawnHolo(String portal) {
		Location location = new Location(Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")),
				getData().getDouble("data.portal." + portal + ".x"), getData().getDouble("data.portal." + portal + ".y") - .17,
				getData().getDouble("data.portal." + portal + ".z"));

		ArmorStand holo = (ArmorStand) Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")).spawnEntity(location, EntityType.ARMOR_STAND);
		holo.setVisible(false);
		holo.setCustomNameVisible(true);
		holo.setCustomName("\u2588\u2588\u2588\u2588   \u2588\u2588\u2588\u2588");
		holo.setGravity(false);

		location = new Location(Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")),
				getData().getDouble("data.portal." + portal + ".x"), getData().getDouble("data.portal." + portal + ".y") + .5,
				getData().getDouble("data.portal." + portal + ".z"));

		ArmorStand holo2 = (ArmorStand) Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")).spawnEntity(location, EntityType.ARMOR_STAND);
		holo2.setVisible(false);
		holo2.setCustomNameVisible(true);
		holo2.setCustomName(getData().getString("data.a" + portal + ".name"));
		holo2.setGravity(false);
	}

//	Remove holograms
	public void removeHolo(String portal) {
		Location location = new Location(Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")),
				getData().getDouble("data.portal." + portal + ".x"), getData().getDouble("data.portal." + portal + ".y"),
				getData().getDouble("data.portal." + portal + ".z"));

		for (Entity holo : Bukkit.getWorld(getData().getString("data.portal." + portal + ".world")).getNearbyEntities(location, 1, 2, 1)) {
			if (holo.getType().equals(EntityType.ARMOR_STAND)) {
				holo.remove();
			}
		}
	}
}
