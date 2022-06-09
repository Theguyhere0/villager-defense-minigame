package me.theguyhere.villagerdefense.nms.v1_19_r1;

import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;

public class TileEntityDataPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    TileEntityDataPacket(BlockPosition position, int type, NBTTagCompound nbtTagCompound) {
        PacketSetter packetSetter = PacketSetter.get();

        // Block entity data
        packetSetter.writePosition(position);
        packetSetter.writeVarInt(type);
        packetSetter.writeNBTTagCompound(nbtTagCompound);

        rawPacket = new PacketPlayOutTileEntityData(packetSetter);
    }

    @Override
    public Packet<?> getRawPacket() {
        return rawPacket;
    }
}
