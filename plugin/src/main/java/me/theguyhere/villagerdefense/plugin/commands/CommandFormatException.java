package me.theguyhere.villagerdefense.plugin.commands;

/**
 * An exception thrown whenever the command format is not valid.
 */
@SuppressWarnings("unused")
public class CommandFormatException extends CommandException {
	public CommandFormatException(String msg) {
		super(msg);
	}

	public CommandFormatException() {
		super();
	}
}
