package me.theguyhere.villagerdefense.nms.v1_19_r2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.*;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
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
        Material original = location.getBlock().getType();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        NBTTagCompound signNBT = new NBTTagCompound();
        signNBT.a("Text1", String.format("{\"text\":\"%s\"}",
                CommunicationManager.format(String.format("&9   Rename Arena %d:   ", arenaID))));
        signNBT.a("Text2", String.format("{\"text\":\"%s\"}",
                new ColoredMessage(ChatColor.DARK_BLUE, "===============")));
        signNBT.a("Text3", String.format("{\"text\":\"%s\"}",
                CommunicationManager.format(arenaName == null ? "" : arenaName)));
        signNBT.a("Text4", String.format("{\"text\":\"%s\"}",
                new ColoredMessage(ChatColor.DARK_BLUE, "===============")));

        PacketGroup.of(
                new BlockChangePacket(position, Material.OAK_SIGN),
                new TileEntityDataPacket(position, BuiltInRegistries.l.a(TileEntityTypes.h), signNBT),
                new OpenSignEditorPacket(position),
                new BlockChangePacket(position, original)).sendTo(player);
    }

    @Override
    public void setBowCooldown(Player player, int cooldownTicks) {
        new SetCooldownPacket(ItemID.BOW, cooldownTicks).sendTo(player);
    }

    @Override
    public void setCrossbowCooldown(Player player, int cooldownTicks) {
        new SetCooldownPacket(ItemID.CROSSBOW, cooldownTicks).sendTo(player);
    }

    @Override
    public PacketGroup createEffect(Location location, double healthRatio) {
        // Protect from invalid health ratios
        if (healthRatio > 1 || healthRatio < 0)
            return null;

        return PacketGroup.of(
                new WorldBorderCenterPacket(location),
                new WorldBorderSizePacket(BORDER_SIZE),
                new WorldBorderWarningDistancePacket(Math.max((int) (BORDER_SIZE * (4 - 7 * healthRatio)), 0))
        );
    }

    @Override
    public PacketGroup resetEffect(Location location, double size, int warningDistance) {
        return PacketGroup.of(
                new WorldBorderCenterPacket(location),
                new WorldBorderSizePacket(size),
                new WorldBorderWarningDistancePacket(warningDistance)
        );
    }

    @Override
    public void injectPacketListener(Player player, PacketListener packetListener) {
        modifyPipeline(player, (ChannelPipeline pipeline) -> {
            ChannelHandler currentListener = pipeline.get(InboundPacketHandler.HANDLER_NAME);

            // Remove old listener
            if (currentListener != null) {
                pipeline.remove(InboundPacketHandler.HANDLER_NAME);
            }

            // Inject new listener
            pipeline.addBefore("packet_handler", InboundPacketHandler.HANDLER_NAME,
                    new InboundPacketHandler(player, packetListener));
        });
    }

    @Override
    public void uninjectPacketListener(Player player) {
        modifyPipeline(player, (ChannelPipeline pipeline) -> {
            ChannelHandler currentListener = pipeline.get(InboundPacketHandler.HANDLER_NAME);

            // Remove old listener
            if (currentListener != null) {
                pipeline.remove(InboundPacketHandler.HANDLER_NAME);
            }
        });
    }

    /**
     * This is to ensure that pipeline modification doesn't happen on the main thread, which can cause concurrency
     * issues.
     * @param player Player to affect.
     * @param pipelineModifierTask Consumer function for modifying pipeline.
     */
    private void modifyPipeline(Player player, Consumer<ChannelPipeline> pipelineModifierTask) {
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().b;
        NetworkManager networkManager = playerConnection.a();
        Channel channel = networkManager.m;

        channel.eventLoop().execute(() -> {
            try {
                pipelineModifierTask.accept(channel.pipeline());
            } catch (Exception e) {
                CommunicationManager.debugError(NMSErrors.EXCEPTION_MODIFYING_CHANNEL_PIPELINE, 0);
                e.printStackTrace();
            }
        });
    }
}
