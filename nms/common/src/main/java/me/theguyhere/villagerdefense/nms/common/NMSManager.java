package me.theguyhere.villagerdefense.nms.common;

import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

/**
 * Interface for all NMS managers.
 * <p>
 * This class structure borrowed from filoghost.
 */
public interface NMSManager {
	TextPacketEntity newTextPacketEntity();

	VillagerPacketEntity newVillagerPacketEntity(String type);

	String getSpawnParticleName();

	String getMonsterParticleName();

	String getVillagerParticleName();

	String getBorderParticleName();

	void nameArena(Player player, String arenaName, int arenaID);

	PacketGroup createBorderWarning(Location location, double healthRatio);

	PacketGroup resetBorderWarning(Location location, double size, int warningDistance);

	Mob spawnVDMob(Location location, String key);

	void injectPacketListener(Player player, PacketListener packetListener);

	void uninjectPacketListener(Player player);
}
