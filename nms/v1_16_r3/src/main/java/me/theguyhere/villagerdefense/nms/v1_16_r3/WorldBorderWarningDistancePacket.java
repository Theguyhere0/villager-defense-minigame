package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;

/**
 * Packet class for setting client world border size.
 * <p>
 * This class format was borrowed from filoghost.
 */
class WorldBorderWarningDistancePacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    WorldBorderWarningDistancePacket(int distance) {
        PacketSetter packetSetter = PacketSetter.get();

        // Write in action type as setting warning distance
        packetSetter.writeVarInt(0);

        // Write in warning distance
        packetSetter.writeVarInt(distance);

        // Send out packet
        rawPacket = writeData(new PacketPlayOutWorldBorder(), packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
