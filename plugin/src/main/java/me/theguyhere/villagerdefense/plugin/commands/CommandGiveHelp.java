package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.WrongFormatException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to provide help to the player through chat or redirect to wiki.
 */
class CommandGiveHelp {
	static final String COMMAND_FORMAT = "/vd " + VDCommandExecutor.Argument.HELP.getArg() + " [optional: 1-3]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.HELP.getArg()))
			return;
		if (GuardClause.checkArgsLengthGreater(args, 2))
			throw new WrongFormatException(COMMAND_FORMAT);

		// Send wiki link if sender is not player
		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.QUIET, String.format(
					"%s: https://github.com/Theguyhere0/villager-defense-minigame/wiki",
					LanguageManager.messages.visitWiki
				));
			return;
		}

		// Try to get page number, or set page to 1
		int page;
		try {
			page = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			page = 1;
		}

		switch (page) {
			case 2:
				player.sendMessage(CommunicationManager.format("&a<----- " +
					String.format(LanguageManager.messages.help, "Villager Defense") +
					" (2/3) ----->"));
				player.sendMessage(new ColoredMessage(ChatColor.BOLD, LanguageManager.messages.help2).toString());
				player.sendMessage("");
				player.sendMessage(new ColoredMessage(ChatColor.GOLD, LanguageManager.messages.help2a).toString());
				break;
			case 3:
				player.sendMessage(CommunicationManager.format("&a<----- " +
					String.format(LanguageManager.messages.help, "Villager Defense") +
					" (3/3) ----->"));
				player.sendMessage(new ColoredMessage(ChatColor.BOLD, LanguageManager.messages.help3).toString());
				player.sendMessage("");
				player.sendMessage(new ColoredMessage(ChatColor.GOLD, LanguageManager.messages.infoAboutWiki)
					.toString());
				TextComponent message = new TextComponent(" " + LanguageManager.messages.visitWiki + "!");
				message.setBold(true);
				message.setClickEvent(new ClickEvent(
					ClickEvent.Action.OPEN_URL,
					"https://github.com/Theguyhere0/villager-defense-minigame/wiki"
				));
				player
					.spigot()
					.sendMessage(message);
				break;
			default:
				player.sendMessage(CommunicationManager.format("&a<----- " +
					String.format(LanguageManager.messages.help, "Villager Defense") +
					" (1/3) ----->"));
				player.sendMessage(CommunicationManager.format("&6 " +
					String.format(LanguageManager.messages.help1, LanguageManager.names.crystals)));
		}
	}
}
