package me.theguyhere.villagerdefense.nms.common;

import org.bukkit.Location;

/**
 * Basic interface for all Villager entities constructed from packets.
 */
public interface VillagerPacketEntity extends PacketEntity {
    PacketGroup newSpawnPackets(Location location);
}
