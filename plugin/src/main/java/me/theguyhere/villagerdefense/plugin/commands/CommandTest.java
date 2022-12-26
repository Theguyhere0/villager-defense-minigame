package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to test stuff.
 */
class CommandTest {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.TEST.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        CommandGuard.checkNotRelease();
        CommandGuard.checkSenderPermissions(player, Permission.ADMIN);
        CommandGuard.checkDebugLevelGreaterEqual(sender, 3);

        // Implement test

        // Confirm
        PlayerManager.notifySuccess(player, "Test Complete");
    }
}
