package me.theguyhere.villagerdefense.game.displays;

import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The scoreboard of an Arena.
 */
public class ArenaBoard {
	/** The information for the ArenaBoard.*/
	private final Hologram hologram;
	/** The location of the ArenaBoard.*/
	private final Location location;

	public ArenaBoard(@NotNull Location location, Arena arena) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Gather relevant stats
		List<String> info = new ArrayList<>();
		info.add(CommunicationManager.format("&6&l" + arena.getName() + " Records"));
		if (!arena.getSortedDescendingRecords().isEmpty())
			arena.getSortedDescendingRecords().forEach(record -> {
				StringBuilder line = new StringBuilder("Wave &b" + record.getWave() + " &f- ");
				for (int i = 0; i < record.getPlayers().size() / 4 + 1; i++) {
					if (i * 4 + 4 < record.getPlayers().size()) {
						for (int j = i * 4; j < i * 4 + 4; j++)
							line.append(record.getPlayers().get(j)).append(", ");
						info.add(CommunicationManager.format(line.substring(0, line.length() - 1)));
					} else {
						for (int j = i * 4; j < record.getPlayers().size(); j++)
							line.append(record.getPlayers().get(j)).append(", ");
						info.add(CommunicationManager.format(line.substring(0, line.length() - 2)));
					}
					line = new StringBuilder();
				}
			});

		// Set location and hologram
		this.location = location;
		this.hologram = new Hologram(location.clone().add(0, 2.5, 0), false,
				info.toArray(new String[]{}));
	}

	public Location getLocation() {
		return location;
	}

	public Hologram getHologram() {
		return hologram;
	}

	/**
	 * Spawn in the ArenaBoard for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
	}

	/**
	 * Spawn in the ArenaBoard for a specific player.
	 * @param player - The player to display the ArenaBoard for.
	 */
	public void displayForPlayer(Player player) {
		hologram.displayForPlayer(player);
	}

	/**
	 * Stop displaying the ArenaBoard for every online player.
	 */
	public void remove() {
		hologram.remove();
	}
}
