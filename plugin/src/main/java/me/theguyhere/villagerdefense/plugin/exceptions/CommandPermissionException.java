package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * An exception thrown whenever the command sender's permission is not sufficient.
 */
@SuppressWarnings("unused")
public class CommandPermissionException extends CommandException {
    public CommandPermissionException(String msg) {
        super(msg);
    }

    public CommandPermissionException() {
        super();
    }
}
