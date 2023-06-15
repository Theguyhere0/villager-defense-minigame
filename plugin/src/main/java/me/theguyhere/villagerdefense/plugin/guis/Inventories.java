package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.Boots;
import me.theguyhere.villagerdefense.plugin.items.armor.Chestplate;
import me.theguyhere.villagerdefense.plugin.items.armor.Helmet;
import me.theguyhere.villagerdefense.plugin.items.armor.Leggings;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import me.theguyhere.villagerdefense.plugin.items.food.ShopFood;
import me.theguyhere.villagerdefense.plugin.items.weapons.*;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Inventories {
	// The main admin menu for the plugin
	public static Inventory createMainMenu() {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to set manage lobby
		buttons.add(new ItemStackBuilder(Material.BELL, CommunicationManager.format("&2&lLobby"))
			.setLores(CommunicationManager.format("&7Manage minigame lobby"))
			.build()
		);

		// Option to manage info boards
		buttons.add(new ItemStackBuilder(Material.OAK_SIGN, CommunicationManager.format("&6&lInfo Boards"))
			.setLores(CommunicationManager.format("&7Manage info boards"))
			.build()
		);

		// Option to manage leaderboards
		buttons.add(new ItemStackBuilder(Material.GOLDEN_HELMET, CommunicationManager.format("&e&lLeaderboards"))
			.setLores(CommunicationManager.format("&7Manage leaderboards"))
			.setButtonFlags()
			.build()
		);

		// Option to edit arenas
		buttons.add(new ItemStackBuilder(Material.NETHERITE_AXE, CommunicationManager.format("&9&lArenas"))
			.setLores(CommunicationManager.format("&7Manage arenas"))
			.setButtonFlags()
			.build()
		);

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.MAIN_MENU, InventoryType.MENU).build(),
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
		GameController
			.getArenas()
			.keySet()
			.stream()
			.sorted()
			.forEach(id -> buttons.add(new ItemStackBuilder(
				Material.EMERALD_BLOCK,
				CommunicationManager.format("&a&lEdit " + GameController
					.getArenas()
					.get(id)
					.getName())
			).build()));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ARENA_DASHBOARD, InventoryType.MENU)
				.setPage(page)
				.build(),
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
		DataManager
			.getConfigLocationMap("infoBoard")
			.forEach((id, location) ->
				buttons.add(new ItemStackBuilder(
					Material.BIRCH_SIGN,
					CommunicationManager.format("&6&lInfo Board " + id)
				).build()));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
			Integer.parseInt(Objects
				.requireNonNull(button.getItemMeta())
				.getDisplayName()
				.split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.INFO_BOARD_DASHBOARD, InventoryType.MENU)
				.setPage(page)
				.build(),
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
		buttons.add(new ItemStackBuilder(
			Material.DRAGON_HEAD,
			CommunicationManager.format("&4&lTotal Kills Leaderboard")
		).build());

		// Option to modify top kills leaderboard
		buttons.add(new ItemStackBuilder(
			Material.ZOMBIE_HEAD,
			CommunicationManager.format("&c&lTop Kills Leaderboard")
		).build());

		// Option to modify total gems leaderboard
		buttons.add(new ItemStackBuilder(
			Material.EMERALD_BLOCK,
			CommunicationManager.format("&2&lTotal Gems Leaderboard")
		).build());

		// Option to modify top balance leaderboard
		buttons.add(new ItemStackBuilder(
			Material.EMERALD,
			CommunicationManager.format("&a&lTop Balance Leaderboard")
		).build());

		// Option to modify top wave leaderboard
		buttons.add(new ItemStackBuilder(Material.GOLDEN_SWORD, CommunicationManager.format("&9&lTop Wave " +
			"Leaderboard"))
			.setButtonFlags()
			.build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.LEADERBOARD_DASHBOARD, InventoryType.MENU).build(),
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
		buttons.add(new ItemStackBuilder(
			Material.NAME_TAG,
			CommunicationManager.format("&6&lEdit Name")
		).build());

		// Option to edit game portal
		buttons.add(new ItemStackBuilder(
			Material.END_PORTAL_FRAME,
			CommunicationManager.format("&5&lArena Portal")
		).build());

		// Option to edit leaderboard
		buttons.add(new ItemStackBuilder(
			Material.TOTEM_OF_UNDYING,
			CommunicationManager.format("&a&lArena Leaderboard")
		).build());

		// Option to edit player settings
		buttons.add(new ItemStackBuilder(
			Material.PLAYER_HEAD,
			CommunicationManager.format("&d&lPlayer Settings")
		).build());

		// Option to edit mob settings
		buttons.add(new ItemStackBuilder(
			Material.ZOMBIE_SPAWN_EGG,
			CommunicationManager.format("&2&lMob Settings")
		).build());

		// Option to edit miscellaneous game settings
		buttons.add(new ItemStackBuilder(
			Material.REDSTONE,
			CommunicationManager.format("&7&lGame Settings")
		).build());

		// Option to close the arena
		String closed = arena.isClosed() ? "&c&lCLOSED" : "&a&lOPEN";
		buttons.add(new ItemStackBuilder(
			Material.NETHER_BRICK_FENCE,
			CommunicationManager.format("&9&lClose Arena: " + closed)
		).build());

		// Option to remove arena
		buttons.add(InventoryButtons.remove("ARENA"));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ARENA_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
			arena.getPortal() != null,
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
		buttons.add(new ItemStackBuilder(
			Material.END_PORTAL_FRAME,
			CommunicationManager.format("&5&lPlayer Spawn")
		).build());

		// Option to toggle player spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&d&lSpawn Particles: " +
				getToggleStatus(arena.hasSpawnParticles()))

		)
			.setLores(
				CommunicationManager.format("&7Particles showing where the spawn is"),
				CommunicationManager.format("&7(Visible in-game)")
			)
			.build());

		// Option to edit waiting room
		buttons.add(new ItemStackBuilder(Material.CLOCK, CommunicationManager.format("&b&lWaiting Room")

		)
			.setLores(
				CommunicationManager.format("&7An optional room to wait in before"),
				CommunicationManager.format("&7the game starts")
			)
			.build());

		// Option to edit max players
		buttons.add(new ItemStackBuilder(
			Material.NETHERITE_HELMET,
			CommunicationManager.format("&4&lMaximum Players: " + arena.getMaxPlayers())

		)
			.setLores(CommunicationManager.format("&7Maximum players the game will have"))
			.setButtonFlags()
			.build());

		// Option to edit min players
		buttons.add(new ItemStackBuilder(
			Material.NETHERITE_BOOTS,
			CommunicationManager.format("&2&lMinimum Players: " + arena.getMinPlayers())

		)
			.setLores(CommunicationManager.format("&7Minimum players needed for game to start"))
			.setButtonFlags()
			.build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PLAYERS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
		buttons.add(new ItemStackBuilder(
			Material.END_PORTAL_FRAME,
			CommunicationManager.format("&2&lMonster Spawns")
		).build());

		// Option to toggle monster spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&a&lMonster Spawn Particles: " +
				getToggleStatus(arena.hasMonsterParticles()))

		)
			.setLores(
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")
			)
			.build());

		// Option to edit villager spawns
		buttons.add(new ItemStackBuilder(
			Material.END_PORTAL_FRAME,
			CommunicationManager.format("&5&lVillager Spawns")
		).build());

		// Option to toggle villager spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&d&lVillager Spawn Particles: " +
				getToggleStatus(arena.hasVillagerParticles()))

		)
			.setLores(
				CommunicationManager.format("&7Particles showing where the spawns are"),
				CommunicationManager.format("&7(Visible in-game)")
			)
			.build());

		// Option to edit villager type
		buttons.add(new ItemStackBuilder(
			Material.LECTERN,
			CommunicationManager.format("&6&lVillager Type: " +
				arena
					.getVillagerType()
					.substring(0, 1)
					.toUpperCase() +
				arena
					.getVillagerType()
					.substring(1))
		).build());

		// Option to edit spawn table
		buttons.add(new ItemStackBuilder(
			Material.DRAGON_HEAD,
			CommunicationManager.format("&3&lSpawn Table: " + arena.getSpawnTableFile())
		).build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.MOBS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
		DataManager
			.getConfigLocationMap(arena.getPath() + ".monster")
			.forEach((id, location) ->
				buttons.add(new ItemStackBuilder(
					Material.ZOMBIE_HEAD,
					CommunicationManager.format("&2&lMob Spawn " + id)
				).build()));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
			Integer.parseInt(Objects
				.requireNonNull(button.getItemMeta())
				.getDisplayName()
				.split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.MONSTER_SPAWN_DASHBOARD, InventoryType.MENU)
				.setArena(arena)
				.setPage(page)
				.build(),
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
			buttons.add(InventoryButtons.relocate("Spawn"));
		else buttons.add(InventoryButtons.create("Spawn"));

		// Option to teleport to monster spawn
		buttons.add(InventoryButtons.teleport("Spawn"));

		// Option to center the monster spawn
		buttons.add(InventoryButtons.center("Spawn"));

		// Option to remove monster spawn
		buttons.add(InventoryButtons.remove("SPAWN"));

		// Toggle to set monster spawn type
		switch (arena.getMonsterSpawnType(monsterSpawnID)) {
			case 1:
				buttons.add(new ItemStackBuilder(
					Material.GUNPOWDER,
					CommunicationManager.format("&5&lType: Ground")
				).build());
				break;
			case 2:
				buttons.add(new ItemStackBuilder(
					Material.FEATHER,
					CommunicationManager.format("&5&lType: Flying")
				).build());
				break;
			default:
				buttons.add(new ItemStackBuilder(
					Material.BONE,
					CommunicationManager.format("&5&lType: All")
				).build());
		}

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.MONSTER_SPAWN_MENU, InventoryType.MENU)
				.setArena(arena)
				.setID(monsterSpawnID)
				.build(),
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
		DataManager
			.getConfigLocationMap(arena.getPath() + ".villager")
			.forEach((id, location) ->
				buttons.add(new ItemStackBuilder(
					Material.POPPY,
					CommunicationManager.format("&5&lVillager Spawn " + id)
				).build()));

		// Sort buttons
		buttons.sort(Comparator.comparing(button ->
			Integer.parseInt(Objects
				.requireNonNull(button.getItemMeta())
				.getDisplayName()
				.split(" ")[2])));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.VILLAGER_SPAWN_DASHBOARD, InventoryType.MENU)
				.setPage(page)
				.setArena(arena)
				.build(),
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
		buttons.add(new ItemStackBuilder(Material.SANDSTONE, CommunicationManager.format("&6&lDesert")).build());
		buttons.add(new ItemStackBuilder(Material.MOSSY_COBBLESTONE, CommunicationManager.format("&2&lJungle")).build());
		buttons.add(new ItemStackBuilder(Material.GRASS_BLOCK, CommunicationManager.format("&a&lPlains")).build());
		buttons.add(new ItemStackBuilder(Material.TERRACOTTA, CommunicationManager.format("&c&lSavanna")).build());
		buttons.add(new ItemStackBuilder(Material.SNOW_BLOCK, CommunicationManager.format("&b&lSnow")).build());
		buttons.add(new ItemStackBuilder(Material.CLAY, CommunicationManager.format("&3&lSwamp")).build());
		buttons.add(new ItemStackBuilder(Material.PODZOL, CommunicationManager.format("&9&lTaiga")).build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.VILLAGER_TYPE_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			CommunicationManager.format("&6&lVillager Type: " +
				arena
					.getVillagerType()
					.substring(0, 1)
					.toUpperCase() + arena
				.getVillagerType()
				.substring(1)),
			true,
			buttons
		);
	}

	// Spawn table menu for an arena
	public static Inventory createSpawnTableMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();
		String chosen = arena.getSpawnTableFile();

		// Option to set spawn table to default
		buttons.add(new ItemStackBuilder(
			Material.OAK_WOOD,
			CommunicationManager.format("&4&lDefault")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to default.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("default"))
			.build());

		// Option to set spawn table to global option 1
		buttons.add(new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&6&lOption 1")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option1.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option1"))
			.build());

		// Option to set spawn table to global option 2
		buttons.add(new ItemStackBuilder(
			Material.ORANGE_CONCRETE,
			CommunicationManager.format("&6&lOption 2")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option2.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option2"))
			.build());

		// Option to set spawn table to global option 3
		buttons.add(new ItemStackBuilder(
			Material.YELLOW_CONCRETE,
			CommunicationManager.format("&6&lOption 3")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option3.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option3"))
			.build());

		// Option to set spawn table to global option 4
		buttons.add(new ItemStackBuilder(
			Material.BROWN_CONCRETE,
			CommunicationManager.format("&6&lOption 4")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option4.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option4"))
			.build());

		// Option to set spawn table to global option 5
		buttons.add(new ItemStackBuilder(
			Material.LIGHT_GRAY_CONCRETE,
			CommunicationManager.format("&6&lOption 5")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option5.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option5"))
			.build());

		// Option to set spawn table to global option 6
		buttons.add(new ItemStackBuilder(
			Material.WHITE_CONCRETE,
			CommunicationManager.format("&6&lOption 6")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to option6.yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.contains("option6"))
			.build());

		// Option to set spawn table to custom option
		buttons.add(new ItemStackBuilder(
			Material.BIRCH_WOOD,
			CommunicationManager.format("&e&lCustom")
		)
			.setLores(CommunicationManager.format("&7Sets spawn table to a" + arena.getId() + ".yml"))
			.setButtonFlags()
			.setGlowingIfTrue(chosen.charAt(0) == 'a')
			.build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SPAWN_TABLE_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			CommunicationManager.format("&3&lSpawn Table: " + arena.getSpawnTableFile()),
			1,
			true,
			buttons
		);
	}

	// Menu for editing the game settings of an arena
	public static Inventory createGameSettingsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to change max waves
		buttons.add(
			new ItemStackBuilder(
				Material.NETHERITE_SWORD,
				CommunicationManager.format("&3&lMax Waves: " + ((arena.getMaxWaves() < 0) ? "Unlimited" :
					arena.getMaxWaves()))
			)
				.setButtonFlags()
				.build()
		);

		// Option to wave time limit
		buttons.add(new ItemStackBuilder(Material.CLOCK, CommunicationManager.format("&2&lWave Time Limit: " +
			((arena.getWaveTimeLimit() < 0) ? "Unlimited" : arena.getWaveTimeLimit() + " minute(s)"))).build());

		// Option to toggle dynamic wave time limit
		buttons.add(
			new ItemStackBuilder(
				Material.SNOWBALL,
				CommunicationManager.format("&a&lDynamic Time Limit: " + getToggleStatus(arena.hasDynamicLimit()))
			)
				.setLores(
					CommunicationManager.format("&7Wave time limit adjusting based on"),
					CommunicationManager.format("&7in-game difficulty")
				)
				.build()

		);

		// Option to toggle community chest
		buttons.add(new ItemStackBuilder(
			Material.CHEST,
			CommunicationManager.format("&d&lCommunity Chest: " + getToggleStatus(arena.hasCommunity()))
		)
			.setLores(
				CommunicationManager.format("&7Turn community chest on and off")
			)
			.build());

		// Option to edit allowed kits
		buttons.add(new ItemStackBuilder(
			Material.ENDER_CHEST,
			CommunicationManager.format("&9&lAllowed Kits")
		).build());

		// Option to edit forced challenges
		buttons.add(new ItemStackBuilder(
			Material.NETHER_STAR,
			CommunicationManager.format("&9&lForced Challenges")
		).build());

		// Option to edit difficulty label
		buttons.add(new ItemStackBuilder(
			Material.NAME_TAG,
			CommunicationManager.format("&6&lDifficulty Label: " + arena.getDifficultyLabel())
		).build());

		// Option to adjust overall difficulty multiplier
		buttons.add(new ItemStackBuilder(
				Material.TURTLE_HELMET,
				CommunicationManager.format("&4&lDifficulty Multiplier: " + arena.getDifficultyMultiplier())
			)
				.setLores(CommunicationManager.format("&7Determines difficulty increase rate"))
				.setButtonFlags()
				.build()
		);

		// Option to toggle late arrival
		buttons.add(new ItemStackBuilder(
			Material.DAYLIGHT_DETECTOR,
			CommunicationManager.format("&e&lLate Arrival: " +
				getToggleStatus(arena.hasLateArrival()))
		)
			.setLores(
				CommunicationManager.format("&7Allows players to join after"),
				CommunicationManager.format("&7the game has started")
			)
			.build());

		// Option to set arena bounds
		buttons.add(new ItemStackBuilder(Material.BEDROCK, CommunicationManager.format("&4&lArena Bounds"))
			.setLores(
				CommunicationManager.format("&7Bounds determine where players are"),
				CommunicationManager.format("&7allowed to go and where the game will"),
				CommunicationManager.format("&7function. Avoid building past arena bounds.")
			)
			.build());

		// Option to edit sounds
		buttons.add(
			new ItemStackBuilder(
				Material.MUSIC_DISC_13,
				CommunicationManager.format("&d&lSounds")
			)
				.setButtonFlags()
				.build()
		);

		// Option to copy game settings from another arena or a preset
		buttons.add(new ItemStackBuilder(
			Material.WRITABLE_BOOK,
			CommunicationManager.format("&f&lCopy Game Settings")
		)
			.setLores(
				CommunicationManager.format("&7Copy settings of another arena or"),
				CommunicationManager.format("&7choose from a menu of presets")
			)
			.build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.GAME_SETTINGS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ALLOWED_KITS_DISPLAY_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			54,
			CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits)
		) : Bukkit.createInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ALLOWED_KITS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			54,
			CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits + ": " + arena.getName())
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.LIME_STAINED_GLASS_PANE,
				CommunicationManager.format("&a&l" + LanguageManager.names.giftKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.giftKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		List<String> bannedKitIDs = arena.getBannedKitIDs();
		if (bannedKitIDs.contains(Kit
			.orc()
			.getID()))
			inv.setItem(9, Kit
				.orc()
				.getButton(-1, false));
		else inv.setItem(9, Kit
			.orc()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.farmer()
			.getID()))
			inv.setItem(10, Kit
				.farmer()
				.getButton(-1, false));
		else inv.setItem(10, Kit
			.farmer()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.soldier()
			.getID()))
			inv.setItem(11, Kit
				.soldier()
				.getButton(-1, false));
		else inv.setItem(11, Kit
			.soldier()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.alchemist()
			.getID()))
			inv.setItem(12, Kit
				.alchemist()
				.getButton(-1, false));
		else inv.setItem(12, Kit
			.alchemist()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.tailor()
			.getID()))
			inv.setItem(13, Kit
				.tailor()
				.getButton(-1, false));
		else inv.setItem(13, Kit
			.tailor()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.trader()
			.getID()))
			inv.setItem(14, Kit
				.trader()
				.getButton(-1, false));
		else inv.setItem(14, Kit
			.trader()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.summoner()
			.getID()))
			inv.setItem(15, Kit
				.summoner()
				.getButton(-1, false));
		else inv.setItem(15, Kit
			.summoner()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.reaper()
			.getID()))
			inv.setItem(16, Kit
				.reaper()
				.getButton(-1, false));
		else inv.setItem(16, Kit
			.reaper()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.phantom()
			.getID()))
			inv.setItem(17, Kit
				.phantom()
				.getButton(-1, false));
		else inv.setItem(17, Kit
			.phantom()
			.getButton(-1, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.MAGENTA_STAINED_GLASS_PANE,
				CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.abilityKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		if (bannedKitIDs.contains(Kit
			.mage()
			.getID()))
			inv.setItem(27, Kit
				.mage()
				.getButton(-1, false));
		else inv.setItem(27, Kit
			.mage()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.ninja()
			.getID()))
			inv.setItem(28, Kit
				.ninja()
				.getButton(-1, false));
		else inv.setItem(28, Kit
			.ninja()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.templar()
			.getID()))
			inv.setItem(29, Kit
				.templar()
				.getButton(-1, false));
		else inv.setItem(29, Kit
			.templar()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.warrior()
			.getID()))
			inv.setItem(30, Kit
				.warrior()
				.getButton(-1, false));
		else inv.setItem(30, Kit
			.warrior()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.knight()
			.getID()))
			inv.setItem(31, Kit
				.knight()
				.getButton(-1, false));
		else inv.setItem(31, Kit
			.knight()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.priest()
			.getID()))
			inv.setItem(32, Kit
				.priest()
				.getButton(-1, false));
		else inv.setItem(32, Kit
			.priest()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.siren()
			.getID()))
			inv.setItem(33, Kit
				.siren()
				.getButton(-1, false));
		else inv.setItem(33, Kit
			.siren()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.monk()
			.getID()))
			inv.setItem(34, Kit
				.monk()
				.getButton(-1, false));
		else inv.setItem(34, Kit
			.monk()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.messenger()
			.getID()))
			inv.setItem(35, Kit
				.messenger()
				.getButton(-1, false));
		else inv.setItem(35, Kit
			.messenger()
			.getButton(-1, true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.YELLOW_STAINED_GLASS_PANE,
				CommunicationManager.format("&e&l" + LanguageManager.names.effectKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.effectKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		if (bannedKitIDs.contains(Kit
			.blacksmith()
			.getID()))
			inv.setItem(45, Kit
				.blacksmith()
				.getButton(-1, false));
		else inv.setItem(45, Kit
			.blacksmith()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.witch()
			.getID()))
			inv.setItem(46, Kit
				.witch()
				.getButton(-1, false));
		else inv.setItem(46, Kit
			.witch()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.merchant()
			.getID()))
			inv.setItem(47, Kit
				.merchant()
				.getButton(-1, false));
		else inv.setItem(47, Kit
			.merchant()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.vampire()
			.getID()))
			inv.setItem(48, Kit
				.vampire()
				.getButton(-1, false));
		else inv.setItem(48, Kit
			.vampire()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.giant()
			.getID()))
			inv.setItem(49, Kit
				.giant()
				.getButton(-1, false));
		else inv.setItem(49, Kit
			.giant()
			.getButton(-1, true));
		if (bannedKitIDs.contains(Kit
			.trainer()
			.getID()))
			inv.setItem(50, Kit
				.trainer()
				.getButton(-1, false));
		else inv.setItem(50, Kit
			.trainer()
			.getButton(-1, true));

		// Option to exit
		inv.setItem(53, InventoryButtons.exit());

		return inv;
	}

	// Menu for forced challenges of an arena
	public static Inventory createForcedChallengesMenu(Arena arena, boolean display) {
		List<ItemStack> buttons = new ArrayList<>();
		List<String> forced = arena.getForcedChallengeIDs();

		// Set buttons
		buttons.add(Challenge
			.amputee()
			.getButton(forced.contains(Challenge
				.amputee()
				.getID())));
		buttons.add(Challenge
			.clumsy()
			.getButton(forced.contains(Challenge
				.clumsy()
				.getID())));
		buttons.add(Challenge
			.featherweight()
			.getButton(forced.contains(Challenge
				.featherweight()
				.getID())));
		buttons.add(Challenge
			.pacifist()
			.getButton(forced.contains(Challenge
				.pacifist()
				.getID())));
		buttons.add(Challenge
			.dwarf()
			.getButton(forced.contains(Challenge
				.dwarf()
				.getID())));
		buttons.add(Challenge
			.uhc()
			.getButton(forced.contains(Challenge
				.uhc()
				.getID())));
		buttons.add(Challenge
			.explosive()
			.getButton(forced.contains(Challenge
				.explosive()
				.getID())));
		buttons.add(Challenge
			.naked()
			.getButton(forced.contains(Challenge
				.naked()
				.getID())));
		buttons.add(Challenge
			.blind()
			.getButton(forced.contains(Challenge
				.blind()
				.getID())));

		return display ? InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.FORCED_CHALLENGES_DISPLAY_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			CommunicationManager.format("&9&l" + LanguageManager.messages.forcedChallenges),
			true,
			buttons
		) : InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.FORCED_CHALLENGES_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
		buttons.add(new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&l" + LanguageManager.names.easy)
		).build());

		// "Medium" option
		buttons.add(new ItemStackBuilder(
			Material.YELLOW_CONCRETE,
			CommunicationManager.format("&e&l" + LanguageManager.names.medium)
		).build());

		// "Hard" option
		buttons.add(new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&l" + LanguageManager.names.hard)
		).build());

		// "Insane" option
		buttons.add(new ItemStackBuilder(
			Material.MAGENTA_CONCRETE,
			CommunicationManager.format("&d&l" + LanguageManager.names.insane)
		).build());

		// "None" option
		buttons.add(new ItemStackBuilder(
			Material.LIGHT_GRAY_CONCRETE,
			CommunicationManager.format("&7&l" + LanguageManager.names.none)
		).build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.DIFFICULTY_LABEL_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			CommunicationManager.format("&6&lDifficulty Label: " + label),
			true,
			buttons
		);
	}

	// Menu for changing the difficulty multiplier of an arena
	public static Inventory createDifficultyMultiplierMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// "1" option
		buttons.add(new ItemStackBuilder(
			Material.LIGHT_BLUE_CONCRETE,
			CommunicationManager.format("&b&l1")
		).build());

		// "2" option
		buttons.add(new ItemStackBuilder(Material.LIME_CONCRETE, CommunicationManager.format("&a&l2")).build());

		// "3" option
		buttons.add(new ItemStackBuilder(Material.YELLOW_CONCRETE, CommunicationManager.format("&6&l3")).build());

		// "4" option
		buttons.add(new ItemStackBuilder(Material.RED_CONCRETE, CommunicationManager.format("&4&l4")).build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.DIFFICULTY_MULTIPLIER_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
		buttons.add(new ItemStackBuilder(Material.TORCH, CommunicationManager.format("&b&lCorner 1: " +
			(arena.getCorner1() == null ? "&c&lMissing" : "&a&lSet"))).build());

		// Option to interact with corner 2
		buttons.add(new ItemStackBuilder(
			Material.SOUL_TORCH,
			CommunicationManager.format("&9&lCorner 2: " +
				(arena.getCorner2() == null ? "&c&lMissing" : "&a&lSet"))
		).build());

		// Option to stretch the arena bounds to the top and bottom of the world
		buttons.add(new ItemStackBuilder(
			Material.WEEPING_VINES,
			CommunicationManager.format("&3&lStretch Bounds")
		)
			.setLores(
				CommunicationManager.format("&7Stretches the arena bounds in the y direction from"),
				CommunicationManager.format("&7the top to just below the bottom of the world.")
			)
			.build());

		// Option to toggle arena border particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&4&lBorder Particles: " +
				getToggleStatus(arena.hasBorderParticles()))
		).build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ARENA_BOUNDS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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

	// Menu for editing the sounds of an arena
	public static Inventory createSoundsMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Option to edit win sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_PIGSTEP,
			CommunicationManager.format("&a&lWin Sound: " + getToggleStatus(arena.hasWinSound()))
		)
			.setLores(CommunicationManager.format("&7Played when game ends and players win"))
			.setButtonFlags()
			.build());

		// Option to edit lose sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_11,
			CommunicationManager.format("&e&lLose Sound: " + getToggleStatus(arena.hasLoseSound()))
		)
			.setLores(CommunicationManager.format("&7Played when game ends and players lose"))
			.setButtonFlags()
			.build());

		// Option to edit wave start sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_CAT,
			CommunicationManager.format("&2&lWave Start Sound: " +
				getToggleStatus(arena.hasWaveStartSound()))
		)
			.setLores(CommunicationManager.format("&7Played when a wave starts"))
			.setButtonFlags()
			.build());

		// Option to edit wave finish sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_BLOCKS,
			CommunicationManager.format("&9&lWave Finish Sound: " +
				getToggleStatus(arena.hasWaveFinishSound()))
		)
			.setLores(CommunicationManager.format("&7Played when a wave ends"))
			.setButtonFlags()
			.build());

		// Option to edit waiting music
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_MELLOHI,
			CommunicationManager.format("&6&lWaiting Sound: &b&l" + arena.getWaitingSoundName())
		)
			.setLores(
				CommunicationManager.format("&7Played while players wait"),
				CommunicationManager.format("&7for the game to start")
			)
			.setButtonFlags()
			.build());

		// Option to edit player death sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_CHIRP,
			CommunicationManager.format("&4&lPlayer Death Sound: " +
				getToggleStatus(arena.hasPlayerDeathSound()))
		)
			.setLores(CommunicationManager.format("&7Played when a player dies"))
			.setButtonFlags()
			.build());

		// Option to edit ability sound
		buttons.add(new ItemStackBuilder(
			Material.MUSIC_DISC_MALL,
			CommunicationManager.format("&d&lAbility Sound: " +
				getToggleStatus(arena.hasAbilitySound()))
		)
			.setLores(CommunicationManager.format("&7Played when a player uses their ability"))
			.setButtonFlags()
			.build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SOUNDS_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
			new InventoryMeta.InventoryMetaBuilder(InventoryID.WAITING_SOUND_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
		Objects
			.requireNonNull(Main
				.getArenaData()
				.getConfigurationSection("arena"))
			.getKeys(false)
			.forEach(id -> {
				if (Integer.parseInt(id) != arena.getId())
					try {
						buttons.add(
							new ItemStackBuilder(
								Material.GRAY_GLAZED_TERRACOTTA,
								CommunicationManager.format("&a&lCopy " +
									GameController
										.getArena(Integer.parseInt(id))
										.getName())
							).build()
						);
					}
					catch (ArenaNotFoundException ignored) {
					}
			});

		// Easy preset
		frozenButtons.add(new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&lEasy Preset")
		).build());

		// Medium preset
		frozenButtons.add(new ItemStackBuilder(
			Material.YELLOW_CONCRETE,
			CommunicationManager.format("&e&lMedium Preset")
		).build());

		// Hard preset
		frozenButtons.add(new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&lHard Preset")
		).build());

		// Insane preset
		frozenButtons.add(new ItemStackBuilder(
			Material.MAGENTA_CONCRETE,
			CommunicationManager.format("&d&lInsane Preset")
		).build());

		return InventoryFactory.createDynamicSizeBottomNavFreezeRowInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.COPY_SETTINGS_MENU, InventoryType.MENU)
				.setArena(arena)
				.setPage(page)
				.build(),
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
	public static Inventory createShopMenu(Arena arena, VDPlayer player) {
		List<ItemStack> buttons = new ArrayList<>();

		String disabled = " &4&l[" + LanguageManager.messages.disabled + "]";

		// Create inventory
		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_SWORD,
			CommunicationManager.format("&2&l" + LanguageManager.names.swordShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_AXE,
			CommunicationManager.format("&2&l" + LanguageManager.names.axeShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_HOE,
			CommunicationManager.format("&2&l" + LanguageManager.names.scytheShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.BOW,
			CommunicationManager.format("&2&l" + LanguageManager.names.bowShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.CROSSBOW,
			CommunicationManager.format("&2&l" + LanguageManager.names.crossbowShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.NETHER_STAR,
			CommunicationManager.format("&2&l" + LanguageManager.names.ammoShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_HELMET,
			CommunicationManager.format("&2&l" + LanguageManager.names.helmetShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_CHESTPLATE,
			CommunicationManager.format("&2&l" + LanguageManager.names.chestplateShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_LEGGINGS,
			CommunicationManager.format("&2&l" + LanguageManager.names.leggingsShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_BOOTS,
			CommunicationManager.format("&2&l" + LanguageManager.names.bootsShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_APPLE,
			CommunicationManager.format("&2&l" + LanguageManager.names.consumableShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.LEAD,
			CommunicationManager.format("&2&l" + LanguageManager.names.petShop,
				Integer.toString(player.getRemainingPetSlots()), Integer.toString(player.getPetSlots())
			)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.CARVED_PUMPKIN,
			CommunicationManager.format("&b&l" + LanguageManager.names.golemShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.ANVIL,
			CommunicationManager.format("&b&l" + LanguageManager.names.abilityUpgradeShop)
		)
			.setButtonFlags()
			.setGlowingIfTrue(true)
			.build());

		buttons.add(new ItemStackBuilder(
			Material.CHEST,
			CommunicationManager.format("&d&l" + LanguageManager.names.communityChest +
				(arena.hasCommunity() ? "" : disabled))
		).build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.itemShop),
			false,
			buttons
		);
	}

	// Generate the sword shop
	public static Inventory createSwordShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T1, Sword.SwordType.TIERED)));
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T2, Sword.SwordType.TIERED)));
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T3, Sword.SwordType.TIERED)));
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T4, Sword.SwordType.TIERED)));
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T5, Sword.SwordType.TIERED)));
		buttons.add(arena.modifyPrice(Sword.create(VDItem.Tier.T6, Sword.SwordType.TIERED)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SWORD_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.swordShop),
			true,
			buttons
		);
	}

	// Generate the axe shop
	public static Inventory createAxeShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Axe.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.AXE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.axeShop),
			true,
			buttons
		);
	}

	// Generate the scythe shop
	public static Inventory createScytheShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T1, Scythe.ScytheType.TIERED)));
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T2, Scythe.ScytheType.TIERED)));
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T3, Scythe.ScytheType.TIERED)));
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T4, Scythe.ScytheType.TIERED)));
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T5, Scythe.ScytheType.TIERED)));
		buttons.add(arena.modifyPrice(Scythe.create(VDItem.Tier.T6, Scythe.ScytheType.TIERED)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SCYTHE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.scytheShop),
			true,
			buttons
		);
	}

	// Generate the bow shop
	public static Inventory createBowShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Bow.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.BOW_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.bowShop),
			true,
			buttons
		);
	}

	// Generate the crossbow shop
	public static Inventory createCrossbowShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Crossbow.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.CROSSBOW_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.crossbowShop),
			true,
			buttons
		);
	}

	// Generate the ammo shop
	public static Inventory createAmmoUpgradeShopMenu(Arena arena, VDPlayer player) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		switch (player.getTieredAmmoLevel() + 1) {
			case 1:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T1)));
				break;
			case 2:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T2)));
				break;
			case 3:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T3)));
				break;
			case 4:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T4)));
				break;
			case 5:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T5)));
				break;
			case 6:
				buttons.add(arena.modifyPrice(Ammo.create(VDItem.Tier.T6)));
				break;
			default:
				buttons.add(InventoryButtons.noUpgrade());
		}

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.AMMO_UPGRADE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.ammoShop),
			true,
			buttons
		);
	}

	// Generate the helmet shop
	public static Inventory createHelmetShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Helmet.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.HELMET_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.helmetShop),
			true,
			buttons
		);
	}

	// Generate the chestplate shop
	public static Inventory createChestplateShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Chestplate.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.CHESTPLATE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.chestplateShop),
			true,
			buttons
		);
	}

	// Generate the leggings shop
	public static Inventory createLeggingsShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Leggings.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.LEGGINGS_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.leggingsShop),
			true,
			buttons
		);
	}

	// Generate the boots shop
	public static Inventory createBootsShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T1)));
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T2)));
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T3)));
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T4)));
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T5)));
		buttons.add(arena.modifyPrice(Boots.create(VDItem.Tier.T6)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.BOOTS_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.bootsShop),
			true,
			buttons
		);
	}

	// Generate the consumable shop
	public static Inventory createConsumableShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.T1, ShopFood.ShopFoodType.TIERED)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.T2, ShopFood.ShopFoodType.TIERED)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.T3, ShopFood.ShopFoodType.TIERED)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.T4, ShopFood.ShopFoodType.TIERED)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.UNIQUE, ShopFood.ShopFoodType.CAPPLE)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.UNIQUE, ShopFood.ShopFoodType.GAPPLE)));
		buttons.add(arena.modifyPrice(ShopFood.create(VDItem.Tier.UNIQUE, ShopFood.ShopFoodType.TOTEM)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.CONSUMABLE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.consumableShop),
			true,
			buttons
		);
	}

	// Generate the pet shop
	public static Inventory createPetShopMenu(Arena arena, VDPlayer player) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// List out existing pets
		player
			.getPets()
			.forEach(pet -> buttons.add(pet.createDisplayButton()));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PET_SHOP_MENU, InventoryType.MENU)
				.setArena(arena)
				.setID((9 - player
					.getPets()
					.size()) / 2)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.petShop,
				Integer.toString(player.getRemainingPetSlots()), Integer.toString(player.getPetSlots())
			),
			true,
			true,
			LanguageManager.names.pet,
			buttons
		);
	}

	// Generate the new pet menu
	public static Inventory createNewPetMenu(Arena arena, VDPlayer player) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// List available pet types to purchase
		if (player.getRemainingPetSlots() > 0)
			buttons.add(arena.modifyPrice(VDEgg.create(1, VDEgg.EggType.DOG)));
		if (player.getRemainingPetSlots() > 1)
			buttons.add(arena.modifyPrice(VDEgg.create(1, VDEgg.EggType.CAT)));
		if (player.getRemainingPetSlots() > 2)
			buttons.add(arena.modifyPrice(VDEgg.create(1, VDEgg.EggType.HORSE)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.NEW_PET_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&a&l" + LanguageManager.names.newPet),
			true,
			buttons
		);
	}

	// Generate the existing pet menu
	public static Inventory createPetManagerMenu(Arena arena, VDPlayer player, int petIndex) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// Upgrade
		buttons.add(arena.modifyPrice(player
			.getPets()
			.get(petIndex)
			.createUpgradeButton()));

		// Remove
		buttons.add(new ItemStackBuilder(
			Material.LAVA_BUCKET,
			CommunicationManager.format("&4&l" + LanguageManager.messages.removePet)
		).build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PET_MANAGER_MENU, InventoryType.MENU)
				.setArena(arena)
				.setID(petIndex)
				.build(),
			player
				.getPets()
				.get(petIndex)
				.getName(),
			1,
			true,
			buttons
		);
	}

	// Display pet removal confirmation
	public static Inventory createPetConfirmMenu(Arena arena, UUID playerID, int petIndex) {
		return InventoryFactory.createConfirmationMenu(
			InventoryID.PET_CONFIRM_MENU,
			playerID,
			arena,
			petIndex,
			CommunicationManager.format("&4&l" + LanguageManager.messages.removePet + "?")
		);
	}

	// Generate the golem shop
	public static Inventory createGolemShopMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// List out golems
		for (int i = 0; i < arena
			.getGolems()
			.size(); i++)
			buttons.add(arena
				.getGolems()
				.get(i)
				.createDisplayButton());

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.GOLEM_SHOP_MENU, InventoryType.MENU)
				.setArena(arena)
				.setID((9 - arena
					.getGolems()
					.size()) / 2)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.golemShop),
			true,
			true,
			LanguageManager.names.golem,
			buttons
		);
	}

	// Generate the new golem menu
	public static Inventory createNewGolemMenu(Arena arena) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// List available pet types to purchase
		buttons.add(arena.modifyPrice(VDEgg.create(1, VDEgg.EggType.IRON_GOLEM)));
		buttons.add(arena.modifyPrice(VDEgg.create(1, VDEgg.EggType.SNOW_GOLEM)));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.NEW_GOLEM_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
			CommunicationManager.format("&a&l" + LanguageManager.names.newGolem),
			true,
			buttons
		);
	}

	// Generate the golem manager menu
	public static Inventory createGolemManagerMenu(Arena arena, int golemIndex) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();

		// Upgrade
		buttons.add(arena.modifyPrice(arena
			.getGolems()
			.get(golemIndex)
			.createUpgradeButton()));

		// Remove
		buttons.add(new ItemStackBuilder(
			Material.LAVA_BUCKET,
			CommunicationManager.format("&4&l" + LanguageManager.messages.removeGolem)
		).build());

		return InventoryFactory.createFixedSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.GOLEM_MANAGER_MENU, InventoryType.MENU)
				.setArena(arena)
				.setID(golemIndex)
				.build(),
			arena
				.getGolems()
				.get(golemIndex)
				.getName(),
			1,
			true,
			buttons
		);
	}

	// Display golem removal confirmation
	public static Inventory createGolemConfirmMenu(Arena arena, int golemIndex) {
		return InventoryFactory.createConfirmationMenu(
			InventoryID.GOLEM_CONFIRM_MENU,
			null,
			arena,
			golemIndex,
			CommunicationManager.format("&4&l" + LanguageManager.messages.removeGolem + "?")
		);
	}

	// Generate the ability upgrade shop
	public static Inventory createAbilityUpgradeShopMenu(Arena arena, VDPlayer player) {
		// Create inventory
		List<ItemStack> buttons = new ArrayList<>();
		if (Kit.checkAbilityKit(player
			.getKit()
			.getID())) {
			if (player.getTieredEssenceLevel() + 1 > player
				.getKit()
				.getLevel() * 2)
				buttons.add(InventoryButtons.noUpgrade());
			else {
				ItemStack ability = null;
				switch (player.getTieredEssenceLevel() + 1) {
					case 1:
						ability = VDAbility.createAbility(player
							.getKit()
							.getID(), VDItem.Tier.T1);
						break;
					case 2:
						ability = VDAbility.createAbility(player
							.getKit()
							.getID(), VDItem.Tier.T2);
						break;
					case 3:
						ability = VDAbility.createAbility(player
							.getKit()
							.getID(), VDItem.Tier.T3);
						break;
					case 4:
						ability = VDAbility.createAbility(player
							.getKit()
							.getID(), VDItem.Tier.T4);
						break;
					case 5:
						ability = VDAbility.createAbility(player
							.getKit()
							.getID(), VDItem.Tier.T5);
						break;
				}
				if (ability == null)
					buttons.add(InventoryButtons.noUpgrade());
				else if (player.isBoosted() && PlayerManager.hasAchievement(
					player.getID(),
					Achievement
						.allMaxedAbility()
						.getID()
				))
					buttons.add(VDAbility.modifyCooldown(arena.modifyPrice(ability), .9));
				else buttons.add(arena.modifyPrice(ability));
			}
		}
		else buttons.add(InventoryButtons.noUpgrade());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ABILITY_UPGRADE_SHOP_MENU, InventoryType.MENU)
				.build(),
			CommunicationManager.format("&2&l" + LanguageManager.names.abilityUpgradeShop),
			true,
			buttons
		);
	}

	// Display player stats
	public static Inventory createPlayerStatsMenu(UUID ownerID, UUID requesterID) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PLAYER_STATS_MENU, InventoryType.MENU)
				.setPlayerID(ownerID)
				.build(),
			18,
			CommunicationManager.format("&2&l" + String.format(
				LanguageManager.messages.playerStatistics,
				Bukkit
					.getOfflinePlayer(ownerID)
					.getName()
			))
		);

		// Total kills
		inv.setItem(0, new ItemStackBuilder(
			Material.DRAGON_HEAD,
			CommunicationManager.format("&4&l" + LanguageManager.playerStats.totalKills.name +
				": &4" + PlayerManager.getTotalKills(ownerID))
		)
			.setLores(
				CommunicationManager.format("&7" + LanguageManager.playerStats.totalKills.description)
			)
			.build());

		// Top kills
		inv.setItem(10, new ItemStackBuilder(
			Material.ZOMBIE_HEAD,
			CommunicationManager.format("&c&l" + LanguageManager.playerStats.topKills.name +
				": &c" + PlayerManager.getTopKills(ownerID))
		)
			.setLores(
				CommunicationManager.format("&7" + LanguageManager.playerStats.topKills.description)
			)
			.build());

		// Total gems
		inv.setItem(2, new ItemStackBuilder(
			Material.EMERALD_BLOCK,
			CommunicationManager.format("&2&l" + LanguageManager.playerStats.totalGems.name +
				": &2" + PlayerManager.getTotalGems(ownerID))
		)
			.setLores(
				CommunicationManager.format("&7" + LanguageManager.playerStats.totalGems.description)
			)
			.build());

		// Top balance
		inv.setItem(12, new ItemStackBuilder(
			Material.EMERALD,
			CommunicationManager.format("&a&l" + LanguageManager.playerStats.topBalance.name +
				": &a" + PlayerManager.getTopBalance(ownerID))
		)
			.setLores(
				CommunicationManager.format("&7" + LanguageManager.playerStats.topBalance.description)
			)
			.build());

		// Top wave
		inv.setItem(4, new ItemStackBuilder(
			Material.GOLDEN_SWORD,
			CommunicationManager.format("&3&l" + LanguageManager.playerStats.topWave.name +
				": &3" + PlayerManager.getTopWave(ownerID))
		)
			.setLores(CommunicationManager.format("&7" + LanguageManager.playerStats.topWave.description))
			.setButtonFlags()
			.build());

		// Achievements
		inv.setItem(14, new ItemStackBuilder(
			Material.GOLDEN_HELMET,
			CommunicationManager.format("&6&l" + LanguageManager.messages.achievements)
		)
			.setButtonFlags()
			.build());

		// Kits
		inv.setItem(6, new ItemStackBuilder(Material.ENDER_CHEST, CommunicationManager.format("&9&l" +
			LanguageManager.messages.kits)).build());

		// Reset stats
		if (ownerID.equals(requesterID))
			inv.setItem(16, new ItemStackBuilder(
				Material.LAVA_BUCKET,
				CommunicationManager.format("&d&l" + LanguageManager.messages.reset)
			)
				.setLores(
					CommunicationManager.format("&5&l" + LanguageManager.messages.resetWarning)
				)
				.build());

		// Crystal balance
		inv.setItem(8, new ItemStackBuilder(
			Material.DIAMOND,
			CommunicationManager.format("&b&l" + String.format(
				LanguageManager.messages.crystalBalance,
				LanguageManager.names.crystal
			) + ": &b" + PlayerManager.getCrystalBalance(ownerID))
		).build());

		return inv;
	}

	// Display kits for a player
	public static Inventory createPlayerKitsMenu(UUID ownerID, UUID requesterID) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PLAYER_KITS_MENU, InventoryType.MENU)
				.setPlayerID(ownerID)
				.build(),
			54,
			CommunicationManager.format("&9&l" + String.format(
				LanguageManager.messages.playerKits,
				Bukkit
					.getOfflinePlayer(ownerID)
					.getName()
			))
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.LIME_STAINED_GLASS_PANE,
				CommunicationManager.format("&a&l" + LanguageManager.names.giftKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.giftKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		inv.setItem(9, Kit
			.orc()
			.getButton(1, true));
		inv.setItem(10, Kit
			.farmer()
			.getButton(1, true));
		inv.setItem(11, Kit
			.soldier()
			.getButton(PlayerManager.hasSingleTierKit(ownerID, Kit
				.soldier()
				.getID()) ?
				1 : 0, true));
		inv.setItem(12, Kit
			.alchemist()
			.getButton(PlayerManager.hasSingleTierKit(ownerID, Kit
				.alchemist()
				.getID()) ?
				1 : 0, true));
		inv.setItem(13, Kit
			.tailor()
			.getButton(
				PlayerManager.hasSingleTierKit(ownerID, Kit
					.tailor()
					.getID()) ? 1 : 0,
				true
			));
		inv.setItem(14, Kit
			.trader()
			.getButton(
				PlayerManager.hasSingleTierKit(ownerID, Kit
					.trader()
					.getID()) ? 1 : 0,
				true
			));
		inv.setItem(15, Kit
			.summoner()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.summoner()
					.getID()),
				true
			));
		inv.setItem(16, Kit
			.reaper()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.reaper()
					.getID()),
				true
			));
		inv.setItem(17, Kit
			.phantom()
			.getButton(PlayerManager.hasSingleTierKit(ownerID, Kit
				.phantom()
				.getID()) ?
				1 : 0, true));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.MAGENTA_STAINED_GLASS_PANE,
				CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.abilityKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		inv.setItem(27, Kit
			.mage()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.mage()
					.getID()),
				true
			));
		inv.setItem(28, Kit
			.ninja()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.ninja()
					.getID()),
				true
			));
		inv.setItem(29, Kit
			.templar()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.templar()
					.getID()),
				true
			));
		inv.setItem(30, Kit
			.warrior()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.warrior()
					.getID()),
				true
			));
		inv.setItem(31, Kit
			.knight()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.knight()
					.getID()),
				true
			));
		inv.setItem(32, Kit
			.priest()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.priest()
					.getID()),
				true
			));
		inv.setItem(33, Kit
			.siren()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.siren()
					.getID()),
				true
			));
		inv.setItem(34, Kit
			.monk()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.monk()
					.getID()),
				true
			));
		inv.setItem(35, Kit
			.messenger()
			.getButton(PlayerManager.getMultiTierKitLevel(
				ownerID,
				Kit
					.messenger()
					.getID()
			), true));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.YELLOW_STAINED_GLASS_PANE,
				CommunicationManager.format("&e&l" + LanguageManager.names.effectKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.effectKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		inv.setItem(45, Kit
			.blacksmith()
			.getButton(PlayerManager.hasSingleTierKit(
				ownerID,
				Kit
					.blacksmith()
					.getID()
			) ? 1 : 0, true));
		inv.setItem(46, Kit
			.witch()
			.getButton(
				PlayerManager.hasSingleTierKit(ownerID, Kit
					.witch()
					.getID()) ? 1 : 0,
				true
			));
		inv.setItem(47, Kit
			.merchant()
			.getButton(PlayerManager.hasSingleTierKit(ownerID, Kit
				.merchant()
				.getID()) ?
				1 : 0, true));
		inv.setItem(48, Kit
			.vampire()
			.getButton(PlayerManager.hasSingleTierKit(ownerID, Kit
				.vampire()
				.getID()) ?
				1 : 0, true));
		inv.setItem(49, Kit
			.giant()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.giant()
					.getID()),
				true
			));
		inv.setItem(50, Kit
			.trainer()
			.getButton(
				PlayerManager.getMultiTierKitLevel(ownerID, Kit
					.trainer()
					.getID()),
				true
			));

		// Crystal balance
		if (ownerID.equals(requesterID))
			inv.setItem(52, new ItemStackBuilder(
				Material.DIAMOND,
				CommunicationManager.format("&b&l" + String.format(
					LanguageManager.messages.crystalBalance,
					LanguageManager.names.crystal
				) + ": &b" + PlayerManager.getCrystalBalance(ownerID))
			).build());

		// Option to exit
		inv.setItem(53, InventoryButtons.exit());

		return inv;
	}

	// Display kits for a player to select
	public static Inventory createSelectKitsMenu(Player player, Arena arena) {
		UUID id = player.getUniqueId();

		// Create inventory
		Inventory inv = Bukkit.createInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SELECT_KITS_MENU, InventoryType.MENU)
				.setPlayerID(id)
				.setArena(arena)
				.build(),
			54,
			CommunicationManager.format("&9&l" + arena.getName() + " " + LanguageManager.messages.kits)
		);

		// Gift kits
		for (int i = 0; i < 9; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.LIME_STAINED_GLASS_PANE,
				CommunicationManager.format("&a&l" + LanguageManager.names.giftKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.giftKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		List<String> bannedKitIDs = arena.getBannedKitIDs();
		if (!bannedKitIDs.contains(Kit
			.orc()
			.getID()))
			inv.setItem(9, Kit
				.orc()
				.getButton(1, false));
		if (!bannedKitIDs.contains(Kit
			.farmer()
			.getID()))
			inv.setItem(10, Kit
				.farmer()
				.getButton(1, false));
		if (!bannedKitIDs.contains(Kit
			.soldier()
			.getID()))
			inv.setItem(11, Kit
				.soldier()
				.getButton(PlayerManager.hasSingleTierKit(id, Kit
					.soldier()
					.getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit
			.alchemist()
			.getID()))
			inv.setItem(12, Kit
				.alchemist()
				.getButton(PlayerManager.hasSingleTierKit(id, Kit
					.alchemist()
					.getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit
			.tailor()
			.getID()))
			inv.setItem(13, Kit
				.tailor()
				.getButton(
					PlayerManager.hasSingleTierKit(id, Kit
						.tailor()
						.getID()) ? 1 : 0,
					false
				));
		if (!bannedKitIDs.contains(Kit
			.trader()
			.getID()))
			inv.setItem(14, Kit
				.trader()
				.getButton(
					PlayerManager.hasSingleTierKit(id, Kit
						.trader()
						.getID()) ? 1 : 0,
					false
				));
		if (!bannedKitIDs.contains(Kit
			.summoner()
			.getID()))
			inv.setItem(15, Kit
				.summoner()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.summoner()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.reaper()
			.getID()))
			inv.setItem(16, Kit
				.reaper()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.reaper()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.phantom()
			.getID()))
			inv.setItem(17, Kit
				.phantom()
				.getButton(PlayerManager.hasSingleTierKit(id, Kit
					.phantom()
					.getID()) ?
					1 : 0, false));

		// Ability kits
		for (int i = 18; i < 27; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.MAGENTA_STAINED_GLASS_PANE,
				CommunicationManager.format("&d&l" + LanguageManager.names.abilityKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.abilityKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		if (!bannedKitIDs.contains(Kit
			.mage()
			.getID()))
			inv.setItem(27, Kit
				.mage()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.mage()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.ninja()
			.getID()))
			inv.setItem(28, Kit
				.ninja()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.ninja()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.templar()
			.getID()))
			inv.setItem(29, Kit
				.templar()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.templar()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.warrior()
			.getID()))
			inv.setItem(30, Kit
				.warrior()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.warrior()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.knight()
			.getID()))
			inv.setItem(31, Kit
				.knight()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.knight()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.priest()
			.getID()))
			inv.setItem(32, Kit
				.priest()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.priest()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.siren()
			.getID()))
			inv.setItem(33, Kit
				.siren()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.siren()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.monk()
			.getID()))
			inv.setItem(34, Kit
				.monk()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.monk()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.messenger()
			.getID()))
			inv.setItem(35, Kit
				.messenger()
				.getButton(PlayerManager.getMultiTierKitLevel(
					id,
					Kit
						.messenger()
						.getID()
				), false));

		// Effect kits
		for (int i = 36; i < 45; i++)
			inv.setItem(i, new ItemStackBuilder(
				Material.YELLOW_STAINED_GLASS_PANE,
				CommunicationManager.format("&e&l" + LanguageManager.names.effectKits)
			)
				.setLores(
					CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
						LanguageManager.messages.effectKitsDescription, Constants.LORE_CHAR_LIMIT
					)
				)
				.build());

		if (!bannedKitIDs.contains(Kit
			.blacksmith()
			.getID()))
			inv.setItem(45, Kit
				.blacksmith()
				.getButton(PlayerManager.hasSingleTierKit(
					id,
					Kit
						.blacksmith()
						.getID()
				) ? 1 : 0, false));
		if (!bannedKitIDs.contains(Kit
			.witch()
			.getID()))
			inv.setItem(46, Kit
				.witch()
				.getButton(
					PlayerManager.hasSingleTierKit(id, Kit
						.witch()
						.getID()) ? 1 : 0,
					false
				));
		if (!bannedKitIDs.contains(Kit
			.merchant()
			.getID()))
			inv.setItem(47, Kit
				.merchant()
				.getButton(PlayerManager.hasSingleTierKit(id, Kit
					.merchant()
					.getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit
			.vampire()
			.getID()))
			inv.setItem(48, Kit
				.vampire()
				.getButton(PlayerManager.hasSingleTierKit(id, Kit
					.vampire()
					.getID()) ?
					1 : 0, false));
		if (!bannedKitIDs.contains(Kit
			.giant()
			.getID()))
			inv.setItem(49, Kit
				.giant()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.giant()
						.getID()),
					false
				));
		if (!bannedKitIDs.contains(Kit
			.trainer()
			.getID()))
			inv.setItem(50, Kit
				.trainer()
				.getButton(
					PlayerManager.getMultiTierKitLevel(id, Kit
						.trainer()
						.getID()),
					false
				));

		// Option for no kit
		inv.setItem(52, Kit
			.none()
			.getButton(0, true));

		// Option to exit
		inv.setItem(53, InventoryButtons.exit());

		return inv;
	}

	// Display achievements for a player
	public static Inventory createPlayerAchievementsMenu(UUID id) {
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement
			.topBalance1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance1()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance2()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance3()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance4()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance5()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance6()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance7()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance8()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance9()
					.getID()
			)));

		buttons.add(Achievement
			.topKills1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills1()
					.getID()
			)));
		buttons.add(Achievement
			.topKills2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills2()
					.getID()
			)));
		buttons.add(Achievement
			.topKills3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills3()
					.getID()
			)));
		buttons.add(Achievement
			.topKills4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills4()
					.getID()
			)));
		buttons.add(Achievement
			.topKills5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills5()
					.getID()
			)));
		buttons.add(Achievement
			.topKills6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills6()
					.getID()
			)));
		buttons.add(Achievement
			.topKills7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills7()
					.getID()
			)));
		buttons.add(Achievement
			.topKills8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills8()
					.getID()
			)));
		buttons.add(Achievement
			.topKills9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills9()
					.getID()
			)));

		buttons.add(Achievement
			.topWave1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave1()
					.getID()
			)));
		buttons.add(Achievement
			.topWave2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave2()
					.getID()
			)));
		buttons.add(Achievement
			.topWave3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave3()
					.getID()
			)));
		buttons.add(Achievement
			.topWave4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave4()
					.getID()
			)));
		buttons.add(Achievement
			.topWave5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave5()
					.getID()
			)));
		buttons.add(Achievement
			.topWave6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave6()
					.getID()
			)));
		buttons.add(Achievement
			.topWave7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave7()
					.getID()
			)));
		buttons.add(Achievement
			.topWave8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave8()
					.getID()
			)));
		buttons.add(Achievement
			.topWave9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave9()
					.getID()
			)));

		buttons.add(Achievement
			.totalGems1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems1()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems2()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems3()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems4()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems5()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems6()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems7()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems8()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems9()
					.getID()
			)));

		buttons.add(Achievement
			.totalKills1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills1()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills2()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills3()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills4()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills5()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills6()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills7()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills8()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills9()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeAlone()
					.getID()
			)));
		buttons.add(Achievement
			.blindAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindAlone()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyAlone()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfAlone()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveAlone()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightAlone()
					.getID()
			)));
		buttons.add(Achievement
			.nakedAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedAlone()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistAlone()
					.getID()
			)));
		buttons.add(Achievement
			.uhcAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcAlone()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.amputeeBalance()
					.getID()
			)));
		buttons.add(Achievement
			.blindBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindBalance()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.clumsyBalance()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfBalance()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveBalance()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightBalance()
					.getID()
			)));
		buttons.add(Achievement
			.nakedBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedBalance()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistBalance()
					.getID()
			)));
		buttons.add(Achievement
			.uhcBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcBalance()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeKills()
					.getID()
			)));
		buttons.add(Achievement
			.blindKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindKills()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyKills()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfKills()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveKills()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightKills()
					.getID()
			)));
		buttons.add(Achievement
			.nakedKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedKills()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistKills()
					.getID()
			)));
		buttons.add(Achievement
			.uhcKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcKills()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeWave()
					.getID()
			)));
		buttons.add(Achievement
			.blindWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindWave()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyWave()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfWave()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveWave()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightWave()
					.getID()
			)));
		buttons.add(Achievement
			.nakedWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedWave()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistWave()
					.getID()
			)));
		buttons.add(Achievement
			.uhcWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcWave()
					.getID()
			)));

		buttons.add(Achievement
			.alone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.alone()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistUhc()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.pacifistUhc()
					.getID()
			)));
		buttons.add(Achievement
			.allChallenges()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allChallenges()
					.getID()
			)));
		buttons.add(Achievement
			.allGift()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allGift()
					.getID()
			)));
		buttons.add(Achievement
			.allAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allAbility()
					.getID()
			)));
		buttons.add(Achievement
			.maxedAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.maxedAbility()
					.getID()
			)));
		buttons.add(Achievement
			.allMaxedAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allMaxedAbility()
					.getID()
			)));
		buttons.add(Achievement
			.allEffect()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allEffect()
					.getID()
			)));
		buttons.add(Achievement
			.allKits()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allKits()
					.getID()
			)));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU)
				.setPlayerID(id)
				.build(),
			CommunicationManager.format("&6&l" + Bukkit
				.getOfflinePlayer(id)
				.getName() + " " +
				LanguageManager.messages.achievements),
			true,
			false,
			"",
			buttons
		);
	}

	public static Inventory createPlayerAchievementsMenu(UUID id, int page) {
		List<ItemStack> buttons = new ArrayList<>();

		buttons.add(Achievement
			.topBalance1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance1()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance2()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance3()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance4()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance5()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance6()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance7()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance8()
					.getID()
			)));
		buttons.add(Achievement
			.topBalance9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topBalance9()
					.getID()
			)));

		buttons.add(Achievement
			.topKills1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills1()
					.getID()
			)));
		buttons.add(Achievement
			.topKills2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills2()
					.getID()
			)));
		buttons.add(Achievement
			.topKills3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills3()
					.getID()
			)));
		buttons.add(Achievement
			.topKills4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills4()
					.getID()
			)));
		buttons.add(Achievement
			.topKills5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills5()
					.getID()
			)));
		buttons.add(Achievement
			.topKills6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills6()
					.getID()
			)));
		buttons.add(Achievement
			.topKills7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills7()
					.getID()
			)));
		buttons.add(Achievement
			.topKills8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills8()
					.getID()
			)));
		buttons.add(Achievement
			.topKills9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topKills9()
					.getID()
			)));

		buttons.add(Achievement
			.topWave1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave1()
					.getID()
			)));
		buttons.add(Achievement
			.topWave2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave2()
					.getID()
			)));
		buttons.add(Achievement
			.topWave3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave3()
					.getID()
			)));
		buttons.add(Achievement
			.topWave4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave4()
					.getID()
			)));
		buttons.add(Achievement
			.topWave5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave5()
					.getID()
			)));
		buttons.add(Achievement
			.topWave6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave6()
					.getID()
			)));
		buttons.add(Achievement
			.topWave7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave7()
					.getID()
			)));
		buttons.add(Achievement
			.topWave8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave8()
					.getID()
			)));
		buttons.add(Achievement
			.topWave9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.topWave9()
					.getID()
			)));

		buttons.add(Achievement
			.totalGems1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems1()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems2()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems3()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems4()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems5()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems6()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems7()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems8()
					.getID()
			)));
		buttons.add(Achievement
			.totalGems9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalGems9()
					.getID()
			)));

		buttons.add(Achievement
			.totalKills1()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills1()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills2()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills2()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills3()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills3()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills4()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills4()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills5()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills5()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills6()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills6()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills7()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills7()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills8()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills8()
					.getID()
			)));
		buttons.add(Achievement
			.totalKills9()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.totalKills9()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeAlone()
					.getID()
			)));
		buttons.add(Achievement
			.blindAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindAlone()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyAlone()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfAlone()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveAlone()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightAlone()
					.getID()
			)));
		buttons.add(Achievement
			.nakedAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedAlone()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistAlone()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistAlone()
					.getID()
			)));
		buttons.add(Achievement
			.uhcAlone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcAlone()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.amputeeBalance()
					.getID()
			)));
		buttons.add(Achievement
			.blindBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindBalance()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.clumsyBalance()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfBalance()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveBalance()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightBalance()
					.getID()
			)));
		buttons.add(Achievement
			.nakedBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedBalance()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistBalance()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistBalance()
					.getID()
			)));
		buttons.add(Achievement
			.uhcBalance()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcBalance()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeKills()
					.getID()
			)));
		buttons.add(Achievement
			.blindKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindKills()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyKills()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfKills()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveKills()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightKills()
					.getID()
			)));
		buttons.add(Achievement
			.nakedKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedKills()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistKills()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistKills()
					.getID()
			)));
		buttons.add(Achievement
			.uhcKills()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcKills()
					.getID()
			)));

		buttons.add(Achievement
			.amputeeWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.amputeeWave()
					.getID()
			)));
		buttons.add(Achievement
			.blindWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.blindWave()
					.getID()
			)));
		buttons.add(Achievement
			.clumsyWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.clumsyWave()
					.getID()
			)));
		buttons.add(Achievement
			.dwarfWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.dwarfWave()
					.getID()
			)));
		buttons.add(Achievement
			.explosiveWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.explosiveWave()
					.getID()
			)));
		buttons.add(Achievement
			.featherweightWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.featherweightWave()
					.getID()
			)));
		buttons.add(Achievement
			.nakedWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.nakedWave()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistWave()
			.getButton(PlayerManager.hasAchievement(
				id,

				Achievement
					.pacifistWave()
					.getID()
			)));
		buttons.add(Achievement
			.uhcWave()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.uhcWave()
					.getID()
			)));

		buttons.add(Achievement
			.alone()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.alone()
					.getID()
			)));
		buttons.add(Achievement
			.pacifistUhc()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.pacifistUhc()
					.getID()
			)));
		buttons.add(Achievement
			.allChallenges()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allChallenges()
					.getID()
			)));
		buttons.add(Achievement
			.allGift()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allGift()
					.getID()
			)));
		buttons.add(Achievement
			.allAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allAbility()
					.getID()
			)));
		buttons.add(Achievement
			.maxedAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.maxedAbility()
					.getID()
			)));
		buttons.add(Achievement
			.allMaxedAbility()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allMaxedAbility()
					.getID()
			)));
		buttons.add(Achievement
			.allEffect()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allEffect()
					.getID()
			)));
		buttons.add(Achievement
			.allKits()
			.getButton(PlayerManager.hasAchievement(
				id,
				Achievement
					.allKits()
					.getID()
			)));

		return InventoryFactory.createDynamicSizeBottomNavInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.PLAYER_ACHIEVEMENTS_MENU, InventoryType.MENU)
				.setPlayerID(id)
				.setPage(page)
				.build(),
			CommunicationManager.format("&6&l" + Bukkit
				.getOfflinePlayer(id)
				.getName() + " " +
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
			new InventoryMeta.InventoryMetaBuilder(InventoryID.CRYSTAL_CONVERT_MENU, InventoryType.MENU)
				.setPlayerID(player
					.getPlayer()
					.getUniqueId())
				.build(),
			27,
			CommunicationManager.format("&9&l" + String.format(
				LanguageManager.names.crystalConverter,
				LanguageManager.names.crystal
			))
		);

		// Display crystals to convert
		inv.setItem(3, new ItemStackBuilder(
			Material.DIAMOND_BLOCK,
			CommunicationManager.format("&b&l" + String.format(
				LanguageManager.messages.crystalsToConvert,
				LanguageManager.names.crystals
			) + ": " + (player.getGemBoost() * 5))
		).build());

		// Display gems to receive
		inv.setItem(5, new ItemStackBuilder(
			Material.EMERALD_BLOCK,
			CommunicationManager.format("&a&l" + LanguageManager.messages.gemsToReceive + ": " +
				player.getGemBoost())
		).build());

		// Crystal balance display
		int balance = PlayerManager.getCrystalBalance(player.getID());

		inv.setItem(8, new ItemStackBuilder(
			Material.DIAMOND,
			CommunicationManager.format("&b&l" + String.format(
				LanguageManager.messages.crystalBalance,
				LanguageManager.names.crystal
			) + ": &b" + balance)
		).build());

		// Option to increase by 1
		inv.setItem(9, new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&l+1 " + LanguageManager.messages.gems)
		).build());

		// Option to increase by 10
		inv.setItem(11, new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&l+10 " + LanguageManager.messages.gems)
		).build());

		// Option to increase by 100
		inv.setItem(13, new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&l+100 " + LanguageManager.messages.gems)
		).build());

		// Option to increase by 1000
		inv.setItem(15, new ItemStackBuilder(
			Material.LIME_CONCRETE,
			CommunicationManager.format("&a&l+1000 " + LanguageManager.messages.gems)
		).build());

		// Option to reset
		inv.setItem(17, new ItemStackBuilder(
			Material.LIGHT_BLUE_CONCRETE,
			CommunicationManager.format("&3&l" + LanguageManager.messages.reset)
		).build());

		// Option to decrease by 1
		inv.setItem(18, new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&l-1 " + LanguageManager.messages.gems)
		).build());

		// Option to decrease by 10
		inv.setItem(20, new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&l-10 " + LanguageManager.messages.gems)
		).build());

		// Option to decrease by 100
		inv.setItem(22, new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&l-100 " + LanguageManager.messages.gems)
		).build());

		// Option to decrease by 1000
		inv.setItem(24, new ItemStackBuilder(
			Material.RED_CONCRETE,
			CommunicationManager.format("&c&l-1000 " + LanguageManager.messages.gems)
		).build());

		// Option to exit
		inv.setItem(26, InventoryButtons.exit());

		return inv;
	}

	// Display challenges for a player to select
	public static Inventory createSelectChallengesMenu(VDPlayer player, Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Set buttons
		buttons.add(Challenge
			.amputee()
			.getButton(player
				.getChallenges()
				.contains(Challenge.amputee())));
		buttons.add(Challenge
			.clumsy()
			.getButton(player
				.getChallenges()
				.contains(Challenge.clumsy())));
		buttons.add(Challenge
			.featherweight()
			.getButton(player
				.getChallenges()
				.contains(Challenge.featherweight())));
		buttons.add(Challenge
			.pacifist()
			.getButton(player
				.getChallenges()
				.contains(Challenge.pacifist())));
		buttons.add(Challenge
			.dwarf()
			.getButton(player
				.getChallenges()
				.contains(Challenge.dwarf())));
		buttons.add(Challenge
			.uhc()
			.getButton(player
				.getChallenges()
				.contains(Challenge.uhc())));
		buttons.add(Challenge
			.explosive()
			.getButton(player
				.getChallenges()
				.contains(Challenge.explosive())));
		buttons.add(Challenge
			.naked()
			.getButton(player
				.getChallenges()
				.contains(Challenge.naked())));
		buttons.add(Challenge
			.blind()
			.getButton(player
				.getChallenges()
				.contains(Challenge.blind())));
		buttons.add(Challenge
			.none()
			.getButton(player
				.getChallenges()
				.isEmpty()));

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.SELECT_CHALLENGES_MENU, InventoryType.MENU)
				.setPlayerID(player
					.getPlayer()
					.getUniqueId())
				.setArena(arena)
				.build(),
			CommunicationManager.format("&5&l" + arena.getName() + " " + LanguageManager.messages.challenges),
			true,
			buttons
		);
	}

	// Display arena information
	public static Inventory createArenaInfoMenu(Arena arena) {
		List<ItemStack> buttons = new ArrayList<>();

		// Maximum players
		buttons.add(new ItemStackBuilder(
			Material.NETHERITE_HELMET,
			CommunicationManager.format("&4&l" + LanguageManager.arenaStats.maxPlayers.name +
				": &4" + arena.getMaxPlayers())
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
				LanguageManager.arenaStats.maxPlayers.description, Constants.LORE_CHAR_LIMIT
			))
			.setButtonFlags()
			.build());

		// Minimum players
		buttons.add(new ItemStackBuilder(
			Material.NETHERITE_BOOTS,
			CommunicationManager.format("&2&l" + LanguageManager.arenaStats.minPlayers.name +
				": &2" + arena.getMinPlayers())
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
				LanguageManager.arenaStats.minPlayers.description, Constants.LORE_CHAR_LIMIT
			))
			.setButtonFlags()
			.build());

		// Max waves
		String waves;
		if (arena.getMaxWaves() < 0)
			waves = LanguageManager.messages.unlimited;
		else waves = Integer.toString(arena.getMaxWaves());
		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_SWORD,
			CommunicationManager.format("&3&l" + LanguageManager.arenaStats.maxWaves.name +
				": &3" + waves)
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
				LanguageManager.arenaStats.maxWaves.description, Constants.LORE_CHAR_LIMIT
			))
			.setButtonFlags()
			.build());

		// Wave time limit
		String limit;
		if (arena.getWaveTimeLimit() < 0)
			limit = LanguageManager.messages.unlimited;
		else limit = arena.getWaveTimeLimit() + " minute(s)";
		buttons.add(new ItemStackBuilder(
			Material.CLOCK,
			CommunicationManager.format("&9&l" + LanguageManager.arenaStats.timeLimit.name +
				": &9" + limit)
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
					LanguageManager.arenaStats.timeLimit.description, Constants.LORE_CHAR_LIMIT
				)
			)
			.build());

		// Allowed kits
		buttons.add(new ItemStackBuilder(
			Material.ENDER_CHEST,
			CommunicationManager.format("&9&l" + LanguageManager.messages.allowedKits)
		).build());

		// Forced challenges
		buttons.add(new ItemStackBuilder(
			Material.NETHER_STAR,
			CommunicationManager.format("&9&l" + LanguageManager.messages.forcedChallenges)
		).build());

		// Dynamic time limit
		buttons.add(new ItemStackBuilder(
			Material.SNOWBALL,
			CommunicationManager.format("&a&l" +
				LanguageManager.arenaStats.dynamicTimeLimit.name +
				": " + getToggleStatus(arena.hasDynamicLimit()))
		)
			.setLores(
				CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
					LanguageManager.arenaStats.dynamicTimeLimit.description, Constants.LORE_CHAR_LIMIT
				)
			)
			.build());

		// Late arrival
		buttons.add(new ItemStackBuilder(
			Material.DAYLIGHT_DETECTOR,
			CommunicationManager.format("&e&l" + LanguageManager.arenaStats.lateArrival.name +
				": " + getToggleStatus(arena.hasLateArrival()))
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
				LanguageManager.arenaStats.lateArrival.description, Constants.LORE_CHAR_LIMIT
			))
			.setButtonFlags()
			.build());

		// Player spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&e&l" + LanguageManager.names.playerSpawnParticles +
				": " + getToggleStatus(arena.hasSpawnParticles()))
		).build());

		// Monster spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&d&l" + LanguageManager.names.monsterSpawnParticles +
				": " + getToggleStatus(arena.hasMonsterParticles()))
		).build());

		// Villager spawn particles
		buttons.add(new ItemStackBuilder(
			Material.FIREWORK_ROCKET,
			CommunicationManager.format("&a&l" +
				LanguageManager.names.villagerSpawnParticles + ": " +
				getToggleStatus(arena.hasVillagerParticles()))
		).build());

		// Community chest
		buttons.add(new ItemStackBuilder(
			Material.CHEST,
			CommunicationManager.format("&d&l" + LanguageManager.names.communityChest +
				": " + getToggleStatus(arena.hasCommunity()))
		).build());

		// Difficulty multiplier
		buttons.add(new ItemStackBuilder(
			Material.TURTLE_HELMET,
			CommunicationManager.format("&4&l" +
				LanguageManager.arenaStats.difficultyMultiplier.name + ": &4" +
				arena.getDifficultyMultiplier())
		)
			.setLores(CommunicationManager.formatDescriptionArr(ChatColor.GRAY,
				LanguageManager.arenaStats.difficultyMultiplier.description, Constants.LORE_CHAR_LIMIT
			))
			.setButtonFlags()
			.build());

		// Arena records
		List<String> records = new ArrayList<>();
		arena
			.getSortedDescendingRecords()
			.forEach(arenaRecord -> {
				records.add(CommunicationManager.format("&f" + LanguageManager.messages.wave + " " +
					arenaRecord.getWave()));
				for (int i = 0; i < arenaRecord
					.getPlayers()
					.size() / 4 + 1; i++) {
					StringBuilder players = new StringBuilder(CommunicationManager.format("&7"));
					if (i * 4 + 4 < arenaRecord
						.getPlayers()
						.size()) {
						for (int j = i * 4; j < i * 4 + 4; j++)
							players
								.append(arenaRecord
									.getPlayers()
									.get(j))
								.append(", ");
						records.add(CommunicationManager.format(players.substring(0, players.length() - 1)));
					}
					else {
						for (int j = i * 4; j < arenaRecord
							.getPlayers()
							.size(); j++)
							players
								.append(arenaRecord
									.getPlayers()
									.get(j))
								.append(", ");
						records.add(CommunicationManager.format(players.substring(0, players.length() - 2)));
					}
				}
			});
		String[] recordsArray = new String[records.size()];
		buttons.add(new ItemStackBuilder(
			Material.GOLDEN_HELMET,
			CommunicationManager.format("&e&l" + LanguageManager.messages.arenaRecords)
		)
			.setLores(records.toArray(recordsArray))
			.setButtonFlags()
			.build());

		return InventoryFactory.createDynamicSizeInventory(
			new InventoryMeta.InventoryMetaBuilder(InventoryID.ARENA_INFO_MENU, InventoryType.MENU)
				.setArena(arena)
				.build(),
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
}
