package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.entities.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.entities.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Executes command to let player join an arenas a phantom.
 */
class CommandJoinAsPhantom {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!CommandGuard.checkArg(args, 0, CommandExecImp.Argument.JOIN.getArg()))
			return;
		Player player = CommandGuard.checkSenderPlayer(sender);

		// Attempt to get arena and player
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

		// Check if player owns the phantom kit if late arrival is not on
		if (!PlayerManager.hasSingleTierKit(player.getUniqueId(), Kit
			.phantom()
			.getID()) &&
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
		PlayerManager.teleAdventure(player, arena
			.getPlayerSpawn()
			.getLocation());
		gamer.setStatus(VDPlayer.Status.ALIVE);
		gamer.giveItems();
		SidebarManager.updateActivePlayerSidebar(gamer);
		gamer.setJoinedWave(arena.getCurrentWave());
		gamer.setKit(Kit.phantom());
		player.closeInventory();
	}
}
