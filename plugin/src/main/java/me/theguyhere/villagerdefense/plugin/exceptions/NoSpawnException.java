package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaSpawn;

/**
 * An exception thrown when an {@link ArenaSpawn} cannot be created.
 */
public class NoSpawnException extends Exception {
    public NoSpawnException(String message) {
        super(message);
    }

    public NoSpawnException(){
        super();
    }
}
