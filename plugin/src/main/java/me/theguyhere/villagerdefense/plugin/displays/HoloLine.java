package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.managers.NMSVersion;
import me.theguyhere.villagerdefense.plugin.managers.PlayerManager;
import org.bukkit.Location;
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
    /** Text packet entity representing this HoloLine.*/
    private final TextPacketEntity textPacketEntity;

    public HoloLine(String text, @NotNull Location location) throws InvalidLocationException {
        this.text = text;

        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and packet entity
        this.location = location;
        this.textPacketEntity = NMSVersion.getCurrent().getNmsManager().newTextPacketEntity();
    }

    public String getText() {
        return text;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Spawn in the HoloLine for every online player.
     */
    public void displayForOnline() {
        PlayerManager.sendLocationPacketToOnline(textPacketEntity.newSpawnPackets(location, text), location.getWorld());
    }

    /**
     * Spawn in the HoloLine for a specific player.
     * @param player - The player to display the HoloLine for.
     */
    public void displayForPlayer(Player player) {
        // Only display if player is in the same world
        if (player.getWorld().equals(location.getWorld()))
            textPacketEntity.newSpawnPackets(location, text).sendTo(player);
    }

    /**
     * Stop displaying the HoloLine for every online player.
     */
    public void remove() {
        PlayerManager.sendPacketToOnline(textPacketEntity.newDestroyPackets());
    }
}
