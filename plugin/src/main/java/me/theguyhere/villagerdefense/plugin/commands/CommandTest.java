package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to test stuff.
 */
class CommandTest {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.TEST.getArg()))
			return;
		Player player = GuardClause.checkSenderPlayer(sender);
		GuardClause.checkNotRelease();
		GuardClause.checkSenderPermissions(player, Permission.ADMIN);
		GuardClause.checkDebugLevelGreaterEqual(sender, CommunicationManager.DebugLevel.DEVELOPER);

		// Implement test

		// Confirm
		PlayerManager.notifySuccess(player, "Test Complete");
	}
}
