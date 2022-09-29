package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;

/**
 * An exception thrown whenever an action was prevented due to a mismatch in the required {@link ArenaStatus}.
 */
@SuppressWarnings("unused")
public class ArenaStatusException extends ArenaException {
    public ArenaStatusException(ArenaStatus status) {
        super("Arena must have a status of " + status);
    }

    public ArenaStatusException(String msg) {
        super(msg);
    }

    public ArenaStatusException() {
        super();
    }
}
