package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to provide help to the player through chat or redirect to wiki.
 */
class CommandGiveHelp {
	static final String COMMAND_FORMAT = "/vd " + CommandExecImp.Argument.HELP.getArg() + " [optional: 1-3]";

	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.HELP.getArg()))
			return;
		if (CommandGuard.checkArgsLengthGreater(args, 2))
			throw new CommandFormatException(COMMAND_FORMAT);
		Player player;

		// Send wiki link if sender is not player
		try {
			player = CommandGuard.checkSenderPlayer(sender);
		}
		catch (CommandPlayerException e) {
			CommunicationManager.debugInfo(
				String.format(
					"%s: https://github.com/Theguyhere0/villager-defense-minigame/wiki",
					LanguageManager.messages.visitWiki
				), CommunicationManager.DebugLevel.QUIET);
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
