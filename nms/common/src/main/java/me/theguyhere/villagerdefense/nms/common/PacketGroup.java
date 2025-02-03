package me.theguyhere.villagerdefense.nms.common;

import org.bukkit.entity.Player;

/**
 * A class to bundle packets together so functions remain intact.
 */
public interface PacketGroup {

    /**
     * Send packet group to player.
     * @param player Recipient.
     */
    void sendTo(Player player);

    static PacketGroup of(PacketGroup packet1, PacketGroup packet2) {
        return new PacketGroupImp.DoublePacket(packet1, packet2);
    }

    static PacketGroup of(PacketGroup packet1, PacketGroup packet2, PacketGroup packet3) {
        return new PacketGroupImp.TriplePacket(packet1, packet2, packet3);
    }

    static PacketGroup of(PacketGroup packet1, PacketGroup packet2, PacketGroup packet3, PacketGroup packet4) {
        return new PacketGroupImp.QuadruplePacket(packet1, packet2, packet3, packet4);
    }
}
