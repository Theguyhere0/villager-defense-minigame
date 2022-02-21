package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.Block;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;

public class BlockChangePacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    BlockChangePacket(BlockPosition position, Material material) {
        PacketSetter packetSetter = PacketSetter.get();

        // Block information
        packetSetter.writePosition(position);
        packetSetter.writeVarInt(Block.REGISTRY_ID.getId(((CraftBlockData) Bukkit.createBlockData(material)).getState()));

        rawPacket = writeData(new PacketPlayOutBlockChange(), packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
