package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * The base class for all exceptions related to commands.
 */
public class CommandException extends Exception {
    public CommandException(String msg) {
        super(msg);
    }

    public CommandException() {
        super();
    }
}
