package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import me.theguyhere.villagerdefense.plugin.inventories.Buttons;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.SignGUIEvent;
import me.theguyhere.villagerdefense.plugin.game.models.*;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.apache.commons.lang.math.NumberUtils;
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
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains(LanguageManager.names.communityChest))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// Prevent losing items by shift clicking in custom inventory
	@EventHandler
	public void onShiftClick(InventoryClickEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;

		// Check for shift click
		if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains(LanguageManager.names.communityChest))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// All click events in the inventories
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		// Get inventory title
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
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
		if (!title.contains(LanguageManager.names.communityChest) && 
				!title.contains("Custom Shop Editor"))
			e.setCancelled(true);

		// Save community chest
		else if (title.contains(LanguageManager.names.communityChest)) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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
			String path = "a" + meta.getArena().getArena() + ".customShop.";
			Arena arenaInstance = meta.getArena();

			// Exit menu
			if (Buttons.exit().equals(button)) {
				e.setCancelled(true);
				player.openInventory(Inventories.createShopsInventory(meta.getArena()));
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
				PlayerManager.giveItem(player, cursor.clone(), LanguageManager.errors.inventoryFull);
				player.setItemOnCursor(new ItemStack(Material.AIR));
				plugin.saveArenaData();
				player.openInventory(Inventories.createCustomItemsInventory(meta.getArena(), slot));
				return;
			}

			// Edit item
			else e.setCancelled(true);

			// Only open inventory for valid click
			if (Objects.requireNonNull(e.getCurrentItem()).getType() != Material.AIR)
				player.openInventory(Inventories.createCustomItemsInventory(meta.getArena(), slot));

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
				player.openInventory(Inventories.createArenaMenu(GameManager.getArena(slot)));

			// Open lobby menu
			else if (buttonName.contains("Lobby"))
				player.openInventory(Inventories.createLobbyMenu());

			// Open info boards menu
			else if (buttonName.contains("Info Boards"))
				player.openInventory(Inventories.createInfoBoardDashboard());

			// Open leaderboards menu
			else if (buttonName.contains("Leaderboards"))
				player.openInventory(Inventories.createLeaderboardDashboard());

			// Close inventory
			else if (buttonName.contains(LanguageManager.messages.exit))
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
				player.openInventory(Inventories.createLobbyMenu());
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
					player.openInventory(Inventories.createLobbyConfirmMenu());
				else PlayerManager.notifyFailure(player, "No lobby to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenasDashboard());
		}

		// Info board menu
		else if (title.contains("Info Boards")) {

			// Edit board
			if (Arrays.asList(Inventories.INFO_BOARD_MATS).contains(buttonType))
				player.openInventory(Inventories.createInfoBoardMenu(slot));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenasDashboard());
		}

		// Info board menu for a specific board
		else if (title.contains("Info Board ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			int num = meta.getId();
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
					player.openInventory(Inventories.createInfoBoardConfirmMenu(num));
				else PlayerManager.notifyFailure(player, "No info board to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createInfoBoardDashboard());
		}

		// Leaderboard menu
		else if (title.contains("Leaderboards")) {
			if (buttonName.contains("Total Kills Leaderboard"))
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());

			if (buttonName.contains("Top Kills Leaderboard"))
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());

			if (buttonName.contains("Total Gems Leaderboard"))
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());

			if (buttonName.contains("Top Balance Leaderboard"))
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());

			if (buttonName.contains("Top Wave Leaderboard"))
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenasDashboard());
		}

		// Total kills leaderboard menu
		else if (title.contains(CommunicationManager.format("&4&lTotal Kills Leaderboard"))) {
			String path = "leaderboard.totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalKills");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
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
					player.openInventory(Inventories.createTotalKillsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top kills leaderboard menu
		else if (title.contains(CommunicationManager.format("&c&lTop Kills Leaderboard"))) {
			String path = "leaderboard.topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topKills");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());
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
					player.openInventory(Inventories.createTopKillsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Total gems leaderboard menu
		else if (title.contains(CommunicationManager.format("&2&lTotal Gems Leaderboard"))) {
			String path = "leaderboard.totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "totalGems");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
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
					player.openInventory(Inventories.createTotalGemsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top balance leaderboard menu
		else if (title.contains(CommunicationManager.format("&a&lTop Balance Leaderboard"))) {
			String path = "leaderboard.topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topBalance");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
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
					player.openInventory(Inventories.createTopBalanceConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top wave leaderboard menu
		else if (title.contains(CommunicationManager.format("&9&lTop Wave Leaderboard"))) {
			String path = "leaderboard.topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGameManager().setLeaderboard(player.getLocation(), "topWave");
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());
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
					player.openInventory(Inventories.createTopWaveConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Menu for an arena
		else if (title.contains(CommunicationManager.format("&2&lEdit "))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed())
					NMSVersion.getCurrent().getNmsManager().nameArena(player, arenaInstance.getName(),
							arenaInstance.getArena() + 1);
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open portal menu
			else if (buttonName.contains("Portal and Leaderboard"))
				player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));

			// Open player menu
			else if (buttonName.contains("Player Settings"))
				player.openInventory(Inventories.createPlayersInventory(meta.getArena()));

			// Open mob menu
			else if (buttonName.contains("Mob Settings"))
				player.openInventory(Inventories.createMobsInventory(meta.getArena()));

			// Open shop menu
			else if (buttonName.contains("Shop Settings"))
				player.openInventory(Inventories.createShopsInventory(meta.getArena()));

			// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));

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
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createArenaConfirmMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Return to arenas menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenasDashboard());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove portal, close arena
					meta.getArena().removePortal();

					// Confirm and return
					PlayerManager.notifySuccess(player, "Portal removed!");
					player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));
				}
			}

			// Confirm to remove leaderboard
			else if (title.contains("Remove Leaderboard?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Delete arena leaderboard
					meta.getArena().removeArenaBoard();

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));
				}
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getArena()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setPlayerSpawn(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Spawn removed!");
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getArena()));
				}
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getArena()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setWaitingRoom(null);
					PlayerManager.notifySuccess(player, "Waiting room removed!");
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getArena()));
				}
			}

			// Confirm to remove monster spawn
			else if (title.contains("Remove Monster Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(), meta.getId()));

				// Remove the monster spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setMonsterSpawn(meta.getId(), null);
					if (arenaInstance.getMonsterSpawns().isEmpty())
						arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Mob spawn removed!");
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(), meta.getId()));
				}
			}

			// Confirm to remove villager spawn
			else if (title.contains("Remove Villager Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getArena(), meta.getId()));

				// Remove the villager spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setVillagerSpawn(meta.getId(), null);
					if (arenaInstance.getVillagerSpawns().isEmpty())
						arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Mob spawn removed!");
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getArena(), meta.getId()));
				}
			}

			// Confirm to remove custom item
			else if (title.contains("Remove Custom Item?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCustomItemsInventory(meta.getArena(), meta.getId()));

				// Remove custom item, then return to custom shop editor
				else if (buttonName.contains("YES")) {
					config.set("a" + meta.getArena() + ".customShop." + meta.getId(), null);
					plugin.saveArenaData();
					player.openInventory(meta.getArena().getCustomShopEditor());
				}
			}

			// Confirm to remove corner 1
			else if (title.contains("Remove Corner 1?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCorner1Inventory(meta.getArena()));

				// Remove corner 1, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setCorner1(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Corner 1 removed!");
					player.openInventory(Inventories.createCorner1Inventory(meta.getArena()));
				}
			}

			// Confirm to remove corner 2
			else if (title.contains("Remove Corner 2?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCorner1Inventory(meta.getArena()));

				// Remove corner 2, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = meta.getArena();

					arenaInstance.setCorner2(null);
					arenaInstance.setClosed(true);
					PlayerManager.notifySuccess(player, "Corner 2 removed!");
					player.openInventory(Inventories.createCorner2Inventory(meta.getArena()));
				}
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createLobbyMenu());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set("lobby", null);
					plugin.saveArenaData();
					plugin.getGameManager().reloadLobby();
					PlayerManager.notifySuccess(player, "Lobby removed!");
					player.openInventory(Inventories.createLobbyMenu());
				}
			}

			// Confirm to remove info board
			else if (title.contains("Remove Info Board?")) {
				String path = "infoBoard." + meta.getId();

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createInfoBoardMenu(meta.getId()));

					// Remove the info board, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove info board data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove info board
					plugin.getGameManager().removeInfoBoard(meta.getId());

					// Confirm and return
					PlayerManager.notifySuccess(player, "Info board removed!");
					player.openInventory(Inventories.createInfoBoardMenu(meta.getId()));
				}
			}

			// Confirm to remove total kills leaderboard
			else if (title.contains("Remove Total Kills Leaderboard?")) {
				String path = "leaderboard.totalKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalKillsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("totalKills");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
				}
			}

			// Confirm to remove top kills leaderboard
			else if (title.contains("Remove Top Kills Leaderboard?")) {
				String path = "leaderboard.topKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopKillsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topKills");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopKillsLeaderboardMenu());
				}
			}

			// Confirm to remove total gems leaderboard
			else if (title.contains("Remove Total Gems Leaderboard?")) {
				String path = "leaderboard.totalGems";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalGemsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("totalGems");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
				}
			}

			// Confirm to remove top balance leaderboard
			else if (title.contains("Remove Top Balance Leaderboard?")) {
				String path = "leaderboard.topBalance";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopBalanceLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topBalance");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
				}
			}

			// Confirm to remove top wave leaderboard
			else if (title.contains("Remove Top Wave Leaderboard?")) {
				String path = "leaderboard.topWave";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopWaveLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGameManager().removeLeaderboard("topWave");

					// Confirm and return
					PlayerManager.notifySuccess(player, "Leaderboard removed!");
					player.openInventory(Inventories.createTopWaveLeaderboardMenu());
				}
			}

			// Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createArenaMenu(meta.getArena()));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove data
					GameManager.removeArena(meta.getArena().getArena());

					// Confirm and return
					PlayerManager.notifySuccess(player, "Arena removed!");
					player.openInventory(Inventories.createArenasDashboard());
				}
			}
		}

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					PlayerManager.notifySuccess(player, "Portal set!");
					player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));
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
						player.openInventory(Inventories.createPortalConfirmInventory(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No portal to remove!");

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createPortalLeaderboardMenu(meta.getArena()));
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
					player.openInventory(Inventories.createArenaBoardConfirmInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				player.openInventory(Inventories.createPlayerSpawnInventory(meta.getArena()));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					player.openInventory(Inventories.createPlayersInventory(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				player.openInventory(Inventories.createWaitingRoomInventory(meta.getArena()));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxPlayerInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMinPlayerInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					PlayerManager.notifySuccess(player, "Spawn set!");
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getArena()));
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
						player.openInventory(Inventories.createSpawnConfirmInventory(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No player spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersInventory(meta.getArena()));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					PlayerManager.notifySuccess(player, "Waiting room set!");
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getArena()));
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
						player.openInventory(Inventories.createWaitingConfirmInventory(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No waiting room to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersInventory(meta.getArena()));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getArena()));
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxPlayers(++current);
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersInventory(meta.getArena()));
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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
				player.openInventory(Inventories.createMinPlayerInventory(meta.getArena()));
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
				player.openInventory(Inventories.createMinPlayerInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersInventory(meta.getArena()));
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				player.openInventory(Inventories.createMonsterSpawnInventory(meta.getArena()));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getArena()));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createSpawnTableInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					player.openInventory(Inventories.createMobsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Edit spawn
			if (Arrays.asList(Inventories.MONSTER_MATS).contains(buttonType))
				player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(), slot));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMobsInventory(meta.getArena()));
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Monster spawn set!");
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(), meta.getId()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Monster spawn relocated!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance.getMonsterSpawn(meta.getId()).getLocation());
					player.closeInventory();
				} catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No monster spawn to teleport to!");
				}

			// Center monster spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
						arenaInstance.centerMonsterSpawn(meta.getId());
						PlayerManager.notifySuccess(player, "Monster spawn centered!");
					} else PlayerManager.notifyFailure(player, "No monster spawn to center!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Set monster type
			else if (buttonName.contains("Type"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
						arenaInstance.setMonsterSpawnType(meta.getId(),
								(arenaInstance.getMonsterSpawnType(meta.getId()) + 1) % 3);
						player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(),
								meta.getId()));
						PlayerManager.notifySuccess(player, "Monster spawn type changed!");
					} else PlayerManager.notifyFailure(player, "No monster spawn to set type!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createMonsterSpawnConfirmInventory(meta.getArena(),
								meta.getId()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No monster spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMonsterSpawnInventory(meta.getArena()));
		}

		// Villager spawn menu for an arena
		else if (title.contains("Villager Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			// Edit spawn
			if (Arrays.asList(Inventories.VILLAGER_MATS).contains(buttonType))
				player.openInventory(Inventories.createVillagerSpawnMenu(meta.getArena(), slot));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMobsInventory(meta.getArena()));
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Villager spawn set!");
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getArena(), meta.getId()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Villager spawn set!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance.getVillagerSpawn(meta.getId()).getLocation());
					player.closeInventory();
				} catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No villager spawn to teleport to!");
				}

			// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getVillagerSpawn(meta.getId()) != null) {
						arenaInstance.centerVillagerSpawn(meta.getId());
						PlayerManager.notifySuccess(player, "Villager spawn centered!");
					} else PlayerManager.notifyFailure(player, "No villager spawn to center!");
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getVillagerSpawn(meta.getId()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createVillagerSpawnConfirmInventory(meta.getArena(),
								meta.getId()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No villager spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getArena()));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

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
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createMobsInventory(meta.getArena()));
				return;
			}

			// Reload inventory
			player.openInventory(Inventories.createSpawnTableInventory(meta.getArena()));
		}

		// Shop settings menu for an arena
		else if (title.contains("Shop Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					player.openInventory(arenaInstance.getCustomShopEditor());
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					player.openInventory(Inventories.createShopsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					player.openInventory(Inventories.createShopsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle enchants shop
			else if (buttonName.contains("Enchants Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setEnchants(!arenaInstance.hasEnchants());
					player.openInventory(Inventories.createShopsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle community chest
			else if (buttonName.contains("Community Chest:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCommunity(!arenaInstance.hasCommunity());
					player.openInventory(Inventories.createShopsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					player.openInventory(Inventories.createShopsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Menu for editing a specific custom item
		else if (title.contains("Edit Item")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			String path = "a" + meta.getArena().getArena() + ".customShop.";
			ItemStack item = config.getItemStack(path + meta.getId());
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
			}

			// Delete item
			else if (buttonName.contains("DELETE")) {
				player.openInventory(Inventories.createCustomItemConfirmInventory(meta.getArena(), meta.getId()));
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
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
				config.set(path + meta.getId(), item);
			}

			// Exit
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(meta.getArena().getCustomShopEditor());
				return;
			}

			// Save changes and refresh GUI
			plugin.saveArenaData();
			player.openInventory(Inventories.createCustomItemsInventory(meta.getArena(), meta.getId()));
		}

		// Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxWaveInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(Inventories.createAllowedKitsInventory(arenaInstance, false));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Late Arrival:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setLateArrival(!arenaInstance.hasLateArrival());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle experience drop
			else if (buttonName.contains("Experience Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setExpDrop(!arenaInstance.hasExpDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle item drop
			else if (buttonName.contains("Item Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemDrop(!arenaInstance.hasGemDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit arena bounds
			else if (buttonName.contains("Arena Bounds"))
				player.openInventory(Inventories.createBoundsInventory(meta.getArena()));

			// Edit wolf cap
			else if (buttonName.contains("Wolf Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWolfCapInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit iron golem cap
			else if (buttonName.contains("Iron Golem Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createGolemCapInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				player.openInventory(Inventories.createSoundsInventory(meta.getArena()));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createCopySettingsInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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

				player.openInventory(Inventories.createMaxWaveInventory(meta.getArena()));
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(-1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getArena()));
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getArena()));
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

				player.openInventory(Inventories.createMaxWaveInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getArena()));
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(-1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getArena()));
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getArena()));
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

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Set to Easy
			if (buttonName.contains("Easy")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Easy");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Medium");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Hard");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Insane");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel(null);
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Set to 1
			if (buttonName.contains("1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(1);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getArena()));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(2);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getArena()));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(3);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getArena()));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(4);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Allowed kits display for an arena
		else if (title.contains(LanguageManager.messages.allowedKits + ": ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				assert meta != null;
				player.openInventory(Inventories.createArenaInfoInventory(meta.getArena()));
			}
		}

		// Allowed kits menu for an arena
		else if (title.contains(LanguageManager.messages.allowedKits)) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			String kit = buttonName.substring(4);
			Arena arenaInstance = meta.getArena();
			List<String> banned = arenaInstance.getBannedKits();

			// Toggle a kit
			if (!(kit.equals(LanguageManager.names.giftKits) || kit.equals(LanguageManager.names.abilityKits) ||
					kit.equals(LanguageManager.names.effectKits) || kit.equals(LanguageManager.messages.exit))) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (banned.contains(kit))
					banned.remove(kit);
				else banned.add(kit);
				arenaInstance.setBannedKits(banned);
				player.openInventory(Inventories.createAllowedKitsInventory(arenaInstance, false));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Arena bounds menu for an arena
		else if (title.contains("Arena Bounds")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Edit corner 1
			if (buttonName.contains("Corner 1"))
				player.openInventory(Inventories.createCorner1Inventory(meta.getArena()));

			// Edit corner 2
			else if (buttonName.contains("Corner 2"))
				player.openInventory(Inventories.createCorner2Inventory(meta.getArena()));

			// Toggle border particles
			else if (buttonName.contains("Border Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setBorderParticles(!arenaInstance.hasBorderParticles());
					player.openInventory(Inventories.createBoundsInventory(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Corner 1 menu for an arena
		else if (title.contains("Corner 1")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
					player.openInventory(Inventories.createBoundsInventory(meta.getArena()));
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
						player.openInventory(Inventories.createCorner1ConfirmInventory(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 1 to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createBoundsInventory(meta.getArena()));
		}

		// Corner 2 menu for an arena
		else if (title.contains("Corner 2")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
					player.openInventory(Inventories.createBoundsInventory(meta.getArena()));
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
						player.openInventory(Inventories.createCorner2ConfirmInventory(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 2 to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createBoundsInventory(meta.getArena()));
		}

		// Wolf cap menu for an arena
		else if (title.contains("Wolf Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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
				player.openInventory(Inventories.createWolfCapInventory(meta.getArena()));
			}

			// Increase wolf cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWolfCap(++current);
				player.openInventory(Inventories.createWolfCapInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Iron golem cap menu for an arena
		else if (title.contains("Iron Golem Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();
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
				player.openInventory(Inventories.createGolemCapInventory(meta.getArena()));
			}

			// Increase iron golem cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setgolemCap(++current);
				player.openInventory(Inventories.createGolemCapInventory(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaitSoundInventory(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle ability sound
			else if (buttonName.contains("Ability")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setAbilitySound(!arenaInstance.hasAbilitySound());
					player.openInventory(Inventories.createSoundsInventory(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
		}

		// Waiting sound menu for an arena
		else if (title.contains("Waiting Sound:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = meta.getArena();

			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createSoundsInventory(meta.getArena()));

			// Set sound
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaitingSound(buttonName.toLowerCase().substring(4));
				player.openInventory(Inventories.createWaitSoundInventory(meta.getArena()));
			}
		}

		// Menu to copy game settings
		else if (title.contains("Copy Game Settings")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arena1 = meta.getArena();

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
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createGameSettingsInventory(meta.getArena()));
				return;
			}

			// Save updates
			PlayerManager.notifySuccess(player, "Game settings copied!");
		}

		// In-game item shop menu
		else if (title.contains(LanguageManager.names.itemShop)) {
			Arena arenaInstance;

			// See if the player is in a game
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (Exception err) {
				return;
			}

			// Open weapon shop
			if (buttonName.contains(LanguageManager.names.weaponShop))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getWeaponShop());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.normalShop);

			// Open armor shop
			else if (buttonName.contains(LanguageManager.names.armorShop))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getArmorShop());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.normalShop);

			// Open consumables shop
			else if (buttonName.contains(LanguageManager.names.consumableShop))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getConsumeShop());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.normalShop);

			// Open enchant shop
			else if (buttonName.contains(LanguageManager.names.enchantShop))
				if (arenaInstance.hasEnchants())
					player.openInventory(Inventories.createEnchantShop());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.enchantShop);

			// Open custom shop
			else if (buttonName.contains(LanguageManager.names.customShop))
				if (arenaInstance.hasCustom())
					player.openInventory(arenaInstance.getCustomShop());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.customShop);

			// Open community chest
			else if (buttonName.contains(LanguageManager.names.communityChest))
				if (arenaInstance.hasCommunity())
					player.openInventory(arenaInstance.getCommunityChest());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.communityChest);
		}

		// Mock custom shop for an arena
		else if (title.contains(LanguageManager.names.customShop + ":")) {
			Arena arenaInstance = Objects.requireNonNull(GameManager.getArena(title.substring(19)));
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaInfoInventory(arenaInstance));
		}

		// In-game shops
		else if (title.contains(LanguageManager.names.weaponShop) ||
				title.contains(LanguageManager.names.armorShop) ||
				title.contains(LanguageManager.names.consumableShop) ||
				title.contains(LanguageManager.names.customShop)) {
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
			if (buttonName.contains(LanguageManager.messages.exit)) {
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
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
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
				PlayerManager.notifySuccess(player, LanguageManager.confirms.helmet);
			} else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getChestplate() == null) {
				equipment.setChestplate(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.chestplate);
			} else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getLeggings() == null) {
				equipment.setLeggings(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.leggings);
			} else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getBoots() == null) {
				equipment.setBoots(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.boots);
			} else {
				PlayerManager.giveItem(player, buy, LanguageManager.errors.inventoryFull);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
			}
		}
		else if (title.contains(LanguageManager.names.enchantShop)) {
			Arena arenaInstance;

			// Attempt to get arena
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (Exception err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
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
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
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
			if (enchant.equals(LanguageManager.enchants.knockback.split(" ")[0]))
				give = EnchantingBook.knockback();
			else if (enchant.equals(LanguageManager.enchants.sweepingEdge.split(" ")[0]))
				give = EnchantingBook.sweepingEdge();
			else if (enchant.equals(LanguageManager.enchants.smite.split(" ")[0]))
				give = EnchantingBook.smite();
			else if (enchant.equals(LanguageManager.enchants.sharpness.split(" ")[0]))
				give = EnchantingBook.sharpness();
			else if (enchant.equals(LanguageManager.enchants.fireAspect.split(" ")[0]))
				give = EnchantingBook.fireAspect();
			else if (enchant.equals(LanguageManager.enchants.punch.split(" ")[0]))
				give = EnchantingBook.punch();
			else if (enchant.equals(LanguageManager.enchants.piercing.split(" ")[0]))
				give = EnchantingBook.piercing();
			else if (enchant.equals(LanguageManager.enchants.quickCharge.split(" ")[0]))
				give = EnchantingBook.quickCharge();
			else if (enchant.equals(LanguageManager.enchants.power.split(" ")[0]))
				give = EnchantingBook.power();
			else if (enchant.equals(LanguageManager.enchants.loyalty.split(" ")[0]))
				give = EnchantingBook.loyalty();
			else if (enchant.equals(LanguageManager.enchants.flame.split(" ")[0]))
				give = EnchantingBook.flame();
			else if (enchant.equals(LanguageManager.enchants.multishot.split(" ")[0]))
				give = EnchantingBook.multishot();
			else if (enchant.equals(LanguageManager.enchants.infinity.split(" ")[0]))
				give = EnchantingBook.infinity();
			else if (enchant.equals(LanguageManager.enchants.blastProtection.split(" ")[0]))
				give = EnchantingBook.blastProtection();
			else if (enchant.equals(LanguageManager.enchants.thorns.split(" ")[0]))
				give = EnchantingBook.thorns();
			else if (enchant.equals(LanguageManager.enchants.projectileProtection.split(" ")[0]))
				give = EnchantingBook.projectileProtection();
			else if (enchant.equals(LanguageManager.enchants.protection.split(" ")[0]))
				give = EnchantingBook.protection();
			else if (enchant.equals(LanguageManager.enchants.unbreaking.split(" ")[0]))
				give = EnchantingBook.unbreaking();
			else if (enchant.equals(LanguageManager.enchants.mending.split(" ")[0]))
				give = EnchantingBook.mending();
			else give = null;

			PlayerManager.giveItem(player, give, LanguageManager.errors.inventoryFull);
			PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
		}

		// Stats menu for a player
		else if (title.contains(LanguageManager.messages.playerStatistics
				.substring(LanguageManager.messages.playerStatistics.indexOf("%s") + 2))) {
			String raw = LanguageManager.messages.playerStatistics;
			String name = title.substring(raw.indexOf("%s") + 6, title.length() - raw.length() + raw.indexOf("%s") + 2);
			if (buttonName.contains("Kits"))
				player.openInventory(Inventories.createPlayerKitsInventory(name, player.getName()));
		}

		// Kits menu for a player
		else if (title.contains(LanguageManager.messages.playerKits
				.substring(LanguageManager.messages.playerKits.indexOf("%s") + 2))) {
			String raw = LanguageManager.messages.playerKits;
			FileConfiguration playerData = plugin.getPlayerData();
			String name = title.substring(raw.indexOf("%s") + 6, title.length() - raw.length() + raw.indexOf("%s") + 2);
			Kit kit = Kit.getKit(buttonName.substring(4));
			String path = name + ".kits.";

			if (buttonName.contains(LanguageManager.messages.exit)) {
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
						PlayerManager.notifySuccess(player, LanguageManager.confirms.kitBuy);
					} else PlayerManager.notifyFailure(player, LanguageManager.errors.kitBuy);
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
						PlayerManager.notifySuccess(player, LanguageManager.confirms.kitBuy);
					} else PlayerManager.notifyFailure(player, LanguageManager.errors.kitBuy);
				} else {
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(++kitLevel)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(kitLevel));
						playerData.set(path + kit.getName(), kitLevel);
						PlayerManager.notifySuccess(player, LanguageManager.confirms.kitUpgrade);
					} else PlayerManager.notifyFailure(player, LanguageManager.errors.kitUpgrade);
				}
			}

			plugin.savePlayerData();
			player.openInventory(Inventories.createPlayerKitsInventory(name, name));
		}

		// Kit selection menu for an arena
		else if (title.contains(" " + LanguageManager.messages.kits)) {
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
			if (buttonName.contains(LanguageManager.messages.exit)) {
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
					PlayerManager.notifySuccess(player, LanguageManager.confirms.kitSelect);
				} else {
					PlayerManager.notifyFailure(player, LanguageManager.errors.kitSelect);
					return;
				}
			}

			// Multiple tier kits
			else {
				if (playerData.getInt(path + kit.getName()) < 1) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.kitSelect);
					return;
				}
				gamer.setKit(kit.setKitLevel(playerData.getInt(path + kit.getName())));
				PlayerManager.notifySuccess(player, LanguageManager.confirms.kitSelect);
			}

			// Close inventory and create scoreboard
			player.closeInventory();
			GameManager.createBoard(gamer);
		}

		// Challenge selection menu for an arena
		else if (title.contains(" " + LanguageManager.messages.challenges)) {
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
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.closeInventory();
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == PlayerStatus.SPECTATOR)
				return;

			// Option for no challenge
			if (Challenge.none().equals(challenge)) {
				gamer.resetChallenges();
				PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeAdd);
			}

			// Remove a challenge
			else if (gamer.getChallenges().contains(challenge)) {
				gamer.removeChallenge(challenge);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeDelete);
			}

			// Add a challenge
			else {
				gamer.addChallenge(challenge);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeAdd);
			}

			// Create scoreboard and update inventory
			GameManager.createBoard(gamer);
			player.openInventory(Inventories.createSelectChallengesInventory(gamer, arenaInstance));
		}

		// Stats menu for an arena
		else if (title.contains(LanguageManager.messages.arenaInfo
				.substring(LanguageManager.messages.arenaInfo.indexOf("%s") + 2))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			if (buttonName.contains(LanguageManager.messages.customShopInv))
				player.openInventory(meta.getArena().getMockCustomShop());

			else if (buttonName.contains(LanguageManager.messages.allowedKits))
				player.openInventory(Inventories.createAllowedKitsInventory(meta.getArena(), true));
		}
	}

	// Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;

		// Check for community chest with shop inside it
		if (title.contains("Community Chest") && e.getInventory().contains(GameItems.shop())) {
			e.getInventory().removeItem(GameItems.shop());
			PlayerManager.giveItem((Player) e.getPlayer(), GameItems.shop(), LanguageManager.errors.inventoryFull);
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
			PlayerManager.notifyFailure(player, "Invalid arena name!");
			return;
		}

		player.openInventory(Inventories.createArenaMenu(arena));
	}
}
