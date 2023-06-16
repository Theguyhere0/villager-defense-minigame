package me.theguyhere.villagerdefense.nms.v1_19_r2;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;

/**
 * Packet class for setting client world border size.
 * <p>
 * This class format was borrowed from filoghost.
 */
class WorldBorderWarningDistancePacket extends VersionNMSPacket {
	private final Packet<?> rawPacket;

	WorldBorderWarningDistancePacket(int distance) {
		PacketSetter packetSetter = PacketSetter.get();

		// Write in warning distance
		packetSetter.writeVarInt(distance);

		// Send out packet
		rawPacket = new ClientboundSetBorderWarningDistancePacket(packetSetter);
	}

	@Override
	Packet<?> getRawPacket() {
		return rawPacket;
	}
}
