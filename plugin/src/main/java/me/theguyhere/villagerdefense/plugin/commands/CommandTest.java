package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
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
		CommandGuard.checkSenderPermissions(player, CommandPermission.ADMIN);
		CommandGuard.checkDebugLevelGreaterEqual(sender, CommunicationManager.DebugLevel.DEVELOPER);

		// Implement test
		Arena arena;
		VDPlayer gamer;
		try {
			arena = GameController.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
			return;
		}
		gamer.addGems(999999);

		// Confirm
		PlayerManager.notifySuccess(player, "Test Complete");
	}
}
