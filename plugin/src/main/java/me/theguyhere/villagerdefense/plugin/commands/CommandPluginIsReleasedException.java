package me.theguyhere.villagerdefense.plugin.commands;

/**
 * An exception thrown whenever the plugin is not supposed to be released.
 */
@SuppressWarnings("unused")
public class CommandPluginIsReleasedException extends CommandException {
    public CommandPluginIsReleasedException(String msg) {
        super(msg);
    }

    public CommandPluginIsReleasedException() {
        super();
    }
}
