package me.theguyhere.villagerdefense.plugin.game.models;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.plugin.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
	private static final Map<Integer, Arena> arenas = new HashMap<>();
	private static final Map<Integer, InfoBoard> infoBoards = new HashMap<>();
	private static final Map<String, Leaderboard> leaderboards = new HashMap<>();
	private static Location lobby;
	private static final List<String> validSounds = new LinkedList<>(Arrays.asList("blocks", "cat", "chirp", "far",
			"mall", "mellohi", "pigstep", "stal", "strad", "wait", "ward"));

	public static void init() {
		ConfigurationSection section;
		wipeArenas();

		if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
			validSounds.add("otherside");

		section = Main.getArenaData().getConfigurationSection("arena");
		if (section != null)
			section.getKeys(false)
					.forEach(id -> arenas.put(Integer.parseInt(id), new Arena(Integer.parseInt(id))));

		section = Main.getArenaData().getConfigurationSection("infoBoard");
		if (section != null)
			section.getKeys(false)
					.forEach(id -> {
						try {
							Location location = DataManager.getConfigLocationNoPitch("infoBoard." + id);
							if (location != null)
								infoBoards.put(Integer.parseInt(id), new InfoBoard(location));
						} catch (InvalidLocationException ignored) {
						}
					});

		section = Main.getArenaData().getConfigurationSection("leaderboard");
		if (section != null)
			section.getKeys(false)
					.forEach(id -> {
						try {
							Location location = DataManager.getConfigLocationNoPitch("leaderboard." + id);
							if (location != null)
								leaderboards.put(id, new Leaderboard(id));
						} catch (InvalidLocationException ignored) {
						}
					});

		setLobby(DataManager.getConfigLocation("lobby"));

		Main.setLoaded(true);
	}

	public static @NotNull Arena getArena(int arenaID) throws ArenaNotFoundException {
		Arena result = arenas.get(arenaID);
		if (result == null)
			throw new ArenaNotFoundException();
		return result;
	}

	public static @NotNull Arena getArena(String arenaName) throws ArenaNotFoundException {
		try {
			return arenas.values().stream().filter(Objects::nonNull).filter(a -> a.getName() != null)
					.filter(a -> a.getName().equals(arenaName)).collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			throw new ArenaNotFoundException();
		}
	}

	public static @NotNull Arena getArena(Player player) throws ArenaNotFoundException {
		try {
			return arenas.values().stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
		} catch (Exception e) {
			throw new ArenaNotFoundException();
		}
	}

	public static void addArena(int id, Arena arena) {
		arenas.put(id, arena);
	}

	public static void removeArena(int arenaID) {
		arenas.get(arenaID).remove();
		arenas.remove(arenaID);
	}

	public static boolean checkPlayer(Player player) {
		return arenas.values().stream().filter(Objects::nonNull).anyMatch(arena -> arena.hasPlayer(player));
	}

	public static Map<Integer, Arena> getArenas() {
		return arenas;
	}

	public static List<String> getValidSounds() {
		return validSounds;
	}

	/**
	 * Creates a scoreboard for a player.
	 * @param player Player to give a scoreboard.
	 */
	public static void createBoard(VDPlayer player) {
		ScoreboardManager manager = Objects.requireNonNull(Bukkit.getScoreboardManager());
		Scoreboard board = manager.getNewScoreboard();
		Arena arena = player.getArena();

		// Create score board
		Objective obj = board.registerNewObjective("VillagerDefense", "dummy",
				CommunicationManager.format("&6&l   " + arena.getName() + "  "));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score13 = obj.getScore(CommunicationManager.format("&e" + LanguageManager.messages.wave + ": " +
				arena.getCurrentWave()));
		score13.setScore(13);

		Score score12 = obj.getScore(CommunicationManager.format("&a" + LanguageManager.messages.gems + ": " +
				player.getGems()));
		score12.setScore(12);

		StringBuilder kit = new StringBuilder(player.getKit().getName());
		if (player.getKit().isMultiLevel()) {
			kit.append(" ");
			for (int i = 0; i < Math.max(0, player.getKit().getLevel()); i++)
				kit.append("I");
		}
		Score score11 = obj.getScore(CommunicationManager.format("&b" + LanguageManager.messages.kit + ": " +
				kit));
		score11.setScore(11);
		Score score10 = obj.getScore(CommunicationManager.format(""));
		score10.setScore(10);

		int bonus = 0;
		for (Challenge challenge : player.getChallenges())
			bonus += challenge.getBonus();
		Score score9 = obj.getScore(CommunicationManager.format(String.format("&5" +
				LanguageManager.messages.challenges + ": (+%d%%)", bonus)));
		score9.setScore(9);

		if (player.getChallenges().size() < 4)
			for (Challenge challenge : player.getChallenges()) {
				Score score8 = obj.getScore(CommunicationManager.format("  &5" + challenge.getName()));
				score8.setScore(8);
			}
		else {
			StringBuilder challenges = new StringBuilder();
			for (Challenge challenge : player.getChallenges())
				challenges.append(challenge.getName().toCharArray()[0]);
			Score score8 = obj.getScore(CommunicationManager.format("  &5" + challenges));
			score8.setScore(8);
		}

		Score score7 = obj.getScore("");
		score7.setScore(7);

		Score score6 = obj.getScore(CommunicationManager.format("&d" + LanguageManager.messages.players + ": " +
				arena.getAlive()));
		score6.setScore(6);

		Score score5 = obj.getScore(LanguageManager.messages.ghosts + ": " + arena.getGhostCount());
		score5.setScore(5);

		Score score4 = obj.getScore(CommunicationManager.format("&7" + LanguageManager.messages.spectators +
				": " + arena.getSpectatorCount()));
		score4.setScore(4);

		Score score3 = obj.getScore(" ");
		score3.setScore(3);

		Score score2 = obj.getScore(CommunicationManager.format("&2" + LanguageManager.messages.villagers + ": " +
				arena.getVillagers()));
		score2.setScore(2);

		Score score1 = obj.getScore(CommunicationManager.format("&c" + LanguageManager.messages.enemies + ": " +
				arena.getEnemies()));
		score1.setScore(1);

		Score score = obj.getScore(CommunicationManager.format("&4" + LanguageManager.messages.kills + ": " +
				player.getKills()));
		score.setScore(0);

		player.getPlayer().setScoreboard(board);
	}

	public static Location getLobby() {
		return lobby;
	}

	/**
	 * Set the cached lobby in {@link GameManager}.
	 * DOES NOT CHANGE THE STORED LOBBY FOR THE SERVER
	 * @param lobby Lobby location.
	 */
	public static void setLobby(Location lobby) {
		GameManager.lobby = lobby;
	}

	/**
	 * Saves a new lobby for the server and changes the cached lobby.
	 * @param lobby Lobby location.
	 */
	public static void saveLobby(Location lobby) {
		DataManager.setConfigurationLocation("lobby", lobby);
		setLobby(lobby);
	}

	public static void reloadLobby() {
		lobby = DataManager.getConfigLocation("lobby");
	}

	/**
	 * Generates a new ID for a new info board.
	 *
	 * @return New info board ID
	 */
	public static int newArenaID() {
		return Utils.nextSmallestUniqueWhole(arenas.keySet());
	}

	public static InfoBoard getInfoBoard(int infoBoardID) {
		return infoBoards.get(infoBoardID);
	}

	/**
	 * Creates a new info board at the given location and deletes the old info board.
	 * @param location - New location.
	 */
	public static void setInfoBoard(Location location, int infoBoardID) {
		// Save config location
		DataManager.setConfigurationLocation("infoBoard." + infoBoardID, location);

		// Recreate the info board
		refreshInfoBoard(infoBoardID);
	}

	/**
	 * Recreates the info board in game based on the location in the arena file.
	 */
	public static void refreshInfoBoard(int infoBoardID) {
		// Delete old board if needed
		if (infoBoards.containsKey(infoBoardID))
			infoBoards.get(infoBoardID).remove();

		try {
			// Create a new board and display it
			infoBoards.put(infoBoardID, new InfoBoard(
					Objects.requireNonNull(DataManager.getConfigLocationNoPitch("infoBoard." + infoBoardID))
            ));
			infoBoards.get(infoBoardID).displayForOnline();
		} catch (Exception e) {
			CommunicationManager.debugError("Invalid location for info board " + infoBoardID, 1);
			CommunicationManager.debugInfo("Info board location data may be corrupt. If data cannot be manually " +
					"corrected in arenaData.yml, please delete the location data for info board " + infoBoardID + ".",
					1);
		}
	}

	/**
	 * Centers the info board location along the x and z axis.
	 */
	public static void centerInfoBoard(int infoBoardID) {
		// Center the location
		DataManager.centerConfigLocation("infoBoard." + infoBoardID);

		// Recreate the info board
		refreshInfoBoard(infoBoardID);
	}

	/**
	 * Removes the info board from the game and from the arena file.
	 */
	public static void removeInfoBoard(int infoBoardID) {
		if (infoBoards.containsKey(infoBoardID)) {
			infoBoards.get(infoBoardID).remove();
			infoBoards.remove(infoBoardID);
		}
		DataManager.setConfigurationLocation("infoBoard." + infoBoardID, null);
	}

	/**
	 * Generates a new ID for a new info board.
	 *
	 * @return New info board ID
	 */
	public static int newInfoBoardID() {
		return Utils.nextSmallestUniqueWhole(infoBoards.keySet());
	}

	public static Leaderboard getLeaderboard(String id) {
		return leaderboards.get(id);
	}

	/**
	 * Creates a new leaderboard at the given location and deletes the old leaderboard.
	 * @param location - New location.
	 */
	public static void setLeaderboard(Location location, String id) {
		// Save config location
		DataManager.setConfigurationLocation("leaderboard." + id, location);

		// Recreate the leaderboard
		refreshLeaderboard(id);
	}

	/**
	 * Recreates the leaderboard in game based on the location in the arena file.
	 */
	public static void refreshLeaderboard(String id) {
		// Delete old board if needed
		if (leaderboards.get(id) != null)
			leaderboards.get(id).remove();

		try {
			// Create a new board and display it
			leaderboards.put(id, new Leaderboard(id));
			leaderboards.get(id).displayForOnline();
		} catch (Exception e) {
			CommunicationManager.debugError("Invalid location for leaderboard " + id, 1);
			CommunicationManager.debugInfo("Leaderboard location data may be corrupt. " +
					"If data cannot be manually corrected in arenaData.yml, please delete the location data for " +
					"leaderboard " + id + ".", 1);
		}
	}

	/**
	 * Centers the leaderboard location along the x and z axis.
	 */
	public static void centerLeaderboard(String id) {
		// Center the location
		DataManager.centerConfigLocation("leaderboard." + id);

		// Recreate the leaderboard
		refreshLeaderboard(id);
	}

	/**
	 * Removes the leaderboard from the game and from the arena file.
	 */
	public static void removeLeaderboard(String id) {
		if (leaderboards.get(id) != null) {
			leaderboards.get(id).remove();
			leaderboards.remove(id);
		}
		DataManager.setConfigurationLocation("leaderboard." + id, null);
	}

	/**
	 * Display all portals to a player.
	 * @param player - The player to display all portals to.
	 */
	public static void displayAllPortals(Player player) {
		arenas.values().stream().filter(Objects::nonNull).map(Arena::getPortal)
				.filter(Objects::nonNull).forEach(portal -> portal.displayForPlayer(player));
	}

	/**
	 * Display all arena boards to a player.
	 * @param player - The player to display all arena boards to.
	 */
	public static void displayAllArenaBoards(Player player) {
		arenas.values().stream().filter(Objects::nonNull).map(Arena::getArenaBoard)
				.filter(Objects::nonNull).forEach(arenaBoard -> arenaBoard.displayForPlayer(player));
	}

	/**
	 * Display all info boards to a player.
	 * @param player - The player to display all info boards to.
	 */
	public static void displayAllInfoBoards(Player player) {
		infoBoards.values().stream().filter(Objects::nonNull).forEach(infoBoard -> infoBoard.displayForPlayer(player));
	}

	/**
	 * Display all leaderboards to a player.
	 * @param player - The player to display all leaderboards to.
	 */
	public static void displayAllLeaderboards(Player player) {
		leaderboards.forEach((type, board) -> board.displayForPlayer(player));
	}

	public static void displayAllIndicators(Player player) {
		arenas.values().stream().filter(Objects::nonNull).forEach(arena ->
		{
			if (arena.getPlayerSpawn() != null && arena.getPlayerSpawn().isOn())
				arena.getPlayerSpawn().getSpawnIndicator().displayForPlayer(player);
		});
		arenas.values().stream().filter(Objects::nonNull).forEach(arena ->
				arena.getMonsterSpawns().forEach(spawn -> {
					if (spawn.isOn())
						spawn.getSpawnIndicator().displayForPlayer(player);
				}));
		arenas.values().stream().filter(Objects::nonNull).forEach(arena ->
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
		arenas.values().stream().filter(Objects::nonNull).forEach(Arena::refreshPortal);
	}

	/**
	 * Refresh the arena board of every arena.
	 */
	public static void refreshArenaBoards() {
		arenas.values().stream().filter(Objects::nonNull).forEach(Arena::refreshArenaBoard);
	}

	/**
	 * Refresh every info board.
	 */
	public static void refreshInfoBoards() {
		for (int i = 0; i < infoBoards.values().stream().filter(Objects::nonNull).count(); i++) {
			refreshInfoBoard(i);
		}
	}

	/**
	 * Refresh every leaderboard.
	 */
	public static void refreshLeaderboards() {
		List<String> types = new ArrayList<>(leaderboards.keySet());
		types.forEach(GameManager::refreshLeaderboard);
	}

	/**
	 * Refresh all holographics.
	 */
	public static void refreshAll() {
		refreshPortals();
		refreshArenaBoards();
		refreshInfoBoards();
		refreshLeaderboards();
	}

	public static void wipeArenas() {
		arenas.values().stream().filter(Objects::nonNull).forEach(Arena::wipe);
	}

	public static void closeArenas() {
		arenas.values().forEach(arena -> arena.setClosed(true));
	}
}
