package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.Boots;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.Chestplate;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.Helmet;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.Leggings;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.*;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.*;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Inventories {
	// The main admin menu for the plugin
	public static Inventory createMainMenu() {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to set lobby location
		buttons.add(ItemManager.createItem(Material.BELL, CommunicationManager.format("&2&lLobby"),
				CommunicationManager.format("&7Manage minigame lobby")));

		// Option to set info boards
		buttons.add(ItemManager.createItem(Material.OAK_SIGN, CommunicationManager.format("&6&lInfo Boards"),
				CommunicationManager.format("&7Manage info boards")));

		// Option to set leaderboards
		buttons.add(ItemManager.createItem(Material.GOLDEN_HELMET,
				CommunicationManager.format("&e&lLeaderboards"), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7Manage leaderboards")));

		// Option to edit arenas
		buttons.add(ItemManager.createItem(Material.NETHERITE_AXE,
				CommunicationManager.format("&9&lArenas"), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.format("&7Manage leaderboards")));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.MAIN_MENU, InventoryType.MENU),
				CommunicationManager.format("&2&lVillager Defense"),
				1,
				true,
				buttons
		);

	}

	// Menu of all the arenas
	public static Inventory createArenasDashboard() {
		return createArenasDashboard(1);
	}
	public static Inventory createArenasDashboard(int page) {
		List<ItemStack> buttons = new ArrayList<>();

		// Gather all arenas in order
		GameManager.getArenas().keySet().stream().sorted().forEach(id -> buttons.add(ItemManager.createItem(
				Material.EMERALD_BLOCK,
				CommunicationManager.format("&a&lEdit " + GameManager.getArenas().get(id).getName())
		)));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.ARENA_DASHBOARD, InventoryType.MENU, page),
				CommunicationManager.format("&9&lArenas"),
				true,
				true,
				"Info Board",
				buttons
		);
	}

	// Menu for lobby
	public static Inventory createLobbyMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.LOBBY_MENU,
				CommunicationManager.format("&2&lLobby"),
				DataManager.getConfigLocation("lobby") != null,
				"Lobby"
		);
	}

	// Confirmation menu for removing lobby
	public static Inventory createLobbyConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.LOBBY_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Lobby?")
		);
	}

	// Dashboard for info boards
	public static Inventory createInfoBoardDashboard() {
		return createInfoBoardDashboard(1);
	}
	public static Inventory createInfoBoardDashboard(int page) {
		List<ItemStack> buttons = new ArrayList<>();

		// Capture all info boards
		DataManager.getConfigLocationMap("infoBoard").forEach((id, location) ->
				buttons.add(ItemManager.createItem(Material.BIRCH_SIGN,
						CommunicationManager.format("&6&lInfo Board " + id))));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
				Integer.parseInt(Objects.requireNonNull(button.getItemMeta()).getDisplayName().split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.INFO_BOARD_DASHBOARD, InventoryType.MENU, page),
				CommunicationManager.format("&6&lInfo Boards"),
				true,
				true,
				"Info Board",
				buttons
		);
	}

	// Menu for editing a specific info board
	public static Inventory createInfoBoardMenu(int infoBoardID) {
		return InventoryFactory.createLocationMenu(
				InventoryID.INFO_BOARD_MENU,
				infoBoardID,
				CommunicationManager.format("&6&lInfo Board " + infoBoardID),
				DataManager.getConfigLocation("infoBoard." + infoBoardID) != null,
				"Info Board"
		);
	}

	// Confirmation menu for removing a specific info board
	public static Inventory createInfoBoardConfirmMenu(int infoBoardID) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.INFO_BOARD_CONFIRM_MENU,
				infoBoardID,
				CommunicationManager.format("&4&lRemove Info Board?")
		);
	}

	// Dashboard for leaderboards
	public static Inventory createLeaderboardDashboard() {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to modify total kills leaderboard
		buttons.add(ItemManager.createItem(Material.DRAGON_HEAD,
				CommunicationManager.format("&4&lTotal Kills Leaderboard")));

		// Option to modify top kills leaderboard
		buttons.add(ItemManager.createItem(Material.ZOMBIE_HEAD,
				CommunicationManager.format("&c&lTop Kills Leaderboard")));

		// Option to modify total gems leaderboard
		buttons.add(ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&2&lTotal Gems Leaderboard")));

		// Option to modify top balance leaderboard
		buttons.add(ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&a&lTop Balance Leaderboard")));

		// Option to modify top wave leaderboard
		buttons.add(ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&9&lTop Wave Leaderboard"), ItemManager.BUTTON_FLAGS, null));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.LEADERBOARD_DASHBOARD, InventoryType.MENU),
				CommunicationManager.format("&e&lLeaderboards"),
				1,
				true,
				buttons
		);
	}

	// Menu for editing the total kills leaderboard
	public static Inventory createTotalKillsLeaderboardMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.TOTAL_KILLS_LEADERBOARD_MENU,
				CommunicationManager.format("&4&lTotal Kills Leaderboard"),
				DataManager.getConfigLocation("leaderboard.totalKills") != null,
				"Leaderboard"
		);
	}

	// Menu for editing the top kills leaderboard
	public static Inventory createTopKillsLeaderboardMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.TOP_KILLS_LEADERBOARD_MENU,
				CommunicationManager.format("&c&lTop Kills Leaderboard"),
				DataManager.getConfigLocation("leaderboard.topKills") != null,
				"Leaderboard"
		);
	}

	// Menu for editing the total gems leaderboard
	public static Inventory createTotalGemsLeaderboardMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.TOTAL_GEMS_LEADERBOARD_MENU,
				CommunicationManager.format("&2&lTotal Gems Leaderboard"),
				DataManager.getConfigLocation("leaderboard.totalGems") != null,
				"Leaderboard"
		);
	}

	// Menu for editing the top balance leaderboard
	public static Inventory createTopBalanceLeaderboardMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.TOP_BALANCE_LEADERBOARD_MENU,
				CommunicationManager.format("&a&lTop Balance Leaderboard"),
				DataManager.getConfigLocation("leaderboard.topBalance") != null,
				"Leaderboard"
		);
	}

	// Menu for editing the top wave leaderboard
	public static Inventory createTopWaveLeaderboardMenu() {
		return InventoryFactory.createLocationMenu(
				InventoryID.TOP_WAVE_LEADERBOARD_MENU,
				CommunicationManager.format("&9&lTop Wave Leaderboard"),
				DataManager.getConfigLocation("leaderboard.topWave") != null,
				"Leaderboard"
		);
	}

	// Confirmation menu for total kills leaderboard
	public static Inventory createTotalKillsConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.TOTAL_KILLS_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Total Kills Leaderboard?")
		);
	}

	// Confirmation menu for top kills leaderboard
	public static Inventory createTopKillsConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.TOP_KILLS_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Top Kills Leaderboard?")
		);
	}

	// Confirmation menu for total gems leaderboard
	public static Inventory createTotalGemsConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.TOTAL_GEMS_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Total Gems Leaderboard?")
		);
	}

	// Confirmation menu for top balance leaderboard
	public static Inventory createTopBalanceConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.TOP_BALANCE_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Top Balance Leaderboard?")
		);
	}

	// Confirmation menu for top wave leaderboard
	public static Inventory createTopWaveConfirmMenu() {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.TOP_WAVE_CONFIRM_MENU,
				CommunicationManager.format("&4&lRemove Top Wave Leaderboard?")
		);
	}

	// Menu for editing an arena
	public static Inventory createArenaMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to edit name
		buttons.add(ItemManager.createItem(Material.NAME_TAG,
				CommunicationManager.format("&6&lEdit Name")));

		// Option to edit game portal
		buttons.add(ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&5&lArena Portal")));

		// Option to edit leaderboard
		buttons.add(ItemManager.createItem(Material.TOTEM_OF_UNDYING,
				CommunicationManager.format("&a&lArena Leaderboard")));

		// Option to edit player settings
		buttons.add(ItemManager.createItem(Material.PLAYER_HEAD,
				CommunicationManager.format("&d&lPlayer Settings")));

		// Option to edit mob settings
		buttons.add(ItemManager.createItem(Material.ZOMBIE_SPAWN_EGG,
				CommunicationManager.format("&2&lMob Settings")));

		// Option to edit shop settings
		buttons.add(ItemManager.createItem(Material.GOLD_BLOCK,
				CommunicationManager.format("&e&lShop Settings")));

		// Option to edit miscellaneous game settings
		buttons.add(ItemManager.createItem(Material.REDSTONE,
				CommunicationManager.format("&7&lGame Settings")));

		// Option to close the arena
		String closed = arena.isClosed() ? "&c&lCLOSED" : "&a&lOPEN";
		buttons.add(ItemManager.createItem(Material.NETHER_BRICK_FENCE,
				CommunicationManager.format("&9&lClose Arena: " + closed)));

		// Option to remove arena
		buttons.add(Buttons.remove("ARENA"));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.ARENA_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&2&lEdit " + arena.getName()),
				true,
				buttons
		);
	}

	// Confirmation menu for removing an arena
	public static Inventory createArenaConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.ARENA_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove " + arena.getName() + '?')
		);
	}

	// Menu for editing the portal of an arena
	public static Inventory createPortalMenu(Arena arena) {
		return InventoryFactory.createLocationMenu(
				InventoryID.PORTAL_MENU,
				arena,
				CommunicationManager.format("&5&lPortal: " + arena.getName()),
				arena.getPortal()  != null,
				"Portal"
		);
	}

	// Menu for editing the leaderboard of an arena
	public static Inventory createArenaBoardMenu(Arena arena) {
		return InventoryFactory.createLocationMenu(
				InventoryID.ARENA_BOARD_MENU,
				arena,
				CommunicationManager.format("&a&lLeaderboard: " + arena.getName()),
				arena.getArenaBoard() != null,
				"Leaderboard"
		);
	}

	// Confirmation menu for removing the arena portal
	public static Inventory createPortalConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.PORTAL_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Portal?")
		);
	}

	// Confirmation menu for removing the arena leaderboard
	public static Inventory createArenaBoardConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.ARENA_BOARD_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Leaderboard?")
		);
	}

	// Menu for editing the player settings of an arena
	public static Inventory createPlayersMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to edit player spawn
		buttons.add(ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&5&lPlayer Spawn")));

		// Option to toggle player spawn particles
		buttons.add(ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&lSpawn Particles: " +
						getToggleStatus(arena.hasSpawnParticles())),
				CommunicationManager.format("&7Particles showing where the spawn is"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit waiting room
		buttons.add(ItemManager.createItem(Material.CLOCK, CommunicationManager.format("&b&lWaiting Room"),
				CommunicationManager.format("&7An optional room to wait in before"),
				CommunicationManager.format("&7the game starts")));

		// Option to edit max players
		buttons.add(ItemManager.createItem(Material.NETHERITE_HELMET,
				CommunicationManager.format("&4&lMaximum Players"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Maximum players the game will have")));

		// Option to edit min players
		buttons.add(ItemManager.createItem(Material.NETHERITE_BOOTS,
				CommunicationManager.format("&2&lMinimum Players"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Minimum players needed for game to start")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.PLAYERS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&d&lPlayer Settings: " + arena.getName()),
				true,
				buttons
		);
	}

	// Menu for editing the player spawn of an arena
	public static Inventory createPlayerSpawnMenu(Arena arena) {
		return InventoryFactory.createLocationMenu(
				InventoryID.PLAYER_SPAWN_MENU,
				arena,
				CommunicationManager.format("&d&lPlayer Spawn: " + arena.getName()),
				arena.getPlayerSpawn() != null,
				"Spawn"
		);
	}

	// Confirmation menu for removing player spawn
	public static Inventory createSpawnConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.SPAWN_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Spawn?")
		);
	}

	// Menu for editing the waiting room of an arena
	public static Inventory createWaitingRoomMenu(Arena arena) {
		return InventoryFactory.createLocationMenu(
				InventoryID.WAITING_ROOM_MENU,
				arena,
				CommunicationManager.format("&b&lWaiting Room: " + arena.getName()),
				arena.getWaitingRoom() != null,
				"Waiting Room"
		);
	}

	// Confirmation menu for removing waiting room
	public static Inventory createWaitingConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.WAITING_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Waiting Room?")
		);
	}

	// Menu for changing max players in an arena
	public static Inventory createMaxPlayerMenu(Arena arena) {
		return InventoryFactory.createIncrementorMenu(
				InventoryID.MAX_PLAYER_MENU,
				arena,
				CommunicationManager.format("&4&lMaximum Players: " + arena.getMaxPlayers())
		);
	}

	// Menu for changing min players in an arena
	public static Inventory createMinPlayerMenu(Arena arena) {
		return InventoryFactory.createIncrementorMenu(
				InventoryID.MIN_PLAYER_MENU,
				arena,
				CommunicationManager.format("&2&lMinimum Players: " + arena.getMinPlayers())
		);
	}

	// Menu for editing the mob settings of an arena
	public static Inventory createMobsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to edit monster spawns
		buttons.add(ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&2&lMonster Spawns")));

		// Option to toggle monster spawn particles
		buttons.add(ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&a&lMonster Spawn Particles: " +
						getToggleStatus(arena.hasMonsterParticles())),
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit villager spawns
		buttons.add(ItemManager.createItem(Material.END_PORTAL_FRAME,
				CommunicationManager.format("&5&lVillager Spawns")));

		// Option to toggle villager spawn particles
		buttons.add(ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&lVillager Spawn Particles: " +
						getToggleStatus(arena.hasVillagerParticles())),
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")));

		// Option to edit villager type
		buttons.add((ItemManager.createItem(Material.LECTERN,
				CommunicationManager.format("&6&lVillager Type: " +
						arena.getVillagerType().substring(0, 1).toUpperCase() +
						arena.getVillagerType().substring(1)))));

		// Option to edit spawn table
		buttons.add(ItemManager.createItem(Material.DRAGON_HEAD,
				CommunicationManager.format("&3&lSpawn Table: " + arena.getSpawnTableFile())));

		// Option to toggle dynamic mob count
		buttons.add(ItemManager.createItem(Material.SLIME_BALL,
				CommunicationManager.format("&e&lDynamic Mob Count: " +
						getToggleStatus(arena.hasDynamicCount())),
				CommunicationManager.format("&7Mob count adjusting based on"),
				CommunicationManager.format("&7number of players")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.MOBS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&2&lMob Settings: " + arena.getName()),
				true,
				buttons
		);
	}

	// Dashboard for editing the monster spawns of an arena
	public static Inventory createMonsterSpawnDashboard(Arena arena) {
		return createMonsterSpawnDashboard(arena, 1);
	}
	public static Inventory createMonsterSpawnDashboard(Arena arena, int page) {
		List<ItemStack> buttons = new ArrayList<>();

		// Capture all monster spawns
		DataManager.getConfigLocationMap(arena.getPath() + ".monster").forEach((id, location) ->
				buttons.add(ItemManager.createItem(Material.ZOMBIE_HEAD,
						CommunicationManager.format("&2&lMob Spawn " + id))));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
				Integer.parseInt(Objects.requireNonNull(button.getItemMeta()).getDisplayName().split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.MONSTER_SPAWN_DASHBOARD, InventoryType.MENU, page, arena),
				CommunicationManager.format("&2&lMonster Spawns: " + arena.getName()),
				true,
				true,
				"Mob Spawn",
				buttons
		);
	}

	// Menu for editing a specific monster spawn of an arena
	public static Inventory createMonsterSpawnMenu(Arena arena, int monsterSpawnID) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to create or relocate monster spawn
		if (arena.getMonsterSpawn(monsterSpawnID) != null)
			buttons.add(Buttons.relocate("Spawn"));
		else buttons.add(Buttons.create("Spawn"));

		// Option to teleport to monster spawn
		buttons.add(Buttons.teleport("Spawn"));

		// Option to center the monster spawn
		buttons.add(Buttons.center("Spawn"));

		// Option to remove monster spawn
		buttons.add(Buttons.remove("SPAWN"));

		// Toggle to set monster spawn type
		switch (arena.getMonsterSpawnType(monsterSpawnID)) {
			case 1:
				buttons.add(ItemManager.createItem(Material.GUNPOWDER,
						CommunicationManager.format("&5&lType: Ground")));
				break;
			case 2:
				buttons.add(ItemManager.createItem(Material.FEATHER,
					CommunicationManager.format("&5&lType: Flying")));
				break;
			default:
				buttons.add(ItemManager.createItem(Material.BONE,
					CommunicationManager.format("&5&lType: All")));
		}

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.MONSTER_SPAWN_MENU, InventoryType.MENU, arena, monsterSpawnID),
				CommunicationManager.format("&2&lMonster Spawn " + monsterSpawnID + ": " + arena.getName()),
				1,
				true,
				buttons
		);
	}

	// Confirmation menu for removing monster spawns
	public static Inventory createMonsterSpawnConfirmMenu(Arena arena, int monsterSpawnID) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.MONSTER_SPAWN_CONFIRM_MENU,
				null,
				arena,
				monsterSpawnID,
				CommunicationManager.format("&4&lRemove Monster Spawn?")
		);
	}

	// Dashboard for editing the villager spawns of an arena
	public static Inventory createVillagerSpawnDashboard(Arena arena) {
		return createVillagerSpawnDashboard(arena, 1);
	}
	public static Inventory createVillagerSpawnDashboard(Arena arena, int page) {
		List<ItemStack> buttons = new ArrayList<>();

		// Capture all monster spawns
		DataManager.getConfigLocationMap(arena.getPath() + ".villager").forEach((id, location) ->
				buttons.add(ItemManager.createItem(Material.POPPY,
						CommunicationManager.format("&5&lVillager Spawn " + id))));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
				Integer.parseInt(Objects.requireNonNull(button.getItemMeta()).getDisplayName().split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.VILLAGER_SPAWN_DASHBOARD, InventoryType.MENU, page, arena),
				CommunicationManager.format("&5&lVillager Spawns: " + arena.getName()),
				true,
				true,
				"Villager Spawn",
				buttons
		);
	}

	// Menu for editing a specific villager spawn of an arena
	public static Inventory createVillagerSpawnMenu(Arena arena, int villagerSpawnID) {
		return InventoryFactory.createLocationMenu(
				InventoryID.VILLAGER_SPAWN_MENU,
				arena,
				villagerSpawnID,
				CommunicationManager.format("&5&lVillager Spawn " + villagerSpawnID + ": " + arena.getName()),
				arena.getVillagerSpawn(villagerSpawnID) != null,
				"Spawn"
		);
	}

	// Confirmation menu for removing mob spawns
	public static Inventory createVillagerSpawnConfirmMenu(Arena arena, int villagerSpawnID) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.VILLAGER_SPAWN_CONFIRM_MENU,
				null,
				arena,
				villagerSpawnID,
				CommunicationManager.format("&4&lRemove Villager Spawn?")
		);
	}

	// Villager type menu for an arena
	public static Inventory createVillagerTypeMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Villager type options
		buttons.add(ItemManager.createItem(Material.SANDSTONE, CommunicationManager.format("&6&lDesert")));
		buttons.add(ItemManager.createItem(Material.MOSSY_COBBLESTONE, CommunicationManager.format("&2&lJungle")));
		buttons.add(ItemManager.createItem(Material.GRASS_BLOCK, CommunicationManager.format("&a&lPlains")));
		buttons.add(ItemManager.createItem(Material.TERRACOTTA, CommunicationManager.format("&c&lSavanna")));
		buttons.add(ItemManager.createItem(Material.SNOW_BLOCK, CommunicationManager.format("&b&lSnow")));
		buttons.add(ItemManager.createItem(Material.CLAY, CommunicationManager.format("&3&lSwamp")));
		buttons.add(ItemManager.createItem(Material.PODZOL, CommunicationManager.format("&9&lTaiga")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.VILLAGER_TYPE_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&6&lVillager Type: " +
						arena.getVillagerType().substring(0, 1).toUpperCase() + arena.getVillagerType().substring(1)),
				true,
				buttons
		);
	}

	// Spawn table menu for an arena
	public static Inventory createSpawnTableMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();
		String chosen = arena.getSpawnTableFile();

		// Option to set spawn table to default
		buttons.add(ItemManager.createItem(Material.OAK_WOOD,
				CommunicationManager.format("&4&lDefault"), ItemManager.BUTTON_FLAGS,
				chosen.contains("default") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to default.yml")));

		// Option to set spawn table to global option 1
		buttons.add(ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&6&lOption 1"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option1") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option1.yml")));

		// Option to set spawn table to global option 2
		buttons.add(ItemManager.createItem(Material.ORANGE_CONCRETE,
				CommunicationManager.format("&6&lOption 2"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option2") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option2.yml")));

		// Option to set spawn table to global option 3
		buttons.add(ItemManager.createItem(Material.YELLOW_CONCRETE,
				CommunicationManager.format("&6&lOption 3"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option3") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option3.yml")));

		// Option to set spawn table to global option 4
		buttons.add(ItemManager.createItem(Material.BROWN_CONCRETE,
				CommunicationManager.format("&6&lOption 4"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option4") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option4.yml")));

		// Option to set spawn table to global option 5
		buttons.add(ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
				CommunicationManager.format("&6&lOption 5"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option5") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option5.yml")));

		// Option to set spawn table to global option 6
		buttons.add(ItemManager.createItem(Material.WHITE_CONCRETE,
				CommunicationManager.format("&6&lOption 6"), ItemManager.BUTTON_FLAGS,
				chosen.contains("option6") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option6.yml")));

		// Option to set spawn table to custom option
		buttons.add(ItemManager.createItem(Material.BIRCH_WOOD,
				CommunicationManager.format("&e&lCustom"), ItemManager.BUTTON_FLAGS,
				chosen.charAt(0) == 'a' ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to a" + arena.getId() + ".yml")));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.SPAWN_TABLE_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&3&lSpawn Table: " + arena.getSpawnTableFile()),
				1,
				true,
				buttons
		);
	}

	// Menu for editing the shop settings of an arena
	public static Inventory createShopSettingsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to toggle community chest
		buttons.add(ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&lCommunity Chest: " + getToggleStatus(arena.hasCommunity())),
				CommunicationManager.format("&7Turn community chest on and off")));

		// Option to toggle dynamic prices
		buttons.add(ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&b&lDynamic Prices: " + getToggleStatus(arena.hasDynamicPrices())),
				CommunicationManager.format("&7Prices adjusting based on number of"),
				CommunicationManager.format("&7players in the game")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.SHOP_SETTINGS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&e&lShop Settings: " + arena.getName()),
				true,
				buttons
		);
	}

	// Menu for editing the game settings of an arena
	public static Inventory createGameSettingsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to change max waves
		buttons.add(
				ItemManager.createItem(Material.NETHERITE_SWORD,
				CommunicationManager.format("&3&lMax Waves"),
				ItemManager.BUTTON_FLAGS,
				null)
		);

		// Option to wave time limit
		buttons.add(ItemManager.createItem(Material.CLOCK, CommunicationManager.format("&2&lWave Time Limit")));

		// Option to toggle dynamic wave time limit
		buttons.add(
				ItemManager.createItem(Material.SNOWBALL,
				CommunicationManager.format("&a&lDynamic Time Limit: " + getToggleStatus(arena.hasDynamicLimit())),
				CommunicationManager.format("&7Wave time limit adjusting based on"),
				CommunicationManager.format("&7in-game difficulty"))
		);

		// Option to edit allowed kits
		buttons.add(ItemManager.createItem(Material.ENDER_CHEST,
				CommunicationManager.format("&9&lAllowed Kits")));

		// Option to edit forced challenges
		buttons.add(ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&9&lForced Challenges")));

		// Option to edit difficulty label
		buttons.add(ItemManager.createItem(Material.NAME_TAG,
				CommunicationManager.format("&6&lDifficulty Label")));

		// Option to adjust overall difficulty multiplier
		buttons.add(ItemManager.createItem(Material.TURTLE_HELMET,
				CommunicationManager.format("&4&lDifficulty Multiplier"),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Determines difficulty increase rate")));

		// Option to toggle dynamic difficulty
		buttons.add(ItemManager.createItem(Material.MAGMA_CREAM,
				CommunicationManager.format("&6&lDynamic Difficulty: " +
						getToggleStatus(arena.hasDynamicDifficulty())),
				CommunicationManager.format("&7Difficulty adjusting based on"),
				CommunicationManager.format("&7number of players")));

		// Option to toggle late arrival
		buttons.add(ItemManager.createItem(Material.DAYLIGHT_DETECTOR,
				CommunicationManager.format("&e&lLate Arrival: " +
						getToggleStatus(arena.hasLateArrival())),
				CommunicationManager.format("&7Allows players to join after"),
				CommunicationManager.format("&7the game has started")));

		// Option to set arena bounds
		buttons.add(ItemManager.createItem(Material.BEDROCK, CommunicationManager.format("&4&lArena Bounds"),
				CommunicationManager.format("&7Bounds determine where players are"),
				CommunicationManager.format("&7allowed to go and where the game will"),
				CommunicationManager.format("&7function. Avoid building past arena bounds.")));

		// Option to edit wolf cap
		buttons.add(ItemManager.createItem(Material.BONE, CommunicationManager.format("&6&lWolf Cap"),
				CommunicationManager.format("&7Maximum wolves a player can have")));

		// Option to edit golem cap
		buttons.add(ItemManager.createItem(Material.IRON_INGOT,
				CommunicationManager.format("&e&lIron Golem Cap"),
				CommunicationManager.format("&7Maximum iron golems an arena can have")));

		// Option to edit sounds
		buttons.add(
				ItemManager.createItem(Material.MUSIC_DISC_13,
				CommunicationManager.format("&d&lSounds"),
				ItemManager.BUTTON_FLAGS,
				null)
		);

		// Option to copy game settings from another arena or a preset
		buttons.add(ItemManager.createItem(Material.WRITABLE_BOOK,
				CommunicationManager.format("&f&lCopy Game Settings"),
				CommunicationManager.format("&7Copy settings of another arena or"),
				CommunicationManager.format("&7choose from a menu of presets")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.GAME_SETTINGS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&8&lGame Settings: " + arena.getName()),
				true,
				buttons
		);
	}

	// Menu for changing max waves of an arena
	public static Inventory createMaxWaveMenu(Arena arena) {
		return InventoryFactory.createAdvancedIncrementorMenu(
				InventoryID.MAX_WAVE_MENU,
				arena,
				(arena.getMaxWaves() < 0) ?
						CommunicationManager.format("&3&lMaximum Waves: Unlimited") :
						CommunicationManager.format("&3&lMaximum Waves: " + arena.getMaxWaves())
		);
	}

	// Menu for changing wave time limit of an arena
	public static Inventory createWaveTimeLimitMenu(Arena arena) {
		return InventoryFactory.createAdvancedIncrementorMenu(
				InventoryID.WAVE_TIME_LIMIT_MENU,
				arena,
				(arena.getWaveTimeLimit() < 0) ?
						CommunicationManager.format("&2&lWave Time Limit: Unlimited") :
						CommunicationManager.format("&2&lWave Time Limit: " + arena.getWaveTimeLimit())
		);
	}

	// Menu for allowed kits of an arena
	public static Inventory createAllowedKitsMenu(Arena arena, boolean display) {
		// Create inventory
		Inventory inv = display ? Bukkit.createInventory(
				new InventoryMeta(InventoryID.ALLOWED_KITS_DISPLAY_MENU, InventoryType.MENU, arena),
				54,
				CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits)
		) : Bukkit.createInventory(
				new InventoryMeta(InventoryID.ALLOWED_KITS_MENU, InventoryType.MENU, arena),
				54,
				CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits + ": " + arena.getName())
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE,
					CommunicationManager.format("&a&l" + LanguageManager.names.giftKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.giftKitsDescription, Utils.LORE_CHAR_LIMIT)));

		List<String> bannedKitIDs = arena.getBannedKitIDs();
		if (bannedKitIDs.contains(Kit.orc().getID()))
			inv.setItem(9, Kit.orc().getButton(-1, false));
		else inv.setItem(9, Kit.orc().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.farmer().getID()))
			inv.setItem(10, Kit.farmer().getButton(-1, false));
		else inv.setItem(10, Kit.farmer().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.soldier().getID()))
			inv.setItem(11, Kit.soldier().getButton(-1, false));
		else inv.setItem(11, Kit.soldier().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.alchemist().getID()))
			inv.setItem(12, Kit.alchemist().getButton(-1, false));
		else inv.setItem(12, Kit.alchemist().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.tailor().getID()))
			inv.setItem(13, Kit.tailor().getButton(-1, false));
		else inv.setItem(13, Kit.tailor().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.trader().getID()))
			inv.setItem(14, Kit.trader().getButton(-1, false));
		else inv.setItem(14, Kit.trader().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.summoner().getID()))
			inv.setItem(15, Kit.summoner().getButton(-1, false));
		else inv.setItem(15, Kit.summoner().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.reaper().getID()))
			inv.setItem(16, Kit.reaper().getButton(-1, false));
		else inv.setItem(16, Kit.reaper().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.phantom().getID()))
			inv.setItem(17, Kit.phantom().getButton(-1, false));
		else inv.setItem(17, Kit.phantom().getButton(-1, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (bannedKitIDs.contains(Kit.mage().getID()))
			inv.setItem(27, Kit.mage().getButton(-1, false));
		else inv.setItem(27, Kit.mage().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.ninja().getID()))
			inv.setItem(28, Kit.ninja().getButton(-1, false));
		else inv.setItem(28, Kit.ninja().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.templar().getID()))
			inv.setItem(29, Kit.templar().getButton(-1, false));
		else inv.setItem(29, Kit.templar().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.warrior().getID()))
			inv.setItem(30, Kit.warrior().getButton(-1, false));
		else inv.setItem(30, Kit.warrior().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.knight().getID()))
			inv.setItem(31, Kit.knight().getButton(-1, false));
		else inv.setItem(31, Kit.knight().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.priest().getID()))
			inv.setItem(32, Kit.priest().getButton(-1, false));
		else inv.setItem(32, Kit.priest().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.siren().getID()))
			inv.setItem(33, Kit.siren().getButton(-1, false));
		else inv.setItem(33, Kit.siren().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.monk().getID()))
			inv.setItem(34, Kit.monk().getButton(-1, false));
		else inv.setItem(34, Kit.monk().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.messenger().getID()))
			inv.setItem(35, Kit.messenger().getButton(-1, false));
		else inv.setItem(35, Kit.messenger().getButton(-1, true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (bannedKitIDs.contains(Kit.blacksmith().getID()))
			inv.setItem(45, Kit.blacksmith().getButton(-1, false));
		else inv.setItem(45, Kit.blacksmith().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.witch().getID()))
			inv.setItem(46, Kit.witch().getButton(-1, false));
		else inv.setItem(46, Kit.witch().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.merchant().getID()))
			inv.setItem(47, Kit.merchant().getButton(-1, false));
		else inv.setItem(47, Kit.merchant().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.vampire().getID()))
			inv.setItem(48, Kit.vampire().getButton(-1, false));
		else inv.setItem(48, Kit.vampire().getButton(-1, true));
		if (bannedKitIDs.contains(Kit.giant().getID()))
			inv.setItem(49, Kit.giant().getButton(-1, false));
		else inv.setItem(49, Kit.giant().getButton(-1, true));

		// Option to exit
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Menu for forced challenges of an arena
	public static Inventory createForcedChallengesMenu(Arena arena, boolean display) {
		List<ItemStack> buttons = new ArrayList<>();
		List<String> forced = arena.getForcedChallengeIDs();

		// Set buttons
		buttons.add(Challenge.amputee().getButton(forced.contains(Challenge.amputee().getID())));
		buttons.add(Challenge.clumsy().getButton(forced.contains(Challenge.clumsy().getID())));
		buttons.add(Challenge.featherweight().getButton(forced.contains(Challenge.featherweight().getID())));
		buttons.add(Challenge.pacifist().getButton(forced.contains(Challenge.pacifist().getID())));
		buttons.add(Challenge.dwarf().getButton(forced.contains(Challenge.dwarf().getID())));
		buttons.add(Challenge.uhc().getButton(forced.contains(Challenge.uhc().getID())));
		buttons.add(Challenge.explosive().getButton(forced.contains(Challenge.explosive().getID())));
		buttons.add(Challenge.naked().getButton(forced.contains(Challenge.naked().getID())));
		buttons.add(Challenge.blind().getButton(forced.contains(Challenge.blind().getID())));

		return display ? InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.FORCED_CHALLENGES_DISPLAY_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&9&l" + LanguageManager.messages.forcedChallenges),
				true,
				buttons
		) : InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.FORCED_CHALLENGES_MENU, InventoryType.MENU, arena),
				CommunicationManager.format(
						"&9&l" + LanguageManager.messages.forcedChallenges + ": " + arena.getName()),
				true,
				buttons
		);
	}

	// Menu for changing the difficulty label of an arena
	public static Inventory createDifficultyLabelMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		String label = arena.getDifficultyLabel();
		switch (label) {
			case "Easy":
				label = "&a&l" + LanguageManager.names.easy;
				break;
			case "Medium":
				label = "&e&l" + LanguageManager.names.medium;
				break;
			case "Hard":
				label = "&c&l" + LanguageManager.names.hard;
				break;
			case "Insane":
				label = "&d&l" + LanguageManager.names.insane;
				break;
			default:
				label = "";
		}

		// "Easy" option
		buttons.add(ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l" + LanguageManager.names.easy)));

		// "Medium" option
		buttons.add(ItemManager.createItem(Material.YELLOW_CONCRETE,
				CommunicationManager.format("&e&l" + LanguageManager.names.medium)));

		// "Hard" option
		buttons.add(ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l" + LanguageManager.names.hard)));

		// "Insane" option
		buttons.add(ItemManager.createItem(Material.MAGENTA_CONCRETE,
				CommunicationManager.format("&d&l" + LanguageManager.names.insane)));

		// "None" option
		buttons.add(ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
				CommunicationManager.format("&7&l" + LanguageManager.names.none)));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.DIFFICULTY_LABEL_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&6&lDifficulty Label: " + label),
				true,
				buttons
		);
	}

	// Menu for changing the difficulty multiplier of an arena
	public static Inventory createDifficultyMultiplierMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// "1" option
		buttons.add(ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE,
				CommunicationManager.format("&b&l1")));

		// "2" option
		buttons.add(ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&l2")));

		// "3" option
		buttons.add(ItemManager.createItem(Material.YELLOW_CONCRETE, CommunicationManager.format("&6&l3")));

		// "4" option
		buttons.add(ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&4&l4")));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.DIFFICULTY_MULTIPLIER_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&4&lDifficulty Multiplier: " + arena.getDifficultyMultiplier()),
				1,
				true,
				buttons
		);
	}

	// Menu for selecting arena bound corners
	public static Inventory createBoundsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to interact with corner 1
		buttons.add(ItemManager.createItem(Material.TORCH, CommunicationManager.format("&b&lCorner 1: " +
				(arena.getCorner1() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to interact with corner 2
		buttons.add(ItemManager.createItem(Material.SOUL_TORCH,
				CommunicationManager.format("&9&lCorner 2: " +
				(arena.getCorner2() == null ? "&c&lMissing" : "&a&lSet"))));

		// Option to toggle arena border particles
		buttons.add(ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&4&lBorder Particles: " +
				getToggleStatus(arena.hasBorderParticles()))));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.ARENA_BOUNDS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&4&lArena Bounds: " + arena.getName()),
				1,
				true,
				buttons
		);
	}

	// Menu for editing corner 1 of an arena
	public static Inventory createCorner1Menu(Arena arena) {
		return InventoryFactory.createSimpleLocationMenu(
				InventoryID.CORNER_1_MENU,
				arena,
				CommunicationManager.format("&b&lCorner 1: " + arena.getName()),
				arena.getCorner1() != null,
				"Corner 1"
		);
	}

	// Confirmation menu for removing corner 1
	public static Inventory createCorner1ConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.CORNER_1_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Corner 1?")
		);
	}

	// Menu for editing corner 2 of an arena
	public static Inventory createCorner2Menu(Arena arena) {
		return InventoryFactory.createSimpleLocationMenu(
				InventoryID.CORNER_2_MENU,
				arena,
				CommunicationManager.format("&9&lCorner 2: " + arena.getName()),
				arena.getCorner2() != null,
				"Corner 2"
		);
	}

	// Confirmation menu for removing corner 2
	public static Inventory createCorner2ConfirmMenu(Arena arena) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.CORNER_2_CONFIRM_MENU,
				arena,
				CommunicationManager.format("&4&lRemove Corner 2?")
		);
	}

	// Menu for changing wolf cap of an arena
	public static Inventory createWolfCapMenu(Arena arena) {
		return InventoryFactory.createIncrementorMenu(
				InventoryID.WOLF_CAP_MENU,
				arena,
				CommunicationManager.format("&6&lWolf Cap: " + arena.getWolfCap())
		);
	}

	// Menu for changing iron golem cap of an arena
	public static Inventory createGolemCapMenu(Arena arena) {
		return InventoryFactory.createIncrementorMenu(
				InventoryID.GOLEM_CAP_MENU,
				arena,
				CommunicationManager.format("&e&lIron Golem Cap: " + arena.getGolemCap())
		);
	}

	// Menu for editing the sounds of an arena
	public static Inventory createSoundsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to edit win sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_PIGSTEP,
				CommunicationManager.format("&a&lWin Sound: " + getToggleStatus(arena.hasWinSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when game ends and players win")));

		// Option to edit lose sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_11,
				CommunicationManager.format("&e&lLose Sound: " + getToggleStatus(arena.hasLoseSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when game ends and players lose")));

		// Option to edit wave start sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_CAT,
				CommunicationManager.format("&2&lWave Start Sound: " +
						getToggleStatus(arena.hasWaveStartSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a wave starts")));

		// Option to edit wave finish sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_BLOCKS,
				CommunicationManager.format("&9&lWave Finish Sound: " +
						getToggleStatus(arena.hasWaveFinishSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a wave ends")));

		// Option to edit waiting music
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_MELLOHI,
				CommunicationManager.format("&6&lWaiting Sound: &b&l" + arena.getWaitingSoundName()),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played while players wait"),
				CommunicationManager.format("&7for the game to start")));

		// Option to edit player death sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_CHIRP,
				CommunicationManager.format("&4&lPlayer Death Sound: " +
						getToggleStatus(arena.hasPlayerDeathSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a player dies")));

		// Option to edit ability sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_MALL,
				CommunicationManager.format("&d&lAbility Sound: " +
						getToggleStatus(arena.hasAbilitySound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when a player uses their ability")));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.SOUNDS_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&d&lSounds: " + arena.getName()),
				true,
				buttons
		);
	}

	// Menu for editing the waiting sound of an arena
	public static Inventory createWaitSoundMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Sound options
		buttons.add(arena.getWaitingSoundButton("blocks"));
		buttons.add(arena.getWaitingSoundButton("cat"));
		buttons.add(arena.getWaitingSoundButton("chirp"));
		buttons.add(arena.getWaitingSoundButton("far"));
		buttons.add(arena.getWaitingSoundButton("mall"));
		buttons.add(arena.getWaitingSoundButton("mellohi"));
		if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
			buttons.add(arena.getWaitingSoundButton("otherside"));

		buttons.add(arena.getWaitingSoundButton("pigstep"));
		buttons.add(arena.getWaitingSoundButton("stal"));
		buttons.add(arena.getWaitingSoundButton("strad"));
		buttons.add(arena.getWaitingSoundButton("wait"));
		buttons.add(arena.getWaitingSoundButton("ward"));
		buttons.add(arena.getWaitingSoundButton("none"));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.WAITING_SOUND_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&6&lWaiting Sound: " + arena.getWaitingSoundName()),
				true,
				buttons
		);
	}

	// Menu to copy game settings
	public static Inventory createCopySettingsMenu(Arena arena) {
		return createCopySettingsMenu(arena, 1);
	}
	public static Inventory createCopySettingsMenu(Arena arena, int page) {
		List<ItemStack> buttons = new ArrayList<>();
		List<ItemStack> frozenButtons = new ArrayList<>();

		// Options to choose any of the other arenas
		Objects.requireNonNull(Main.getArenaData().getConfigurationSection("arena")).getKeys(false)
				.forEach(id -> {
					if (Integer.parseInt(id) != arena.getId())
						try {
							buttons.add(
									ItemManager.createItem(Material.GRAY_GLAZED_TERRACOTTA,
											CommunicationManager.format("&a&lCopy " +
													GameManager.getArena(Integer.parseInt(id)).getName()))
							);
						} catch (ArenaNotFoundException ignored) {
						}
				});

		// Easy preset
		frozenButtons.add(ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&lEasy Preset")));

		// Medium preset
		frozenButtons.add(ItemManager.createItem(Material.YELLOW_CONCRETE,
				CommunicationManager.format("&e&lMedium Preset")));

		// Hard preset
		frozenButtons.add(ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&lHard Preset")));

		// Insane preset
		frozenButtons.add(ItemManager.createItem(Material.MAGENTA_CONCRETE,
				CommunicationManager.format("&d&lInsane Preset")));

		return InventoryFactory.createDynamicSizeBottomNavFreezeRowInventory(
				new InventoryMeta(InventoryID.COPY_SETTINGS_MENU, InventoryType.MENU, page, arena),
				CommunicationManager.format("&8&lCopy Game Settings"),
				true,
				false,
				"",
				buttons,
				1,
				frozenButtons
		);
	}

	// Generate the shop menu
	public static Inventory createShopMenu(int level, Arena arena) {
		String disabled = " &4&l[" + LanguageManager.messages.disabled + "]";

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.SHOP_MENU, InventoryType.MENU),
				9,
				CommunicationManager.format("&2&l" + LanguageManager.messages.level +
						" &9&l" + level + " &2&l" + LanguageManager.names.itemShop));

		inv.setItem(1, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&4&l" + LanguageManager.messages.level +
						" &9&l" + level + " &4&l" + LanguageManager.names.weaponShop), ItemManager.BUTTON_FLAGS,
						ItemManager.glow()));

		inv.setItem(3, ItemManager.createItem(Material.GOLDEN_CHESTPLATE,
				CommunicationManager.format("&5&l" + LanguageManager.messages.level +
						" &9&l" + level + " &5&l" + LanguageManager.names.armorShop), ItemManager.BUTTON_FLAGS,
						ItemManager.glow()));

		inv.setItem(5, ItemManager.createItem(Material.GOLDEN_APPLE,
				CommunicationManager.format("&3&l" + LanguageManager.messages.level +
						" &9&l" + level + " &3&l" + LanguageManager.names.consumableShop), ItemManager.BUTTON_FLAGS,
						ItemManager.glow()));

		inv.setItem(8, ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&l" + LanguageManager.names.communityChest +
						(arena.hasCommunity() ? "" : disabled))));

		return inv;
	}

	// Generate the weapon shop
	public static Inventory createWeaponShopMenu(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.WEAPON_SHOP_MENU, InventoryType.MENU),
				36,
				CommunicationManager.format("&4&l" + LanguageManager.messages.level +
						" &9&l" + level + " &4&l" + LanguageManager.names.weaponShop)
		);

		// Fill in swords
		List<ItemStack> swords = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack sword = Sword.create(arena.getCurrentDifficulty());
			if (swords.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(sword.getItemMeta()).getDisplayName())))
				swords.add(sword);
			else swords.add(Buttons.duplicatePlaceholder());
		}
		sort(swords);
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(swords.get(i), modifier));

		// Fill in axes
		List<ItemStack> axes = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack axe = Axe.create(arena.getCurrentDifficulty());
			if (level < 2)
				axes.add(Buttons.levelPlaceholder(2));
			else if (axes.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(axe.getItemMeta()).getDisplayName())))
				axes.add(axe);
			else axes.add(Buttons.duplicatePlaceholder());
		}
		sort(axes);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 9, modifyPrice(axes.get(i), modifier));

		// Fill in scythes
		List<ItemStack> scythes = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack scythe = Scythe.create(arena.getCurrentDifficulty());
			if (level < 4)
				scythes.add(Buttons.levelPlaceholder(4));
			else if (scythes.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(scythe.getItemMeta()).getDisplayName())))
				scythes.add(scythe);
			else scythes.add(Buttons.duplicatePlaceholder());
		}
		sort(scythes);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 18, modifyPrice(scythes.get(i), modifier));

		// Fill in bows
		List<ItemStack> bows = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack bow = Bow.create(arena.getCurrentDifficulty());
			if (level < 3)
				bows.add(Buttons.levelPlaceholder(3));
			else if (bows.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(bow.getItemMeta()).getDisplayName())))
				bows.add(bow);
			else bows.add(Buttons.duplicatePlaceholder());
		}
		sort(bows);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(bows.get(i), modifier));

		// Fill in crossbows
		List<ItemStack> crossbows = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack crossbow = Crossbow.create(arena.getCurrentDifficulty());
			if (level < 5)
				crossbows.add(Buttons.levelPlaceholder(5));
			else if (crossbows.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(crossbow.getItemMeta()).getDisplayName())))
				crossbows.add(crossbow);
			else crossbows.add(Buttons.duplicatePlaceholder());
		}
		sort(crossbows);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 14, modifyPrice(crossbows.get(i), modifier));

		// Fill in tridents

		// Fill in ammo
		List<ItemStack> ammos = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack ammo = Ammo.create(arena.getCurrentDifficulty());
			if (level < 3)
				ammos.add(Buttons.levelPlaceholder(3));
			else if (ammos.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(ammo.getItemMeta()).getDisplayName())))
				ammos.add(ammo);
			else ammos.add(Buttons.duplicatePlaceholder());
		}
		sort(ammos);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 23, modifyPrice(ammos.get(i), modifier));

		// Return option
		inv.setItem(31, Buttons.exit());

		return inv;
	}

	// Generate the armor shop
	public static Inventory createArmorShopMenu(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.ARMOR_SHOP_MENU, InventoryType.MENU),
				27,
				CommunicationManager.format("&5&l" + LanguageManager.messages.level +
						" &9&l" + level + " &5&l" + LanguageManager.names.armorShop)
		);

		// Fill in helmets
		List<ItemStack> helmets = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack helmet = Helmet.create(arena.getCurrentDifficulty());
			if (helmets.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(helmet.getItemMeta()).getDisplayName())))
				helmets.add(helmet);
			else helmets.add(Buttons.duplicatePlaceholder());
		}
		sort(helmets);
		for (int i = 0; i < 4; i++)
			inv.setItem(i, modifyPrice(helmets.get(i), modifier));

		// Fill in chestplates
		List<ItemStack> chestplates = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack chestplate = Chestplate.create(arena.getCurrentDifficulty());
			if (chestplates.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(chestplate.getItemMeta()).getDisplayName())))
				chestplates.add(chestplate);
			else chestplates.add(Buttons.duplicatePlaceholder());
		}
		sort(chestplates);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 5, modifyPrice(chestplates.get(i), modifier));

		// Fill in leggings
		List<ItemStack> leggings = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack legging = Leggings.create(arena.getCurrentDifficulty());
			if (leggings.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(legging.getItemMeta()).getDisplayName())))
				leggings.add(legging);
			else leggings.add(Buttons.duplicatePlaceholder());
		}
		sort(leggings);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 9, modifyPrice(leggings.get(i), modifier));

		// Fill in boots
		List<ItemStack> boots = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			ItemStack boot = Boots.create(arena.getCurrentDifficulty());
			if (boots.stream().noneMatch(item -> Objects.requireNonNull(item.getItemMeta()).getDisplayName()
					.equals(Objects.requireNonNull(boot.getItemMeta()).getDisplayName())))
				boots.add(boot);
			else boots.add(Buttons.duplicatePlaceholder());
		}
		sort(boots);
		for (int i = 0; i < 4; i++)
			inv.setItem(i + 14, modifyPrice(boots.get(i), modifier));

		// Return option
		inv.setItem(22, Buttons.exit());

		return inv;
	}

	// Generate the consumable shop
	public static Inventory createConsumableShopMenu(int level, Arena arena) {
		// Set price modifier
		double modifier = Math.pow(arena.getActiveCount() - 5, 2) / 200 + 1;
		if (!arena.hasDynamicPrices())
			modifier = 1;

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.CONSUMABLE_SHOP_MENU, InventoryType.MENU),
				27,
				CommunicationManager.format("&3&l" + LanguageManager.messages.level +
						" &9&l" + level + " &3&l" + LanguageManager.names.consumableShop)
		);

		// Fill in food
		List<ItemStack> foods = new ArrayList<>();
		if (level < 2) {
			foods.add(Buttons.levelPlaceholder(2));
			foods.add(Buttons.levelPlaceholder(2));
		} else {
			foods.add(Beetroot.create());
			foods.add(Carrot.create());
		}
		if (level < 3)
			foods.add(Buttons.levelPlaceholder(3));
		else
			foods.add(Bread.create());
		if (level < 4) {
			foods.add(Buttons.levelPlaceholder(4));
			foods.add(Buttons.levelPlaceholder(4));
		} else {
			foods.add(Mutton.create());
			foods.add(Steak.create());
		}
		if (level < 5) {
			foods.add(Buttons.levelPlaceholder(5));
			foods.add(Buttons.levelPlaceholder(5));
		} else {
			foods.add(GoldenCarrot.create());
			foods.add(GoldenApple.create());
		}
		if (level < 6)
			foods.add(Buttons.levelPlaceholder(6));
		else
			foods.add(EnchantedApple.create());
		if (level < 7)
			foods.add(Buttons.levelPlaceholder(7));
		else
			foods.add(Totem.create());

		for (int i = 0; i < 9; i++)
			inv.setItem(i, modifyPrice(foods.get(i), modifier));

		// Fill in other

		// Return option
		inv.setItem(22, Buttons.exit());

		return inv;
	}

	// Display player stats
	public static Inventory createPlayerStatsMenu(UUID ownerID, UUID requesterID) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.PLAYER_STATS_MENU, InventoryType.MENU, ownerID),
				18,
				CommunicationManager.format("&2&l" + String.format(LanguageManager.messages.playerStatistics,
						Bukkit.getOfflinePlayer(ownerID).getName()))
		);

		// Total kills
		inv.setItem(0, ItemManager.createItem(Material.DRAGON_HEAD,
				CommunicationManager.format("&4&l" + LanguageManager.playerStats.totalKills.name +
						": &4" + PlayerManager.getTotalKills(ownerID)),
				CommunicationManager.format("&7" + LanguageManager.playerStats.totalKills.description)));

		// Top kills
		inv.setItem(10, ItemManager.createItem(Material.ZOMBIE_HEAD,
				CommunicationManager.format("&c&l" + LanguageManager.playerStats.topKills.name +
						": &c" + PlayerManager.getTopKills(ownerID)),
				CommunicationManager.format("&7" + LanguageManager.playerStats.topKills.description)));

		// Total gems
		inv.setItem(2, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&2&l" + LanguageManager.playerStats.totalGems.name +
						": &2" + PlayerManager.getTotalGems(ownerID)),
				CommunicationManager.format("&7" + LanguageManager.playerStats.totalGems.description)));

		// Top balance
		inv.setItem(12, ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&a&l" + LanguageManager.playerStats.topBalance.name +
						": &a" + PlayerManager.getTopBalance(ownerID)),
				CommunicationManager.format("&7" + LanguageManager.playerStats.topBalance.description)));

		// Top wave
		inv.setItem(4, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&3&l" + LanguageManager.playerStats.topWave.name +
						": &3" + PlayerManager.getTopWave(ownerID)),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&7" +
						LanguageManager.playerStats.topWave.description)));

		// Achievements
		inv.setItem(14, ItemManager.createItem(Material.GOLDEN_HELMET,
				CommunicationManager.format("&6&l" + LanguageManager.messages.achievements),
				ItemManager.BUTTON_FLAGS, null));

		// Kits
		inv.setItem(6, ItemManager.createItem(Material.ENDER_CHEST, CommunicationManager.format("&9&l" +
				LanguageManager.messages.kits)));

		// Reset stats
		if (ownerID.equals(requesterID))
			inv.setItem(16, ItemManager.createItem(
					Material.LAVA_BUCKET,
					CommunicationManager.format("&d&l" + LanguageManager.messages.reset),
					CommunicationManager.format("&5&l" + LanguageManager.messages.resetWarning)
			));

		// Crystal balance
		inv.setItem(8, ItemManager.createItem(Material.DIAMOND,
				CommunicationManager.format("&b&l" + String.format(LanguageManager.messages.crystalBalance,
						LanguageManager.names.crystal) + ": &b" + PlayerManager.getCrystalBalance(ownerID))));

		return inv;
	}

	// Display kits for a player
	public static Inventory createPlayerKitsMenu(UUID ownerID, UUID requesterID) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.PLAYER_KITS_MENU, InventoryType.MENU, ownerID),
				54,
				CommunicationManager.format("&9&l" + String.format(LanguageManager.messages.playerKits,
						Bukkit.getOfflinePlayer(ownerID).getName()))
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE,
					CommunicationManager.format("&a&l" + LanguageManager.names.giftKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.giftKitsDescription, Utils.LORE_CHAR_LIMIT)));

		inv.setItem(9, Kit.orc().getButton(1, true));
		inv.setItem(10, Kit.farmer().getButton(1, true));
		inv.setItem(11, Kit.soldier().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.soldier().getID()) ?
				1 : 0, true));
		inv.setItem(12, Kit.alchemist().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.alchemist().getID()) ?
				1 : 0, true));
		inv.setItem(13, Kit.tailor().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.tailor().getID()) ? 1 : 0,
				true));
		inv.setItem(14, Kit.trader().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.trader().getID()) ? 1 : 0,
				true));
		inv.setItem(15, Kit.summoner().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.summoner().getID()),
				true));
		inv.setItem(16, Kit.reaper().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.reaper().getID()),
				true));
		inv.setItem(17, Kit.phantom().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.phantom().getID()) ?
				1 : 0, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

		inv.setItem(27, Kit.mage().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.mage().getID()),
				true));
		inv.setItem(28, Kit.ninja().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.ninja().getID()),
				true));
		inv.setItem(29, Kit.templar().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.templar().getID()),
				true));
		inv.setItem(30, Kit.warrior().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.warrior().getID()),
				true));
		inv.setItem(31, Kit.knight().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.knight().getID()),
				true));
		inv.setItem(32, Kit.priest().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.priest().getID()),
				true));
		inv.setItem(33, Kit.siren().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.siren().getID()),
				true));
		inv.setItem(34, Kit.monk().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.monk().getID()),
				true));
		inv.setItem(35, Kit.messenger().getButton(PlayerManager.getMultiTierKitLevel(ownerID,
				Kit.messenger().getID()), true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

		inv.setItem(45, Kit.blacksmith().getButton(PlayerManager.hasSingleTierKit(ownerID,
				Kit.blacksmith().getID()) ? 1 : 0, true));
		inv.setItem(46, Kit.witch().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.witch().getID()) ? 1 : 0,
				true));
		inv.setItem(47, Kit.merchant().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.merchant().getID()) ?
				1 : 0, true));
		inv.setItem(48, Kit.vampire().getButton(PlayerManager.hasSingleTierKit(ownerID, Kit.vampire().getID()) ?
				1 : 0, true));
		inv.setItem(49, Kit.giant().getButton(PlayerManager.getMultiTierKitLevel(ownerID, Kit.giant().getID()),
				true));

		// Crystal balance
		if (ownerID.equals(requesterID))
			inv.setItem(52, ItemManager.createItem(Material.DIAMOND,
					CommunicationManager.format("&b&l" + String.format(LanguageManager.messages.crystalBalance,
							LanguageManager.names.crystal) + ": &b" + PlayerManager.getCrystalBalance(ownerID))));

		// Option to exit
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Display kits for a player to select
	public static Inventory createSelectKitsMenu(Player player, Arena arena) {
		UUID id = player.getUniqueId();

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.SELECT_KITS_MENU, InventoryType.MENU, id, arena),
				54,
				CommunicationManager.format("&9&l" + arena.getName() + " " + LanguageManager.messages.kits)
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE,
					CommunicationManager.format("&a&l" + LanguageManager.names.giftKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.giftKitsDescription, Utils.LORE_CHAR_LIMIT)));

		List<String> bannedKitIDs = arena.getBannedKitIDs();
		if (!bannedKitIDs.contains(Kit.orc().getID()))
			inv.setItem(9, Kit.orc().getButton(1, false));
		if (!bannedKitIDs.contains(Kit.farmer().getID()))
			inv.setItem(10, Kit.farmer().getButton(1, false));
		if (!bannedKitIDs.contains(Kit.soldier().getID()))
			inv.setItem(11, Kit.soldier().getButton(PlayerManager.hasSingleTierKit(id, Kit.soldier().getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit.alchemist().getID()))
			inv.setItem(12, Kit.alchemist().getButton(PlayerManager.hasSingleTierKit(id, Kit.alchemist().getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit.tailor().getID()))
			inv.setItem(13, Kit.tailor().getButton(PlayerManager.hasSingleTierKit(id, Kit.tailor().getID()) ? 1 : 0,
					false));
		if (!bannedKitIDs.contains(Kit.trader().getID()))
			inv.setItem(14, Kit.trader().getButton(PlayerManager.hasSingleTierKit(id, Kit.trader().getID()) ? 1 : 0,
					false));
		if (!bannedKitIDs.contains(Kit.summoner().getID()))
			inv.setItem(15, Kit.summoner().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.summoner().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.reaper().getID()))
			inv.setItem(16, Kit.reaper().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.reaper().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.phantom().getID()))
			inv.setItem(17, Kit.phantom().getButton(PlayerManager.hasSingleTierKit(id, Kit.phantom().getID()) ?
					1 : 0, false));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (!bannedKitIDs.contains(Kit.mage().getID()))
			inv.setItem(27, Kit.mage().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.mage().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.ninja().getID()))
			inv.setItem(28, Kit.ninja().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.ninja().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.templar().getID()))
			inv.setItem(29, Kit.templar().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.templar().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.warrior().getID()))
			inv.setItem(30, Kit.warrior().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.warrior().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.knight().getID()))
			inv.setItem(31, Kit.knight().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.knight().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.priest().getID()))
			inv.setItem(32, Kit.priest().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.priest().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.siren().getID()))
			inv.setItem(33, Kit.siren().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.siren().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.monk().getID()))
			inv.setItem(34, Kit.monk().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.monk().getID()),
					false));
		if (!bannedKitIDs.contains(Kit.messenger().getID()))
			inv.setItem(35, Kit.messenger().getButton(PlayerManager.getMultiTierKitLevel(id,
					Kit.messenger().getID()), false));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (!bannedKitIDs.contains(Kit.blacksmith().getID()))
			inv.setItem(45, Kit.blacksmith().getButton(PlayerManager.hasSingleTierKit(id,
					Kit.blacksmith().getID()) ? 1 : 0, false));
		if (!bannedKitIDs.contains(Kit.witch().getID()))
			inv.setItem(46, Kit.witch().getButton(PlayerManager.hasSingleTierKit(id, Kit.witch().getID()) ? 1 : 0,
					false));
		if (!bannedKitIDs.contains(Kit.merchant().getID()))
			inv.setItem(47, Kit.merchant().getButton(PlayerManager.hasSingleTierKit(id, Kit.merchant().getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit.vampire().getID()))
			inv.setItem(48, Kit.vampire().getButton(PlayerManager.hasSingleTierKit(id, Kit.vampire().getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit.giant().getID()))
			inv.setItem(49, Kit.giant().getButton(PlayerManager.getMultiTierKitLevel(id, Kit.giant().getID()),
					false));

		// Option for no kit
		inv.setItem(52, Kit.none().getButton(0, true));

		// Option to exit
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Display achievements for a player
	public static Inventory createPlayerAchievementsMenu(UUID id) {
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement.topBalance1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance1().getID())));
		buttons.add(Achievement.topBalance2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance2().getID())));
		buttons.add(Achievement.topBalance3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance3().getID())));
		buttons.add(Achievement.topBalance4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance4().getID())));
		buttons.add(Achievement.topBalance5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance5().getID())));
		buttons.add(Achievement.topBalance6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance6().getID())));
		buttons.add(Achievement.topBalance7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance7().getID())));
		buttons.add(Achievement.topBalance8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance8().getID())));
		buttons.add(Achievement.topBalance9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance9().getID())));

		buttons.add(Achievement.topKills1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills1().getID())));
		buttons.add(Achievement.topKills2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills2().getID())));
		buttons.add(Achievement.topKills3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills3().getID())));
		buttons.add(Achievement.topKills4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills4().getID())));
		buttons.add(Achievement.topKills5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills5().getID())));
		buttons.add(Achievement.topKills6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills6().getID())));
		buttons.add(Achievement.topKills7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills7().getID())));
		buttons.add(Achievement.topKills8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills8().getID())));
		buttons.add(Achievement.topKills9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills9().getID())));

		buttons.add(Achievement.topWave1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave1().getID())));
		buttons.add(Achievement.topWave2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave2().getID())));
		buttons.add(Achievement.topWave3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave3().getID())));
		buttons.add(Achievement.topWave4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave4().getID())));
		buttons.add(Achievement.topWave5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave5().getID())));
		buttons.add(Achievement.topWave6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave6().getID())));
		buttons.add(Achievement.topWave7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave7().getID())));
		buttons.add(Achievement.topWave8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave8().getID())));
		buttons.add(Achievement.topWave9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave9().getID())));

		buttons.add(Achievement.totalGems1().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems1().getID())));
		buttons.add(Achievement.totalGems2().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems2().getID())));
		buttons.add(Achievement.totalGems3().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems3().getID())));
		buttons.add(Achievement.totalGems4().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems4().getID())));
		buttons.add(Achievement.totalGems5().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems5().getID())));
		buttons.add(Achievement.totalGems6().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems6().getID())));
		buttons.add(Achievement.totalGems7().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems7().getID())));
		buttons.add(Achievement.totalGems8().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems8().getID())));
		buttons.add(Achievement.totalGems9().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems9().getID())));

		buttons.add(Achievement.totalKills1().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills1().getID())));
		buttons.add(Achievement.totalKills2().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills2().getID())));
		buttons.add(Achievement.totalKills3().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills3().getID())));
		buttons.add(Achievement.totalKills4().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills4().getID())));
		buttons.add(Achievement.totalKills5().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills5().getID())));
		buttons.add(Achievement.totalKills6().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills6().getID())));
		buttons.add(Achievement.totalKills7().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills7().getID())));
		buttons.add(Achievement.totalKills8().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills8().getID())));
		buttons.add(Achievement.totalKills9().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills9().getID())));

		buttons.add(Achievement.amputeeAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeAlone().getID())));
		buttons.add(Achievement.blindAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindAlone().getID())));
		buttons.add(Achievement.clumsyAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyAlone().getID())));
		buttons.add(Achievement.dwarfAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfAlone().getID())));
		buttons.add(Achievement.explosiveAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveAlone().getID())));
		buttons.add(Achievement.featherweightAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightAlone().getID())));
		buttons.add(Achievement.nakedAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedAlone().getID())));
		buttons.add(Achievement.pacifistAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistAlone().getID())));
		buttons.add(Achievement.uhcAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcAlone().getID())));

		buttons.add(Achievement.amputeeBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.amputeeBalance().getID())));
		buttons.add(Achievement.blindBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindBalance().getID())));
		buttons.add(Achievement.clumsyBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.clumsyBalance().getID())));
		buttons.add(Achievement.dwarfBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfBalance().getID())));
		buttons.add(Achievement.explosiveBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveBalance().getID())));
		buttons.add(Achievement.featherweightBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightBalance().getID())));
		buttons.add(Achievement.nakedBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedBalance().getID())));
		buttons.add(Achievement.pacifistBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistBalance().getID())));
		buttons.add(Achievement.uhcBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcBalance().getID())));

		buttons.add(Achievement.amputeeKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeKills().getID())));
		buttons.add(Achievement.blindKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindKills().getID())));
		buttons.add(Achievement.clumsyKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyKills().getID())));
		buttons.add(Achievement.dwarfKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfKills().getID())));
		buttons.add(Achievement.explosiveKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveKills().getID())));
		buttons.add(Achievement.featherweightKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightKills().getID())));
		buttons.add(Achievement.nakedKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedKills().getID())));
		buttons.add(Achievement.pacifistKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistKills().getID())));
		buttons.add(Achievement.uhcKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcKills().getID())));

		buttons.add(Achievement.amputeeWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeWave().getID())));
		buttons.add(Achievement.blindWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindWave().getID())));
		buttons.add(Achievement.clumsyWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyWave().getID())));
		buttons.add(Achievement.dwarfWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfWave().getID())));
		buttons.add(Achievement.explosiveWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveWave().getID())));
		buttons.add(Achievement.featherweightWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightWave().getID())));
		buttons.add(Achievement.nakedWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedWave().getID())));
		buttons.add(Achievement.pacifistWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistWave().getID())));
		buttons.add(Achievement.uhcWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcWave().getID())));

		buttons.add(Achievement.alone().getButton(PlayerManager.hasAchievement(id,
				Achievement.alone().getID())));
		buttons.add(Achievement.pacifistUhc().getButton(PlayerManager.hasAchievement(id,
				Achievement.pacifistUhc().getID())));
		buttons.add(Achievement.allChallenges().getButton(PlayerManager.hasAchievement(id,
				Achievement.allChallenges().getID())));
		buttons.add(Achievement.allGift().getButton(PlayerManager.hasAchievement(id,
				Achievement.allGift().getID())));
		buttons.add(Achievement.allAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.allAbility().getID())));
		buttons.add(Achievement.maxedAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.maxedAbility().getID())));
		buttons.add(Achievement.allMaxedAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.allMaxedAbility().getID())));
		buttons.add(Achievement.allEffect().getButton(PlayerManager.hasAchievement(id,
				Achievement.allEffect().getID())));
		buttons.add(Achievement.allKits().getButton(PlayerManager.hasAchievement(id,
				Achievement.allKits().getID())));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU, id),
				CommunicationManager.format("&6&l" + Bukkit.getOfflinePlayer(id).getName() + " " +
						LanguageManager.messages.achievements),
				true,
				false,
				"",
				buttons
		);
	}
	public static Inventory createPlayerAchievementsMenu(UUID id, int page) {
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement.topBalance1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance1().getID())));
		buttons.add(Achievement.topBalance2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance2().getID())));
		buttons.add(Achievement.topBalance3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance3().getID())));
		buttons.add(Achievement.topBalance4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance4().getID())));
		buttons.add(Achievement.topBalance5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance5().getID())));
		buttons.add(Achievement.topBalance6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance6().getID())));
		buttons.add(Achievement.topBalance7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance7().getID())));
		buttons.add(Achievement.topBalance8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance8().getID())));
		buttons.add(Achievement.topBalance9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topBalance9().getID())));

		buttons.add(Achievement.topKills1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills1().getID())));
		buttons.add(Achievement.topKills2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills2().getID())));
		buttons.add(Achievement.topKills3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills3().getID())));
		buttons.add(Achievement.topKills4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills4().getID())));
		buttons.add(Achievement.topKills5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills5().getID())));
		buttons.add(Achievement.topKills6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills6().getID())));
		buttons.add(Achievement.topKills7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills7().getID())));
		buttons.add(Achievement.topKills8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills8().getID())));
		buttons.add(Achievement.topKills9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topKills9().getID())));

		buttons.add(Achievement.topWave1().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave1().getID())));
		buttons.add(Achievement.topWave2().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave2().getID())));
		buttons.add(Achievement.topWave3().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave3().getID())));
		buttons.add(Achievement.topWave4().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave4().getID())));
		buttons.add(Achievement.topWave5().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave5().getID())));
		buttons.add(Achievement.topWave6().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave6().getID())));
		buttons.add(Achievement.topWave7().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave7().getID())));
		buttons.add(Achievement.topWave8().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave8().getID())));
		buttons.add(Achievement.topWave9().getButton(PlayerManager.hasAchievement(id,
				Achievement.topWave9().getID())));

		buttons.add(Achievement.totalGems1().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems1().getID())));
		buttons.add(Achievement.totalGems2().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems2().getID())));
		buttons.add(Achievement.totalGems3().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems3().getID())));
		buttons.add(Achievement.totalGems4().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems4().getID())));
		buttons.add(Achievement.totalGems5().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems5().getID())));
		buttons.add(Achievement.totalGems6().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems6().getID())));
		buttons.add(Achievement.totalGems7().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems7().getID())));
		buttons.add(Achievement.totalGems8().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems8().getID())));
		buttons.add(Achievement.totalGems9().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalGems9().getID())));

		buttons.add(Achievement.totalKills1().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills1().getID())));
		buttons.add(Achievement.totalKills2().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills2().getID())));
		buttons.add(Achievement.totalKills3().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills3().getID())));
		buttons.add(Achievement.totalKills4().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills4().getID())));
		buttons.add(Achievement.totalKills5().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills5().getID())));
		buttons.add(Achievement.totalKills6().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills6().getID())));
		buttons.add(Achievement.totalKills7().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills7().getID())));
		buttons.add(Achievement.totalKills8().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills8().getID())));
		buttons.add(Achievement.totalKills9().getButton(PlayerManager.hasAchievement(id,
				Achievement.totalKills9().getID())));

		buttons.add(Achievement.amputeeAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeAlone().getID())));
		buttons.add(Achievement.blindAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindAlone().getID())));
		buttons.add(Achievement.clumsyAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyAlone().getID())));
		buttons.add(Achievement.dwarfAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfAlone().getID())));
		buttons.add(Achievement.explosiveAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveAlone().getID())));
		buttons.add(Achievement.featherweightAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightAlone().getID())));
		buttons.add(Achievement.nakedAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedAlone().getID())));
		buttons.add(Achievement.pacifistAlone().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistAlone().getID())));
		buttons.add(Achievement.uhcAlone().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcAlone().getID())));

		buttons.add(Achievement.amputeeBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.amputeeBalance().getID())));
		buttons.add(Achievement.blindBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindBalance().getID())));
		buttons.add(Achievement.clumsyBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.clumsyBalance().getID())));
		buttons.add(Achievement.dwarfBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfBalance().getID())));
		buttons.add(Achievement.explosiveBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveBalance().getID())));
		buttons.add(Achievement.featherweightBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightBalance().getID())));
		buttons.add(Achievement.nakedBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedBalance().getID())));
		buttons.add(Achievement.pacifistBalance().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistBalance().getID())));
		buttons.add(Achievement.uhcBalance().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcBalance().getID())));

		buttons.add(Achievement.amputeeKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeKills().getID())));
		buttons.add(Achievement.blindKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindKills().getID())));
		buttons.add(Achievement.clumsyKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyKills().getID())));
		buttons.add(Achievement.dwarfKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfKills().getID())));
		buttons.add(Achievement.explosiveKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveKills().getID())));
		buttons.add(Achievement.featherweightKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightKills().getID())));
		buttons.add(Achievement.nakedKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedKills().getID())));
		buttons.add(Achievement.pacifistKills().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistKills().getID())));
		buttons.add(Achievement.uhcKills().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcKills().getID())));

		buttons.add(Achievement.amputeeWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.amputeeWave().getID())));
		buttons.add(Achievement.blindWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.blindWave().getID())));
		buttons.add(Achievement.clumsyWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.clumsyWave().getID())));
		buttons.add(Achievement.dwarfWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.dwarfWave().getID())));
		buttons.add(Achievement.explosiveWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.explosiveWave().getID())));
		buttons.add(Achievement.featherweightWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.featherweightWave().getID())));
		buttons.add(Achievement.nakedWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.nakedWave().getID())));
		buttons.add(Achievement.pacifistWave().getButton(PlayerManager.hasAchievement(id,

				Achievement.pacifistWave().getID())));
		buttons.add(Achievement.uhcWave().getButton(PlayerManager.hasAchievement(id,
				Achievement.uhcWave().getID())));

		buttons.add(Achievement.alone().getButton(PlayerManager.hasAchievement(id,
				Achievement.alone().getID())));
		buttons.add(Achievement.pacifistUhc().getButton(PlayerManager.hasAchievement(id,
				Achievement.pacifistUhc().getID())));
		buttons.add(Achievement.allChallenges().getButton(PlayerManager.hasAchievement(id,
				Achievement.allChallenges().getID())));
		buttons.add(Achievement.allGift().getButton(PlayerManager.hasAchievement(id,
				Achievement.allGift().getID())));
		buttons.add(Achievement.allAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.allAbility().getID())));
		buttons.add(Achievement.maxedAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.maxedAbility().getID())));
		buttons.add(Achievement.allMaxedAbility().getButton(PlayerManager.hasAchievement(id,
				Achievement.allMaxedAbility().getID())));
		buttons.add(Achievement.allEffect().getButton(PlayerManager.hasAchievement(id,
				Achievement.allEffect().getID())));
		buttons.add(Achievement.allKits().getButton(PlayerManager.hasAchievement(id,
				Achievement.allKits().getID())));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU, page, id),
				CommunicationManager.format("&6&l" + Bukkit.getOfflinePlayer(id).getName() + " " +
						LanguageManager.messages.achievements),
				true,
				false,
				"",
				buttons
		);
	}

	// Display player stats reset confirmation
	public static Inventory createResetStatsConfirmMenu(UUID playerID) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.RESET_STATS_CONFIRM_MENU,
				playerID,
				CommunicationManager.format("&4&l" + LanguageManager.messages.reset + "?")
		);
	}

	// Display crystal converter
	public static Inventory createCrystalConvertMenu(VDPlayer player) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.CRYSTAL_CONVERT_MENU, InventoryType.MENU,
						player.getPlayer().getUniqueId()),
				27,
				CommunicationManager.format("&9&l" + String.format(LanguageManager.names.crystalConverter,
						LanguageManager.names.crystal))
		);

		// Display crystals to convert
		inv.setItem(3, ItemManager.createItem(Material.DIAMOND_BLOCK,
				CommunicationManager.format("&b&l" + String.format(LanguageManager.messages.crystalsToConvert,
						LanguageManager.names.crystals) + ": " + (player.getGemBoost() * 5))));

		// Display gems to receive
		inv.setItem(5, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&a&l" + LanguageManager.messages.gemsToReceive + ": " +
						player.getGemBoost())));

		// Crystal balance display
		int balance = PlayerManager.getCrystalBalance(player.getID());

		inv.setItem(8, ItemManager.createItem(Material.DIAMOND,
				CommunicationManager.format("&b&l" + String.format(LanguageManager.messages.crystalBalance,
						LanguageManager.names.crystal) + ": &b" + balance)));

		// Option to increase by 1
		inv.setItem(9, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+1 " + LanguageManager.messages.gems)));

		// Option to increase by 10
		inv.setItem(11, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+10 " + LanguageManager.messages.gems)));

		// Option to increase by 100
		inv.setItem(13, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+100 " + LanguageManager.messages.gems)));

		// Option to increase by 1000
		inv.setItem(15, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+1000 " + LanguageManager.messages.gems)));

		// Option to reset
		inv.setItem(17, ItemManager.createItem(Material.LIGHT_BLUE_CONCRETE,
				CommunicationManager.format("&3&l" + LanguageManager.messages.reset)));

		// Option to decrease by 1
		inv.setItem(18, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-1 " + LanguageManager.messages.gems)));

		// Option to decrease by 10
		inv.setItem(20, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-10 " + LanguageManager.messages.gems)));

		// Option to decrease by 100
		inv.setItem(22, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-100 " + LanguageManager.messages.gems)));

		// Option to decrease by 1000
		inv.setItem(24, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-1000 " + LanguageManager.messages.gems)));

		// Option to exit
		inv.setItem(26, Buttons.exit());

		return inv;
	}

	// Display challenges for a player to select
	public static Inventory createSelectChallengesMenu(VDPlayer player, Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Set buttons
		buttons.add(Challenge.amputee().getButton(player.getChallenges().contains(Challenge.amputee())));
		buttons.add(Challenge.clumsy().getButton(player.getChallenges().contains(Challenge.clumsy())));
		buttons.add(Challenge.featherweight().getButton(player.getChallenges().contains(Challenge.featherweight())));
		buttons.add(Challenge.pacifist().getButton(player.getChallenges().contains(Challenge.pacifist())));
		buttons.add(Challenge.dwarf().getButton(player.getChallenges().contains(Challenge.dwarf())));
		buttons.add(Challenge.uhc().getButton(player.getChallenges().contains(Challenge.uhc())));
		buttons.add(Challenge.explosive().getButton(player.getChallenges().contains(Challenge.explosive())));
		buttons.add(Challenge.naked().getButton(player.getChallenges().contains(Challenge.naked())));
		buttons.add(Challenge.blind().getButton(player.getChallenges().contains(Challenge.blind())));
		buttons.add(Challenge.none().getButton(player.getChallenges().isEmpty()));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.SELECT_CHALLENGES_MENU, InventoryType.MENU,
						player.getPlayer().getUniqueId(), arena),
				CommunicationManager.format("&5&l" + arena.getName() + " " + LanguageManager.messages.challenges),
				true,
				buttons
		);
	}

	// Display arena information
	public static Inventory createArenaInfoMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Maximum players
		buttons.add(ItemManager.createItem(Material.NETHERITE_HELMET,
				CommunicationManager.format("&4&l" + LanguageManager.arenaStats.maxPlayers.name +
						": &4" + arena.getMaxPlayers()), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.maxPlayers.description, Utils.LORE_CHAR_LIMIT)));

		// Minimum players
		buttons.add(ItemManager.createItem(Material.NETHERITE_BOOTS,
				CommunicationManager.format("&2&l" + LanguageManager.arenaStats.minPlayers.name +
						": &2" + arena.getMinPlayers()), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.minPlayers.description, Utils.LORE_CHAR_LIMIT)));

		// Max waves
		String waves;
		if (arena.getMaxWaves() < 0)
			waves = LanguageManager.messages.unlimited;
		else waves = Integer.toString(arena.getMaxWaves());
		buttons.add(ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&3&l" + LanguageManager.arenaStats.maxWaves.name +
						": &3" + waves), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.maxWaves.description, Utils.LORE_CHAR_LIMIT)));

		// Wave time limit
		String limit;
		if (arena.getWaveTimeLimit() < 0)
			limit = LanguageManager.messages.unlimited;
		else limit = arena.getWaveTimeLimit() + " minute(s)";
		buttons.add(ItemManager.createItem(Material.CLOCK,
				CommunicationManager.format("&9&l" + LanguageManager.arenaStats.timeLimit.name +
						": &9" + limit), CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.timeLimit.description, Utils.LORE_CHAR_LIMIT)));

		// Wolf cap
		buttons.add(ItemManager.createItem(Material.BONE,
				CommunicationManager.format("&6&l" + LanguageManager.arenaStats.wolfCap.name +
						": &6" + arena.getWolfCap()),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.wolfCap.description, Utils.LORE_CHAR_LIMIT)));

		// Golem cap
		buttons.add(ItemManager.createItem(Material.IRON_INGOT,
				CommunicationManager.format("&e&l" + LanguageManager.arenaStats.golemCap.name +
						": &e" + arena.getGolemCap()),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.golemCap.description, Utils.LORE_CHAR_LIMIT)));

		// Allowed kits
		buttons.add(ItemManager.createItem(Material.ENDER_CHEST,
				CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits)));

		// Forced challenges
		buttons.add(ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&9&l" + LanguageManager.messages.forcedChallenges)));

		// Dynamic mob count
		buttons.add(ItemManager.createItem(Material.SLIME_BALL,
				CommunicationManager.format("&e&l" +
						LanguageManager.arenaStats.dynamicMobCount.name +
						": " + getToggleStatus(arena.hasDynamicCount())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.dynamicMobCount.description, Utils.LORE_CHAR_LIMIT)));

		// Dynamic difficulty
		buttons.add(ItemManager.createItem(Material.MAGMA_CREAM,
				CommunicationManager.format("&6&l" +
						LanguageManager.arenaStats.dynamicDifficulty.name + ": " +
						getToggleStatus(arena.hasDynamicDifficulty())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.dynamicDifficulty.description, Utils.LORE_CHAR_LIMIT)));

		// Dynamic prices
		buttons.add(ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&b&l" +
						LanguageManager.arenaStats.dynamicPrices.name +
						": " + getToggleStatus(arena.hasDynamicPrices())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.dynamicPrices.description, Utils.LORE_CHAR_LIMIT)));

		// Dynamic time limit
		buttons.add(ItemManager.createItem(Material.SNOWBALL,
				CommunicationManager.format("&a&l" +
						LanguageManager.arenaStats.dynamicTimeLimit.name +
						": " + getToggleStatus(arena.hasDynamicLimit())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.dynamicTimeLimit.description, Utils.LORE_CHAR_LIMIT)));

		// Late arrival
		buttons.add( ItemManager.createItem(Material.DAYLIGHT_DETECTOR,
				CommunicationManager.format("&e&l" + LanguageManager.arenaStats.lateArrival.name +
						": " + getToggleStatus(arena.hasLateArrival())), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.lateArrival.description, Utils.LORE_CHAR_LIMIT)));

		// Player spawn particles
		buttons.add( ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&e&l" + LanguageManager.names.playerSpawnParticles +
						": " + getToggleStatus(arena.hasSpawnParticles()))));

		// Monster spawn particles
		buttons.add( ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&d&l" + LanguageManager.names.monsterSpawnParticles +
						": " + getToggleStatus(arena.hasMonsterParticles()))));

		// Villager spawn particles
		buttons.add( ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&a&l" +
						LanguageManager.names.villagerSpawnParticles + ": " +
						getToggleStatus(arena.hasVillagerParticles()))));

		// Community chest
		buttons.add( ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&l" + LanguageManager.names.communityChest +
						": " + getToggleStatus(arena.hasCommunity()))));

		// Difficulty multiplier
		buttons.add( ItemManager.createItem(Material.TURTLE_HELMET,
				CommunicationManager.format("&4&l" +
						LanguageManager.arenaStats.difficultyMultiplier.name + ": &4" +
						arena.getDifficultyMultiplier()), ItemManager.BUTTON_FLAGS, null,
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.difficultyMultiplier.description, Utils.LORE_CHAR_LIMIT)));

		// Arena records
		List<String> records = new ArrayList<>();
		arena.getSortedDescendingRecords().forEach(arenaRecord -> {
			records.add(CommunicationManager.format("&f" + LanguageManager.messages.wave + " " +
					arenaRecord.getWave()));
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
		buttons.add( ItemManager.createItem(Material.GOLDEN_HELMET,
				CommunicationManager.format("&e&l" + LanguageManager.messages.arenaRecords),
				ItemManager.BUTTON_FLAGS, null, records));

		return InventoryFactory.createDynamicSizeInventory(
				new InventoryMeta(InventoryID.ARENA_INFO_MENU, InventoryType.MENU, arena),
				CommunicationManager.format("&6&l" +
						String.format(LanguageManager.messages.arenaInfo, arena.getName())),
				false,
				buttons
		);
	}

	// Easy way to get a string for a toggle status
	private static String getToggleStatus(boolean status) {
		String toggle;
		if (status)
			toggle = "&a&l" + LanguageManager.messages.onToggle;
		else toggle = "&c&l" + LanguageManager.messages.offToggle;
		return toggle;
	}

	// Modify the price of an item
	@NotNull
	private static ItemStack modifyPrice(ItemStack itemStack, double modifier) {
		ItemStack item = itemStack.clone();
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		try {
			List<String> lore = Objects.requireNonNull(meta.getLore());
			int price = (int) Math.round(Integer.parseInt(lore.get(lore.size() - 1)
					.substring(6 + LanguageManager.messages.gems.length())) * modifier / 5) * 5;
			lore.set(lore.size() - 1, CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
					price));
			meta.setLore(lore);
			item.setItemMeta(meta);
		} catch (NumberFormatException | NullPointerException ignored) {
		}
		return item;
	}

	private static void sort(List<ItemStack> list) {
		list.sort(Comparator.comparingInt(itemStack -> {
			try {
				List<String> lore = Objects.requireNonNull(Objects.requireNonNull(itemStack.getItemMeta()).getLore());
				return Integer.parseInt(lore.get(lore.size() - 1)
						.substring(6 + LanguageManager.messages.gems.length()));
			} catch (NumberFormatException | NullPointerException e) {
				return Integer.MAX_VALUE;
			}
		}));
	}
}
