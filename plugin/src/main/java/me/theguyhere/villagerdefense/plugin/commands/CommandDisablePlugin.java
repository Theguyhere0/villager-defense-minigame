package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.SenderNotPlayerException;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Executes command to disable the plugin.
 */
class CommandDisablePlugin {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.DISABLE.getArg()))
			return;
		GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

		// Try to get a UUID
		Player player;
		UUID uuid;
		try {
			player = GuardClause.checkSenderPlayer(sender);
			uuid = player.getUniqueId();
		}
		catch (SenderNotPlayerException e) {
			player = null;
			uuid = null;
		}

		// Safeguard
		if (!VDCommandExecutor.disable.containsKey(uuid) ||
			VDCommandExecutor.disable.get(uuid) < System.currentTimeMillis()) {
			// Notify of safeguard measures
			if (player != null)
				PlayerManager.notifyAlert(player, "Are you sure you want to disable the plugin? " +
					"Re-send the command within 10 seconds to confirm.");
			else CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, "Are you sure you want to disable the plugin? " +
				"Re-send the command within 10 seconds to confirm.");

			// Keep track of trigger
			VDCommandExecutor.disable.put(uuid, System.currentTimeMillis() + Calculator.secondsToMillis(10));

			return;
		}

		// Notify of disable
		if (player != null)
			PlayerManager.notifyAlert(player, "Disabling the plugin");
		else CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, "Disabling the plugin");

		Bukkit
			.getPluginManager()
			.disablePlugin(Main.plugin);
	}
}
