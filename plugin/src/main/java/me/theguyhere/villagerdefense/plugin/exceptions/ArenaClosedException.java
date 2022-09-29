package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;

/**
 * An exception thrown whenever an action was prevented due to an {@link Arena} being closed.
 */
@SuppressWarnings("unused")
public class ArenaClosedException extends ArenaException {
    public ArenaClosedException(String msg) {
        super(msg);
    }

    public ArenaClosedException() {
        super();
    }
}
