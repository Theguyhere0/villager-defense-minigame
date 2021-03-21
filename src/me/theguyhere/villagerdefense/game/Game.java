package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
	private final Main plugin;

	public Game(Main plugin) {
		this.plugin = plugin;
	}

	public List<VDPlayer> playing = new ArrayList<>(); // Tracks players playing and their other related stats
	public List<Arena> actives = new ArrayList<>(); // Tracks active arenas and their related stats

	// Creates a game board for the player
	public void createBoard(VDPlayer player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		int arena = player.getArena();
		int[] players = {0};
		int[] ghosts = {0};
		Arena arenaInstance = actives.stream().filter(r -> r.getArena() == arena).collect(Collectors.toList()).get(0);

		// Count players and ghosts
		playing.stream().filter(p -> p.getArena() == arena).forEach(gamer -> {
			players[0]++;
			if (gamer.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
				ghosts[0]++;
				players[0]--;
			}
		});

		// Create score board
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				Utils.format("&2&l" + plugin.getData().getString("a" + arena + ".name")));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(Utils.format("&6Wave: " + plugin.getData().getInt("a" + arena +
				".currentWave")));
		score.setScore(7);
		Score score2 = obj.getScore(Utils.format("&2Gems: " + player.getGems()));
		score2.setScore(6);
		Score score3 = obj.getScore(Utils.format("&dPlayers: " + players[0]));
		score3.setScore(5);
		Score score4 = obj.getScore(Utils.format("&8Ghosts: " + ghosts[0]));
		score4.setScore(4);
		Score score5 = obj.getScore(Utils.format("&7Spectators: " + plugin.getData().getInt("a" + arena +
				".players.spectating")));
		score5.setScore(3);
		Score score6 = obj.getScore(Utils.format("&aVillagers: " + arenaInstance.getVillagers()));
		score6.setScore(2);
		Score score7 = obj.getScore(Utils.format("&cEnemies: " + arenaInstance.getEnemies()));
		score7.setScore(1);
		Score score8 = obj.getScore(Utils.format("&4Kills: " + player.getKills()));
		score8.setScore(0);

		player.getPlayer().setScoreboard(board);
	}
}
