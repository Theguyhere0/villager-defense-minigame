package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class InventoryEvents implements Listener {
	private final Main plugin;
	private final Inventories inv;
	private final Portal portal;
	private int arena = 0; // Keeps track of which arena for many of the menus
	private int oldSlot = 0;
	private String old = ""; // Old name to revert name back if cancelled during naming
	private Boolean close = false; // Safe close toggle initialized to off
	
	public InventoryEvents (Main plugin, Inventories inv, Portal portal) {
		this.plugin = plugin;
		this.inv = inv;
		this.portal = portal;
	}

	// Prevent losing items by drag clicking in custom inventory
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Cancel the event
		e.setCancelled(true);
	}
	
//	All click events in the inventories
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Ignore null inventories
		if (e.getClickedInventory() == null)
			return;

		// Ignore clicks in player inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event
		e.setCancelled(true);

		ItemStack button = e.getCurrentItem();

		// Ignore clicks on nothing and remove NullPointerExceptions
		if (button == null || !button.hasItemMeta() || !button.getItemMeta().hasDisplayName())
			return;

		Material buttonType = button.getType();
		String buttonName = button.getItemMeta().getDisplayName();
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		int num = slot;

//		Arena inventory
		if (title.contains("Villager Defense Arenas")) {
			// Create new arena with naming inventory
			if (buttonType == Material.RED_CONCRETE)
				openInv(player, inv.createNamingInventory(slot));

			// Edit existing arena
			else if (buttonType == Material.LIME_CONCRETE)
				openInv(player, inv.createArenaInventory(slot));

			// Set new lobby
			else if (buttonName.contains("Set")) {
				if (!plugin.getData().contains("lobby")) {
					plugin.getData().set("lobby.x", player.getLocation().getX());
					plugin.getData().set("lobby.y", player.getLocation().getY());
					plugin.getData().set("lobby.z", player.getLocation().getZ());
					plugin.getData().set("lobby.world", player.getLocation().getWorld().getName());
					plugin.saveData();
					player.sendMessage(Utils.format("&aLobby set!"));
					player.closeInventory();
				}
			}

			// Teleport to lobby
			else if (buttonName.contains("Teleport")) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("lobby.world")),
							plugin.getData().getDouble("lobby.x"),
							plugin.getData().getDouble("lobby.y"),
							plugin.getData().getDouble("lobby.z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Confirm to delete lobby
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createLobbyConfirmInventory());

			// Close inventory
			else if (buttonName.contains("EXIT"))
				player.closeInventory();

			arena = slot;
			old = plugin.getData().getString("a" + arena + ".name");
		}
		
