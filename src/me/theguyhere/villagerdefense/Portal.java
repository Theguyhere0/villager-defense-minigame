package me.theguyhere.villagerdefense;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Portal {
	private final Main plugin;

	public Portal(Main plugin) {
		this.plugin = plugin;
	}

	// Lists to store NPCs and holograms
	private final EntityVillager[] NPC = new EntityVillager[45];
	private final Hologram[] holos = new Hologram[45];

	public void createPortal(Player player, int num) {
		// Get NMS versions of world
		WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();

		// Create portal NPC
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, nmsWorld);
		npc.setLocation(player.getLocation().getX(),
				player.getLocation().getY(),
				player.getLocation().getZ(),
				player.getLocation().getYaw(),
				0);

		// Add packets to all players
		addNPCAll(npc);
		NPC[num] = npc;

		// Create hologram
		addHolo(player.getLocation(), num, getHoloText(num));

		// Save data about the NPC
		plugin.getData().set("portal." + num + ".x", player.getLocation().getX());
		plugin.getData().set("portal." + num + ".y", player.getLocation().getY());
		plugin.getData().set("portal." + num + ".z", player.getLocation().getZ());
		plugin.getData().set("portal." + num + ".yaw", player.getLocation().getYaw());
		plugin.getData().set("portal." + num + ".world", player.getWorld().getName());
		plugin.saveData();
	}

	public void loadPortal(Location location, int arena) {
		// Create portal NPC
		WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, world);
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		// Add packets to all players
		addNPCAll(npc);
		NPC[arena] = npc;

		// Create hologram
		addHolo(location, arena, getHoloText(arena));
	}

	public void removePortalAll(int arena) {
		removeNPCAll(arena);
		if (holos[arena] != null)
			holos[arena].delete();
	}
	
	private void addNPC(Player player, EntityVillager npc) {
		if (npc != null) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutSpawnEntityLiving(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}

	private void addNPCAll(EntityVillager npc) {
		for (Player player : Bukkit.getOnlinePlayers())
			addNPC(player, npc);
	}
	
	private void removeNPC(Player player, EntityVillager npc) {
		if (npc != null) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}
	}

	private void removeNPCAll(int arena) {
		for (Player player : Bukkit.getOnlinePlayers())
			removeNPC(player, NPC[arena]);
	}
	
	public void addJoinPacket(Player player) {
		for (EntityVillager npc : NPC)
			addNPC(player, npc);
	}

	public EntityVillager[] getNPCs() {
		return NPC;
	}

	private void addHolo(Location location, int arena, String[] text) {
		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 3);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		holo.insertTextLine(0, text[0]);
		for (int i = 1; i < text.length; i++)
			holo.appendTextLine(text[i]);

		// Save hologram in array
		holos[arena] = holo;
	}

	public void refreshHolo(int arena) {
		holos[arena].delete();
		Location location = new Location(Bukkit.getWorld(plugin.getData().getString("portal." + arena + ".world")),
				plugin.getData().getDouble("portal." + arena + ".x"),
				plugin.getData().getDouble("portal." + arena + ".y"),
				plugin.getData().getDouble("portal." + arena + ".z"));
		location.setYaw((float) plugin.getData().getDouble("portal." + arena + ".yaw"));
		addHolo(location, arena, getHoloText(arena));
	}

	public Hologram[] getHolos() {
		return holos;
	}

	public void removeAll() {
		for (int i = 0; i < 45; i++)
			removeNPCAll(i);
		for (Hologram holo : holos)
			if (holo != null)
				holo.delete();
	}

	private String[] getHoloText(int arena) {
		return new String[]{Utils.format("&6&l" + plugin.getData().getString("a" + arena + ".name")),
		Utils.format("&bPlayers: " + plugin.getData().getInt("a" + arena + ".players.playing") + '/' +
				plugin.getData().getInt("a" + arena + ".max")),
		Utils.format("&7Spectators: " + plugin.getData().getInt("a" + arena + ".players.spectating"))};
	}
}
