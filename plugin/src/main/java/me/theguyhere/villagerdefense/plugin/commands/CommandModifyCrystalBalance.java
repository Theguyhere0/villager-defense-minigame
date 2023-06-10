package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
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
	private static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.CRYSTALS.getArg() +
		" [player] [change amount]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.CRYSTALS.getArg()))
			return;
		if (!CommandGuard.checkArgsLengthMatch(args, 3))
			throw new CommandFormatException(COMMAND_FORMAT);
		CommandGuard.checkSenderPermissions(sender, CommandPermission.ADMIN);

		// Check if vault economy is enabled
		if (Main.hasCustomEconomy()) {
			CommandExecImp.notifyFailure(sender, LanguageManager.errors.economy);
			return;
		}

		// Check for valid player
		UUID id;
		try {
			id = Arrays.stream(Bukkit.getOfflinePlayers())
				.filter(oPlayer -> Objects.equals(oPlayer.getName(), args[1]))
				.collect(Collectors.toList()).get(0).getUniqueId();
		}
		catch (NullPointerException e) {
			CommandExecImp.notifyFailure(sender, LanguageManager.errors.invalidPlayer);
			return;
		}
		if (!PlayerManager.hasPlayer(id)) {
			CommandExecImp.notifyFailure(sender, LanguageManager.errors.invalidPlayer);
			return;
		}

		// Check for valid amount
		try {
			int amount = Integer.parseInt(args[2]);
			if (amount < 0)
				PlayerManager.withdrawCrystalBalance(id, -amount);
			else PlayerManager.depositCrystalBalance(id, amount);
			if (sender instanceof Player)
				PlayerManager.notifySuccess(
					(Player) sender,
					LanguageManager.confirms.balanceSet,
					new ColoredMessage(ChatColor.AQUA, args[1]),
					new ColoredMessage(
						ChatColor.AQUA,
						Integer.toString(PlayerManager.getCrystalBalance(id))
					)
				);
			else
				CommunicationManager.debugInfo(LanguageManager.confirms.balanceSet,
					CommunicationManager.DebugLevel.QUIET, args[1],
					Integer.toString(PlayerManager.getCrystalBalance(id))
				);
		}
		catch (Exception e) {
			CommandExecImp.notifyFailure(sender, LanguageManager.errors.integer);
		}
	}
}
