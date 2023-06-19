package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The portal to an Arena.
 */
public class Portal {
	/**
	 * The NPC for the Portal.
	 */
	private final NPCVillager npc;
	/**
	 * The information for the Portal.
	 */
	private final Hologram hologram;
	/**
	 * The location of the Portal.
	 */
	private final Location location;

	public Portal(@NotNull Location location, Arena arena) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Get game mode
		String gameMode = arena.getGameMode();
		if (gameMode != null)
			switch (gameMode) {
				case "Legacy":
					gameMode = "&7&l[" + LanguageManager.names.legacy + "]";
					break;
				case "Campaign":
					gameMode = "&b&l[" + LanguageManager.names.campaign + "]";
					break;
				default:
					gameMode = "&3&l[" + LanguageManager.names.freeplay + "]";
			}
		else gameMode = " &3&l[" + LanguageManager.names.freeplay + "]";

		// Get difficulty
		String difficulty = arena.getDifficultyLabel();
		if (difficulty != null)
			switch (difficulty) {
				case "Easy":
					difficulty = " &a&l[" + LanguageManager.names.easy + "]";
					break;
				case "Medium":
					difficulty = " &e&l[" + LanguageManager.names.medium + "]";
					break;
				case "Hard":
					difficulty = " &c&l[" + LanguageManager.names.hard + "]";
					break;
				case "Insane":
					difficulty = " &d&l[" + LanguageManager.names.insane + "]";
					break;
				default:
					difficulty = "";
			}
		else difficulty = "";

		// Get status
		String status;
		if (arena.isClosed())
			status = " &c&l[" + LanguageManager.messages.closed + "]";
		else if (arena.getStatus() == ArenaStatus.ENDING)
			status = " &e&l[" + LanguageManager.messages.ending + "]";
		else if (arena.getStatus() == ArenaStatus.WAITING)
			status = " &b&l[" + LanguageManager.messages.waiting + "]";
		else status = " &a&l[" + LanguageManager.messages.wave + ": " + arena.getCurrentWave() + "]";

		// Get player count color
		String countColor;
		double fillRatio = arena.getActiveCount() / (double) arena.getMaxPlayers();
		if (fillRatio < .8)
			countColor = "&a";
		else if (fillRatio < 1)
			countColor = "&6";
		else countColor = "&c";

		// Set location, hologram, and npc
		this.location = location;
		this.npc = new NPCVillager(location, arena.getVillagerType());
		this.hologram = new Hologram(location
			.clone()
			.add(0, 2.5, 0), false,
			CommunicationManager.format("&6&l" + arena.getName() + status),
			CommunicationManager.format(gameMode + difficulty),
			arena.isClosed() ? "" : CommunicationManager.format("&b" + LanguageManager.messages.players +
				": " + countColor + arena.getActiveCount() + "&b / " + arena.getMaxPlayers()),
			arena.isClosed() ? "" : CommunicationManager.format(LanguageManager.messages.spectators + ": " +
				arena.getSpectatorCount())
		);
	}

	public Location getLocation() {
		return location;
	}

	public NPCVillager getNpc() {
		return npc;
	}

	/**
	 * Spawn in the Portal for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
		npc.displayForOnline();
	}

	/**
	 * Spawn in the Portal for a specific player.
	 *
	 * @param player - The player to display the Portal for.
	 */
	public void displayForPlayer(Player player) {
		hologram.displayForPlayer(player);
		npc.displayForPlayer(player);
	}

	/**
	 * Stop displaying the Portal for every online player.
	 */
	public void remove() {
		hologram.remove();
		npc.remove();
	}
}
