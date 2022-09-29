package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandFormatException;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to change the debug level of the plugin instance.
 */
class CommandChangeDebugLevel {
    static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.DEBUG.getArg() + " [debug level (0-3)]";

    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.DEBUG.getArg()))
            return;
        if (!CommandGuard.checkArgsLengthMatch(args, 2))
            throw new CommandFormatException(COMMAND_FORMAT);
        CommandGuard.checkSenderPermissions(sender, Permission.ADMIN);

        // Set debug level
        try {
            CommunicationManager.setDebugLevel(Integer.parseInt(args[1]));
        } catch (Exception e) {
            throw new CommandFormatException(COMMAND_FORMAT);
        }

        // Notify
        if (sender instanceof Player)
            PlayerManager.notifySuccess(
                    (Player) sender,
                    LanguageManager.messages.debugLevelSet,
                    new ColoredMessage(ChatColor.AQUA, args[1])
            );
        else CommunicationManager.debugInfo(LanguageManager.messages.debugLevelSet, 0, args[1]);
    }
}
