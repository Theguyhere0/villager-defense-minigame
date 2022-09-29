package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;

/**
 * An exception thrown whenever the name given to an {@link Arena} is not allowed.
 */
@SuppressWarnings("unused")
public class IllegalArenaNameException extends ArenaException{
    public IllegalArenaNameException(String msg) {
        super(msg);
    }

    public IllegalArenaNameException() {
        super();
    }
}
