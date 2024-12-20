package me.theguyhere.villagerdefense.nms.v1_21_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.v1_21_r3.PacketSetter;
import me.theguyhere.villagerdefense.nms.v1_21_r3.VersionNMSPacket;
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
        rawPacket = new PacketPlayOutEntityDestroy(packetSetter.a());
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
