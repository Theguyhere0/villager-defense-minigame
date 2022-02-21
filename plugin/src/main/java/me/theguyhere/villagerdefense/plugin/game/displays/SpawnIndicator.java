package me.theguyhere.villagerdefense.plugin.game.displays;

import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A holographic indicator to tell admins which spawn is which.
 */
public class SpawnIndicator {
    /** The information for the SpawnIndicator.*/
    private final Hologram hologram;
    /** The location of the SpawnIndicator.*/
    private final Location location;

    public SpawnIndicator(@NotNull Location location, String name) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location and hologram
        this.location = location;
        this.hologram = new Hologram(location.clone().add(0, .75, 0), name);
    }

    public Location getLocation() {
        return location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Spawn in the indicator for every online player.
     */
    public void displayForOnline() {
        hologram.displayForOnline();
    }

    /**
     * Spawn in the indicator for a specific player.
     * @param player - The player to display the indicator for.
     */
    public void displayForPlayer(Player player) {
        hologram.displayForPlayer(player);
    }

    /**
     * Stop displaying the indicator for every online player.
     */
    public void remove() {
        hologram.remove();
    }
}
