package me.theguyhere.villagerdefense.nms;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.nms.v1_16_R3.EntityNMSArmorStand;
import me.theguyhere.villagerdefense.nms.v1_16_R3.EntityNMSVillager;
import me.theguyhere.villagerdefense.packets.PacketManager;
import me.theguyhere.villagerdefense.packets.PacketReader;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * A manager class to retrieve version-agnostic Bukkit entities based on the detected server Minecraft version.
 */
public class NMSManager {
    /**
     * Retrieves a version-agnostic Bukkit entity of the armor stand based on the detected server Minecraft version.
     *
     * @param text - The text to be displayed by the armor stand.
     * @param location - The location of the armor stand.
     * @return - Bukkit entity of the armor stand.
     */
    public static Entity getArmorStand(String text, @NotNull Location location) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return (new EntityNMSArmorStand(location, text)).getBukkitEntity();
            case v1_17_R1:
                return (new me.theguyhere.villagerdefense.nms.v1_17_R1.EntityNMSArmorStand(location, text)).getBukkitEntity();
            case v1_18_R1:
                return null;
            default:
                return null;
        }
    }

    /**
     * Retrieves a version-agnostic Bukkit entity of the villager based on the detected server Minecraft version.
     *
     * @param location - The location of the villager.
     * @return - Bukkit entity of the villager.
     */
    public static Entity getVillager(@NotNull Location location) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return (new EntityNMSVillager(location)).getBukkitEntity();
            case v1_17_R1:
                return (new me.theguyhere.villagerdefense.nms.v1_17_R1.EntityNMSVillager(location)).getBukkitEntity();
            case v1_18_R1:
                return null;
            default:
                return null;
        }
    }

    /**
     * Retrieves a version-agnostic Channel.
     *
     * @param player - The player to retrieve the channel from.
     * @return - Channel of the player.
     */
    public static Channel getChannel(@NotNull Player player) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                CraftPlayer craftPlayer16R3 = (CraftPlayer) player;
                return craftPlayer16R3.getHandle().playerConnection.networkManager.channel;
            case v1_17_R1:
                org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer craftPlayer17R1 =
                        (org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) player;
                return craftPlayer17R1.getHandle().b.a.k;
            case v1_18_R1:
                return null;
            default:
                return null;
        }
    }

    /**
     * Retrieves a version-agnostic Object representing the IChatBaseComponent class.
     *
     * @param string - String to convert.
     * @return - Generalized IChatBaseComponent.
     */
    public static Object getIChatBaseComponent(String string) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return CraftChatMessage.fromStringOrNull(string);
            case v1_17_R1:
                return org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage.fromStringOrNull(string);
            case v1_18_R1:
                return null;
            default:
                return null;
        }
    }

    /**
     * Retrieves a version-agnostic ChannelHandler to handle PacketPlayInUseEntity packets.
     *
     * @param player - Player packet was sent from.
     * @return - Proper ChannelHandler
     */
    public static ChannelHandler getClientUseEntityHandler(@NotNull Player player) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return new MessageToMessageDecoder<PacketPlayInUseEntity>() {

                    @Override
                    protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) {
                        arg.add(packet);
                        PacketReader.readPacket(player, packet);
                    }

                };
            case v1_17_R1:
            case v1_18_R1:
                return new MessageToMessageDecoder<net.minecraft.network.protocol.game.PacketPlayInUseEntity>() {

                    @Override
                    protected void decode(ChannelHandlerContext channel,
                                          net.minecraft.network.protocol.game.PacketPlayInUseEntity packet,
                                          List<Object> arg) {
                        arg.add(packet);
                        PacketReader.readPacket(player, packet);
                    }

                };
            default:
                return null;
        }
    }

    /**
     * Retrieves a version-agnostic ChannelHandler to handle PacketPlayInUpdateSign packets.
     *
     * @param player - Player packet was sent from.
     * @return - Proper ChannelHandler
     */
    public static ChannelHandler getClientUpdateSignHandler(@NotNull Player player) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return new MessageToMessageDecoder<PacketPlayInUpdateSign>() {

                    @Override
                    protected void decode(ChannelHandlerContext channel, PacketPlayInUpdateSign packet, List<Object> arg) {
                        arg.add(packet);
                        PacketReader.readPacket(player, packet);
                    }

                };
            case v1_17_R1:
            case v1_18_R1:
                return new MessageToMessageDecoder<net.minecraft.network.protocol.game.PacketPlayInUpdateSign>() {

                    @Override
                    protected void decode(ChannelHandlerContext channel,
                                          net.minecraft.network.protocol.game.PacketPlayInUpdateSign packet,
                                          List<Object> arg) {
                        arg.add(packet);
                        PacketReader.readPacket(player, packet);
                    }

                };
            default:
                return null;
        }
    }

    /**
     * Retrieves the correct name of the field for sign GUI header.
     *
     * @return Field name.
     */
    public static String getSignGUIHeaderField() {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return "b";
            case v1_17_R1:
            case v1_18_R1:
                return "c";
            default:
                return null;
        }
    }

    /**
     * Checks whether the PLayInUseEntity packet was of type ATTACK.
     *
     * @param packet The packet to check.
     * @return Whether the packet meets requirements.
     */
    public static boolean checkAttack(Object packet) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return PacketManager.getValue(packet, "action").toString().equalsIgnoreCase("ATTACK");
            case v1_17_R1:
                return PacketManager.getValue(packet, "b").getClass().getDeclaredFields().length == 0;
            default:
                return false;
        }
    }

    /**
     * Checks whether the PLayInUseEntity packet was of type INTERACT and hand MAIN.
     *
     * @param packet The packet to check.
     * @return Whether the packet meets requirements.
     */
    public static boolean checkInteractMain(Object packet) {
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                return PacketManager.getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")
                        && PacketManager.getValue(packet, "d").toString().equalsIgnoreCase("MAIN_HAND");
            case v1_17_R1:
                return PacketManager.getValue(packet, "b").getClass().getDeclaredFields().length == 1
                        && PacketManager.getValue(PacketManager.getValue(packet, "b"), "a")
                        .toString().equalsIgnoreCase("MAIN_HAND");
            default:
                return false;
        }
    }

    /**
     * Opens up the sign GUI for players to name an arena
     *
     * @param player - The player opening the menu.
     * @param arena - The arena to rename.
     */
    public static void nameArena(Player player, Arena arena) {
        Location location = player.getLocation();
        location.setY(0);
        switch (NMSVersion.getCurrent()) {
            case v1_16_R3:
                BlockPosition blockPosition16R3 = new BlockPosition(location.getX(), location.getY(), location.getZ());
                player.sendBlockChange(location, Bukkit.createBlockData(Material.OAK_SIGN));

                TileEntitySign sign16R3 = new TileEntitySign();
                sign16R3.setPosition(blockPosition16R3);
                sign16R3.lines[0] = CraftSign.sanitizeLines(new String[]{String.format("Rename Arena %d:", arena.getArena())})[0];
                sign16R3.lines[1] = CraftSign.sanitizeLines(new String[]{CommunicationManager.format("===============")})[0];
                sign16R3.lines[3] = CraftSign.sanitizeLines(new String[]{CommunicationManager.format("===============")})[0];
                sign16R3.lines[2] = CraftSign.sanitizeLines(new String[]{arena.getName()})[0];
                sign16R3.update();

                EntityPlayer entityPlayer16R3 = ((CraftPlayer) player).getHandle();
                Object packet = sign16R3.getUpdatePacket();
                Object packet2 = new PacketPlayOutOpenSignEditor(blockPosition16R3);
                entityPlayer16R3.playerConnection.sendPacket((Packet<?>) packet);
                entityPlayer16R3.playerConnection.sendPacket((Packet<?>) packet2);

                break;
            case v1_17_R1:
                net.minecraft.core.BlockPosition blockPosition17R1 =
                        new net.minecraft.core.BlockPosition(location.getX(), location.getY(), location.getZ());
                player.sendBlockChange(location, Bukkit.createBlockData(Material.OAK_SIGN));

                net.minecraft.world.level.block.entity.TileEntitySign sign17R1 =
                        new net.minecraft.world.level.block.entity.TileEntitySign(blockPosition17R1, null);
                sign17R1.d[0] = org.bukkit.craftbukkit.v1_17_R1.block.CraftSign.sanitizeLines(
                        new String[]{String.format("Rename Arena %d:", arena.getArena())})[0];
                sign17R1.d[1] = org.bukkit.craftbukkit.v1_17_R1.block.CraftSign.sanitizeLines(
                        new String[]{CommunicationManager.format("===============")})[0];
                sign17R1.d[3] = org.bukkit.craftbukkit.v1_17_R1.block.CraftSign.sanitizeLines(
                        new String[]{CommunicationManager.format("===============")})[0];
                sign17R1.d[2] = org.bukkit.craftbukkit.v1_17_R1.block.CraftSign.sanitizeLines(
                        new String[]{arena.getName()})[0];
                sign17R1.update();

                net.minecraft.server.level.EntityPlayer entityPlayer17R1 =
                        ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) player).getHandle();
                entityPlayer17R1.b.sendPacket(sign17R1.getUpdatePacket());
                entityPlayer17R1.b.sendPacket(
                        new net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor(blockPosition17R1));
        }
    }
}
