package me.theguyhere.villagerdefense.nms.v1_18_r1;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import me.theguyhere.villagerdefense.common.Log;
import me.theguyhere.villagerdefense.nms.common.*;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
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
    public VillagerPacketEntity newVillagerPacketEntity() {
        return new PacketEntityVillager(new EntityID());
    }

    @Override
    public void nameArena(Player player, String arenaName, int arenaNum) {
        Location location = player.getLocation();
        Material original = location.getBlock().getType();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        NBTTagCompound signNBT = new NBTTagCompound();
        signNBT.a("Text1", String.format("Rename Arena %d:", arenaNum));
        signNBT.a("Text2", "===============");
        signNBT.a("Text3", "arenaName");
        signNBT.a("Text4", "===============");

        PacketGroup.of(
                new BlockChangePacket(position, Material.OAK_SIGN),
                new TileEntityDataPacket(position, 0, signNBT),
                new OpenSignEditorPacket(position),
                new BlockChangePacket(position, original)).sendTo(player);
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
        Channel channel = networkManager.k;

        channel.eventLoop().execute(() -> {
            try {
                pipelineModifierTask.accept(channel.pipeline());
            } catch (Exception e) {
                Log.warning(NMSErrors.EXCEPTION_MODIFYING_CHANNEL_PIPELINE);
                e.printStackTrace();
            }
        });
    }
}