//		Naming inventory
		else if (title.contains("Arena")) {
			// Get name of arena
			String name = plugin.getData().getString("a" + arena + ".name");

			// If no name exists, set to nothing
			if (name == null)
				name = "";

			// Check for caps lock toggle
			boolean caps = plugin.getData().getBoolean("a" + arena + ".caps");
			if (caps)
				num += 36;
			
//			Letters and numbers
			if (Arrays.asList(Inventories.KEYMATS).contains(buttonType)){
				plugin.getData().set("a" + arena + ".name", name + Inventories.NAMES[num]);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Spaces
			else if (buttonName.contains("Space")){
				plugin.getData().set("a" + arena + ".name", name + Inventories.NAMES[72]);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Caps lock
			else if (buttonName.contains("CAPS LOCK")) {
				plugin.getData().set("a" + arena + ".caps", !caps);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Backspace
			else if (buttonName.contains("Backspace")) {
				if (name.length() == 0)
					return;
				plugin.getData().set("a" + arena + ".name", name.substring(0, name.length() - 1));
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Save
			else if (buttonName.contains("SAVE") && name.length() > 0) {
				openInv(player, inv.createArenasInventory());
				old = plugin.getData().getString("a" + arena + ".name");
				// Recreate portal if it exists
				if (plugin.getData().contains("portal." + arena))
					portal.refreshHolo(arena);

				// Set default max players to 12 if it doesn't exist
				if (!plugin.getData().contains("a" + arena + ".max")) {
					plugin.getData().set("a" + arena + ".max", 12);
					plugin.saveData();
				}

				// Set default min players to 1 if it doesn't exist
				if (!plugin.getData().contains("a" + arena + ".min")) {
					plugin.getData().set("a" + arena + ".min", 1);
					plugin.saveData();
				}

			}

//			Cancel
			else if (buttonName.contains("CANCEL")) {
				plugin.getData().set("a" + arena + ".name", old);
				openInv(player, inv.createArenasInventory());
			}
		}
		
//		Menu for an arena
		else if (title.contains(Utils.format("&2&lEdit "))) {

			// Open name editor
			if (buttonName.contains("Edit Name"))
				openInv(player, inv.createNamingInventory(arena));

			// Open portal menu
			else if (buttonName.contains("Game Portal"))
				openInv(player, inv.createPortalInventory(arena));

			// Open player menu
			else if (buttonName.contains("Player Settings"))
				openInv(player, inv.createPlayersInventory(arena));

			// Open mob menu
			else if (buttonName.contains("Mob Settings"))
				openInv(player, inv.createMobsInventory(arena));

			// Open shop menu
			else if (buttonName.contains("Shop Settings"))
				openInv(player, inv.createShopsInventory(arena));

			// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				openInv(player, inv.createGameSettingsInventory(arena));

			// Toggle arena close
			else if (buttonName.contains("Close")) {
				// Arena currently closed
				if (plugin.getData().getBoolean("a" + arena + ".closed")) {
					// No arena portal
					if (!plugin.getData().contains("portal." + arena))
						return;

					// No player spawn
					if (!plugin.getData().contains("a" + arena + ".spawn"))
						return;

					// No mob spawn
					if (!plugin.getData().contains("a" + arena + ".mob"))
						return;

					// Open arena
					plugin.getData().set("a" + arena + ".closed", false);
				}

				// Arena currently open
				else plugin.getData().set("a" + arena + ".closed", true);
				plugin.saveData();
				openInv(player, inv.createArenaInventory(arena));
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createArenaConfirmInventory(arena));

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}
		
//		Confirmation menus
		else if (title.contains("Remove")) {
//			Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES") && plugin.getData().contains("portal." + arena)) {
					// Remove portal data, close arena
					plugin.getData().set("portal." + arena, null);
					plugin.getData().set("a" + arena + ".closed", true);
					plugin.saveData();

					// Remove Portal
					portal.removePortalAll(arena);

					// Confirm and return
					player.sendMessage(Utils.format("&aPortal removed!"));
					openInv(player, inv.createPortalInventory(arena));
				}
			}

//			Confirm to remove spawn
			else if (title.contains("Remove Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPlayerSpawnInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES") && plugin.getData().contains("a" + arena + ".spawn")) {
					plugin.getData().set("a" + arena + ".spawn", null);
					plugin.getData().set("a" + arena + ".closed", true);
					plugin.saveData();
					player.sendMessage(Utils.format("&aSpawn removed!"));
					openInv(player, inv.createPlayerSpawnInventory(arena));
				}
			}

//			Confirm to remove mob spawn
			else if (title.contains("Remove Monster Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));

				// Remove the mob spawn, then return to previous menu
				else if (buttonName.contains("YES") && plugin.getData().contains("a" + arena + ".mob." + oldSlot)) {
					plugin.getData().set("a" + arena + ".mob." + oldSlot, null);
					if (!plugin.getData().contains("a" + arena + ".mob"))
						plugin.getData().set("a" + arena + ".closed", true);
					plugin.saveData();
					player.sendMessage(Utils.format("&aMob spawn removed!"));
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));
				}
			}

//			Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createArenasInventory());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES") && plugin.getData().contains("lobby")) {
					plugin.getData().set("lobby", null);
					plugin.saveData();
					player.sendMessage(Utils.format("&aLobby removed!"));
					openInv(player, inv.createArenasInventory());
				}
			}

//			Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createArenaInventory(arena));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove data
					plugin.getData().set("a" + arena, null);
					plugin.getData().set("portal." + arena, null);
					plugin.saveData();

					// Remove portal
					portal.removePortalAll(arena);

					// Confirm and return
					player.sendMessage(Utils.format("&aArena removed!"));
					openInv(player, inv.createArenasInventory());
					close = true;
				}
			}
		}
		
