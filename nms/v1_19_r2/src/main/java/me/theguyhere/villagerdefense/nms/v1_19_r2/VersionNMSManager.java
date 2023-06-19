package me.theguyhere.villagerdefense.nms.v1_19_r2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.nms.common.*;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

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
				new TileEntityDataPacket(position, BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(BlockEntityType.SIGN),
					signNBT
				),
				new OpenSignEditorPacket(position),
				new BlockChangePacket(position, original)
			)
			.sendTo(player);
	}

	@Override
	public PacketGroup createBorderWarning(Location location, double healthRatio) {
		// Protect from invalid health ratios
		if (healthRatio > 1 || healthRatio < 0)
			return null;

		return PacketGroup.of(
			new WorldBorderCenterPacket(location),
			new WorldBorderSizePacket(Constants.BORDER_SIZE),
			new WorldBorderWarningDistancePacket(Math.max((int) (Constants.BORDER_SIZE * (4 - 7 * healthRatio)), 0))
		);
	}

	@Override
	public PacketGroup resetBorderWarning(Location location, double size, int warningDistance) {
		return PacketGroup.of(
			new WorldBorderCenterPacket(location),
			new WorldBorderSizePacket(size),
			new WorldBorderWarningDistancePacket(warningDistance)
		);
	}

	@Override
	public Mob spawnVDMob(Location location, String key) {
		return null;
	}

	@Override
	public void injectPacketListener(Player player, PacketListener packetListener) {
		modifyPipeline(player, (ChannelPipeline pipeline) -> {
			ChannelHandler inboundListener = pipeline.get(InboundPacketHandler.HANDLER_NAME);
			ChannelHandler outboundListener = pipeline.get(OutboundPacketHandler.HANDLER_NAME);

			// Remove old listener
			if (inboundListener != null)
				pipeline.remove(InboundPacketHandler.HANDLER_NAME);
			if (outboundListener != null)
				pipeline.remove(OutboundPacketHandler.HANDLER_NAME);

			// Inject new listener
			pipeline.addBefore("packet_handler", InboundPacketHandler.HANDLER_NAME,
				new InboundPacketHandler(player, packetListener)
			);
			pipeline.addBefore("packet_handler", OutboundPacketHandler.HANDLER_NAME,
				new OutboundPacketHandler(packetListener)
			);
		});
	}

	@Override
	public void uninjectPacketListener(Player player) {
		modifyPipeline(player, (ChannelPipeline pipeline) -> {
			ChannelHandler inboundListener = pipeline.get(InboundPacketHandler.HANDLER_NAME);
			ChannelHandler outboundListener = pipeline.get(OutboundPacketHandler.HANDLER_NAME);

			// Remove old listener
			if (inboundListener != null)
				pipeline.remove(InboundPacketHandler.HANDLER_NAME);
			if (outboundListener != null)
				pipeline.remove(OutboundPacketHandler.HANDLER_NAME);
		});
	}

	/**
	 * This is to ensure that pipeline modification doesn't happen on the main thread, which can cause concurrency
	 * issues.
	 *
	 * @param player               Player to affect.
	 * @param pipelineModifierTask Consumer function for modifying pipeline.
	 */
	private void modifyPipeline(Player player, Consumer<ChannelPipeline> pipelineModifierTask) {
		Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;

		channel
			.eventLoop()
			.execute(() -> {
				try {
					pipelineModifierTask.accept(channel.pipeline());
				}
				catch (Exception e) {
					CommunicationManager.debugError(
						NMSErrors.EXCEPTION_MODIFYING_CHANNEL_PIPELINE,
						CommunicationManager.DebugLevel.QUIET
					);
					e.printStackTrace();
				}
			});
	}
}
