package me.theguyhere.villagerdefense.nms.v1_19_r1;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.nms.common.PacketGroup;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
		return Particle.FLAME.name();
	}

	@Override
	public String getMonsterParticleName() {
		return Particle.SOUL_FIRE_FLAME.name();
	}

	@Override
	public String getVillagerParticleName() {
		return Particle.COMPOSTER.name();
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
		BlockPos position = new BlockPos(location.getX(), location.getY(), location.getZ());
		CompoundTag signNBT = new CompoundTag();
		signNBT.putString("Text1", String.format(
			"{\"text\":\"%s\"}",
			CommunicationManager.format(String.format("&9   Rename Arena %d:   ", arenaID))
		));
		signNBT.putString("Text2", String.format(
			"{\"text\":\"%s\"}",
			new ColoredMessage(ChatColor.DARK_BLUE, "===============")
		));
		signNBT.putString("Text3", String.format(
			"{\"text\":\"%s\"}",
			CommunicationManager.format(arenaName == null ? "" : arenaName)
		));
		signNBT.putString("Text4", String.format(
			"{\"text\":\"%s\"}",
			new ColoredMessage(ChatColor.DARK_BLUE, "===============")
		));

		PacketGroup
			.of(
				new BlockChangePacket(position, Material.OAK_SIGN),
				new TileEntityDataPacket(position, Registry.BLOCK_ENTITY_TYPE.getId(BlockEntityType.SIGN), signNBT),
				new OpenSignEditorPacket(position),
				new BlockChangePacket(position, original)
			)
			.sendTo(player);
	}
}
