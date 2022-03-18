package me.theguyhere.villagerdefense.nms.v1_18_r1;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.world.level.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public class BlockChangePacket extends VersionNMSPacket{
    private final Packet<?> rawPacket;

    BlockChangePacket(BlockPosition position, Material material) {
        PacketSetter packetSetter = PacketSetter.get();

        // Block information
        packetSetter.writePosition(position);
        packetSetter.writeVarInt(Block.p.a(((CraftBlockData) Bukkit.createBlockData(material)).getState()));

        rawPacket = new PacketPlayOutBlockChange(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
