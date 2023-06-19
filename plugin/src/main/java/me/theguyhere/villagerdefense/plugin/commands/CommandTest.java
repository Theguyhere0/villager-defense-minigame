package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
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
		CommandGuard.checkSenderPermissions(player, CommandPermission.ADMIN);
		CommandGuard.checkDebugLevelGreaterEqual(sender, CommunicationManager.DebugLevel.DEVELOPER);

		// Implement test

		// Confirm
		PlayerManager.notifySuccess(player, "Test Complete");
	}
}
