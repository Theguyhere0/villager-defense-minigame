package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
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
	private final Utils utils;
	// Tracks active arenas and their related stats
	public List<Arena> arenas = new ArrayList<>(Collections.nCopies(45, null));
	private Location lobby;

	public Game(Main plugin, Portal portal) {
		utils = new Utils(plugin);
		plugin.getData().getConfigurationSection("").getKeys(false).forEach(path -> {
			if (path.charAt(0) == 'a')
				arenas.set(Integer.parseInt(path.substring(1)), new Arena(plugin, Integer.parseInt(path.substring(1)),
						new Tasks(plugin, this, Integer.parseInt(path.substring(1)), portal)));
		});
		lobby = utils.getConfigLocation("lobby");
	}

	public Location getLobby() {
		return lobby;
	}

	public void reloadLobby() {
		lobby = utils.getConfigLocation("lobby");
	}

	// Creates a game board for the player
	public void createBoard(VDPlayer player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Arena arena = arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Create score board
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				Utils.format("&2&l" + arena.getName()));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(Utils.format("&6Wave: " + arena.getCurrentWave()));
		score.setScore(7);
		Score score2 = obj.getScore(Utils.format("&2Gems: " + player.getGems()));
		score2.setScore(6);
		Score score3 = obj.getScore(Utils.format("&dPlayers: " + arena.getAlive()));
		score3.setScore(5);
		Score score4 = obj.getScore(Utils.format("&8Ghosts: " + arena.getGhostCount()));
		score4.setScore(4);
		Score score5 = obj.getScore(Utils.format("&7Spectators: " + arena.getSpectatorCount()));
		score5.setScore(3);
		Score score6 = obj.getScore(Utils.format("&aVillagers: " + arena.getVillagers()));
		score6.setScore(2);
		Score score7 = obj.getScore(Utils.format("&cEnemies: " + arena.getEnemies()));
		score7.setScore(1);
		Score score8 = obj.getScore(Utils.format("&4Kills: " + player.getKills()));
		score8.setScore(0);

		player.getPlayer().setScoreboard(board);
	}
}
