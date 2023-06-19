package me.theguyhere.villagerdefense.nms.common.entities;

import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import org.bukkit.Location;

/**
 * Basic interface for all entities with text constructed from packets.
 * <p>
 * This interface was borrowed from filoghost.
 */
public interface TextPacketEntity extends PacketEntity {
	PacketGroup newSpawnPackets(Location location, String text);
}
