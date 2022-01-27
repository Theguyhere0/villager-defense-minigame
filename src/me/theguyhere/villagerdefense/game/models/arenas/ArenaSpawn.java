package me.theguyhere.villagerdefense.game.models.arenas;

import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.game.displays.SpawnIndicator;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ArenaSpawn {
    /** Location of the arena spawn.*/
    private final Location location;
    /** Type of arena spawn.*/
    private final ArenaSpawnType spawnType;
    /** The spawn indicator for this spawn.*/
    private final SpawnIndicator spawnIndicator;
    /** Id of the arena spawn.*/
    private final int id;
    /** Whether the spawn indicator is on or not.*/
    private boolean on;

    public ArenaSpawn(@NotNull Location location, @NotNull ArenaSpawnType spawnType, int id)
            throws InvalidLocationException {
        // Check for null world
        if (location.getWorld() == null)
            throw new InvalidLocationException("Location world cannot be null!");

        this.location = location;
        this.spawnType = spawnType;
        this.id = id;
        spawnIndicator = new SpawnIndicator(location, getName());
        on = false;
    }

    public Location getLocation() {
        return location;
    }

    public ArenaSpawnType getSpawnType() {
        return spawnType;
    }

    public SpawnIndicator getSpawnIndicator() {
        return spawnIndicator;
    }

    public int getId() {
        return id;
    }

    public boolean isOn() {
        return on;
    }

    public void turnOnIndicator() {
        if (!on) {
            spawnIndicator.displayForOnline();
            on = true;
        }
    }

    public void turnOffIndicator() {
        if (on) {
            spawnIndicator.remove();
            on = false;
        }
    }

    private String getName() {
        switch (spawnType) {
            case PLAYER:
                return Utils.format("&6Player Spawn");
            case MONSTER:
                return Utils.format("&3Monster Spawn " + id);
            case VILLAGER:
                return Utils.format("&aVillager Spawn " + id);
            default:
                return null;
        }
    }
}
