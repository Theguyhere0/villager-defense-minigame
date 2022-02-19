package me.theguyhere.villagerdefense.nms.common;

import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import org.bukkit.entity.Player;

/**
 * Interface for all NMS managers.
 *
 * This class structure borrowed from filoghost.
 */
public interface NMSManager {

    TextPacketEntity newTextPacketEntity();

    VillagerPacketEntity newVillagerPacketEntity();

    void injectPacketListener(Player player, PacketListener packetListener);

    void uninjectPacketListener(Player player);
}
