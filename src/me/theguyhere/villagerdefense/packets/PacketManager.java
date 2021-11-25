package me.theguyhere.villagerdefense.packets;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.theguyhere.villagerdefense.exceptions.EntitySpawnPacketException;
import me.theguyhere.villagerdefense.game.displays.HoloLine;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PacketManager {
    public static void spawnEntityForPlayer(Entity entity, int type, Player player) throws EntitySpawnPacketException {
        if (entity != null && entity.getWorld().equals(player.getWorld())) {
            WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(entity, type, 1);
            packet.sendPacket(player);
        } else throw new EntitySpawnPacketException(
                "Attempted to spawn null entity or an entity in a different world than the player!");
    }

    public static void spawnEntityForOnline(Entity entity, int type) throws EntitySpawnPacketException {
        if (entity == null)
            throw new EntitySpawnPacketException("Attempted to spawn null entity!");

        for (Player player : Bukkit.getOnlinePlayers())
            spawnEntityForPlayer(entity, type, player);
    }

    public static void spawnEntityLivingForPlayer(Entity entity, Player player) throws EntitySpawnPacketException {
        if (entity != null && entity.getWorld().equals(player.getWorld())) {
            WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving(entity);
            packet.sendPacket(player);
        } else throw new EntitySpawnPacketException(
                "Attempted to spawn null entity or an entity in a different world than the player!");
    }

    public static void spawnEntityLivingForOnline(Entity entity) throws EntitySpawnPacketException {
        if (entity == null)
            throw new EntitySpawnPacketException("Attempted to spawn null entity!");

        for (Player player : Bukkit.getOnlinePlayers())
            spawnEntityLivingForPlayer(entity, player);
    }

    public static void destroyEntityForPlayer(Entity entity, Player player) {
        WrapperPlayServerEntityDestroy packet = new WrapperPlayServerEntityDestroy();
        packet.setEntities(new int[]{entity.getEntityId()});
        packet.sendPacket(player);
    }

    public static void destroyEntityForOnline(Entity entity) {
        for (Player player : Bukkit.getOnlinePlayers())
            destroyEntityForPlayer(entity, player);
    }

    public static void updateHoloLineForPlayer(HoloLine holoLine, Player player) {
        WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata();
        packet.setEntityID(holoLine.getArmorStand().getEntityId());

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        MetadataHelper.setEntityStatus(dataWatcher, (byte) 0x20); // Invisible
        MetadataHelper.setArmorStandStatus(dataWatcher, (byte) (0x01 | 0x08 | 0x10)); // Small, no base-plate, marker
        if (holoLine.getText() != null && !holoLine.getText().isEmpty()) {
            MetadataHelper.setCustomNameNMSObject(dataWatcher, CraftChatMessage.fromStringOrNull(holoLine.getText()));
            MetadataHelper.setCustomNameVisible(dataWatcher, true);
        }
        packet.setEntityMetadata(dataWatcher.getWatchableObjects());
        packet.sendPacket(player);
    }

    public static void updateHoloLineForOnline(HoloLine holoLine) {
        for (Player player : Bukkit.getOnlinePlayers())
            updateHoloLineForPlayer(holoLine, player);
    }

    public static void entityHeadRotationForPlayer(Entity entity, float yaw, Player player) {
        WrapperPlayServerEntityHeadRotation packet = new WrapperPlayServerEntityHeadRotation();
        packet.setEntityId(entity.getEntityId());
        packet.setHeadYaw(yaw);
        packet.sendPacket(player);
    }

    public static void entityHeadRotationForOnline(Entity entity, float yaw) {
        for (Player player : Bukkit.getOnlinePlayers())
            entityHeadRotationForPlayer(entity, yaw, player);
    }
}
