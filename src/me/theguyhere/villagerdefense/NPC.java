package me.theguyhere.villagerdefense;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPC {
	private final Main plugin;
	
	public NPC(Main plugin) {
		this.plugin = plugin;
	}
	
	private List<EntityPlayer> NPC = new ArrayList<>();
	
	public void createNPC(Player player, int num) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), Integer.toString(num));
		EntityPlayer npc = new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
		npc.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(),
				player.getLocation().getPitch());
				
		String[] name = getSkin(player);
		gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));
		
		addNPCPacket(npc);
		NPC.add(npc);
		
		plugin.getData().set("data.portal." + num + ".x", (double) player.getLocation().getX());
		plugin.getData().set("data.portal." + num + ".y", (double) player.getLocation().getY());
		plugin.getData().set("data.portal." + num + ".z", (double) player.getLocation().getZ());
		plugin.getData().set("data.portal." + num + ".p", player.getLocation().getPitch());
		plugin.getData().set("data.portal." + num + ".yaw", player.getLocation().getYaw());
		plugin.getData().set("data.portal." + num + ".world", player.getLocation().getWorld().getName());
		plugin.getData().set("data.portal." + num + ".text", name[0]);
		plugin.getData().set("data.portal." + num + ".signature", name[1]);
		plugin.saveData();
	}
	
	public void LoadNPC(Location location, GameProfile profile) {
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) Bukkit.getWorld(location.getWorld().getName())).getHandle();
		EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
		npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		
		addNPCPacket(npc);
		NPC.add(npc);
	}
	
	private String[] getSkin(Player player) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		GameProfile profile = p.getProfile();
		Property property = profile.getProperties().get("textures").iterator().next();
		String texture = property.getValue();
		String signature = property.getSignature();
		return new String[] {texture, signature};
	}
	
	public void addNPCPacket(EntityPlayer npc) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
					npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}
	
	public void removeNPC(Player player, EntityPlayer npc) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
	}
	
	public void addJoinPacket(Player player) {
		for (EntityPlayer npc : NPC) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
					npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		}
	}

	public List<EntityPlayer> getNPCs() {
		return NPC;
	}
}
