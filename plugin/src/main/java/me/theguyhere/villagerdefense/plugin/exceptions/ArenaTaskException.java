package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;

/**
 * An exception thrown when a task run by an {@link Arena} encounters an error.
 */
@SuppressWarnings("unused")
public class ArenaTaskException extends ArenaException {
    public ArenaTaskException(String msg) {
        super(msg);
    }

    public ArenaTaskException() {
        super();
    }
}
