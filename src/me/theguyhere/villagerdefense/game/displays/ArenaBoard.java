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
	private final Utils utils;
	private final Hologram[] arenaBoards = new Hologram[45];

	public ArenaBoard(Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
		utils = new Utils(plugin);
	}

	public void createArenaBoard(Player player, Arena arena) {
		// Create hologram
		addHolo(player.getLocation(), arena);

		// Save location data
		utils.setConfigurationLocation("arenaBoard." + arena.getArena(), player.getLocation());
		plugin.saveArenaData();
	}

	public void refreshArenaBoard(int arena) {
		arenaBoards[arena].delete();
		addHolo(utils.getConfigLocationNoPitch("arenaBoard." + arena), game.arenas.get(arena));
	}

	public void removeArenaBoard(int arena) {
		arenaBoards[arena].delete();
		arenaBoards[arena] = null;
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
				Location location = utils.getConfigLocationNoPitch("arenaBoard." + board);
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
				StringBuilder firstLine = new StringBuilder("&fWave &b" + record.getWave() + " &f-&7");
				StringBuilder secondLine = new StringBuilder("&7");
				if (record.getPlayers().size() > 6) {
					for (int i = 0; i < record.getPlayers().size() / 2; i++)
						firstLine.append(" ").append(record.getPlayers().get(i)).append(",");
					for (int i = record.getPlayers().size() / 2; i < record.getPlayers().size(); i++)
						secondLine.append(record.getPlayers().get(i)).append(", ");
					info.add(Utils.format(firstLine.toString()));
					info.add(Utils.format(secondLine.substring(0, secondLine.length() - 2)));
				} else {
					for (int i = 0; i < record.getPlayers().size(); i++)
						firstLine.append(" ").append(record.getPlayers().get(i)).append(",");
					info.add(Utils.format(firstLine.substring(0, firstLine.length() - 1)));
				}
			});

		return info.toArray(new String[]{});
	}
}
