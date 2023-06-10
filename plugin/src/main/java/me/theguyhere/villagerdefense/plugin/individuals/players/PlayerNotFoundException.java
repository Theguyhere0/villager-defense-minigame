package me.theguyhere.villagerdefense.plugin.individuals.players;

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
