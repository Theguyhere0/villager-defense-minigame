package me.theguyhere.villagerdefense.plugin.arenas;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;

/**
 * The base class for exceptions related to {@link Arena}.
 */
@SuppressWarnings("unused")
public abstract class ArenaException extends Exception {
    public ArenaException(String msg) {
        super(msg);
    }

    public ArenaException() {
        super();
    }
}
