package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Executes command to make changes to a player's crystal balance.
 */
class CommandModifyCrystalBalance {
	private static final String COMMAND_FORMAT = "/vd " + VDCommandExecutor.Argument.CRYSTALS.getArg() +
		" [player] [change amount]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.CRYSTALS.getArg()))
			return;
		if (!GuardClause.checkArgsLengthMatch(args, 3))
			throw new WrongFormatException(COMMAND_FORMAT);
		GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

		// Check for valid player
		UUID id;
		try {
			id = Arrays.stream(Bukkit.getOfflinePlayers())
				.filter(oPlayer -> Objects.equals(oPlayer.getName(), args[1]))
				.collect(Collectors.toList()).get(0).getUniqueId();
		}
		catch (NullPointerException e) {
			VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.invalidPlayer);
			return;
		}
		if (!Main.getPlayerData().contains(id.toString())) {
			VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.invalidPlayer);
			return;
		}

		// Check for valid amount
		try {
			int amount = Integer.parseInt(args[2]);
			Main.getPlayerData().set(
				id + ".crystalBalance",
				Math.max(Main.getPlayerData().getInt(id + ".crystalBalance") + amount, 0)
			);
			Main.savePlayerData();
			if (sender instanceof Player)
				PlayerManager.notifySuccess(
					(Player) sender,
					LanguageManager.confirms.balanceSet,
					new ColoredMessage(ChatColor.AQUA, args[1]),
					new ColoredMessage(ChatColor.AQUA,
						Integer.toString(Main.getPlayerData().getInt(id + ".crystalBalance")))
				);
			else CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.balanceSet,
				args[1],
				Integer.toString(Main.getPlayerData().getInt(id + ".crystalBalance")));
		}
		catch (Exception e) {
			VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.integer);
		}
	}
}
