package me.theguyhere.villagerdefense.nms.v1_20_r4;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;

import java.util.UUID;

/**
 * A class to help with setting up a packet for a specific version.
 *
 * This class was borrowed from filoghost to learn about Packets.
 */
class PacketSetter extends PacketDataSerializer {

    /** Keeps an instance so new ones don't need to be instantiated (don't know why but filoghost did it).*/
    private static final PacketSetter INSTANCE = new PacketSetter();

    /**
     * Get the instance of me.theguyhere.villagedefense.nms.v1_18_r1.PacketSetter for this version.
     * @return me.theguyhere.villagedefense.nms.v1_18_r1.PacketSetter for specific version.
     */
    static PacketSetter get() {
        INSTANCE.clear();
        return INSTANCE;
    }

    /**
     * Create instance once on class load.
     */
    private PacketSetter() {
        super(Unpooled.buffer());
    }


    void writeVarInt(int i) {
        super.c(i);
    }

    void writeVarIntArray(int i1) {
        writeVarInt(1);
        writeVarInt(i1);
    }

    void writeVarIntArray(int i1, int i2) {
        writeVarInt(2);
        writeVarInt(i1);
        writeVarInt(i2);
    }

    void writeUUID(UUID uuid) {
        super.a(uuid);
    }

    void writePosition(BlockPosition position) {
        super.a(position);
    }

    void writeNBTTagCompound(NBTTagCompound nbtTagCompound) {
        super.a(nbtTagCompound);
    }

    <T> void writeDataWatcherEntry(DataWatcherKey<T> key, T value) {
        writeByte(key.getIndex());
        writeVarInt(key.getSerializerTypeID());
        key.getSerializer().a(this, value);
    }

    void writeDataWatcherEntriesEnd() {
        writeByte(0xFF);
    }
}
