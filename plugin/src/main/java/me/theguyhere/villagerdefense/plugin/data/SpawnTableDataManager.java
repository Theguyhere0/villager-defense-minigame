package me.theguyhere.villagerdefense.plugin.data;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;

import java.util.ArrayList;
import java.util.List;

public class SpawnTableDataManager {
    private final YAMLManager yamlManager;

    public SpawnTableDataManager(String name) {
        yamlManager = new YAMLManager("spawnTables/" + name + ".yml");
    }

    public int getVillagersToSpawn(int wave, int current, double multiplier) throws NoSuchPathException {
        // Find the right wave to pull data from
        String spawnWave = String.valueOf(wave);
        if (!yamlManager.hasPath(spawnWave)) {
            if (yamlManager.hasPath("freeplay")) {
                spawnWave = "freeplay";
            }
            else {
                spawnWave = "1";
            }
        }

        // Calculate amount to spawn
        return Math.max((int) (yamlManager.getInteger(spawnWave + ".count.v") * multiplier), 1) - current;
    }

    public int getMonstersToSpawn(int wave, double multiplier) throws NoSuchPathException {
        // Find the right wave to pull data from
        String spawnWave = String.valueOf(wave);
        if (!yamlManager.hasPath(spawnWave)) {
            if (yamlManager.hasPath("freeplay")) {
                spawnWave = "freeplay";
            }
            else {
                spawnWave = "1";
            }
        }

        // Calculate amount to spawn
        int raw = yamlManager.getInteger(spawnWave + ".count.m");
        if (raw == 0) {
            return raw;
        }
        return Math.max((int) (raw * multiplier), 1);
    }

    public List<String> getMonsterTypes(int wave) throws NoSuchPathException, BadDataException {
        // Find the right wave to pull data from
        String spawnWave = String.valueOf(wave);
        if (!yamlManager.hasPath(spawnWave)) {
            if (yamlManager.hasPath("freeplay")) {
                spawnWave = "freeplay";
            }
            else {
                spawnWave = "1";
            }
        }

        // Create proportional type list
        List<String> typeRatio = new ArrayList<>();
        String finalSpawnWave = spawnWave;
        yamlManager.getKeys(spawnWave + ".mtypes").forEach(type -> {
            try {
                for (int i = 0; i < yamlManager.getInteger(finalSpawnWave + ".mtypes." + type); i++) {
                    typeRatio.add(type);
                }
            } catch (NoSuchPathException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        });

        return typeRatio;
    }

    public int getBossesToSpawn(int wave) throws NoSuchPathException {
        // Find the right wave to pull data from
        String spawnWave = String.valueOf(wave);
        if (!yamlManager.hasPath(spawnWave)) {
            if (yamlManager.hasPath("freeplay")) {
                spawnWave = "freeplay";
            }
            else {
                spawnWave = "1";
            }
        }

        // Get amount to spawn
        return yamlManager.getInteger(spawnWave + ".count.b");
    }

    public List<String> getBossTypes(int wave) throws NoSuchPathException, BadDataException {
        // Find the right wave to pull data from
        String spawnWave = String.valueOf(wave);
        if (!yamlManager.hasPath(spawnWave)) {
            if (yamlManager.hasPath("freeplay")) {
                spawnWave = "freeplay";
            }
            else {
                spawnWave = "1";
            }
        }

        // Create proportional type list
        List<String> typeRatio = new ArrayList<>();
        String finalSpawnWave = spawnWave;
        yamlManager.getKeys(spawnWave + ".btypes").forEach(type -> {
            try {
                for (int i = 0; i < yamlManager.getInteger(finalSpawnWave + ".btypes." + type); i++) {
                    typeRatio.add(type);
                }
            } catch (NoSuchPathException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        });

        return typeRatio;
    }
}
