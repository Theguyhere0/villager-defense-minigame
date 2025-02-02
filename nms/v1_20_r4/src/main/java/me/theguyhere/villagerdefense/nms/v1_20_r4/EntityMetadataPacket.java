package me.theguyhere.villagerdefense.nms.v1_20_r4;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.MinecraftServer;

/**
 * A class for sending entity metadata packets.
 */
@SuppressWarnings("deprecation")
class EntityMetadataPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    private EntityMetadataPacket(PacketSetter packetSetter) {
        rawPacket = ClientboundSetEntityDataPacket.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(packetSetter, MinecraftServer
            .getServer().registryAccess()));
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
