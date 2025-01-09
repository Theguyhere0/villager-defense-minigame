package me.theguyhere.villagerdefense.plugin.entities.players;

/**
 * An exception thrown when a {@link LegacyVDPlayer} cannot be found.
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
