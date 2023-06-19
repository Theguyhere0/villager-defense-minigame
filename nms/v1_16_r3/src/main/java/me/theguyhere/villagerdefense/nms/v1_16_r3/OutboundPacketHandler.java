package me.theguyhere.villagerdefense.nms.v1_16_r3;

import com.mojang.datafixers.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Reflection;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle client bound packets.
 */
@SuppressWarnings("unchecked")
public class OutboundPacketHandler extends ChannelOutboundHandlerAdapter {
	public static final String HANDLER_NAME = "villager_defense_outbound_listener";
	private final PacketListener packetListener;

	OutboundPacketHandler(PacketListener packetListener) {
		this.packetListener = packetListener;
	}

	@Override
	public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
		try {
			if (packet instanceof PacketPlayOutEntityEquipment) {
				// Get equipment list
				List<Pair<EnumItemSlot, ItemStack>> pairList =
					(List<Pair<EnumItemSlot, ItemStack>>) Reflection.getFieldValue(packet,
						"b");
				List<Pair<EnumItemSlot, ItemStack>> newPairList = new ArrayList<>();

				// Remove any invisible items
				pairList.forEach(pair -> {
					if (packetListener.checkInvisible(CraftItemStack.asBukkitCopy(pair.getSecond())))
						newPairList.add(new Pair<>(
							pair.getFirst(),
							CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(Material.AIR))
						));
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
		super.write(context, packet, promise);
	}
}
