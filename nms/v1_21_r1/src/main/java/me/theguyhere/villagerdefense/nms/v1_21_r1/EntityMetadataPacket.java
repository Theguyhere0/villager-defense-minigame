package me.theguyhere.villagerdefense.nms.v1_21_r1;

import me.theguyhere.villagerdefense.nms.common.EntityID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for sending entity metadata packets.
 *
 * This class format was borrowed from filoghost.
 */
class EntityMetadataPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    private EntityMetadataPacket(PacketSetter packetSetter) {
        rawPacket = PacketPlayOutEntityMetadata.a.decode(new RegistryFriendlyByteBuf(packetSetter, MinecraftServer
            .getServer().bc()));
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
