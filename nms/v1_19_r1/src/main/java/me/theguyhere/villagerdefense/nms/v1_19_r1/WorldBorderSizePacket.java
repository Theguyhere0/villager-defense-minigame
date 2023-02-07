package me.theguyhere.villagerdefense.nms.v1_19_r1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;

/**
 * Packet class for setting client world border size.
 * <p>
 * This class format was borrowed from filoghost.
 */
class WorldBorderSizePacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    WorldBorderSizePacket(double size) {
        PacketSetter packetSetter = PacketSetter.get();

        // Write in size
        packetSetter.writeDouble(size);

        // Send out packet
        rawPacket = new ClientboundSetBorderSizePacket(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
