package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.game.*;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

/**
 * Executes command to force an Arena to start.
 */
class CommandForceStartArena {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.START.getArg()))
			return;

		// Start current arena
		Player player;
		Arena arena;
		VDPlayer gamer;
		if (GuardClause.checkArgsLengthMatch(args, 1)) {
			player = GuardClause.checkSenderPlayer(sender);
			GuardClause.checkSenderPermissions(player, Permission.START);

			// Attempt to get arena and player
			try {
				arena = GameManager.getArena(player);
				gamer = arena.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException e) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
				return;
			}

			// Check if player is an active player
			if (!arena
				.getActives()
				.contains(gamer)) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.activePlayer);
				return;
			}

			// Check if arena already started
			if (arena.getStatus() != ArenaStatus.WAITING) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.arenaInProgress);
				return;
			}

			Tasks task = arena.getTask();
			Map<Runnable, Integer> tasks = task.getTasks();
			BukkitScheduler scheduler = Bukkit.getScheduler();

			// Bring game to quick start if not already
			if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
				!scheduler.isQueued(tasks.get(task.sec10))) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.startingSoon);
			} else {
				// Remove all tasks
				tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
				tasks.clear();

				// Schedule accelerated countdown tasks
				task.sec10.run();
				tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
				tasks.put(task.sec5,
					scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5, Calculator.secondsToTicks(5)));
				tasks.put(task.start,
					scheduler.scheduleSyncDelayedTask(Main.plugin, task.start, Calculator.secondsToTicks(10)));
			}
		}

		// Start specific arena
		else {
			GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

			StringBuilder name = new StringBuilder(args[1]);
			for (int i = 0; i < args.length - 2; i++)
				name
					.append(" ")
					.append(args[i + 2]);

			// Check if this arena exists
			try {
				arena = GameManager.getArena(name.toString());
			}
			catch (ArenaNotFoundException e) {
				VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.noArena);
				return;
			}

			// Check if arena already started
			if (arena.getStatus() != ArenaStatus.WAITING) {
				VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.arenaInProgress);
				return;
			}

			// Check if there is at least 1 player
			if (arena.getActiveCount() == 0) {
				VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.arenaNoPlayers);
				return;
			}

			Tasks task = arena.getTask();
			Map<Runnable, Integer> tasks = task.getTasks();
			BukkitScheduler scheduler = Bukkit.getScheduler();

			// Bring game to quick start if not already
			if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
				!scheduler.isQueued(tasks.get(task.sec10))) {
				if (sender instanceof Player)
					PlayerManager.notifyFailure((Player) sender,
						LanguageManager.errors.startingSoon);
				else CommunicationManager.debugError(LanguageManager.errors.startingSoon,
					CommunicationManager.DebugLevel.QUIET);
			} else {
				// Remove all tasks
				tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
				tasks.clear();

				// Schedule accelerated countdown tasks
				task.sec10.run();
				tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
				tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
					Calculator.secondsToTicks(5)));
				tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
					Calculator.secondsToTicks(10)));

				// Notify console
				CommunicationManager.debugInfo(arena.getName() + " was force started.", CommunicationManager.DebugLevel.NORMAL);
			}
		}
	}
}
