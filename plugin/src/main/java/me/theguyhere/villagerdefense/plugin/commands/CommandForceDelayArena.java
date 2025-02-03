package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.Tasks;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;

/**
 * Executes command to force a delay starting an Arena.
 */
class CommandForceDelayArena {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.DELAY.getArg()))
			return;

		// Delay current arena
		Player player;
		Arena arena;
		if (GuardClause.checkArgsLengthMatch(args, 1)) {
			player = GuardClause.checkSenderPlayer(sender);
			GuardClause.checkSenderPermissions(player, Permission.START);

			// Attempt to get arena
			try {
				arena = GameManager.getArena(player);
			}
			catch (ArenaNotFoundException e) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
				return;
			}

			Tasks task = arena.getTask();
			Map<Runnable, Integer> tasks = task.getTasks();
			BukkitScheduler scheduler = Bukkit.getScheduler();

			// Remove all tasks
			tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
			tasks.clear();

			// Reschedule countdown tasks
			task.min2.run();
			tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(Main.plugin, task.min1,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(1))));
			tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec30,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 30)));
			tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec10,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 10)));
			tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 5)));
			tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2))));
		}

		// Delay specific arena
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

			Tasks task = arena.getTask();
			Map<Runnable, Integer> tasks = task.getTasks();
			BukkitScheduler scheduler = Bukkit.getScheduler();

			// Remove all tasks
			tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
			tasks.clear();

			// Reschedule countdown tasks
			task.min2.run();
			tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(Main.plugin, task.min1,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(1))));
			tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec30,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 30)));
			tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec10,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 10)));
			tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 5)));
			tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
				Calculator.secondsToTicks(Calculator.minutesToSeconds(2))));
		}
	}
}
