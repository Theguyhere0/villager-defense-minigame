package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.exceptions.NoSpawnException;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
	private final Main plugin;

	// Tracks arenas, info boards, and leaderboards for the game
	public static Arena[] arenas = new Arena[45];
	public static InfoBoard[] infoBoards = new InfoBoard[8];
	public static Map<String, Leaderboard> leaderboards = new HashMap<>();

	private static Location lobby;

	public Game(Main plugin) {
		this.plugin = plugin;

		Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("")).getKeys(false)
				.forEach(path -> {
			if (path.charAt(0) == 'a' && path.length() < 4)
				arenas[Integer.parseInt(path.substring(1))] = new Arena(plugin,
						Integer.parseInt(path.substring(1)),
						new Tasks(plugin, Integer.parseInt(path.substring(1))));
		});
		Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("infoBoard")).getKeys(false)
				.forEach(path -> {
					try {
						Location location = Utils.getConfigLocationNoPitch(plugin, "infoBoard." + path);
						if (location != null)
							infoBoards[Integer.parseInt(path)] = new InfoBoard(location, plugin);
					} catch (InvalidLocationException ignored) {
					}
				});
		Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("leaderboard")).getKeys(false)
				.forEach(path -> {
					try {
						Location location = Utils.getConfigLocationNoPitch(plugin, "leaderboard." + path);
						if (location != null)
							leaderboards.put(path, new Leaderboard(path, plugin));
					} catch (InvalidLocationException ignored) {
					}
				});
		setLobby(Utils.getConfigLocation(plugin, "lobby"));
	}

	/**
	 * Creates a scoreboard for a player.
	 * @param player Player to give a scoreboard.
	 */
	public static void createBoard(VDPlayer player) {
		// Create scoreboard manager and check that it isn't null
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		assert manager != null;

		Scoreboard board = manager.getNewScoreboard();
		Arena arena = Arrays.stream(arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);

		// Create score board
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				Utils.format("&6&l   " + arena.getName() + "  "));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score12= obj.getScore(Utils.format("&eWave: " + arena.getCurrentWave()));
		score12.setScore(12);
		Score score11 = obj.getScore(Utils.format("&aGems: " + player.getGems()));
		score11.setScore(11);
		StringBuilder kit = new StringBuilder(player.getKit().getName());
		if (player.getKit().isMultiLevel()) {
			kit.append(" ");
			for (int i = 0; i < player.getKit().getLevel(); i++) {
				kit.append("I");
			}
		}
		Score score10 = obj.getScore(Utils.format("&bKit: " + kit));
		score10.setScore(10);
		int bonus = 0;
		for (Challenge challenge : player.getChallenges())
			bonus += challenge.getBonus();
		Score score9 = obj.getScore(Utils.format(String.format("&5Challenges: (+%d%%)", bonus)));
		score9.setScore(9);
		if (player.getChallenges().size() < 4)
			for (Challenge challenge : player.getChallenges()) {
				Score score8 = obj.getScore(Utils.format("  &5" + challenge.getName()));
				score8.setScore(8);
			}
		else {
			StringBuilder challenges = new StringBuilder();
			for (Challenge challenge : player.getChallenges())
				challenges.append(challenge.getName().toCharArray()[0]);
			Score score8 = obj.getScore(Utils.format("  &5" + challenges));
			score8.setScore(8);
		}
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

	/**
	 * Wipes all mobs in all valid arenas.
	 */
	public static void cleanAll() {
		Arrays.stream(arenas).filter(Objects::nonNull).filter(arena -> !arena.isClosed())
				.forEach(arena -> Utils.clear(arena.getCorner1(), arena.getCorner2()));
	}

	public static Location getLobby() {
		return lobby;
	}

	public static void setLobby(Location lobby) {
		Game.lobby = lobby;
	}

	public void reloadLobby() {
		lobby = Utils.getConfigLocation(plugin, "lobby");
	}

	/**
	 * Creates a new info board at the given location and deletes the old info board.
	 * @param location - New location.
	 */
	public void setInfoBoard(Location location, int num) {
		// Save config location
		Utils.setConfigurationLocation(plugin, "infoBoard." + num, location);

		// Recreate the info board
		refreshInfoBoard(num);
	}

	/**
	 * Recreates the info board in game based on the location in the arena file.
	 */
	public void refreshInfoBoard(int num) {
		// Delete old board if needed
		if (infoBoards[num] != null)
			infoBoards[num].remove();

		try {
			// Create a new board and display it
			infoBoards[num] = new InfoBoard(
					Objects.requireNonNull(Utils.getConfigLocationNoPitch(plugin, "infoBoard." + num)),
					plugin);
			infoBoards[num].displayForOnline();
		} catch (Exception e) {
			Utils.debugError("Invalid location for info board " + num, 1);
			Utils.debugInfo("Info board location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the location data for info board " + num + ".", 1);
		}
	}

	/**
	 * Centers the info board location along the x and z axis.
	 */
	public void centerInfoBoard(int num) {
		// Center the location
		Utils.centerConfigLocation(plugin, "infoBoard." + num);

		// Recreate the portal
		refreshInfoBoard(num);
	}

	/**
	 * Removes the info board from the game and from the arena file.
	 */
	public void removeInfoBoard(int num) {
		if (infoBoards[num] != null) {
			infoBoards[num].remove();
			infoBoards[num] = null;
		}
		Utils.setConfigurationLocation(plugin, "infoBoard." + num, null);
	}

	/**
	 * Creates a new leaderboard at the given location and deletes the old leaderboard.
	 * @param location - New location.
	 */
	public void setLeaderboard(Location location, String type) {
		// Save config location
		Utils.setConfigurationLocation(plugin, "leaderboard." + type, location);

		// Recreate the leaderboard
		refreshLeaderboard(type);
	}

	/**
	 * Recreates the leaderboard in game based on the location in the arena file.
	 */
	public void refreshLeaderboard(String type) {
		// Delete old board if needed
		if (leaderboards.get(type) != null)
			leaderboards.remove(type);

		try {
			// Create a new board and display it
			leaderboards.put(type, new Leaderboard(type, plugin));
			leaderboards.get(type).displayForOnline();
		} catch (Exception e) {
			Utils.debugError("Invalid location for leaderboard " + type, 1);
			Utils.debugInfo("Leaderboard location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the location data for leaderboard " + type + ".", 1);
		}
	}

	/**
	 * Centers the leaderboard location along the x and z axis.
	 */
	public void centerLeaderboard(String type) {
		// Center the location
		Utils.centerConfigLocation(plugin, "leaderboard." + type);

		// Recreate the leaderboard
		refreshLeaderboard(type);
	}

	/**
	 * Removes the leaderboard from the game and from the arena file.
	 */
	public void removeLeaderboard(String type) {
		if (leaderboards.get(type) != null) {
			leaderboards.get(type).remove();
			leaderboards.remove(type);
		}
		Utils.setConfigurationLocation(plugin, "leaderboard." + type, null);
	}

	/**
	 * Display all portals to a player.
	 * @param player - The player to display all portals to.
	 */
	public static void displayAllPortals(Player player) {
		Arrays.stream(arenas).filter(Objects::nonNull).map(Arena::getPortal)
				.filter(Objects::nonNull).forEach(portal -> portal.displayForPlayer(player));
	}

	/**
	 * Display all arena boards to a player.
	 * @param player - The player to display all arena boards to.
	 */
	public static void displayAllArenaBoards(Player player) {
		Arrays.stream(arenas).filter(Objects::nonNull).map(Arena::getArenaBoard)
				.filter(Objects::nonNull).forEach(arenaBoard -> arenaBoard.displayForPlayer(player));
	}

	/**
	 * Display all info boards to a player.
	 * @param player - The player to display all info boards to.
	 */
	public static void displayAllInfoBoards(Player player) {
		Arrays.stream(infoBoards).filter(Objects::nonNull).forEach(infoBoard -> infoBoard.displayForPlayer(player));
	}

	/**
	 * Display all leaderboards to a player.
	 * @param player - The player to display all leaderboards to.
	 */
	public static void displayAllLeaderboards(Player player) {
		leaderboards.forEach((type, board) -> board.displayForPlayer(player));
	}

	public static void displayAllIndicators(Player player) {
		Arrays.stream(arenas).filter(Objects::nonNull).forEach(arena ->
		{
			if (arena.getPlayerSpawn() != null && arena.getPlayerSpawn().isOn())
				arena.getPlayerSpawn().getSpawnIndicator().displayForPlayer(player);
		});
		Arrays.stream(arenas).filter(Objects::nonNull).forEach(arena ->
				arena.getMonsterSpawns().forEach(spawn -> {
					if (spawn.isOn())
						spawn.getSpawnIndicator().displayForPlayer(player);
				}));
		Arrays.stream(arenas).filter(Objects::nonNull).forEach(arena ->
				arena.getVillagerSpawns().forEach(spawn -> {
					if (spawn.isOn())
						spawn.getSpawnIndicator().displayForPlayer(player);
				}));
	}

	/**
	 * Display everything displayable to a player.
	 * @param player - The player to display everything to.
	 */
	public static void displayEverything(Player player) {
		displayAllPortals(player);
		displayAllArenaBoards(player);
		displayAllInfoBoards(player);
		displayAllLeaderboards(player);
		displayAllIndicators(player);
	}

	/**
	 * Refresh the portal of every arena.
	 */
	public static void refreshPortals() {
		Arrays.stream(arenas).filter(Objects::nonNull).forEach(Arena::refreshPortal);
	}

	/**
	 * Refresh the arena board of every arena.
	 */
	public static void refreshArenaBoards() {
		Arrays.stream(arenas).filter(Objects::nonNull).forEach(Arena::refreshArenaBoard);
	}

	/**
	 * Refresh every info board.
	 */
	public void refreshInfoBoards() {
		for (int i = 0; i < infoBoards.length; i++) {
			refreshInfoBoard(i);
		}
	}

	/**
	 * Refresh every leaderboard.
	 */
	public void refreshLeaderboards() {
		List<String> types = new ArrayList<>(leaderboards.keySet());
		types.forEach(this::refreshLeaderboard);
	}

	/**
	 * Refresh all holographics.
	 */
	public void refreshAll() {
		refreshPortals();
		refreshArenaBoards();
		refreshInfoBoards();
		refreshLeaderboards();
	}

	public static void removePortals() {
        Arrays.stream(arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
				.forEach(Portal::remove);
    }
}
