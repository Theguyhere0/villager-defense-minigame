package me.theguyhere.villagerdefense;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.theguyhere.villagerdefense.game.GameItems;

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
	public final char[] NAMES = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
		's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
		'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		' '
	};

	// Inventory name constants
	public static final String ARENAINV = "&2&lVillager Defense Arenas";
	public static final String LOBBYCONFIRMINV = "&4&lRemove Lobby?";
	public static final String ARENA = "&2&lArena ";
	public static final String EDIT1 = "&2&lEdit ";
	public static final String EDIT2 = "&6&lEdit ";
	public static final String CREATE = "&a&lCreate ";
	public static final String REMOVE = "&4&lRemove ";

//	Menu of all the arenas
	public Inventory createArenaInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54,  Utils.format("&k") +
				Utils.format(ARENAINV));

		// Prepare for items and metas
		ItemStack item;
		ItemMeta meta;

		// Options to interact with all 45 possible arenas
		for (int i = 0; i < 45; i++) {
			// Check if arena exists
			if (plugin.getData().getString("data.a" + (i) + ".name") == null ||
					plugin.getData().getString("data.a" + (i) + ".name").length() == 0) {
				item = new ItemStack(Material.RED_CONCRETE);
				meta = item.getItemMeta();
				meta.setDisplayName(Utils.format("&c&lCreate Arena " + (i + 1)));
			} else {
				item = new ItemStack(Material.LIME_CONCRETE);
				meta = item.getItemMeta();
				meta.setDisplayName(Utils.format("&a&lEdit " +
						plugin.getData().getString("data.a" + (i) + ".name")));
			}

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Option to set lobby location
		item = new ItemStack(Material.BELL);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&a&lSet Lobby"));
		item.setItemMeta(meta);
		inv.setItem(45, item);

		// Option to teleport lobby location
		inv.setItem(46, ii.teleport("Lobby"));

		// Option to remove lobby location
		inv.setItem(52, ii.remove("LOBBY"));

		// Option to exit
		inv.setItem(53, ii.exit());
		
		return inv;
	}
	
//	Confirmation menu for removing lobby
	public Inventory createLobbyConfirmInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format(LOBBYCONFIRMINV));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

//	Menu for naming an arena
	public Inventory createNamingInventory(int num) {
		// Prepare for items and metas
		ItemStack item;
		ItemMeta meta;

		// Easily alternate between black and white wool
		ItemStack[] items = {
			new ItemStack(Material.BLACK_CONCRETE),
			new ItemStack(Material.WHITE_WOOL)
		};

		// Gather arena name and caps lock state
		String name = plugin.getData().getString("data.a" + num + ".name");
		if (name == null)
			name = "";
		boolean caps = plugin.getData().getBoolean("data.a" + num + ".caps");

		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 54, Utils.format("&k") +
				Utils.format(ARENA + (num + 1) + " Name: &8&l" + name));

		// Letter and number inputs
		for (int i = 0; i < 36; i++) {
			// Get alternating wool color
			item = items[i % 2];
			meta = item.getItemMeta();

			// Set name depending on caps lock state
			if (caps)
				meta.setDisplayName(Utils.format("&f&l" + NAMES[i + 36]));
			else meta.setDisplayName(Utils.format("&f&l" + NAMES[i]));

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Space inputs
		for (int i = 36; i < 45; i++) {
			// Create item
			item = new ItemStack(Material.GRAY_CONCRETE);
			meta = item.getItemMeta();

			// Set name to Space
			meta.setDisplayName(Utils.format("&f&lSpace"));

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Caps lock toggle
		if (caps) {
			// Create item
			item = new ItemStack(Material.SPECTRAL_ARROW);
			meta = item.getItemMeta();

			// Set name
			meta.setDisplayName(Utils.format("&2&lCAPS LOCK: ON"));

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(45, item);
		} else {
			// Create item
			item = new ItemStack(Material.ARROW);
			meta = item.getItemMeta();

			// Set name
			meta.setDisplayName(Utils.format("&4&lCAPS LOCK: OFF"));

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(45, item);
		}

		// Backspace input
		item = new ItemStack(Material.RED_CONCRETE);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&c&lBackspace"));
		item.setItemMeta(meta);
		inv.setItem(46, item);

		// Save option
		item = new ItemStack(Material.LIME_CONCRETE);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&a&lSAVE"));
		item.setItemMeta(meta);
		inv.setItem(52, item);

		// Cancel option
		item = new ItemStack(Material.BARRIER);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&c&lCANCEL"));
		item.setItemMeta(meta);
		inv.setItem(53, item);

		return inv;
	}

//	Menu for editing an arena
	public Inventory createEditInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format(EDIT1 + plugin.getData().getString("data.a" + num + ".name")));

		// Option to edit name
		ItemStack item = new ItemStack(Material.NAME_TAG);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format(EDIT2 + "Name"));
		item.setItemMeta(meta);
		inv.setItem(0, item);

		// Option to edit game portal
		item = new ItemStack(Material.END_PORTAL_FRAME);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&5&lGame Portal"));
		item.setItemMeta(meta);
		inv.setItem(1, item);

		// Option to edit player spawn
		item = new ItemStack(Material.PLAYER_HEAD);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&d&lPlayer Spawn"));
		item.setItemMeta(meta);
		inv.setItem(2, item);

		// Option to edit mob spawns
		item = new ItemStack(Material.ZOMBIE_HEAD);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&2&lMob Spawns"));
		item.setItemMeta(meta);
		inv.setItem(3, item);

		// Option to edit villager spawns
		item = new ItemStack(Material.POPPY);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&e&lVillager Spawns"));
		item.setItemMeta(meta);
		inv.setItem(4, item);

		// Option to edit miscellaneous game settings
		item = new ItemStack(Material.REDSTONE);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&7&lGame Settings"));
		item.setItemMeta(meta);
		inv.setItem(5, item);

		// Option to remove arena
		inv.setItem(7, ii.remove("ARENA"));

		// Option to exit
		item = ii.exit();
		inv.setItem(8, item);

		return inv;
	}

