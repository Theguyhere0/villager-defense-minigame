package me.theguyhere.villagerdefense.nms.v1_16_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

/**
 * Packet class for destroying entities.
 *
 * This class format was borrowed from filoghost.
 */
class EntityDestroyPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    EntityDestroyPacket(EntityID entityID) {
        PacketSetter packetSetter = PacketSetter.get();

        // Write ID of entity to destroy
        packetSetter.writeVarIntArray(entityID.getNumericID());

        // Send out packet
        rawPacket = writeData(new PacketPlayOutEntityDestroy(), packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
