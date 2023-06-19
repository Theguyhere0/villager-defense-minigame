package me.theguyhere.villagerdefense.nms.v1_16_r3;

import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import net.minecraft.server.v1_16_R3.Packet;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;

/**
 * Abstract packet class for specific NMS version.
 * <p>
 * This abstract class was borrowed from filoghost.
 */
abstract class VersionNMSPacket implements PacketGroup {

	/**
	 * Send packet group to player.
	 *
	 * @param player Recipient.
	 */
	@Override
	public void sendTo(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(getRawPacket());
	}

	abstract Packet<?> getRawPacket();

	protected static <T extends Packet<?>> T writeData(T packet, PacketSetter packetSetter) {
		try {
			packet.a(packetSetter);
			return packet;
		}
		catch (IOException e) {
			// Never thrown by the implementations
			throw new RuntimeException(e);
		}
	}
}
