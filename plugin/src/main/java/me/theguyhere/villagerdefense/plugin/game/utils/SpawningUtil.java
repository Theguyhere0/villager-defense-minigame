package me.theguyhere.villagerdefense.plugin.game.utils;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidVDMobKeyException;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.minions.VDMinion;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.villagers.VDNormalVillager;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A utility class to generate the spawn sequence for monsters and villagers.
 */
public class SpawningUtil {
    /**
     * Generates a list of spawning tasks for villagers of an Arena.
     * @param arena Arena of interest.
     * @return List of spawning tasks.
     */
    @NotNull
    public static List<BukkitTask> generateVillagerSpawnSequence(Arena arena) {
        List<BukkitTask> spawningTasks = new ArrayList<>();

        // Check if the arena is capable of spawning
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return spawningTasks;

        // Gather wave information
        DataManager data = new DataManager("spawnTables/" + arena.getSpawnTableFile());
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave)) {
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";
        }
        int waveValue = calculateWaveValue(data.getConfig().getInt(wave + ".value"), arena);
        int toSpawn = waveValue / 125 - arena.getVillagers();

        // Generate spawn sequence
        Random r = new Random();
        int delay = 0;
        arena.setSpawningVillagers(true);

        for (int i = 0; i < toSpawn; i++) {
            // Recalculate delay
            delay += 2 * spawnDelayTicks(spawningTasks.size());

            // Get spawn locations
            Location spawn = arena.getVillagerSpawnLocations().get(r.nextInt(arena.getVillagerSpawnLocations().size()));

            // Create task for spawning
            spawningTasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    arena.addMob(new VDNormalVillager(arena, spawn));
                }
            }.runTaskLater(Main.plugin, delay));
        }

        // Set max enemies
        arena.setMaxEnemies(spawningTasks.size());

        // Set spawning villagers to false
        spawningTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                arena.setSpawningVillagers(false);
            }
        }.runTaskLater(Main.plugin, delay));

        return spawningTasks;
    }

    /**
     * Generates a list of spawning tasks for monsters of an Arena.
     * @param arena Arena of interest.
     * @return List of spawning tasks.
     */
    @NotNull
    public static List<BukkitTask> generateMinionSpawnSequence(Arena arena) {
        List<BukkitTask> spawningTasks = new ArrayList<>();

        // Check if the arena is capable of spawning
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return spawningTasks;

        // Gather wave information
        DataManager data = new DataManager("spawnTables/" + arena.getSpawnTableFile());
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave)) {
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";
        }
        HashMap<String, Double> targetMinionRatio = getTargetRatio(data, wave + ".mtypes");
        HashMap<String, Double> minionRatio = new HashMap<>();
        HashMap<String, Integer> minionValue = new HashMap<>();
        HashMap<String, Integer> minionValueSpawned = new HashMap<>();
        targetMinionRatio.keySet().forEach(type -> {
            minionRatio.put(type, 0d);
            try {
                minionValue.put(type, VDMinion.getValueOf(type, arena));
            } catch (InvalidVDMobKeyException ignored) {
            }
            minionValueSpawned.put(type, 0);
        });
        int waveValue = calculateWaveValue(data.getConfig().getInt(wave + ".value"), arena);
        final int[] valueSpawned = {0};

        // Generate spawn sequence
        Random r = new Random();
        int delay = 0;
        arena.setSpawningMonsters(true);

        while (valueSpawned[0] < waveValue) {
            // Recalculate delay and current ratio
            delay += spawnDelayTicks(spawningTasks.size());
            minionRatio.replaceAll((type, ratio) -> minionValueSpawned.get(type) / (double) valueSpawned[0]);

            // Get spawn locations and type
            Location ground = arena.getMonsterGroundSpawnLocations()
                    .get(r.nextInt(arena.getMonsterGroundSpawnLocations().size()));
            Location air = arena.getMonsterAirSpawnLocations()
                    .get(r.nextInt(arena.getMonsterAirSpawnLocations().size()));
            String type = findNextType(waveValue - valueSpawned[0], targetMinionRatio, minionRatio,
                    minionValue);

            // Create task for spawning if there is anything to spawn
            if (type == null)
                valueSpawned[0] = waveValue;
            else {
                spawningTasks.add(new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            arena.addMob(VDMinion.of(type, arena, ground, air));
                        } catch (InvalidVDMobKeyException e) {
                            CommunicationManager.debugError("Invalid mob key detected in spawn file!", 1);
                        } catch (Exception ignored) {
                        }
                    }
                }.runTaskLater(Main.plugin, delay));
                minionValueSpawned.replace(type, minionValueSpawned.get(type) + minionValue.get(type));
                valueSpawned[0] += minionValue.get(type);
            }
        }

        // Set spawning monsters to false
        spawningTasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                arena.setSpawningMonsters(false);
            }
        }.runTaskLater(Main.plugin, delay));

        return spawningTasks;
    }

    private static int spawnDelayTicks(int mobNum) {
        Random r = new Random();
        return r.nextInt((int) (60 * Math.pow(Math.E, - mobNum / 60d)));
    }

    private static int calculateWaveValue(int original, Arena arena) {
        return (int) (original * (Math.pow(arena.getActiveCount() - 1, .8) + 1));
    }

    private static HashMap<String, Double> getTargetRatio(DataManager data, String path) {
        HashMap<String, Double> targetRatio = new HashMap<>();
        AtomicInteger total = new AtomicInteger();

        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
                    int amount = data.getConfig().getInt(path + "." + type);
                    targetRatio.put(type, (double) amount);
                    total.addAndGet(amount);
                });
        targetRatio.replaceAll((type, ratio) -> ratio / (double) total.get());
        return targetRatio;
    }

    private static String findNextType(int valueLeftToSpawn, HashMap<String, Double> targetRatio,
                                       HashMap<String, Double> currentRatio, HashMap<String, Integer> values) {
        // Check if anything can spawn at all
        if (values.values().stream().noneMatch(value -> value <= valueLeftToSpawn))
            return null;

        // Get ratio differences
        HashMap<String, Double> ratioDifference = new HashMap<>();
        currentRatio.keySet().forEach(key -> ratioDifference.put(key, currentRatio.get(key) - targetRatio.get(key)));

        // Return lowest ratio difference that is below value left to spawn
        return ratioDifference.keySet().stream().filter(key -> values.get(key) <= valueLeftToSpawn)
                .sorted(Comparator.comparingDouble(ratioDifference::get))
                .collect(Collectors.toList()).get(0);
    }
}
