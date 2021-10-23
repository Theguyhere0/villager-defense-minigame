package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.tools.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A class managing data about a Villager Defense arena's scoreboard.
 */
public class ArenaBoard {
	private Hologram hologram;

	public ArenaBoard(Hologram hologram) {
		this.hologram = hologram;
	}

	public Hologram getHologram() {
		return hologram;
	}

	public void setHologram(Hologram hologram) {
		if (this.hologram != null)
			this.hologram.delete();
		this.hologram = hologram;
	}

	public static void refreshArenaBoards() {
		Arrays.stream(Game.arenas).filter(Objects::nonNull).forEach(Arena::refreshArenaBoard);
	}

	/**
	 * Formats a string array of text based on the Arena given.
	 * @param arena The arena this text will be for.
	 * @return The properly formatted string array of text.
	 */
	public static String[] getHoloText(Arena arena) {
		List<String> info = new ArrayList<>();

		// Gather relevant stats
		info.add(Utils.format("&6&l" + arena.getName() + " Records"));
		if (!arena.getSortedDescendingRecords().isEmpty())
			arena.getSortedDescendingRecords().forEach(record -> {
				StringBuilder line = new StringBuilder("Wave &b" + record.getWave() + " &f- ");
				for (int i = 0; i < record.getPlayers().size() / 4 + 1; i++) {
					if (i * 4 + 4 < record.getPlayers().size()) {
						for (int j = i * 4; j < i * 4 + 4; j++)
							line.append(record.getPlayers().get(j)).append(", ");
						info.add(Utils.format(line.substring(0, line.length() - 1)));
					} else {
						for (int j = i * 4; j < record.getPlayers().size(); j++)
							line.append(record.getPlayers().get(j)).append(", ");
						info.add(Utils.format(line.substring(0, line.length() - 2)));
					}
					line = new StringBuilder();
				}
			});

		return info.toArray(new String[]{});
	}
}