//		Portal menu for an arena
		else if (title.contains("Portal:")) {
//			Create portal, then return to previous menu
			if (buttonName.contains("Create Portal")) {
				if (!plugin.getData().contains("portal." + arena)) {
					portal.createPortal(player, arena);
					player.sendMessage(Utils.format("&aPortal set!"));
					openInv(player, inv.createArenaInventory(arena));
				}
			}

//			Teleport player to portal
			else if (buttonName.contains("Teleport")) {
				Location location;
				try {
					location = new Location(
							Bukkit.getWorld(plugin.getData().getString("portal." + arena + ".world")),
							plugin.getData().getDouble("portal." + arena + ".x"),
							plugin.getData().getDouble("portal." + arena + ".y"),
							plugin.getData().getDouble("portal." + arena + ".z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

//			Remove portal
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createPortalConfirmInventory());

//			Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				openInv(player, inv.createPlayerSpawnInventory(arena));

			// Toggle player spawn particles
//			else if (buttonName.contains("Toggle"))

			// Edit max players
			else if (buttonName.contains("Maximum"))
				openInv(player, inv.createMaxPlayerInventory(arena));

			// Edit min players
			else if (buttonName.contains("Minimum"))
				openInv(player, inv.createMinPlayerInventory(arena));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}
		
//		Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
//			Create spawn, then return to previous menu
			if (buttonName.contains("Create Spawn")) {
				plugin.getData().set("a" + arena + ".spawn.x", player.getLocation().getX());
				plugin.getData().set("a" + arena + ".spawn.y", player.getLocation().getY());
				plugin.getData().set("a" + arena + ".spawn.z", player.getLocation().getZ());
				plugin.getData().set("a" + arena + ".spawn.world", player.getLocation().getWorld().getName());
				plugin.saveData();
				player.sendMessage(Utils.format("&aSpawn set!"));
				openInv(player, inv.createPlayersInventory(arena));
			}

//			Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
							plugin.getData().getDouble("a" + arena + ".spawn.x"),
							plugin.getData().getDouble("a" + arena + ".spawn.y"),
							plugin.getData().getDouble("a" + arena + ".spawn.z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

//			Remove spawn
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createSpawnConfirmInventory());

//			Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
//			Decrease max players
			if (buttonName.contains("Decrease") && plugin.getData().getInt("a" + arena + ".max") > 1) {
				plugin.getData().set("a" + arena + ".max", plugin.getData().getInt("a" + arena + ".max") - 1);
				plugin.saveData();
				openInv(player, inv.createMaxPlayerInventory(arena));
			}

//			Increase max players
			else if (buttonName.contains("Increase")) {
				plugin.getData().set("a" + arena + ".max", plugin.getData().getInt("a" + arena + ".max") + 1);
				plugin.saveData();
				openInv(player, inv.createMaxPlayerInventory(arena));
			}

//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createPlayersInventory(arena));
			}
		}

		// Max player menu for an arena
		else if (title.contains("Minimum Players:")) {
//			Decrease max players
			if (buttonName.contains("Decrease") && plugin.getData().getInt("a" + arena + ".min") > 1) {
				plugin.getData().set("a" + arena + ".min", plugin.getData().getInt("a" + arena + ".min") - 1);
				plugin.saveData();
				portal.refreshHolo(arena);
				openInv(player, inv.createMinPlayerInventory(arena));
			}

//			Increase max players
			else if (buttonName.contains("Increase")) {
				plugin.getData().set("a" + arena + ".min", plugin.getData().getInt("a" + arena + ".min") + 1);
				plugin.saveData();
				portal.refreshHolo(arena);
				openInv(player, inv.createMinPlayerInventory(arena));
			}

//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createPlayersInventory(arena));
			}
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				openInv(player, inv.createMonsterSpawnInventory(arena));

			// Toggle monster spawn particles
//			else if (buttonName.contains("Toggle Monster"))

			// Open villager spawns editor
//			else if (buttonName.contains("Villager Spawns"))

			// Toggle villager spawn particles
//			else if (buttonName.contains("Toggle Villager"))

			// Edit monsters allowed
//			else if (buttonName.contains("Monsters Allowed")

			// Toggle dynamic monster count
//			else if (buttonName.contains("Toggle Dynamic Monster"))

			// Toggle dynamic difficulty
//			else if (buttonName.contains("Toggle Dynamic Difficulty"))

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

//		Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
//			Create spawn
			if (Arrays.asList(Inventories.MONSTERMATS).contains(buttonType)) {
				openInv(player, inv.createMonsterSpawnMenu(arena, slot));
				oldSlot = slot;
			}

//			Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createMobsInventory(arena));
			}
		}

