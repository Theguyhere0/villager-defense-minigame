package me.theguyhere.villagerdefense.plugin.game.exceptions;

import me.theguyhere.villagerdefense.plugin.game.Arena;

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
