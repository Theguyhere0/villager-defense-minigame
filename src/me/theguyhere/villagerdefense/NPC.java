package me.theguyhere.villagerdefense;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NPC {
	private final Main plugin;
	
	public NPC(Main plugin) {
		this.plugin = plugin;
	}
	
	private final List<EntityVillager> NPC = new ArrayList<>();
	
	public void createNPC(Player player, int num) {
		// Get NMS versions of world
		WorldServer nmsWorld = ((CraftWorld) player.getWorld()).getHandle();

		// Create portal NPC
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, nmsWorld);
		npc.setLocation(player.getLocation().getX(),
				player.getLocation().getY(),
				player.getLocation().getZ(),
				player.getLocation().getYaw(),
				0);

		// Add packets
		addNPCPacket(npc);
		NPC.add(npc);

		// Save data about the NPC
		plugin.getData().set("portal." + num + ".x", player.getLocation().getX());
		plugin.getData().set("portal." + num + ".y", player.getLocation().getY());
		plugin.getData().set("portal." + num + ".z", player.getLocation().getZ());
		plugin.getData().set("portal." + num + ".yaw", player.getLocation().getYaw());
		plugin.getData().set("portal." + num + ".world", player.getWorld().getName());
		plugin.saveData();
	}
	
	public void LoadNPC(Location location) {
		WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
		EntityVillager npc = new EntityVillager(EntityTypes.VILLAGER, world);
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		
		addNPCPacket(npc);
		NPC.add(npc);
	}
	
	public void addNPCPacket(EntityVillager npc) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutSpawnEntityLiving(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}
	
	public void removeNPC(Player player, EntityVillager npc) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
	}
	
	public void addJoinPacket(Player player) {
		for (EntityVillager npc : NPC) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutSpawnEntityLiving(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}

	public List<EntityVillager> getNPCs() {
		return NPC;
	}
}
