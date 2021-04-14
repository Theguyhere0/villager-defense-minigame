package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.GameItems;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.util.Random;

public class Inventories {
	private final Main plugin;
	private final Game game;
	private final InventoryItems ii;

	public Inventories (Main plugin, Game game, InventoryItems ii) {
		this.plugin = plugin;
		this.game = game;
		this.ii = ii;
	}

	// Easily get alphabet
	public static final char[] NAMES = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
		's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' '
	};

	// Easily alternate between black and white wool
	public static final Material[] KEY_MATS = {Material.BLACK_CONCRETE, Material.WHITE_WOOL};
	public static final Material[] INFO_BOARD_MATS = {Material.DARK_OAK_SIGN, Material.BIRCH_SIGN};
	public static final Material[] MONSTER_MATS = {Material.SKELETON_SKULL, Material.ZOMBIE_HEAD};
	public static final Material[] VILLAGER_MATS = {Material.WITHER_ROSE, Material.POPPY};

	// Temporary constants for buttons that don't work yet
	final static String CONSTRUCTION = "&fComing Soon!";
	final static boolean[] FLAGS = {true, true};

	// Menu of all the arenas
	public Inventory createArenasInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  Utils.format("&k") +
				Utils.format("&2&lVillager Defense Arenas"));

		// Options to interact with all 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (game.arenas.get(i) == null)
				inv.setItem(i, Utils.createItem(Material.RED_CONCRETE,
						Utils.format("&c&lCreate Arena " + (i + 1))));
			else
				inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE,
						Utils.format("&a&lEdit " + game.arenas.get(i).getName())));
		}

		// Option to set lobby location
		inv.setItem(45, Utils.createItem(Material.BELL, Utils.format("&2&lLobby"),
				Utils.format("&7Manage minigame lobby")));

		// Option to set info hologram
		inv.setItem(46, Utils.createItem(Material.OAK_SIGN, Utils.format("&6&lInfo Boards"),
				Utils.format("&7Manage info boards")));

		// Option to set leaderboard hologram
		inv.setItem(47, Utils.createItem(Material.GOLDEN_HELMET, Utils.format("&e&lLeaderboards"),
				FLAGS, null, Utils.format("&7Manage leaderboards")));

		// Option to exit
		inv.setItem(53, ii.exit());
		
		return inv;
	}

	// Menu for lobby
	public Inventory createLobbyInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lLobby"));

		// Option to create the lobby
		inv.setItem(0, ii.create("Lobby"));

		// Option to teleport to the lobby
		inv.setItem(2, ii.teleport("Lobby"));

		// Option to center the lobby
		inv.setItem(4, ii.center("Lobby"));

		// Option to remove the lobby
		inv.setItem(6, ii.remove("LOBBY"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing lobby
	public Inventory createLobbyConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Lobby?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing info boards
	public Inventory createInfoBoardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&6&lInfo Boards"));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible info boards
		for (int i = 0; i < 8; i++) {
			// Check if the info board exists
			if (!plugin.getArenaData().contains("infoBoard." + i))
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(INFO_BOARD_MATS[index], Utils.format("&6&lInfo Board " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing a specific info board
	public Inventory createInfoBoardMenu(int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&6&lInfo Board " + slot));

		// Option to create info board
		inv.setItem(0, ii.create("Info Board"));

		// Option to teleport to info board
		inv.setItem(2, ii.teleport("Info Board"));

		// Option to center the info board
		inv.setItem(4, ii.center("Info Board"));

		// Option to remove info board
		inv.setItem(6, ii.remove("INFO BOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing info boards
	public Inventory createInfoBoardConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Info Board?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for leaderboards
	public Inventory createLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&e&lLeaderboards"));

		// Option to modify total kills leaderboard
		inv.setItem(0, Utils.createItem(Material.DRAGON_HEAD, Utils.format("&4&lTotal Kills Leaderboard")));

		// Option to modify top kills leaderboard
		inv.setItem(1, Utils.createItem(Material.ZOMBIE_HEAD, Utils.format("&c&lTop Kills Leaderboard")));

		// Option to modify total gems leaderboard
		inv.setItem(2, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&2&lTotal Gems Leaderboard")));

		// Option to modify top balance leaderboard
		inv.setItem(3, Utils.createItem(Material.EMERALD, Utils.format("&a&lTop Balance Leaderboard")));

		// Option to modify top wave leaderboard
		inv.setItem(4, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&9&lTop Wave Leaderboard"),
				FLAGS, null));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the total kills leaderboard
	public Inventory createTotalKillsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lTotal Kills Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, ii.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, ii.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, ii.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, ii.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the top kills leaderboard
	public Inventory createTopKillsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&c&lTop Kills Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, ii.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, ii.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, ii.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, ii.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the total gems leaderboard
	public Inventory createTotalGemsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lTotal Gems Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, ii.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, ii.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, ii.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, ii.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the top balance leaderboard
	public Inventory createTopBalanceLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&a&lTop Balance Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, ii.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, ii.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, ii.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, ii.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the top wave leaderboard
	public Inventory createTopWaveLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&9&lTop Wave Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, ii.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, ii.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, ii.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, ii.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for total kills leaderboard
	public Inventory createTotalKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Confirmation menu for top kills leaderboard
	public Inventory createTopKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Confirmation menu for total gems leaderboard
	public Inventory createTotalGemsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Gems Leaderboard?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Confirmation menu for top balance leaderboard
	public Inventory createTopBalanceConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Balance Leaderboard?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Confirmation menu for top wave leaderboard
	public Inventory createTopWaveConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Wave Leaderboard?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for naming an arena
	public Inventory createNamingInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Gather arena name and caps lock state
		String name = arenaInstance.getName();
		if (name == null)
			name = "";
		boolean caps = arenaInstance.isCaps();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&2&lArena " + (arena + 1) + " Name: &8&l" + name));

		// Letter and number inputs
		for (int i = 0; i < 36; i++) {
			if (caps)
				inv.setItem(i, Utils.createItem(KEY_MATS[i % 2], Utils.format("&f&l" + NAMES[i + 36])));
			else inv.setItem(i, Utils.createItem(KEY_MATS[i % 2], Utils.format("&f&l" + NAMES[i])));
		}

		// Space inputs
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.GRAY_CONCRETE, Utils.format("&f&lSpace")));

		// Caps lock toggle
		if (caps)
			inv.setItem(45, Utils.createItem(Material.SPECTRAL_ARROW, Utils.format("&2&lCAPS LOCK: ON")));
		else inv.setItem(45, Utils.createItem(Material.ARROW, Utils.format("&4&lCAPS LOCK: OFF")));

		// Backspace input
		inv.setItem(46, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&lBackspace")));

		// Save option
		inv.setItem(52, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&lSAVE")));

		// Cancel option
		inv.setItem(53, Utils.createItem(Material.BARRIER, Utils.format("&c&lCANCEL")));

		return inv;
	}

	// Menu for editing an arena
	public Inventory createArenaInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lEdit " + arenaInstance.getName()));

		// Option to edit name
		inv.setItem(0, Utils.createItem(Material.NAME_TAG, Utils.format("&6&lEdit Name")));

		// Option to edit game portal
		inv.setItem(1, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&5&lGame Portal")));

		// Option to edit player settings
		inv.setItem(2, Utils.createItem(Material.PLAYER_HEAD, Utils.format("&d&lPlayer Settings")));

		// Option to edit mob settings
		inv.setItem(3, Utils.createItem(Material.ZOMBIE_SPAWN_EGG, Utils.format("&2&lMob Settings")));

		// Option to edit shop settings
		inv.setItem(4, Utils.createItem(Material.GOLD_BLOCK, Utils.format("&e&lShop Settings")));

		// Option to edit miscellaneous game settings
		inv.setItem(5, Utils.createItem(Material.REDSTONE, Utils.format("&7&lGame Settings")));

		// Option to close the arena
		String closed;
		if (arenaInstance.isClosed())
			closed = "&c&lCLOSED";
		else closed = "&a&lOPEN";
		inv.setItem(6, Utils.createItem(Material.NETHER_BRICK_FENCE, Utils.format("&9&lClose Arena: " + closed)));

		// Option to remove arena
		inv.setItem(7, ii.remove("ARENA"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing an arena
	public Inventory createArenaConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove " + game.arenas.get(arena).getName() + '?'));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the portal of an arena
	public Inventory createPortalInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lPortal: " + game.arenas.get(arena).getName()));

		// Option to create the portal
		inv.setItem(0, ii.create("Portal"));

		// Option to teleport to the portal
		inv.setItem(2, ii.teleport("Portal"));

		// Option to center the portal
		inv.setItem(4, ii.center("Portal"));

		// Option to remove the portal
		inv.setItem(6, ii.remove("PORTAL"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing the arena portal
	public Inventory createPortalConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Portal?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the player settings of an arena
	public Inventory createPlayersInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Settings: " + game.arenas.get(arena).getName()));

		// Option to edit player spawn
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&5&lPlayer Spawn")));

		// Option to toggle player spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lToggle Spawn Particles"),
				Utils.format("&7Particles showing where the spawn is"),
				Utils.format("&7(Visible in-game)"),
				Utils.format(CONSTRUCTION)));

		// Option to edit waiting room
		inv.setItem(2, Utils.createItem(Material.CLOCK, Utils.format("&b&lWaiting Room"),
				Utils.format("&7An optional room to wait in before"), Utils.format("&7the game starts")));

		// Option to edit max players
		inv.setItem(3, Utils.createItem(Material.NETHERITE_HELMET,
				Utils.format("&4&lMaximum Players"),
				FLAGS,
				null,
				Utils.format("&7Maximum players the game will have")));

		// Option to edit min players
		inv.setItem(4, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum Players"),
				FLAGS,
				null,
				Utils.format("&7Minimum players needed for game to start")));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the player spawn of an arena
	public Inventory createPlayerSpawnInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Spawn: " + game.arenas.get(arena).getName()));

		// Option to create player spawn
		inv.setItem(0, ii.create("Spawn"));

		// Option to teleport to player spawn
		inv.setItem(2, ii.teleport("Spawn"));

		// Option to center the player spawn
		inv.setItem(4, ii.center("Spawn"));

		// Option to remove player spawn
		inv.setItem(6, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing player spawn
	public Inventory createSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Spawn?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the waiting room of an arena
	public Inventory createWaitingRoomInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&b&lWaiting Room: " + game.arenas.get(arena).getName()));

		// Option to create waiting room
		inv.setItem(0, ii.create("Waiting Room"));

		// Option to teleport to waiting room
		inv.setItem(2, ii.teleport("Waiting Room"));

		// Option to center the waiting room
		inv.setItem(4, ii.center("Waiting Room"));

		// Option to remove waiting room
		inv.setItem(6, ii.remove("WAITING ROOM"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing waiting room
	public Inventory createWaitingConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Waiting Room?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for changing max players in an arena
	public Inventory createMaxPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lMaximum Players: " + game.arenas.get(arena).getMaxPlayers()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, Utils.createItem(Material.RED_CONCRETE, Utils.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for changing min players in an arena
	public Inventory createMinPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMinimum Players: " + game.arenas.get(arena).getMinPlayers()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, Utils.createItem(Material.RED_CONCRETE, Utils.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the mob settings of an arena
	public Inventory createMobsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMob Settings: " + game.arenas.get(arena).getName()));

		// Option to edit monster spawns
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&2&lMonster Spawns")));

		// Option to toggle monster spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&a&lToggle Monster Spawn Particles"),
				Utils.format("&7Particles showing where the spawns are"),
				Utils.format("&7(Visible in-game)"),
				Utils.format(CONSTRUCTION)));

		// Option to edit villager spawns
		inv.setItem(2, Utils.createItem(Material.END_PORTAL_FRAME,
				Utils.format("&5&lVillager Spawns")));

		// Option to toggle villager spawn particles
		inv.setItem(3, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lToggle Villager Spawn Particles"),
				Utils.format("&7Particles showing where the spawns are"),
				Utils.format("&7(Visible in-game)"),
				Utils.format(CONSTRUCTION)));

		// Option to edit monsters allowed
		inv.setItem(4, Utils.createItem(Material.DRAGON_HEAD,
				Utils.format("&3&lMonsters Allowed"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic monster count
		inv.setItem(5, Utils.createItem(Material.SLIME_BALL,
				Utils.format("&e&lToggle Dynamic Monster Count"),
				Utils.format("&7Monster count adjusts based on number of players"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic difficulty
		inv.setItem(6, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lToggle Dynamic Difficulty"),
				Utils.format("&7Difficulty adjusts based on number of players"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the monster spawns of an arena
	public Inventory createMonsterSpawnInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawns: " + arenaInstance.getName()));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible mob spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (!plugin.getArenaData().contains("a" + arena + ".monster." + i))
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(MONSTER_MATS[index], Utils.format("&2&lMob Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing a specific monster spawn of an arena
	public Inventory createMonsterSpawnMenu(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawn " + slot + ": " + game.arenas.get(arena).getName()));

		// Option to create monster spawn
		inv.setItem(0, ii.create("Spawn"));

		// Option to teleport to monster spawn
		inv.setItem(2, ii.teleport("Spawn"));

		// Option to center the monster spawn
		inv.setItem(4, ii.center("Spawn"));

		// Option to remove monster spawn
		inv.setItem(6, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing monster spawns
	public Inventory createMonsterSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Monster Spawn?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the villager spawns of an arena
	public Inventory createVillagerSpawnInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lVillager Spawns: " + arenaInstance.getName()));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible villager spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (!plugin.getArenaData().contains("a" + arena + ".villager." + i))
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(VILLAGER_MATS[index], Utils.format("&5&lVillager Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing a specific villager spawn of an arena
	public Inventory createVillagerSpawnMenu(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lVillager Spawn " + slot + ": " + game.arenas.get(arena).getName()));

		// Option to create villager spawn
		inv.setItem(0, ii.create("Spawn"));

		// Option to teleport to villager spawn
		inv.setItem(2, ii.teleport("Spawn"));

		// Option to center the villager spawn
		inv.setItem(4, ii.center("Spawn"));

		// Option to remove villager spawn
		inv.setItem(6, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing mob spawns
	public Inventory createVillagerSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Villager Spawn?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the shop settings of an arena
	public Inventory createShopsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&e&lShop Settings: " + game.arenas.get(arena).getName()));

		// Option to create a custom shop
		inv.setItem(0, Utils.createItem(Material.EMERALD,
				Utils.format("&a&lCreate Custom Shop"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle default shop
		inv.setItem(2, Utils.createItem(Material.GOLD_BLOCK,
				Utils.format("&6&lToggle Default Shop"),
				Utils.format("&7Turn default shop on and off"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle custom shop
		inv.setItem(4, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&2&lToggle Custom Shop"),
				Utils.format("&7Turn custom shop on and off"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic prices
		inv.setItem(6, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lToggle Dynamic Prices"),
				Utils.format("&7Prices adjusting based on number of"),
				Utils.format("&7players in the game"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the game settings of an arena
	public Inventory createGameSettingsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&8&lGame Settings: " + game.arenas.get(arena).getName()));

		// Option to change max waves
		inv.setItem(0, Utils.createItem(Material.NETHERITE_SWORD,
				Utils.format("&3&lMax Waves"),
				FLAGS,
				null));

		// Option to wave time limit
		inv.setItem(1, Utils.createItem(Material.CLOCK, Utils.format("&2&lWave Time Limit")));

		// Option to toggle dynamic wave time limit
		inv.setItem(2, Utils.createItem(Material.SNOWBALL,
				Utils.format("&a&lToggle Dynamic Time Limit"),
				Utils.format("&7Wave time limit adjusts based on"),
				Utils.format("&7in-game difficulty"),
				Utils.format(CONSTRUCTION)));

		// Option to edit allowed kits
		inv.setItem(3, Utils.createItem(Material.ENDER_CHEST,
				Utils.format("&9&lAllowed Kits"),
				Utils.format(CONSTRUCTION)));

		// Option to edit persistent rewards
		inv.setItem(4, Utils.createItem(Material.EMERALD,
				Utils.format("&b&lPersistent Rewards"),
				Utils.format(CONSTRUCTION)));

		// Option to edit sounds
		inv.setItem(5, Utils.createItem(Material.MUSIC_DISC_13,
				Utils.format("&d&lSounds"),
				FLAGS,
				null));

		// Option to adjust overall difficulty multiplier
		inv.setItem(6, Utils.createItem(Material.TURTLE_HELMET,
				Utils.format("&4&lDifficulty Multiplier"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to copy game settings from another arena
		inv.setItem(7, Utils.createItem(Material.WRITABLE_BOOK,
				Utils.format("&7&lCopy Game Settings"),
				Utils.format("&7Copy settings of another arena excluding"),
				Utils.format("&7location data, arena name, and closed status"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for changing max waves of an arena
	public Inventory createMaxWaveInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (game.arenas.get(arena).getMaxWaves() < 0)
			inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
					Utils.format("&3&lMaximum Waves: Unlimited"));
		else inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&3&lMaximum Waves: " + game.arenas.get(arena).getMaxWaves()));

		// Option to decrease
		for (int i = 0; i < 3; i++)
			inv.setItem(i, Utils.createItem(Material.RED_CONCRETE, Utils.format("&4&lDecrease")));

		// Option to set to unlimited
		inv.setItem(3, Utils.createItem(Material.ORANGE_CONCRETE, Utils.format("&6&lUnlimited")));

		// Option to reset to 1
		inv.setItem(4, Utils.createItem(Material.LIGHT_BLUE_CONCRETE, Utils.format("&3&lReset to 1")));

		// Option to increase
		for (int i = 5; i < 8; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for changing wave time limit of an arena
	public Inventory createWaveTimeLimitInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (game.arenas.get(arena).getWaveTimeLimit() < 0)
			inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
					Utils.format("&2&lWave Time Limit: Unlimited"));
		else inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lWave Time Limit: " + game.arenas.get(arena).getWaveTimeLimit()));

		// Option to decrease
		for (int i = 0; i < 3; i++)
			inv.setItem(i, Utils.createItem(Material.RED_CONCRETE, Utils.format("&4&lDecrease")));

		// Option to set to unlimited
		inv.setItem(3, Utils.createItem(Material.ORANGE_CONCRETE, Utils.format("&6&lUnlimited")));

		// Option to reset to 1
		inv.setItem(4, Utils.createItem(Material.LIGHT_BLUE_CONCRETE, Utils.format("&3&lReset to 1")));

		// Option to increase
		for (int i = 5; i < 8; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the sounds of an arena
	public Inventory createSoundsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lSounds: " + game.arenas.get(arena).getName()));

		// Option to edit win sound
		inv.setItem(0, Utils.createItem(Material.MUSIC_DISC_PIGSTEP,
				Utils.format("&a&lWin"),
				FLAGS,
				null,
				Utils.format("&7Played when game ends and players win"),
				Utils.format(CONSTRUCTION)));

		// Option to edit lose sound
		inv.setItem(1, Utils.createItem(Material.MUSIC_DISC_11,
				Utils.format("&e&lLose"),
				FLAGS,
				null,
				Utils.format("&7Played when game ends and players lose"),
				Utils.format(CONSTRUCTION)));

		// Option to edit wave start sound
		inv.setItem(2, Utils.createItem(Material.MUSIC_DISC_CAT,
				Utils.format("&2&lWave Start"),
				FLAGS,
				null,
				Utils.format("&7Played when a wave starts"),
				Utils.format(CONSTRUCTION)));

		// Option to edit wave finish sound
		inv.setItem(3, Utils.createItem(Material.MUSIC_DISC_BLOCKS,
				Utils.format("&4&lWave Finish"),
				FLAGS,
				null,
				Utils.format("&7Played when a wave ends"),
				Utils.format(CONSTRUCTION)));

		// Option to edit waiting music
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_MELLOHI,
				Utils.format("&6&lWaiting Music"),
				FLAGS,
				null,
				Utils.format("&7Played while players wait for game to start"),
				Utils.format(CONSTRUCTION)));

		// Option to edit gem pickup sound
		inv.setItem(5, Utils.createItem(Material.MUSIC_DISC_FAR,
				Utils.format("&b&lGem Pickup Music"),
				FLAGS,
				null,
				Utils.format("&7Played when players pick up gems"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Generate the shop menu
	public static Inventory createShop() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lItem Shop"));

		inv.setItem(1, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&4&lWeapon Shop"),
				FLAGS, null));

		inv.setItem(3, Utils.createItem(Material.GOLDEN_CHESTPLATE, Utils.format("&5&lArmor Shop"),
				FLAGS, null));

		inv.setItem(5, Utils.createItem(Material.GOLDEN_APPLE, Utils.format("&3&lConsumables Shop")));

		inv.setItem(7, Utils.createItem(Material.QUARTZ, Utils.format("&6&lCustom Shop"),
				Utils.format(CONSTRUCTION)));

		return inv;
	}

	// Generate the weapon shop
	public static Inventory createWeaponShop(int level) {
		Random r = new Random();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&4&lWeapon Shop"));

		// Fill in weapons
		for (int i = 0; i < 9; i++) {
			double chance = r.nextDouble();
			switch (level) {
				case 1:
					if (chance < .4)
						inv.setItem(i, GameItems.sword(level));
					else if (chance < .75)
						inv.setItem(i, GameItems.axe(level));
					else if (chance < .9)
						inv.setItem(i, GameItems.bow(level));
					else inv.setItem(i, GameItems.arrows());
					break;
				case 2:
					if (chance < .35)
						inv.setItem(i, GameItems.sword(level));
					else if (chance < .7)
						inv.setItem(i, GameItems.axe(level));
					else if (chance < .8)
						inv.setItem(i, GameItems.shield(level));
					else if (chance < .9)
						inv.setItem(i, GameItems.bow(level));
					else if (chance < .94)
						inv.setItem(i, GameItems.arrows());
					else if (chance < .97)
						inv.setItem(i, GameItems.arrowsS());
					else inv.setItem(i, GameItems.arrowsP());
					break;
				case 3:
					if (chance < .3)
						inv.setItem(i, GameItems.sword(level));
					else if (chance < .6)
						inv.setItem(i, GameItems.axe(level));
					else if (chance < .7)
						inv.setItem(i, GameItems.bow(level));
					else if (chance < .8)
						inv.setItem(i, GameItems.shield(level));
					else if (chance < .9)
						inv.setItem(i, GameItems.crossbow(level));
					else if (chance < .93)
						inv.setItem(i, GameItems.arrows());
					else if (chance < .96)
						inv.setItem(i, GameItems.arrowsS());
					else if (chance < .98)
						inv.setItem(i, GameItems.arrowsP());
					else inv.setItem(i, GameItems.arrowsW());
					break;
				case 4:
					if (chance < .3)
						inv.setItem(i, GameItems.sword(level));
					else if (chance < .55)
						inv.setItem(i, GameItems.axe(level));
					else if (chance < .65)
						inv.setItem(i, GameItems.bow(level));
					else if (chance < .75)
						inv.setItem(i, GameItems.shield(level));
					else if (chance < .85)
						inv.setItem(i, GameItems.crossbow(level));
					else if (chance < .88)
						inv.setItem(i, GameItems.arrows());
					else if (chance < .91)
						inv.setItem(i, GameItems.arrowsS());
					else if (chance < .94)
						inv.setItem(i, GameItems.arrowsP());
					else if (chance < .96)
						inv.setItem(i, GameItems.arrowsW());
					else if (chance < .98)
						inv.setItem(i, GameItems.arrowsD());
					else inv.setItem(i, GameItems.rockets());
					break;
				default:
					if (chance < .2)
						inv.setItem(i, GameItems.sword(level));
					else if (chance < .4)
						inv.setItem(i, GameItems.axe(level));
					else if (chance < .5)
						inv.setItem(i, GameItems.shield(level));
					else if (chance < .6)
						inv.setItem(i, GameItems.bow(level));
					else if (chance < .7)
						inv.setItem(i, GameItems.crossbow(level));
					else if (chance < .8)
						inv.setItem(i, GameItems.trident(level));
					else if (chance < .84)
						inv.setItem(i, GameItems.arrows());
					else if (chance < .88)
						inv.setItem(i, GameItems.arrowsS());
					else if (chance < .91)
						inv.setItem(i, GameItems.arrowsP());
					else if (chance < .94)
						inv.setItem(i, GameItems.arrowsW());
					else if (chance < .96)
						inv.setItem(i, GameItems.arrowsD());
					else if (chance < .98)
						inv.setItem(i, GameItems.rockets());
					else inv.setItem(i, GameItems.rocketsPlus());
			}
		}

		// Return option
		inv.setItem(13, Utils.createItem(Material.BARRIER, Utils.format("&c&lRETURN")));

		return inv;
	}

	// Generate the armor shop
	public static Inventory createArmorShop(int level) {
		Random r = new Random();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 27, Utils.format("&k") +
				Utils.format("&5&lArmor Shop"));

		// Fill in armor
		for (int i = 0; i < 18; i++) {
			int chance = r.nextInt(4);
			switch (chance) {
				case 0:
					inv.setItem(i, GameItems.helmet(level));
					break;
				case 1:
					inv.setItem(i, GameItems.chestplate(level));
					break;
				case 2:
					inv.setItem(i, GameItems.leggings(level));
					break;
				case 3:
					inv.setItem(i, GameItems.boots(level));
			}
		}

		// Return option
		inv.setItem(22, Utils.createItem(Material.BARRIER, Utils.format("&c&lRETURN")));

		return inv;
	}

	// Generate the consumables shop
	public static Inventory createConsumablesShop(int level) {
		Random r = new Random();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&3&lConsumables Shop"));

		// Fill in consumables
		for (int i = 0; i < 9; i++) {
			double chance = r.nextDouble();
			switch (level) {
				case 1:
					if (chance < .3)
						inv.setItem(i, GameItems.beetroot());
					else if (chance < .6)
						inv.setItem(i, GameItems.carrot());
					else if (chance < .75)
						inv.setItem(i, GameItems.bread());
					else if (chance < .9)
						inv.setItem(i, GameItems.health());
					else inv.setItem(i, GameItems.speed());
					break;
				case 2:
					if (chance < .1)
						inv.setItem(i, GameItems.beetroot());
					else if (chance < .3)
						inv.setItem(i, GameItems.carrot());
					else if (chance < .5)
						inv.setItem(i, GameItems.bread());
					else if (chance < .65)
						inv.setItem(i, GameItems.steak());
					else if (chance < .8)
						inv.setItem(i, GameItems.health());
					else if (chance < .9)
						inv.setItem(i, GameItems.speed());
					else inv.setItem(i, GameItems.strength());
					break;
				case 3:
					if (chance < .1)
						inv.setItem(i, GameItems.carrot());
					else if (chance < .3)
						inv.setItem(i, GameItems.bread());
					else if (chance < .5)
						inv.setItem(i, GameItems.steak());
					else if (chance < .7)
						inv.setItem(i, GameItems.health());
					else if (chance < .85)
						inv.setItem(i, GameItems.speed());
					else if (chance < .95)
						inv.setItem(i, GameItems.strength());
					else inv.setItem(i, GameItems.gcarrot());
					break;
				case 4:
					if (chance < .1)
						inv.setItem(i, GameItems.bread());
					else if (chance < .3)
						inv.setItem(i, GameItems.steak());
					else if (chance < .5)
						inv.setItem(i, GameItems.health());
					else if (chance < .7)
						inv.setItem(i, GameItems.speed());
					else if (chance < .85)
						inv.setItem(i, GameItems.strength());
					else if (chance < .95)
						inv.setItem(i, GameItems.gcarrot());
					else inv.setItem(i, GameItems.gapple());
					break;
				case 5:
					if (chance < .15)
						inv.setItem(i, GameItems.steak());
					else if (chance < .35)
						inv.setItem(i, GameItems.health());
					else if (chance < .5)
						inv.setItem(i, GameItems.speed());
					else if (chance < .65)
						inv.setItem(i, GameItems.strength());
					else if (chance < .75)
						inv.setItem(i, GameItems.gcarrot());
					else if (chance < .85)
						inv.setItem(i, GameItems.gapple());
					else if (chance < .95)
						inv.setItem(i, GameItems.egapple());
					else inv.setItem(i, GameItems.totem());
					break;
				default:
					if (chance < .1)
						inv.setItem(i, GameItems.steak());
					else if (chance < .3)
						inv.setItem(i, GameItems.health());
					else if (chance < .5)
						inv.setItem(i, GameItems.speed());
					else if (chance < .6)
						inv.setItem(i, GameItems.strength());
					else if (chance < .7)
						inv.setItem(i, GameItems.gcarrot());
					else if (chance < .8)
						inv.setItem(i, GameItems.gapple());
					else if (chance < .9)
						inv.setItem(i, GameItems.egapple());
					else inv.setItem(i, GameItems.totem());
			}
		}

		// Return option
		inv.setItem(13, Utils.createItem(Material.BARRIER, Utils.format("&c&lRETURN")));

		return inv;
	}

	// Display player stats
	public Inventory createPlayerStatsInventory(String name) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&l" + name + "'s Stats"));

		FileConfiguration playerData = plugin.getPlayerData();

		// Total kills
		inv.setItem(0, Utils.createItem(Material.DRAGON_HEAD, Utils.format("&4&lTotal Kills: &4" +
				playerData.getInt(name + ".totalKills")), Utils.format("&7Lifetime kill count")));

		// Top kills
		inv.setItem(1, Utils.createItem(Material.ZOMBIE_HEAD, Utils.format("&c&lTop Kills: &c" +
				playerData.getInt(name + ".topKills")), Utils.format("&7Most kills in a game")));

		// Total gems
		inv.setItem(2, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&2&lTotal Gems: &2" +
				playerData.getInt(name + ".totalGems")), Utils.format("&7Lifetime gems collected")));

		// Top balance
		inv.setItem(3, Utils.createItem(Material.EMERALD, Utils.format("&a&lTop Balance: &a" +
				playerData.getInt(name + ".topBalance")),
				Utils.format("&7Highest gem balance in a game")));

		// Top wave
		inv.setItem(4, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&9&lTop Wave: &9" +
				playerData.getInt(name + ".topWave")), FLAGS, null,
				Utils.format("&7Highest completed wave")));

		// Crystal balance
		inv.setItem(8, Utils.createItem(Material.DIAMOND, Utils.format("&b&lCrystal Balance: &b" +
				playerData.getInt(name + ".crystalBalance"))));

		return inv;
	}

	// Display arena information
	public Inventory createArenaInfoInventory(Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&6&l" + arena.getName() + " Info"));

		// Maximum players
		inv.setItem(1, Utils.createItem(Material.NETHERITE_HELMET,
				Utils.format("&4&lMaximum players: &4" + arena.getMaxPlayers()), FLAGS, null,
				Utils.format("&7The most players an arena can have")));

		// Minimum players
		inv.setItem(3, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum players: &2" + arena.getMinPlayers()), FLAGS, null,
				Utils.format("&7The least players an arena can have to start")));

		// Max waves
		inv.setItem(5, Utils.createItem(Material.GOLDEN_SWORD,
				Utils.format("&3&lMax waves: &3" + arena.getMaxWaves()), FLAGS, null,
				Utils.format("&7The highest wave the arena will go to")));

		// Wave time limit
		inv.setItem(7, Utils.createItem(Material.CLOCK,
				Utils.format("&9&lWave time limit: &9" + arena.getWaveTimeLimit() + " minute(s)"),
				Utils.format("&7The time limit for each wave before"), Utils.format("&7the game ends")));

		return inv;
	}
}
