package me.theguyhere.villagerdefense.nms.common;

import org.bukkit.entity.Player;

/**
 * A class with implementations of me.theguyhere.villagedefense.nms.common.PacketGroup
 *
 * Idea for this interface borrowed from filoghost.
 */
class PacketGroupImp {
    static class DoublePacket implements PacketGroup {
        private final PacketGroup packet1;
        private final PacketGroup packet2;

        DoublePacket(PacketGroup packet1, PacketGroup packet2) {
            this.packet1 = packet1;
            this.packet2 = packet2;
        }

        @Override
        public void sendTo(Player player) {
            packet1.sendTo(player);
            packet2.sendTo(player);
        }
    }
}
