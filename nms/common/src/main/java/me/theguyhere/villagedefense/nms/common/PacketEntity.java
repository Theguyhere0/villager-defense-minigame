package me.theguyhere.villagedefense.nms.common;

/**
 * Basic interface for all entities constructed from packets.
 *
 * This interface was borrowed from filoghost.
 */
public interface PacketEntity {
    PacketGroup newDestroyPackets();
}
