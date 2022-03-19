package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.GUI.Inventories;
import me.theguyhere.villagerdefense.plugin.GUI.InventoryItems;
import me.theguyhere.villagerdefense.plugin.GUI.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.SignGUIEvent;
import me.theguyhere.villagerdefense.plugin.game.models.*;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InventoryListener implements Listener {
	private final Main plugin;

	// Constants for armor types
	public InventoryListener(Main plugin) {
		this.plugin = plugin;
	}

	// Prevent losing items by drag clicking in custom inventory
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(CommunicationManager.format("&k")))
			return;

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains(plugin.getLanguageString("names.communityChest")))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// Prevent losing items by shift clicking in custom inventory
	@EventHandler
	public void onShiftClick(InventoryClickEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(CommunicationManager.format("&k")))
			return;

		// Check for shift click
		if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains(plugin.getLanguageString("names.communityChest")))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// All click events in the inventories
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		// Get inventory title
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(CommunicationManager.format("&k")))
			return;

		// Ignore null inventories
		if (e.getClickedInventory() == null)
			return;

		// Debugging info
		CommunicationManager.debugInfo("Inventory Item: " + e.getCurrentItem(), 2);
		CommunicationManager.debugInfo("Cursor Item: " + e.getCursor(), 2);
		CommunicationManager.debugInfo("Clicked Inventory: " + e.getClickedInventory(), 2);
		CommunicationManager.debugInfo("Inventory Name: " + title, 2);

		// Cancel the event if the inventory isn't the community chest or custom shop editor to prevent changing the GUI
		if (!title.contains(plugin.getLanguageString("names.communityChest")) && 
				!title.contains("Custom Shop Editor"))
			e.setCancelled(true);

		// Save community chest
		else if (title.contains(plugin.getLanguageString("names.communityChest"))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			arenaInstance.setCommunityChest(e.getInventory());
		}

		// Ignore clicks in player inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER)
			return;

		// Get button information
		ItemStack button = e.getCurrentItem();
		Material buttonType;
		String buttonName;

		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		FileConfiguration config = plugin.getArenaData();

		// Custom shop editor for an arena
		if (title.contains("Custom Shop Editor:")) {
			CommunicationManager.debugInfo("Custom shop editor being used.", 2);
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			ItemStack cursor = e.getCursor();
			assert cursor != null;
			String path = "a" + meta.getInteger1() + ".customShop.";
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Exit menu
			if (InventoryItems.exit(plugin).equals(button)) {
				e.setCancelled(true);
				player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				return;
			}

			// Check for arena closure
			if (!arenaInstance.isClosed()) {
				PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				return;
			}

			// Add item
			if (cursor.getType() != Material.AIR) {
				ItemMeta itemMeta = cursor.getItemMeta();
				assert itemMeta != null;
				itemMeta.setDisplayName((itemMeta.getDisplayName().equals("") ?
						Arrays.stream(cursor.getType().name().toLowerCase().split("_"))
								.reduce("", (partial, element) -> partial + " " +
										element.substring(0, 1).toUpperCase() + element.substring(1)).substring(1) :
						itemMeta.getDisplayName()) + String.format("%05d", 0));
				ItemStack copy = cursor.clone();
				copy.setItemMeta(itemMeta);
				config.set(path + slot, copy);
				PlayerManager.giveItem(player, cursor.clone(), plugin.getLanguageString("errors.inventoryFull"));
				player.setItemOnCursor(new ItemStack(Material.AIR));
				plugin.saveArenaData();
				player.openInventory(Inventories.createCustomItemsInventory(meta.getInteger1(), slot));
				return;
			}

			// Edit item
			else e.setCancelled(true);

			// Only open inventory for valid click
			if (Objects.requireNonNull(e.getCurrentItem()).getType() != Material.AIR)
				player.openInventory(Inventories.createCustomItemsInventory(meta.getInteger1(), slot));

			return;
		} else {
			// Ignore null items
			if (button == null)
				return;

			// Get button type and name
			buttonType = button.getType();
			buttonName = Objects.requireNonNull(button.getItemMeta()).getDisplayName();
		}

		// Arena inventory
		if (title.contains("Villager Defense Arenas")) {
			// Create new arena with naming inventory
			if (buttonType == Material.RED_CONCRETE) {
				Arena arena = new Arena(plugin, slot, new Tasks(plugin, slot));

				// Set a new arena
				GameManager.setArena(slot, arena);
				NMSVersion.getCurrent().getNmsManager().nameArena(player, arena.getName(),
						arena.getArena() + 1);
			}

			// Edit existing arena
			else if (buttonType == Material.LIME_CONCRETE)
				player.openInventory(Inventories.createArenaInventory(slot));

			// Open lobby menu
			else if (buttonName.contains("Lobby"))
				player.openInventory(Inventories.createLobbyInventory());

			// Open info boards menu
			else if (buttonName.contains("Info Boards"))
				player.openInventory(Inventories.createInfoBoardInventory());

			// Open leaderboards menu
			else if (buttonName.contains("Leaderboards"))
				player.openInventory(Inventories.createLeaderboardInventory());

			// Close inventory
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.closeInventory();
		}

		// Lobby menu
		else if (title.contains(CommunicationManager.format("&2&lLobby"))) {
			String path = "lobby";

			// Create lobby
			if (buttonName.contains("Create Lobby")) {
				DataManager.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGameManager().reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby set!");
				player.openInventory(Inventories.createLobbyInventory());
			}

			// Relocate lobby
			else if (buttonName.contains("Relocate Lobby")) {
				DataManager.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGameManager().reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby relocated!");
			}

			// Teleport player to lobby
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No lobby to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center lobby
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No lobby to center!");
					return;
				}
				DataManager.centerConfigLocation(plugin, path);
				PlayerManager.notifySuccess(player, "Lobby centered!");
			}

			// Remove lobby
			else if (buttonName.contains("REMOVE"))
				if (config.contains("lobby"))
					player.openInventory(Inventories.createLobbyConfirmInventory());
				else PlayerManager.notifyFailure(player, "No lobby to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Info board menu
		else if (title.contains("Info Boards")) {

			// Edit board
			if (Arrays.asList(Inventories.INFO_BOARD_MATS).contains(buttonType))
				player.openInventory(Inventories.createInfoBoardMenu(slot));

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Info board menu for a specific board
		else if (title.contains("Info Board ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			int num = meta.getInteger1();
			String path = "infoBoard." + num;

			// Create board
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setInfoBoard(player.getLocation(), num);
				PlayerManager.notifySuccess(player, "Info board set!");
				player.openInventory(Inventories.createInfoBoardMenu(num));
			}

			// Relocate board
			else if (buttonName.contains("Relocate")) {
				DataManager.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGameManager().refreshInfoBoard(num);
				PlayerManager.notifySuccess(player, "Info board relocated!");
			}

			// Teleport player to info board
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No info board to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center info board
			else if (buttonName.contains("Center")) {
				if (DataManager.getConfigLocationNoRotation(plugin, path) == null) {
					PlayerManager.notifyFailure(player, "No info board to center!");
					return;
				}
				plugin.getGameManager().centerInfoBoard(num);
				PlayerManager.notifySuccess(player, "Info board centered!");
			}

			// Remove info board
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createInfoBoardConfirmInventory(num));
				else PlayerManager.notifyFailure(player, "No info board to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createInfoBoardInventory());
		}

		// Leaderboard menu
		else if (title.contains("Leaderboards")) {
			if (buttonName.contains("Total Kills Leaderboard"))
				player.openInventory(Inventories.createTotalKillsLeaderboardInventory());

			if (buttonName.contains("Top Kills Leaderboard"))
				player.openInventory(Inventories.createTopKillsLeaderboardInventory());

			if (buttonName.contains("Total Gems Leaderboard"))
				player.openInventory(Inventories.createTotalGemsLeaderboardInventory());

			if (buttonName.contains("Top Balance Leaderboard"))
				player.openInventory(Inventories.createTopBalanceLeaderboardInventory());

			if (buttonName.contains("Top Wave Leaderboard"))
				player.openInventory(Inventories.createTopWaveLeaderboardInventory());

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Total kills leaderboard menu
		else if (title.contains(CommunicationManager.format("&4&lTotal Kills Leaderboard"))) {
			String path = "leaderboard.totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalKills");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalKillsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalKills");
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				plugin.getGameManager().centerLeaderboard("totalKills");
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTotalKillsConfirmInventory());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top kills leaderboard menu
		else if (title.contains(CommunicationManager.format("&c&lTop Kills Leaderboard"))) {
			String path = "leaderboard.topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topKills");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopKillsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topKills");
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				plugin.getGameManager().centerLeaderboard("topKills");
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopKillsConfirmInventory());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Total gems leaderboard menu
		else if (title.contains(CommunicationManager.format("&2&lTotal Gems Leaderboard"))) {
			String path = "leaderboard.totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalGems");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalGemsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalGems");
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				plugin.getGameManager().centerLeaderboard("totalGems");
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTotalGemsConfirmInventory());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top balance leaderboard menu
		else if (title.contains(CommunicationManager.format("&a&lTop Balance Leaderboard"))) {
			String path = "leaderboard.topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topBalance");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopBalanceLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topBalance");
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				plugin.getGameManager().centerLeaderboard("topBalance");
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopBalanceConfirmInventory());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top wave leaderboard menu
		else if (title.contains(CommunicationManager.format("&9&lTop Wave Leaderboard"))) {
			String path = "leaderboard.topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topWave");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopWaveLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topWave");
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				plugin.getGameManager().centerLeaderboard("topWave");
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopWaveConfirmInventory());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Menu for an arena
		else if (title.contains(CommunicationManager.format("&2&lEdit "))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed())
					NMSVersion.getCurrent().getNmsManager().nameArena(player, arenaInstance.getName(),
							arenaInstance.getArena() + 1);
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open portal menu
			else if (buttonName.contains("Portal and Leaderboard"))
				player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));

			// Open player menu
			else if (buttonName.contains("Player Settings"))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));

			// Open mob menu
			else if (buttonName.contains("Mob Settings"))
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));

			// Open shop menu
			else if (buttonName.contains("Shop Settings"))
				player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));

			// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));

			// Toggle arena close
			else if (buttonName.contains("Close")) {
				// Arena currently closed
				if (arenaInstance.isClosed()) {
					// No lobby
					if (!config.contains("lobby")) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a lobby!");
						return;
					}

					// No arena portal
					if (arenaInstance.getPortalLocation() == null) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a portal!");
						return;
					}

					// No player spawn
					if (arenaInstance.getPlayerSpawn() == null) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a player spawn!");
						return;
					}

					// No monster spawn
					if (arenaInstance.getMonsterSpawns().isEmpty()) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a monster spawn!");
						return;
					}

					// No villager spawn
					if (arenaInstance.getVillagerSpawns().isEmpty()) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a villager spawn!");
						return;
					}

					// No shops
					if (!arenaInstance.hasCustom() && !arenaInstance.hasNormal()) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a shop!");
						return;
					}

					// Invalid arena bounds
					if (arenaInstance.getCorner1() == null || arenaInstance.getCorner2() == null ||
							!Objects.equals(arenaInstance.getCorner1().getWorld(), 
									arenaInstance.getCorner2().getWorld())) {
						PlayerManager.notifyFailure(player, "Arena cannot open without valid arena bounds!");
						return;
					}

					// Open arena
					arenaInstance.setClosed(false);
				}

				// Arena currently open
				else arenaInstance.setClosed(true);

				// Save perm data
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createArenaConfirmInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Return to arenas menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove portal, close arena
					GameManager.getArena(meta.getInteger1()).removePortal();

					// Confirm and return
					PlayerManager.notifySuccess(player, "Portal removed!");
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove leaderboard
			else if (title.contains("Remove Leaderboard?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Delete arena leaderboard
					GameManager.getArena(meta.getInteger1()).removeArenaBoard();

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setPlayerSpawn(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Spawn removed!");
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setWaitingRoom(null);
					PlayerManager.notifySuccess(player, "Waiting room removed!");
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove monster spawn
			else if (title.contains("Remove Monster Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));

				// Remove the monster spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setMonsterSpawn(meta.getInteger2(), null);
					if (arenaInstance.getMonsterSpawns().isEmpty())
						arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Mob spawn removed!");
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				}
			}

			// Confirm to remove villager spawn
			else if (title.contains("Remove Villager Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));

				// Remove the villager spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setVillagerSpawn(meta.getInteger2(), null);
					if (arenaInstance.getVillagerSpawns().isEmpty())
						arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Mob spawn removed!");
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				}
			}

			// Confirm to remove custom item
			else if (title.contains("Remove Custom Item?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCustomItemsInventory(meta.getInteger1(), meta.getInteger2()));

				// Remove custom item, then return to custom shop editor
				else if (buttonName.contains("YES")) {
					config.set("a" + meta.getInteger1() + ".customShop." + meta.getInteger2(), null);
					plugin.saveArenaData();
					player.openInventory(GameManager.getArena(meta.getInteger1()).getCustomShopEditor());
				}
			}

			// Confirm to remove corner 1
			else if (title.contains("Remove Corner 1?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCorner1Inventory(meta.getInteger1()));

				// Remove corner 1, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setCorner1(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Corner 1 removed!");
					player.openInventory(Inventories.createCorner1Inventory(meta.getInteger1()));
				}
			}

			// Confirm to remove corner 2
			else if (title.contains("Remove Corner 2?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCorner1Inventory(meta.getInteger1()));

				// Remove corner 2, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = GameManager.getArena(meta.getInteger1());

					arenaInstance.setCorner2(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Corner 2 removed!");
					player.openInventory(Inventories.createCorner2Inventory(meta.getInteger1()));
				}
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createLobbyInventory());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set("lobby", null);
					plugin.saveArenaData();
					plugin.getGameManager().reloadLobby();
					PlayerManager.notifySuccess(player, "Lobby removed!");
					player.openInventory(Inventories.createLobbyInventory());
				}
			}

			// Confirm to remove info board
			else if (title.contains("Remove Info Board?")) {
				String path = "infoBoard." + meta.getInteger1();

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createInfoBoardMenu(meta.getInteger1()));

					// Remove the info board, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove info board data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove info board
					plugin.getGameManager().removeInfoBoard(meta.getInteger1());

					// Confirm and return
					PlayerManager.notifySuccess(player, "Info board removed!");
					player.openInventory(Inventories.createInfoBoardMenu(meta.getInteger1()));
				}
			}

			// Confirm to remove total kills leaderboard
			else if (title.contains("Remove Total Kills Leaderboard?")) {
				String path = "leaderboard.totalKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("totalKills");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTotalKillsLeaderboardInventory());
				}
			}

			// Confirm to remove top kills leaderboard
			else if (title.contains("Remove Top Kills Leaderboard?")) {
				String path = "leaderboard.topKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topKills");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopKillsLeaderboardInventory());
				}
			}

			// Confirm to remove total gems leaderboard
			else if (title.contains("Remove Total Gems Leaderboard?")) {
				String path = "leaderboard.totalGems";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalGemsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("totalGems");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTotalGemsLeaderboardInventory());
				}
			}

			// Confirm to remove top balance leaderboard
			else if (title.contains("Remove Top Balance Leaderboard?")) {
				String path = "leaderboard.topBalance";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopBalanceLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topBalance");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopBalanceLeaderboardInventory());
				}
			}

			// Confirm to remove top wave leaderboard
			else if (title.contains("Remove Top Wave Leaderboard?")) {
				String path = "leaderboard.topWave";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopWaveLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topWave");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopWaveLeaderboardInventory());
				}
			}

			// Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove data
					GameManager.removeArena(meta.getInteger1());

					// Confirm and return
					PlayerManager.notifySuccess(player, "Arena removed!");
					player.openInventory(Inventories.createArenasInventory());
				}
			}
		}

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					PlayerManager.notifySuccess(player, "Portal set!");
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			
			// Relocate portal
			if (buttonName.contains("Relocate Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					PlayerManager.notifySuccess(player, "Portal relocated!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to portal
			else if (buttonName.contains("Teleport to Portal")) {
				Location location = arenaInstance.getPortalLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No portal to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center portal
			else if (buttonName.contains("Center Portal")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getPortal() == null) {
						PlayerManager.notifyFailure(player, "No portal to center!");
						return;
					}
					arenaInstance.centerPortal();
					PlayerManager.notifySuccess(player, "Portal centered!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Remove portal
			else if (buttonName.contains("REMOVE PORTAL"))
				if (arenaInstance.getPortal() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createPortalConfirmInventory(meta.getInteger1()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No portal to remove!");

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
			}

			// Relocate leaderboard
			if (buttonName.contains("Relocate Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport to Leaderboard")) {
				Location location = arenaInstance.getArenaBoardLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center Leaderboard")) {
				if (arenaInstance.getArenaBoard() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				arenaInstance.centerArenaBoard();
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE LEADERBOARD"))
				if (arenaInstance.getArenaBoardLocation() != null)
					player.openInventory(Inventories.createArenaBoardConfirmInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					PlayerManager.notifySuccess(player, "Spawn set!");
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Create spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					PlayerManager.notifySuccess(player, "Spawn relocated!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(arenaInstance.getPlayerSpawn().getLocation());
					player.closeInventory();
				} catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No player spawn to teleport to!");
				}
			}

			// Center player spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getPlayerSpawn() != null) {
						arenaInstance.centerPlayerSpawn();
						PlayerManager.notifySuccess(player, "Spawn centered!");
					} else PlayerManager.notifyFailure(player, "No player spawn to center!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getPlayerSpawn() != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createSpawnConfirmInventory(meta.getInteger1()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No player spawn to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					PlayerManager.notifySuccess(player, "Waiting room set!");
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Relocate waiting room
			if (buttonName.contains("Relocate")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					PlayerManager.notifySuccess(player, "Waiting room relocated!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Teleport player to waiting room
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getWaitingRoom();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No waiting room to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center waiting room
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getWaitingRoom() == null) {
						PlayerManager.notifyFailure(player, "No waiting room to center!");
						return;
					}
					arenaInstance.centerWaitingRoom();
					PlayerManager.notifySuccess(player, "Waiting room centered!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Remove waiting room
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getWaitingRoom() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createWaitingConfirmInventory(meta.getInteger1()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No waiting room to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getMaxPlayers();
			
			// Decrease max players
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if max players is greater than 1
				if (current <= 1) {
					PlayerManager.notifyFailure(player, "Max players cannot be less than 1!");
					return;
				}

				// Check if max players is greater than min players
				if (current <= arenaInstance.getMinPlayers()) {
					PlayerManager.notifyFailure(player, "Max players cannot be less than min player!");
					return;
				}

				arenaInstance.setMaxPlayers(--current);
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxPlayers(++current);
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getMinPlayers();

			// Decrease min players
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if min players is greater than 1
				if (current <= 1) {
					PlayerManager.notifyFailure(player, "Min players cannot be less than 1!");
					return;
				}

				arenaInstance.setMinPlayers(--current);
				player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
			}

			// Increase min players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if min players is less than max players
				if (current >= arenaInstance.getMaxPlayers()) {
					PlayerManager.notifyFailure(player, "Min players cannot be greater than max player!");
					return;
				}

				arenaInstance.setMinPlayers(++current);
				player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				player.openInventory(Inventories.createMonsterSpawnInventory(meta.getInteger1()));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getInteger1()));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createSpawnTableInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Edit spawn
			if (Arrays.asList(Inventories.MONSTER_MATS).contains(buttonType))
				player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(), slot));

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getInteger2(), player.getLocation());
					PlayerManager.notifySuccess(player, "Monster spawn set!");
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getInteger2(), player.getLocation());
					PlayerManager.notifySuccess(player, "Monster spawn relocated!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance.getMonsterSpawn(meta.getInteger2()).getLocation());
					player.closeInventory();
				} catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No monster spawn to teleport to!");
				}

			// Center monster spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getInteger2()) != null) {
						arenaInstance.centerMonsterSpawn(meta.getInteger2());
						PlayerManager.notifySuccess(player, "Monster spawn centered!");
					} else PlayerManager.notifyFailure(player, "No monster spawn to center!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Set monster type
			else if (buttonName.contains("Type"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getInteger2()) != null) {
						arenaInstance.setMonsterSpawnType(meta.getInteger2(),
								(arenaInstance.getMonsterSpawnType(meta.getInteger2()) + 1) % 3);
						player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(),
								meta.getInteger2()));
						PlayerManager.notifySuccess(player, "Monster spawn type changed!");
					} else PlayerManager.notifyFailure(player, "No monster spawn to set type!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(meta.getInteger2()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createMonsterSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No monster spawn to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createMonsterSpawnInventory(meta.getInteger1()));
		}

		// Villager spawn menu for an arena
		else if (title.contains("Villager Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Edit spawn
			if (Arrays.asList(Inventories.VILLAGER_MATS).contains(buttonType))
				player.openInventory(Inventories.createVillagerSpawnMenu(meta.getInteger1(), slot));

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getInteger2(), player.getLocation());
					PlayerManager.notifySuccess(player, "Villager spawn set!");
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getInteger2(), player.getLocation());
					PlayerManager.notifySuccess(player, "Villager spawn set!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance.getVillagerSpawn(meta.getInteger2()).getLocation());
					player.closeInventory();
				} catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No villager spawn to teleport to!");
				}

			// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getVillagerSpawn(meta.getInteger2()) != null) {
						arenaInstance.centerVillagerSpawn(meta.getInteger2());
						PlayerManager.notifySuccess(player, "Villager spawn centered!");
					} else PlayerManager.notifyFailure(player, "No villager spawn to center!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getVillagerSpawn(meta.getInteger2()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createVillagerSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No villager spawn to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getInteger1()));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Default
			if (buttonName.contains("Default")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("default"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 1
			else if (buttonName.contains("Option 1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option1"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 2
			else if (buttonName.contains("Option 2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option2"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 3
			else if (buttonName.contains("Option 3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option3"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 4
			else if (buttonName.contains("Option 4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option4"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 5
			else if (buttonName.contains("Option 5")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option5"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Option 6
			else if (buttonName.contains("Option 6")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option6"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Custom
			else if (buttonName.contains("Custom")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("custom"))
					PlayerManager.notifyFailure(player, "File doesn't exist!");
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				return;
			}

			// Reload inventory
			player.openInventory(Inventories.createSpawnTableInventory(meta.getInteger1()));
		}

		// Shop settings menu for an arena
		else if (title.contains("Shop Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					player.openInventory(arenaInstance.getCustomShopEditor());
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle enchants shop
			else if (buttonName.contains("Enchants Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setEnchants(!arenaInstance.hasEnchants());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle community chest
			else if (buttonName.contains("Community Chest:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCommunity(!arenaInstance.hasCommunity());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Menu for editing a specific custom item
		else if (title.contains("Edit Item")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			String path = "a" + meta.getInteger1() + ".customShop.";
			ItemStack item = config.getItemStack(path + meta.getInteger2());
			assert item != null;
			ItemMeta itemMeta = item.getItemMeta();
			assert itemMeta != null;
			String name = itemMeta.getDisplayName();
			String realName = name.substring(0, name.length() - 5);
			int price = NumberUtils.toInt(name.substring(name.length() - 5), -1);

			// Set un-purchasable
			if (buttonName.contains("Toggle Un-purchasable")) {
				if (price < 0) {
					price = 0;
					itemMeta.setDisplayName(realName + String.format("%05d", price));
				} else itemMeta.setDisplayName(realName + "-----");
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Increase by 1
			else if (buttonName.contains("+1 gem")) {
				if (price < 0) {
					price = 0;
				} else price++;

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Increase by 10
			else if (buttonName.contains("+10 gems")) {
				if (price < 0) {
					price = 0;
				} else price += 10;

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Increase by 100
			else if (buttonName.contains("+100 gems")) {
				if (price < 0) {
					price = 0;
				} else price += 100;

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Increase by 1000
			else if (buttonName.contains("+1000 gems")) {
				if (price < 0) {
					price = 0;
				} else price += 1000;

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Delete item
			else if (buttonName.contains("DELETE")) {
				player.openInventory(Inventories.createCustomItemConfirmInventory(meta.getInteger1(), meta.getInteger2()));
				return;
			}

			// Decrease by 1
			else if (buttonName.contains("-1 gem")) {
				if (price < 0) {
					price = 0;
				} else price--;

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Decrease by 10
			else if (buttonName.contains("-10 gems")) {
				if (price < 0) {
					price = 0;
				} else price -= 10;

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Decrease by 100
			else if (buttonName.contains("-100 gems")) {
				if (price < 0) {
					price = 0;
				} else price -= 100;

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Decrease by 1000
			else if (buttonName.contains("-1000 gems")) {
				if (price < 0) {
					price = 0;
				} else price -= 1000;

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Exit
			else if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(GameManager.getArena(meta.getInteger1()).getCustomShopEditor());
				return;
			}

			// Save changes and refresh GUI
			plugin.saveArenaData();
			player.openInventory(Inventories.createCustomItemsInventory(meta.getInteger1(), meta.getInteger2()));
		}

		// Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(Inventories.createAllowedKitsInventory(arenaInstance.getArena(), false));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Late Arrival:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setLateArrival(!arenaInstance.hasLateArrival());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle experience drop
			else if (buttonName.contains("Experience Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setExpDrop(!arenaInstance.hasExpDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle item drop
			else if (buttonName.contains("Item Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemDrop(!arenaInstance.hasGemDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit arena bounds
			else if (buttonName.contains("Arena Bounds"))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));

			// Edit wolf cap
			else if (buttonName.contains("Wolf Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit iron golem cap
			else if (buttonName.contains("Iron Golem Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createCopySettingsInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getMaxWaves();

			// Decrease max waves
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);

				// Check if max waves is greater than 1
				else if (current <= 1) {
					PlayerManager.notifyFailure(player, "Max waves cannot be less than 1!");
					return;
				} else arenaInstance.setMaxWaves(--current);

				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(-1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Increase max waves
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);
				else arenaInstance.setMaxWaves(++current);

				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getWaveTimeLimit();

			// Decrease wave time limit
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);

				// Check if wave time limit is greater than 1
				else if (current <= 1) {
					PlayerManager.notifyFailure(player, "Wave time limit cannot be less than 1!");
					return;
				} else arenaInstance.setWaveTimeLimit(--current);

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(-1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Increase wave time limit
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);
				else arenaInstance.setWaveTimeLimit(++current);

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Set to Easy
			if (buttonName.contains("Easy")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Easy");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Medium");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Hard");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Insane");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel(null);
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Set to 1
			if (buttonName.contains("1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(1);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(2);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(3);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(4);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Allowed kits display for an arena
		else if (title.contains(plugin.getLanguageString("messages.allowedKits") + ": ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Exit menu
			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				assert meta != null;
				player.openInventory(Inventories.createArenaInfoInventory(GameManager.getArena(meta.getInteger1())));
			}
		}

		// Allowed kits menu for an arena
		else if (title.contains(plugin.getLanguageString("messages.allowedKits"))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			String kit = buttonName.substring(4);
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			List<String> banned = arenaInstance.getBannedKits();

			// Toggle a kit
			if (!(kit.equals(plugin.getLanguageString("names.giftKits")) || 
					kit.equals(plugin.getLanguageString("names.abilityKits")) ||
					kit.equals(plugin.getLanguageString("names.effectKits")) ||
					kit.equals(plugin.getLanguageString("messages.exit")))) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (banned.contains(kit))
					banned.remove(kit);
				else banned.add(kit);
				arenaInstance.setBannedKits(banned);
				player.openInventory(Inventories.createAllowedKitsInventory(arenaInstance.getArena(), false));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Arena bounds menu for an arena
		else if (title.contains("Arena Bounds")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Edit corner 1
			if (buttonName.contains("Corner 1"))
				player.openInventory(Inventories.createCorner1Inventory(meta.getInteger1()));

			// Edit corner 2
			else if (buttonName.contains("Corner 2"))
				player.openInventory(Inventories.createCorner2Inventory(meta.getInteger1()));

			// Toggle border particles
			else if (buttonName.contains("Border Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setBorderParticles(!arenaInstance.hasBorderParticles());
					player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Corner 1 menu for an arena
		else if (title.contains("Corner 1")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
					player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getCorner1();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No corner 1 to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getCorner1() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createCorner1ConfirmInventory(meta.getInteger1()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 1 to remove!");

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
		}

		// Corner 2 menu for an arena
		else if (title.contains("Corner 2")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
					player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getCorner2();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No corner 2 to teleport to!");
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getCorner2() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createCorner2ConfirmInventory(meta.getInteger1()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 2 to remove!");

				// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
		}

		// Wolf cap menu for an arena
		else if (title.contains("Wolf Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getWolfCap();

			// Decrease wolf cap
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if wolf cap is greater than 1
				if (current <= 1) {
					PlayerManager.notifyFailure(player, "Wolf cap cannot be less than 1!");
					return;
				}

				arenaInstance.setWolfCap(--current);
				player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
			}

			// Increase wolf cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWolfCap(++current);
				player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Iron golem cap menu for an arena
		else if (title.contains("Iron Golem Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());
			int current = arenaInstance.getGolemCap();

			// Decrease iron golem cap
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				// Check if iron golem cap is greater than 1
				if (current <= 1) {
					PlayerManager.notifyFailure(player, "Iron golem cap cannot be less than 1!");
					return;
				}

				arenaInstance.setgolemCap(--current);
				player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
			}

			// Increase iron golem cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setgolemCap(++current);
				player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaitSoundInventory(meta.getInteger1()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle ability sound
			else if (buttonName.contains("Ability")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setAbilitySound(!arenaInstance.hasAbilitySound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Waiting sound menu for an arena
		else if (title.contains("Waiting Sound:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = GameManager.getArena(meta.getInteger1());

			// Exit menu
			if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));

			// Set sound
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaitingSound(slot);
				player.openInventory(Inventories.createWaitSoundInventory(meta.getInteger1()));
			}
		}

		// Menu to copy game settings
		else if (title.contains("Copy Game Settings")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arena1 = GameManager.getArena(meta.getInteger1());

			if (slot < 45) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				Arena arena2 = GameManager.getArena(slot);

				// Copy settings from another arena
				if (buttonType == Material.WHITE_CONCRETE)
					arena1.copy(arena2);
				else return;
			}

			// Copy easy preset
			else if (buttonName.contains("Easy Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(45);
				arena1.setWaveTimeLimit(5);
				arena1.setDifficultyMultiplier(1);
				arena1.setDynamicCount(false);
				arena1.setDynamicDifficulty(false);
				arena1.setDynamicLimit(true);
				arena1.setDynamicPrices(false);
				arena1.setDifficultyLabel("Easy");
			}

			// Copy medium preset
			else if (buttonName.contains("Medium Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(50);
				arena1.setWaveTimeLimit(4);
				arena1.setDifficultyMultiplier(2);
				arena1.setDynamicCount(false);
				arena1.setDynamicDifficulty(false);
				arena1.setDynamicLimit(true);
				arena1.setDynamicPrices(true);
				arena1.setDifficultyLabel("Medium");
			}

			// Copy hard preset
			else if (buttonName.contains("Hard Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(60);
				arena1.setWaveTimeLimit(3);
				arena1.setDifficultyMultiplier(3);
				arena1.setDynamicCount(true);
				arena1.setDynamicDifficulty(true);
				arena1.setDynamicLimit(true);
				arena1.setDynamicPrices(true);
				arena1.setDifficultyLabel("Hard");
			}

			// Copy insane preset
			else if (buttonName.contains("Insane Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(-1);
				arena1.setWaveTimeLimit(3);
				arena1.setDifficultyMultiplier(4);
				arena1.setDynamicCount(true);
				arena1.setDynamicDifficulty(true);
				arena1.setDynamicLimit(false);
				arena1.setDynamicPrices(true);
				arena1.setDifficultyLabel("Insane");
			}

			// Exit menu
			else if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				return;
			}

			// Save updates
			PlayerManager.notifySuccess(player, "Game settings copied!");
		}

		// In-game item shop menu
		else if (title.contains(plugin.getLanguageString("names.itemShop"))) {
			Arena arenaInstance;

			// See if the player is in a game
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (Exception err) {
				return;
			}

			// Open weapon shop
			if (buttonName.contains(plugin.getLanguageString("names.weaponShop")))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getWeaponShop());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.normalShop"));

			// Open armor shop
			else if (buttonName.contains(plugin.getLanguageString("names.armorShop")))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getArmorShop());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.normalShop"));

			// Open consumables shop
			else if (buttonName.contains(plugin.getLanguageString("names.consumableShop")))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getConsumeShop());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.normalShop"));

			// Open enchant shop
			else if (buttonName.contains(plugin.getLanguageString("names.enchantShop")))
				if (arenaInstance.hasEnchants())
					player.openInventory(Inventories.createEnchantShop());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.enchantShop"));

			// Open custom shop
			else if (buttonName.contains(plugin.getLanguageString("names.customShop")))
				if (arenaInstance.hasCustom())
					player.openInventory(arenaInstance.getCustomShop());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.customShop"));

			// Open community chest
			else if (buttonName.contains(plugin.getLanguageString("names.communityChest")))
				if (arenaInstance.hasCommunity())
					player.openInventory(arenaInstance.getCommunityChest());
				else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.communityChest"));
		}

		// Mock custom shop for an arena
		else if (title.contains(plugin.getLanguageString("names.customShop") + ":")) {
			Arena arenaInstance = GameManager.getArena(title.substring(19));
			if (buttonName.contains(plugin.getLanguageString("messages.exit")))
				player.openInventory(Inventories.createArenaInfoInventory(arenaInstance));
		}

		// In-game shops
		else if (title.contains(plugin.getLanguageString("names.weaponShop")) ||
				title.contains(plugin.getLanguageString("names.armorShop")) ||
				title.contains(plugin.getLanguageString("names.consumableShop")) ||
				title.contains(plugin.getLanguageString("names.customShop"))) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(Inventories.createShop(arenaInstance.getCurrentWave() / 10 + 1, arenaInstance));
				return;
			}

			// Ignore null items
			if (e.getClickedInventory().getItem(e.getSlot()) == null)
				return;

			ItemStack buy = Objects.requireNonNull(e.getClickedInventory().getItem(e.getSlot())).clone();
			Material buyType = buy.getType();
			List<String> lore = Objects.requireNonNull(buy.getItemMeta()).getLore();

			// Ignore un-purchasable items
			if (lore == null)
				return;

			int cost = Integer.parseInt(lore.get(lore.size() - 1).substring(10));

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.buy"));
				return;
			}

			// Remove cost meta
			buy = ItemManager.removeLastLore(buy);

			// Make unbreakable for blacksmith
			if (gamer.getKit().getName().equals(Kit.blacksmith().getName()))
				buy = ItemManager.makeUnbreakable(buy);

			// Make splash potion for witch
			if (gamer.getKit().getName().equals(Kit.witch().getName()))
				buy = ItemManager.makeSplash(buy);

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (gamer.getKit().getName().equals(Kit.merchant().getName()))
				gamer.addGems(cost / 10);
			GameManager.createBoard(gamer);

			EntityEquipment equipment = Objects.requireNonNull(player.getPlayer()).getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getHelmet() == null) {
				equipment.setHelmet(buy);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.helmet"));
			} else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getChestplate() == null) {
				equipment.setChestplate(buy);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.chestplate"));
			} else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getLeggings() == null) {
				equipment.setLeggings(buy);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.leggings"));
			} else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getBoots() == null) {
				equipment.setBoots(buy);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.boots"));
			} else {
				PlayerManager.giveItem(player, buy, plugin.getLanguageString("errors.inventoryFull"));
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.buy"));
			}
		}
		else if (title.contains(plugin.getLanguageString("names.enchantShop"))) {
			Arena arenaInstance;

			// Attempt to get arena
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (Exception err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(Inventories.createShop(arenaInstance.getCurrentWave() / 10 + 1, arenaInstance));
				return;
			}

			// Ignore null items
			if (e.getClickedInventory().getItem(e.getSlot()) == null)
				return;

			// Get necessary info
			ItemStack buy = e.getClickedInventory().getItem(e.getSlot());
			assert buy != null;
			List<String> lore = Objects.requireNonNull(buy.getItemMeta()).getLore();
			assert lore != null;
			int cost = Integer.parseInt(lore.get(0).split(" ")[1]);

			// Check if they can afford the item, then deduct
			if (player.getLevel() < cost) {
				PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.buy"));
				return;
			}
			player.setLevel(player.getLevel() - cost);

			// Give book
			String enchant;
			ItemStack give;

			// Gather enchant from name
			try {
				enchant = Objects.requireNonNull(buy.getItemMeta()).getDisplayName().split(" ")[1];
			} catch (Exception err) {
				return;
			}

			// Assign to known enchanting books
			if (enchant.equals(plugin.getLanguageString("enchants.knockback").split(" ")[0]))
				give = EnchantingBook.knockback();
			else if (enchant.equals(plugin.getLanguageString("enchants.sweepingEdge").split(" ")[0]))
				give = EnchantingBook.sweepingEdge();
			else if (enchant.equals(plugin.getLanguageString("enchants.smite").split(" ")[0]))
				give = EnchantingBook.smite();
			else if (enchant.equals(plugin.getLanguageString("enchants.sharpness").split(" ")[0]))
				give = EnchantingBook.sharpness();
			else if (enchant.equals(plugin.getLanguageString("enchants.fireAspect").split(" ")[0]))
				give = EnchantingBook.fireAspect();
			else if (enchant.equals(plugin.getLanguageString("enchants.punch").split(" ")[0]))
				give = EnchantingBook.punch();
			else if (enchant.equals(plugin.getLanguageString("enchants.piercing").split(" ")[0]))
				give = EnchantingBook.piercing();
			else if (enchant.equals(plugin.getLanguageString("enchants.quickCharge").split(" ")[0]))
				give = EnchantingBook.quickCharge();
			else if (enchant.equals(plugin.getLanguageString("enchants.power").split(" ")[0]))
				give = EnchantingBook.power();
			else if (enchant.equals(plugin.getLanguageString("enchants.loyalty").split(" ")[0]))
				give = EnchantingBook.loyalty();
			else if (enchant.equals(plugin.getLanguageString("enchants.flame").split(" ")[0]))
				give = EnchantingBook.flame();
			else if (enchant.equals(plugin.getLanguageString("enchants.multishot").split(" ")[0]))
				give = EnchantingBook.multishot();
			else if (enchant.equals(plugin.getLanguageString("enchants.infinity").split(" ")[0]))
				give = EnchantingBook.infinity();
			else if (enchant.equals(plugin.getLanguageString("enchants.blastProtection").split(" ")[0]))
				give = EnchantingBook.blastProtection();
			else if (enchant.equals(plugin.getLanguageString("enchants.thorns").split(" ")[0]))
				give = EnchantingBook.thorns();
			else if (enchant.equals(plugin.getLanguageString("enchants.projectileProtection").split(" ")[0]))
				give = EnchantingBook.projectileProtection();
			else if (enchant.equals(plugin.getLanguageString("enchants.protection").split(" ")[0]))
				give = EnchantingBook.protection();
			else if (enchant.equals(plugin.getLanguageString("enchants.unbreaking").split(" ")[0]))
				give = EnchantingBook.unbreaking();
			else if (enchant.equals(plugin.getLanguageString("enchants.mending").split(" ")[0]))
				give = EnchantingBook.mending();
			else give = null;

			PlayerManager.giveItem(player, give, plugin.getLanguageString("errors.inventoryFull"));
			PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.buy"));
		}

		// Stats menu for a player
		else if (title.contains(plugin.getLanguageString("messages.playerStatistics")
				.substring(plugin.getLanguageString("messages.playerStatistics").indexOf("%s") + 2))) {
			String raw = plugin.getLanguageString("messages.playerStatistics");
			String name = title.substring(raw.indexOf("%s") + 6, title.length() - raw.length() + raw.indexOf("%s") + 2);
			if (buttonName.contains("Kits"))
				player.openInventory(Inventories.createPlayerKitsInventory(name, player.getName()));
		}

		// Kits menu for a player
		else if (title.contains(plugin.getLanguageString("messages.playerKits")
				.substring(plugin.getLanguageString("messages.playerKits").indexOf("%s") + 2))) {
			String raw = plugin.getLanguageString("messages.playerKits");
			FileConfiguration playerData = plugin.getPlayerData();
			String name = title.substring(raw.indexOf("%s") + 6, title.length() - raw.length() + raw.indexOf("%s") + 2);
			Kit kit = Kit.getKit(buttonName.substring(4));
			String path = name + ".kits.";

			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.openInventory(Inventories.createPlayerStatsInventory(name));
				return;
			}

			// Check if requester is owner
			if (!name.equals(player.getName()))
				return;

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError("No kit of " + buttonName.substring(4) + " was found.", 1);
				return;
			}

			// Single tier kits
			if (!kit.isMultiLevel()) {
				if (!playerData.getBoolean(path + kit.getName()))
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(1)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(1));
						playerData.set(path + kit.getName(), true);
						PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.kitBuy"));
					} else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.kitBuy"));
			}

			// Multiple tier kits
			else {
				int kitLevel = playerData.getInt(path + kit.getName());
				if (kitLevel == kit.getMaxLevel())
					return;
				else if (kitLevel == 0) {
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(++kitLevel)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(kitLevel));
						playerData.set(path + kit.getName(), kitLevel);
						PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.kitBuy"));
					} else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.kitBuy"));
				} else {
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(++kitLevel)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(kitLevel));
						playerData.set(path + kit.getName(), kitLevel);
						PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.kitUpgrade"));
					} else PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.kitUpgrade"));
				}
			}

			plugin.savePlayerData();
			player.openInventory(Inventories.createPlayerKitsInventory(name, name));
		}

		// Kit selection menu for an arena
		else if (title.contains(" " + plugin.getLanguageString("messages.kits"))) {
			FileConfiguration playerData = plugin.getPlayerData();
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			Kit kit = Kit.getKit(buttonName.substring(4));
			String path = player.getName() + ".kits.";

			// Leave if EXIT
			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.closeInventory();
				return;
			}

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError("No kit of " + buttonName.substring(4) + " was found.", 
						1);
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == PlayerStatus.SPECTATOR)
				return;

			// Single tier kits
			if (!kit.isMultiLevel()) {
				if (playerData.getBoolean(path + kit.getName()) || kit.equals(Kit.orc()) ||
						kit.equals(Kit.farmer()) || kit.equals(Kit.none())) {
					gamer.setKit(kit.setKitLevel(1));
					PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.kitSelect"));
				} else {
					PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.kitSelect"));
					return;
				}
			}

			// Multiple tier kits
			else {
				if (playerData.getInt(path + kit.getName()) < 1) {
					PlayerManager.notifyFailure(player, plugin.getLanguageString("errors.kitSelect"));
					return;
				}
				gamer.setKit(kit.setKitLevel(playerData.getInt(path + kit.getName())));
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.kitSelect"));
			}

			// Close inventory and create scoreboard
			player.closeInventory();
			GameManager.createBoard(gamer);
		}

		// Challenge selection menu for an arena
		else if (title.contains(" " + plugin.getLanguageString("messages.challenges"))) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			Challenge challenge = Challenge.getChallenge(buttonName.substring(4));

			// Leave if EXIT
			if (buttonName.contains(plugin.getLanguageString("messages.exit"))) {
				player.closeInventory();
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == PlayerStatus.SPECTATOR)
				return;

			// Option for no challenge
			if (Challenge.none().equals(challenge)) {
				gamer.resetChallenges();
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.challengeAdd"));
			}

			// Remove a challenge
			else if (gamer.getChallenges().contains(challenge)) {
				gamer.removeChallenge(challenge);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.challengeDelete"));
			}

			// Add a challenge
			else {
				gamer.addChallenge(challenge);
				PlayerManager.notifySuccess(player, plugin.getLanguageString("confirms.challengeAdd"));
			}

			// Create scoreboard and update inventory
			GameManager.createBoard(gamer);
			player.openInventory(Inventories.createSelectChallengesInventory(gamer, arenaInstance));
		}

		// Stats menu for an arena
		else if (title.contains(plugin.getLanguageString("messages.arenaInfo")
				.substring(plugin.getLanguageString("messages.arenaInfo").indexOf("%s") + 2))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			if (buttonName.contains(plugin.getLanguageString("messages.customShopInv")))
				player.openInventory(GameManager.getArena(meta.getInteger1()).getMockCustomShop());

			else if (buttonName.contains(plugin.getLanguageString("messages.allowedKits")))
				player.openInventory(Inventories.createAllowedKitsInventory(meta.getInteger1(), true));
		}
	}

	// Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(CommunicationManager.format("&k")))
			return;

		// Check for community chest with shop inside it
		if (title.contains("Community Chest") && e.getInventory().contains(GameItems.shop())) {
			e.getInventory().removeItem(GameItems.shop());
			PlayerManager.giveItem((Player) e.getPlayer(), GameItems.shop(),
					plugin.getLanguageData().getString("inventoryFull"));
		}
	}

	// Handles arena naming
	@EventHandler
	public void onRename(SignGUIEvent e) {
		Arena arena = e.getArena();
		Player player = e.getPlayer();

		// Try updating name
		try {
			arena.setName(e.getLines()[2]);
			CommunicationManager.debugInfo(String.format("Name changed for arena %d!", arena.getArena()), 2);
		} catch (Exception err) {
			if (arena.getName() == null)
				GameManager.removeArena(arena.getArena());
			else PlayerManager.notifyFailure(player, "Invalid arena name!");
			return;
		}

		player.openInventory(Inventories.createArenaInventory(arena.getArena()));
	}
}
