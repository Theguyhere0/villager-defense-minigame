package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaBoard {
	private final Main plugin;
	private final Game game;
	private final Hologram[] arenaBoards = new Hologram[45];

	public ArenaBoard(Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}

	public void createArenaBoard(Player player, Arena arena) {
		// Create hologram
		addHolo(player.getLocation(), arena);

		// Save location data
		Utils.setConfigurationLocation(plugin, "arenaBoard." + arena.getArena(), player.getLocation());
		plugin.saveArenaData();
	}

	public void refreshArenaBoard(int arena) {
		if (arenaBoards[arena] != null) {
			arenaBoards[arena].delete();
			addHolo(Utils.getConfigLocationNoPitch(plugin, "arenaBoard." + arena), game.arenas.get(arena));
		}
	}

	public void removeArenaBoard(int arena) {
		if (arenaBoards[arena] != null) {
			arenaBoards[arena].delete();
			arenaBoards[arena] = null;
		}
	}

	public void addHolo(Location location, Arena arena) {
		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 3);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		holo.insertTextLine(0, getHoloText(arena)[0]);
		for (int i = 1; i < getHoloText(arena).length; i++)
			holo.appendTextLine(getHoloText(arena)[i]);

		// Save hologram
		arenaBoards[arena.getArena()] = holo;
	}

	public void loadArenaBoards() {
		if (plugin.getArenaData().contains("arenaBoard"))
			plugin.getArenaData().getConfigurationSection("arenaBoard").getKeys(false).forEach(board -> {
				Location location = Utils.getConfigLocationNoPitch(plugin, "arenaBoard." + board);
				if (location != null)
					addHolo(location, game.arenas.get(Integer.parseInt(board)));
			});
	}

	private String[] getHoloText(Arena arena) {
		List<String> info = new ArrayList<>();

		// Gather relevant stats
		info.add(Utils.format("&6&l" + arena.getName() + " Records"));
		if (!arena.getSortedDescendingRecords().isEmpty())
			arena.getSortedDescendingRecords().stream().forEachOrdered(record -> {
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
