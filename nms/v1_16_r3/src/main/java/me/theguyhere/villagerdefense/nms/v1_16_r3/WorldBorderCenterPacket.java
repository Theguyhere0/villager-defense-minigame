package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
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

		// Write in action type as setting center
		packetSetter.writeVarInt(2);

		// Write in x and z coordinates
		packetSetter.writeDouble(location.getX());
		packetSetter.writeDouble(location.getZ());

		// Send out packet
		rawPacket = writeData(new PacketPlayOutWorldBorder(), packetSetter);
	}

	@Override
	Packet<?> getRawPacket() {
		return rawPacket;
	}
}
