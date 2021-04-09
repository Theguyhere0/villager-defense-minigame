package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.Arena;
import me.theguyhere.villagerdefense.game.Game;
import me.theguyhere.villagerdefense.game.Portal;
import me.theguyhere.villagerdefense.game.Tasks;
import me.theguyhere.villagerdefense.tools.Utils;
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
	private final Game game;
	private final Inventories inv;
	private final Portal portal;
	private final Utils utils;
	private int arena = 0; // Keeps track of which arena for many of the menus
	private int oldSlot = 0;
	private String old = ""; // Old name to revert name back if cancelled during naming
	private boolean close; // Safe close toggle initialized to off
	
	public InventoryEvents (Main plugin, Game game, Inventories inv, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.inv = inv;
		this.portal = portal;
		utils = new Utils(plugin);
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

		// Arena inventory
		if (title.contains("Villager Defense Arenas")) {
			// Create new arena with naming inventory
			if (buttonType == Material.RED_CONCRETE) {
				game.arenas.set(slot, new Arena(plugin, slot, new Tasks(plugin, game, slot, portal)));
				openInv(player, inv.createNamingInventory(slot));
			}

			// Edit existing arena
			else if (buttonType == Material.LIME_CONCRETE)
				openInv(player, inv.createArenaInventory(slot));

			// Open lobby menu
			else if (buttonName.contains("Lobby"))
				openInv(player, inv.createLobbyInventory());

			// Close inventory
			else if (buttonName.contains("EXIT"))
				player.closeInventory();

			arena = slot;
			old = plugin.getData().getString("a" + arena + ".name");
		}

		// Lobby menu
		else if (title.contains(Utils.format("&2&lLobby"))) {
			String path = "lobby";

			if (buttonName.contains("Create Lobby"))
				// Create lobby, then return to previous menu
				if (!plugin.getData().contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.reloadLobby();
					player.sendMessage(Utils.notify("&aLobby set!"));
					player.closeInventory();
				} else player.sendMessage(Utils.notify("&cLobby already exists!"));

			// Teleport player to lobby
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo lobby to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center lobby
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo lobby to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				player.sendMessage(Utils.notify("&aLobby centered!"));
			}

			// Remove lobby
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createLobbyConfirmInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Naming inventory
		else if (title.contains("Arena ")) {
			Arena arenaInstance = game.arenas.get(arena);
			// Get name of arena
			String name = plugin.getData().getString("a" + arena + ".name");

			// If no name exists, set to nothing
			if (name == null)
				name = "";

			// Check for caps lock toggle
			boolean caps = arenaInstance.isCaps();
			if (caps)
				num += 36;

			// Letters and numbers
			if (Arrays.asList(Inventories.KEYMATS).contains(buttonType)){
				plugin.getData().set("a" + arena + ".name", name + Inventories.NAMES[num]);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createNamingInventory(arena));
			}

			// Spaces
			else if (buttonName.contains("Space")){
				plugin.getData().set("a" + arena + ".name", name + Inventories.NAMES[72]);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createNamingInventory(arena));
			}

			// Caps lock
			else if (buttonName.contains("CAPS LOCK")) {
				arenaInstance.flipCaps();
				openInv(player, inv.createNamingInventory(arena));
			}

			// Backspace
			else if (buttonName.contains("Backspace")) {
				if (name.length() == 0)
					return;
				plugin.getData().set("a" + arena + ".name", name.substring(0, name.length() - 1));
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createNamingInventory(arena));
			}

			// Save
			else if (buttonName.contains("SAVE")) {
				// Check if name is not empty
				if (name.length() <= 0) {
					player.sendMessage(Utils.notify("&cName cannot be empty!"));
					return;
				}

				openInv(player, inv.createArenasInventory());
				old = plugin.getData().getString("a" + arena + ".name");
				game.arenas.get(arena).setName(old);

				// Recreate portal if it exists
				if (plugin.getData().contains("portal." + arena))
					portal.refreshHolo(arena, game);

				// Set default max players to 12 if it doesn't exist
				if (!plugin.getData().contains("a" + arena + ".max")) {
					plugin.getData().set("a" + arena + ".max", 12);
				}

				// Set default min players to 1 if it doesn't exist
				if (!plugin.getData().contains("a" + arena + ".min")) {
					plugin.getData().set("a" + arena + ".min", 1);
				}

				// Set default to closed if arena closed doesn't exist
				if (!plugin.getData().contains("a" + arena + ".closed")) {
					plugin.getData().set("a" + arena + ".closed", true);
				}

				plugin.saveData();
				game.arenas.get(arena).updateArena();
				arenaInstance.updateArena();
			}

			// Cancel
			else if (buttonName.contains("CANCEL")) {
				plugin.getData().set("a" + arena + ".name", old);
				plugin.saveData();
				if (old == null)
					game.arenas.set(arena, null);
				openInv(player, inv.createArenasInventory());
			}
		}

		// Menu for an arena
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
					// No lobby
					if (!plugin.getData().contains("lobby")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a lobby!"));
						return;
					}

					// No arena portal
					if (!plugin.getData().contains("portal." + arena)) {
						player.sendMessage(Utils.notify("&cArena cannot open without a portal!"));
						return;
					}

					// No player spawn
					if (!plugin.getData().contains("a" + arena + ".spawn")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a player spawn!"));
						return;
					}

					// No monster spawn
					if (!plugin.getData().contains("a" + arena + ".monster")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a monster spawn!"));
						return;
					}

					// No villager spawn
					if (!plugin.getData().contains("a" + arena + ".villager")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a villager spawn!"));
						return;
					}

					// Open arena
					plugin.getData().set("a" + arena + ".closed", false);
				}

				// Arena currently open
				else plugin.getData().set("a" + arena + ".closed", true);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createArenaInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createArenaConfirmInventory(arena));

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				String path = "portal." + arena;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains(path)) {
						// Remove portal data, close arena
						plugin.getData().set(path, null);
						plugin.getData().set("a" + arena + ".closed", true);
						plugin.saveData();
						game.arenas.get(arena).updateArena();

						// Remove Portal
						portal.removePortalAll(arena);

						// Confirm and return
						player.sendMessage(Utils.notify("&aPortal removed!"));
						openInv(player, inv.createPortalInventory(arena));
					} else player.sendMessage(Utils.notify("&cNo portal to remove!"));
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				String path = "a" + arena + ".spawn";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPlayerSpawnInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains(path)) {
						plugin.getData().set(path, null);
						plugin.getData().set("a" + arena + ".closed", true);
						plugin.saveData();
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aSpawn removed!"));
						openInv(player, inv.createPlayerSpawnInventory(arena));
						portal.refreshHolo(arena, game);
					} else player.sendMessage(Utils.notify("&cNo player spawn to remove!"));
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				String path = "a" + arena + ".waiting";
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createWaitingRoomInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains(path)) {
						plugin.getData().set(path, null);
						plugin.saveData();
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aWaiting room removed!"));
						openInv(player, inv.createWaitingRoomInventory(arena));
						portal.refreshHolo(arena, game);
					} else player.sendMessage(Utils.notify("&cNo waiting room to remove!"));
			}

			// Confirm to remove monster spawn
			else if (title.contains("Remove Monster Spawn?")) {
				String path = "a" + arena + ".monster." + oldSlot;
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));

				// Remove the monster spawn, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains(path)) {
						plugin.getData().set(path, null);
						if (!plugin.getData().contains("a" + arena + ".monster"))
							plugin.getData().set("a" + arena + ".closed", true);
						plugin.saveData();
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aMob spawn removed!"));
						openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));
						portal.refreshHolo(arena, game);
					} else player.sendMessage(Utils.notify("&cNo monster spawn to remove!"));
			}

			// Confirm to remove villager spawn
			else if (title.contains("Remove Villager Spawn?")) {
				String path = "a" + arena + ".villager." + oldSlot;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createVillagerSpawnMenu(arena, oldSlot));

				// Remove the villager spawn, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains(path)) {
						plugin.getData().set(path, null);
						if (!plugin.getData().contains("a" + arena + ".villager"))
							plugin.getData().set("a" + arena + ".closed", true);
						plugin.saveData();
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aMob spawn removed!"));
						openInv(player, inv.createVillagerSpawnMenu(arena, oldSlot));
						portal.refreshHolo(arena, game);
					} else player.sendMessage(Utils.notify("&cNo villager spawn to remove!"));
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createLobbyInventory());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES"))
					if (plugin.getData().contains("lobby")) {
						plugin.getData().set("lobby", null);
						plugin.saveData();
						game.reloadLobby();
						player.sendMessage(Utils.notify("&aLobby removed!"));
						openInv(player, inv.createLobbyInventory());
					} else player.sendMessage(Utils.notify("&cNo lobby to remove!"));
			}

			// Confirm to remove arena
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
					game.arenas.set(arena, null);

					// Remove portal
					portal.removePortalAll(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aArena removed!"));
					openInv(player, inv.createArenasInventory());
					close = true;
				}
			}
		}

		// Portal menu for an arena
		else if (title.contains("Portal:")) {
			String path = "portal." + arena;

			// Create portal, then return to previous menu
			if (buttonName.contains("Create Portal"))
				if (!plugin.getData().contains(path)) {
					portal.createPortal(player, arena, game);
					player.sendMessage(Utils.notify("&aPortal set!"));
					openInv(player, inv.createArenaInventory(arena));
				} else player.sendMessage(Utils.notify("&cPortal already exists!"));

			// Teleport player to portal
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo portal to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center portal
			else if (buttonName.contains("Center")) {
				if (utils.getConfigLocationNoPitch(path) == null) {
					player.sendMessage(Utils.notify("&cNo portal to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				portal.removePortalAll(arena);
				portal.loadPortal(utils.getConfigLocationNoPitch(path), arena, game);
				player.sendMessage(Utils.notify("&aPortal centered!"));
			}

			// Remove portal
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createPortalConfirmInventory());

			// Exit menu
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

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				openInv(player, inv.createWaitingRoomInventory(arena));

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

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			String path = "a" + arena + ".spawn";

			// Create spawn, then return to previous menu
			if (buttonName.contains("Create"))
				if (!plugin.getData().contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.arenas.get(arena).updateArena();
					player.sendMessage(Utils.notify("&aSpawn set!"));
					openInv(player, inv.createPlayersInventory(arena));
				} else player.sendMessage(Utils.notify("&cPlayer spawn already exists!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo player spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center player spawn
			else if (buttonName.contains("Center")) {
				if (utils.getConfigLocationNoRotation(path) == null) {
					player.sendMessage(Utils.notify("&cNo player spawn to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				player.sendMessage(Utils.notify("&aSpawn centered!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createSpawnConfirmInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			String path = "a" + arena + ".waiting";

			// Create waiting room, then return to previous menu
			if (buttonName.contains("Create"))
				if (!plugin.getData().contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.arenas.get(arena).updateArena();
					player.sendMessage(Utils.notify("&aWaiting room set!"));
					openInv(player, inv.createPlayersInventory(arena));
				} else player.sendMessage(Utils.notify("&cWaiting room already exists!"));

			// Teleport player to waiting room
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo waiting room to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center waiting room
			else if (buttonName.contains("Center")) {
				if (utils.getConfigLocationNoRotation(path) == null) {
					player.sendMessage(Utils.notify("&cNo waiting room to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				player.sendMessage(Utils.notify("&aWaiting room centered!"));
			}

			// Remove waiting room
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createWaitingConfirmInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			int current = plugin.getData().getInt("a" + arena + ".max");

			// Decrease max players
			if (buttonName.contains("Decrease")) {
				// Check if max players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than 1!"));
					return;
				}

				// Check if max players is greater than min players
				if (current <= plugin.getData().getInt("a" + arena + ".min")) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than min player!"));
					return;
				}

				plugin.getData().set("a" + arena + ".max", current - 1);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				plugin.getData().set("a" + arena + ".max", current + 1);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8) {
				openInv(player, inv.createPlayersInventory(arena));
			}
		}

		// Max player menu for an arena
		else if (title.contains("Minimum Players:")) {
			int current = plugin.getData().getInt("a" + arena + ".min");

			// Decrease max players
			if (buttonName.contains("Decrease")) {
				// Check if min players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMin players cannot be less than 1!"));
					return;
				}

				plugin.getData().set("a" + arena + ".min", current - 1);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createMinPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check if min players is less than max players
				if (current >= plugin.getData().getInt("a" + arena + ".max")) {
					player.sendMessage(Utils.notify("&cMin players cannot be greater than max player!"));
					return;
				}

				plugin.getData().set("a" + arena + ".min", current + 1);
				plugin.saveData();
				game.arenas.get(arena).updateArena();
				openInv(player, inv.createMinPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
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
			else if (buttonName.contains("Villager Spawns"))
				openInv(player, inv.createVillagerSpawnInventory(arena));

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

		// Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
			// Create spawn
			if (Arrays.asList(Inventories.MONSTERMATS).contains(buttonType)) {
				openInv(player, inv.createMonsterSpawnMenu(arena, slot));
				oldSlot = slot;
			}

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createMobsInventory(arena));
			}
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			String path = "a" + arena + ".monster." + oldSlot;

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (!plugin.getData().contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.arenas.get(arena).updateArena();
					player.sendMessage(Utils.notify("&aMonster spawn set!"));
					openInv(player, inv.createMonsterSpawnInventory(arena));
				} else player.sendMessage(Utils.notify("&cMonster spawn already exists!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo monster spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (utils.getConfigLocationNoRotation(path) == null) {
					player.sendMessage(Utils.notify("&cNo monster spawn to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				player.sendMessage(Utils.notify("&aMonster spawn centered!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createMonsterSpawnConfirmInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createMonsterSpawnInventory(arena));
		}

		// Villager spawn menu for an arena
		else if (title.contains("Villager Spawns:")) {
			// Create spawn
			if (Arrays.asList(Inventories.VILLAGERMATS).contains(buttonType)) {
				openInv(player, inv.createVillagerSpawnMenu(arena, slot));
				oldSlot = slot;
			}

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createMobsInventory(arena));
			}
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			String path = "a" + arena + ".villager." + oldSlot;

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (!plugin.getData().contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.arenas.get(arena).updateArena();
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
					openInv(player, inv.createVillagerSpawnInventory(arena));
				} else player.sendMessage(Utils.notify("&cVillager spawn already exists!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo villager spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (utils.getConfigLocationNoRotation(path) == null) {
					player.sendMessage(Utils.notify("&cNo villager spawn to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				player.sendMessage(Utils.notify("&aVillager spawn centered!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				openInv(player, inv.createVillagerSpawnConfirmInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createVillagerSpawnInventory(arena));
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

		// Game settings menu for an arena
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
				openInv(player, inv.createGameSettingsInventory(arena));
		}
	}
	
	// Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Ignore if safe close toggle is on
		if (close)
			return;

		// Close safely for the inventory of concern
		if (title.contains("Arena ")) {
			plugin.getData().set("a" + arena + ".name", old);
			plugin.saveData();
			if (old == null)
				game.arenas.set(arena, null);
		}
	}
	
	// Ensures safely opening inventories
	private void openInv(Player player, Inventory inventory) {
		// Set safe close toggle on
		close = true;

		// Open the desired inventory
		player.openInventory(inventory);

		// Set safe close toggle to off
		close = false;
	}
}
