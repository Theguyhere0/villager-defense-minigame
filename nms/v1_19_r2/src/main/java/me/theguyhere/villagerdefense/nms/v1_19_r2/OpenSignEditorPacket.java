package me.theguyhere.villagerdefense.nms.v1_19_r2;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;

public class OpenSignEditorPacket extends VersionNMSPacket {
    private final Packet<?> rawPacket;

    OpenSignEditorPacket(BlockPos position) {
        PacketSetter packetSetter = PacketSetter.get();

        // Sign location
        packetSetter.writeBlockPos(position);

        rawPacket = new ClientboundOpenSignEditorPacket(packetSetter);
    }

    @Override
    public Packet<?> getRawPacket() {
        return rawPacket;
    }
}
