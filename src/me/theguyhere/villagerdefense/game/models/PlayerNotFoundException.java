package me.theguyhere.villagerdefense.game.models;

/**
 * An exception thrown when a {@link VDPlayer} cannot be found.
 */
public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException() {
        super();
    }
}
