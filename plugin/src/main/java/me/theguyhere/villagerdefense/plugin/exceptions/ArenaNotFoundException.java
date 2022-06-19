package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * An exception thrown when an {@link me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena} cannot be found.
 */
public class ArenaNotFoundException extends Exception {
    public ArenaNotFoundException(String message) {
        super(message);
    }

    public ArenaNotFoundException() {
        super();
    }
}
