package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.game.GameItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class Inventories {
	private final Main plugin;
	private final GameItems gi;
	private final InventoryItems ii;

	public Inventories (Main plugin, GameItems gi, InventoryItems ii) {
		this.plugin = plugin;
		this.gi = gi;
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
	public static final Material[] KEYMATS = {Material.BLACK_CONCRETE, Material.WHITE_WOOL};
	public static final Material[] MONSTERMATS = {Material.SKELETON_SKULL, Material.ZOMBIE_HEAD};

	// Temporary constants for buttons that don't work yet
	final String CONSTRUCTION = "&fComing Soon!";
	final boolean[] FLAGS = {true, true};

	//	Menu of all the arenas
	public Inventory createArenasInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  Utils.format("&k") +
				Utils.format("&2&lVillager Defense Arenas"));

		// Prepare for items and metas
		ItemStack item;

		// Options to interact with all 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists, set button accordingly
			if (plugin.getData().getString("data.a" + (i) + ".name") == null ||
					plugin.getData().getString("data.a" + (i) + ".name").length() == 0)
				inv.setItem(i,
						Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&lCreate Arena " + (i + 1))));
			else
				inv.setItem(i, Utils.createItem(Material.LIME_CONCRETE,
						Utils.format("&a&lEdit " + plugin.getData().getString("data.a" + (i) + ".name"))));
		}

		// Option to set lobby location
		inv.setItem(45, Utils.createItem(Material.BELL, Utils.format("&a&lSet Lobby")));

		// Option to teleport lobby location
		inv.setItem(46, ii.teleport("Lobby"));

		// Option to remove lobby location
		inv.setItem(52, ii.remove("LOBBY"));

		// Option to exit
		inv.setItem(53, ii.exit());
		
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

	// Menu for naming an arena
	public Inventory createNamingInventory(int num) {
		// Gather arena name and caps lock state
		String name = plugin.getData().getString("data.a" + num + ".name");
		if (name == null)
			name = "";
		boolean caps = plugin.getData().getBoolean("data.a" + num + ".caps");

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format("&2&lArena " + (num + 1) + " Name: &8&l" + name));

		// Letter and number inputs
		for (int i = 0; i < 36; i++) {
			if (caps)
				inv.setItem(i, Utils.createItem(KEYMATS[i % 2], Utils.format("&f&l" + NAMES[i + 36])));
			else inv.setItem(i, Utils.createItem(KEYMATS[i % 2], Utils.format("&f&l" + NAMES[i])));
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
	public Inventory createArenaInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lEdit " + plugin.getData().getString("data.a" + num + ".name")));

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
		inv.setItem(6, Utils.createItem(Material.NETHER_BRICK_FENCE,
				Utils.format("&9&lClose Arena"),
				Utils.format(CONSTRUCTION)));

		// Option to remove arena
		inv.setItem(7, ii.remove("ARENA"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing an arena
	public Inventory createArenaConfirmInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove " + plugin.getData().getString("data.a" + num + ".name") + '?'));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

	// Menu for editing the portal of an arena
	public Inventory createPortalInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lPortal: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to create the portal
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&a&lCreate Portal")));

		// Option to teleport to the portal
		inv.setItem(1, ii.teleport("Portal"));

		// Option to remove the portal
		inv.setItem(7, ii.remove("PORTAL"));

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

	//	Menu for editing the player settings of an arena
	public Inventory createPlayersInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Settings: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to edit player spawn
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&5&lPlayer Spawn")));

		// Option to toggle player spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&b&lToggle Spawn Particles"),
				Utils.format(CONSTRUCTION)));

		// Option to edit max players
		inv.setItem(2, Utils.createItem(Material.NETHERITE_HELMET,
				Utils.format("&4&lMaximum Players"),
				FLAGS,
				null));

		// Option to edit min players
		inv.setItem(3, Utils.createItem(Material.NETHERITE_BOOTS,
				Utils.format("&2&lMinimum Players"),
				FLAGS,
				null));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	//	Menu for editing the player spawn of an arena
	public Inventory createPlayerSpawnInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Spawn: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to create player spawn
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&a&lCreate Spawn")));

		// Option to teleport to player spawn
		inv.setItem(1, ii.teleport("Spawn"));

		// Option to remove player spawn
		inv.setItem(7, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	//	Menu for changing max players in an arena
	public Inventory createMaxPlayerInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lMaximum Players: " + plugin.getData().getInt("data.a" + num + ".max")));

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

	//	Menu for changing min players in an arena
	public Inventory createMinPlayerInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMinimum Players: " + plugin.getData().getInt("data.a" + num + ".min")));

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

	//	Confirmation menu for removing player spawn
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

	//	Menu for editing the mob settings of an arena
	public Inventory createMobsInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMob Settings: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to edit monster spawns
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&2&lMonster Spawns")));

		// Option to toggle monster spawn particles
		inv.setItem(1, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&a&lToggle Monster Spawn Particles"),
				Utils.format(CONSTRUCTION)));

		// Option to edit villager spawns
		inv.setItem(2, Utils.createItem(Material.END_PORTAL_FRAME,
				Utils.format("&5&lVillager Spawns"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle villager spawn particles
		inv.setItem(3, Utils.createItem(Material.FIREWORK_ROCKET,
				Utils.format("&d&lToggle Villager Spawn Particles"),
				Utils.format(CONSTRUCTION)));

		// Option to edit monsters allowed
		inv.setItem(4, Utils.createItem(Material.DRAGON_HEAD,
				Utils.format("&3&lMonsters Allowed"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic monster count
		inv.setItem(5, Utils.createItem(Material.SLIME_BALL,
				Utils.format("&e&lToggle Dynamic Monster Count"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic difficulty
		inv.setItem(6, Utils.createItem(Material.MAGMA_CREAM,
				Utils.format("&6&lToggle Dynamic Difficulty"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	//	Menu for editing the monster spawns of an arena
	public Inventory createMonsterSpawnInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawns: " + plugin.getData().getString("data.a" + num + ".name")));

		// Prepare for material indexing
		int index;

		// Options to interact with all 8 possible mob spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (plugin.getData().getString("data.a" + (num) + ".mob." + i + ".x") == null)
				index = 0;
			else index = 1;

			// Create and set item
			inv.setItem(i, Utils.createItem(MONSTERMATS[index], Utils.format("&2&lMob Spawn " + (i + 1))));
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing a specific monster spawn of an arena
	public Inventory createMonsterSpawnMenu(int num, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMonster Spawn " + slot + ": " +
				plugin.getData().getString("data.a" + num + ".name")));

		// Option to create monster spawn
		inv.setItem(0, Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&a&lCreate Spawn")));

		// Option to teleport to monster spawn
		inv.setItem(1, ii.teleport("Spawn"));

		// Option to remove monster spawn
		inv.setItem(7, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Confirmation menu for removing mob spawns
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

	// Menu for editing the shop settings of an arena
	public Inventory createShopsInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&e&lShop Settings: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to create a custom shop
		inv.setItem(0, Utils.createItem(Material.EMERALD,
				Utils.format("&a&lCreate Custom Shop"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle default shop
		inv.setItem(1, Utils.createItem(Material.GOLD_BLOCK,
				Utils.format("&6&lToggle Default Shop"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle custom shop
		inv.setItem(2, Utils.createItem(Material.EMERALD_BLOCK,
				Utils.format("&2&lToggle Custom Shop"),
				Utils.format(CONSTRUCTION)));

		// Option to toggle dynamic prices
		inv.setItem(3, Utils.createItem(Material.NETHER_STAR,
				Utils.format("&b&lToggle Dynamic Prices"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the game settings of an arena
	public Inventory createGameSettingsInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&8&lGame Settings: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to change max rounds
		inv.setItem(0, Utils.createItem(Material.NETHERITE_SWORD,
				Utils.format("&3&lMax Rounds"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to round time limit
		inv.setItem(1, Utils.createItem(Material.CLOCK,
				Utils.format("&2&lRound Time Limit"),
				Utils.format(CONSTRUCTION)));

		// Option to edit allowed kits
		inv.setItem(2, Utils.createItem(Material.ENDER_CHEST,
				Utils.format("&9&lAllowed Kits"),
				Utils.format(CONSTRUCTION)));

		// Option to edit persistent rewards
		inv.setItem(3, Utils.createItem(Material.EMERALD,
				Utils.format("&a&lPersistent Rewards"),
				Utils.format(CONSTRUCTION)));

		// Option to edit sounds
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_13,
				Utils.format("&d&lSounds"),
				FLAGS,
				null));

		// Option to copy game settings from another arena
		inv.setItem(5, Utils.createItem(Material.FEATHER,
				Utils.format("&7&lCopy Game Settings"),
				Utils.format(CONSTRUCTION)));

		// Option to copy arena settings from another arena
		inv.setItem(6, Utils.createItem(Material.WRITABLE_BOOK,
				Utils.format("&8&lCopy Arena Settings"),
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Menu for editing the sounds of an arena
	public Inventory createSoundsInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lSounds: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to edit win sound
		inv.setItem(0, Utils.createItem(Material.MUSIC_DISC_PIGSTEP,
				Utils.format("&a&lWin"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to edit lose sound
		inv.setItem(1, Utils.createItem(Material.MUSIC_DISC_11,
				Utils.format("&e&lLose"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to edit round start sound
		inv.setItem(2, Utils.createItem(Material.MUSIC_DISC_CAT,
				Utils.format("&2&lRound Start"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to edit round finish sound
		inv.setItem(3, Utils.createItem(Material.MUSIC_DISC_BLOCKS,
				Utils.format("&4&lRound Finish"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to edit waiting music
		inv.setItem(4, Utils.createItem(Material.MUSIC_DISC_MELLOHI,
				Utils.format("&6&lWaiting Music"),
				FLAGS,
				null,
				Utils.format(CONSTRUCTION)));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

	// Generate the shop menu
	public Inventory createShop(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 36, Utils.format("&k") +
				Utils.format("&2&lItem Shop"));
		Random r = new Random();

		// Fill in armor
		for (int i = 0; i < 18; i++) {
			inv.setItem(i, gi.armor[num][r.nextInt(gi.armor[num].length)]);
		}

		// Fill in weapons
		for (int i = 18; i < 27; i++) {
			inv.setItem(i, gi.weapon[num][r.nextInt(gi.weapon[num].length)]);
		}

		// Fill in consumables
		for (int i = 27; i < 36; i++) {
			inv.setItem(i, gi.consume[num][r.nextInt(gi.consume[num].length)]);
		}

		return inv;
	}
}
