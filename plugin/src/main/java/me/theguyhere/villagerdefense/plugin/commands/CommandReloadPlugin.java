package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.SenderNotPlayerException;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Executes command to reload plugin data.
 */
class CommandReloadPlugin {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.RELOAD.getArg()))
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
		if (!VDCommandExecutor.reload.containsKey(uuid) || VDCommandExecutor.reload.get(uuid) < System.currentTimeMillis()) {
			// Notify of safeguard measures
			if (player != null)
				PlayerManager.notifyAlert(player, "Are you sure you want to reload the plugin? " +
					"Re-send the command within 10 seconds to confirm.");
			else CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, "Are you sure you want to reload the plugin? " +
				"Re-send the command within 10 seconds to confirm.");

			// Keep track of trigger
			VDCommandExecutor.reload.put(uuid, System.currentTimeMillis() + Calculator.secondsToMillis(10));

			return;
		}

		// Notify of reload
		if (player != null)
			PlayerManager.notifyAlert(player, "Reloading plugin data in 5 seconds");
		else
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.QUIET, "Reloading plugin data in 5 seconds"
			);

		// Close all arenas
		GameManager.getArenas().forEach(((integer, arena) -> arena.setClosed(true)));

		// Reload plugin after 5 seconds
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, () -> Main.plugin.reload(),
				Calculator.secondsToTicks(5)
			);
	}
}
