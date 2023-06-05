package me.theguyhere.villagerdefense.nms.v1_19_r1;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;

public class BlockChangePacket extends VersionNMSPacket{
    private final Packet<?> rawPacket;

    BlockChangePacket(BlockPos position, Material material) {
        PacketSetter packetSetter = PacketSetter.get();

        // Block information
        packetSetter.writeBlockPos(position);
        packetSetter.writeVarInt(Block.getId(((CraftBlockData) Bukkit.createBlockData(material)).getState()));

        rawPacket = new ClientboundBlockUpdatePacket(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
