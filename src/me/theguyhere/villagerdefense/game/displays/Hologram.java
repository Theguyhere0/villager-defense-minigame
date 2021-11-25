package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Floating text displayed on client side. Can have multiple lines.
 */
public class Hologram {
    /** Text to be displayed by the Hologram.*/
    private final HoloLine[] lines;
    /** The location of the Hologram.*/
    private final Location location;

    public Hologram(@NotNull Location location, String... lines) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and lines
        this.location = location;
        this.lines = new HoloLine[lines.length];
        for (int i = 0; i < lines.length; i++)
            this.lines[i] = new HoloLine(lines[i], location.clone().add(0, (lines.length - i - 1) * .25, 0));
    }

    public Location getLocation() {
        return location;
    }

    public HoloLine[] getLines() {
        return lines;
    }

    /**
     * Spawn in the Hologram for every online player.
     */
    public void displayForOnline() {
        Arrays.stream(lines).forEach(HoloLine::displayForOnline);
    }

    /**
     * Spawn in the Hologram for a specific player.
     * @param player - The player to display the Hologram for.
     */
    public void displayForPlayer(Player player) {
        Arrays.stream(lines).forEach(holoLine -> holoLine.displayForPlayer(player));
    }

    /**
     * Stop displaying the Hologram for every online player.
     */
    public void remove() {
        Arrays.stream(lines).forEach(HoloLine::remove);
    }
}
