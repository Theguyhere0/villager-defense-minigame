package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandFormatException;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to open up the achievements menu for a player.
 */
class CommandCheckAchievements {
    private static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.ACHIEVEMENTS.getArg();

    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.ACHIEVEMENTS.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        if (!CommandGuard.checkArgsLengthMatch(args, 1))
            throw new CommandFormatException(COMMAND_FORMAT);

        // Open achievements menu
        player.openInventory(Inventories.createPlayerAchievementsMenu(player.getUniqueId()));
    }
}
