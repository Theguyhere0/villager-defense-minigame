package me.theguyhere.villagerdefense.packets;

import io.netty.channel.Channel;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.events.RightClickNPCEvent;
import me.theguyhere.villagerdefense.events.SignGUIEvent;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.nms.NMSManager;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PacketReader {
	Channel channel;
	public static Map<UUID, Channel> channels = new HashMap<>();
	
	public void inject(Player player) {
		channel = NMSManager.getChannel(player);
		channels.put(player.getUniqueId(), channel);
		
		if (channel.pipeline().get("PacketInjector") != null)
			return;

		channel.pipeline().addAfter("decoder", "PacketInjector1", NMSManager.getClientUseEntityHandler(player));
		channel.pipeline().addAfter("decoder", "PacketInjector2", NMSManager.getClientUpdateSignHandler(player));
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

	public static void readPacket(Player player, Object packet) {
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			int id = (int) PacketManager.getValue(packet, "a");

			if (NMSManager.checkAttack(packet)) {
				Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
						.map(Portal::getNpc).filter(Objects::nonNull).forEach(npc -> {
							int npcId = npc.getVillager().getEntityId();
							if (npcId == id)
								Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
										Bukkit.getPluginManager().callEvent(new LeftClickNPCEvent(player, npcId)));
						});
				return;
			}
			if (NMSManager.checkInteractMain(packet))
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
			String header = ((String[]) PacketManager.getValue(packet, NMSManager.getSignGUIHeaderField()))[0];

			try {
				arena = ArenaManager.arenas[Integer.parseInt(header.substring(13, header.length() - 1))];
			} catch (Exception ignored) {
				return;
			}

			// Check for right sign GUI
			if (!(((String[]) PacketManager.getValue(packet, NMSManager.getSignGUIHeaderField()))[1]
					.contains(CommunicationManager.format("===============")) &&
					((String[]) PacketManager.getValue(packet, NMSManager.getSignGUIHeaderField()))[3]
							.contains(CommunicationManager.format("==============="))))
				return;

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
					Bukkit.getPluginManager().callEvent(new SignGUIEvent(arena, player,
							(String[]) PacketManager.getValue(packet, NMSManager.getSignGUIHeaderField()))));
		}
	}
}
