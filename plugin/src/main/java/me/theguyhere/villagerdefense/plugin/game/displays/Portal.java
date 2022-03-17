package me.theguyhere.villagerdefense.plugin.game.displays;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
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

    public Portal(@NotNull Location location, Arena arena, Main plugin) throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        // Get difficulty
        String difficulty = arena.getDifficultyLabel();
        if (difficulty != null)
            switch (difficulty) {
                case "Easy":
                    difficulty = " &a&l[" + plugin.getLanguageString("names." + difficulty.toLowerCase()) + "]";
                    break;
                case "Medium":
                    difficulty = " &e&l[" + plugin.getLanguageString("names." + difficulty.toLowerCase()) + "]";
                    break;
                case "Hard":
                    difficulty = " &c&l[" + plugin.getLanguageString("names." + difficulty.toLowerCase()) + "]";
                    break;
                case "Insane":
                    difficulty = " &d&l[" + plugin.getLanguageString("names." + difficulty.toLowerCase()) + "]";
                    break;
                default:
                    difficulty = "";
            }
        else difficulty = "";

        // Get status
        String status;
        if (arena.isClosed())
            status = "&4&l" + plugin.getLanguageString("messages.closed");
        else if (arena.getStatus() == ArenaStatus.ENDING)
            status = "&c&l" + plugin.getLanguageString("messages.ending");
        else if (arena.getStatus() == ArenaStatus.WAITING)
            status = "&5&l" + plugin.getLanguageString("messages.waiting");
        else status = "&a&l" + plugin.getLanguageString("messages.wave") + ": " + arena.getCurrentWave();

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
                CommunicationManager.format("&6&l" + arena.getName() + difficulty),
                CommunicationManager.format(status),
                arena.isClosed() ? "" : CommunicationManager.format("&b" +
                        plugin.getLanguageString("messages.players") + ": " + countColor +
                        arena.getActiveCount() + "&b / " + arena.getMaxPlayers()),
                arena.isClosed() ? "" : CommunicationManager.format(
                        plugin.getLanguageString("messages.spectators") + ": " + arena.getSpectatorCount()));
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
