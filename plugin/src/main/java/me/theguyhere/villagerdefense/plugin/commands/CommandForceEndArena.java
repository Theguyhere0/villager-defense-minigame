package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaStatusException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to force an Arena to end.
 */
class CommandForceEndArena {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.END.getArg()))
            return;
        CommandGuard.checkSenderPermissions(sender, Permission.ADMIN);

        // End current arena
        Player player;
        Arena arena;
        if (CommandGuard.checkArgsLengthMatch(args, 1)) {
            player = CommandGuard.checkSenderPlayer(sender);

            // Attempt to get arena
            try {
                arena = GameManager.getArena(player);
            } catch (ArenaNotFoundException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
                return;
            }

            // Force end
            try {
                arena.endGame();
            } catch (ArenaStatusException e) {
                if (arena.getStatus() == ArenaStatus.ENDING) {
                    PlayerManager.notifyFailure(player, LanguageManager.errors.endingSoon);
                    return;
                }
                PlayerManager.notifyFailure(player, LanguageManager.errors.noGameEnd);
                return;
            } catch (ArenaException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.close);
                return;
            }

            // Notify console
            CommunicationManager.debugInfo("%s was force ended.", 1, arena.getName());
        }

        // End specific arena
        else {
            StringBuilder name = new StringBuilder(args[1]);
            for (int i = 0; i < args.length - 2; i++)
                name.append(" ").append(args[i + 2]);

            // Check if this arena exists
            try {
                arena = GameManager.getArena(name.toString());
            } catch (ArenaNotFoundException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.noArena);
                return;
            }

            // Force end
            try {
                arena.endGame();
            } catch (ArenaStatusException e) {
                if (arena.getStatus() == ArenaStatus.ENDING) {
                    CommandExecImp.notifyFailure(sender, LanguageManager.errors.endingSoon);
                    return;
                }
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.noGameEnd);
                return;
            } catch (ArenaException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.close);
                return;
            }

            // Notify console
            CommunicationManager.debugInfo("%s was force ended.", 1, arena.getName());
        }
    }
}
