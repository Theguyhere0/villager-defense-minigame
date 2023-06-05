package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Objects;

/**
 * Executes command to kill a player in a game.
 */
class CommandKillPlayer {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.DIE.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);

        // Check for player in a game
        if (!GameController.checkPlayer(player)) {
            PlayerManager.notifyFailure(player, LanguageManager.errors.notInGame);
            return;
        }

        // Check for player in an active game
        if (GameController.getArenas().values().stream().filter(Objects::nonNull)
                .filter(arena1 -> arena1.getStatus() == ArenaStatus.ACTIVE)
                .noneMatch(arena1 -> arena1.hasPlayer(player))) {
            PlayerManager.notifyFailure(player, LanguageManager.errors.suicideActive);
            return;
        }

        // Check for alive player
        try {
            if (GameController.getArena(player).getPlayer(player).getStatus() != VDPlayer.Status.ALIVE) {
                PlayerManager.notifyFailure(player, LanguageManager.errors.suicide);
                return;
            }
        } catch (PlayerNotFoundException err) {
            PlayerManager.notifyFailure(player, LanguageManager.errors.suicide);
            return;
        } catch (ArenaNotFoundException err) {
            return;
        }

        // Create a player death and make sure it gets detected
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                Bukkit.getPluginManager().callEvent(new EntityDamageEvent(player,
                        EntityDamageEvent.DamageCause.SUICIDE, 99)));
    }
}
