package me.theguyhere.villagerdefense.plugin.entities;

/**
 * An exception thrown when a {@link VDPlayer} cannot be found.
 */
@SuppressWarnings("unused")
public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException() {
        super();
    }
}
