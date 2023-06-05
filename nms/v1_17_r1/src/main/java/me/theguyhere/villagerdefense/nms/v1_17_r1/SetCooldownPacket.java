package me.theguyhere.villagerdefense.nms.v1_17_r1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;

public class SetCooldownPacket extends VersionNMSPacket{
    private final Packet<?> rawPacket;

    SetCooldownPacket(int itemID, int cooldownTicks) {
        PacketSetter packetSetter = PacketSetter.get();

        // Cooldown info
        packetSetter.writeVarInt(itemID);
        packetSetter.writeVarInt(cooldownTicks);

        rawPacket = new ClientboundCooldownPacket(packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
