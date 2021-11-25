package me.theguyhere.villagerdefense.nms;

import me.theguyhere.villagerdefense.nms.v1_16_R3.EntityNMSArmorStand;
import me.theguyhere.villagerdefense.nms.v1_16_R3.EntityNMSVillager;
import me.theguyhere.villagerdefense.tools.NMSVersion;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

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
            case v1_16_R2:
                return null;
            case v1_16_R3:
                return (new EntityNMSArmorStand(location, text)).getBukkitEntity();
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
            case v1_16_R2:
                return null;
            case v1_16_R3:
                return (new EntityNMSVillager(location)).getBukkitEntity();
            default:
                return null;
        }
    }
}
