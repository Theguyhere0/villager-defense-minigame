package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.EntitySpawnPacketException;
import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.nms.NMSManager;
import me.theguyhere.villagerdefense.packets.PacketManager;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A holographic text line. Not meant to be used on its own.
 */
public class HoloLine {
    /** The text to display.*/
    private final String text;
    /** The location of the HoloLine.*/
    private final Location location;
    /** The armor stand entity used to create the HoloLine.*/
    private final Entity armorStand;

    public HoloLine(String text, @NotNull Location location) throws InvalidLocationException {
        this.text = text;

        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and armor stand
        this.location = location;
        armorStand = NMSManager.getArmorStand(text, location);
    }

    public String getText() {
        return text;
    }

    public Location getLocation() {
        return location;
    }

    public Entity getArmorStand() {
        return armorStand;
    }

    /**
     * Spawn in the HoloLine for every online player.
     */
    public void displayForOnline() {
        try {
            PacketManager.spawnEntityLivingForOnline(armorStand);
            PacketManager.updateHoloLineForOnline(this);
        } catch (EntitySpawnPacketException e) {
            Utils.debugError(e.getMessage(), 1);
        }
    }

    /**
     * Spawn in the HoloLine for a specific player.
     * @param player - The player to display the HoloLine for.
     */
    public void displayForPlayer(Player player) {
        try {
            PacketManager.spawnEntityLivingForPlayer(armorStand, player);
            PacketManager.updateHoloLineForPlayer(this, player);
        } catch (EntitySpawnPacketException e) {
            Utils.debugError(e.getMessage(), 1);
        }
    }

    /**
     * Stop displaying the HoloLine for every online player.
     */
    public void remove() {
        PacketManager.destroyEntityForOnline(armorStand);
    }
}
