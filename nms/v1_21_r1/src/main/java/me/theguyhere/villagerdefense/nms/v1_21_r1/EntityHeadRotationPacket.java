package me.theguyhere.villagerdefense.nms.v1_21_r1;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;

/**
 * Class for sending entity head rotation packets.
 */
public class EntityHeadRotationPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    EntityHeadRotationPacket(EntityID entityID, float headYaw) {
        PacketSetter packetSetter = PacketSetter.get();

        // Entity info
        packetSetter.writeVarInt(entityID.getNumericID());

        // Head yaw
        packetSetter.writeByte(Calculator.degreesToByte(headYaw));
        PacketPlayOutEntityHeadRotation.a.a();

        rawPacket = PacketPlayOutEntityHeadRotation.a.decode(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
