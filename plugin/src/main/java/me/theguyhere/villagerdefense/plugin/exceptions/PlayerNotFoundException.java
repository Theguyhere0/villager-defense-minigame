package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;

/**
 * An exception thrown when a {@link VDPlayer} cannot be found.
 */
@SuppressWarnings("unused")
public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String msg) {
        super(msg);
    }

    public PlayerNotFoundException() {
        super();
    }
}
