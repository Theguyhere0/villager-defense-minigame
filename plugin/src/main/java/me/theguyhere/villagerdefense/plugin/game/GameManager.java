package me.theguyhere.villagerdefense.plugin.game;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.data.*;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.game.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.structures.InfoBoard;
import me.theguyhere.villagerdefense.plugin.structures.Leaderboard;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {
	@Getter
    private static final Map<Integer, Arena> arenas = new HashMap<>();
	private static final Map<Integer, InfoBoard> infoBoards = new HashMap<>();
	private static final Map<String, Leaderboard> leaderboards = new HashMap<>();
	@Getter
    private static Location lobby;
	@Getter
    private static final List<String> validSounds = new LinkedList<>(Arrays.asList("blocks", "cat", "chirp", "far",
			"mall", "mellohi", "pigstep", "stal", "strad", "wait", "ward"));

	public static void init() {
		wipeArenas();

		if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
			validSounds.add("otherside");

		ArenaDataManager.getArenaIDs().forEach(id -> arenas.put(id, new Arena(id)));

		try {
			for (Integer id : GameDataManager.getInfoBoardIDs()) {
				infoBoards.put(id, new InfoBoard(GameDataManager.getInfoBoardLocation(id)));
			}
		}
		catch (BadDataException e) {
			// TODO
			CommunicationManager.debugErrorShouldNotHappen();
		}
		catch (InvalidLocationException | NoSuchPathException e) {
			CommunicationManager.debugErrorShouldNotHappen();
		}

		try {
            for (String type : GameDataManager.getLeaderboardTypes()) {
                leaderboards.put(type, new Leaderboard(type));
            }
        }
		catch (BadDataException e) {
			// TODO
			CommunicationManager.debugErrorShouldNotHappen();
		}
		catch (InvalidLocationException | NoSuchPathException e) {
			CommunicationManager.debugErrorShouldNotHappen();
		}

		try {
			lobby = GameDataManager.getLobbyLocation();
		}
		catch (BadDataException e) {
			// TODO
			CommunicationManager.debugErrorShouldNotHappen();
		}
		catch (NoSuchPathException e) {
			lobby = null;
		}

		Main.plugin.setLoaded(true);
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

    /**
	 * Creates a scoreboard for a player.
	 * @param player Player to give a scoreboard
	 */
	public static void createBoard(VDPlayer player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		if (manager == null)
			return;
		Scoreboard board = manager.getNewScoreboard();
		Arena arena;
		try {
				arena = getArena(player.getPlayer());
		} catch (ArenaNotFoundException e) {
			return;
		}

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
		StringBuilder kit2 = new StringBuilder("    ");
		if (player.getKit().isMultiLevel()) {
			kit.append(" ");
			for (int i = 0; i < Math.max(0, player.getKit().getLevel()); i++)
				kit.append("I");
		}
		if (player.getKit2() != null) {
			kit.append(" +");
			kit2.append(player.getKit2().getName());
			if (player.getKit2().isMultiLevel()) {
				kit2.append(" ");
				for (int i = 0; i < Math.max(0, player.getKit2().getLevel()); i++)
					kit2.append("I");
			}
		}
		Score score11 = obj.getScore(CommunicationManager.format("&b" + LanguageManager.messages.kit + ": " +
				kit));
		score11.setScore(11);
		Score score10 = obj.getScore(CommunicationManager.format("&b" + kit2));
		score10.setScore(10);

		int bonus = 0;
		for (Challenge challenge : player.getChallenges())
			bonus += challenge.getBonus();
		Score score9 = obj.getScore(CommunicationManager.format(String.format("&5" +
				LanguageManager.messages.challenges + ": (+%d%%)", bonus)));
		score9.setScore(9);

		if (player.getChallenges().size() < (player.getKit2() != null ? 3 : 4))
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

	/**
	 * Saves a new lobby for the server and updates the cached lobby.
	 * @param location Lobby location
	 */
	public static void saveLobby(Location location) {
		GameDataManager.setLobbyLocation(location);
		lobby = GameManager.getLobby();
	}

	/**
	 * Reloads the cached lobby location.
	 */
	public static void reloadLobby() {
		lobby = GameManager.getLobby();
	}

	/**
	 * Generates a new ID for a new info board.
	 * @return New info board ID
	 */
	public static int newArenaID() {
		return Calculator.nextSmallestUniqueWhole(arenas.keySet());
	}

	/**
	 * Creates a new info board at the given location and deletes the old info board.
	 * @param location New location
	 */
	public static void setInfoBoard(int infoBoardID, Location location) {
		// Save config location
		GameDataManager.setInfoBoardLocation(infoBoardID, location);

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

		// Create a new board and display it
		try {
			infoBoards.put(infoBoardID, new InfoBoard(GameDataManager.getInfoBoardLocation(infoBoardID)));
			infoBoards.get(infoBoardID).displayForOnline();
		} catch (BadDataException | InvalidLocationException e) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.NORMAL,
				"Invalid location for info board " + infoBoardID
			);
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.NORMAL,
				"Info board location data may be corrupt. If data cannot be manually corrected in arenaData.yml, " +
					"please delete the location data for info board " + infoBoardID + "."
			);
		} catch (NoSuchPathException e) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.NORMAL,
				"Info board " + infoBoardID + " does not exist"
			);
		}
	}

	/**
	 * Centers the info board location along the x and z axis.
	 */
	public static void centerInfoBoard(int infoBoardID) {
		// Center the location
        try {
            GameDataManager.centerInfoBoardLocation(infoBoardID);
        }
		catch (BadDataException | NoSuchPathException e) {
            return;
        }

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
		GameDataManager.removeInfoBoardLocation(infoBoardID);
	}

	/**
	 * Generates a new ID for a new info board.
	 *
	 * @return New info board ID
	 */
	public static int newInfoBoardID() {
		return Calculator.nextSmallestUniqueWhole(infoBoards.keySet());
	}

	/**
	 * Creates a new leaderboard at the given location and deletes the old leaderboard.
	 *
	 * @param location New location
	 */
	public static void setLeaderboard(String type, Location location) {
		// Save config location
		GameDataManager.setLeaderboardLocation(type, location);

		// Recreate the leaderboard
		refreshLeaderboard(type);
	}

	/**
	 * Recreates the leaderboard in game based on the location in the arena file.
	 */
	public static void refreshLeaderboard(String type) {
		// Delete old board if needed
		if (leaderboards.get(type) != null)
			leaderboards.get(type).remove();

		// Create a new board and display it
		try {
			leaderboards.put(type, new Leaderboard(type));
			leaderboards.get(type).displayForOnline();
		} catch (BadDataException | InvalidLocationException e) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.NORMAL,
				"Invalid location for leaderboard " + type
			);
			CommunicationManager.debugInfo(
				CommunicationManager.DebugLevel.NORMAL, "Leaderboard location data may be corrupt. " +
					"If data cannot be manually corrected in arenaData.yml, please delete the location data for " +
					"leaderboard " + type + ".");
		} catch (NoSuchPathException e) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.NORMAL,
				"Leaderboard of type " + type + " does not exist"
			);
		}
	}

	/**
	 * Centers the leaderboard location along the x and z axis.
	 */
	public static void centerLeaderboard(String type) {
		// Center the location
		try {
			GameDataManager.centerLeaderboardLocation(type);
		}
		catch (BadDataException | NoSuchPathException e) {
			return;
		}

		// Recreate the leaderboard
		refreshLeaderboard(type);
	}

	/**
	 * Removes the leaderboard from the game and from the arena file.
	 */
	public static void removeLeaderboard(String type) {
		if (leaderboards.get(type) != null) {
			leaderboards.get(type).remove();
			leaderboards.remove(type);
		}
		GameDataManager.removeLeaderboardLocation(type);
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
}
