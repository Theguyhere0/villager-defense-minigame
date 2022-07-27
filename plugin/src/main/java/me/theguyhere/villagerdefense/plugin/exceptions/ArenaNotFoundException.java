package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;

/**
 * An exception thrown when an {@link Arena} cannot be found.
 */
public class ArenaNotFoundException extends ArenaException {
    public ArenaNotFoundException(String msg) {
        super(msg);
    }

    public ArenaNotFoundException() {
        super();
    }
}
