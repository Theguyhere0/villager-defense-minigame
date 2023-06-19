package me.theguyhere.villagerdefense.plugin.commands;

/**
 * An exception thrown whenever the command sender is not a player.
 */
@SuppressWarnings("unused")
public class CommandPlayerException extends CommandException {
	public CommandPlayerException(String msg) {
		super(msg);
	}

	public CommandPlayerException() {
		super();
	}
}
