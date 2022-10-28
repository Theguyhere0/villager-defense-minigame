package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandFormatException;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to open up the kits menu for a player.
 */
class CommandCheckKits {
    private static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.KITS.getArg();

    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.KITS.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        if (!CommandGuard.checkArgsLengthMatch(args, 1))
            throw new CommandFormatException(COMMAND_FORMAT);

        // Open kits menu
        player.openInventory(Inventories.createPlayerKitsMenu(player.getUniqueId(), player.getUniqueId()));
    }
}
