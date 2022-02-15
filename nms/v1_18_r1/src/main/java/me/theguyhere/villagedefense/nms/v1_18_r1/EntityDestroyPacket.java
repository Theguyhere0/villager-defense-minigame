package me.theguyhere.villagedefense.nms.v1_18_r1;

import me.theguyhere.villagedefense.nms.common.EntityID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;

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
        this.rawPacket = new PacketPlayOutEntityDestroy(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
