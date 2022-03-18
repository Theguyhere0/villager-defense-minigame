package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.tools.Utils;
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

    public Portal(@NotNull Location location, Arena arena) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Get difficulty
        String difficulty = arena.getDifficultyLabel();
        if (difficulty != null)
            switch (difficulty) {
                case "Easy":
                    difficulty = " &a&l[" + difficulty + "]";
                    break;
                case "Medium":
                    difficulty = " &e&l[" + difficulty + "]";
                    break;
                case "Hard":
                    difficulty = " &c&l[" + difficulty + "]";
                    break;
                case "Insane":
                    difficulty = " &d&l[" + difficulty + "]";
                    break;
                default:
                    difficulty = "";
            }
        else difficulty = "";

        // Get status
        String status;
        if (arena.isClosed())
            status = "&4&lClosed";
        else if (arena.getStatus() == ArenaStatus.ENDING)
            status = "&c&lEnding";
        else if (arena.getStatus() == ArenaStatus.WAITING)
            status = "&5&lWaiting";
        else status = "&a&lWave: " + arena.getCurrentWave();

        // Get player count color
        String countColor;
        double fillRatio = arena.getActiveCount() / (double) arena.getMaxPlayers();
        if (fillRatio < .8)
            countColor = "&a";
        else if (fillRatio < 1)
            countColor = "&6";
        else countColor = "&c";

        // Set location, hologram, and npc
        this.location = location;
        this.npc = new NPCVillager(location);
        this.hologram = new Hologram(location.clone().add(0, 2.5, 0), false,
                Utils.format("&6&l" + arena.getName() + difficulty),
                Utils.format(status),
                arena.isClosed() ? "" : Utils.format(
                        "&bPlayers: " + countColor + arena.getActiveCount() + "&b / " + arena.getMaxPlayers()),
                arena.isClosed() ? "" : Utils.format("Spectators: " + arena.getSpectatorCount()));
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
