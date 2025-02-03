package me.theguyhere.villagerdefense.nms.v1_21_r1;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;

/**
 * A class to help with setting up a packet for a specific version.
 */
@SuppressWarnings("deprecation")
class PacketSetter extends FriendlyByteBuf {

    // Singleton instance
    private static final PacketSetter INSTANCE = new PacketSetter();

    /**
     * Get the instance of {@link PacketSetter} for this version.
     *
     * @return {@link PacketSetter} for specific version.
     */
    static PacketSetter get() {
        INSTANCE.clear();
        return INSTANCE;
    }

    // Create instance once on class load
    private PacketSetter() {
        super(Unpooled.buffer());
    }

    public FriendlyByteBuf writeVarIntArray(int... is) {
        return super.writeVarIntArray(is);
    }

    <T> void writeDataWatcherEntry(DataWatcherKey<T> key, T value) {
        writeByte(key.getIndex());
        writeVarInt(key.getSerializerTypeID());
        key.getSerializer().codec().encode(new RegistryFriendlyByteBuf(this, MinecraftServer
            .getServer().registryAccess()), value);
    }

    void writeDataWatcherEntriesEnd() {
        writeByte(0xFF);
    }
}
