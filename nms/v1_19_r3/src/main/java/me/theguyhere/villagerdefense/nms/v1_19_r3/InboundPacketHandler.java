package me.theguyhere.villagerdefense.nms.v1_19_r3;

import com.mojang.datafixers.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Reflection;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle server bound packets.
 */
@SuppressWarnings("unchecked")
class InboundPacketHandler extends ChannelInboundHandlerAdapter {
	public static final String HANDLER_NAME = "villager_defense_listener";
	private final Player player;
	private final PacketListener packetListener;

	InboundPacketHandler(Player player, PacketListener packetListener) {
		this.player = player;
		this.packetListener = packetListener;
	}

	@Override
	public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
		try {
			if (packet instanceof ServerboundInteractPacket) {
				int entityID = Reflection.getFieldValue(packet, "a", Integer.class);

				// Left click
				if (Reflection
					.getFieldValue(packet, "b")
					.getClass()
					.getDeclaredFields().length == 0) {
					packetListener.onAttack(player, entityID);
				}

				// Main hand right click
				else if (Reflection
					.getFieldValue(packet, "b")
					.getClass()
					.getDeclaredFields().length == 1
					&& Reflection
					.getFieldValue(Reflection.getFieldValue(packet, "b"), "a")
					.toString()
					.equalsIgnoreCase("MAIN_HAND")) {
					packetListener.onInteractMain(player, entityID);
				}
			}

			else if (packet instanceof ServerboundSignUpdatePacket) {
				packetListener.onSignUpdate(player, ((ServerboundSignUpdatePacket) packet).getLines());
			}

			else if (packet instanceof ClientboundSetEquipmentPacket) {
				// Get equipment list
				List<Pair<EquipmentSlot, ItemStack>> pairList =
					(List<Pair<EquipmentSlot, ItemStack>>) Reflection.getFieldValue(packet,
					"b");
				List<Pair<EquipmentSlot, ItemStack>> newPairList = new ArrayList<>();

				// Remove any invisible items
				pairList.forEach(pair -> {
					if (packetListener.checkInvisible(CraftItemStack.asBukkitCopy(pair.getSecond())))
						newPairList.add(new Pair<>(pair.getFirst(), null));
					else newPairList.add(new Pair<>(pair.getFirst(), pair.getSecond()));
				});

				// Modify equipment list
				Reflection.setFieldValue(packet, "b", newPairList);
			}
		}
		catch (Exception e) {
			CommunicationManager.debugError(NMSErrors.EXCEPTION_ON_PACKET_READ, CommunicationManager.DebugLevel.QUIET);
			e.printStackTrace();
		}
		super.channelRead(context, packet);
	}
}
