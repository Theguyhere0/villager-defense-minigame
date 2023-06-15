package me.theguyhere.villagerdefense.nms.v1_16_r3;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Manager class for a specific NMS version.
 */
public class VersionNMSManager implements NMSManager {
	@Override
	public TextPacketEntity newTextPacketEntity() {
		return new PacketEntityArmorStand(new EntityID());
	}

	@Override
	public VillagerPacketEntity newVillagerPacketEntity(String type) {
		return new PacketEntityVillager(new EntityID(), type);
	}

	@Override
	public String getSpawnParticleName() {
		return org.bukkit.Particle.FLAME.name();
	}

	@Override
	public String getMonsterParticleName() {
		return org.bukkit.Particle.SOUL_FIRE_FLAME.name();
	}

	@Override
	public String getVillagerParticleName() {
		return org.bukkit.Particle.COMPOSTER.name();
	}

	@Override
	public String getBorderParticleName() {
		return Particle.REDSTONE.name();
	}

	@Override
	public void nameArena(Player player, String arenaName, int arenaID) {
		Location location = player.getLocation();
		location.setY(location.getY() + 1);
		Material original = location
			.getBlock()
			.getType();
		BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
		NBTTagCompound signNBT = new NBTTagCompound();
		signNBT.setString("Text1", String.format(
			"{\"text\":\"%s\"}",
			CommunicationManager.format(String.format("&9   Rename Arena %d:   ", arenaID))
		));
		signNBT.setString("Text2", String.format(
			"{\"text\":\"%s\"}",
			new ColoredMessage(ChatColor.DARK_BLUE, "===============")
		));
		signNBT.setString("Text3", String.format(
			"{\"text\":\"%s\"}",
			CommunicationManager.format(arenaName == null ? "" : arenaName)
		));
		signNBT.setString("Text4", String.format(
			"{\"text\":\"%s\"}",
			new ColoredMessage(ChatColor.DARK_BLUE, "===============")
		));

		PacketGroup
			.of(
				new BlockChangePacket(position, Material.OAK_SIGN),
				new TileEntityDataPacket(position, 9, signNBT),
				new OpenSignEditorPacket(position),
				new BlockChangePacket(position, original)
			)
			.sendTo(player);
	}
}
