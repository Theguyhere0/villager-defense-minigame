package me.theguyhere.villagerdefense.plugin.game;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.displays.InfoBoard;
import me.theguyhere.villagerdefense.plugin.displays.Leaderboard;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that keeps and controls the state of the plugin.
 */
public class GameController {
	private static final Map<Integer, Arena> arenas = new HashMap<>();
	private static final Map<Integer, InfoBoard> infoBoards = new HashMap<>();
	private static final Map<String, Leaderboard> leaderboards = new HashMap<>();
	private static Location lobby;
	private static final List<String> validSounds = new LinkedList<>(Arrays.asList("blocks", "cat", "chirp", "far",
		"mall", "mellohi", "pigstep", "stal", "strad", "wait", "ward"
	));

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
					}
					catch (InvalidLocationException ignored) {
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
					}
					catch (InvalidLocationException ignored) {
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
		}
		catch (Exception e) {
			throw new ArenaNotFoundException();
		}
	}

	public static @NotNull Arena getArena(Player player) throws ArenaNotFoundException {
		try {
			return arenas.values().stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
				.collect(Collectors.toList()).get(0);
		}
		catch (Exception e) {
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

	public static Location getLobby() {
		return lobby;
	}

	/**
	 * Set the cached lobby in {@link GameController}.
	 * DOES NOT CHANGE THE STORED LOBBY FOR THE SERVER
	 *
	 * @param lobby Lobby location.
	 */
	public static void setLobby(Location lobby) {
		GameController.lobby = lobby;
	}

	/**
	 * Saves a new lobby for the server and changes the cached lobby.
	 *
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
		return Calculator.nextSmallestUniqueWhole(arenas.keySet());
	}

	public static InfoBoard getInfoBoard(int infoBoardID) {
		return infoBoards.get(infoBoardID);
	}

	/**
	 * Creates a new info board at the given location and deletes the old info board.
	 *
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
		}
		catch (Exception e) {
			CommunicationManager.debugError("Invalid location for info board " + infoBoardID,
				CommunicationManager.DebugLevel.NORMAL);
			CommunicationManager.debugInfo(
				"Info board location data may be corrupt. If data cannot be manually corrected in arenaData.yml, " +
					"please delete the location data for info board " + infoBoardID + ".",
				CommunicationManager.DebugLevel.NORMAL
			);
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
		return Calculator.nextSmallestUniqueWhole(infoBoards.keySet());
	}

	public static Leaderboard getLeaderboard(String id) {
		return leaderboards.get(id);
	}

	/**
	 * Creates a new leaderboard at the given location and deletes the old leaderboard.
	 *
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
		}
		catch (Exception e) {
			CommunicationManager.debugError("Invalid location for leaderboard " + id,
				CommunicationManager.DebugLevel.NORMAL);
			CommunicationManager.debugInfo("Leaderboard location data may be corrupt. If data cannot be manually " +
				"corrected in arenaData.yml, please delete the location data for leaderboard " + id + ".",
				CommunicationManager.DebugLevel.NORMAL);
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
	 *
	 * @param player - The player to display all portals to.
	 */
	public static void displayAllPortals(Player player) {
		arenas.values().stream().filter(Objects::nonNull).map(Arena::getPortal)
			.filter(Objects::nonNull).forEach(portal -> portal.displayForPlayer(player));
	}

	/**
	 * Display all arena boards to a player.
	 *
	 * @param player - The player to display all arena boards to.
	 */
	public static void displayAllArenaBoards(Player player) {
		arenas.values().stream().filter(Objects::nonNull).map(Arena::getArenaBoard)
			.filter(Objects::nonNull).forEach(arenaBoard -> arenaBoard.displayForPlayer(player));
	}

	/**
	 * Display all info boards to a player.
	 *
	 * @param player - The player to display all info boards to.
	 */
	public static void displayAllInfoBoards(Player player) {
		infoBoards.values().stream().filter(Objects::nonNull).forEach(infoBoard -> infoBoard.displayForPlayer(player));
	}

	/**
	 * Display all leaderboards to a player.
	 *
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
	 *
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
		types.forEach(GameController::refreshLeaderboard);
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
