package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.LeaveArenaEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to attempt to let player leave an arena.
 */
class CommandLeaveArena {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.LEAVE.getArg()))
			return;
		Player player = CommandGuard.checkSenderPlayer(sender);

		// Schedule attempt to leave for the player
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, () ->
				Bukkit
					.getPluginManager()
					.callEvent(new LeaveArenaEvent(player)));
	}
}
