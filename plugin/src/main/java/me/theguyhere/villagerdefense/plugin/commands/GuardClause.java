package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.DebugLevelException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.NoPermissionException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.PluginIsReleasedException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.SenderNotPlayerException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A static class to provide guard clauses in the command executor implementation.
 */
@SuppressWarnings({"unused", "SameParameterValue"})
class GuardClause {
    /**
     * Precedence of 2.
     */
    static void checkSenderPermissions(Player sender, Permission commandPermission) throws NoPermissionException {
        if (sender.hasPermission(commandPermission.getPermission()))
            return;
        PlayerManager.notifyFailure(sender, LanguageManager.errors.permission);
        throw new NoPermissionException(
            sender.getDisplayName() + " does not have the commandPermission " + commandPermission);
    }

    /**
     * Precedence of 1.
     */
    static void checkSenderPermissions(CommandSender sender, Permission commandPermission) throws NoPermissionException {
        if (!(sender instanceof Player) || sender.hasPermission(commandPermission.getPermission()))
            return;
        Player player = (Player) sender;
        PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
        throw new NoPermissionException(
            player.getDisplayName() + " does not have the commandPermission " + commandPermission);
    }

    /**
     * Precedence of 1.
     */
    static Player checkSenderPlayer(CommandSender sender) throws SenderNotPlayerException {
        if (sender instanceof Player)
            return (Player) sender;
        throw new SenderNotPlayerException("Command must be executed by a player");
    }

    /**
     * Precedence of 3.
     */
    static void checkDebugLevelLessEqual(CommandSender sender, CommunicationManager.DebugLevel debugLevel) throws DebugLevelException {
        if (CommunicationManager
            .getDebugLevel()
            .atMost(debugLevel))
            return;
        throw new DebugLevelException("Debug level of " + CommunicationManager.getDebugLevel() + " is higher " +
            "than " + debugLevel);
    }

    /**
     * Precedence of 3.
     */
    static void checkDebugLevelGreaterEqual(CommandSender sender, CommunicationManager.DebugLevel debugLevel) throws DebugLevelException {
        if (CommunicationManager
            .getDebugLevel()
            .atLeast(debugLevel))
            return;
        throw new DebugLevelException("Debug level of " + CommunicationManager.getDebugLevel() + " is lower " +
            "than " + debugLevel);
    }

    /**
     * Precedence of 0.
     */
    static boolean checkArgsLengthGreater(String[] args, int compare) {
        return args.length > compare;
    }

    /**
     * Precedence of 0.
     */
    static boolean checkArgsLengthLess(String[] args, int compare) {
        return args.length < compare;
    }

    /**
     * Precedence of 0.
     *
     * @param args  Command arguments.
     * @param match Number of arguments to match against.
     * @return <CODE>false</CODE> if the number of elements does not match.
     */
    static boolean checkArgsLengthMatch(String[] args, int match) {
        return args.length == match;
    }

    /**
     * Precedence of 0.
     *
     * @param args     Command arguments.
     * @param index    Argument index to check.
     * @param argument Argument to check against.
     * @return <CODE>false</CODE> if there is a discrepancy or the argument doesn't exist.
     */
    static boolean checkArg(String[] args, int index, @NotNull String argument) {
        if (args.length <= index)
            return false;
        return argument.equalsIgnoreCase(args[index]);
    }

    static boolean checkArgStartWith(String[] args, int index, @NotNull String argument) {
        if (args.length <= index)
            return false;
        return args[index]
            .toLowerCase()
            .startsWith(argument.toLowerCase());
    }

    static void checkNotRelease() throws PluginIsReleasedException {
        if (Main.releaseMode)
            throw new PluginIsReleasedException("The command is not supposed to be run when the plugin is " +
                "released!");
    }
}
