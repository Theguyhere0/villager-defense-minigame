package me.theguyhere.villagerdefense.nms.v1_19_r3;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;

/**
 * A class for sending entity metadata packets.
 *
 * This class format was borrowed from filoghost.
 */
class EntityMetadataPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    private EntityMetadataPacket(PacketSetter packetSetter) {
        rawPacket = new PacketPlayOutEntityMetadata(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }

    public static DataWatcherPacketBuilder<EntityMetadataPacket> builder(EntityID entityID) {
        PacketSetter packetSetter = PacketSetter.get();
        packetSetter.writeVarInt(entityID.getNumericID());
        return new Builder(packetSetter);
    }

    private static class Builder extends DataWatcherPacketBuilder<EntityMetadataPacket> {
        private Builder(PacketSetter packetSetter) {
            super(packetSetter);
        }

        @Override
        EntityMetadataPacket createPacket(PacketSetter packetSetter) {
            return new EntityMetadataPacket(packetSetter);
        }
    }
}
