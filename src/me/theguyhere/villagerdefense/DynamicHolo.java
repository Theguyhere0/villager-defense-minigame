package me.theguyhere.villagerdefense;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DynamicHolo {
    private final Main plugin;

    public DynamicHolo(Main plugin) {
        this.plugin = plugin;
    }

    private final HashMap<EntityArmorStand, Integer> holos = new HashMap<>();

    public void createHolo(Location location, int arena, String name) {
        // Get NMS versions of world
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        // Create hologram
        EntityArmorStand holo = new EntityArmorStand(EntityTypes.ARMOR_STAND, nmsWorld);
        holo.setLocation(location.getX(),
                location.getY(),
                location.getZ(),
                0,
                0);
        holo.setInvisible(true);
        holo.setCustomNameVisible(true);
        holo.setCustomName(IChatBaseComponent.ChatSerializer.b(name));
        holo.setNoGravity(true);

        // Add packets
        addHoloPacket(holo);
        holos.put(holo, arena);
    }

    public void createNameHolo(Player player, int arena) {
        createHolo(player.getLocation(), arena, "a" + arena + ".name");
    }

    public void LoadHolo(Location location, int arena) {
        location.setY(location.getY() + 2);
        createHolo(location, arena, "a" + arena + ".name");
    }

    public void addHoloPacket(EntityArmorStand holo) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutSpawnEntity(holo));
        }
    }

    public void removeHolo(Player player, EntityArmorStand holo) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(holo.getId()));
    }

    public void refreshHolo(Player player, EntityArmorStand holo) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(holo.getId()));
        connection.sendPacket(new PacketPlayOutSpawnEntity(holo));
    }

    public void addJoinPacket(Player player) {
        holos.forEach((k, v) -> {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutSpawnEntity(k));
        });
    }

    public HashMap<EntityArmorStand, Integer> getHolos() {
        return holos;
    }
}
