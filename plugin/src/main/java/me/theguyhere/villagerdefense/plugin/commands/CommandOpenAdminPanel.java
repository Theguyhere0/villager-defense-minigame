package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to open the admin panel.
 */
class CommandOpenAdminPanel {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.ADMIN.getArg()) ||
                !CommandGuard.checkArgsLengthMatch(args, 1))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        CommandGuard.checkSenderPermissions(player, CommandPermission.USE);

        // Open admin panel
        player.openInventory(Inventories.createMainMenu());
    }
}
