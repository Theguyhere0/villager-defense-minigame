package me.theguyhere.villagerdefense.nms.common.entities;

import me.theguyhere.villagerdefense.nms.common.PacketGroup;

/**
 * Basic interface for all entities constructed from packets.
 *
 * This interface was borrowed from filoghost.
 */
public interface PacketEntity {
    PacketGroup newDestroyPackets();
}
