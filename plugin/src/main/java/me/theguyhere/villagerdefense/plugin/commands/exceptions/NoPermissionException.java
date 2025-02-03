package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * An exception thrown whenever the {@link org.bukkit.command.CommandSender} does not have permission to use the command.
 */
public class NoPermissionException extends CommandException {
    public NoPermissionException(String msg) {
        super(msg);
    }

    public NoPermissionException() {
        super();
    }
}
