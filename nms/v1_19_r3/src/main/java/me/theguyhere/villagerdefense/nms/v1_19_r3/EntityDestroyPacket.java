package me.theguyhere.villagerdefense.nms.v1_19_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;

/**
 * Packet class for destroying entities.
 */
class EntityDestroyPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    EntityDestroyPacket(EntityID entityID) {
        PacketSetter packetSetter = PacketSetter.get();

        // Write ID of entity to destroy
        packetSetter.writeVarIntArray(entityID.getNumericID());

        // Send out packet
        rawPacket = new ClientboundRemoveEntitiesPacket(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
