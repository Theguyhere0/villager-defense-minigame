package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to force an Arena to end.
 */
class CommandForceEndArena {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.END.getArg()))
			return;
		GuardClause.checkSenderPermissions(sender, Permission.ADMIN);

		// End current arena
		Player player;
		Arena arena;
		if (GuardClause.checkArgsLengthMatch(args, 1)) {
			player = GuardClause.checkSenderPlayer(sender);

			// Attempt to get arena
			try {
				arena = GameManager.getArena(player);
			}
			catch (ArenaNotFoundException e) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
				return;
			}

			// Check if arena has a game in progress
			if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.noGameEnd);
				return;
			}

			// Check if game is about to end
			if (arena.getStatus() == ArenaStatus.ENDING) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.endingSoon);
				return;
			}
        }

		// End specific arena
		else {
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

			// Check if arena has a game in progress
			if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
				VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.noGameEnd);
				return;
			}

			// Check if game is about to end
			if (arena.getStatus() == ArenaStatus.ENDING) {
				VDCommandExecutor.notifyFailure(sender, LanguageManager.errors.endingSoon);
				return;
			}
        }

		// Force end
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
            Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

		// Notify console
        CommunicationManager.debugInfo(CommunicationManager.DebugLevel.NORMAL, "%s was force ended.",
			arena.getName()
        );
    }
}
