package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Executes command to change the debug level of the plugin instance.
 */
class CommandChangeDebugLevel {
	static final String COMMAND_FORMAT =
		"/vd " + VDCommandExecutor.Argument.DEBUG.getArg() + " " +
			Arrays.toString(Arrays
				.stream(CommunicationManager.DebugLevel.values())
				.map(debugLevel -> debugLevel
					.name()
					.toLowerCase())
				.toArray());

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.DEBUG.getArg()))
			return;
		if (!GuardClause.checkArgsLengthMatch(args, 2))
			throw new WrongFormatException(COMMAND_FORMAT);
		GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

		// Try to set new debug level
		try {
			CommunicationManager.setDebugLevel(CommunicationManager.DebugLevel.valueOf(args[1].toUpperCase()));
		}
		catch (IllegalArgumentException e) {
			throw new WrongFormatException(COMMAND_FORMAT);
		}

		// Notify
		if (sender instanceof Player)
			PlayerManager.notifySuccess(
				(Player) sender,
				LanguageManager.messages.debugLevelSet,
				new ColoredMessage(ChatColor.AQUA, args[1])
			);
		else
			CommunicationManager.debugInfo(LanguageManager.messages.debugLevelSet,
				CommunicationManager.DebugLevel.QUIET, args[1]
			);
	}
}
