package me.theguyhere.villagerdefense.nms.v1_20_r3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Reflections;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import org.bukkit.entity.Player;

/**
 * A class to handle server bound packets.
 */
@SuppressWarnings("CallToPrintStackTrace")
class InboundPacketHandler extends ChannelInboundHandlerAdapter {
    public static final String HANDLER_NAME = "villager_defense_listener";
    private final Player player;
    private final PacketListener packetListener;

    InboundPacketHandler(Player player, PacketListener packetListener) {
        this.player = player;
        this.packetListener = packetListener;
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
        try {
            if (packet instanceof ServerboundInteractPacket) {
                int entityID = (int) Reflections.getFieldValue(packet, "a");

                // Left click
                if (Reflections.getFieldValue(packet, "b").getClass().getDeclaredFields().length == 0) {
                    packetListener.onAttack(player, entityID);
                }

                // Main hand right click
                else if (Reflections.getFieldValue(packet, "b").getClass().getDeclaredFields().length == 1
                        && Reflections.getFieldValue(Reflections.getFieldValue(packet, "b"), "a")
                        .toString().equalsIgnoreCase("MAIN_HAND")) {
                    packetListener.onInteractMain(player, entityID);
                }
            }
        } catch (Exception e) {
            CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, NMSErrors.EXCEPTION_ON_PACKET_READ);
            e.printStackTrace();
        }
        super.channelRead(context, packet);
    }
}
