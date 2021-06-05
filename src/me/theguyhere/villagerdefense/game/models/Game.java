package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Game {
	private final Main plugin;
	// Tracks active arenas and their related stats
	public List<Arena> arenas = new ArrayList<>(Collections.nCopies(45, null));
	private Location lobby;

	public Game(Main plugin, Portal portal) {
		this.plugin = plugin;
		plugin.getArenaData().getConfigurationSection("").getKeys(false).forEach(path -> {
			if (path.charAt(0) == 'a' && path.length() < 4)
				arenas.set(Integer.parseInt(path.substring(1)), new Arena(plugin, Integer.parseInt(path.substring(1)),
						new Tasks(plugin, this, Integer.parseInt(path.substring(1)), portal)));
		});
		lobby = Utils.getConfigLocation(plugin, "lobby");
	}

	public Location getLobby() {
		return lobby;
	}

	public void reloadLobby() {
		lobby = Utils.getConfigLocation(plugin, "lobby");
	}

	// Creates a game board for the player
	public void createBoard(VDPlayer player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Arena arena = arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Create score board
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				Utils.format("&6&l   " + arena.getName() + "  "));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score10 = obj.getScore(Utils.format("&eWave: " + arena.getCurrentWave()));
		score10.setScore(10);
		Score score9 = obj.getScore(Utils.format("&aGems: " + player.getGems()));
		score9.setScore(9);
		StringBuilder kit = new StringBuilder();
		try {
			kit.append(player.getKit().substring(0, player.getKit().length() - 1)).append(" ");
			for (int i = 0; i < Integer.parseInt(player.getKit().substring(player.getKit().length() - 1)); i++)
				kit.append("I");
		} catch (NumberFormatException e) {
			kit = new StringBuilder();
			kit.append(player.getKit());
		}
		Score score8 = obj.getScore(Utils.format("&bKit: " + kit));
		score8.setScore(8);
		Score score7 = obj.getScore("");
		score7.setScore(7);
		Score score6 = obj.getScore(Utils.format("&dPlayers: " + arena.getAlive()));
		score6.setScore(6);
		Score score5 = obj.getScore("Ghosts: " + arena.getGhostCount());
		score5.setScore(5);
		Score score4 = obj.getScore(Utils.format("&7Spectators: " + arena.getSpectatorCount()));
		score4.setScore(4);
		Score score3 = obj.getScore(" ");
		score3.setScore(3);
		Score score2 = obj.getScore(Utils.format("&2Villagers: " + arena.getVillagers()));
		score2.setScore(2);
		Score score1 = obj.getScore(Utils.format("&cEnemies: " + arena.getEnemies()));
		score1.setScore(1);
		Score score = obj.getScore(Utils.format("&4Kills: " + player.getKills()));
		score.setScore(0);

		player.getPlayer().setScoreboard(board);
	}
}
