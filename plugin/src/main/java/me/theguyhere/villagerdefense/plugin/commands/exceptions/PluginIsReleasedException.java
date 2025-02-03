package me.theguyhere.villagerdefense.plugin.commands.exceptions;

/**
 * An exception thrown whenever the command should not trigger once the plugin is released.
 */
public class PluginIsReleasedException extends CommandException {
    public PluginIsReleasedException(String message) {
        super(message);
    }

    public PluginIsReleasedException() {
        super();
    }
}
