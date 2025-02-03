package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * An exception thrown whenever the {@link org.bukkit.command.CommandSender} is supposed to be a
 * {@link org.bukkit.entity.Player} but is not.
 */
public class SenderNotPlayerException extends CommandException {
    public SenderNotPlayerException(String msg) {
        super(msg);
    }

    public SenderNotPlayerException() {
        super();
    }
}
