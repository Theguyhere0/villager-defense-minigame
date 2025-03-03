package me.theguyhere.villagerdefense.nms.v1_21_r3;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Reflections;
import me.theguyhere.villagerdefense.nms.common.EntityID;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.nms.common.entities.VillagerPacketEntity;
import net.minecraft.network.Connection;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Manager class for a specific NMS version.
 */
@SuppressWarnings("CallToPrintStackTrace")
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
    public String getSpawnParticleName() {
        return "FLAME";
    }

    @Override
    public String getMonsterParticleName() {
        return "SOUL_FIRE_FLAME";
    }

    @Override
    public String getVillagerParticleName() {
        return "COMPOSTER";
    }

    @Override
    public String getBorderParticleName() {
        return "REDSTONE";
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
     *
     * @param player               Player to affect.
     * @param pipelineModifierTask Consumer function for modifying pipeline.
     */
    private void modifyPipeline(Player player, Consumer<ChannelPipeline> pipelineModifierTask) {
        Connection connection = (Connection) Reflections.getSuperFieldValue(
            ((CraftPlayer) player).getHandle().connection,
            "e"
        );
        Channel channel = connection.channel;

        channel.eventLoop().execute(() -> {
            try {
                pipelineModifierTask.accept(channel.pipeline());
            } catch (Exception e) {
                CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, NMSErrors.EXCEPTION_MODIFYING_CHANNEL_PIPELINE
                );
                e.printStackTrace();
            }
        });
    }
}
