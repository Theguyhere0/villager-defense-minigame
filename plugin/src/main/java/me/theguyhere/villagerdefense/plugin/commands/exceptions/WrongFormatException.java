package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * An exception thrown whenever the command format is wrong.
 */
public class WrongFormatException extends CommandException {
    public WrongFormatException(String message) {
        super(message);
    }

    public WrongFormatException() {
        super();
    }
}
