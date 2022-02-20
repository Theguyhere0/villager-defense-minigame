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

    static class TriplePacket implements PacketGroup {
        private final PacketGroup packet1;
        private final PacketGroup packet2;
        private final PacketGroup packet3;

        TriplePacket(PacketGroup packet1, PacketGroup packet2, PacketGroup packet3) {
            this.packet1 = packet1;
            this.packet2 = packet2;
            this.packet3 = packet3;
        }

        @Override
        public void sendTo(Player player) {
            packet1.sendTo(player);
            packet2.sendTo(player);
            packet3.sendTo(player);
        }
    }

    static class QuadruplePacket implements PacketGroup {
        private final PacketGroup packet1;
        private final PacketGroup packet2;
        private final PacketGroup packet3;
        private final PacketGroup packet4;

        QuadruplePacket(PacketGroup packet1, PacketGroup packet2, PacketGroup packet3, PacketGroup packet4) {
            this.packet1 = packet1;
            this.packet2 = packet2;
            this.packet3 = packet3;
            this.packet4 = packet4;
        }

        @Override
        public void sendTo(Player player) {
            packet1.sendTo(player);
            packet2.sendTo(player);
            packet3.sendTo(player);
            packet4.sendTo(player);
        }
    }
}
