package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to test stuff.
 */
class CommandTest {
    static void execute(String[] args, CommandSender sender) throws CommandException {
        // Guard clauses
        if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.TEST.getArg()))
            return;
        Player player = CommandGuard.checkSenderPlayer(sender);
        CommandGuard.checkNotRelease();
        CommandGuard.checkSenderPermissions(player, Permission.ADMIN);
        CommandGuard.checkDebugLevelGreaterEqual(sender, 3);

        // Implement test
        Arena arena;
        VDPlayer gamer;
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
            PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
            return;
        }
        gamer.addGems(999999);

        // Confirm
        PlayerManager.notifySuccess(player, "Test Complete");
    }
}
