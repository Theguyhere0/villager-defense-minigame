package me.theguyhere.villagerdefense.plugin.arenas.exceptions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;

/**
 * An exception thrown when an {@link Arena} cannot be found.
 */
public class ArenaNotFoundException extends Exception {
    public ArenaNotFoundException(String message) {
        super(message);
    }

    public ArenaNotFoundException() {
        super();
    }
}
