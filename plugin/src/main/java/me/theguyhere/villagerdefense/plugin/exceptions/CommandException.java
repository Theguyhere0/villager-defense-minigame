package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * The base class for exceptions related to command execution.
 */
@SuppressWarnings("unused")
public abstract class CommandException extends Exception {
    public CommandException(String msg) {
        super(msg);
    }

    public CommandException() {
        super();
    }
}
