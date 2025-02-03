package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * An exception thrown whenever the {@link me.theguyhere.villagerdefense.common.CommunicationManager.DebugLevel} is
 * not at the right level for the command to trigger.
 */
public class DebugLevelException extends CommandException {
    public DebugLevelException(String message) {
        super(message);
    }

    public DebugLevelException() {
        super();
    }
}
