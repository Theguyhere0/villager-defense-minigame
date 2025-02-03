package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to open the admin panel.
 */
class CommandOpenAdminPanel {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.ADMIN.getArg()) ||
			!GuardClause.checkArgsLengthMatch(args, 1))
			return;
		Player player = GuardClause.checkSenderPlayer(sender);
		GuardClause.checkSenderPermissions(player, Permission.USE);

		// Open admin panel
		player.openInventory(Inventories.createMainMenu());
	}
}
