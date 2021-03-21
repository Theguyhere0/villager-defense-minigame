package me.theguyhere.villagerdefense.tools;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.Portal;
import me.theguyhere.villagerdefense.customEvents.RightClickNPCEvent;
import net.minecraft.server.v1_16_R3.EntityVillager;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketReader {
	private final Portal portal;
	
	public PacketReader(Portal portal) {
		this.portal = portal;
	}
	
	Channel channel;
	public static Map<UUID, Channel> channels = new HashMap<>();
	
	public void inject(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
		channels.put(player.getUniqueId(), channel);
		
		if (channel.pipeline().get("PacketInjector") != null)
			return;
		
		channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

			@Override
			protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) {
				arg.add(packet);
				readPacket(player, packet);
			}
			
		});
	}
	
	public void uninject(Player player) {
		channel = channels.get(player.getUniqueId());
		if (channel != null)
			if (channel.pipeline().get("PacketInjector") != null)
				channel.pipeline().remove("PacketInjector");
	}

	public void readPacket(Player player, Packet<?> packet) {
				
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
				return;
			if (getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
				return;
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
				return;
			
			int id = (int) getValue(packet, "a");
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
				for (EntityVillager npc : portal.getNPCs()) {
					if (npc != null && npc.getId() == id) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
								Bukkit.getPluginManager().callEvent(new RightClickNPCEvent(player, npc)), 0);
					}
				}
			}
		}
	}
		
	private Object getValue(Object instance, String name) {
		Object result = null;
		
		try {
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			
			result = field.get(instance);
			
			field.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
