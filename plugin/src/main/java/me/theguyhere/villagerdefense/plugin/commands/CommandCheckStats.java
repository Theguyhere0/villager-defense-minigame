package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.guis.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to check player statistics.
 */
@SuppressWarnings("deprecation")
class CommandCheckStats {
	private static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.STATS.getArg() +
		" [optional: player name]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.STATS.getArg()))
			return;
		Player player = CommandGuard.checkSenderPlayer(sender);
		if (CommandGuard.checkArgsLengthGreater(args, 2))
			throw new CommandFormatException(COMMAND_FORMAT);

		// Open stats display
		if (CommandGuard.checkArgsLengthMatch(args, 1))
			player.openInventory(Inventories.createPlayerStatsMenu(
				player.getUniqueId(),
				player.getUniqueId()
			));
		else if (PlayerManager.hasPlayer(Bukkit
			.getOfflinePlayer(args[1])
			.getUniqueId()))
			player.openInventory(Inventories.createPlayerStatsMenu(
				Bukkit
					.getOfflinePlayer(args[1])
					.getUniqueId(), player.getUniqueId()));
		else PlayerManager.notifyFailure(player, LanguageManager.messages.noStats,
				new ColoredMessage(ChatColor.AQUA, args[1])
			);
	}
}
