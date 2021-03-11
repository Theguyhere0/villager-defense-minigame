package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.*;
import net.minecraft.server.v1_16_R3.EntityPlayer;
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

public class InventoryEvents implements Listener {
	private final Main plugin;
	private final Inventories inv;
	private final PacketReader reader;
	private final NPC npc;
	private int arena = 0;
	private int oldSlot = 0;
	private String old = "";
	private Boolean close = false; // Safe close toggle initialized to off
	
	public InventoryEvents (Main plugin, Inventories inv, NPC npc, PacketReader reader) {
		this.plugin = plugin;
		this.inv = inv;
		this.npc = npc;
		this.reader = reader;
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

		// Ignore clicks in player inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event
		e.setCancelled(true);

		// Ignore clicks on nothing
		if (e.getCurrentItem() == null)
			return;

		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		int num = slot;

//		Arena inventory
		if (title.contains(Utils.format(inv.ARENAINV))) {
			// Create new arena with naming inventory
			if (e.getCurrentItem().getType() == Material.RED_CONCRETE)
				openInv(player, inv.createNamingInventory(slot));
			// Edit existing arena
			else if (e.getCurrentItem().getType() == Material.LIME_CONCRETE)
				openInv(player, inv.createEditInventory(slot));
			// Set new lobby
			else if (slot == 45) {
				if (!plugin.getData().contains("data.lobby")) {
					plugin.getData().set("data.lobby.x", player.getLocation().getX());
					plugin.getData().set("data.lobby.y", player.getLocation().getY());
					plugin.getData().set("data.lobby.z", player.getLocation().getZ());
					plugin.getData().set("data.lobby.world", player.getLocation().getWorld().getName());
					plugin.saveData();
					player.sendMessage(Utils.format("&aLobby set!"));
					player.closeInventory();
				}
			// Teleport to lobby
			} else if (slot == 46) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("data.lobby.world")),
							plugin.getData().getDouble("data.lobby.x"),
							plugin.getData().getDouble("data.lobby.y"),
							plugin.getData().getDouble("data.lobby.z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			// Confirm to delete lobby
			} else if (slot == 52) {
				openInv(player, inv.createLobbyConfirmInventory());
			// Close inventory
			} else if (slot == 53) {
				player.closeInventory();
			}
			arena = slot;
			old = plugin.getData().getString("data.a" + arena + ".name");
		}
		
//		Naming inventory
		else if (e.getView().getTitle().contains(Utils.format(inv.ARENA))) {
			// Get name of arena
			String name = plugin.getData().getString("data.a" + arena + ".name");

			// If no name exists, set to nothing
			if (name == null)
				name = "";

			// Check for caps lock toggle
			boolean caps = plugin.getData().getBoolean("data.a" + arena + ".caps");
			if (caps)
				num += 36;
			
//			Letters and numbers
			if (slot < 36){
				plugin.getData().set("data.a" + arena + ".name", name + inv.NAMES[num]);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Spaces
			else if (slot < 45){
				plugin.getData().set("data.a" + arena + ".name", name + inv.NAMES[72]);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Caps lock
			else if (slot == 45) {
				plugin.getData().set("data.a" + arena + ".caps", !caps);
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Backspace
			else if (slot == 46) {
				if (name.length() == 0)
					return;
				plugin.getData().set("data.a" + arena + ".name", name.substring(0, name.length() - 1));
				plugin.saveData();
				openInv(player, inv.createNamingInventory(arena));
			}

//			Save
			else if (slot == 52 && name.length() > 0) {
				openInv(player, inv.createArenaInventory());
				// Recreate portal if it exists
				if (plugin.getData().contains("data.portal." + arena)) {
					plugin.removeHolo(Integer.toString(arena));
					plugin.spawnHolo(Integer.toString(arena));
				}
				// Set default max players to 12 if it doesn't exist
				if (!plugin.getData().contains("data.a" + arena + ".max")) {
					plugin.getData().set("data.a" + arena + ".max", 12);
					plugin.saveData();
				}
			}

//			Cancel
			else if (slot == 53) {
				plugin.getData().set("data.a" + arena + ".name", old);
				openInv(player, inv.createArenaInventory());
			}
		}
		
//		Menu for an arena
		else if (e.getView().getTitle().contains(Utils.format(inv.EDIT1))) {

//			Open name editor
			if (slot == 0)
				openInv(player, inv.createNamingInventory(arena));

//			Open portal menu
			else if (slot == 1)
				openInv(player, inv.createPortalInventory(arena));

//			Open player spawn menu
			else if (slot == 2)
				openInv(player, inv.createPlayerSpawnInventory(arena));

//			Open mob spawns menu
			else if (slot == 3)
				openInv(player, inv.createMobSpawnInventory(arena));

//			Open villager spawns menu
			else if (slot == 4)
				openInv(player, inv.createMobSpawnInventory(arena));

//			Open game settings menu
			else if (slot == 5)
				openInv(player, inv.createArenaSettingsInventory(arena));

//			Open arena remove confirmation menu
			else if (slot == 7)
				openInv(player, inv.createConfirmInventory(arena));

//			Return to arenas menu
			else if (slot == 8)
				openInv(player, inv.createArenaInventory());
		}
		
//		Confirmation menus
		else if (e.getView().getTitle().contains(Utils.format("&4&lRemove "))) {
//			Confirm to remove portal
			if (e.getView().getTitle().contains(Utils.format("&4&lRemove Portal?"))) {
				// Return to previous menu
				if (slot == 0)
					openInv(player, inv.createPortalInventory(arena));
				// Remove the portal and return to previous menu
				else if (slot == 8 && plugin.getData().contains("data.portal." + arena)) {
					plugin.getData().set("data.portal." + arena, null);
					plugin.saveData();
					plugin.removeHolo(Integer.toString(arena));
					for (Player p : Bukkit.getOnlinePlayers()) {
						reader.uninject(p);
						for (EntityPlayer NPC : npc.getNPCs())
							npc.removeNPC(p, NPC);
					}
					openInv(player, inv.createPortalInventory(arena));
				}
			}

//			Confirm to remove spawn
			else if (e.getView().getTitle().contains(Utils.format("&4&lRemove Spawn?"))) {
				if (slot == 0) {
					openInv(player, inv.createPlayerSpawnInventory(arena));
				}
				else if (slot == 8 && plugin.getData().contains("data.a" + arena + ".spawn")) {
						plugin.getData().set("data.a" + arena + ".spawn", null);
						plugin.saveData();
						player.sendMessage(Utils.format("&aSpawn removed!"));
						player.closeInventory();
				}
			}

//			Confirm to remove mob spawn
			else if (e.getView().getTitle().contains(Utils.format("&4&lRemove Mob Spawn?"))) {
				if (slot == 0) {
					openInv(player, inv.createMobSpawnMenu(arena, oldSlot));
				}
				else if (slot == 8 && plugin.getData().contains("data.a" + arena + ".mob." + oldSlot)) {
						plugin.getData().set("data.a" + arena + ".mob." + oldSlot, null);
						plugin.saveData();
						player.sendMessage(Utils.format("&aMob spawn removed!"));
						player.closeInventory();
				}
			}

//			Confirm to remove lobby
			else if (e.getView().getTitle().contains(Utils.format(inv.LOBBYCONFIRMINV))) {
				if (slot == 0) {
					openInv(player, inv.createArenaInventory());
				}
				else if (slot == 8 && plugin.getData().contains("data.lobby")) {
					plugin.getData().set("data.lobby", null);
					plugin.saveData();
					player.sendMessage(Utils.format("&aLobby removed!"));
					player.closeInventory();
				}
			}
//			Confirm to remove arena
			else {
				if (slot == 0) {
					openInv(player, inv.createEditInventory(arena));
					return;
				}
				else if (slot == 8) {
					plugin.getData().set("data.a" + arena, null);
					plugin.getData().set("data.portal." + arena, null);
					plugin.saveData();
					plugin.removeHolo(Integer.toString(arena));
					openInv(player, inv.createArenaInventory());
					return;
				}
			}
		}
		
//		Portal menu for an arena
		else if (e.getView().getTitle().contains(Utils.format("&5&lPortal: "))) {
//			Create arena
			if (slot == 0) {
				if (!plugin.getData().contains("data.portal." + arena)) {
					npc.createNPC(player, arena);
					plugin.spawnHolo(Integer.toString(arena));
					player.sendMessage(Utils.format("&aPortal set!"));
					player.closeInventory();
				}
			}
//			Teleport player to portal
			else if (slot == 1) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("data.portal." + arena + ".world")),
							plugin.getData().getDouble("data.portal." + arena + ".x"), plugin.getData().getDouble("data.portal." + arena + ".y"),
							plugin.getData().getDouble("data.portal." + arena + ".z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}
//			Remove portal
			else if (slot == 7) {
				openInv(player, inv.createPortalConfirmInventory());
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createEditInventory(arena));
			}
		}
		
//		Player spawn menu for an arena
		else if (e.getView().getTitle().contains(Utils.format("&d&lPlayer Spawn: "))) {
//			Create spawn
			if (slot == 0) {
				plugin.getData().set("data.a" + arena + ".spawn.x", player.getLocation().getX());
				plugin.getData().set("data.a" + arena + ".spawn.y", player.getLocation().getY());
				plugin.getData().set("data.a" + arena + ".spawn.z", player.getLocation().getZ());
				plugin.getData().set("data.a" + arena + ".spawn.world", player.getLocation().getWorld().getName());
				plugin.saveData();
				player.sendMessage(Utils.format("&aSpawn set!"));
				player.closeInventory();
			}
//			Teleport player to spawn
			else if (slot == 1) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("data.a" + arena + ".spawn.world")),
							plugin.getData().getDouble("data.a" + arena + ".spawn.x"), plugin.getData().getDouble("data.a" + arena + ".spawn.y"),
							plugin.getData().getDouble("data.a" + arena + ".spawn.z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}
//			Remove spawn
			else if (slot == 7) {
				openInv(player, inv.createSpawnConfirmInventory());
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createEditInventory(arena));
			}
		}
		
//		Mob spawn menu for an arena
		else if (e.getView().getTitle().contains(Utils.format("&2&lMob Spawns: "))) {
//			Create spawn
			if (slot < 8) {
				openInv(player, inv.createMobSpawnMenu(arena, slot));
				oldSlot = slot;
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createEditInventory(arena));
			}
		}

//		Mob spawn menu for a specific spawn
		else if (e.getView().getTitle().contains(Utils.format("&2&lMob Spawn"))) {
//			Create spawn
			if (slot == 0) {
				plugin.getData().set("data.a" + arena + ".mob." + oldSlot + ".x", player.getLocation().getX());
				plugin.getData().set("data.a" + arena + ".mob." + oldSlot + ".y", player.getLocation().getY());
				plugin.getData().set("data.a" + arena + ".mob." + oldSlot + ".z", player.getLocation().getZ());
				plugin.getData().set("data.a" + arena + ".mob." + oldSlot + ".world", player.getLocation().getWorld().getName());
				plugin.saveData();
				player.sendMessage(Utils.format("&aMob spawn set!"));
				player.closeInventory();
			}
//			Teleport player to spawn
			else if (slot == 1) {
				Location location;
				try {
					location = new Location(Bukkit.getWorld(plugin.getData().getString("data.a" + arena + ".mob." + oldSlot + ".world")),
							plugin.getData().getDouble("data.a" + arena + ".mob." + oldSlot + ".x"), plugin.getData().getDouble("data.a" + arena + ".mob." + oldSlot + ".y"),
							plugin.getData().getDouble("data.a" + arena + ".mob." + oldSlot + ".z"));
				} catch (Exception err) {
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}
//			Remove spawn
			else if (slot == 7) {
				openInv(player, inv.createMobSpawnConfirmInventory());
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createMobSpawnInventory(arena));
			}
		}
		
//		Mob spawn menu for an arena
		else if (e.getView().getTitle().contains(Utils.format("&8&lGame Settings: "))) {
//			Open inv for changing max players
			if (slot == 0) {
				openInv(player, inv.createMaxPlayerInventory(arena));
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createEditInventory(arena));
			}
		}
		
//		Max player menu for an arena
		else if (e.getView().getTitle().contains(Utils.format("&e&lMax Players: "))) {
//			Open inv for changing max players
			if (slot < 4 && plugin.getData().getInt("data.a" + arena + ".max") > 1) {
				plugin.getData().set("data.a" + arena + ".max", plugin.getData().getInt("data.a" + arena + ".max") - 1);
				plugin.saveData();
				openInv(player, inv.createMaxPlayerInventory(arena));
			}
//			Exit menu
			else if (slot >= 4 && slot < 8) {
				plugin.getData().set("data.a" + arena + ".max", plugin.getData().getInt("data.a" + arena + ".max") + 1);
				plugin.saveData();
				openInv(player, inv.createMaxPlayerInventory(arena));
			}
//			Exit menu
			else if (slot == 8) {
				openInv(player, inv.createArenaSettingsInventory(arena));
			}
		}
	}
	
//	Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		// Ignore if safe close toggle is on
		if (close)
			return;
		// Close safely for the inventory of concern
		if (e.getView().getTitle().contains(Utils.format(inv.ARENA))) {
			plugin.getData().set("data.a" + arena + ".name", old);
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
