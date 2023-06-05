package me.theguyhere.villagerdefense.nms.v1_18_r2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import org.bukkit.entity.Player;

/**
 * A class to handle server bound packets.
 */
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
                int entityID = Utils.getFieldValue(packet, "a", Integer.class);

                // Left click
                if (Utils.getFieldValue(packet, "b").getClass().getDeclaredFields().length == 0) {
                    packetListener.onAttack(player, entityID);
                }

                // Main hand right click
                else if (Utils.getFieldValue(packet, "b").getClass().getDeclaredFields().length == 1
                        && Utils.getFieldValue(Utils.getFieldValue(packet, "b"), "a")
                        .toString().equalsIgnoreCase("MAIN_HAND")) {
                    packetListener.onInteractMain(player, entityID);
                }
            }

            else if (packet instanceof ServerboundSignUpdatePacket) {
                packetListener.onSignUpdate(player, ((ServerboundSignUpdatePacket) packet).getLines());
            }
        } catch (Exception e) {
            CommunicationManager.debugError(NMSErrors.EXCEPTION_ON_PACKET_READ, 0);
            e.printStackTrace();
        }
        super.channelRead(context, packet);
    }
}
