package me.theguyhere.villagedefense.nms.common;

import org.bukkit.Location;

/**
 * Basic interface for all entities with text constructed from packets.
 *
 * This interface was borrowed from filoghost.
 */
public interface TextPacketEntity extends PacketEntity {
    PacketGroup newSpawnPackets(Location location, String text);
}
