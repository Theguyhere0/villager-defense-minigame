package me.theguyhere.villagerdefense.plugin.game.managers;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to manage generating the spawn sequence for monsters and villagers.
 */
public class SpawnManager {
    /**
     * Generates a list of spawning tasks for monsters of an Arena.
     * @param arena Arena of interest.
     * @return List of spawning tasks.
     */
    public static List<BukkitTask> generateMinionSpawnSequence(Arena arena) {
        // Check if the arena is capable of spawning
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return null;

        List<BukkitTask> spawningTasks = new ArrayList<>();

        // TODO: move delay function here
        // TODO: create function to get proper spawn location
        // TODO:

        return spawningTasks;
    }
}
