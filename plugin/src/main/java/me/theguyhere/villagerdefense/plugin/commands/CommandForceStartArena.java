package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.*;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to force an Arena to start.
 */
class CommandForceStartArena {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.START.getArg()))
            return;

        // Start current arena
        Player player;
        Arena arena;
        VDPlayer gamer;
        if (CommandGuard.checkArgsLengthMatch(args, 1)) {
            player = CommandGuard.checkSenderPlayer(sender);
            CommandGuard.checkSenderPermissions(player, CommandPermission.START);

            // Attempt to get arena and player
            try {
                arena = GameController.getArena(player);
                gamer = arena.getPlayer(player);
            } catch (ArenaNotFoundException | PlayerNotFoundException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
                return;
            }

            // Check if player is an active player
            if (!arena.getActives().contains(gamer)) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.activePlayer);
                return;
            }

            // Bring game to quick start if not already
            try {
                arena.expediteCountDown();

                // Notify console
                CommunicationManager.debugInfo("%s was force started.", 1, arena.getName());
            } catch (ArenaClosedException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.close);
            } catch (ArenaStatusException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.arenaInProgress);
            } catch (ArenaTaskException e) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.startingSoon);
            }
        }

        // Start specific arena
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

            // Bring game to quick start if not already
            try {
                arena.expediteCountDown();

                // Notify console
                CommunicationManager.debugInfo("%s was force started.", 1, arena.getName());
            } catch (ArenaClosedException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.close);
            } catch (ArenaStatusException e) {
                CommandExecImp.notifyFailure(sender, LanguageManager.errors.arenaInProgress);
            } catch (ArenaTaskException e) {
                if (e.getMessage().equals("Arena cannot start countdown without players"))
                    CommandExecImp.notifyFailure(sender, LanguageManager.errors.arenaNoPlayers);
                else CommandExecImp.notifyFailure(sender, LanguageManager.errors.startingSoon);
            }
        }
    }
}
