package me.theguyhere.villagerdefense.nms.v1_19_r3;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import org.bukkit.Location;

/**
 * Packet class for setting client world border center.
 * <p>
 * This class format was borrowed from filoghost.
 */
class WorldBorderCenterPacket extends VersionNMSPacket {
	private final Packet<?> rawPacket;

	WorldBorderCenterPacket(Location location) {
		PacketSetter packetSetter = PacketSetter.get();

		// Write in x and z coordinates
		packetSetter.writeDouble(location.getX());
		packetSetter.writeDouble(location.getZ());

		// Send out packet
		rawPacket = new ClientboundSetBorderCenterPacket(packetSetter);
	}

	@Override
	Packet<?> getRawPacket() {
		return rawPacket;
	}
}
