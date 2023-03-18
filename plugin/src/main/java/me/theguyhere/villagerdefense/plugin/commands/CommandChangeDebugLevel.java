package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import me.theguyhere.villagerdefense.plugin.managers.PlayerManager;
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
        CommandGuard.checkSenderPermissions(sender, CommandPermission.ADMIN);

        // Check proper level
        int level;
        try {
            level = Integer.parseInt(args[1]);
            if (level < 0 || level > 3)
                throw new CommandFormatException(COMMAND_FORMAT);
        } catch (Exception e) {
            throw new CommandFormatException(COMMAND_FORMAT);
        }

        // Set debug level
        CommunicationManager.setDebugLevel(level);

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
