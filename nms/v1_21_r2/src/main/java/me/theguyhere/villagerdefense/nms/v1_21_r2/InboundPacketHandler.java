package me.theguyhere.villagerdefense.nms.v1_21_r2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Reflections;
import me.theguyhere.villagerdefense.nms.common.NMSErrors;
import me.theguyhere.villagerdefense.nms.common.PacketListener;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import org.bukkit.entity.Player;

/**
 * Class borrowed from filoghost.
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
            if (packet instanceof PacketPlayInUseEntity) {
                int entityID = (int) Reflections.getFieldValue(packet, "b");

                // Left click
                if (Reflections.getFieldValue(packet, "c").getClass().getDeclaredFields().length == 0) {
                    packetListener.onAttack(player, entityID);
                }

                // Main hand right click
                else if (Reflections.getFieldValue(packet, "c").getClass().getDeclaredFields().length == 1
                        && Reflections.getFieldValue(Reflections.getFieldValue(packet, "c"), "a")
                        .toString().equalsIgnoreCase("MAIN_HAND")) {
                    packetListener.onInteractMain(player, entityID);
                }
            }
        } catch (Exception e) {
            CommunicationManager.debugError(NMSErrors.EXCEPTION_ON_PACKET_READ, CommunicationManager.DebugLevel.QUIET);
            e.printStackTrace();
        }
        super.channelRead(context, packet);
    }
}