//	Confirmation menu for removing an arena
	public Inventory createConfirmInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format(REMOVE + plugin.getData().getString("data.a" + num + ".name") + '?'));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

//	Menu for editing the portal of an arena
	public Inventory createPortalInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&5&lPortal: " + plugin.getData().getString("data.a" + num + ".name") + ""));

		// Option to create the portal
		ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&2&lCreate Portal"));
		item.setItemMeta(meta);
		inv.setItem(0, item);

		// Option to teleport to the portal
		inv.setItem(1, ii.teleport("Portal"));

		// Option to remove the portal
		inv.setItem(7, ii.remove("PORTAL"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

//	Confirmation menu for removing the arena portal
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

//	Menu for editing the player spawn of an arena
	public Inventory createPlayerSpawnInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&d&lPlayer Spawn: " +
						plugin.getData().getString("data.a" + num + ".name") + ""));

		// Option to create player spawn
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&2&lCreate Spawn"));
		item.setItemMeta(meta);
		inv.setItem(0, item);

		// Option to teleport to player spawn
		inv.setItem(1, ii.teleport("Spawn"));

		// Option to remove player spawn
		inv.setItem(7, ii.remove("SPAWN"));

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

//	Menu for editing the mob spawns of an arena
	public Inventory createMobSpawnInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMob Spawns: " +
						plugin.getData().getString("data.a" + num + ".name") + ""));

		// Prepare for items and metas
		ItemStack item;
		ItemMeta meta;

		// Options to interact with all 8 possible mob spawns
		for (int i = 0; i < 8; i++) {
			// Check if the spawn exists
			if (plugin.getData().getString("data.a" + (num) + ".mob." + i + ".x") == null)
				item = new ItemStack(Material.SKELETON_SKULL);
			else item = new ItemStack(Material.ZOMBIE_HEAD);

			// Set name
			meta = item.getItemMeta();
			meta.setDisplayName(Utils.format("&2&lMob Spawn " + (i + 1)));

			// Set item in inventory
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

//	Menu for editing a specific mob spawn of an arena
	public Inventory createMobSpawnMenu(int num, int slot) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&2&lMob Spawn " + slot + ": " +
				plugin.getData().getString("data.a" + num + ".name")));

		// Option to create mob spawn
		ItemStack item = new ItemStack(Material.ZOMBIE_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&2&lCreate Spawn"));
		item.setItemMeta(meta);
		inv.setItem(0, item);

		// Option to teleport to mob spawn
		inv.setItem(1, ii.teleport("Spawn"));

		// Option to remove mob spawn
		inv.setItem(7, ii.remove("SPAWN"));

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

//	Confirmation menu for removing mob spawns
	public Inventory createMobSpawnConfirmInventory() {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&4&lRemove Mob Spawn?"));

		// "No" option
		inv.setItem(0, ii.no());

		// "Yes" option
		inv.setItem(8, ii.yes());

		return inv;
	}

//	Menu for editing the mob spawns of an arena
	public Inventory createArenaSettingsInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&8&lGame Settings: " + plugin.getData().getString("data.a" + num + ".name")));

		// Option to change max players
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&e&lMax Players"));
		item.setItemMeta(meta);
		inv.setItem(0, item);

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

//	Menu for changing max players in an arena
	public Inventory createMaxPlayerInventory(int num) {
		// Create inventory
		Inventory inv = Bukkit.createInventory(null, 9, Utils.format("&k") +
				Utils.format("&e&lMax Players: " + plugin.getData().getInt("data.a" + num + ".max")));

		// Option to decrease
		ItemStack item = new ItemStack(Material.RED_CONCRETE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&4&lDecrease"));
		for (int i = 0; i < 4; i++) {
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Option to increase
		item = new ItemStack(Material.LIME_CONCRETE);
		meta = item.getItemMeta();
		meta.setDisplayName(Utils.format("&2&lIncrease"));
		for (int i = 4; i < 8; i++) {
			item.setItemMeta(meta);
			inv.setItem(i, item);
		}

		// Option to exit
		inv.setItem(8, ii.exit());

		return inv;
	}

//	Generate the shop menu
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
