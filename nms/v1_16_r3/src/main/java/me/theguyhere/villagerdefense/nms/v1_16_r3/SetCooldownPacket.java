package me.theguyhere.villagerdefense.nms.v1_16_r3;

import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutSetCooldown;

public class SetCooldownPacket extends VersionNMSPacket{
    private final Packet<?> rawPacket;

    SetCooldownPacket(int itemID, int cooldownTicks) {
        PacketSetter packetSetter = PacketSetter.get();

        // Cooldown info
        packetSetter.writeVarInt(itemID);
        packetSetter.writeVarInt(cooldownTicks);

        rawPacket = writeData(new PacketPlayOutSetCooldown(), packetSetter);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }
}
