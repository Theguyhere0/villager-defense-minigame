package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
				"Arena",
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

		// Option to edit spawn table
		buttons.add(ItemManager.createItem(Material.DRAGON_HEAD,
				CommunicationManager.format("&3&lSpawn Table")));

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

	// Spawn table menu for an arena
	public static Inventory createSpawnTableMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();
		String chosen = arena.getSpawnTableFile();

		// Option to set spawn table to default
		buttons.add(ItemManager.createItem(Material.OAK_WOOD,
				CommunicationManager.format("&4&lDefault"), ItemManager.BUTTON_FLAGS,
				chosen.equals("default") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to default.yml")));

		// Option to set spawn table to global option 1
		buttons.add(ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&6&lOption 1"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option1") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option1.yml")));

		// Option to set spawn table to global option 2
		buttons.add(ItemManager.createItem(Material.ORANGE_CONCRETE,
				CommunicationManager.format("&6&lOption 2"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option2") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option2.yml")));

		// Option to set spawn table to global option 3
		buttons.add(ItemManager.createItem(Material.YELLOW_CONCRETE,
				CommunicationManager.format("&6&lOption 3"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option3") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option3.yml")));

		// Option to set spawn table to global option 4
		buttons.add(ItemManager.createItem(Material.BROWN_CONCRETE,
				CommunicationManager.format("&6&lOption 4"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option4") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option4.yml")));

		// Option to set spawn table to global option 5
		buttons.add(ItemManager.createItem(Material.LIGHT_GRAY_CONCRETE,
				CommunicationManager.format("&6&lOption 5"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option5") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option5.yml")));

		// Option to set spawn table to global option 6
		buttons.add(ItemManager.createItem(Material.WHITE_CONCRETE,
				CommunicationManager.format("&6&lOption 6"), ItemManager.BUTTON_FLAGS,
				chosen.equals("option6") ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to option6.yml")));

		// Option to set spawn table to custom option
		buttons.add(ItemManager.createItem(Material.BIRCH_WOOD,
				CommunicationManager.format("&e&lCustom"), ItemManager.BUTTON_FLAGS,
				chosen.length() < 4 ? ItemManager.glow() : null,
				CommunicationManager.format("&7Sets spawn table to a[arena number].yml"),
				CommunicationManager.format("&7(Check the arena number in arenaData.yml)")));

		return InventoryFactory.createFixedSizeInventory(
				new InventoryMeta(InventoryID.SPAWN_TABLE_MENU, InventoryType.MENU, arena),
				chosen.equals("custom") ?
						CommunicationManager.format("&3&lSpawn Table: " + arena.getPath() + ".yml") :
						CommunicationManager.format("&3&lSpawn Table: " + arena.getSpawnTableFile() + ".yml"),
				1,
				true,
				buttons
		);
	}

	// Menu for editing the shop settings of an arena
	public static Inventory createShopSettingsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to create a custom shop
		buttons.add(ItemManager.createItem(Material.QUARTZ,
				CommunicationManager.format("&a&lEdit Custom Shop")));

		// Option to toggle default shop
		buttons.add(ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&6&lDefault Shop: " + getToggleStatus(arena.hasNormal())),
				CommunicationManager.format("&7Turn default shop on and off")));

		// Option to toggle custom shop
		buttons.add(ItemManager.createItem(Material.QUARTZ_BLOCK,
				CommunicationManager.format("&2&lCustom Shop: " + getToggleStatus(arena.hasCustom())),
				CommunicationManager.format("&7Turn custom shop on and off")));

		// Option to toggle custom shop
		buttons.add(ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&3&lEnchant Shop: " + getToggleStatus(arena.hasEnchants())),
				CommunicationManager.format("&7Turn enchants shop on and off")));

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

	// Menu for adding custom items
	public static Inventory createCustomItemsMenu(Arena arena, int id) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.CUSTOM_ITEMS_MENU, InventoryType.MENU, arena, id),
				27,
				CommunicationManager.format("&6&lEdit Item")
		);

		// Item of interest
		inv.setItem(4, arena.getCustomShop().getItem(id));

		// Option to set un-purchasable
		inv.setItem(8, ItemManager.createItem(Material.BEDROCK,
				CommunicationManager.format("&5&lToggle Un-purchasable")));

		// Option to increase by 1
		inv.setItem(9, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+1 gem")));

		// Option to increase by 10
		inv.setItem(11, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+10 gems")));

		// Option to increase by 100
		inv.setItem(13, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+100 gems")));

		// Option to increase by 1000
		inv.setItem(15, ItemManager.createItem(Material.LIME_CONCRETE,
				CommunicationManager.format("&a&l+1000 gems")));

		// Option to delete item
		inv.setItem(17, ItemManager.createItem(Material.LAVA_BUCKET,
				CommunicationManager.format("&4&lDELETE")));

		// Option to decrease by 1
		inv.setItem(18, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-1 gem")));

		// Option to decrease by 10
		inv.setItem(20, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-10 gems")));

		// Option to decrease by 100
		inv.setItem(22, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-100 gems")));

		// Option to decrease by 1000
		inv.setItem(24, ItemManager.createItem(Material.RED_CONCRETE,
				CommunicationManager.format("&c&l-1000 gems")));

		// Option to exit
		inv.setItem(26, Buttons.exit());

		return inv;
	}

	// Confirmation menu for removing custom item
	public static Inventory createCustomItemConfirmMenu(Arena arena, int id) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.CUSTOM_ITEM_CONFIRM_MENU,
				null,
				arena,
				id,
				CommunicationManager.format("&4&lRemove Custom Item?")
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

		// Option to toggle experience drop
		buttons.add(ItemManager.createItem(Material.EXPERIENCE_BOTTLE,
				CommunicationManager.format("&b&lExperience Drop: " + getToggleStatus(arena.hasExpDrop())),
				CommunicationManager.format("&7Change whether experience drop or go"),
				CommunicationManager.format("&7straight into the killer's experience bar")));

		// Option to toggle item dropping
		buttons.add(ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&9&lItem Drop: " + getToggleStatus(arena.hasGemDrop())),
				CommunicationManager.format("&7Change whether gems and loot drop"),
				CommunicationManager.format("&7as physical items or go straight"),
				CommunicationManager.format("&7into the killer's balance/inventory")));

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

		if (arena.getBannedKits().contains(Kit.orc().getName()))
			inv.setItem(9, Kit.orc().getButton(-1, false));
		else inv.setItem(9, Kit.orc().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.farmer().getName()))
			inv.setItem(10, Kit.farmer().getButton(-1, false));
		else inv.setItem(10, Kit.farmer().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.soldier().getName()))
			inv.setItem(11, Kit.soldier().getButton(-1, false));
		else inv.setItem(11, Kit.soldier().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.alchemist().getName()))
			inv.setItem(12, Kit.alchemist().getButton(-1, false));
		else inv.setItem(12, Kit.alchemist().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.tailor().getName()))
			inv.setItem(13, Kit.tailor().getButton(-1, false));
		else inv.setItem(13, Kit.tailor().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.trader().getName()))
			inv.setItem(14, Kit.trader().getButton(-1, false));
		else inv.setItem(14, Kit.trader().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.summoner().getName()))
			inv.setItem(15, Kit.summoner().getButton(-1, false));
		else inv.setItem(15, Kit.summoner().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.reaper().getName()))
			inv.setItem(16, Kit.reaper().getButton(-1, false));
		else inv.setItem(16, Kit.reaper().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.phantom().getName()))
			inv.setItem(17, Kit.phantom().getButton(-1, false));
		else inv.setItem(17, Kit.phantom().getButton(-1, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (arena.getBannedKits().contains(Kit.mage().getName()))
			inv.setItem(27, Kit.mage().getButton(-1, false));
		else inv.setItem(27, Kit.mage().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.ninja().getName()))
			inv.setItem(28, Kit.ninja().getButton(-1, false));
		else inv.setItem(28, Kit.ninja().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.templar().getName()))
			inv.setItem(29, Kit.templar().getButton(-1, false));
		else inv.setItem(29, Kit.templar().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.warrior().getName()))
			inv.setItem(30, Kit.warrior().getButton(-1, false));
		else inv.setItem(30, Kit.warrior().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.knight().getName()))
			inv.setItem(31, Kit.knight().getButton(-1, false));
		else inv.setItem(31, Kit.knight().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.priest().getName()))
			inv.setItem(32, Kit.priest().getButton(-1, false));
		else inv.setItem(32, Kit.priest().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.siren().getName()))
			inv.setItem(33, Kit.siren().getButton(-1, false));
		else inv.setItem(33, Kit.siren().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.monk().getName()))
			inv.setItem(34, Kit.monk().getButton(-1, false));
		else inv.setItem(34, Kit.monk().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.messenger().getName()))
			inv.setItem(35, Kit.messenger().getButton(-1, false));
		else inv.setItem(35, Kit.messenger().getButton(-1, true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (arena.getBannedKits().contains(Kit.blacksmith().getName()))
			inv.setItem(45, Kit.blacksmith().getButton(-1, false));
		else inv.setItem(45, Kit.blacksmith().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.witch().getName()))
			inv.setItem(46, Kit.witch().getButton(-1, false));
		else inv.setItem(46, Kit.witch().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.merchant().getName()))
			inv.setItem(47, Kit.merchant().getButton(-1, false));
		else inv.setItem(47, Kit.merchant().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.vampire().getName()))
			inv.setItem(48, Kit.vampire().getButton(-1, false));
		else inv.setItem(48, Kit.vampire().getButton(-1, true));
		if (arena.getBannedKits().contains(Kit.giant().getName()))
			inv.setItem(49, Kit.giant().getButton(-1, false));
		else inv.setItem(49, Kit.giant().getButton(-1, true));

		// Option to exit
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Menu for forced challenges of an arena
	public static Inventory createForcedChallengesMenu(Arena arena, boolean display) {
		List<ItemStack> buttons = new ArrayList<>();

		// Set buttons
		buttons.add(Challenge.amputee().getButton(arena.getForcedChallenges()
				.contains(Challenge.amputee().getName())));
		buttons.add(Challenge.clumsy().getButton(arena.getForcedChallenges()
				.contains(Challenge.clumsy().getName())));
		buttons.add(Challenge.featherweight().getButton(arena.getForcedChallenges()
				.contains(Challenge.featherweight().getName())));
		buttons.add(Challenge.pacifist().getButton(arena.getForcedChallenges()
				.contains(Challenge.pacifist().getName())));
		buttons.add(Challenge.dwarf().getButton(arena.getForcedChallenges()
				.contains(Challenge.dwarf().getName())));
		buttons.add(Challenge.uhc().getButton(arena.getForcedChallenges()
				.contains(Challenge.uhc().getName())));
		buttons.add(Challenge.explosive().getButton(arena.getForcedChallenges()
				.contains(Challenge.explosive().getName())));
		buttons.add(Challenge.naked().getButton(arena.getForcedChallenges()
				.contains(Challenge.naked().getName())));
		buttons.add(Challenge.blind().getButton(arena.getForcedChallenges()
				.contains(Challenge.blind().getName())));

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

		// Option to edit gem pickup sound
		buttons.add( ItemManager.createItem(Material.MUSIC_DISC_FAR,
				CommunicationManager.format("&b&lGem Pickup Sound: " +
						getToggleStatus(arena.hasGemSound())),
				ItemManager.BUTTON_FLAGS,
				null,
				CommunicationManager.format("&7Played when players pick up gems")));

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
		buttons.add( arena.getWaitingSoundButton("blocks"));
		buttons.add( arena.getWaitingSoundButton("cat"));
		buttons.add( arena.getWaitingSoundButton("chirp"));
		buttons.add( arena.getWaitingSoundButton("far"));
		buttons.add( arena.getWaitingSoundButton("mall"));
		buttons.add( arena.getWaitingSoundButton("mellohi"));
		if (NMSVersion.isGreaterEqualThan(NMSVersion.v1_18_R1))
			buttons.add( arena.getWaitingSoundButton("otherside"));

		buttons.add( arena.getWaitingSoundButton("pigstep"));
		buttons.add( arena.getWaitingSoundButton("stal"));
		buttons.add( arena.getWaitingSoundButton("strad"));
		buttons.add( arena.getWaitingSoundButton("wait"));
		buttons.add( arena.getWaitingSoundButton("ward"));
		buttons.add( arena.getWaitingSoundButton("none"));

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
		Objects.requireNonNull(Main.plugin.getArenaData().getConfigurationSection("arena")).getKeys(false)
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

		inv.setItem(0, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&4&l" + LanguageManager.messages.level +
						" &9&l" + level + " &4&l" + LanguageManager.names.weaponShop +
						(arena.hasNormal() ? "" : disabled)), ItemManager.BUTTON_FLAGS, arena.hasNormal() ?
						ItemManager.glow() : null));

		inv.setItem(1, ItemManager.createItem(Material.GOLDEN_CHESTPLATE,
				CommunicationManager.format("&5&l" + LanguageManager.messages.level +
						" &9&l" + level + " &5&l" + LanguageManager.names.armorShop +
						(arena.hasNormal() ? "" : disabled)), ItemManager.BUTTON_FLAGS, arena.hasNormal() ?
						ItemManager.glow() : null));

		inv.setItem(2, ItemManager.createItem(Material.GOLDEN_APPLE,
				CommunicationManager.format("&3&l" + LanguageManager.messages.level +
						" &9&l" + level + " &3&l" + LanguageManager.names.consumableShop +
						(arena.hasNormal() ? "" : disabled)), ItemManager.BUTTON_FLAGS, arena.hasNormal() ?
						ItemManager.glow() : null));

		inv.setItem(4, ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&a&l" + LanguageManager.names.enchantShop +
						(arena.hasEnchants() ? "" : disabled)), ItemManager.BUTTON_FLAGS, arena.hasEnchants() ?
						ItemManager.glow() : null));

		inv.setItem(6, ItemManager.createItem(Material.QUARTZ,
				CommunicationManager.format("&6&l" + LanguageManager.names.customShop +
						(arena.hasCustom() ? "" : disabled)), ItemManager.BUTTON_FLAGS, arena.hasNormal() ?
						ItemManager.glow() : null));

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
				27,
				CommunicationManager.format("&4&l" + LanguageManager.messages.level +
						" &9&l" + level + " &4&l" + LanguageManager.names.weaponShop)
		);

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
		inv.setItem(22, Buttons.exit());

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
				18,
				CommunicationManager.format("&3&l" + LanguageManager.messages.level +
						" &9&l" + level + " &3&l" + LanguageManager.names.consumableShop)
		);

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
		inv.setItem(13, Buttons.exit());

		return inv;
	}

	// Generate the enchant shop
	public static Inventory createEnchantShopMenu() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.ENCHANT_SHOP_MENU, InventoryType.MENU),
				54,
				CommunicationManager.format("&a&l" + LanguageManager.names.enchantShop)
		);

		// Melee enchants
		inv.setItem(0, ItemManager.createItem(Material.PISTON,
				CommunicationManager.format("&a&lIncrease Knockback"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(1, ItemManager.createItem(Material.GOLDEN_HOE,
				CommunicationManager.format("&a&lIncrease Sweeping Edge"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(2, ItemManager.createItem(Material.DIAMOND_SWORD,
				CommunicationManager.format("&a&lIncrease Smite"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 7 XP Levels")));
		inv.setItem(3, ItemManager.createItem(Material.NETHERITE_AXE,
				CommunicationManager.format("&a&lIncrease Sharpness"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 8 XP Levels")));
		inv.setItem(4, ItemManager.createItem(Material.FIRE,
				CommunicationManager.format("&a&lIncrease Fire Aspect"),
				CommunicationManager.format("&2Costs 10 XP Levels")));

		// Ranged enchants
		inv.setItem(18, ItemManager.createItem(Material.STICKY_PISTON,
				CommunicationManager.format("&a&lIncrease Punch"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(19, ItemManager.createItem(Material.SPECTRAL_ARROW,
				CommunicationManager.format("&a&lIncrease Piercing"),
				CommunicationManager.format("&2Costs 5 XP Levels")));
		inv.setItem(20, ItemManager.createItem(Material.REDSTONE_TORCH,
				CommunicationManager.format("&a&lIncrease Quick Charge"),
				CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(21, ItemManager.createItem(Material.BOW, CommunicationManager.format("&a&lIncrease Power"),
				CommunicationManager.format("&2Costs 8 XP Levels")));
		inv.setItem(22, ItemManager.createItem(Material.TRIDENT,
				CommunicationManager.format("&a&lIncrease Loyalty"),
				ItemManager.BUTTON_FLAGS, null, CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(23, ItemManager.createItem(Material.MAGMA_BLOCK,
				CommunicationManager.format("&a&lAdd Flame"),
				CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(24, ItemManager.createItem(Material.CROSSBOW,
				CommunicationManager.format("&a&lAdd Multishot"),
				CommunicationManager.format("&2Costs 10 XP Levels")));
		inv.setItem(25, ItemManager.createItem(Material.BEACON, CommunicationManager.format("&a&lAdd Infinity"),
				CommunicationManager.format("&2Costs 15 XP Levels")));

		// Armor enchants
		inv.setItem(36, ItemManager.createItem(Material.TNT,
				CommunicationManager.format("&a&lIncrease Blast Protection"),
				CommunicationManager.format("&2Costs 4 XP Levels")));
		inv.setItem(37, ItemManager.createItem(Material.VINE,
				CommunicationManager.format("&a&lIncrease Thorns"),
				CommunicationManager.format("&2Costs 5 XP Levels")));
		inv.setItem(38, ItemManager.createItem(Material.ARROW,
				CommunicationManager.format("&a&lIncrease Projectile Protection"),
				CommunicationManager.format("&2Costs 6 XP Levels")));
		inv.setItem(39, ItemManager.createItem(Material.SHIELD,
				CommunicationManager.format("&a&lIncrease Protection"),
				CommunicationManager.format("&2Costs 8 XP Levels")));

		// General enchants
		inv.setItem(43, ItemManager.createItem(Material.BEDROCK,
				CommunicationManager.format("&a&lIncrease Unbreaking"),
				CommunicationManager.format("&2Costs 3 XP Levels")));
		inv.setItem(44, ItemManager.createItem(Material.ANVIL, CommunicationManager.format("&a&lAdd Mending"),
				CommunicationManager.format("&2Costs 20 XP Levels")));

		// Return option
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Display player stats
	public static Inventory createPlayerStatsMenu(Player player) {
		FileConfiguration playerData = Main.plugin.getPlayerData();
		String name = player.getName();
		UUID id = player.getUniqueId();

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.PLAYER_STATS_MENU, InventoryType.MENU, player),
				18,
				CommunicationManager.format("&2&l" + String.format(LanguageManager.messages.playerStatistics,
						name))
		);

		// Total kills
		inv.setItem(0, ItemManager.createItem(Material.DRAGON_HEAD,
				CommunicationManager.format("&4&l" + LanguageManager.playerStats.totalKills.name +
						": &4" + playerData.getInt(id + ".totalKills")),
				CommunicationManager.format("&7" +
						LanguageManager.playerStats.totalKills.description)));

		// Top kills
		inv.setItem(10, ItemManager.createItem(Material.ZOMBIE_HEAD,
				CommunicationManager.format("&c&l" + LanguageManager.playerStats.topKills.name +
						": &c" + playerData.getInt(id + ".topKills")),
				CommunicationManager.format("&7" +
						LanguageManager.playerStats.topKills.description)));

		// Total gems
		inv.setItem(2, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&2&l" + LanguageManager.playerStats.totalGems.name +
						": &2" + playerData.getInt(id + ".totalGems")),
				CommunicationManager.format("&7" +
						LanguageManager.playerStats.totalGems.description)));

		// Top balance
		inv.setItem(12, ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&a&l" + LanguageManager.playerStats.topBalance.name +
						": &a" + playerData.getInt(id + ".topBalance")),
				CommunicationManager.format("&7" +
						LanguageManager.playerStats.topBalance.description)));

		// Top wave
		inv.setItem(4, ItemManager.createItem(Material.GOLDEN_SWORD,
				CommunicationManager.format("&3&l" + LanguageManager.playerStats.topWave.name +
						": &3" + playerData.getInt(id + ".topWave")),
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
		inv.setItem(16, ItemManager.createItem(
				Material.LAVA_BUCKET,
				CommunicationManager.format("&d&l" + LanguageManager.messages.reset),
				CommunicationManager.format("&5&l" + LanguageManager.messages.resetWarning)
		));

		// Crystal balance
		inv.setItem(8, ItemManager.createItem(Material.DIAMOND,
				CommunicationManager.format("&b&l" + LanguageManager.messages.crystalBalance +
						": &b" + playerData.getInt(id + ".crystalBalance"))));

		return inv;
	}

	// Display kits for a player
	public static Inventory createPlayerKitsMenu(Player owner, String requester) {
		FileConfiguration playerData = Main.plugin.getPlayerData();
		String name = owner.getName();
		UUID id = owner.getUniqueId();
		String path = id + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.PLAYER_KITS_MENU, InventoryType.MENU, owner),
				54,
				CommunicationManager.format("&9&l" + String.format(LanguageManager.messages.playerKits, name))
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE,
					CommunicationManager.format("&a&l" + LanguageManager.names.giftKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.giftKitsDescription, Utils.LORE_CHAR_LIMIT)));

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
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

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
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

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
			inv.setItem(52, ItemManager.createItem(Material.DIAMOND,
					CommunicationManager.format("&b&l" + LanguageManager.messages.crystalBalance +
							": &b" + playerData.getInt(id + ".crystalBalance"))));

		// Option to exit
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Display kits for a player to select
	public static Inventory createSelectKitsMenu(Player player, Arena arena) {
		FileConfiguration playerData = Main.plugin.getPlayerData();
		String path = player.getUniqueId() + ".kits.";

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.SELECT_KITS_MENU, InventoryType.MENU, player, arena),
				54,
				CommunicationManager.format("&9&l" + arena.getName() + " " + LanguageManager.messages.kits)
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, ItemManager.createItem(Material.LIME_STAINED_GLASS_PANE,
					CommunicationManager.format("&a&l" + LanguageManager.names.giftKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.giftKitsDescription, Utils.LORE_CHAR_LIMIT)));

		if (!arena.getBannedKits().contains("Orc"))
			inv.setItem(9, Kit.orc().getButton(1, false));
		if (!arena.getBannedKits().contains("Farmer"))
			inv.setItem(10, Kit.farmer().getButton(1, false));
		if (!arena.getBannedKits().contains("Soldier"))
			inv.setItem(11, Kit.soldier().getButton(playerData.getBoolean(
					path + Kit.soldier().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Alchemist"))
			inv.setItem(12, Kit.alchemist().getButton(playerData.getBoolean(
					path + Kit.alchemist().getName()) ?
					1 : 0, false));
		if (!arena.getBannedKits().contains("Tailor"))
			inv.setItem(13, Kit.tailor().getButton(playerData.getBoolean(
					path + Kit.tailor().getName()) ? 1 : 0,
					false));
		if (!arena.getBannedKits().contains("Trader"))
			inv.setItem(14, Kit.trader().getButton(playerData.getBoolean(
					path + Kit.trader().getName()) ? 1 : 0,
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
			inv.setItem(i, ItemManager.createItem(Material.MAGENTA_STAINED_GLASS_PANE,
					CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.abilityKitsDescription, Utils.LORE_CHAR_LIMIT)));

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
			inv.setItem(i, ItemManager.createItem(Material.YELLOW_STAINED_GLASS_PANE,
					CommunicationManager.format("&e&l" + LanguageManager.names.effectKits),
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
							LanguageManager.messages.effectKitsDescription, Utils.LORE_CHAR_LIMIT)));

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
		inv.setItem(53, Buttons.exit());

		return inv;
	}

	// Display achievements for a player
	public static Inventory createPlayerAchievementsMenu(Player owner) {
		List<String> achievements = Main.plugin.getPlayerData()
				.getStringList(owner.getUniqueId() + ".achievements");
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement.topBalance1().getButton(achievements.contains(Achievement.topBalance1().getID())));
		buttons.add(Achievement.topBalance2().getButton(achievements.contains(Achievement.topBalance2().getID())));
		buttons.add(Achievement.topBalance3().getButton(achievements.contains(Achievement.topBalance3().getID())));
		buttons.add(Achievement.topBalance4().getButton(achievements.contains(Achievement.topBalance4().getID())));
		buttons.add(Achievement.topBalance5().getButton(achievements.contains(Achievement.topBalance5().getID())));
		buttons.add(Achievement.topBalance6().getButton(achievements.contains(Achievement.topBalance6().getID())));
		buttons.add(Achievement.topBalance7().getButton(achievements.contains(Achievement.topBalance7().getID())));
		buttons.add(Achievement.topBalance8().getButton(achievements.contains(Achievement.topBalance8().getID())));
		buttons.add(Achievement.topBalance9().getButton(achievements.contains(Achievement.topBalance9().getID())));

		buttons.add(Achievement.topKills1().getButton(achievements.contains(Achievement.topKills1().getID())));
		buttons.add(Achievement.topKills2().getButton(achievements.contains(Achievement.topKills2().getID())));
		buttons.add(Achievement.topKills3().getButton(achievements.contains(Achievement.topKills3().getID())));
		buttons.add(Achievement.topKills4().getButton(achievements.contains(Achievement.topKills4().getID())));
		buttons.add(Achievement.topKills5().getButton(achievements.contains(Achievement.topKills5().getID())));
		buttons.add(Achievement.topKills6().getButton(achievements.contains(Achievement.topKills6().getID())));
		buttons.add(Achievement.topKills7().getButton(achievements.contains(Achievement.topKills7().getID())));
		buttons.add(Achievement.topKills8().getButton(achievements.contains(Achievement.topKills8().getID())));
		buttons.add(Achievement.topKills9().getButton(achievements.contains(Achievement.topKills9().getID())));

		buttons.add(Achievement.topWave1().getButton(achievements.contains(Achievement.topWave1().getID())));
		buttons.add(Achievement.topWave2().getButton(achievements.contains(Achievement.topWave2().getID())));
		buttons.add(Achievement.topWave3().getButton(achievements.contains(Achievement.topWave3().getID())));
		buttons.add(Achievement.topWave4().getButton(achievements.contains(Achievement.topWave4().getID())));
		buttons.add(Achievement.topWave5().getButton(achievements.contains(Achievement.topWave5().getID())));
		buttons.add(Achievement.topWave6().getButton(achievements.contains(Achievement.topWave6().getID())));
		buttons.add(Achievement.topWave7().getButton(achievements.contains(Achievement.topWave7().getID())));
		buttons.add(Achievement.topWave8().getButton(achievements.contains(Achievement.topWave8().getID())));
		buttons.add(Achievement.topWave9().getButton(achievements.contains(Achievement.topWave9().getID())));

		buttons.add(Achievement.totalGems1().getButton(achievements.contains(Achievement.totalGems1().getID())));
		buttons.add(Achievement.totalGems2().getButton(achievements.contains(Achievement.totalGems2().getID())));
		buttons.add(Achievement.totalGems3().getButton(achievements.contains(Achievement.totalGems3().getID())));
		buttons.add(Achievement.totalGems4().getButton(achievements.contains(Achievement.totalGems4().getID())));
		buttons.add(Achievement.totalGems5().getButton(achievements.contains(Achievement.totalGems5().getID())));
		buttons.add(Achievement.totalGems6().getButton(achievements.contains(Achievement.totalGems6().getID())));
		buttons.add(Achievement.totalGems7().getButton(achievements.contains(Achievement.totalGems7().getID())));
		buttons.add(Achievement.totalGems8().getButton(achievements.contains(Achievement.totalGems8().getID())));
		buttons.add(Achievement.totalGems9().getButton(achievements.contains(Achievement.totalGems9().getID())));

		buttons.add(Achievement.totalKills1().getButton(achievements.contains(Achievement.totalKills1().getID())));
		buttons.add(Achievement.totalKills2().getButton(achievements.contains(Achievement.totalKills2().getID())));
		buttons.add(Achievement.totalKills3().getButton(achievements.contains(Achievement.totalKills3().getID())));
		buttons.add(Achievement.totalKills4().getButton(achievements.contains(Achievement.totalKills4().getID())));
		buttons.add(Achievement.totalKills5().getButton(achievements.contains(Achievement.totalKills5().getID())));
		buttons.add(Achievement.totalKills6().getButton(achievements.contains(Achievement.totalKills6().getID())));
		buttons.add(Achievement.totalKills7().getButton(achievements.contains(Achievement.totalKills7().getID())));
		buttons.add(Achievement.totalKills8().getButton(achievements.contains(Achievement.totalKills8().getID())));
		buttons.add(Achievement.totalKills9().getButton(achievements.contains(Achievement.totalKills9().getID())));

		buttons.add(Achievement.amputeeAlone().getButton(achievements.contains(Achievement.amputeeAlone().getID())));
		buttons.add(Achievement.blindAlone().getButton(achievements.contains(Achievement.blindAlone().getID())));
		buttons.add(Achievement.clumsyAlone().getButton(achievements.contains(Achievement.clumsyAlone().getID())));
		buttons.add(Achievement.dwarfAlone().getButton(achievements.contains(Achievement.dwarfAlone().getID())));
		buttons.add(Achievement.explosiveAlone().getButton(achievements.contains(
				Achievement.explosiveAlone().getID())));
		buttons.add(Achievement.featherweightAlone().getButton(achievements.contains(
				Achievement.featherweightAlone().getID())));
		buttons.add(Achievement.nakedAlone().getButton(achievements.contains(Achievement.nakedAlone().getID())));
		buttons.add(Achievement.pacifistAlone().getButton(achievements.contains(
				Achievement.pacifistAlone().getID())));
		buttons.add(Achievement.uhcAlone().getButton(achievements.contains(Achievement.uhcAlone().getID())));

		buttons.add(Achievement.amputeeBalance().getButton(achievements.contains(
				Achievement.amputeeBalance().getID())));
		buttons.add(Achievement.blindBalance().getButton(achievements.contains(Achievement.blindBalance().getID())));
		buttons.add(Achievement.clumsyBalance().getButton(achievements.contains(
				Achievement.clumsyBalance().getID())));
		buttons.add(Achievement.dwarfBalance().getButton(achievements.contains(Achievement.dwarfBalance().getID())));
		buttons.add(Achievement.explosiveBalance().getButton(achievements.contains(
				Achievement.explosiveBalance().getID())));
		buttons.add(Achievement.featherweightBalance().getButton(achievements.contains(
				Achievement.featherweightBalance().getID())));
		buttons.add(Achievement.nakedBalance().getButton(achievements.contains(Achievement.nakedBalance().getID())));
		buttons.add(Achievement.pacifistBalance().getButton(achievements.contains(
				Achievement.pacifistBalance().getID())));
		buttons.add(Achievement.uhcBalance().getButton(achievements.contains(Achievement.uhcBalance().getID())));

		buttons.add(Achievement.amputeeKills().getButton(achievements.contains(Achievement.amputeeKills().getID())));
		buttons.add(Achievement.blindKills().getButton(achievements.contains(Achievement.blindKills().getID())));
		buttons.add(Achievement.clumsyKills().getButton(achievements.contains(Achievement.clumsyKills().getID())));
		buttons.add(Achievement.dwarfKills().getButton(achievements.contains(Achievement.dwarfKills().getID())));
		buttons.add(Achievement.explosiveKills().getButton(achievements.contains(
				Achievement.explosiveKills().getID())));
		buttons.add(Achievement.featherweightKills().getButton(achievements.contains(
				Achievement.featherweightKills().getID())));
		buttons.add(Achievement.nakedKills().getButton(achievements.contains(Achievement.nakedKills().getID())));
		buttons.add(Achievement.pacifistKills().getButton(achievements.contains(
				Achievement.pacifistKills().getID())));
		buttons.add(Achievement.uhcKills().getButton(achievements.contains(Achievement.uhcKills().getID())));

		buttons.add(Achievement.amputeeWave().getButton(achievements.contains(Achievement.amputeeWave().getID())));
		buttons.add(Achievement.blindWave().getButton(achievements.contains(Achievement.blindWave().getID())));
		buttons.add(Achievement.clumsyWave().getButton(achievements.contains(Achievement.clumsyWave().getID())));
		buttons.add(Achievement.dwarfWave().getButton(achievements.contains(Achievement.dwarfWave().getID())));
		buttons.add(Achievement.explosiveWave().getButton(achievements.contains(
				Achievement.explosiveWave().getID())));
		buttons.add(Achievement.featherweightWave().getButton(achievements.contains(
				Achievement.featherweightWave().getID())));
		buttons.add(Achievement.nakedWave().getButton(achievements.contains(Achievement.nakedWave().getID())));
		buttons.add(Achievement.pacifistWave().getButton(achievements.contains(
				Achievement.pacifistWave().getID())));
		buttons.add(Achievement.uhcWave().getButton(achievements.contains(Achievement.uhcWave().getID())));

		buttons.add(Achievement.alone().getButton(achievements.contains(Achievement.alone().getID())));
		buttons.add(Achievement.pacifistUhc().getButton(achievements.contains(Achievement.pacifistUhc().getID())));
		buttons.add(Achievement.allChallenges().getButton(achievements.contains(Achievement.allChallenges().getID())));
		buttons.add(Achievement.allGift().getButton(achievements.contains(Achievement.allGift().getID())));
		buttons.add(Achievement.allAbility().getButton(achievements.contains(Achievement.allAbility().getID())));
		buttons.add(Achievement.maxedAbility().getButton(achievements.contains(Achievement.maxedAbility().getID())));
		buttons.add(Achievement.allMaxedAbility().getButton(achievements.contains(Achievement.allMaxedAbility().getID())));
		buttons.add(Achievement.allEffect().getButton(achievements.contains(Achievement.allEffect().getID())));
		buttons.add(Achievement.allKits().getButton(achievements.contains(Achievement.allKits().getID())));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU, owner),
				CommunicationManager.format("&6&l" + owner.getName() + " " +
						LanguageManager.messages.achievements),
				true,
				false,
				"",
				buttons
		);
	}
	public static Inventory createPlayerAchievementsMenu(Player owner, int page) {
		List<String> achievements = Main.plugin.getPlayerData().getStringList(owner.getUniqueId() + ".achievements");
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement.topBalance1().getButton(achievements.contains(Achievement.topBalance1().getID())));
		buttons.add(Achievement.topBalance2().getButton(achievements.contains(Achievement.topBalance2().getID())));
		buttons.add(Achievement.topBalance3().getButton(achievements.contains(Achievement.topBalance3().getID())));
		buttons.add(Achievement.topBalance4().getButton(achievements.contains(Achievement.topBalance4().getID())));
		buttons.add(Achievement.topBalance5().getButton(achievements.contains(Achievement.topBalance5().getID())));
		buttons.add(Achievement.topBalance6().getButton(achievements.contains(Achievement.topBalance6().getID())));
		buttons.add(Achievement.topBalance7().getButton(achievements.contains(Achievement.topBalance7().getID())));
		buttons.add(Achievement.topBalance8().getButton(achievements.contains(Achievement.topBalance8().getID())));
		buttons.add(Achievement.topBalance9().getButton(achievements.contains(Achievement.topBalance9().getID())));

		buttons.add(Achievement.topKills1().getButton(achievements.contains(Achievement.topKills1().getID())));
		buttons.add(Achievement.topKills2().getButton(achievements.contains(Achievement.topKills2().getID())));
		buttons.add(Achievement.topKills3().getButton(achievements.contains(Achievement.topKills3().getID())));
		buttons.add(Achievement.topKills4().getButton(achievements.contains(Achievement.topKills4().getID())));
		buttons.add(Achievement.topKills5().getButton(achievements.contains(Achievement.topKills5().getID())));
		buttons.add(Achievement.topKills6().getButton(achievements.contains(Achievement.topKills6().getID())));
		buttons.add(Achievement.topKills7().getButton(achievements.contains(Achievement.topKills7().getID())));
		buttons.add(Achievement.topKills8().getButton(achievements.contains(Achievement.topKills8().getID())));
		buttons.add(Achievement.topKills9().getButton(achievements.contains(Achievement.topKills9().getID())));

		buttons.add(Achievement.topWave1().getButton(achievements.contains(Achievement.topWave1().getID())));
		buttons.add(Achievement.topWave2().getButton(achievements.contains(Achievement.topWave2().getID())));
		buttons.add(Achievement.topWave3().getButton(achievements.contains(Achievement.topWave3().getID())));
		buttons.add(Achievement.topWave4().getButton(achievements.contains(Achievement.topWave4().getID())));
		buttons.add(Achievement.topWave5().getButton(achievements.contains(Achievement.topWave5().getID())));
		buttons.add(Achievement.topWave6().getButton(achievements.contains(Achievement.topWave6().getID())));
		buttons.add(Achievement.topWave7().getButton(achievements.contains(Achievement.topWave7().getID())));
		buttons.add(Achievement.topWave8().getButton(achievements.contains(Achievement.topWave8().getID())));
		buttons.add(Achievement.topWave9().getButton(achievements.contains(Achievement.topWave9().getID())));

		buttons.add(Achievement.totalGems1().getButton(achievements.contains(Achievement.totalGems1().getID())));
		buttons.add(Achievement.totalGems2().getButton(achievements.contains(Achievement.totalGems2().getID())));
		buttons.add(Achievement.totalGems3().getButton(achievements.contains(Achievement.totalGems3().getID())));
		buttons.add(Achievement.totalGems4().getButton(achievements.contains(Achievement.totalGems4().getID())));
		buttons.add(Achievement.totalGems5().getButton(achievements.contains(Achievement.totalGems5().getID())));
		buttons.add(Achievement.totalGems6().getButton(achievements.contains(Achievement.totalGems6().getID())));
		buttons.add(Achievement.totalGems7().getButton(achievements.contains(Achievement.totalGems7().getID())));
		buttons.add(Achievement.totalGems8().getButton(achievements.contains(Achievement.totalGems8().getID())));
		buttons.add(Achievement.totalGems9().getButton(achievements.contains(Achievement.totalGems9().getID())));

		buttons.add(Achievement.totalKills1().getButton(achievements.contains(Achievement.totalKills1().getID())));
		buttons.add(Achievement.totalKills2().getButton(achievements.contains(Achievement.totalKills2().getID())));
		buttons.add(Achievement.totalKills3().getButton(achievements.contains(Achievement.totalKills3().getID())));
		buttons.add(Achievement.totalKills4().getButton(achievements.contains(Achievement.totalKills4().getID())));
		buttons.add(Achievement.totalKills5().getButton(achievements.contains(Achievement.totalKills5().getID())));
		buttons.add(Achievement.totalKills6().getButton(achievements.contains(Achievement.totalKills6().getID())));
		buttons.add(Achievement.totalKills7().getButton(achievements.contains(Achievement.totalKills7().getID())));
		buttons.add(Achievement.totalKills8().getButton(achievements.contains(Achievement.totalKills8().getID())));
		buttons.add(Achievement.totalKills9().getButton(achievements.contains(Achievement.totalKills9().getID())));

		buttons.add(Achievement.amputeeAlone().getButton(achievements.contains(Achievement.amputeeAlone().getID())));
		buttons.add(Achievement.blindAlone().getButton(achievements.contains(Achievement.blindAlone().getID())));
		buttons.add(Achievement.clumsyAlone().getButton(achievements.contains(Achievement.clumsyAlone().getID())));
		buttons.add(Achievement.dwarfAlone().getButton(achievements.contains(Achievement.dwarfAlone().getID())));
		buttons.add(Achievement.explosiveAlone().getButton(achievements.contains(
				Achievement.explosiveAlone().getID())));
		buttons.add(Achievement.featherweightAlone().getButton(achievements.contains(
				Achievement.featherweightAlone().getID())));
		buttons.add(Achievement.nakedAlone().getButton(achievements.contains(Achievement.nakedAlone().getID())));
		buttons.add(Achievement.pacifistAlone().getButton(achievements.contains(
				Achievement.pacifistAlone().getID())));
		buttons.add(Achievement.uhcAlone().getButton(achievements.contains(Achievement.uhcAlone().getID())));

		buttons.add(Achievement.amputeeBalance().getButton(achievements.contains(
				Achievement.amputeeBalance().getID())));
		buttons.add(Achievement.blindBalance().getButton(achievements.contains(Achievement.blindBalance().getID())));
		buttons.add(Achievement.clumsyBalance().getButton(achievements.contains(
				Achievement.clumsyBalance().getID())));
		buttons.add(Achievement.dwarfBalance().getButton(achievements.contains(Achievement.dwarfBalance().getID())));
		buttons.add(Achievement.explosiveBalance().getButton(achievements.contains(
				Achievement.explosiveBalance().getID())));
		buttons.add(Achievement.featherweightBalance().getButton(achievements.contains(
				Achievement.featherweightBalance().getID())));
		buttons.add(Achievement.nakedBalance().getButton(achievements.contains(Achievement.nakedBalance().getID())));
		buttons.add(Achievement.pacifistBalance().getButton(achievements.contains(
				Achievement.pacifistBalance().getID())));
		buttons.add(Achievement.uhcBalance().getButton(achievements.contains(Achievement.uhcBalance().getID())));

		buttons.add(Achievement.amputeeKills().getButton(achievements.contains(Achievement.amputeeKills().getID())));
		buttons.add(Achievement.blindKills().getButton(achievements.contains(Achievement.blindKills().getID())));
		buttons.add(Achievement.clumsyKills().getButton(achievements.contains(Achievement.clumsyKills().getID())));
		buttons.add(Achievement.dwarfKills().getButton(achievements.contains(Achievement.dwarfKills().getID())));
		buttons.add(Achievement.explosiveKills().getButton(achievements.contains(
				Achievement.explosiveKills().getID())));
		buttons.add(Achievement.featherweightKills().getButton(achievements.contains(
				Achievement.featherweightKills().getID())));
		buttons.add(Achievement.nakedKills().getButton(achievements.contains(Achievement.nakedKills().getID())));
		buttons.add(Achievement.pacifistKills().getButton(achievements.contains(
				Achievement.pacifistKills().getID())));
		buttons.add(Achievement.uhcKills().getButton(achievements.contains(Achievement.uhcKills().getID())));

		buttons.add(Achievement.amputeeWave().getButton(achievements.contains(Achievement.amputeeWave().getID())));
		buttons.add(Achievement.blindWave().getButton(achievements.contains(Achievement.blindWave().getID())));
		buttons.add(Achievement.clumsyWave().getButton(achievements.contains(Achievement.clumsyWave().getID())));
		buttons.add(Achievement.dwarfWave().getButton(achievements.contains(Achievement.dwarfWave().getID())));
		buttons.add(Achievement.explosiveWave().getButton(achievements.contains(
				Achievement.explosiveWave().getID())));
		buttons.add(Achievement.featherweightWave().getButton(achievements.contains(
				Achievement.featherweightWave().getID())));
		buttons.add(Achievement.nakedWave().getButton(achievements.contains(Achievement.nakedWave().getID())));
		buttons.add(Achievement.pacifistWave().getButton(achievements.contains(
				Achievement.pacifistWave().getID())));
		buttons.add(Achievement.uhcWave().getButton(achievements.contains(Achievement.uhcWave().getID())));

		buttons.add(Achievement.alone().getButton(achievements.contains(Achievement.alone().getID())));
		buttons.add(Achievement.pacifistUhc().getButton(achievements.contains(Achievement.pacifistUhc().getID())));
		buttons.add(Achievement.allChallenges().getButton(achievements.contains(Achievement.allChallenges().getID())));
		buttons.add(Achievement.allGift().getButton(achievements.contains(Achievement.allGift().getID())));
		buttons.add(Achievement.allAbility().getButton(achievements.contains(Achievement.allAbility().getID())));
		buttons.add(Achievement.maxedAbility().getButton(achievements.contains(Achievement.maxedAbility().getID())));
		buttons.add(Achievement.allMaxedAbility().getButton(achievements.contains(Achievement.allMaxedAbility().getID())));
		buttons.add(Achievement.allEffect().getButton(achievements.contains(Achievement.allEffect().getID())));
		buttons.add(Achievement.allKits().getButton(achievements.contains(Achievement.allKits().getID())));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
				new InventoryMeta(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU, page, owner),
				CommunicationManager.format("&6&l" + owner.getName() + " " +
						LanguageManager.messages.achievements),
				true,
				false,
				"",
				buttons
		);
	}

	// Display player stats reset confirmation
	public static Inventory createResetStatsConfirmMenu(Player player) {
		return InventoryFactory.createConfirmationMenu(
				InventoryID.RESET_STATS_CONFIRM_MENU,
				player,
				CommunicationManager.format("&4&l" + LanguageManager.messages.reset + "?")
		);
	}

	// Display crystal converter
	public static Inventory createCrystalConvertMenu(VDPlayer player) {
		FileConfiguration playerData = Main.plugin.getPlayerData();

		// Create inventory
		Inventory inv = Bukkit.createInventory(
				new InventoryMeta(InventoryID.CRYSTAL_CONVERT_MENU, InventoryType.MENU, player.getPlayer()),
				27,
				CommunicationManager.format("&9&l" + LanguageManager.names.crystalConverter)
		);

		// Display crystals to convert
		inv.setItem(3, ItemManager.createItem(Material.DIAMOND_BLOCK,
				CommunicationManager.format("&b&l" + LanguageManager.messages.crystalsToConvert + ": " +
						(player.getGemBoost() * 5))));

		// Display gems to receive
		inv.setItem(5, ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&a&l" + LanguageManager.messages.gemsToReceive + ": " +
						player.getGemBoost())));

		// Crystal balance display
		inv.setItem(8, ItemManager.createItem(Material.DIAMOND,
				CommunicationManager.format("&b&l" + LanguageManager.messages.crystalBalance +
						": &b" + playerData.getInt(player.getPlayer().getUniqueId() + ".crystalBalance"))));

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
				new InventoryMeta(InventoryID.SELECT_CHALLENGES_MENU, InventoryType.MENU, player.getPlayer(), arena),
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

		// Item dropping
		buttons.add( ItemManager.createItem(Material.EMERALD,
				CommunicationManager.format("&9&l" + LanguageManager.arenaStats.gemDrop.name +
						": " + getToggleStatus(arena.hasGemDrop())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.gemDrop.description, Utils.LORE_CHAR_LIMIT)));

		// Experience drop
		buttons.add( ItemManager.createItem(Material.EXPERIENCE_BOTTLE,
				CommunicationManager.format("&b&l" + LanguageManager.arenaStats.expDrop.name +
						": " + getToggleStatus(arena.hasExpDrop())),
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.arenaStats.expDrop.description, Utils.LORE_CHAR_LIMIT)));

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

		// Default shop
		buttons.add( ItemManager.createItem(Material.EMERALD_BLOCK,
				CommunicationManager.format("&6&l" + LanguageManager.names.defaultShop +
						": " + getToggleStatus(arena.hasNormal()))));

		// Custom shop
		buttons.add( ItemManager.createItem(Material.QUARTZ_BLOCK,
				CommunicationManager.format("&2&l" + LanguageManager.names.customShop +
						": " + getToggleStatus(arena.hasCustom()))));

		// Enchants shop
		buttons.add( ItemManager.createItem(Material.BOOKSHELF,
				CommunicationManager.format("&3&l" + LanguageManager.names.enchantShop +
						": " + getToggleStatus(arena.hasEnchants()))));

		// Community chest
		buttons.add( ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&d&l" + LanguageManager.names.communityChest +
						": " + getToggleStatus(arena.hasCommunity()))));

		// Custom shop inventory
		buttons.add( ItemManager.createItem(Material.QUARTZ,
				CommunicationManager.format("&f&l" + LanguageManager.messages.customShopInv)));

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
	private static ItemStack modifyPrice(ItemStack itemStack, double modifier) {
		ItemStack item = itemStack.clone();
		ItemMeta meta = item.getItemMeta();
		assert meta != null;
		List<String> lore = meta.getLore();
		assert lore != null;
		int price = (int) Math.round(Integer.parseInt(lore.get(lore.size() - 1).substring(10)) * modifier / 5) * 5;
		lore.set(lore.size() - 1, CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));
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