//		Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
//			Create spawn
			if (buttonName.contains("Create Spawn")) {
				plugin.getData().set("a" + arena + ".mob." + oldSlot + ".x", player.getLocation().getX());
				plugin.getData().set("a" + arena + ".mob." + oldSlot + ".y", player.getLocation().getY());
				plugin.getData().set("a" + arena + ".mob." + oldSlot + ".z", player.getLocation().getZ());
				plugin.getData().set("a" + arena + ".mob." + oldSlot + ".world", player.getLocation().getWorld().getName());
				plugin.saveData();
				player.sendMessage(Utils.format("&aMonster spawn set!"));
				openInv(player, inv.createMonsterSpawnInventory(arena));
			}

//			Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("a" + arena + ".mob." + oldSlot + ".world")),
							plugin.getData().getDouble("a" + arena + ".mob." + oldSlot + ".x"), plugin.getData().getDouble("a" + arena + ".mob." + oldSlot + ".y"),
							plugin.getData().getDouble("a" + arena + ".mob." + oldSlot + ".z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

//			Remove spawn
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createMonsterSpawnConfirmInventory());

//			Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createMonsterSpawnInventory(arena));
		}

		// Shop settings menu for an arena
		else if (title.contains("Shop Settings:")) {
			// Open custom shop editor
//			if (buttonName.contains("Create"))

			// Toggle default shop
//			else if (buttonName.contains("Toggle Default Shop"))

			// Toggle custom shop
//			else if (buttonName.contains("Toggle Custom Shop"))

			// Toggle dynamic prices
//			else if (buttonName.contains("Toggle Dynamic"))

			// Exit menu
//			else if (buttonName.contains("EXIT"))
			if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

//		Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			// Change max rounds
//			if (buttonName.contains("Max Rounds"))

			// Change round time limit
//			else if (buttonName.contains("Round Time Limit"))

			// Edit allowed kits
//			else if (buttonName.contains("Allowed Kits"))

			// Edit persistent rewards
//			else if (buttonName.contains("Persistent Rewards"))

			// Edit sounds
//			else if (buttonName.contains("Sounds"))
			if (buttonName.contains("Sounds"))
				openInv(player, inv.createSoundsInventory(arena));

			// Copy game settings from another arena
//			else if (buttonName.contains("Copy Game Settings"))

			// Copy arena settings from another arena
//			else if (buttonName.contains("Copy Arena Settings"))

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			// Edit win sound
//			if (buttonName.contains("Win"))

			// Edit lose sound
//			else if (buttonName.contains("Lose"))

			// Edit round start sound
//			else if (buttonName.contains("Start"))

			// Edit round finish sound
//			else if (buttonName.contains("Finish"))

			// Edit waiting music
//			else if (buttonName.contains("Waiting"))

			// Exit menu
//			else if (buttonName.contains("EXIT"))
			if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}
	}
	
//	Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		// Ignore if safe close toggle is on
		if (close)
			return;

		// Close safely for the inventory of concern
		if (e.getView().getTitle().contains("Arena")) {
			plugin.getData().set("a" + arena + ".name", old);
			plugin.saveData();
		}
	}
	
//	Ensures safely opening inventories
	private void openInv(Player player, Inventory inventory) {
		// Set safe close toggle on
		close = true;
		// Open the desired inventory
		player.openInventory(inventory);
		// Set safe close toggle to off
		close = false;
	}
}
