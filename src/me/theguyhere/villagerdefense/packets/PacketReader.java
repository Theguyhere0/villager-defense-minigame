package me.theguyhere.villagerdefense.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.events.RightClickNPCEvent;
import me.theguyhere.villagerdefense.events.SignGUIEvent;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class PacketReader {
	Channel channel;
	public static Map<UUID, Channel> channels = new HashMap<>();
	
	public void inject(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
		channels.put(player.getUniqueId(), channel);
		
		if (channel.pipeline().get("PacketInjector") != null)
			return;
		
		channel.pipeline().addAfter("decoder", "PacketInjector1", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

			@Override
			protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) {
				arg.add(packet);
				readPacket(player, packet);
			}
			
		});

		channel.pipeline().addAfter("decoder", "PacketInjector2", new MessageToMessageDecoder<PacketPlayInUpdateSign>() {

			@Override
			protected void decode(ChannelHandlerContext channel, PacketPlayInUpdateSign packet, List<Object> arg) {
				arg.add(packet);
				readPacket(player, packet);
			}

		});
	}
	
	public void uninject(Player player) {
		channel = channels.get(player.getUniqueId());
		if (channel != null) {
			if (channel.pipeline().get("PacketInjector1") != null)
				channel.pipeline().remove("PacketInjector1");
			if (channel.pipeline().get("PacketInjector2") != null)
				channel.pipeline().remove("PacketInjector2");
		}
	}

	public void readPacket(Player player, Packet<?> packet) {
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			int id = (int) getValue(packet, "a");

			if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
				Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
						.map(Portal::getNpc).filter(Objects::nonNull).forEach(npc -> {
							int npcId = npc.getVillager().getEntityId();
							if (npcId == id)
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
										Bukkit.getPluginManager().callEvent(new LeftClickNPCEvent(player, npcId)));
						});
				return;
			}
			if (getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
				return;
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
				return;
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT"))
				Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
						.map(Portal::getNpc).filter(Objects::nonNull).forEach(npc -> {
							int npcId = npc.getVillager().getEntityId();
							if (npcId == id)
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
										Bukkit.getPluginManager().callEvent(new RightClickNPCEvent(player, npcId)));
						});
			return;
		}

		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUpdateSign")) {
			Arena arena;
			String header = ((String[]) getValue(packet, "b"))[0];

			try {
				arena = ArenaManager.arenas[Integer.parseInt(header.substring(13, header.length() - 1))];
			} catch (Exception ignored) {
				return;
			}

			// Check for right sign GUI
			if (!(((String[]) getValue(packet, "b"))[1].contains(CommunicationManager.format("===============")) &&
					((String[]) getValue(packet, "b"))[3].contains(CommunicationManager.format("==============="))))
				return;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
					Bukkit.getPluginManager().callEvent(new SignGUIEvent(arena, player,
							(String[]) getValue(packet, "b"))));
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
