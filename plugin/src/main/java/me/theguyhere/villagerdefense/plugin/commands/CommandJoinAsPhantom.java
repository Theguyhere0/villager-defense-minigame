package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.data.PlayerDataManager;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to let player join an arenas a phantom.
 */
class CommandJoinAsPhantom {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.JOIN.getArg()))
			return;
		Player player = GuardClause.checkSenderPlayer(sender);

		// Attempt to get arena and player
		Arena arena;
		VDPlayer gamer;
		try {
			arena = GameManager.getArena(player);
			gamer = arena.getPlayer(player);
		}
		catch (ArenaNotFoundException | PlayerNotFoundException err) {
			PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
			return;
		}

		// Check if player owns the phantom kit if late arrival is not on
		if (!PlayerDataManager.playerOwnsKit(player.getUniqueId(), Kit.phantom()) &&
			!arena.hasLateArrival()) {
			PlayerManager.notifyFailure(player, LanguageManager.errors.phantomOwn);
			return;
		}

		// Check if arena is not ending
		if (arena.getStatus() == ArenaStatus.ENDING) {
			PlayerManager.notifyFailure(player, LanguageManager.errors.phantomArena);
			return;
		}

		// Check for useful phantom use
		if (gamer.getStatus() != VDPlayer.Status.SPECTATOR) {
			PlayerManager.notifyFailure(player, LanguageManager.errors.phantomPlayer);
			return;
		}

		// Check for arena capacity if late arrival is on
		if (arena.hasLateArrival() && arena.getActiveCount() >= arena.getMaxPlayers()) {
			PlayerManager.notifyAlert(player, LanguageManager.messages.maxCapacity);
			return;
		}

		// Let player join using phantom kit
		PlayerManager.teleportIntoAdventure(player, arena.getPlayerSpawn().getLocation());
		gamer.setStatus(VDPlayer.Status.ALIVE);
		arena.getTask().giveItems(gamer);
		GameManager.createBoard(gamer);
		gamer.setJoinedWave(arena.getCurrentWave());
		gamer.setKit(Kit.phantom());
		player.closeInventory();
	}
}
