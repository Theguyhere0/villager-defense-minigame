package me.theguyhere.villagerdefense.nms.v1_18_r2;

import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
		((CraftPlayer) player).getHandle().connection.send(getRawPacket());
	}

	abstract Packet<?> getRawPacket();
}
