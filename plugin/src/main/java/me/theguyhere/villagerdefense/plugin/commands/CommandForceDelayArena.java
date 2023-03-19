package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.*;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to force a delay starting an Arena.
 */
class CommandForceDelayArena {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.DELAY.getArg()))
            return;

        // Delay current arena
        Player player;
        Arena arena;
        if (CommandGuard.checkArgsLengthMatch(args, 1)) {
            player = CommandGuard.checkSenderPlayer(sender);
            CommandGuard.checkSenderPermissions(player, CommandPermission.START);

            // Attempt to get arena
            try {
                arena = GameController.getArena(player);
            } catch (ArenaNotFoundException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
                return;
            }

            // Reschedule countdown
            try {
                arena.restartCountDown();

                // Notify console
                CommunicationManager.debugInfo("%s was delayed.", 1, arena.getName());
            } catch (ArenaClosedException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.close);
            } catch (ArenaStatusException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.arenaInProgress);
            } catch (ArenaException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        }

        // Delay specific arena
        else {
            CommandGuard.checkSenderPermissions(sender, CommandPermission.ADMIN);

            StringBuilder name = new StringBuilder(args[1]);
            for (int i = 0; i < args.length - 2; i++)
                name.append(" ").append(args[i + 2]);

            // Check if this arena exists
            try {
                arena = GameController.getArena(name.toString());
            } catch (ArenaNotFoundException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.noArena);
                return;
            }

            // Reschedule countdown
            try {
                arena.restartCountDown();

                // Notify console
                CommunicationManager.debugInfo("%s was delayed.", 1, arena.getName());
            } catch (ArenaClosedException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.close);
            } catch (ArenaStatusException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.arenaInProgress);
            } catch (ArenaTaskException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.emptyArena);
            }
        }
    }
}
