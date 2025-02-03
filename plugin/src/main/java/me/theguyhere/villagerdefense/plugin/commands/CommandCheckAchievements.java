package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to open up the achievements menu for a player.
 */
class CommandCheckAchievements {
	private static final String COMMAND_FORMAT = "/vd " + VDCommandExecutor.Argument.ACHIEVEMENTS.getArg();

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.ACHIEVEMENTS.getArg()))
			return;
		Player player = GuardClause.checkSenderPlayer(sender);
		if (!GuardClause.checkArgsLengthMatch(args, 1))
			throw new WrongFormatException(COMMAND_FORMAT);

		// Open achievements menu
		player.openInventory(Inventories.createPlayerAchievementsMenu(player));
	}
}
