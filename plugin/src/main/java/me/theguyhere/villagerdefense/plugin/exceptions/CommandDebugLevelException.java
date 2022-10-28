package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * An exception thrown whenever the debug level is not valid.
 */
@SuppressWarnings("unused")
public class CommandDebugLevelException extends CommandException {
    public CommandDebugLevelException(String msg) {
        super(msg);
    }

    public CommandDebugLevelException() {
        super();
    }
}
