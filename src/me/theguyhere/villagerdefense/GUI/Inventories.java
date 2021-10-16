package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Challenge;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.GameItems;
import me.theguyhere.villagerdefense.game.models.InventoryMeta;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.kits.Kit;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_16_R3.TileEntitySign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Inventories {
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
	
	// Menu of all the arenas
	public static Inventory createArenasInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  Utils.format("&k") +
				Utils.format("&2&lVillager Defense Arenas"));

		// Options to interact with all 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (Game.arenas[i] == null)
				inv.setItem(i, Utils.createItem(Material.RED_CONCRETE,
						Utils.format("&c&lCreate Arena " + (i + 1))));
			else
				inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE,
						Utils.format("&a&lEdit " + Game.arenas[i].getName())));
		}

		// Option to set lobby location
		inv.setItem(45, Utils.createItem(Material.BELL, Utils.format("&2&lLobby"),
				Utils.format("&7Manage minigame lobby")));

		// Option to set info hologram
		inv.setItem(46, Utils.createItem(Material.OAK_SIGN, Utils.format("&6&lInfo Boards"),
				Utils.format("&7Manage info boards")));

		// Option to set leaderboard hologram
		inv.setItem(47, Utils.createItem(Material.GOLDEN_HELMET, Utils.format("&e&lLeaderboards"),
				Utils.BUTTON_FLAGS, null, Utils.format("&7Manage leaderboards")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Menu for lobby
	public static Inventory createLobbyInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lLobby"));

		// Option to create or relocate the lobby
		if (Utils.getConfigLocation(plugin, "lobby") == null)
			inv.setItem(0, InventoryItems.create("Lobby"));
		else inv.setItem(0, InventoryItems.relocate("Lobby"));

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
	public static Inventory createLobbyConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Lobby?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing info boards
	public static Inventory createInfoBoardInventory(Main plugin) {
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
	public static Inventory createInfoBoardMenu(Main plugin, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(slot), 9, Utils.format("&k") +
				Utils.format("&6&lInfo Board " + slot));

		// Option to create or relocate info board
		if (Utils.getConfigLocation(plugin, "infoBoard." + slot) == null)
			inv.setItem(0, InventoryItems.create("Info Board"));
		else inv.setItem(0, InventoryItems.relocate("Info Board"));

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
	public static Inventory createInfoBoardConfirmInventory(int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(slot), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Info Board?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for leaderboards
	public static Inventory createLeaderboardInventory() {
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
				Utils.BUTTON_FLAGS, null));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the total kills leaderboard
	public static Inventory createTotalKillsLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lTotal Kills Leaderboard"));

		// Option to create or relocate the leaderboard
		if (Utils.getConfigLocation(plugin, "leaderboard.totalKills") == null)
			inv.setItem(0, InventoryItems.create("Leaderboard"));
		else inv.setItem(0, InventoryItems.relocate("Leaderboard"));

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
	public static Inventory createTopKillsLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&c&lTop Kills Leaderboard"));

		// Option to create or relocate the leaderboard
		if (Utils.getConfigLocation(plugin, "leaderboard.topKills") == null)
			inv.setItem(0, InventoryItems.create("Leaderboard"));
		else inv.setItem(0, InventoryItems.relocate("Leaderboard"));

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
	public static Inventory createTotalGemsLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lTotal Gems Leaderboard"));

		// Option to create or relocate the leaderboard
		if (Utils.getConfigLocation(plugin, "leaderboard.totalGems") == null)
			inv.setItem(0, InventoryItems.create("Leaderboard"));
		else inv.setItem(0, InventoryItems.relocate("Leaderboard"));

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
	public static Inventory createTopBalanceLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&a&lTop Balance Leaderboard"));

		// Option to create or relocate the leaderboard
		if (Utils.getConfigLocation(plugin, "leaderboard.topBalance") == null)
			inv.setItem(0, InventoryItems.create("Leaderboard"));
		else inv.setItem(0, InventoryItems.relocate("Leaderboard"));

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
	public static Inventory createTopWaveLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&9&lTop Wave Leaderboard"));

		// Option to create or relocate the leaderboard
		if (Utils.getConfigLocation(plugin, "leaderboard.topWave") == null)
			inv.setItem(0, InventoryItems.create("Leaderboard"));
		else inv.setItem(0, InventoryItems.relocate("Leaderboard"));

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
	public static Inventory createTotalKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top kills leaderboard
	public static Inventory createTopKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for total gems leaderboard
	public static Inventory createTotalGemsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Total Gems Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top balance leaderboard
	public static Inventory createTopBalanceConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Balance Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top wave leaderboard
	public static Inventory createTopWaveConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Top Wave Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for naming an arena
	public static Inventory createNamingInventory(int arena, String newName) {
		Arena arenaInstance = Game.arenas[arena];
		boolean caps = arenaInstance.isCaps();

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(newName, arena), 54, Utils.format("&k") +
				Utils.format("&2&lArena " + (arena + 1) + " Name: &8&l" + newName));

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
	public static  Inventory createArenaInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createArenaConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove " + Game.arenas[arena].getName() + '?'));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the portal and leaderboard of an arena
	public static Inventory createPortalInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createPortalConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Portal?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for removing the arena leaderboard
	public static Inventory createArenaBoardConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the player settings of an arena
	public static Inventory createPlayersInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Maximum players the game will have")));

		// Option to edit min players
		inv.setItem(4, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum Players"),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Minimum players needed for game to start")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the player spawn of an arena
	public static Inventory createPlayerSpawnInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createSpawnConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the waiting room of an arena
	public static Inventory createWaitingRoomInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createWaitingConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Waiting Room?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for changing max players in an arena
	public static Inventory createMaxPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lMaximum Players: " + Game.arenas[arena].getMaxPlayers()));

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
	public static Inventory createMinPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&2&lMinimum Players: " + Game.arenas[arena].getMinPlayers()));

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
	public static Inventory createMobsInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the monster spawns of an arena
	public static Inventory createMonsterSpawnInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createMonsterSpawnMenu(int arena, int slot) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawn " + (slot + 1) + ": " + arenaInstance.getName()));

		// Option to create or relocate monster spawn
		if (arenaInstance.getMonsterSpawn(slot) == null)
			inv.setItem(0, InventoryItems.create("Spawn"));
		else inv.setItem(0, InventoryItems.relocate("Spawn"));

		// Option to teleport to monster spawn
		inv.setItem(1, InventoryItems.teleport("Spawn"));

		// Option to center the monster spawn
		inv.setItem(2, InventoryItems.center("Spawn"));

		// Option to remove monster spawn
		inv.setItem(3, InventoryItems.remove("SPAWN"));

		// Toggle to set monster spawn type
		switch (arenaInstance.getMonsterSpawnType(slot)) {
			case 1:
				inv.setItem(4, Utils.createItem(Material.GUNPOWDER, Utils.format("&5&lType: Ground")));
				break;
			case 2:
				inv.setItem(4, Utils.createItem(Material.FEATHER, Utils.format("&5&lType: Flying")));
				break;
			default:
				inv.setItem(4, Utils.createItem(Material.BONE, Utils.format("&5&lType: All")));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing monster spawns
	public static Inventory createMonsterSpawnConfirmInventory(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Monster Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the villager spawns of an arena
	public static Inventory createVillagerSpawnInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createVillagerSpawnMenu(int arena, int slot) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, Utils.format("&k") +
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
	public static Inventory createVillagerSpawnConfirmInventory(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Villager Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Spawn table menu for an arena
	public static Inventory createSpawnTableInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];
		String chosen = arenaInstance.getSpawnTableFile();
		Inventory inv;
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		// Create inventory
		if (arenaInstance.getSpawnTableFile().equals("custom"))
			 inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
					Utils.format("&3&lSpawn Table: a" + arena + ".yml"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&3&lSpawn Table: " + arenaInstance.getSpawnTableFile() + ".yml"));

		// Option to set spawn table to default
		inv.setItem(0, Utils.createItem(Material.OAK_WOOD, Utils.format("&4&lDefault"), Utils.BUTTON_FLAGS,
				chosen.equals("default") ? enchants : null, Utils.format("&7Sets spawn table to default.yml")));

		// Option to set spawn table to global option 1
		inv.setItem(1, Utils.createItem(Material.RED_CONCRETE, Utils.format("&6&lOption 1"), Utils.BUTTON_FLAGS,
				chosen.equals("option1") ? enchants : null, Utils.format("&7Sets spawn table to option1.yml")));

		// Option to set spawn table to global option 2
		inv.setItem(2, Utils.createItem(Material.ORANGE_CONCRETE, Utils.format("&6&lOption 2"), Utils.BUTTON_FLAGS,
				chosen.equals("option2") ? enchants : null, Utils.format("&7Sets spawn table to option2.yml")));

		// Option to set spawn table to global option 3
		inv.setItem(3, Utils.createItem(Material.YELLOW_CONCRETE, Utils.format("&6&lOption 3"), Utils.BUTTON_FLAGS,
				chosen.equals("option3") ? enchants : null, Utils.format("&7Sets spawn table to option3.yml")));

		// Option to set spawn table to global option 4
		inv.setItem(4, Utils.createItem(Material.BROWN_CONCRETE, Utils.format("&6&lOption 4"), Utils.BUTTON_FLAGS,
				chosen.equals("option4") ? enchants : null, Utils.format("&7Sets spawn table to option4.yml")));

		// Option to set spawn table to global option 5
		inv.setItem(5, Utils.createItem(Material.LIGHT_GRAY_CONCRETE, Utils.format("&6&lOption 5"), Utils.BUTTON_FLAGS,
				chosen.equals("option5") ? enchants : null, Utils.format("&7Sets spawn table to option5.yml")));

		// Option to set spawn table to global option 6
		inv.setItem(6, Utils.createItem(Material.WHITE_CONCRETE, Utils.format("&6&lOption 6"), Utils.BUTTON_FLAGS,
				chosen.equals("option6") ? enchants : null, Utils.format("&7Sets spawn table to option6.yml")));

		// Option to set spawn table to custom option
		inv.setItem(7, Utils.createItem(Material.BIRCH_WOOD, Utils.format("&e&lCustom"), Utils.BUTTON_FLAGS,
				chosen.length() < 4 ? enchants : null,
				Utils.format("&7Sets spawn table to a[arena number].yml"),
				Utils.format("&7(Check the arena number in arenaData.yml)")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the shop settings of an arena
	public static Inventory createShopsInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&e&lShop Settings: " + arenaInstance.getName()));

		// Option to create a custom shop
		inv.setItem(0, Utils.createItem(Material.QUARTZ,
				Utils.format("&a&lEdit Custom Shop")));

		// Option to toggle default shop
		inv.setItem(1, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&6&lDefault Shop: " + getToggleStatus(arenaInstance.hasNormal())),
				Utils.format("&7Turn default shop on and off")));

		// Option to toggle custom shop
		inv.setItem(2, Utils.createItem(Material.QUARTZ_BLOCK,
				Utils.format("&2&lCustom Shop: " + getToggleStatus(arenaInstance.hasCustom())),
				Utils.format("&7Turn custom shop on and off")));

		// Option to toggle community chest
		inv.setItem(3, Utils.createItem(Material.CHEST,
				Utils.format("&d&lCommunity Chest: " + getToggleStatus(arenaInstance.hasCommunity())),
				Utils.format("&7Turn community chest on and off")));

		// Option to toggle dynamic prices
		inv.setItem(4, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lDynamic Prices: " + getToggleStatus(arenaInstance.hasDynamicPrices())),
				Utils.format("&7Prices adjusting based on number of"),
				Utils.format("&7players in the game")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for adding custom items
	public static Inventory createCustomItemsInventory(int arena, int slot) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 27, Utils.format("&k") +
				Utils.format("&6&lEdit Item"));

		// Item of interest
		inv.setItem(4, arenaInstance.getCustomShop().getItem(slot));

		// Option to increase by 1
		inv.setItem(9, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&l+1 gem")));

		// Option to increase by 10
		inv.setItem(11, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&l+10 gems")));

		// Option to increase by 100
		inv.setItem(13, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&l+100 gems")));

		// Option to increase by 1000
		inv.setItem(15, Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&l+1000 gems")));

		// Option to delete item
		inv.setItem(17, Utils.createItem(Material.LAVA_BUCKET, Utils.format("&4&lDELETE")));

		// Option to decrease by 1
		inv.setItem(18, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&l-1 gem")));

		// Option to decrease by 10
		inv.setItem(20, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&l-10 gems")));

		// Option to decrease by 100
		inv.setItem(22, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&l-100 gems")));

		// Option to decrease by 1000
		inv.setItem(24, Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&l-1000 gems")));

		// Option to exit
		inv.setItem(26, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing custom item
	public static Inventory createCustomItemConfirmInventory(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Custom Item?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the game settings of an arena
	public static Inventory createGameSettingsInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 18, Utils.format("&k") +
				Utils.format("&8&lGame Settings: " + arenaInstance.getName()));

		// Option to change max waves
		inv.setItem(0, Utils.createItem(Material.NETHERITE_SWORD,
				Utils.format("&3&lMax Waves"),
				Utils.BUTTON_FLAGS,
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
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Determines difficulty increase rate")));

		// Option to toggle dynamic difficulty
		inv.setItem(6, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lDynamic Difficulty: " + getToggleStatus(arenaInstance.hasDynamicDifficulty())),
				Utils.format("&7Difficulty adjusting based on"), Utils.format("&7number of players")));

		// Option to toggle late arrival
		inv.setItem(7, Utils.createItem(Material.DAYLIGHT_DETECTOR,
				Utils.format("&e&lLate Arrival: " + getToggleStatus(arenaInstance.hasLateArrival())),
				Utils.format("&7Allows players to join after"), Utils.format("&7the game has started")));

		// Option to toggle experience drop
		inv.setItem(9, Utils.createItem(Material.EXPERIENCE_BOTTLE,
				Utils.format("&b&lExperience Drop: " + getToggleStatus(arenaInstance.hasExpDrop())),
				Utils.format("&7Change whether experience drop or go"),
				Utils.format("&7straight into the killer's experience bar")));

		// Option to toggle item dropping
		inv.setItem(10, Utils.createItem(Material.EMERALD,
				Utils.format("&9&lItem Drop: " + getToggleStatus(arenaInstance.hasGemDrop())),
				Utils.format("&7Change whether gems and loot drop"),
				Utils.format("&7as physical items or go straight"),
				Utils.format("&7into the killer's balance/inventory")));

		// Option to set arena bounds
		inv.setItem(11, Utils.createItem(Material.BEDROCK, Utils.format("&4&lArena Bounds"),
				Utils.format("&7Bounds determine where players are"),
				Utils.format("&7allowed to go and where the game will"),
				Utils.format("&7function. Avoid building past arena bounds.")));

		// Option to edit wolf cap
		inv.setItem(12, Utils.createItem(Material.BONE, Utils.format("&6&lWolf Cap"),
				Utils.format("&7Maximum wolves a player can have")));

		// Option to edit golem cap
		inv.setItem(13, Utils.createItem(Material.IRON_INGOT, Utils.format("&e&lIron Golem Cap"),
				Utils.format("&7Maximum iron golems an arena can have")));

		// Option to edit sounds
		inv.setItem(14, Utils.createItem(Material.MUSIC_DISC_13,
				Utils.format("&d&lSounds"),
				Utils.BUTTON_FLAGS,
				null));

		// Option to copy game settings from another arena or a preset
		inv.setItem(15, Utils.createItem(Material.WRITABLE_BOOK,
				Utils.format("&f&lCopy Game Settings"),
				Utils.format("&7Copy settings of another arena or"),
				Utils.format("&7choose from a menu of presets")));

		// Option to exit
		inv.setItem(17, InventoryItems.exit());

		return inv;
	}

	// Menu for changing max waves of an arena
	public static Inventory createMaxWaveInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (Game.arenas[arena].getMaxWaves() < 0)
			inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
					Utils.format("&3&lMaximum Waves: Unlimited"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&3&lMaximum Waves: " + Game.arenas[arena].getMaxWaves()));

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
	public static Inventory createWaveTimeLimitInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (Game.arenas[arena].getWaveTimeLimit() < 0)
			inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
					Utils.format("&2&lWave Time Limit: Unlimited"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&2&lWave Time Limit: " + Game.arenas[arena].getWaveTimeLimit()));

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
	public static Inventory createAllowedKitsInventory(int arena, boolean mock) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, Utils.format("&k") +
				(mock ? Utils.format("&9&lAllowed Kits: " + arenaInstance.getName()) :
						Utils.format("&9&lAllowed Kits")));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefits"), Utils.format("&7per game or respawn")));

		if (arenaInstance.getBannedKits().contains(Kit.orc().getName()))
			inv.setItem(9, Kit.orc().getButton(-1, false));
		else inv.setItem(9, Kit.orc().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.farmer().getName()))
			inv.setItem(10, Kit.farmer().getButton(-1, false));
		else inv.setItem(10, Kit.farmer().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.soldier().getName()))
			inv.setItem(11, Kit.soldier().getButton(-1, false));
		else inv.setItem(11, Kit.soldier().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.alchemist().getName()))
			inv.setItem(12, Kit.alchemist().getButton(-1, false));
		else inv.setItem(12, Kit.alchemist().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.tailor().getName()))
			inv.setItem(13, Kit.tailor().getButton(-1, false));
		else inv.setItem(13, Kit.tailor().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.trader().getName()))
			inv.setItem(14, Kit.trader().getButton(-1, false));
		else inv.setItem(14, Kit.trader().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.summoner().getName()))
			inv.setItem(15, Kit.summoner().getButton(-1, false));
		else inv.setItem(15, Kit.summoner().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.reaper().getName()))
			inv.setItem(16, Kit.reaper().getButton(-1, false));
		else inv.setItem(16, Kit.reaper().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.phantom().getName()))
			inv.setItem(17, Kit.phantom().getButton(-1, false));
		else inv.setItem(17, Kit.phantom().getButton(-1, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		if (arenaInstance.getBannedKits().contains(Kit.mage().getName()))
			inv.setItem(27, Kit.mage().getButton(-1, false));
		else inv.setItem(27, Kit.mage().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.ninja().getName()))
			inv.setItem(28, Kit.ninja().getButton(-1, false));
		else inv.setItem(28, Kit.ninja().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.templar().getName()))
			inv.setItem(29, Kit.templar().getButton(-1, false));
		else inv.setItem(29, Kit.templar().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.warrior().getName()))
			inv.setItem(30, Kit.warrior().getButton(-1, false));
		else inv.setItem(30, Kit.warrior().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.knight().getName()))
			inv.setItem(31, Kit.knight().getButton(-1, false));
		else inv.setItem(31, Kit.knight().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.priest().getName()))
			inv.setItem(32, Kit.priest().getButton(-1, false));
		else inv.setItem(32, Kit.priest().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.siren().getName()))
			inv.setItem(33, Kit.siren().getButton(-1, false));
		else inv.setItem(33, Kit.siren().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.monk().getName()))
			inv.setItem(34, Kit.monk().getButton(-1, false));
		else inv.setItem(34, Kit.monk().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.messenger().getName()))
			inv.setItem(35, Kit.messenger().getButton(-1, false));
		else inv.setItem(35, Kit.messenger().getButton(-1, true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give players a permanent"), Utils.format("&7special effect")));

		if (arenaInstance.getBannedKits().contains(Kit.blacksmith().getName()))
			inv.setItem(45, Kit.blacksmith().getButton(-1, false));
		else inv.setItem(45, Kit.blacksmith().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.witch().getName()))
			inv.setItem(46, Kit.witch().getButton(-1, false));
		else inv.setItem(46, Kit.witch().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.merchant().getName()))
			inv.setItem(47, Kit.merchant().getButton(-1, false));
		else inv.setItem(47, Kit.merchant().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.vampire().getName()))
			inv.setItem(48, Kit.vampire().getButton(-1, false));
		else inv.setItem(48, Kit.vampire().getButton(-1, true));
		if (arenaInstance.getBannedKits().contains(Kit.giant().getName()))
			inv.setItem(49, Kit.giant().getButton(-1, false));
		else inv.setItem(49, Kit.giant().getButton(-1, true));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Menu for changing the difficulty label of an arena
	public static Inventory createDifficultyLabelInventory(int arena) {
		String label = Game.arenas[arena].getDifficultyLabel();
		switch (label) {
			case "Easy":
				label = "&a&lEasy";
				break;
			case "Medium":
				label = "&e&lMedium";
				break;
			case "Hard":
				label = "&c&lHard";
				break;
			case "Insane":
				label = "&d&lInsane";
				break;
			default:
				label = "";
		}

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
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
	public static Inventory createDifficultyMultiplierInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lDifficulty Multiplier: " + Game.arenas[arena].getDifficultyMultiplier()));

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

	// Menu for selecting arena bound corners
	public static Inventory createBoundsInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lArena Bounds: " + arenaInstance.getName()));

		// Option to interact with corner 1
		inv.setItem(0, Utils.createItem(Material.TORCH, Utils.format("&b&lCorner 1: " +
				(arenaInstance.getCorner1() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to interact with corner 2
		inv.setItem(3, Utils.createItem(Material.SOUL_TORCH, Utils.format("&9&lCorner 2: " +
				(arenaInstance.getCorner2() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to toggle arena border particles
		inv.setItem(6, Utils.createItem(Material.FIREWORK_ROCKET, Utils.format("&4&lBorder Particles: " +
				getToggleStatus(arenaInstance.hasBorderParticles()))));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing corner 1 of an arena
	public static Inventory createCorner1Inventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&b&lCorner 1: " + arenaInstance.getName()));

		// Option to create waiting room
		if (arenaInstance.getCorner1() == null)
			inv.setItem(0, InventoryItems.create("Corner 1"));
		else inv.setItem(0, InventoryItems.relocate("Corner 1"));

		// Option to teleport to waiting room
		inv.setItem(2, InventoryItems.teleport("Corner 1"));

		// Option to remove waiting room
		inv.setItem(4, InventoryItems.remove("CORNER 1"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing corner 1
	public static Inventory createCorner1ConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Corner 1?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing corner 2 of an arena
	public static Inventory createCorner2Inventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&9&lCorner 2: " + arenaInstance.getName()));

		// Option to create waiting room
		if (arenaInstance.getCorner2() == null)
			inv.setItem(0, InventoryItems.create("Corner 2"));
		else inv.setItem(0, InventoryItems.relocate("Corner 2"));

		// Option to teleport to waiting room
		inv.setItem(2, InventoryItems.teleport("Corner 2"));

		// Option to remove waiting room
		inv.setItem(4, InventoryItems.remove("CORNER 2"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing corner 2
	public static Inventory createCorner2ConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&4&lRemove Corner 2?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for changing wolf cap of an arena
	public static Inventory createWolfCapInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&6&lWolf Cap: " + Game.arenas[arena].getWolfCap()));

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

	// Menu for changing iron golem cap of an arena
	public static Inventory createGolemCapInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&e&lIron Golem Cap: " + Game.arenas[arena].getGolemCap()));

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

	// Menu for editing the sounds of an arena
	public static Inventory createSoundsInventory(int arena) {
		Arena arenaInstance = Game.arenas[arena];

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, Utils.format("&k") +
				Utils.format("&d&lSounds: " + Game.arenas[arena].getName()));

		// Option to edit win sound
		inv.setItem(0, Utils.createItem(Material.MUSIC_DISC_PIGSTEP,
				Utils.format("&a&lWin Sound: " + getToggleStatus(arenaInstance.hasWinSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when game ends and players win")));

		// Option to edit lose sound
		inv.setItem(1, Utils.createItem(Material.MUSIC_DISC_11,
				Utils.format("&e&lLose Sound: " + getToggleStatus(arenaInstance.hasLoseSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when game ends and players lose")));

		// Option to edit wave start sound
		inv.setItem(2, Utils.createItem(Material.MUSIC_DISC_CAT,
				Utils.format("&2&lWave Start Sound: " + getToggleStatus(arenaInstance.hasWaveStartSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when a wave starts")));

		// Option to edit wave finish sound
		inv.setItem(3, Utils.createItem(Material.MUSIC_DISC_BLOCKS,
				Utils.format("&9&lWave Finish Sound: " + getToggleStatus(arenaInstance.hasWaveFinishSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when a wave ends")));

		// Option to edit waiting music
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_MELLOHI,
				Utils.format("&6&lWaiting Sound"),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played while players wait"), Utils.format("&7for the game to start")));

		// Option to edit gem pickup sound
		inv.setItem(5, Utils.createItem(Material.MUSIC_DISC_FAR,
				Utils.format("&b&lGem Pickup Sound: " + getToggleStatus(arenaInstance.hasGemSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when players pick up gems")));

		// Option to edit player death sound
		inv.setItem(6, Utils.createItem(Material.MUSIC_DISC_CHIRP,
				Utils.format("&4&lPlayer Death Sound: " + getToggleStatus(arenaInstance.hasPlayerDeathSound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when a player dies")));

		// Option to edit ability sound
		inv.setItem(7, Utils.createItem(Material.MUSIC_DISC_MALL,
				Utils.format("&d&lAbility Sound: " + getToggleStatus(arenaInstance.hasAbilitySound())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Played when a player uses their ability")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the win sound of an arena
	public static Inventory createWaitSoundInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 18, Utils.format("&k") +
				Utils.format("&6&lWaiting Sound: " + Game.arenas[arena].getWaitingSoundName()));

		Arena arenaInstance = Game.arenas[arena];
		int music = arenaInstance.getWaitingSoundNum();

		// Sound options
		inv.setItem(0, arenaInstance.getWaitingSoundButton(0));
		inv.setItem(1, arenaInstance.getWaitingSoundButton(1));
		inv.setItem(2, arenaInstance.getWaitingSoundButton(2));
		inv.setItem(3, arenaInstance.getWaitingSoundButton(3));
		inv.setItem(4, arenaInstance.getWaitingSoundButton(4));
		inv.setItem(5, arenaInstance.getWaitingSoundButton(5));

		inv.setItem(9, arenaInstance.getWaitingSoundButton(9));
		inv.setItem(10, arenaInstance.getWaitingSoundButton(10));
		inv.setItem(11, arenaInstance.getWaitingSoundButton(11));
		inv.setItem(12, arenaInstance.getWaitingSoundButton(12));
		inv.setItem(13, arenaInstance.getWaitingSoundButton(13));
		inv.setItem(14, arenaInstance.getWaitingSoundButton(14));

		// Option to exit
		inv.setItem(17, InventoryItems.exit());

		return inv;
	}

	// Menu to copy game settings
	public static Inventory createCopySettingsInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54,  Utils.format("&k") +
				Utils.format("&8&lCopy Game Settings"));

		// Options to choose any of the 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (Game.arenas[i] == null)
				inv.setItem(i, Utils.createItem(Material.BLACK_CONCRETE,
						Utils.format("&c&lArena " + (i + 1) + " not available")));
			else if (i == arena)
				inv.setItem(i, Utils.createItem(Material.GRAY_GLAZED_TERRACOTTA,
						Utils.format("&6&l" + Game.arenas[i].getName())));
			else
				inv.setItem(i, Utils.createItem(Material.WHITE_CONCRETE,
						Utils.format("&a&lCopy " + Game.arenas[i].getName())));
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

		inv.setItem(0, Utils.createItem(Material.GOLDEN_SWORD,
				Utils.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), Utils.BUTTON_FLAGS, null));

		inv.setItem(2, Utils.createItem(Material.GOLDEN_CHESTPLATE,
				Utils.format("&5&lLevel &9&l" + level + " &5&lArmor Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), Utils.BUTTON_FLAGS, null));

		inv.setItem(4, Utils.createItem(Material.GOLDEN_APPLE,
				Utils.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]"))));

		inv.setItem(6, Utils.createItem(Material.QUARTZ, Utils.format("&6&lCustom Shop" +
				(arena.hasCustom() ? "" : " &4&l[DISABLED]"))));

		inv.setItem(8, Utils.createItem(Material.CHEST, Utils.format("&d&lCommunity Chest" +
				(arena.hasCommunity() ? "" : " &4&l[DISABLED]"))));

		return inv;
	}

	// Generate the weapon shop
	public static Inventory createWeaponShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 27, Utils.format("&k") +
				Utils.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop"));

		// Fill in swords
		List<ItemStack> swords = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			swords.add(GameItems.sword(level));
		swords.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(swords.get(i), modifier));

		// Fill in axes
		List<ItemStack> axes = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			axes.add(GameItems.axe(level));
		axes.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(axes.get(i), modifier));

		// Fill in range
		List<ItemStack> ranges = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			ranges.add(GameItems.randRange(level));
		ranges.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 5; i++)
			inv.setItem(i + 9, modifyPrice(ranges.get(i), modifier));

		// Fill in ammo
		List<ItemStack> ammos = new ArrayList<>();
		for (int i = 0; i < 3; i++)
			ammos.add(GameItems.randAmmo(level));
		ammos.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 3; i++)
			inv.setItem(i + 15, modifyPrice(ammos.get(i), modifier));

		// Return option
		inv.setItem(22, InventoryItems.exit());

		return inv;
	}

	// Generate the armor shop
	public static Inventory createArmorShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 27, Utils.format("&k") +
				Utils.format("&5&lLevel &9&l" + level + " &5&lArmor Shop"));

		// Fill in helmets
		List<ItemStack> helmets = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			helmets.add(GameItems.helmet(level));
		helmets.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(helmets.get(i), modifier));

		// Fill in chestplates
		List<ItemStack> chestplates = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			chestplates.add(GameItems.chestplate(level));
		chestplates.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(chestplates.get(i), modifier));

		// Fill in leggings
		List<ItemStack> leggings = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			leggings.add(GameItems.leggings(level));
		leggings.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 9, modifyPrice(leggings.get(i), modifier));

		// Fill in boots
		List<ItemStack> boots = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			boots.add(GameItems.boots(level));
		boots.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 14, modifyPrice(boots.get(i), modifier));

		// Return option
		inv.setItem(22, InventoryItems.exit());

		return inv;
	}

	// Generate the consumables shop
	public static Inventory createConsumablesShop(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop"));

		// Fill in food
		List<ItemStack> foods = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			foods.add(GameItems.randFood(level));
		foods.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(foods.get(i), modifier));

		// Fill in other
		List<ItemStack> others = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			others.add(GameItems.randOther(level));
		others.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = itemStack.getItemMeta().getLore();
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(others.get(i), modifier));

		// Return option
		inv.setItem(13, InventoryItems.exit());

		return inv;
	}

	// Display player stats
	public static Inventory createPlayerStatsInventory(Main plugin, String name) {
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
		inv.setItem(4, Utils.createItem(Material.GOLDEN_SWORD, Utils.format("&3&lTop Wave: &3" +
				playerData.getInt(name + ".topWave")), Utils.BUTTON_FLAGS, null,
				Utils.format("&7Highest completed wave")));

		// Kits
		inv.setItem(6, Utils.createItem(Material.ENDER_CHEST, Utils.format("&9&lKits")));

		// Crystal balance
		inv.setItem(8, Utils.createItem(Material.DIAMOND, Utils.format("&b&lCrystal Balance: &b" +
				playerData.getInt(name + ".crystalBalance"))));

		return inv;
	}

	// Display kits for a player
	public static Inventory createPlayerKitsInventory(Main plugin, String name, String requester) {
		FileConfiguration playerData = plugin.getPlayerData();
		String path = name + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&9&l" + name + "'s Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefits"), Utils.format("&7per game or respawn")));

		inv.setItem(9, Kit.orc().getButton(1, true));
		inv.setItem(10, Kit.farmer().getButton(1, true));
		inv.setItem(11, Kit.soldier().getButton(playerData.getBoolean(path + Kit.soldier().getName()) ? 1 : 0,
				true));
		inv.setItem(12, Kit.alchemist().getButton(playerData.getBoolean(path + Kit.alchemist().getName()) ?
				1 : 0, true));
		inv.setItem(13, Kit.tailor().getButton(playerData.getBoolean(path + Kit.tailor().getName()) ? 1 : 0,
				true));
		inv.setItem(14, Kit.trader().getButton(playerData.getBoolean(path + Kit.trader().getName()) ? 1 : 0,
				true));
		inv.setItem(15, Kit.summoner().getButton(playerData.getInt(path + Kit.summoner().getName()),
				true));
		inv.setItem(16, Kit.reaper().getButton(playerData.getInt(path + Kit.reaper().getName()),
				true));
		inv.setItem(17, Kit.phantom().getButton(playerData.getBoolean(path + Kit.phantom().getName()) ? 1 : 0,
				true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		inv.setItem(27, Kit.mage().getButton(playerData.getInt(path + Kit.mage().getName()), true));
		inv.setItem(28, Kit.ninja().getButton(playerData.getInt(path + Kit.ninja().getName()),
				true));
		inv.setItem(29, Kit.templar().getButton(playerData.getInt(path + Kit.templar().getName()),
				true));
		inv.setItem(30, Kit.warrior().getButton(playerData.getInt(path + Kit.warrior().getName()),
				true));
		inv.setItem(31, Kit.knight().getButton(playerData.getInt(path + Kit.knight().getName()),
				true));
		inv.setItem(32, Kit.priest().getButton(playerData.getInt(path + Kit.priest().getName()),
				true));
		inv.setItem(33, Kit.siren().getButton(playerData.getInt(path + Kit.siren().getName()),
				true));
		inv.setItem(34, Kit.monk().getButton(playerData.getInt(path + Kit.monk().getName()), true));
		inv.setItem(35, Kit.messenger().getButton(playerData.getInt(path + Kit.messenger().getName()),
				true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give players a permanent"),
							Utils.format("&7special effect")));

		inv.setItem(45, Kit.blacksmith().getButton(playerData.getBoolean(path + Kit.blacksmith().getName()) ?
				1 : 0, true));
		inv.setItem(46, Kit.witch().getButton(playerData.getBoolean(path + Kit.witch().getName()) ? 1 : 0,
				true));
		inv.setItem(47, Kit.merchant().getButton(playerData.getBoolean(path + Kit.merchant().getName()) ?
				1 : 0, true));
		inv.setItem(48, Kit.vampire().getButton(playerData.getBoolean(path + Kit.vampire().getName()) ? 1 : 0,
				true));
		inv.setItem(49, Kit.giant().getButton(playerData.getInt(path + Kit.giant().getName()),
				true));

		// Crystal balance
		if (name.equals(requester))
			inv.setItem(52, Utils.createItem(Material.DIAMOND, Utils.format("&b&lCrystal Balance: &b" +
					playerData.getInt(name + ".crystalBalance"))));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Display kits for a player to select
	public static Inventory createSelectKitsInventory(Main plugin, Player player, Arena arena) {
		FileConfiguration playerData = plugin.getPlayerData();
		String path = player.getName() + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&9&l" + arena.getName() + " Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, Utils.createItem(Material.LIME_STAINED_GLASS_PANE, Utils.format("&a&lGift Kits"),
					Utils.format("&7Kits give one-time benefits"), Utils.format("&7per game or respawn")));

		if (!arena.getBannedKits().contains("Orc"))
			inv.setItem(9, Kit.orc().getButton(1, false));
		if (!arena.getBannedKits().contains("Farmer"))
			inv.setItem(10, Kit.farmer().getButton(1, false));
		if (!arena.getBannedKits().contains("Soldier"))
			inv.setItem(11, Kit.soldier().getButton(playerData.getBoolean(path + Kit.soldier().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Alchemist"))
			inv.setItem(12, Kit.alchemist().getButton(playerData.getBoolean(path + Kit.alchemist().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Tailor"))
			inv.setItem(13, Kit.tailor().getButton(playerData.getBoolean(path + Kit.tailor().getName()) ? 1 : 0,
					false));
		if (!arena.getBannedKits().contains("Trader"))
			inv.setItem(14, Kit.trader().getButton(playerData.getBoolean(path + Kit.trader().getName()) ? 1 : 0,
					false));
		if (!arena.getBannedKits().contains("Summoner"))
			inv.setItem(15, Kit.summoner().getButton(playerData.getInt(path + Kit.summoner().getName()),
					false));
		if (!arena.getBannedKits().contains("Reaper"))
			inv.setItem(16, Kit.reaper().getButton(playerData.getInt(path + Kit.reaper().getName()),
					false));
		if (!arena.getBannedKits().contains("Phantom"))
			inv.setItem(17, Kit.phantom().getButton(playerData.getBoolean(path + Kit.phantom().getName()) ?
					1 : 0, false));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, Utils.createItem(Material.MAGENTA_STAINED_GLASS_PANE, Utils.format("&d&lAbility Kits"),
					Utils.format("&7Kits give special ability per respawn")));

		if (!arena.getBannedKits().contains("Mage"))
			inv.setItem(27, Kit.mage().getButton(playerData.getInt(path + Kit.mage().getName()),
					false));
		if (!arena.getBannedKits().contains("Ninja"))
			inv.setItem(28, Kit.ninja().getButton(playerData.getInt(path + Kit.ninja().getName()),
					false));
		if (!arena.getBannedKits().contains("Templar"))
			inv.setItem(29, Kit.templar().getButton(playerData.getInt(path + Kit.templar().getName()),
					false));
		if (!arena.getBannedKits().contains("Warrior"))
			inv.setItem(30, Kit.warrior().getButton(playerData.getInt(path + Kit.warrior().getName()),
					false));
		if (!arena.getBannedKits().contains("Knight"))
			inv.setItem(31, Kit.knight().getButton(playerData.getInt(path + Kit.knight().getName()),
					false));
		if (!arena.getBannedKits().contains("Priest"))
			inv.setItem(32, Kit.priest().getButton(playerData.getInt(path + Kit.priest().getName()),
					false));
		if (!arena.getBannedKits().contains("Siren"))
			inv.setItem(33, Kit.siren().getButton(playerData.getInt(path + Kit.siren().getName()),
					false));
		if (!arena.getBannedKits().contains("Monk"))
			inv.setItem(34, Kit.monk().getButton(playerData.getInt(path + Kit.monk().getName()),
					false));
		if (!arena.getBannedKits().contains("Messenger"))
			inv.setItem(35, Kit.messenger().getButton(playerData.getInt(path + Kit.messenger().getName()),
					false));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, Utils.createItem(Material.YELLOW_STAINED_GLASS_PANE, Utils.format("&e&lEffect Kits"),
					Utils.format("&7Kits give players a permanent"), Utils.format("&7special effect")));

		if (!arena.getBannedKits().contains("Blacksmith"))
			inv.setItem(45, Kit.blacksmith().getButton(
					playerData.getBoolean(path + Kit.blacksmith().getName()) ? 1 : 0, false));
		if (!arena.getBannedKits().contains("Witch"))
			inv.setItem(46, Kit.witch().getButton(playerData.getBoolean(path + Kit.witch().getName()) ? 1 : 0,
					false));
		if (!arena.getBannedKits().contains("Merchant"))
			inv.setItem(47, Kit.merchant().getButton(playerData.getBoolean(path + Kit.merchant().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Vampire"))
			inv.setItem(48, Kit.vampire().getButton(playerData.getBoolean(path + Kit.vampire().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Giant"))
			inv.setItem(49, Kit.giant().getButton(playerData.getInt(path + Kit.giant().getName()),
					false));

		// Option for no kit
		inv.setItem(52, Kit.none().getButton(0, true));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Display challenges for a player to select
	public static Inventory createSelectChallengesInventory(VDPlayer player, Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 18, Utils.format("&k") +
				Utils.format("&5&l" + arena.getName() + " Challenges"));

		// Set buttons
		inv.setItem(0, Challenge.amputee().getButton(player.getChallenges().contains(Challenge.amputee())));
		inv.setItem(1, Challenge.clumsy().getButton(player.getChallenges().contains(Challenge.clumsy())));
		inv.setItem(2, Challenge.featherweight().getButton(player.getChallenges().contains(Challenge.featherweight())));
		inv.setItem(3, Challenge.pacifist().getButton(player.getChallenges().contains(Challenge.pacifist())));
		inv.setItem(4, Challenge.dwarf().getButton(player.getChallenges().contains(Challenge.dwarf())));
		inv.setItem(5, Challenge.uhc().getButton(player.getChallenges().contains(Challenge.uhc())));
		inv.setItem(6, Challenge.naked().getButton(player.getChallenges().contains(Challenge.naked())));
		inv.setItem(7, Challenge.blind().getButton(player.getChallenges().contains(Challenge.blind())));
		inv.setItem(8, Challenge.none().getButton(player.getChallenges().isEmpty()));

		// Option to exit
		inv.setItem(13, InventoryItems.exit());

		return inv;
	}

	// Display arena information
	public static Inventory createArenaInfoInventory(Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena.getArena()), 36, Utils.format("&k") +
				Utils.format("&6&l" + arena.getName() + " Info"));

		// Maximum players
		inv.setItem(1, Utils.createItem(Material.NETHERITE_HELMET,
				Utils.format("&4&lMaximum players: &4" + arena.getMaxPlayers()), Utils.BUTTON_FLAGS, null,
				Utils.format("&7The most players an arena can have")));

		// Minimum players
		inv.setItem(2, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum players: &2" + arena.getMinPlayers()), Utils.BUTTON_FLAGS, null,
				Utils.format("&7The least players an arena can have to start")));

		// Max waves
		String waves;
		if (arena.getMaxWaves() < 0)
			waves = "Unlimited";
		else waves = Integer.toString(arena.getMaxWaves());
		inv.setItem(3, Utils.createItem(Material.GOLDEN_SWORD,
				Utils.format("&3&lMax waves: &3" + waves), Utils.BUTTON_FLAGS, null,
				Utils.format("&7The highest wave the arena will go to")));

		// Wave time limit
		String limit;
		if (arena.getWaveTimeLimit() < 0)
			limit = "Unlimited";
		else limit = arena.getWaveTimeLimit() + " minute(s)";
		inv.setItem(4, Utils.createItem(Material.CLOCK,
				Utils.format("&9&lWave time limit: &9" + limit),
				Utils.format("&7The time limit for each wave before"), Utils.format("&7the game ends")));

		// Wolf cap
		inv.setItem(5, Utils.createItem(Material.BONE, Utils.format("&6&lWolf Cap: &6" + arena.getWolfCap()),
				Utils.format("&7Maximum wolves a player can have")));

		// Golem cap
		inv.setItem(6, Utils.createItem(Material.IRON_INGOT, Utils.format("&e&lIron Golem Cap: &e" +
						arena.getGolemCap()),
				Utils.format("&7Maximum iron golems an arena can have")));

		// Allowed kits
		inv.setItem(7, Utils.createItem(Material.ENDER_CHEST, Utils.format("&9&lAllowed Kits")));

		// Dynamic mob count
		inv.setItem(10, Utils.createItem(Material.SLIME_BALL,
				Utils.format("&e&lDynamic Mob Count: " + getToggleStatus(arena.hasDynamicCount())),
				Utils.format("&7Mob count adjusting based on"), Utils.format("&7number of players")));

		// Dynamic difficulty
		inv.setItem(11, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lDynamic Difficulty: " + getToggleStatus(arena.hasDynamicDifficulty())),
				Utils.format("&7Difficulty adjusting based on"), Utils.format("&7number of players")));

		// Dynamic prices
		inv.setItem(12, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lDynamic Prices: " + getToggleStatus(arena.hasDynamicPrices())),
				Utils.format("&7Prices adjusting based on number of"),
				Utils.format("&7players in the game")));

		// Dynamic time limit
		inv.setItem(13, Utils.createItem(Material.SNOWBALL,
				Utils.format("&a&lDynamic Time Limit: " + getToggleStatus(arena.hasDynamicLimit())),
				Utils.format("&7Wave time limit adjusting based on"),
				Utils.format("&7in-game difficulty")));

		// Difficulty multiplier
		inv.setItem(14, Utils.createItem(Material.DAYLIGHT_DETECTOR,
				Utils.format("&e&lLate Arrival: " + getToggleStatus(arena.hasLateArrival())),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Whether players can enter"),
				Utils.format("&7arenas late")));

		// Item dropping
		inv.setItem(15, Utils.createItem(Material.EMERALD,
				Utils.format("&9&lItem Drop: " + getToggleStatus(arena.hasGemDrop())),
				Utils.format("&7Change whether gems drop as"),
				Utils.format("&7physical gems or go straight"),
				Utils.format("&7into the killer's balance")));

		// Experience drop
		inv.setItem(16, Utils.createItem(Material.EXPERIENCE_BOTTLE,
				Utils.format("&b&lExperience Drop: " + getToggleStatus(arena.hasExpDrop())),
				Utils.format("&7Change whether experience drop or go"),
				Utils.format("&7straight into the killer's experience bar")));

		// Player spawn particles
		inv.setItem(19, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&e&lPlayer Spawn Particles: " + getToggleStatus(arena.hasSpawnParticles()))));

		// Monster spawn particles
		inv.setItem(20, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lMonster Spawn Particles: " + getToggleStatus(arena.hasMonsterParticles()))));

		// Villager spawn particles
		inv.setItem(21, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&a&lVillager Spawn Particles: " + getToggleStatus(arena.hasVillagerParticles()))));

		// Default shop
		inv.setItem(22, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&6&lDefault Shop: " + getToggleStatus(arena.hasNormal()))));

		// Custom shop
		inv.setItem(23, Utils.createItem(Material.QUARTZ_BLOCK,
				Utils.format("&2&lCustom Shop: " + getToggleStatus(arena.hasCustom()))));

		// Community chest
		inv.setItem(24, Utils.createItem(Material.CHEST,
				Utils.format("&d&lCommunity Chest: " + getToggleStatus(arena.hasCommunity()))));

		// Custom shop inventory
		inv.setItem(25, Utils.createItem(Material.QUARTZ, Utils.format("&f&lCustom Shop Inventory")));

		// Difficulty multiplier
		inv.setItem(30, Utils.createItem(Material.TURTLE_HELMET,
				Utils.format("&4&lDifficulty Multiplier: &4" + arena.getDifficultyMultiplier()),
				Utils.BUTTON_FLAGS,
				null,
				Utils.format("&7Determines difficulty increase rate")));

		// Arena records
		List<String> records = new ArrayList<>();
		arena.getSortedDescendingRecords().forEach(arenaRecord -> {
			records.add(Utils.format("&fWave " + arenaRecord.getWave()));
			for (int i = 0; i < arenaRecord.getPlayers().size() / 4 + 1; i++) {
				StringBuilder players = new StringBuilder(Utils.format("&7"));
				if (i * 4 + 4 < arenaRecord.getPlayers().size()) {
					for (int j = i * 4; j < i * 4 + 4; j++)
						players.append(arenaRecord.getPlayers().get(j)).append(", ");
					records.add(Utils.format(players.substring(0, players.length() - 1)));
				} else {
					for (int j = i * 4; j < arenaRecord.getPlayers().size(); j++)
						players.append(arenaRecord.getPlayers().get(j)).append(", ");
					records.add(Utils.format(players.substring(0, players.length() - 2)));
				}
			}
		});
		inv.setItem(32, Utils.createItem(Material.GOLDEN_HELMET, Utils.format("&e&lArena Records"), Utils.BUTTON_FLAGS,
				null, records));

		return inv;
	}

	// Shows sign GUI to name arena
	public static void nameArena(Player player, Arena arena) {
		Location location = player.getLocation();
		location.setY(0);
		BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
		player.sendBlockChange(location, Bukkit.createBlockData(Material.OAK_SIGN));

		TileEntitySign sign = new TileEntitySign();
		sign.setPosition(blockPosition);
		sign.lines[0] = CraftSign.sanitizeLines(new String[]{String.format("Rename Arena %d:", arena.getArena())})[0];
		sign.lines[1] = CraftSign.sanitizeLines(new String[]{Utils.format("===============")})[0];
		sign.lines[3] = CraftSign.sanitizeLines(new String[]{Utils.format("===============")})[0];
		sign.lines[2] = CraftSign.sanitizeLines(new String[]{arena.getName()})[0];
		sign.update();

		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		entityPlayer.playerConnection.sendPacket(sign.getUpdatePacket());
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(blockPosition));
	}

	// Easy way to get a string for a toggle status
	private static String getToggleStatus(boolean status) {
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
		assert meta != null;
		List<String> lore = meta.getLore();
		assert lore != null;
		int price = (int) Math.round(Integer.parseInt(lore.get(lore.size() - 1).substring(10)) * modifier / 5) * 5;
		lore.set(lore.size() - 1, Utils.format("&2Gems: &a" + price));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
