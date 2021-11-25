package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The portal to an Arena.
 */
public class Portal {
    /** The NPC for the Portal.*/
    private final NPCVillager npc;
    /** The information for the Portal.*/
    private final Hologram hologram;
    /** The location of the Portal.*/
    private final Location location;

    public Portal(@NotNull Location location, String[] lines) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Set location, hologram, and npc
        this.location = location;
        this.npc = new NPCVillager(location);
        this.hologram = new Hologram(location.clone().add(0, 2, 0), lines);
    }

    public Location getLocation() {
        return location;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public NPCVillager getNpc() {
        return npc;
    }

    /**
     * Spawn in the Portal for every online player.
     */
    public void displayForOnline() {
        hologram.displayForOnline();
        npc.displayForOnline();
    }

    /**
     * Spawn in the Portal for a specific player.
     * @param player - The player to display the Portal for.
     */
    public void displayForPlayer(Player player) {
        hologram.displayForPlayer(player);
        npc.displayForPlayer(player);
    }

    /**
     * Stop displaying the Portal for every online player.
     */
    public void remove() {
        hologram.remove();
        npc.remove();
    }
}
