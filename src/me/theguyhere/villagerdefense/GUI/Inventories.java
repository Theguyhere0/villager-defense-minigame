package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.game.models.Kits;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Inventories {
	private final Main plugin;
	private final Game game;
	private final Kits kits = new Kits();

	public Inventories (Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}

	// Easily get alphabet
	public static final char[] NAMES = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
		's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' '
	};

	// Easily alternate between different materials
	public static final Material[] KEY_MATS = {Material.BLACK_CONCRETE, Material.WHITE_WOOL};
	public static final Material[] INFO_BOARD_MATS = {Material.DARK_OAK_SIGN, Material.BIRCH_SIGN};
	public static final Material[] MONSTER_MATS = {Material.SKELETON_SKULL, Material.ZOMBIE_HEAD};
	public static final Material[] VILLAGER_MATS = {Material.WITHER_ROSE, Material.POPPY};

	// Button creation constants
//	final static String CONSTRUCTION = "&fComing Soon!";
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
		inv.setItem(53, InventoryItems.exit());
		
		return inv;
	}

	// Menu for lobby
	public Inventory createLobbyInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lLobby"));

		// Option to create the lobby
		inv.setItem(0, InventoryItems.create("Lobby"));

		// Option to teleport to the lobby
		inv.setItem(2, InventoryItems.teleport("Lobby"));

		// Option to center the lobby
		inv.setItem(4, InventoryItems.center("Lobby"));

		// Option to remove the lobby
		inv.setItem(6, InventoryItems.remove("LOBBY"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing lobby
	public Inventory createLobbyConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Lobby?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

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
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific info board
	public Inventory createInfoBoardMenu(int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&6&lInfo Board " + slot));

		// Option to create info board
		inv.setItem(0, InventoryItems.create("Info Board"));

		// Option to teleport to info board
		inv.setItem(2, InventoryItems.teleport("Info Board"));

		// Option to center the info board
		inv.setItem(4, InventoryItems.center("Info Board"));

		// Option to remove info board
		inv.setItem(6, InventoryItems.remove("INFO BOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing info boards
	public Inventory createInfoBoardConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Info Board?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

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
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the total kills leaderboard
	public Inventory createTotalKillsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lTotal Kills Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, InventoryItems.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the top kills leaderboard
	public Inventory createTopKillsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&c&lTop Kills Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, InventoryItems.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the total gems leaderboard
	public Inventory createTotalGemsLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lTotal Gems Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, InventoryItems.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the top balance leaderboard
	public Inventory createTopBalanceLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&a&lTop Balance Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, InventoryItems.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the top wave leaderboard
	public Inventory createTopWaveLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&9&lTop Wave Leaderboard"));

		// Option to create the leaderboard
		inv.setItem(0, InventoryItems.create("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(2, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(4, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(6, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for total kills leaderboard
	public Inventory createTotalKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top kills leaderboard
	public Inventory createTopKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for total gems leaderboard
	public Inventory createTotalGemsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Gems Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top balance leaderboard
	public Inventory createTopBalanceConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Balance Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top wave leaderboard
	public Inventory createTopWaveConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Wave Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

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

		// Option to edit game portal and leaderboard
		inv.setItem(1, Utils.createItem(Material.END_PORTAL_FRAME,
				Utils.format("&5&lPortal and Leaderboard")));

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
		inv.setItem(7, InventoryItems.remove("ARENA"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing an arena
	public Inventory createArenaConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove " + game.arenas.get(arena).getName() + '?'));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the portal and leaderboard of an arena
	public Inventory createPortalInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lPortal/LBoard: " + arenaInstance.getName()));

		// Option to create or relocate the portal
		if (arenaInstance.getPortal() == null)
			inv.setItem(0, InventoryItems.create("Portal"));
		else inv.setItem(0, InventoryItems.relocate("Portal"));

		// Option to teleport to the portal
		inv.setItem(1, InventoryItems.teleport("Portal"));

		// Option to center the portal
		inv.setItem(2, InventoryItems.center("Portal"));

		// Option to remove the portal
		inv.setItem(3, InventoryItems.remove("PORTAL"));

		// Option to create or relocate the leaderboard
		if (arenaInstance.getArenaBoard() == null)
			inv.setItem(4, InventoryItems.create("Leaderboard"));
		else inv.setItem(4, InventoryItems.relocate("Leaderboard"));

		// Option to teleport to the leaderboard
		inv.setItem(5, InventoryItems.teleport("Leaderboard"));

		// Option to center the leaderboard
		inv.setItem(6, InventoryItems.center("Leaderboard"));

		// Option to remove the leaderboard
		inv.setItem(7, InventoryItems.remove("LEADERBOARD"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing the arena portal
	public Inventory createPortalConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Portal?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for removing the arena leaderboard
	public Inventory createArenaBoardConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the player settings of an arena
	public Inventory createPlayersInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Settings: " + arenaInstance.getName()));

		// Option to edit player spawn
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&5&lPlayer Spawn")));

		// Option to toggle player spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lSpawn Particles: " + getToggleStatus(arenaInstance.hasSpawnParticles())),
				Utils.format("&7Particles showing where the spawn is"),
				Utils.format("&7(Visible in-game)")));

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
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the player spawn of an arena
	public Inventory createPlayerSpawnInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Spawn: " + arenaInstance.getName()));

		// Option to create or relocate player spawn
		if (arenaInstance.getPlayerSpawn() == null)
			inv.setItem(0, InventoryItems.create("Spawn"));
		else inv.setItem(0, InventoryItems.relocate("Spawn"));

		// Option to teleport to player spawn
		inv.setItem(2, InventoryItems.teleport("Spawn"));

		// Option to center the player spawn
		inv.setItem(4, InventoryItems.center("Spawn"));

		// Option to remove player spawn
		inv.setItem(6, InventoryItems.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing player spawn
	public Inventory createSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the waiting room of an arena
	public Inventory createWaitingRoomInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&b&lWaiting Room: " + arenaInstance.getName()));

		// Option to create waiting room
		if (arenaInstance.getWaitingRoom() == null)
			inv.setItem(0, InventoryItems.create("Waiting Room"));
		else inv.setItem(0, InventoryItems.relocate("Waiting Room"));

		// Option to teleport to waiting room
		inv.setItem(2, InventoryItems.teleport("Waiting Room"));

		// Option to center the waiting room
		inv.setItem(4, InventoryItems.center("Waiting Room"));

		// Option to remove waiting room
		inv.setItem(6, InventoryItems.remove("WAITING ROOM"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing waiting room
	public Inventory createWaitingConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Waiting Room?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

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
		inv.setItem(8, InventoryItems.exit());

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
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the mob settings of an arena
	public Inventory createMobsInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMob Settings: " + arenaInstance.getName()));

		// Option to edit monster spawns
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&2&lMonster Spawns")));

		// Option to toggle monster spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&a&lMonster Spawn Particles: " + getToggleStatus(arenaInstance.hasMonsterParticles())),
				Utils.format("&7Particles showing where the spawns are"),
				Utils.format("&7(Visible in-game)")));

		// Option to edit villager spawns
		inv.setItem(2, Utils.createItem(Material.END_PORTAL_FRAME,
				Utils.format("&5&lVillager Spawns")));

		// Option to toggle villager spawn particles
		inv.setItem(3, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lVillager Spawn Particles: " + getToggleStatus(arenaInstance.hasVillagerParticles())),
				Utils.format("&7Particles showing where the spawns are"),
				Utils.format("&7(Visible in-game)")));

		// Option to edit spawn table
		inv.setItem(4, Utils.createItem(Material.DRAGON_HEAD, Utils.format("&3&lSpawn Table")));

		// Option to toggle dynamic mob count
		inv.setItem(5, Utils.createItem(Material.SLIME_BALL,
				Utils.format("&e&lDynamic Mob Count: " + getToggleStatus(arenaInstance.hasDynamicCount())),
				Utils.format("&7Mob count adjusting based on"), Utils.format("&7number of players")));

		// Option to toggle dynamic difficulty
		inv.setItem(6, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lDynamic Difficulty: " + getToggleStatus(arenaInstance.hasDynamicDifficulty())),
				Utils.format("&7Difficulty adjusting based on"), Utils.format("&7number of players")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

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
			if (arenaInstance.getMonsterSpawn(i) == null)
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(MONSTER_MATS[index], Utils.format("&2&lMob Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific monster spawn of an arena
	public Inventory createMonsterSpawnMenu(int arena, int slot) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawn " + (slot + 1) + ": " + arenaInstance.getName()));

		// Option to create or relocate monster spawn
		if (arenaInstance.getMonsterSpawn(slot) == null)
			inv.setItem(0, InventoryItems.create("Spawn"));
		else inv.setItem(0, InventoryItems.relocate("Spawn"));

		// Option to teleport to monster spawn
		inv.setItem(2, InventoryItems.teleport("Spawn"));

		// Option to center the monster spawn
		inv.setItem(4, InventoryItems.center("Spawn"));

		// Option to remove monster spawn
		inv.setItem(6, InventoryItems.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing monster spawns
	public Inventory createMonsterSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Monster Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

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
			if (arenaInstance.getVillagerSpawn(i) == null)
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(VILLAGER_MATS[index], Utils.format("&5&lVillager Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific villager spawn of an arena
	public Inventory createVillagerSpawnMenu(int arena, int slot) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lVillager Spawn " + (slot + 1) + ": " + arenaInstance.getName()));

		// Option to create or relocate villager spawn
		if (arenaInstance.getVillagerSpawn(slot) == null)
			inv.setItem(0, InventoryItems.create("Spawn"));
		else inv.setItem(0, InventoryItems.relocate("Spawn"));

		// Option to teleport to villager spawn
		inv.setItem(2, InventoryItems.teleport("Spawn"));

		// Option to center the villager spawn
		inv.setItem(4, InventoryItems.center("Spawn"));

		// Option to remove villager spawn
		inv.setItem(6, InventoryItems.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing mob spawns
	public Inventory createVillagerSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Villager Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Spawn table menu for an arena
	public Inventory createSpawnTableInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);
		Inventory inv;

		// Create inventory
		if (arenaInstance.getSpawnTableFile().equals("custom"))
			 inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
					Utils.format("&3&lSpawn Table: a" + arena + ".yml"));
		else inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&3&lSpawn Table: " + arenaInstance.getSpawnTableFile() + ".yml"));

		// Option to set spawn table to default
		inv.setItem(0, Utils.createItem(Material.OAK_WOOD, Utils.format("&4&lDefault"),
				Utils.format("&7Sets spawn table to default.yml")));

		// Option to set spawn table to global option 1
		inv.setItem(1, Utils.createItem(Material.RED_CONCRETE, Utils.format("&6&lOption 1"),
				Utils.format("&7Sets spawn table to option1.yml")));

		// Option to set spawn table to global option 2
		inv.setItem(2, Utils.createItem(Material.ORANGE_CONCRETE, Utils.format("&6&lOption 2"),
				Utils.format("&7Sets spawn table to option2.yml")));

		// Option to set spawn table to global option 3
		inv.setItem(3, Utils.createItem(Material.YELLOW_CONCRETE, Utils.format("&6&lOption 3"),
				Utils.format("&7Sets spawn table to option3.yml")));

		// Option to set spawn table to global option 4
		inv.setItem(4, Utils.createItem(Material.BROWN_CONCRETE, Utils.format("&6&lOption 4"),
				Utils.format("&7Sets spawn table to option4.yml")));

		// Option to set spawn table to global option 5
		inv.setItem(5, Utils.createItem(Material.LIGHT_GRAY_CONCRETE, Utils.format("&6&lOption 5"),
				Utils.format("&7Sets spawn table to option5.yml")));

		// Option to set spawn table to global option 6
		inv.setItem(6, Utils.createItem(Material.WHITE_CONCRETE, Utils.format("&6&lOption 6"),
				Utils.format("&7Sets spawn table to option6.yml")));

		// Option to set spawn table to custom option
		inv.setItem(7, Utils.createItem(Material.BIRCH_WOOD, Utils.format("&e&lCustom"),
				Utils.format("&7Sets spawn table to a[arena number].yml"),
				Utils.format("&7(Check the arena number in arenaData.yml)")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the shop settings of an arena
	public Inventory createShopsInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&e&lShop Settings: " + arenaInstance.getName()));

		// Option to create a custom shop
		inv.setItem(0, Utils.createItem(Material.QUARTZ,
				Utils.format("&a&lEdit Custom Shop")));

		// Option to toggle default shop
		inv.setItem(2, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&6&lDefault Shop: " + getToggleStatus(arenaInstance.hasNormal())),
				Utils.format("&7Turn default shop on and off")));

		// Option to toggle custom shop
		inv.setItem(4, Utils.createItem(Material.QUARTZ_BLOCK,
				Utils.format("&2&lCustom Shop: " + getToggleStatus(arenaInstance.hasCustom())),
				Utils.format("&7Turn custom shop on and off")));

		// Option to toggle dynamic prices
		inv.setItem(6, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lDynamic Prices: " + getToggleStatus(arenaInstance.hasDynamicPrices())),
				Utils.format("&7Prices adjusting based on number of"),
				Utils.format("&7players in the game")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the game settings of an arena
	public Inventory createGameSettingsInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&8&lGame Settings: " + arenaInstance.getName()));

		// Option to change max waves
		inv.setItem(0, Utils.createItem(Material.NETHERITE_SWORD,
				Utils.format("&3&lMax Waves"),
				FLAGS,
				null));

		// Option to wave time limit
		inv.setItem(1, Utils.createItem(Material.CLOCK, Utils.format("&2&lWave Time Limit")));

		// Option to toggle dynamic wave time limit
		inv.setItem(2, Utils.createItem(Material.SNOWBALL,
				Utils.format("&a&lDynamic Time Limit: " + getToggleStatus(arenaInstance.hasDynamicLimit())),
				Utils.format("&7Wave time limit adjusting based on"),
				Utils.format("&7in-game difficulty")));

		// Option to edit allowed kits
		inv.setItem(3, Utils.createItem(Material.ENDER_CHEST, Utils.format("&9&lAllowed Kits")));

		// Option to edit difficulty label
		inv.setItem(4, Utils.createItem(Material.NAME_TAG, Utils.format("&6&lDifficulty Label")));

		// Option to adjust overall difficulty multiplier
		inv.setItem(5, Utils.createItem(Material.TURTLE_HELMET,
				Utils.format("&4&lDifficulty Multiplier"),
				FLAGS,
				null,
				Utils.format("&7Determines difficulty increase rate")));

		// Option to edit sounds
		inv.setItem(6, Utils.createItem(Material.MUSIC_DISC_13,
				Utils.format("&d&lSounds"),
				FLAGS,
				null));

		// Option to copy game settings from another arena or a preset
		inv.setItem(7, Utils.createItem(Material.WRITABLE_BOOK,
				Utils.format("&f&lCopy Game Settings"),
				Utils.format("&7Copy settings of another arena or"),
				Utils.format("&7choose from a menu of presets")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

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
		inv.setItem(8, InventoryItems.exit());

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
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for allowed kits of an arena
	public Inventory createAllowedKitsInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&9&lAllowed Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefit"), Utils.format("&7per game or respawn")));

		if (!arenaInstance.getBannedKits().contains("Orc"))
			inv.setItem(9, Utils.createItem(Material.STICK, Utils.format("&a&lOrc"), FLAGS, enchants,
					Utils.format("&7Start with a Knockback V stick")));
		else inv.setItem(9, Utils.createItem(Material.STICK, Utils.format("&4&LOrc"),
				Utils.format("&7Start with a Knockback V stick")));

		if (!arenaInstance.getBannedKits().contains("Farmer"))
			inv.setItem(10, Utils.createItem(Material.CARROT, Utils.format("&a&lFarmer"), FLAGS, enchants,
					Utils.format("&7Start with 5 carrots")));
		else inv.setItem(10, Utils.createItem(Material.CARROT, Utils.format("&4&LFarmer"),
				Utils.format("&7Start with 5 carrots")));

		if (!arenaInstance.getBannedKits().contains("Soldier"))
			inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&a&lSoldier"), FLAGS,
					enchants, Utils.format("&7Start with a stone sword")));
		else inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&4&LSoldier"), FLAGS,
				null, Utils.format("&7Start with a stone sword")));

		if (!arenaInstance.getBannedKits().contains("Tailor"))
			inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&a&lTailor"), FLAGS,
					enchants, Utils.format("&7Start with a full leather armor set")));
		else inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&4&LTailor"), FLAGS,
				null, Utils.format("&7Start with a full leather armor set")));

		if (!arenaInstance.getBannedKits().contains("Alchemist"))
			inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&a&lAlchemist"), FLAGS,
					enchants, Utils.format("&7Start with 1 speed and 2 healing"),
					Utils.format("&7splash potions")));
		else inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&4&LAlchemist"),
				Utils.format("&7Start with 1 speed and 2 healing"),
				Utils.format("&7splash potions")));

		if (!arenaInstance.getBannedKits().contains("Trader"))
			inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&a&lTrader"), FLAGS, enchants,
					Utils.format("&7Start with 200 gems")));
		else inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&4&LTrader"),
				Utils.format("&7Start with 200 gems")));

		if (!arenaInstance.getBannedKits().contains("Summoner"))
			inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
					FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Start with a wolf spawn"),
					Utils.format("&fLevel 2"), Utils.format("&7Start with 2 wolf spawns"),
					Utils.format("&fLevel 3"), Utils.format("&7Start with an iron golem spawn")));
		else inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&4&LSummoner"),
				Utils.format("&fLevel 1"), Utils.format("&7Start with a wolf spawn"),
				Utils.format("&fLevel 2"), Utils.format("&7Start with 2 wolf spawns"),
				Utils.format("&fLevel 3"), Utils.format("&7Start with an iron golem spawn")));

		if (!arenaInstance.getBannedKits().contains("Reaper"))
			inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
					enchants, Utils.format("&fLevel 1"),
					Utils.format("&7Start with a sharpness III netherite hoe"), Utils.format("&fLevel 2"),
					Utils.format("&7Start with a sharpness V netherite hoe"), Utils.format("&fLevel 3"),
					Utils.format("&7Start with a sharpness VIII netherite hoe")));
		else inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&4&LReaper"), FLAGS,
				null, Utils.format("&fLevel 1"),
				Utils.format("&7Start with a sharpness III netherite hoe"), Utils.format("&fLevel 2"),
				Utils.format("&7Start with a sharpness V netherite hoe"), Utils.format("&fLevel 3"),
				Utils.format("&7Start with a sharpness VIII netherite hoe")));

		if (!arenaInstance.getBannedKits().contains("Phantom"))
			inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&a&lPhantom"), FLAGS,
					enchants, Utils.format("&7Join as a player in any non-maxed game")));
		else inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&4&LPhantom"),
				Utils.format("&7Join as a player in any non-maxed game")));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		if (!arenaInstance.getBannedKits().contains("Mage"))
			inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Shoot a fireball"),
					Utils.format("&7(Cooldown 1 second)"), Utils.format("&fLevel 2"),
					Utils.format("&7Shoot a strong fireball"), Utils.format("&7(Cooldown 2 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Shoot a very strong fireball"),
					Utils.format("&7(Cooldown 3 seconds)")));
		else inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&4&LMage"),
				Utils.format("&fLevel 1"), Utils.format("&7Shoot a fireball"),
				Utils.format("&7(Cooldown 1 second)"), Utils.format("&fLevel 2"),
				Utils.format("&7Shoot a strong fireball"), Utils.format("&7(Cooldown 2 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Shoot a very strong fireball"),
				Utils.format("&7(Cooldown 3 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Ninja"))
			inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7You and your pets become invisible"), 
					Utils.format("&7and disarm nearby monsters for 10 seconds"),
					Utils.format("&7(Cooldown 30 seconds)"), Utils.format("&fLevel 2"),
					Utils.format("&7You and your pets become invisible"), 
					Utils.format("&7and disarm nearby monsters for 15 seconds"), 
					Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7You and your pets become invisible"),
					Utils.format("&7and disarm nearby monsters for 20 seconds"),
					Utils.format("&7(Cooldown 90 seconds)")));
		else inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&4&LNinja"),
				Utils.format("&fLevel 1"), Utils.format("&7You and your pets become invisible"),
				Utils.format("&7and disarm nearby monsters for 10 seconds"),
				Utils.format("&7(Cooldown 30 seconds)"), Utils.format("&fLevel 2"),
				Utils.format("&7You and your pets become invisible"),
				Utils.format("&7and disarm nearby monsters for 15 seconds"),
				Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7You and your pets become invisible"),
				Utils.format("&7and disarm nearby monsters for 20 seconds"),
				Utils.format("&7(Cooldown 90 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Templar"))
			inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
					enchants, Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7absorption I for 15 seconds,"),
					Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7absorption II for 15 seconds,"),
					Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7absorption III for 20 seconds,"),
					Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)")));
		else inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&4&LTemplar"), FLAGS,
				null, Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7absorption I for 15 seconds,"),
				Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7absorption II for 15 seconds,"),
				Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7absorption III for 20 seconds,"),
				Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Warrior"))
			inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
					enchants, Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7strength I for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7strength II for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7strength III for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)")));
		else inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&4&LWarrior"), FLAGS,
				null, Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7strength I for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7strength II for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7strength III for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Knight"))
			inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7resistance I for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7resistance II for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7resistance III for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)")));
		else inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&4&LKnight"),
				Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7resistance I for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7resistance II for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7resistance III for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Priest"))
			inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"), FLAGS,
					enchants, Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7regeneration I for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7regeneration II for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7regeneration III for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)")));
		else inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&4&LPriest"),
				Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7regeneration I for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7regeneration II for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7regeneration III for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Siren"))
			inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Give mobs within 3 blocks"),
					Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give mobs within 5 blocks"),
					Utils.format("&7weakness II for 10 seconds"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give mobs within 6 blocks"),
					Utils.format("&7weakness II for 10 seconds,"),
					Utils.format("&7slowness I for 10 seconds"), Utils.format("&7(Cooldown 80 seconds)")));
		else inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&4&LSiren"),
				Utils.format("&fLevel 1"), Utils.format("&7Give mobs within 3 blocks"),
				Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give mobs within 5 blocks"),
				Utils.format("&7weakness II for 10 seconds"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give mobs within 6 blocks"),
				Utils.format("&7weakness II for 10 seconds,"),
				Utils.format("&7slowness I for 10 seconds"), Utils.format("&7(Cooldown 80 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Monk"))
			inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7haste I for 15 seconds,"),
					Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7haste II for 15 seconds,"),
					Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7haste III for 20 seconds,"),
					Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)")));
		else inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&4&LMonk"),
				Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7haste I for 15 seconds,"),
				Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7haste II for 15 seconds,"),
				Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7haste III for 20 seconds,"),
				Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)")));

		if (!arenaInstance.getBannedKits().contains("Messenger"))
			inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
					Utils.format("&7speed I for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
					Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
					Utils.format("&7speed II for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
					Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
					Utils.format("&7speed III for 10 seconds,"),
					Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)")));
		else inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&4&lMessenger"),
				Utils.format("&fLevel 1"), Utils.format("&7Give all allies within 2.5 blocks"),
				Utils.format("&7speed I for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
				Utils.format("&fLevel 2"), Utils.format("&7Give all allies within 4 blocks"),
				Utils.format("&7speed II for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
				Utils.format("&fLevel 3"), Utils.format("&7Give all allies within 5 blocks"),
				Utils.format("&7speed III for 10 seconds,"),
				Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)")));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give player a special effect")));

		if (!arenaInstance.getBannedKits().contains("Blacksmith"))
			inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&e&lBlacksmith"), FLAGS, enchants,
					Utils.format("&7All equipment purchased are unbreakable")));
		else inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&4&LBlacksmith"),
				Utils.format("&7All equipment purchased are unbreakable")));

		if (!arenaInstance.getBannedKits().contains("Witch"))
			inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&e&lWitch"), FLAGS, enchants,
					Utils.format("&7All purchased potions become splash potions")));
		else inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&4&LWitch"),
				Utils.format("&7All purchased potions become splash potions")));

		if (!arenaInstance.getBannedKits().contains("Merchant"))
			inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&e&lMerchant"), FLAGS,
					enchants, Utils.format("&7Earn a 5% rebate on all purchases")));
		else inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&4&LMerchant"),
				Utils.format("&7Earn a 5% rebate on all purchases")));

		if (!arenaInstance.getBannedKits().contains("Vampire"))
			inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&e&lVampire"), FLAGS, enchants,
					Utils.format("&7Dealing x damage has an x% chance"),
					Utils.format("&7of healing half a heart")));
		else inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&4&LVampire"),
				Utils.format("&7Dealing x damage has an x% chance"),
				Utils.format("&7of healing half a heart")));

		if (!arenaInstance.getBannedKits().contains("Giant"))
			inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"), FLAGS, enchants,
					Utils.format("&fLevel 1"), Utils.format("&7Permanent 10% health boost"),
					Utils.format("&fLevel 2"), Utils.format("&7Permanent 20% health boost")));
		else inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&4&LGiant"),
				Utils.format("&fLevel 1"), Utils.format("&7Permanent 10% health boost"),
				Utils.format("&fLevel 2"), Utils.format("&7Permanent 20% health boost")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Menu for changing the difficulty label of an arena
	public Inventory createDifficultyLabelInventory(int arena) {
		String label = game.arenas.get(arena).getDifficultyLabel();
		if (label == null)
			label = "";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&6&lDifficulty Label: " + label));

		// "Easy" option
		inv.setItem(0, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&lEasy")));

		// "Medium" option
		inv.setItem(1, Utils.createItem(Material.YELLOW_CONCRETE, Utils.format("&e&lMedium")));

		// "Hard" option
		inv.setItem(2, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&lHard")));

		// "Insane" option
		inv.setItem(3, Utils.createItem(Material.MAGENTA_CONCRETE, Utils.format("&d&lInsane")));

		// "None" option
		inv.setItem(4, Utils.createItem(Material.LIGHT_GRAY_CONCRETE, Utils.format("&7&lNone")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for changing the difficulty multiplier of an arena
	public Inventory createDifficultyMultiplierInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lDifficulty Multiplier: " + game.arenas.get(arena).getDifficultyMultiplier()));

		// "1" option
		inv.setItem(0, Utils.createItem(Material.LIGHT_BLUE_CONCRETE, Utils.format("&b&l1")));

		// "2" option
		inv.setItem(2, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&l2")));

		// "3" option
		inv.setItem(4, Utils.createItem(Material.YELLOW_CONCRETE, Utils.format("&6&l3")));

		// "4" option
		inv.setItem(6, Utils.createItem(Material.RED_CONCRETE, Utils.format("&4&l4")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the sounds of an arena
	public Inventory createSoundsInventory(int arena) {
		Arena arenaInstance = game.arenas.get(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lSounds: " + game.arenas.get(arena).getName()));

		// Option to edit win sound
		inv.setItem(0, Utils.createItem(Material.MUSIC_DISC_PIGSTEP,
				Utils.format("&a&lWin Sound: " + getToggleStatus(arenaInstance.hasWinSound())),
				FLAGS,
				null,
				Utils.format("&7Played when game ends and players win")));

		// Option to edit lose sound
		inv.setItem(1, Utils.createItem(Material.MUSIC_DISC_11,
				Utils.format("&e&lLose Sound: " + getToggleStatus(arenaInstance.hasLoseSound())),
				FLAGS,
				null,
				Utils.format("&7Played when game ends and players lose")));

		// Option to edit wave start sound
		inv.setItem(2, Utils.createItem(Material.MUSIC_DISC_CAT,
				Utils.format("&2&lWave Start Sound: " + getToggleStatus(arenaInstance.hasWaveStartSound())),
				FLAGS,
				null,
				Utils.format("&7Played when a wave starts")));

		// Option to edit wave finish sound
		inv.setItem(3, Utils.createItem(Material.MUSIC_DISC_BLOCKS,
				Utils.format("&9&lWave Finish Sound: " + getToggleStatus(arenaInstance.hasWaveFinishSound())),
				FLAGS,
				null,
				Utils.format("&7Played when a wave ends")));

		// Option to edit waiting music
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_MELLOHI,
				Utils.format("&6&lWaiting Sound"),
				FLAGS,
				null,
				Utils.format("&7Played while players wait for game to start")));

		// Option to edit gem pickup sound
		inv.setItem(5, Utils.createItem(Material.MUSIC_DISC_FAR,
				Utils.format("&b&lGem Pickup Sound: " + getToggleStatus(arenaInstance.hasGemSound())),
				FLAGS,
				null,
				Utils.format("&7Played when players pick up gems")));

		// Option to edit player death sound
		inv.setItem(6, Utils.createItem(Material.MUSIC_DISC_CHIRP,
				Utils.format("&4&lPlayer Death Sound: " + getToggleStatus(arenaInstance.hasPlayerDeathSound())),
				FLAGS,
				null,
				Utils.format("&7Played when a player dies")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the win sound of an arena
	public Inventory createWaitSoundInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&6&lWaiting Sound: " + game.arenas.get(arena).getWaitingSoundName()));

		// Sound options
		inv.setItem(0, Utils.createItem(Material.MUSIC_DISC_CAT, null));
		inv.setItem(1, Utils.createItem(Material.MUSIC_DISC_BLOCKS, null));
		inv.setItem(2, Utils.createItem(Material.MUSIC_DISC_FAR, null));
		inv.setItem(3, Utils.createItem(Material.MUSIC_DISC_STRAD, null));
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_MELLOHI, null));
		inv.setItem(5, Utils.createItem(Material.MUSIC_DISC_WARD, null));

		inv.setItem(9, Utils.createItem(Material.MUSIC_DISC_CHIRP, null));
		inv.setItem(10, Utils.createItem(Material.MUSIC_DISC_STAL, null));
		inv.setItem(11, Utils.createItem(Material.MUSIC_DISC_MALL, null));
		inv.setItem(12, Utils.createItem(Material.MUSIC_DISC_WAIT, null));
		inv.setItem(13, Utils.createItem(Material.MUSIC_DISC_PIGSTEP, null));
		inv.setItem(14, Utils.createItem(Material.LIGHT_GRAY_CONCRETE, Utils.format("&fNone")));

		// Option to exit
		inv.setItem(17, InventoryItems.exit());

		return inv;
	}

	// Menu to copy game settings
	public Inventory createCopySettingsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  Utils.format("&k") +
				Utils.format("&8&lCopy Game Settings"));

		// Options to choose any of the 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (game.arenas.get(i) == null)
				inv.setItem(i, Utils.createItem(Material.BLACK_CONCRETE,
						Utils.format("&c&lArena " + (i + 1) + " not available")));
			else if (i == arena)
				inv.setItem(i, Utils.createItem(Material.GRAY_GLAZED_TERRACOTTA,
						Utils.format("&6&l" + game.arenas.get(i).getName())));
			else
				inv.setItem(i, Utils.createItem(Material.WHITE_CONCRETE,
						Utils.format("&a&lCopy " + game.arenas.get(i).getName())));
		}

		// Easy preset
		inv.setItem(45, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&lEasy Preset")));

		// Medium preset
		inv.setItem(47, Utils.createItem(Material.YELLOW_CONCRETE, Utils.format("&e&lMedium Preset")));

		// Hard preset
		inv.setItem(49, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&lHard Preset")));

		// Insane preset
		inv.setItem(51, Utils.createItem(Material.MAGENTA_CONCRETE, Utils.format("&d&lInsane Preset")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Generate the shop menu
	public static Inventory createShop(int level, Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lLevel &9&l" + level + " &2&lItem Shop"));

		inv.setItem(1, Utils.createItem(Material.GOLDEN_SWORD,
				Utils.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), FLAGS, null));

		inv.setItem(3, Utils.createItem(Material.GOLDEN_CHESTPLATE,
				Utils.format("&5&lLevel &9&l" + level + " &5&lArmor Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), FLAGS, null));

		inv.setItem(5, Utils.createItem(Material.GOLDEN_APPLE,
				Utils.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]"))));

		inv.setItem(7, Utils.createItem(Material.QUARTZ, Utils.format("&6&lCustom Shop" +
				(arena.hasCustom() ? "" : " &4&l[DISABLED]"))));

		return inv;
	}

	// Generate the weapon shop
	public static Inventory createWeaponShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 4, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 27, Utils.format("&k") +
				Utils.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop"));

		// Fill in weapons
		for (int i = 0; i < 18; i++)
			inv.setItem(i, modifyPrice(GameItems.randWeapon(level), modifier));

		// Return option
		inv.setItem(22, InventoryItems.exit());

		return inv;
	}

	// Generate the armor shop
	public static Inventory createArmorShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 4, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 27, Utils.format("&k") +
				Utils.format("&5&lLevel &9&l" + level + " &5&lArmor Shop"));

		// Fill in armor
		for (int i = 0; i < 18; i++)
			inv.setItem(i, modifyPrice(GameItems.randArmor(level), modifier));

		// Return option
		inv.setItem(22, InventoryItems.exit());

		return inv;
	}

	// Generate the consumables shop
	public static Inventory createConsumablesShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 4, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop"));

		// Fill in consumables
		for (int i = 0; i < 9; i++)
			inv.setItem(i, modifyPrice(GameItems.randConsumable(level), modifier));

		// Return option
		inv.setItem(13, InventoryItems.exit());

		return inv;
	}

	// Display player stats
	public Inventory createPlayerStatsInventory(String name) {
		FileConfiguration playerData = plugin.getPlayerData();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&l" + name + "'s Stats"));

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
		inv.setItem(4, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&3&lTop Wave: &9" +
				playerData.getInt(name + ".topWave")), FLAGS, null,
				Utils.format("&7Highest completed wave")));

		// Kits
		inv.setItem(6, Utils.createItem(Material.ENDER_CHEST, Utils.format("&9&lKits")));

		// Crystal balance
		inv.setItem(8, Utils.createItem(Material.DIAMOND, Utils.format("&b&lCrystal Balance: &b" +
				playerData.getInt(name + ".crystalBalance"))));

		return inv;
	}

	// Display kits for a player
	public Inventory createPlayerKitsInventory(String name, String requester) {
		FileConfiguration playerData = plugin.getPlayerData();
		String path = name + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&9&l" + name + "'s Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefit"), Utils.format("&7per game or respawn")));

		inv.setItem(9, Utils.createItem(Material.STICK, Utils.format("&a&lOrc"),
				Utils.format("&7Start with a Knockback V stick"), Utils.format("&aFree!")));

		inv.setItem(10, Utils.createItem(Material.CARROT, Utils.format("&a&lFarmer"),
				Utils.format("&7Start with 5 carrots"), Utils.format("&aFree!")));

		if (playerData.getBoolean(path + "Soldier"))
			inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&a&lSoldier"), FLAGS,
					null, Utils.format("&7Start with a stone sword"), Utils.format("&aPurchased!")));
		else inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&a&lSoldier"), FLAGS,
				null, Utils.format("&7Start with a stone sword"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Soldier") + " Crystals")));

		if (playerData.getBoolean(path + "Tailor"))
			inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&a&lTailor"), FLAGS,
					null, Utils.format("&7Start with a full leather armor set"),
					Utils.format("&aPurchased!")));
		else inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&a&lTailor"), FLAGS,
				null, Utils.format("&7Start with a full leather armor set"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Tailor") + " Crystals")));

		if (playerData.getBoolean(path + "Alchemist"))
			inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&a&lAlchemist"),
				Utils.format("&7Start with 1 speed and 2 healing"),
					Utils.format("&7splash potions"), Utils.format("&aPurchased!")));
		else inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&a&lAlchemist"),
				Utils.format("&7Start with 1 speed and 2 healing"), Utils.format("&7splash potions"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Alchemist") + " Crystals")));

		if (playerData.getBoolean(path + "Trader"))
			inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&a&lTrader"),
				Utils.format("&7Start with 200 gems"), Utils.format("&aPurchased!")));
		else inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&a&lTrader"),
				Utils.format("&7Start with 200 gems"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Trader") + " Crystals")));

		switch (playerData.getInt(path + "Summoner")) {
			case 1:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 1"), Utils.format("&7Start with a wolf spawn"),
						Utils.format("&aPurchased!"),
						Utils.format("&cLevel 2"), Utils.format("&7Start with 2 wolf spawns"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Summoner", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 2"), Utils.format("&7Start with 2 wolf spawns"),
						Utils.format("&aPurchased!"),
						Utils.format("&cLevel 3"), Utils.format("&7Start with an iron golem spawn"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Summoner", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 3"), Utils.format("&7Start with an iron golem spawn"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&cLevel 1"), Utils.format("&7Start with a wolf spawn"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Summoner", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Reaper")) {
			case 1:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 1"), Utils.format("&7Start with a sharpness III netherite hoe"),
						Utils.format("&aPurchased!"),
						Utils.format("&cLevel 2"), Utils.format("&7Start with a sharpness V netherite hoe"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Reaper", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 2"), Utils.format("&7Start with a sharpness V netherite hoe"),
						Utils.format("&aPurchased!"),
						Utils.format("&cLevel 3"), Utils.format("&7Start with a sharpness VIII netherite hoe"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Reaper", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 3"), Utils.format("&7Start with a sharpness VIII netherite hoe"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&cLevel 1"), Utils.format("&7Start with a sharpness III netherite hoe"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Reaper", 1) +" Crystals")));
		}

		if (playerData.getBoolean(path + "Phantom"))
			inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&a&lPhantom"),
					Utils.format("&7Join as a player in any non-maxed game"), Utils.format("&aPurchased!")));
		else inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&a&lPhantom"),
				Utils.format("&7Join as a player in any non-maxed game"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Phantom") + " Crystals")));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		switch (playerData.getInt(path + "Mage")) {
			case 1:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 1"), Utils.format("&7Shoot a fireball"),
						Utils.format("&7(Cooldown 1 second)"), Utils.format("&aPurchased!"),
						Utils.format("&cLevel 2"), Utils.format("&7Shoot a strong fireball"),
						Utils.format("&7(Cooldown 2 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Mage", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 2"), Utils.format("&7Shoot a strong fireball"),
						Utils.format("&7(Cooldown 2 seconds)"), Utils.format("&aPurchased!"),
						Utils.format("&cLevel 3"), Utils.format("&7Shoot a very strong fireball"),
						Utils.format("&7(Cooldown 3 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Mage", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 3"), Utils.format("&7Shoot a very strong fireball"),
						Utils.format("&7(Cooldown 3 seconds)"), Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&cLevel 1"), Utils.format("&7Shoot a fireball"),
						Utils.format("&7(Cooldown 1 second)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Mage", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Ninja")) {
			case 1:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 1"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 10 seconds"),
						Utils.format("&7(Cooldown 30 seconds)"), Utils.format("&aPurchased!"),
						Utils.format("&cLevel 2"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 15 seconds"),
						Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Ninja", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 2"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 15 seconds"),
						Utils.format("&7(Cooldown 60 seconds)"), Utils.format("&aPurchased!"),
						Utils.format("&cLevel 3"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 20 seconds"),
						Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Ninja", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 3"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 20 seconds"),
						Utils.format("&7(Cooldown 90 seconds)"), Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&cLevel 1"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 10 seconds"),
						Utils.format("&7(Cooldown 30 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Ninja", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Templar")) {
			case 1:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7absorption I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7absorption II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Templar", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7absorption II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7absorption III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Templar", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7absorption III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7absorption I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Templar", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Warrior")) {
			case 1:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7strength I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7strength II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Warrior", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7strength II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7strength III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Warrior", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7strength III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7strength I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Warrior", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Knight")) {
			case 1:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7resistance I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7resistance II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Knight", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7resistance II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7resistance III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Knight", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7resistance III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7resistance I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Knight", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Priest")) {
			case 1:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7regeneration I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7regeneration II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Priest", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7regeneration II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7regeneration III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Priest", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7regeneration III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7regeneration I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Priest", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Siren")) {
			case 1:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give mobs within 3 blocks"),
						Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give mobs within 5 blocks"),
						Utils.format("&7weakness II for 10 seconds"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Siren", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give mobs within 5 blocks"),
						Utils.format("&7weakness II for 10 seconds"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give mobs within 6 blocks"),
						Utils.format("&7weakness II for 10 seconds,"),
						Utils.format("&7slowness I for 10 seconds"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Siren", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give mobs within 6 blocks"),
						Utils.format("&7weakness II for 10 seconds,"),
						Utils.format("&7slowness I for 10 seconds"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give mobs within 3 blocks"),
						Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Siren", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Monk")) {
			case 1:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7haste I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7haste II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Monk", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7haste II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7haste III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Monk", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7haste III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7haste I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Monk", 1) +" Crystals")));
		}

		switch (playerData.getInt(path + "Messenger")) {
			case 1:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7speed I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7speed II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Messenger", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7speed II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aPurchased!"), Utils.format("&cLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7speed III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Messenger", 3) +" Crystals")));
				break;
			case 3:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7speed III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7speed I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Messenger", 1) +" Crystals")));
		}

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give player a special effect")));

		if (playerData.getBoolean(path + "Blacksmith"))
			inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&e&lBlacksmith"),
					Utils.format("&7All equipment purchased are unbreakable"), Utils.format("&aPurchased!")));
		else inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&e&lBlacksmith"),
				Utils.format("&7All equipment purchased are unbreakable"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Blacksmith") + " Crystals")));

		if (playerData.getBoolean(path + "Witch"))
			inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&e&lWitch"),
					Utils.format("&7All purchased potions become splash potions"), Utils.format("&aPurchased!")));
		else inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&e&lWitch"),
				Utils.format("&7All purchased potions become splash potions"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Witch") + " Crystals")));

		if (playerData.getBoolean(path + "Merchant"))
			inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&e&lMerchant"),
					Utils.format("&7Earn a 5% rebate on all purchases"), Utils.format("&aPurchased!")));
		else inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&e&lMerchant"),
				Utils.format("&7Earn a 5% rebate on all purchases"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Merchant") + " Crystals")));

		if (playerData.getBoolean(path + "Vampire"))
			inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&e&lVampire"),
					Utils.format("&7Dealing x damage has an x% chance"),
					Utils.format("&7of healing half a heart"), Utils.format("&aPurchased!")));
		else inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&e&lVampire"),
				Utils.format("&7Dealing x damage has an x% chance"),
				Utils.format("&7of healing half a heart"),
				Utils.format("&cPurchase: &b" + kits.getPrice("Vampire") + " Crystals")));

		switch (playerData.getInt(path + "Giant")) {
			case 1:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Permanent 10% health boost"), Utils.format("&aPurchased!"),
						Utils.format("&cLevel 2"),
						Utils.format("&7Permanent 20% health boost"),
						Utils.format("&cUpgrade: &b" + kits.getPrice("Giant", 2) +" Crystals")));
				break;
			case 2:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Permanent 20% health boost"), Utils.format("&aPurchased!")));
				break;
			default:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Permanent 10% health boost"),
						Utils.format("&cPurchase: &b" + kits.getPrice("Giant", 1) +" Crystals")));
		}

		// Crystal balance
		if (name.equals(requester))
			inv.setItem(52, Utils.createItem(Material.DIAMOND, Utils.format("&b&lCrystal Balance: &b" +
					playerData.getInt(name + ".crystalBalance"))));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Display kits for a player to select
	public Inventory createSelectKitsInventory(Player player, Arena arena) {
		FileConfiguration playerData = plugin.getPlayerData();
		String path = player.getName() + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&9&l" + arena.getName() + " Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefit"), Utils.format("&7per game or respawn")));

		if (!arena.getBannedKits().contains("Orc"))
			inv.setItem(9, Utils.createItem(Material.STICK, Utils.format("&a&lOrc"),
					Utils.format("&7Start with a Knockback V stick"), Utils.format("&aAvailable")));

		if (!arena.getBannedKits().contains("Farmer"))
			inv.setItem(10, Utils.createItem(Material.CARROT, Utils.format("&a&lFarmer"),
				Utils.format("&7Start with 5 carrots"), Utils.format("&aAvailable")));

		if (!arena.getBannedKits().contains("Soldier"))
			if (playerData.getBoolean(path + "Soldier"))
				inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&a&lSoldier"), FLAGS,
						null, Utils.format("&7Start with a stone sword"), Utils.format("&aAvailable")));
			else inv.setItem(11, Utils.createItem(Material.STONE_SWORD, Utils.format("&a&lSoldier"), FLAGS,
					null, Utils.format("&7Start with a stone sword"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Tailor"))
			if (playerData.getBoolean(path + "Tailor"))
				inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&a&lTailor"), FLAGS,
						null, Utils.format("&7Start with a full leather armor set"),
						Utils.format("&aAvailable")));
			else inv.setItem(12, Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&a&lTailor"), FLAGS,
					null, Utils.format("&7Start with a full leather armor set"),
					Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Alchemist"))
			if (playerData.getBoolean(path + "Alchemist"))
				inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&a&lAlchemist"),
						Utils.format("&7Start with 1 speed and 2 healing"),
						Utils.format("&7splash potions"), Utils.format("&aAvailable")));
			else inv.setItem(13, Utils.createItem(Material.BREWING_STAND, Utils.format("&a&lAlchemist"),
					Utils.format("&7Start with 1 speed and 2 healing"),
					Utils.format("&7splash potions"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Trader"))
			if (playerData.getBoolean(path + "Trader"))
				inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&a&lTrader"),
						Utils.format("&7Start with 200 gems"), Utils.format("&aAvailable")));
			else inv.setItem(14, Utils.createItem(Material.EMERALD, Utils.format("&a&lTrader"),
					Utils.format("&7Start with 200 gems"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Summoner"))
			switch (playerData.getInt(path + "Summoner")) {
			case 1:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 1"), Utils.format("&7Start with a wolf spawn"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 2"), Utils.format("&7Start with 2 wolf spawns"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&aLevel 3"), Utils.format("&7Start with an iron golem spawn"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(15, Utils.createItem(Material.POLAR_BEAR_SPAWN_EGG, Utils.format("&a&lSummoner"),
						Utils.format("&cLevel 1"), Utils.format("&7Start with a wolf spawn"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Reaper"))
			switch (playerData.getInt(path + "Reaper")) {
			case 1:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 1"),
						Utils.format("&7Start with a sharpness III netherite hoe"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 2"),
						Utils.format("&7Start with a sharpness V netherite hoe"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&aLevel 3"),
						Utils.format("&7Start with a sharpness VIII netherite hoe"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(16, Utils.createItem(Material.NETHERITE_HOE, Utils.format("&a&lReaper"), FLAGS,
						null,
						Utils.format("&cLevel 1"), Utils.format("&7Start with a sharpness III netherite hoe"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Phantom"))
			if (playerData.getBoolean(path + "Phantom"))
				inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&a&lPhantom"),
						Utils.format("&7Join as a player in any non-maxed game"), Utils.format("&aAvailable")));
			else inv.setItem(17, Utils.createItem(Material.PHANTOM_MEMBRANE, Utils.format("&a&lPhantom"),
					Utils.format("&7Join as a player in any non-maxed game"), Utils.format("&cUnavailable")));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		if (!arena.getBannedKits().contains("Mage"))
			switch (playerData.getInt(path + "Mage")) {
			case 1:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 1"), Utils.format("&7Shoot a fireball"),
						Utils.format("&7(Cooldown 1 second)"), Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 2"), Utils.format("&7Shoot a strong fireball"),
						Utils.format("&7(Cooldown 2 seconds)"), Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&aLevel 3"), Utils.format("&7Shoot a very strong fireball"),
						Utils.format("&7(Cooldown 3 seconds)"), Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(27, Utils.createItem(Material.FIRE_CHARGE, Utils.format("&d&lMage"),
						Utils.format("&cLevel 1"), Utils.format("&7Shoot a fireball"),
						Utils.format("&7(Cooldown 1 second)"), Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Ninja"))
			switch (playerData.getInt(path + "Ninja")) {
			case 1:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 1"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 10 seconds"),
						Utils.format("&7(Cooldown 30 seconds)"), Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 2"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 15 seconds"),
						Utils.format("&7(Cooldown 60 seconds)"), Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&aLevel 3"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 20 seconds"),
						Utils.format("&7(Cooldown 90 seconds)"), Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(28, Utils.createItem(Material.CHAIN, Utils.format("&d&lNinja"),
						Utils.format("&cLevel 1"), Utils.format("&7You and your pets become invisible"),
						Utils.format("&7and disarm nearby monsters for 10 seconds"),
						Utils.format("&7(Cooldown 30 seconds)"), Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Templar"))
			switch (playerData.getInt(path + "Templar")) {
			case 1:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7absorption I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7absorption II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7absorption III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(29, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&d&lTemplar"), FLAGS,
						null, Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7absorption I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Warrior"))
			switch (playerData.getInt(path + "Warrior")) {
			case 1:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7strength I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7strength II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7strength III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 100 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(30, Utils.createItem(Material.NETHERITE_HELMET, Utils.format("&d&lWarrior"), FLAGS,
						null, Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7strength I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Knight"))
			switch (playerData.getInt(path + "Knight")) {
			case 1:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7resistance I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7resistance II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7resistance III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(31, Utils.createItem(Material.SHIELD, Utils.format("&d&lKnight"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7resistance I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Priest"))
			switch (playerData.getInt(path + "Priest")) {
			case 1:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7regeneration I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7regeneration II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 90 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7regeneration III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 120 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(32, Utils.createItem(Material.TOTEM_OF_UNDYING, Utils.format("&d&lPriest"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7regeneration I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Siren"))
			switch (playerData.getInt(path + "Siren")) {
			case 1:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give mobs within 3 blocks"),
						Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give mobs within 5 blocks"),
						Utils.format("&7weakness II for 10 seconds"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give mobs within 6 blocks"),
						Utils.format("&7weakness II for 10 seconds,"),
						Utils.format("&7slowness I for 10 seconds"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(33, Utils.createItem(Material.COBWEB, Utils.format("&d&lSiren"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give mobs within 3 blocks"),
						Utils.format("&7weakness I for 10 seconds"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Monk"))
			switch (playerData.getInt(path + "Monk")) {
			case 1:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7haste I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7haste II for 15 seconds,"),
						Utils.format("&725 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7haste III for 20 seconds,"),
						Utils.format("&730 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(34, Utils.createItem(Material.BELL, Utils.format("&d&lMonk"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7haste I for 15 seconds,"),
						Utils.format("&720 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cUnavailable")));
		}

		if (!arena.getBannedKits().contains("Messenger"))
			switch (playerData.getInt(path + "Messenger")) {
			case 1:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7speed I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Give all allies within 4 blocks"),
						Utils.format("&7speed II for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 60 seconds)"),
						Utils.format("&aAvailable")));
				break;
			case 3:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&aLevel 3"),
						Utils.format("&7Give all allies within 5 blocks"),
						Utils.format("&7speed III for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 80 seconds)"),
						Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(35, Utils.createItem(Material.FEATHER, Utils.format("&d&lMessenger"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Give all allies within 2.5 blocks"),
						Utils.format("&7speed I for 10 seconds,"),
						Utils.format("&715 seconds for yourself"), Utils.format("&7(Cooldown 40 seconds)"),
						Utils.format("&cUnavailable")));
		}

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give player a special effect")));

		if (!arena.getBannedKits().contains("Blacksmith"))
			if (playerData.getBoolean(path + "Blacksmith"))
				inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&e&lBlacksmith"),
						Utils.format("&7All equipment purchased are unbreakable"), Utils.format("&aAvailable")));
			else inv.setItem(45, Utils.createItem(Material.ANVIL, Utils.format("&e&lBlacksmith"),
					Utils.format("&7All equipment purchased are unbreakable"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Witch"))
			if (playerData.getBoolean(path + "Witch"))
				inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&e&lWitch"),
						Utils.format("&7All purchased potions become splash potions"), Utils.format("&aAvailable")));
			else inv.setItem(46, Utils.createItem(Material.CAULDRON, Utils.format("&e&lWitch"),
					Utils.format("&7All purchased potions become splash potions"),
					Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Merchant"))
			if (playerData.getBoolean(path + "Merchant"))
				inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&e&lMerchant"),
						Utils.format("&7Earn a 5% rebate on all purchases"), Utils.format("&aAvailable")));
			else inv.setItem(47, Utils.createItem(Material.EMERALD_BLOCK, Utils.format("&e&lMerchant"),
					Utils.format("&7Earn a 5% rebate on all purchases"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Vampire"))
			if (playerData.getBoolean(path + "Vampire"))
				inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&e&lVampire"),
						Utils.format("&7Dealing x damage has an x% chance"),
						Utils.format("&7of healing half a heart"), Utils.format("&aAvailable")));
			else inv.setItem(48, Utils.createItem(Material.GHAST_TEAR, Utils.format("&e&lVampire"),
					Utils.format("&7Dealing x damage has an x% chance"),
					Utils.format("&7of healing half a heart"), Utils.format("&cUnavailable")));

		if (!arena.getBannedKits().contains("Giant"))
			switch (playerData.getInt(path + "Giant")) {
			case 1:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&aLevel 1"),
						Utils.format("&7Permanent 10% health boost"), Utils.format("&aAvailable")));
				break;
			case 2:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&aLevel 2"),
						Utils.format("&7Permanent 20% health boost"), Utils.format("&aAvailable")));
				break;
			default:
				inv.setItem(49, Utils.createItem(Material.DARK_OAK_SAPLING, Utils.format("&e&lGiant"),
						Utils.format("&cLevel 1"),
						Utils.format("&7Permanent 10% health boost"), Utils.format("&cUnavailable")));
		}

		// Option for no kit
		inv.setItem(52, Utils.createItem(Material.LIGHT_GRAY_CONCRETE, Utils.format("&f&lNone")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Display arena information
	public Inventory createArenaInfoInventory(Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&6&l" + arena.getName() + " Info"));

		// Maximum players
		inv.setItem(0, Utils.createItem(Material.NETHERITE_HELMET,
				Utils.format("&4&lMaximum players: &4" + arena.getMaxPlayers()), FLAGS, null,
				Utils.format("&7The most players an arena can have")));

		// Minimum players
		inv.setItem(1, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum players: &2" + arena.getMinPlayers()), FLAGS, null,
				Utils.format("&7The least players an arena can have to start")));

		// Max waves
		String waves;
		if (arena.getMaxWaves() < 0)
			waves = "Unlimited";
		else waves = Integer.toString(arena.getMaxWaves());
		inv.setItem(2, Utils.createItem(Material.GOLDEN_SWORD,
				Utils.format("&3&lMax waves: &3" + waves), FLAGS, null,
				Utils.format("&7The highest wave the arena will go to")));

		// Wave time limit
		String limit;
		if (arena.getWaveTimeLimit() < 0)
			limit = "Unlimited";
		else limit = arena.getWaveTimeLimit() + " minute(s)";
		inv.setItem(3, Utils.createItem(Material.CLOCK,
				Utils.format("&9&lWave time limit: &9" + limit),
				Utils.format("&7The time limit for each wave before"), Utils.format("&7the game ends")));

		// Dynamic mob count
		inv.setItem(4, Utils.createItem(Material.SLIME_BALL,
				Utils.format("&e&lDynamic Mob Count: &e" + getToggleStatus(arena.hasDynamicCount())),
				Utils.format("&7Mob count adjusting based on"), Utils.format("&7number of players")));

		// Dynamic difficulty
		inv.setItem(5, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lDynamic Difficulty: &6" + getToggleStatus(arena.hasDynamicDifficulty())),
				Utils.format("&7Difficulty adjusting based on"), Utils.format("&7number of players")));

		// Dynamic prices
		inv.setItem(6, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lDynamic Prices: &b" + getToggleStatus(arena.hasDynamicPrices())),
				Utils.format("&7Prices adjusting based on number of"),
				Utils.format("&7players in the game")));

		// Dynamic time limit
		inv.setItem(7, Utils.createItem(Material.SNOWBALL,
				Utils.format("&a&lDynamic Time Limit: &a" + getToggleStatus(arena.hasDynamicLimit())),
				Utils.format("&7Wave time limit adjusting based on"),
				Utils.format("&7in-game difficulty")));

		// Difficulty multiplier
		inv.setItem(8, Utils.createItem(Material.TURTLE_HELMET,
				Utils.format("&4&lDifficulty Multiplier: &4" + arena.getDifficultyMultiplier()),
				FLAGS,
				null,
				Utils.format("&7Determines difficulty increase rate")));

		// Player spawn particles toggle
		inv.setItem(10, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&e&lPlayer Spawn Particles: " + getToggleStatus(arena.hasSpawnParticles()))));

		// Monster spawn particles toggle
		inv.setItem(11, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lMonster Spawn Particles: " + getToggleStatus(arena.hasMonsterParticles()))));

		// Villager spawn particles toggle
		inv.setItem(12, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&a&lVillager Spawn Particles: " + getToggleStatus(arena.hasVillagerParticles()))));

		// Default shop toggle
		inv.setItem(13, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&6&lDefault Shop: " + getToggleStatus(arena.hasNormal()))));

		// Custom shop toggle
		inv.setItem(14, Utils.createItem(Material.QUARTZ_BLOCK,
				Utils.format("&2&lCustom Shop: " + getToggleStatus(arena.hasNormal()))));

		// Custom shop inventory
		inv.setItem(15, Utils.createItem(Material.QUARTZ, Utils.format("&f&lCustom Shop Inventory")));

		// Arena records
		List<String> records = new ArrayList<>();
		arena.getSortedDescendingRecords().forEach(arenaRecord -> {
			records.add(Utils.format("&fWave " + arenaRecord.getWave()));
			StringBuilder players = new StringBuilder();
			arenaRecord.getPlayers().forEach(player -> players.append(player).append(", "));
			records.add(Utils.format("&7" + players.substring(0, players.length() - 2)));
		});
		inv.setItem(16, Utils.createItem(Material.GOLDEN_HELMET, Utils.format("&e&lArena Records"), FLAGS,
				null, records));

		return inv;
	}

	// Easy way to get a string for a toggle status
	private String getToggleStatus(boolean status) {
		String toggle;
		if (status)
			toggle = "&a&lON";
		else toggle = "&c&lOFF";
		return toggle;
	}

	// Modify the price of an item
	private static ItemStack modifyPrice(ItemStack itemStack, double modifier) {
		ItemStack item = itemStack.clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int price = (int) Math.round(Integer.parseInt(lore.get(lore.size() - 1).substring(10)) * modifier / 5) * 5;
		lore.set(lore.size() - 1, Utils.format("&2Gems: &a" + price));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
