package me.theguyhere.villagerdefense.plugin.GUI;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Inventories {
	// Easily alternate between different materials
	public static final Material[] INFO_BOARD_MATS = {Material.DARK_OAK_SIGN, Material.BIRCH_SIGN};
	public static final Material[] MONSTER_MATS = {Material.SKELETON_SKULL, Material.ZOMBIE_HEAD};
	public static final Material[] VILLAGER_MATS = {Material.WITHER_ROSE, Material.POPPY};
	
	// Menu of all the arenas
	public static Inventory createArenasInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lVillager Defense Arenas"));

		// Options to interact with all 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (GameManager.getArena(i) == null)
				inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE,
						CommunicationManager.format("&c&lCreate Arena " + (i + 1))));
			else
				inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE,
						CommunicationManager.format("&a&lEdit " + GameManager.getArena(i).getName())));
		}

		// Option to set lobby location
		inv.setItem(45, ItemManager.createItem(Material.BELL, CommunicationManager.format("&2&lLobby"),
				CommunicationManager.format("&7Manage minigame lobby")));

		// Option to set info hologram
		inv.setItem(46, ItemManager.createItem(Material.OAK_SIGN, CommunicationManager.format("&6&lInfo Boards"),
				CommunicationManager.format("&7Manage info boards")));

		// Option to set leaderboard hologram
		inv.setItem(47, ItemManager.createItem(Material.GOLDEN_HELMET, CommunicationManager.format("&e&lLeaderboards"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&7Manage leaderboards")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Menu for lobby
	public static Inventory createLobbyInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lLobby"));

		// Option to create or relocate the lobby
		if (DataManager.getConfigLocation(plugin, "lobby") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Lobby?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing info boards
	public static Inventory createInfoBoardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lInfo Boards"));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible info boards
		for (int i = 0; i < 8; i++) {
			// Check if the info board exists
			if (!plugin.getArenaData().contains("infoBoard." + i))
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, ItemManager.createItem(INFO_BOARD_MATS[index], CommunicationManager.format("&6&lInfo Board " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific info board
	public static Inventory createInfoBoardMenu(Main plugin, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(slot), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lInfo Board " + slot));

		// Option to create or relocate info board
		if (DataManager.getConfigLocation(plugin, "infoBoard." + slot) == null)
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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(slot), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Info Board?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for leaderboards
	public static Inventory createLeaderboardInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&e&lLeaderboards"));

		// Option to modify total kills leaderboard
		inv.setItem(0, ItemManager.createItem(Material.DRAGON_HEAD, CommunicationManager.format("&4&lTotal Kills Leaderboard")));

		// Option to modify top kills leaderboard
		inv.setItem(1, ItemManager.createItem(Material.ZOMBIE_HEAD, CommunicationManager.format("&c&lTop Kills Leaderboard")));

		// Option to modify total gems leaderboard
		inv.setItem(2, ItemManager.createItem(Material.EMERALD_BLOCK, CommunicationManager.format("&2&lTotal Gems Leaderboard")));

		// Option to modify top balance leaderboard
		inv.setItem(3, ItemManager.createItem(Material.EMERALD, CommunicationManager.format("&a&lTop Balance Leaderboard")));

		// Option to modify top wave leaderboard
		inv.setItem(4, ItemManager.createItem(Material.GOLDEN_SWORD, CommunicationManager.format("&9&lTop Wave Leaderboard"),
				ItemManager.BUTTON_FLAGS, null));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the total kills leaderboard
	public static Inventory createTotalKillsLeaderboardInventory(Main plugin) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lTotal Kills Leaderboard"));

		// Option to create or relocate the leaderboard
		if (DataManager.getConfigLocation(plugin, "leaderboard.totalKills") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&c&lTop Kills Leaderboard"));

		// Option to create or relocate the leaderboard
		if (DataManager.getConfigLocation(plugin, "leaderboard.topKills") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lTotal Gems Leaderboard"));

		// Option to create or relocate the leaderboard
		if (DataManager.getConfigLocation(plugin, "leaderboard.totalGems") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&a&lTop Balance Leaderboard"));

		// Option to create or relocate the leaderboard
		if (DataManager.getConfigLocation(plugin, "leaderboard.topBalance") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&9&lTop Wave Leaderboard"));

		// Option to create or relocate the leaderboard
		if (DataManager.getConfigLocation(plugin, "leaderboard.topWave") == null)
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
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Total Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top kills leaderboard
	public static Inventory createTopKillsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Top Kills Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for total gems leaderboard
	public static Inventory createTotalGemsConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Total Gems Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top balance leaderboard
	public static Inventory createTopBalanceConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Top Balance Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for top wave leaderboard
	public static Inventory createTopWaveConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Top Wave Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing an arena
	public static  Inventory createArenaInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lEdit " + arenaInstance.getName()));

		// Option to edit name
		inv.setItem(0, ItemManager.createItem(Material.NAME_TAG, CommunicationManager.format("&6&lEdit Name")));

		// Option to edit game portal and leaderboard
		inv.setItem(1, ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&5&lPortal and Leaderboard")));

		// Option to edit player settings
		inv.setItem(2, ItemManager.createItem(Material.PLAYER_HEAD, CommunicationManager.format("&d&lPlayer Settings")));

		// Option to edit mob settings
		inv.setItem(3, ItemManager.createItem(Material.ZOMBIE_SPAWN_EGG, CommunicationManager.format("&2&lMob Settings")));

		// Option to edit shop settings
		inv.setItem(4, ItemManager.createItem(Material.GOLD_BLOCK, CommunicationManager.format("&e&lShop Settings")));

		// Option to edit miscellaneous game settings
		inv.setItem(5, ItemManager.createItem(Material.REDSTONE, CommunicationManager.format("&7&lGame Settings")));

		// Option to close the arena
		String closed;
		if (arenaInstance.isClosed())
			closed = "&c&lCLOSED";
		else closed = "&a&lOPEN";
		inv.setItem(6, ItemManager.createItem(Material.NETHER_BRICK_FENCE, CommunicationManager.format("&9&lClose Arena: " + closed)));

		// Option to remove arena
		inv.setItem(7, InventoryItems.remove("ARENA"));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing an arena
	public static Inventory createArenaConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove " + GameManager.getArena(arena).getName() + '?'));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the portal and leaderboard of an arena
	public static Inventory createPortalInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&5&lPortal/LBoard: " + arenaInstance.getName()));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Portal?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Confirmation menu for removing the arena leaderboard
	public static Inventory createArenaBoardConfirmInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Leaderboard?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the player settings of an arena
	public static Inventory createPlayersInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&d&lPlayer Settings: " + arenaInstance.getName()));

		// Option to edit player spawn
		inv.setItem(0, ItemManager.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&5&lPlayer Spawn")));

		// Option to toggle player spawn particles
		inv.setItem(1, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&lSpawn Particles: " + getToggleStatus(arenaInstance.hasSpawnParticles())),
				CommunicationManager.format("&7Particles showing where the spawn is"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit waiting room
		inv.setItem(2, ItemManager.createItem(Material.CLOCK, CommunicationManager.format("&b&lWaiting Room"),
				CommunicationManager.format("&7An optional room to wait in before"), CommunicationManager.format("&7the game starts")));

		// Option to edit max players
		inv.setItem(3, ItemManager.createItem(Material.NETHERITE_HELMET,
				CommunicationManager.format("&4&lMaximum Players"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Maximum players the game will have")));

		// Option to edit min players
		inv.setItem(4, ItemManager.createItem(Material.NETHERITE_BOOTS,
				CommunicationManager.format("&2&lMinimum Players"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Minimum players needed for game to start")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the player spawn of an arena
	public static Inventory createPlayerSpawnInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&d&lPlayer Spawn: " + arenaInstance.getName()));

		// Option to create or relocate player spawn
		if (arenaInstance.getPlayerSpawn() != null)
			inv.setItem(0, InventoryItems.relocate("Spawn"));
		else inv.setItem(0, InventoryItems.create("Spawn"));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the waiting room of an arena
	public static Inventory createWaitingRoomInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&b&lWaiting Room: " + arenaInstance.getName()));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Waiting Room?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for changing max players in an arena
	public static Inventory createMaxPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lMaximum Players: " + GameManager.getArena(arena).getMaxPlayers()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for changing min players in an arena
	public static Inventory createMinPlayerInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lMinimum Players: " + GameManager.getArena(arena).getMinPlayers()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the mob settings of an arena
	public static Inventory createMobsInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lMob Settings: " + arenaInstance.getName()));

		// Option to edit monster spawns
		inv.setItem(0, ItemManager.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&2&lMonster Spawns")));

		// Option to toggle monster spawn particles
		inv.setItem(1, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&a&lMonster Spawn Particles: " + getToggleStatus(arenaInstance.hasMonsterParticles())),
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit villager spawns
		inv.setItem(2, ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&5&lVillager Spawns")));

		// Option to toggle villager spawn particles
		inv.setItem(3, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&lVillager Spawn Particles: " + getToggleStatus(arenaInstance.hasVillagerParticles())),
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit spawn table
		inv.setItem(4, ItemManager.createItem(Material.DRAGON_HEAD, CommunicationManager.format("&3&lSpawn Table")));

		// Option to toggle dynamic mob count
		inv.setItem(5, ItemManager.createItem(Material.SLIME_BALL,
				CommunicationManager.format("&e&lDynamic Mob Count: " + getToggleStatus(arenaInstance.hasDynamicCount())),
				CommunicationManager.format("&7Mob count adjusting based on"), CommunicationManager.format("&7number of players")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the monster spawns of an arena
	public static Inventory createMonsterSpawnInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lMonster Spawns: " + arenaInstance.getName()));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible mob spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (arenaInstance.getMonsterSpawn(i) != null)
				index = 1;
			else index = 0;

			// Create and set item
			inv.setItem(i, ItemManager.createItem(MONSTER_MATS[index],
					CommunicationManager.format("&2&lMob Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific monster spawn of an arena
	public static Inventory createMonsterSpawnMenu(int arena, int slot) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9,
				CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lMonster Spawn " + (slot + 1) + ": " + arenaInstance.getName()));

		// Option to create or relocate monster spawn
		if (arenaInstance.getMonsterSpawn(slot) != null)
			inv.setItem(0, InventoryItems.relocate("Spawn"));
		else inv.setItem(0, InventoryItems.create("Spawn"));

		// Option to teleport to monster spawn
		inv.setItem(1, InventoryItems.teleport("Spawn"));

		// Option to center the monster spawn
		inv.setItem(2, InventoryItems.center("Spawn"));

		// Option to remove monster spawn
		inv.setItem(3, InventoryItems.remove("SPAWN"));

		// Toggle to set monster spawn type
		switch (arenaInstance.getMonsterSpawnType(slot)) {
			case 1:
				inv.setItem(4, ItemManager.createItem(Material.GUNPOWDER,
						CommunicationManager.format("&5&lType: Ground")));
				break;
			case 2:
				inv.setItem(4, ItemManager.createItem(Material.FEATHER,
					CommunicationManager.format("&5&lType: Flying")));
				break;
			default:
				inv.setItem(4, ItemManager.createItem(Material.BONE,
					CommunicationManager.format("&5&lType: All")));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing monster spawns
	public static Inventory createMonsterSpawnConfirmInventory(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9,
				CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Monster Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the villager spawns of an arena
	public static Inventory createVillagerSpawnInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&5&lVillager Spawns: " + arenaInstance.getName()));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible villager spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (arenaInstance.getVillagerSpawn(i) != null)
				index = 1;
			else index = 0;

			// Create and set item
			inv.setItem(i, ItemManager.createItem(VILLAGER_MATS[index],
					CommunicationManager.format("&5&lVillager Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing a specific villager spawn of an arena
	public static Inventory createVillagerSpawnMenu(int arena, int slot) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&5&lVillager Spawn " + (slot + 1) + ": " + arenaInstance.getName()));

		// Option to create or relocate villager spawn
		if (arenaInstance.getVillagerSpawn(slot) != null)
			inv.setItem(0, InventoryItems.relocate("Spawn"));
		else inv.setItem(0, InventoryItems.create("Spawn"));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Villager Spawn?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Spawn table menu for an arena
	public static Inventory createSpawnTableInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);
		String chosen = arenaInstance.getSpawnTableFile();
		Inventory inv;

		// Create inventory
		if (arenaInstance.getSpawnTableFile().equals("custom"))
			 inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
					CommunicationManager.format("&3&lSpawn Table: a" + arena + ".yml"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&3&lSpawn Table: " + arenaInstance.getSpawnTableFile() + ".yml"));

		// Option to set spawn table to default
		inv.setItem(0, ItemManager.createItem(Material.OAK_WOOD,
				CommunicationManager.format("&4&lDefault"), ItemManager.BUTTON_FLAGS,
				chosen.equals("default") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to default.yml")));

		// Option to set spawn table to global option 1
		inv.setItem(1, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&6&lOption 1"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option1") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option1.yml")));

		// Option to set spawn table to global option 2
		inv.setItem(2, ItemManager.createItem(Material.ORANGE_CONCRETE,
				CommunicationManager.format("&6&lOption 2"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option2") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option2.yml")));

		// Option to set spawn table to global option 3
		inv.setItem(3, ItemManager.createItem(Material.YELLOW_CONCRETE,
				CommunicationManager.format("&6&lOption 3"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option3") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option3.yml")));

		// Option to set spawn table to global option 4
		inv.setItem(4, ItemManager.createItem(Material.BROWN_CONCRETE,
				CommunicationManager.format("&6&lOption 4"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option4") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option4.yml")));

		// Option to set spawn table to global option 5
		inv.setItem(5, ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
				CommunicationManager.format("&6&lOption 5"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option5") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option5.yml")));

		// Option to set spawn table to global option 6
		inv.setItem(6, ItemManager.createItem(Material.WHITE_CONCRETE,
				CommunicationManager.format("&6&lOption 6"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option6") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option6.yml")));

		// Option to set spawn table to custom option
		inv.setItem(7, ItemManager.createItem(Material.BIRCH_WOOD,
				CommunicationManager.format("&e&lCustom"), ItemManager.BUTTON_FLAGS,
				chosen.length() < 4 ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to a[arena number].yml"),
				CommunicationManager.format("&7(Check the arena number in arenaData.yml)")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the shop settings of an arena
	public static Inventory createShopsInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&e&lShop Settings: " + arenaInstance.getName()));

		// Option to create a custom shop
		inv.setItem(0, ItemManager.createItem(Material.QUARTZ,
				CommunicationManager.format("&a&lEdit Custom Shop")));

		// Option to toggle default shop
		inv.setItem(1, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&6&lDefault Shop: " + getToggleStatus(arenaInstance.hasNormal())),
				CommunicationManager.format("&7Turn default shop on and off")));

		// Option to toggle custom shop
		inv.setItem(2, ItemManager.createItem(Material.QUARTZ_BLOCK,
				CommunicationManager.format("&2&lCustom Shop: " + getToggleStatus(arenaInstance.hasCustom())),
				CommunicationManager.format("&7Turn custom shop on and off")));

		// Option to toggle custom shop
		inv.setItem(3, ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&3&lEnchants Shop: " + getToggleStatus(arenaInstance.hasEnchants())),
				CommunicationManager.format("&7Turn enchants shop on and off")));

		// Option to toggle community chest
		inv.setItem(4, ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&lCommunity Chest: " + getToggleStatus(arenaInstance.hasCommunity())),
				CommunicationManager.format("&7Turn community chest on and off")));

		// Option to toggle dynamic prices
		inv.setItem(5, ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&b&lDynamic Prices: " + getToggleStatus(arenaInstance.hasDynamicPrices())),
				CommunicationManager.format("&7Prices adjusting based on number of"),
				CommunicationManager.format("&7players in the game")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for adding custom items
	public static Inventory createCustomItemsInventory(int arena, int slot) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 27, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lEdit Item"));

		// Item of interest
		inv.setItem(4, arenaInstance.getCustomShop().getItem(slot));

		// Option to set un-purchasable
		inv.setItem(8, ItemManager.createItem(Material.BEDROCK, CommunicationManager.format("&5&lToggle Un-purchasable")));

		// Option to increase by 1
		inv.setItem(9, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l+1 gem")));

		// Option to increase by 10
		inv.setItem(11, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l+10 gems")));

		// Option to increase by 100
		inv.setItem(13, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l+100 gems")));

		// Option to increase by 1000
		inv.setItem(15, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l+1000 gems")));

		// Option to delete item
		inv.setItem(17, ItemManager.createItem(Material.LAVA_BUCKET, CommunicationManager.format("&4&lDELETE")));

		// Option to decrease by 1
		inv.setItem(18, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&l-1 gem")));

		// Option to decrease by 10
		inv.setItem(20, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&l-10 gems")));

		// Option to decrease by 100
		inv.setItem(22, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&l-100 gems")));

		// Option to decrease by 1000
		inv.setItem(24, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&l-1000 gems")));

		// Option to exit
		inv.setItem(26, InventoryItems.exit());

		return inv;
	}

	// Confirmation menu for removing custom item
	public static Inventory createCustomItemConfirmInventory(int arena, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena, slot), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Custom Item?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing the game settings of an arena
	public static Inventory createGameSettingsInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 18, CommunicationManager.format("&k") +
				CommunicationManager.format("&8&lGame Settings: " + arenaInstance.getName()));

		// Option to change max waves
		inv.setItem(0, ItemManager.createItem(Material.NETHERITE_SWORD,
				CommunicationManager.format("&3&lMax Waves"),
				ItemManager.BUTTON_FLAGS,
				null));

		// Option to wave time limit
		inv.setItem(1, ItemManager.createItem(Material.CLOCK, CommunicationManager.format("&2&lWave Time Limit")));

		// Option to toggle dynamic wave time limit
		inv.setItem(2, ItemManager.createItem(Material.SNOWBALL,
				CommunicationManager.format("&a&lDynamic Time Limit: " + getToggleStatus(arenaInstance.hasDynamicLimit())),
				CommunicationManager.format("&7Wave time limit adjusting based on"),
				CommunicationManager.format("&7in-game difficulty")));

		// Option to edit allowed kits
		inv.setItem(3, ItemManager.createItem(Material.ENDER_CHEST, CommunicationManager.format("&9&lAllowed Kits")));

		// Option to edit difficulty label
		inv.setItem(4, ItemManager.createItem(Material.NAME_TAG, CommunicationManager.format("&6&lDifficulty Label")));

		// Option to adjust overall difficulty multiplier
		inv.setItem(5, ItemManager.createItem(Material.TURTLE_HELMET,
				CommunicationManager.format("&4&lDifficulty Multiplier"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Determines difficulty increase rate")));

		// Option to toggle dynamic difficulty
		inv.setItem(6, ItemManager.createItem(Material.MAGMA_CREAM,
				CommunicationManager.format("&6&lDynamic Difficulty: " + getToggleStatus(arenaInstance.hasDynamicDifficulty())),
				CommunicationManager.format("&7Difficulty adjusting based on"), CommunicationManager.format("&7number of players")));

		// Option to toggle late arrival
		inv.setItem(7, ItemManager.createItem(Material.DAYLIGHT_DETECTOR,
				CommunicationManager.format("&e&lLate Arrival: " + getToggleStatus(arenaInstance.hasLateArrival())),
				CommunicationManager.format("&7Allows players to join after"), CommunicationManager.format("&7the game has started")));

		// Option to toggle experience drop
		inv.setItem(9, ItemManager.createItem(Material.EXPERIENCE_BOTTLE,
				CommunicationManager.format("&b&lExperience Drop: " + getToggleStatus(arenaInstance.hasExpDrop())),
				CommunicationManager.format("&7Change whether experience drop or go"),
				CommunicationManager.format("&7straight into the killer's experience bar")));

		// Option to toggle item dropping
		inv.setItem(10, ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&9&lItem Drop: " + getToggleStatus(arenaInstance.hasGemDrop())),
				CommunicationManager.format("&7Change whether gems and loot drop"),
				CommunicationManager.format("&7as physical items or go straight"),
				CommunicationManager.format("&7into the killer's balance/inventory")));

		// Option to set arena bounds
		inv.setItem(11, ItemManager.createItem(Material.BEDROCK, CommunicationManager.format("&4&lArena Bounds"),
				CommunicationManager.format("&7Bounds determine where players are"),
				CommunicationManager.format("&7allowed to go and where the game will"),
				CommunicationManager.format("&7function. Avoid building past arena bounds.")));

		// Option to edit wolf cap
		inv.setItem(12, ItemManager.createItem(Material.BONE, CommunicationManager.format("&6&lWolf Cap"),
				CommunicationManager.format("&7Maximum wolves a player can have")));

		// Option to edit golem cap
		inv.setItem(13, ItemManager.createItem(Material.IRON_INGOT, CommunicationManager.format("&e&lIron Golem Cap"),
				CommunicationManager.format("&7Maximum iron golems an arena can have")));

		// Option to edit sounds
		inv.setItem(14, ItemManager.createItem(Material.MUSIC_DISC_13,
				CommunicationManager.format("&d&lSounds"),
				ItemManager.BUTTON_FLAGS,
				null));

		// Option to copy game settings from another arena or a preset
		inv.setItem(15, ItemManager.createItem(Material.WRITABLE_BOOK,
				CommunicationManager.format("&f&lCopy Game Settings"),
				CommunicationManager.format("&7Copy settings of another arena or"),
				CommunicationManager.format("&7choose from a menu of presets")));

		// Option to exit
		inv.setItem(17, InventoryItems.exit());

		return inv;
	}

	// Menu for changing max waves of an arena
	public static Inventory createMaxWaveInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (GameManager.getArena(arena).getMaxWaves() < 0)
			inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
					CommunicationManager.format("&3&lMaximum Waves: Unlimited"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&3&lMaximum Waves: " + GameManager.getArena(arena).getMaxWaves()));

		// Option to decrease
		for (int i = 0; i < 3; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to set to unlimited
		inv.setItem(3, ItemManager.createItem(Material.ORANGE_CONCRETE, CommunicationManager.format("&6&lUnlimited")));

		// Option to reset to 1
		inv.setItem(4, ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE, CommunicationManager.format("&3&lReset to 1")));

		// Option to increase
		for (int i = 5; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for changing wave time limit of an arena
	public static Inventory createWaveTimeLimitInventory(int arena) {
		Inventory inv;

		// Create inventory
		if (GameManager.getArena(arena).getWaveTimeLimit() < 0)
			inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
					CommunicationManager.format("&2&lWave Time Limit: Unlimited"));
		else inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lWave Time Limit: " + GameManager.getArena(arena).getWaveTimeLimit()));

		// Option to decrease
		for (int i = 0; i < 3; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to set to unlimited
		inv.setItem(3, ItemManager.createItem(Material.ORANGE_CONCRETE, CommunicationManager.format("&6&lUnlimited")));

		// Option to reset to 1
		inv.setItem(4, ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE, CommunicationManager.format("&3&lReset to 1")));

		// Option to increase
		for (int i = 5; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for allowed kits of an arena
	public static Inventory createAllowedKitsInventory(int arena, boolean mock) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54, CommunicationManager.format("&k") +
				(mock ? CommunicationManager.format("&9&lAllowed Kits: " + arenaInstance.getName()) :
						CommunicationManager.format("&9&lAllowed Kits")));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE, CommunicationManager.format("&a&lGift Kits"),
					CommunicationManager.format("&7Kits give one-time benefits"), CommunicationManager.format("&7per game or respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE, CommunicationManager.format("&d&lAbility Kits"),
					CommunicationManager.format("&7Kits give special ability per respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE, CommunicationManager.format("&e&lEffect Kits"),
					CommunicationManager.format("&7Kits give players a permanent"), CommunicationManager.format("&7special effect")));

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
		String label = GameManager.getArena(arena).getDifficultyLabel();
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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lDifficulty Label: " + label));

		// "Easy" option
		inv.setItem(0, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&lEasy")));

		// "Medium" option
		inv.setItem(1, ItemManager.createItem(Material.YELLOW_CONCRETE, CommunicationManager.format("&e&lMedium")));

		// "Hard" option
		inv.setItem(2, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&lHard")));

		// "Insane" option
		inv.setItem(3, ItemManager.createItem(Material.MAGENTA_CONCRETE, CommunicationManager.format("&d&lInsane")));

		// "None" option
		inv.setItem(4, ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE, CommunicationManager.format("&7&lNone")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for changing the difficulty multiplier of an arena
	public static Inventory createDifficultyMultiplierInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lDifficulty Multiplier: " + GameManager.getArena(arena).getDifficultyMultiplier()));

		// "1" option
		inv.setItem(0, ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE, CommunicationManager.format("&b&l1")));

		// "2" option
		inv.setItem(2, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l2")));

		// "3" option
		inv.setItem(4, ItemManager.createItem(Material.YELLOW_CONCRETE, CommunicationManager.format("&6&l3")));

		// "4" option
		inv.setItem(6, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&l4")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for selecting arena bound corners
	public static Inventory createBoundsInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lArena Bounds: " + arenaInstance.getName()));

		// Option to interact with corner 1
		inv.setItem(0, ItemManager.createItem(Material.TORCH, CommunicationManager.format("&b&lCorner 1: " +
				(arenaInstance.getCorner1() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to interact with corner 2
		inv.setItem(3, ItemManager.createItem(Material.SOUL_TORCH, CommunicationManager.format("&9&lCorner 2: " +
				(arenaInstance.getCorner2() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to toggle arena border particles
		inv.setItem(6, ItemManager.createItem(Material.FIREWORK_ROCKET, CommunicationManager.format("&4&lBorder Particles: " +
				getToggleStatus(arenaInstance.hasBorderParticles()))));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing corner 1 of an arena
	public static Inventory createCorner1Inventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&b&lCorner 1: " + arenaInstance.getName()));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Corner 1?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for editing corner 2 of an arena
	public static Inventory createCorner2Inventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&9&lCorner 2: " + arenaInstance.getName()));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lRemove Corner 2?"));

		// "No" option
		inv.setItem(0, InventoryItems.no());

		// "Yes" option
		inv.setItem(8, InventoryItems.yes());

		return inv;
	}

	// Menu for changing wolf cap of an arena
	public static Inventory createWolfCapInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lWolf Cap: " + GameManager.getArena(arena).getWolfCap()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for changing iron golem cap of an arena
	public static Inventory createGolemCapInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&e&lIron Golem Cap: " + GameManager.getArena(arena).getGolemCap()));

		// Option to decrease
		for (int i = 0; i < 4; i++)
			inv.setItem(i, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&lDecrease")));

		// Option to increase
		for (int i = 4; i < 8; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&2&lIncrease")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the sounds of an arena
	public static Inventory createSoundsInventory(int arena) {
		Arena arenaInstance = GameManager.getArena(arena);

		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&d&lSounds: " + GameManager.getArena(arena).getName()));

		// Option to edit win sound
		inv.setItem(0, ItemManager.createItem(Material.MUSIC_DISC_PIGSTEP,
				CommunicationManager.format("&a&lWin Sound: " + getToggleStatus(arenaInstance.hasWinSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when game ends and players win")));

		// Option to edit lose sound
		inv.setItem(1, ItemManager.createItem(Material.MUSIC_DISC_11,
				CommunicationManager.format("&e&lLose Sound: " + getToggleStatus(arenaInstance.hasLoseSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when game ends and players lose")));

		// Option to edit wave start sound
		inv.setItem(2, ItemManager.createItem(Material.MUSIC_DISC_CAT,
				CommunicationManager.format("&2&lWave Start Sound: " + getToggleStatus(arenaInstance.hasWaveStartSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a wave starts")));

		// Option to edit wave finish sound
		inv.setItem(3, ItemManager.createItem(Material.MUSIC_DISC_BLOCKS,
				CommunicationManager.format("&9&lWave Finish Sound: " + getToggleStatus(arenaInstance.hasWaveFinishSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a wave ends")));

		// Option to edit waiting music
		inv.setItem(4, ItemManager.createItem(Material.MUSIC_DISC_MELLOHI,
				CommunicationManager.format("&6&lWaiting Sound"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played while players wait"), CommunicationManager.format("&7for the game to start")));

		// Option to edit gem pickup sound
		inv.setItem(5, ItemManager.createItem(Material.MUSIC_DISC_FAR,
				CommunicationManager.format("&b&lGem Pickup Sound: " + getToggleStatus(arenaInstance.hasGemSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when players pick up gems")));

		// Option to edit player death sound
		inv.setItem(6, ItemManager.createItem(Material.MUSIC_DISC_CHIRP,
				CommunicationManager.format("&4&lPlayer Death Sound: " + getToggleStatus(arenaInstance.hasPlayerDeathSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a player dies")));

		// Option to edit ability sound
		inv.setItem(7, ItemManager.createItem(Material.MUSIC_DISC_MALL,
				CommunicationManager.format("&d&lAbility Sound: " + getToggleStatus(arenaInstance.hasAbilitySound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a player uses their ability")));

		// Option to exit
		inv.setItem(8, InventoryItems.exit());

		return inv;
	}

	// Menu for editing the win sound of an arena
	public static Inventory createWaitSoundInventory(int arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 18, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&lWaiting Sound: " + GameManager.getArena(arena).getWaitingSoundName()));

		Arena arenaInstance = GameManager.getArena(arena);

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena), 54,  CommunicationManager.format("&k") +
				CommunicationManager.format("&8&lCopy Game Settings"));

		// Options to choose any of the 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (GameManager.getArena(i) == null)
				inv.setItem(i, ItemManager.createItem(Material.BLACK_CONCRETE,
						CommunicationManager.format("&c&lArena " + (i + 1) + " not available")));
			else if (i == arena)
				inv.setItem(i, ItemManager.createItem(Material.GRAY_GLAZED_TERRACOTTA,
						CommunicationManager.format("&6&l" + GameManager.getArena(i).getName())));
			else
				inv.setItem(i, ItemManager.createItem(Material.WHITE_CONCRETE,
						CommunicationManager.format("&a&lCopy " + GameManager.getArena(i).getName())));
		}

		// Easy preset
		inv.setItem(45, ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&lEasy Preset")));

		// Medium preset
		inv.setItem(47, ItemManager.createItem(Material.YELLOW_CONCRETE, CommunicationManager.format("&e&lMedium Preset")));

		// Hard preset
		inv.setItem(49, ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&lHard Preset")));

		// Insane preset
		inv.setItem(51, ItemManager.createItem(Material.MAGENTA_CONCRETE, CommunicationManager.format("&d&lInsane Preset")));

		// Option to exit
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Generate the shop menu
	public static Inventory createShop(int level, Arena arena) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&lLevel &9&l" + level + " &2&lItem Shop"));

		inv.setItem(0, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), ItemManager.BUTTON_FLAGS,
				arena.hasNormal() ? ItemManager.glow() : null));

		inv.setItem(1, ItemManager.createItem(Material.GOLDEN_CHESTPLATE,
				CommunicationManager.format("&5&lLevel &9&l" + level + " &5&lArmor Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), ItemManager.BUTTON_FLAGS,
				arena.hasNormal() ? ItemManager.glow() : null));

		inv.setItem(2, ItemManager.createItem(Material.GOLDEN_APPLE,
				CommunicationManager.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop" +
						(arena.hasNormal() ? "" : " &4&l[DISABLED]")), ItemManager.BUTTON_FLAGS,
				arena.hasNormal() ? ItemManager.glow() : null));

		inv.setItem(4, ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&a&lEnchants Shop" + (arena.hasEnchants() ? "" : " &4&l[DISABLED]")),
				ItemManager.BUTTON_FLAGS, arena.hasEnchants() ? ItemManager.glow() : null));

		inv.setItem(6, ItemManager.createItem(Material.QUARTZ, CommunicationManager.format("&6&lCustom Shop" +
				(arena.hasCustom() ? "" : " &4&l[DISABLED]")), ItemManager.BUTTON_FLAGS,
				arena.hasNormal() ? ItemManager.glow() : null));

		inv.setItem(8, ItemManager.createItem(Material.CHEST, CommunicationManager.format("&d&lCommunity Chest" +
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
		Inventory inv = Bukkit.createInventory(null, 27, CommunicationManager.format("&k") +
				CommunicationManager.format("&4&lLevel &9&l" + level + " &4&lWeapon Shop"));

		// Fill in swords
		List<ItemStack> swords = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			swords.add(GameItems.sword(level));
		sort(swords);
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(swords.get(i), modifier));

		// Fill in axes
		List<ItemStack> axes = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			axes.add(GameItems.axe(level));
		sort(axes);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(axes.get(i), modifier));

		// Fill in range
		List<ItemStack> ranges = new ArrayList<>();
		for (int i = 0; i < 5; i++)
			ranges.add(GameItems.randRange(level));
		sort(ranges);
		for (int i = 0; i < 5; i++)
			inv.setItem(i + 9, modifyPrice(ranges.get(i), modifier));

		// Fill in ammo
		List<ItemStack> ammo = new ArrayList<>();
		for (int i = 0; i < 3; i++)
			ammo.add(GameItems.randAmmo(level));
		sort(ammo);
		for (int i = 0; i < 3; i++)
			inv.setItem(i + 15, modifyPrice(ammo.get(i), modifier));

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
		Inventory inv = Bukkit.createInventory(null, 27, CommunicationManager.format("&k") +
				CommunicationManager.format("&5&lLevel &9&l" + level + " &5&lArmor Shop"));

		// Fill in helmets
		List<ItemStack> helmets = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			helmets.add(GameItems.helmet(level));
		sort(helmets);
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(helmets.get(i), modifier));

		// Fill in chestplates
		List<ItemStack> chestplates = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			chestplates.add(GameItems.chestplate(level));
		sort(chestplates);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(chestplates.get(i), modifier));

		// Fill in leggings
		List<ItemStack> leggings = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			leggings.add(GameItems.leggings(level));
		sort(leggings);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 9, modifyPrice(leggings.get(i), modifier));

		// Fill in boots
		List<ItemStack> boots = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			boots.add(GameItems.boots(level));
		sort(boots);
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
		Inventory inv = Bukkit.createInventory(null, 18, CommunicationManager.format("&k") +
				CommunicationManager.format("&3&lLevel &9&l" + level + " &3&lConsumables Shop"));

		// Fill in food
		List<ItemStack> foods = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			foods.add(GameItems.randFood(level));
		sort(foods);
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(foods.get(i), modifier));

		// Fill in other
		List<ItemStack> others = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			others.add(GameItems.randOther(level));
		sort(others);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(others.get(i), modifier));

		// Return option
		inv.setItem(13, InventoryItems.exit());

		return inv;
	}

	// Generate the enchants shop
	public static Inventory createEnchantsShop() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, CommunicationManager.format("&k") +
				CommunicationManager.format("&a&lEnchants Shop"));

		// Melee enchants
		inv.setItem(0, ItemManager.createItem(Material.PISTON, CommunicationManager.format("&a&lIncrease Knockback"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(1, ItemManager.createItem(Material.GOLDEN_HOE, CommunicationManager.format("&a&lIncrease Sweeping Edge"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(2, ItemManager.createItem(Material.DIAMOND_SWORD, CommunicationManager.format("&a&lIncrease Smite"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 7 XP Levels")));
		inv.setItem(3, ItemManager.createItem(Material.NETHERITE_AXE, CommunicationManager.format("&a&lIncrease Sharpness"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 8 XP Levels")));
		inv.setItem(4, ItemManager.createItem(Material.FIRE, CommunicationManager.format("&a&lIncrease Fire Aspect"),
				CommunicationManager.format("&2Costs 10 XP Levels")));

		// Ranged enchants
		inv.setItem(18, ItemManager.createItem(Material.STICKY_PISTON, CommunicationManager.format("&a&lIncrease Punch"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(19, ItemManager.createItem(Material.SPECTRAL_ARROW, CommunicationManager.format("&a&lIncrease Piercing"),
				CommunicationManager.format("&2Costs 5 XP Levels")));
		inv.setItem(20, ItemManager.createItem(Material.REDSTONE_TORCH, CommunicationManager.format("&a&lIncrease Quick Charge"),
				CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(21, ItemManager.createItem(Material.BOW, CommunicationManager.format("&a&lIncrease Power"),
				CommunicationManager.format("&2Costs 8 XP Levels")));
		inv.setItem(22, ItemManager.createItem(Material.TRIDENT, CommunicationManager.format("&a&lIncrease Loyalty"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(23, ItemManager.createItem(Material.MAGMA_BLOCK, CommunicationManager.format("&a&lAdd Flame"),
				CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(24, ItemManager.createItem(Material.CROSSBOW, CommunicationManager.format("&a&lAdd Multishot"),
				CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(25, ItemManager.createItem(Material.BEACON, CommunicationManager.format("&a&lAdd Infinity"),
				CommunicationManager.format("&2Costs 15 XP Levels")));

		// Armor enchants
		inv.setItem(36, ItemManager.createItem(Material.TNT, CommunicationManager.format("&a&lIncrease Blast Protection"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(37, ItemManager.createItem(Material.VINE, CommunicationManager.format("&a&lIncrease Thorns"),
				CommunicationManager.format("&2Costs 5 XP Levels")));
		inv.setItem(38, ItemManager.createItem(Material.ARROW, CommunicationManager.format("&a&lIncrease Projectile Protection"),
				CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(39, ItemManager.createItem(Material.SHIELD, CommunicationManager.format("&a&lIncrease Protection"),
				CommunicationManager.format("&2Costs 8 XP Levels")));

		// General enchants
		inv.setItem(43, ItemManager.createItem(Material.BEDROCK, CommunicationManager.format("&a&lIncrease Unbreaking"),
				CommunicationManager.format("&2Costs 3 XP Levels")));
		inv.setItem(44, ItemManager.createItem(Material.ANVIL, CommunicationManager.format("&a&lAdd Mending"),
				CommunicationManager.format("&2Costs 20 XP Levels")));

		// Return option
		inv.setItem(53, InventoryItems.exit());

		return inv;
	}

	// Display player stats
	public static Inventory createPlayerStatsInventory(Main plugin, String name) {
		FileConfiguration playerData = plugin.getPlayerData();

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, CommunicationManager.format("&k") +
				CommunicationManager.format("&2&l" + name + "'s Stats"));

		// Total kills
		inv.setItem(0, ItemManager.createItem(Material.DRAGON_HEAD, CommunicationManager.format("&4&lTotal Kills: &4" +
				playerData.getInt(name + ".totalKills")), CommunicationManager.format("&7Lifetime kill count")));

		// Top kills
		inv.setItem(1, ItemManager.createItem(Material.ZOMBIE_HEAD, CommunicationManager.format("&c&lTop Kills: &c" +
				playerData.getInt(name + ".topKills")), CommunicationManager.format("&7Most kills in a game")));

		// Total gems
		inv.setItem(2, ItemManager.createItem(Material.EMERALD_BLOCK, CommunicationManager.format("&2&lTotal Gems: &2" +
				playerData.getInt(name + ".totalGems")), CommunicationManager.format("&7Lifetime gems collected")));

		// Top balance
		inv.setItem(3, ItemManager.createItem(Material.EMERALD, CommunicationManager.format("&a&lTop Balance: &a" +
				playerData.getInt(name + ".topBalance")),
				CommunicationManager.format("&7Highest gem balance in a game")));

		// Top wave
		inv.setItem(4, ItemManager.createItem(Material.GOLDEN_SWORD, CommunicationManager.format("&3&lTop Wave: &3" +
				playerData.getInt(name + ".topWave")), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7Highest completed wave")));

		// Kits
		inv.setItem(6, ItemManager.createItem(Material.ENDER_CHEST, CommunicationManager.format("&9&lKits")));

		// Crystal balance
		inv.setItem(8, ItemManager.createItem(Material.DIAMOND, CommunicationManager.format("&b&lCrystal Balance: &b" +
				playerData.getInt(name + ".crystalBalance"))));

		return inv;
	}

	// Display kits for a player
	public static Inventory createPlayerKitsInventory(Main plugin, String name, String requester) {
		FileConfiguration playerData = plugin.getPlayerData();
		String path = name + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, CommunicationManager.format("&k") +
				CommunicationManager.format("&9&l" + name + "'s Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE, CommunicationManager.format("&a&lGift Kits"),
					CommunicationManager.format("&7Kits give one-time benefits"), CommunicationManager.format("&7per game or respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE, CommunicationManager.format("&d&lAbility Kits"),
					CommunicationManager.format("&7Kits give special ability per respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE, CommunicationManager.format("&e&lEffect Kits"),
					CommunicationManager.format("&7Kits give players a permanent"),
							CommunicationManager.format("&7special effect")));

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
			inv.setItem(52, ItemManager.createItem(Material.DIAMOND, CommunicationManager.format("&b&lCrystal Balance: &b" +
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
		Inventory inv = Bukkit.createInventory(null, 54, CommunicationManager.format("&k") +
				CommunicationManager.format("&9&l" + arena.getName() + " Kits"));

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE, CommunicationManager.format("&a&lGift Kits"),
					CommunicationManager.format("&7Kits give one-time benefits"), CommunicationManager.format("&7per game or respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE, CommunicationManager.format("&d&lAbility Kits"),
					CommunicationManager.format("&7Kits give special ability per respawn")));

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
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE, CommunicationManager.format("&e&lEffect Kits"),
					CommunicationManager.format("&7Kits give players a permanent"), CommunicationManager.format("&7special effect")));

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
		Inventory inv = Bukkit.createInventory(null, 18, CommunicationManager.format("&k") +
				CommunicationManager.format("&5&l" + arena.getName() + " Challenges"));

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
		Inventory inv = Bukkit.createInventory(new InventoryMeta(arena.getArena()), 36, CommunicationManager.format("&k") +
				CommunicationManager.format("&6&l" + arena.getName() + " Info"));

		// Maximum players
		inv.setItem(1, ItemManager.createItem(Material.NETHERITE_HELMET,
				CommunicationManager.format("&4&lMaximum players: &4" + arena.getMaxPlayers()), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7The most players an arena can have")));

		// Minimum players
		inv.setItem(2, ItemManager.createItem(Material.NETHERITE_BOOTS,
				CommunicationManager.format("&2&lMinimum players: &2" + arena.getMinPlayers()), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7The least players an arena can have to start")));

		// Max waves
		String waves;
		if (arena.getMaxWaves() < 0)
			waves = "Unlimited";
		else waves = Integer.toString(arena.getMaxWaves());
		inv.setItem(3, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&3&lMax waves: &3" + waves), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7The highest wave the arena will go to")));

		// Wave time limit
		String limit;
		if (arena.getWaveTimeLimit() < 0)
			limit = "Unlimited";
		else limit = arena.getWaveTimeLimit() + " minute(s)";
		inv.setItem(4, ItemManager.createItem(Material.CLOCK,
				CommunicationManager.format("&9&lWave time limit: &9" + limit),
				CommunicationManager.format("&7The time limit for each wave before"), CommunicationManager.format("&7the game ends")));

		// Wolf cap
		inv.setItem(5, ItemManager.createItem(Material.BONE, CommunicationManager.format("&6&lWolf Cap: &6" + arena.getWolfCap()),
				CommunicationManager.format("&7Maximum wolves a player can have")));

		// Golem cap
		inv.setItem(6, ItemManager.createItem(Material.IRON_INGOT, CommunicationManager.format("&e&lIron Golem Cap: &e" +
						arena.getGolemCap()),
				CommunicationManager.format("&7Maximum iron golems an arena can have")));

		// Allowed kits
		inv.setItem(7, ItemManager.createItem(Material.ENDER_CHEST, CommunicationManager.format("&9&lAllowed Kits")));

		// Dynamic mob count
		inv.setItem(10, ItemManager.createItem(Material.SLIME_BALL,
				CommunicationManager.format("&e&lDynamic Mob Count: " + getToggleStatus(arena.hasDynamicCount())),
				CommunicationManager.format("&7Mob count adjusting based on"), CommunicationManager.format("&7number of players")));

		// Dynamic difficulty
		inv.setItem(11, ItemManager.createItem(Material.MAGMA_CREAM,
				CommunicationManager.format("&6&lDynamic Difficulty: " + getToggleStatus(arena.hasDynamicDifficulty())),
				CommunicationManager.format("&7Difficulty adjusting based on"), CommunicationManager.format("&7number of players")));

		// Dynamic prices
		inv.setItem(12, ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&b&lDynamic Prices: " + getToggleStatus(arena.hasDynamicPrices())),
				CommunicationManager.format("&7Prices adjusting based on number of"),
				CommunicationManager.format("&7players in the game")));

		// Dynamic time limit
		inv.setItem(13, ItemManager.createItem(Material.SNOWBALL,
				CommunicationManager.format("&a&lDynamic Time Limit: " + getToggleStatus(arena.hasDynamicLimit())),
				CommunicationManager.format("&7Wave time limit adjusting based on"),
				CommunicationManager.format("&7in-game difficulty")));

		// Difficulty multiplier
		inv.setItem(14, ItemManager.createItem(Material.DAYLIGHT_DETECTOR,
				CommunicationManager.format("&e&lLate Arrival: " + getToggleStatus(arena.hasLateArrival())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Whether players can enter"),
				CommunicationManager.format("&7arenas late")));

		// Item dropping
		inv.setItem(15, ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&9&lItem Drop: " + getToggleStatus(arena.hasGemDrop())),
				CommunicationManager.format("&7Change whether gems drop as"),
				CommunicationManager.format("&7physical gems or go straight"),
				CommunicationManager.format("&7into the killer's balance")));

		// Experience drop
		inv.setItem(16, ItemManager.createItem(Material.EXPERIENCE_BOTTLE,
				CommunicationManager.format("&b&lExperience Drop: " + getToggleStatus(arena.hasExpDrop())),
				CommunicationManager.format("&7Change whether experience drop or go"),
				CommunicationManager.format("&7straight into the killer's experience bar")));

		// Player spawn particles
		inv.setItem(19, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&e&lPlayer Spawn Particles: " + getToggleStatus(arena.hasSpawnParticles()))));

		// Monster spawn particles
		inv.setItem(20, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&lMonster Spawn Particles: " + getToggleStatus(arena.hasMonsterParticles()))));

		// Villager spawn particles
		inv.setItem(21, ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&a&lVillager Spawn Particles: " + getToggleStatus(arena.hasVillagerParticles()))));

		// Default shop
		inv.setItem(22, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&6&lDefault Shop: " + getToggleStatus(arena.hasNormal()))));

		// Custom shop
		inv.setItem(23, ItemManager.createItem(Material.QUARTZ_BLOCK,
				CommunicationManager.format("&2&lCustom Shop: " + getToggleStatus(arena.hasCustom()))));

		// Enchants shop
		inv.setItem(24, ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&3&lEnchants Shop: " + getToggleStatus(arena.hasEnchants()))));

		// Community chest
		inv.setItem(25, ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&lCommunity Chest: " + getToggleStatus(arena.hasCommunity()))));

		// Custom shop inventory
		inv.setItem(30, ItemManager.createItem(Material.QUARTZ, CommunicationManager.format("&f&lCustom Shop Inventory")));

		// Difficulty multiplier
		inv.setItem(31, ItemManager.createItem(Material.TURTLE_HELMET,
				CommunicationManager.format("&4&lDifficulty Multiplier: &4" + arena.getDifficultyMultiplier()),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Determines difficulty increase rate")));

		// Arena records
		List<String> records = new ArrayList<>();
		arena.getSortedDescendingRecords().forEach(arenaRecord -> {
			records.add(CommunicationManager.format("&fWave " + arenaRecord.getWave()));
			for (int i = 0; i < arenaRecord.getPlayers().size() / 4 + 1; i++) {
				StringBuilder players = new StringBuilder(CommunicationManager.format("&7"));
				if (i * 4 + 4 < arenaRecord.getPlayers().size()) {
					for (int j = i * 4; j < i * 4 + 4; j++)
						players.append(arenaRecord.getPlayers().get(j)).append(", ");
					records.add(CommunicationManager.format(players.substring(0, players.length() - 1)));
				} else {
					for (int j = i * 4; j < arenaRecord.getPlayers().size(); j++)
						players.append(arenaRecord.getPlayers().get(j)).append(", ");
					records.add(CommunicationManager.format(players.substring(0, players.length() - 2)));
				}
			}
		});
		inv.setItem(32, ItemManager.createItem(Material.GOLDEN_HELMET, CommunicationManager.format("&e&lArena Records"), ItemManager.BUTTON_FLAGS,
				null, records));

		return inv;
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
		lore.set(lore.size() - 1, CommunicationManager.format("&2Gems: &a" + price));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	private static void sort(List<ItemStack> list) {
		list.sort(Comparator.comparingInt(itemStack -> {
			List<String> lore = Objects.requireNonNull(itemStack.getItemMeta()).getLore();
			assert lore != null;
			return Integer.parseInt(lore.get(lore.size() - 1).substring(10));
		}));
	}
}
