package me.theguyhere.villagerdefense.nms.common.entities;

import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import org.bukkit.Location;

/**
 * Basic interface for all Villager entities constructed from packets.
 */
public interface VillagerPacketEntity extends PacketEntity {
	PacketGroup newSpawnPackets(Location location);

	int getEntityID();
}
