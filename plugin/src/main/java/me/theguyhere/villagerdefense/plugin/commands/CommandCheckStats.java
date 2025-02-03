package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Executes command to check player statistics.
 */
class CommandCheckStats {
	private static final String COMMAND_FORMAT = "/vd " + VDCommandExecutor.Argument.STATS.getArg() +
		" [optional: player name]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.STATS.getArg()))
			return;
		Player player = GuardClause.checkSenderPlayer(sender);
		if (GuardClause.checkArgsLengthGreater(args, 2))
			throw new WrongFormatException(COMMAND_FORMAT);

		// Open stats display
		if (GuardClause.checkArgsLengthMatch(args, 1)) {
			player.openInventory(Inventories.createPlayerStatsMenu(player));
		}
		else if (Main.getPlayerData().contains(args[1])) {
			player.openInventory(Inventories.createPlayerStatsMenu(
				Objects.requireNonNull(Bukkit.getPlayer(args[1]))));
		}
		else {
			PlayerManager.notifyFailure(player, LanguageManager.messages.noStats,
				new ColoredMessage(ChatColor.AQUA, args[1])
			);
		}
	}
}
