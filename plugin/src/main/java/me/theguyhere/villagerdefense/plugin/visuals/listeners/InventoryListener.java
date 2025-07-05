package me.theguyhere.villagerdefense.plugin.visuals.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.data.*;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.InvalidNameException;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.items.EnchantingBook;
import me.theguyhere.villagerdefense.plugin.items.GameItems;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryID;
import me.theguyhere.villagerdefense.plugin.visuals.InventoryMeta;
import me.theguyhere.villagerdefense.plugin.data.listeners.ChatListener;
import me.theguyhere.villagerdefense.plugin.items.ItemManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class InventoryListener implements Listener {
	// Prevent losing items by drag clicking in custom inventory
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (meta.getInventoryID() != InventoryID.COMMUNITY_CHEST_INVENTORY)
			e.setCancelled(true);
		else meta.getArena().setCommunityChest(e.getInventory());
	}

	// Prevent losing items by shift clicking in custom inventory
	@EventHandler
	public void onShiftClick(InventoryClickEvent e) {
		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

		// Check for shift click
		if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (meta.getInventoryID() != InventoryID.COMMUNITY_CHEST_INVENTORY)
			e.setCancelled(true);
		else meta.getArena().setCommunityChest(e.getInventory());
	}

	// Prevent items from being removed from the game
	@EventHandler
	public void onDragOther(InventoryDragEvent e) {
		// Ignore plugin inventories
		if (e.getInventory().getHolder() instanceof InventoryMeta)
			return;

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER)
			return;

		// Ignore players that aren't part of an arena
		Player player = (Player) e.getWhoClicked();
		try {
			GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event
		e.setCancelled(true);
	}
	@EventHandler
	public void onClickOther(InventoryClickEvent e) {
		// Ignore plugin inventories
		if (e.getInventory().getHolder() instanceof InventoryMeta)
			return;

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER || e.getView().getType() == InventoryType.CRAFTING)
			return;

		// Ignore players that aren't part of an arena
		Player player = (Player) e.getWhoClicked();
		try {
			GameManager.getArena(player);
		} catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event
		e.setCancelled(true);
	}

	// All click events in the inventories
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		// Get inventory title
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
		InventoryID invID = meta.getInventoryID();

		// Ignore null inventories
		if (e.getClickedInventory() == null)
			return;

		// Debugging info
		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Inventory Item: " + e.getCurrentItem());
		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Cursor Item: " + e.getCursor());
		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Clicked Inventory: " + e.getClickedInventory());
		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Inventory Name: ", title);

		// Cancel the event if the inventory isn't the community chest or custom shop editor to prevent changing the GUI
		if (invID != InventoryID.COMMUNITY_CHEST_INVENTORY &&
				invID != InventoryID.CUSTOM_SHOP_EDITOR_MENU)
			e.setCancelled(true);

		// Save community chest
		else if (invID == InventoryID.COMMUNITY_CHEST_INVENTORY)
			meta.getArena().setCommunityChest(e.getInventory());

		// Ignore clicks in player inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER)
			return;

		// Get button information
		ItemStack button = e.getCurrentItem();
		Material buttonType;
		String buttonName;

		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();

		// Custom shop editor for an arena
		if (invID == InventoryID.CUSTOM_SHOP_EDITOR_MENU) {
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Custom shop editor being used.");
			ItemStack cursor = e.getCursor();
			assert cursor != null;
			Arena arenaInstance = meta.getArena();

			// Exit menu
			if (InventoryButtons.exit().equals(button)) {
				e.setCancelled(true);
				player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
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
				List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
                assert lore != null;
                lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a0"));
				itemMeta.setLore(lore);
				ItemStack copy = cursor.clone();
				copy.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(meta.getArena().getId(), slot, copy);
				PlayerManager.giveItem(player, cursor.clone(), LanguageManager.errors.inventoryFull);
				player.setItemOnCursor(new ItemStack(Material.AIR));
				player.openInventory(Inventories.createCustomItemsMenu(meta.getArena(), slot));
				return;
			}

			// Edit item
			else e.setCancelled(true);

			// Only open inventory for valid click
			if (Objects.requireNonNull(e.getCurrentItem()).getType() != Material.AIR)
				player.openInventory(Inventories.createCustomItemsMenu(meta.getArena(), slot));

			return;
		} else {
			// Ignore null items
			if (button == null)
				return;

			// Get button type and name
			buttonType = button.getType();
			buttonName = Objects.requireNonNull(button.getItemMeta()).getDisplayName();
		}

		// Main menu
		if (invID == InventoryID.MAIN_MENU) {
			// Open lobby menu
			if (buttonName.contains("Lobby"))
				player.openInventory(Inventories.createLobbyMenu());

			// Open info boards dashboard
			else if (buttonName.contains("Info Boards"))
				player.openInventory(Inventories.createInfoBoardDashboard());

			// Open leaderboards dashboard
			else if (buttonName.contains("Leaderboards"))
				player.openInventory(Inventories.createLeaderboardDashboard());

			// Open arenas dashboard
			else if (buttonName.contains("Arenas"))
				player.openInventory(Inventories.createArenasDashboard());

			// Close inventory
			else if (buttonName.contains(LanguageManager.messages.exit))
				closeInv(player);
		}

		// Arenas dashboard
		else if (invID == InventoryID.ARENA_DASHBOARD) {
			// Edit existing arena
			if (buttonType == Material.EMERALD_BLOCK)
				try {
					player.openInventory(Inventories.createArenaMenu(GameManager.getArena(buttonName.substring(9))));
				} catch (ArenaNotFoundException ignored) {
				}

			// Create new arena after getting name
			else if (buttonName.contains(CommunicationManager.format("&a&lNew "))) {
				// Close current inventory
				closeInv(player);

				// Prompt for name
				ChatListener.ChatTask task = (msg) -> {
					// Check for cancelling
					if (msg.equalsIgnoreCase("cancel")) {
						Bukkit
							.getScheduler()
							.scheduleSyncDelayedTask(Main.plugin,
								() -> player.openInventory(Inventories.createArenasDashboard()));
						return;
					}

					// Create new arena
					Arena arena = new Arena(GameManager.newArenaID());
					GameManager.addArena(GameManager.newArenaID(), arena);

					// Try updating name
					try {
						arena.setName(msg.trim());
						CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Name changed for arena %s!",
                            String.valueOf(arena.getId()));
						Bukkit
							.getScheduler()
							.scheduleSyncDelayedTask(Main.plugin,
								() -> player.openInventory(Inventories.createArenaMenu(arena)));
					} catch (InvalidNameException err) {
						if (arena.getName() == null)
							GameManager.removeArena(arena.getId());
						PlayerManager.notifyFailure(player, "Invalid arena name!");
					}
				};
				ChatListener.addTask(player, task, "Enter the new unique name for the arena chat, or type CANCEL to " +
					"quit:");
			}

			// Return to main menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMainMenu());

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createArenasDashboard(meta.getPage() - 1));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createArenasDashboard(meta.getPage() + 1));
		}

		// Lobby menu
		else if (invID == InventoryID.LOBBY_MENU) {
			// Create lobby
			if (buttonName.contains("Create Lobby")) {
				GameDataManager.setLobbyLocation(player.getLocation());
				GameManager.reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby set!");
				player.openInventory(Inventories.createLobbyMenu());
			}

			// Relocate lobby
			else if (buttonName.contains("Relocate Lobby")) {
				GameDataManager.setLobbyLocation(player.getLocation());
				GameManager.reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby relocated!");
			}

			// Teleport player to lobby
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLobbyLocation());
					closeInv(player);
				}
				catch (NoSuchPathException | BadDataException err) {
					PlayerManager.notifyFailure(player, "No lobby to teleport to!");
				}
			}

			// Center lobby
			else if (buttonName.contains("Center")) {
				try {
					GameDataManager.centerLobbyLocation();
					PlayerManager.notifySuccess(player, "Lobby centered!");
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No lobby to center!");
				}
			}

			// Remove lobby
			else if (buttonName.contains("REMOVE")) {
				if (GameManager.getArenas().values().stream().filter(Objects::nonNull)
					.anyMatch(arena -> !arena.isClosed())) {
					PlayerManager.notifyFailure(player, "All arenas must be closed to modify this!");
				}
				else if (GameDataManager.hasLobby()) {
					player.openInventory(Inventories.createLobbyConfirmMenu());
				}
				else PlayerManager.notifyFailure(player, "No lobby to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMainMenu());
		}

		// Info board dashboard
		else if (invID == InventoryID.INFO_BOARD_DASHBOARD) {
			// Edit board
			if (buttonType == Material.BIRCH_SIGN)
				player.openInventory(Inventories.createInfoBoardMenu(Integer.parseInt(buttonName.split(" ")[2])));

			// Create new
			else if (buttonName.contains(CommunicationManager.format("&a&lNew ")))
				player.openInventory(Inventories.createInfoBoardMenu(GameManager.newInfoBoardID()));

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createInfoBoardDashboard(meta.getPage() - 1));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createInfoBoardDashboard(meta.getPage() + 1));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMainMenu());
		}

		// Info board menu for a specific board
		else if (invID == InventoryID.INFO_BOARD_MENU) {
			int id = meta.getId();

			// Create board
			if (buttonName.contains("Create")) {
				GameManager.setInfoBoard(id, player.getLocation());
				PlayerManager.notifySuccess(player, "Info board set!");
				player.openInventory(Inventories.createInfoBoardMenu(id));
			}

			// Relocate board
			else if (buttonName.contains("Relocate")) {
				GameManager.setInfoBoard(id, player.getLocation());
				PlayerManager.notifySuccess(player, "Info board relocated!");
			}

			// Teleport player to info board
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getInfoBoardLocation(id));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No info board to teleport to!");
				}
			}

			// Center info board
			else if (buttonName.contains("Center")) {
				if (!GameDataManager.hasInfoBoard(id)) {
					PlayerManager.notifyFailure(player, "No info board to center!");
					return;
				}
				GameManager.centerInfoBoard(id);
				PlayerManager.notifySuccess(player, "Info board centered!");
			}

			// Remove info board
			else if (buttonName.contains("REMOVE")) {
				if (!GameDataManager.hasInfoBoard(id))
					player.openInventory(Inventories.createInfoBoardConfirmMenu(id));
				else PlayerManager.notifyFailure(player, "No info board to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createInfoBoardDashboard());
		}

		// Leaderboard dashboard
		else if (invID == InventoryID.LEADERBOARD_DASHBOARD) {
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
				player.openInventory(Inventories.createMainMenu());
		}

		// Total kills leaderboard menu
		else if (invID == InventoryID.TOTAL_KILLS_LEADERBOARD_MENU) {
			String type = "totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLeaderboardLocation(type));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				}
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameDataManager.hasLeaderboard(type)) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameManager.centerLeaderboard(type);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE")) {
				if (GameDataManager.hasLeaderboard(type)) {
					player.openInventory(Inventories.createTotalKillsConfirmMenu());
				} else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top kills leaderboard menu
		else if (invID == InventoryID.TOP_KILLS_LEADERBOARD_MENU) {
			String type = "topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLeaderboardLocation(type));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				}
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameDataManager.hasLeaderboard(type)) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameManager.centerLeaderboard(type);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE")) {
				if (GameDataManager.hasLeaderboard(type)) {
					player.openInventory(Inventories.createTopKillsConfirmMenu());
				} else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Total gems leaderboard menu
		else if (invID == InventoryID.TOTAL_GEMS_LEADERBOARD_MENU) {
			String type = "totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLeaderboardLocation(type));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				}
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameDataManager.hasLeaderboard(type)) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameManager.centerLeaderboard(type);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE")) {
				if (GameDataManager.hasLeaderboard(type)) {
					player.openInventory(Inventories.createTotalGemsConfirmMenu());
				} else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top balance leaderboard menu
		else if (invID == InventoryID.TOP_BALANCE_LEADERBOARD_MENU) {
			String type = "topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLeaderboardLocation(type));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				}
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameDataManager.hasLeaderboard(type)) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameManager.centerLeaderboard(type);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE")) {
				if (GameDataManager.hasLeaderboard(type)) {
					player.openInventory(Inventories.createTopBalanceConfirmMenu());
				} else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top wave leaderboard menu
		else if (invID == InventoryID.TOP_WAVE_LEADERBOARD_MENU) {
			String type = "topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameManager.setLeaderboard(type, player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(GameDataManager.getLeaderboardLocation(type));
					closeInv(player);
				}
				catch (BadDataException | NoSuchPathException err) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
				}
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameDataManager.hasLeaderboard(type)) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameManager.centerLeaderboard(type);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE")) {
				if (GameDataManager.hasLeaderboard(type)) {
					player.openInventory(Inventories.createTopWaveConfirmMenu());
				} else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Menu for an arena
		else if (invID == InventoryID.ARENA_MENU) {
			Arena arenaInstance = meta.getArena();

			// Prompt for new name
			if (buttonName.contains("Edit Name")) {
				if (arenaInstance.isClosed()) {
					// Close current inventory
					closeInv(player);

					ChatListener.ChatTask task = (msg) -> {
						// Check for cancelling
						if (msg.equalsIgnoreCase("cancel")) {
							Bukkit
								.getScheduler()
								.scheduleSyncDelayedTask(Main.plugin,
									() -> player.openInventory(Inventories.createArenaMenu(arenaInstance)));
							return;
						}

						// Try updating name
						try {
							arenaInstance.setName(msg.trim());
							CommunicationManager.debugInfo(CommunicationManager.DebugLevel.VERBOSE, "Name changed for arena %s!",
                                String.valueOf(arenaInstance.getId())
							);
							Bukkit
								.getScheduler()
								.scheduleSyncDelayedTask(Main.plugin,
									() -> player.openInventory(Inventories.createArenaMenu(arenaInstance)));
						}
						catch (InvalidNameException err) {
							if (arenaInstance.getName() == null)
								GameManager.removeArena(arenaInstance.getId());
							PlayerManager.notifyFailure(player, "Invalid arena name!");
						}
					};
					ChatListener.addTask(
						player, task, "Enter the new unique name for the arena chat, or type CANCEL to " +
							"quit:");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Open portal menu
			else if (buttonName.contains("Arena Portal"))
				player.openInventory(Inventories.createPortalMenu(meta.getArena()));

			// Open leaderboard menu
			else if (buttonName.contains("Arena Leaderboard"))
				player.openInventory(Inventories.createArenaBoardMenu(meta.getArena()));

			// Open player menu
			else if (buttonName.contains("Player Settings"))
				player.openInventory(Inventories.createPlayersMenu(meta.getArena()));

			// Open mob menu
			else if (buttonName.contains("Mob Settings"))
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));

			// Open shop menu
			else if (buttonName.contains("Shop Settings"))
				player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));

			// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));

			// Toggle arena close
			else if (buttonName.contains("Close")) {
				// Arena currently closed
				if (arenaInstance.isClosed()) {
					// No lobby
					if (!GameDataManager.hasLobby()) {
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
					player.openInventory(Inventories.createArenaConfirmMenu(arenaInstance));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Return to arenas menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenasDashboard());
		}

		// Confirm to remove portal
		else if (invID == InventoryID.PORTAL_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createPortalMenu(meta.getArena()));

			// Remove the portal, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove portal, close arena
				meta.getArena().removePortal();

				// Confirm and return
				PlayerManager.notifySuccess(player, "Portal removed!");
				player.openInventory(Inventories.createPortalMenu(meta.getArena()));
			}
		}

		// Confirm to remove arena leaderboard
		else if (invID == InventoryID.ARENA_BOARD_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createArenaBoardMenu(meta.getArena()));

			// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Delete arena leaderboard
				meta.getArena().removeArenaBoard();

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createArenaBoardMenu(meta.getArena()));
			}
		}

		// Confirm to remove player spawn
		else if (invID == InventoryID.SPAWN_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createPlayerSpawnMenu(meta.getArena()));

			// Remove spawn, then return to previous menu
			else if (buttonName.contains("YES")) {
				Arena arenaInstance = meta.getArena();

				arenaInstance.setPlayerSpawn(null);
				arenaInstance.setClosed(true);
				PlayerManager.notifySuccess(player, "Spawn removed!");
				player.openInventory(Inventories.createPlayerSpawnMenu(meta.getArena()));
			}
		}

		// Confirm to remove waiting room
		else if (invID == InventoryID.WAITING_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createWaitingRoomMenu(meta.getArena()));

			// Remove spawn, then return to previous menu
			else if (buttonName.contains("YES")) {
				Arena arenaInstance = meta.getArena();

				arenaInstance.setWaitingRoom(null);
				PlayerManager.notifySuccess(player, "Waiting room removed!");
				player.openInventory(Inventories.createWaitingRoomMenu(meta.getArena()));
			}
		}

		// Confirm to remove monster spawn
		else if (invID == InventoryID.MONSTER_SPAWN_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createMonsterSpawnMenu(meta.getArena(), meta.getId()));

			// Remove the monster spawn, then return to dashboard
			else if (buttonName.contains("YES")) {
				Arena arenaInstance = meta.getArena();

				arenaInstance.setMonsterSpawn(meta.getId(), null);
				if (arenaInstance.getMonsterSpawns().isEmpty())
					arenaInstance.setClosed(true);
				PlayerManager.notifySuccess(player, "Mob spawn removed!");
				player.openInventory(Inventories.createMonsterSpawnDashboard(meta.getArena()));
			}
		}

		// Confirm to remove villager spawn
		else if (invID == InventoryID.VILLAGER_SPAWN_CONFIRM_MENU) {
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
				player.openInventory(Inventories.createVillagerSpawnDashboard(meta.getArena()));
			}
		}

		// Confirm to remove custom item
		else if (invID == InventoryID.CUSTOM_ITEM_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createCustomItemsMenu(meta.getArena(), meta.getId()));

			// Remove custom item, then return to custom shop editor
			else if (buttonName.contains("YES")) {
				ArenaDataManager.removeCustomShopItem(meta.getArena().getId(), meta.getId());
				player.openInventory(meta.getArena().getCustomShopEditorMenu());
			}
		}

		// Confirm to remove corner 1
		else if (invID == InventoryID.CORNER_1_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createCorner1Menu(meta.getArena()));

			// Remove corner 1, then return to previous menu
			else if (buttonName.contains("YES")) {
				Arena arenaInstance = meta.getArena();

				arenaInstance.setCorner1(null);
				arenaInstance.setClosed(true);
				PlayerManager.notifySuccess(player, "Corner 1 removed!");
				player.openInventory(Inventories.createCorner1Menu(meta.getArena()));
			}
		}

		// Confirm to remove corner 2
		else if (invID == InventoryID.CORNER_2_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createCorner1Menu(meta.getArena()));

			// Remove corner 2, then return to previous menu
			else if (buttonName.contains("YES")) {
				Arena arenaInstance = meta.getArena();

				arenaInstance.setCorner2(null);
				arenaInstance.setClosed(true);
				PlayerManager.notifySuccess(player, "Corner 2 removed!");
				player.openInventory(Inventories.createCorner2Menu(meta.getArena()));
			}
		}

		// Confirm to remove lobby
		else if (invID == InventoryID.LOBBY_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createLobbyMenu());

			// Remove the lobby, then return to previous menu
			else if (buttonName.contains("YES")) {
				GameDataManager.removeLobbyLocation();
				GameManager.reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby removed!");
				player.openInventory(Inventories.createLobbyMenu());
			}
		}

		// Confirm to remove info board
		else if (invID == InventoryID.INFO_BOARD_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createInfoBoardMenu(meta.getId()));

			// Remove the info board, then return to dashboard
			else if (buttonName.contains("YES")) {
				// Remove info board
				GameManager.removeInfoBoard(meta.getId());

				// Confirm and return
				PlayerManager.notifySuccess(player, "Info board removed!");
				player.openInventory(Inventories.createInfoBoardDashboard());
			}
		}

		// Confirm to remove total kills leaderboard
		else if (invID == InventoryID.TOTAL_KILLS_CONFIRM_MENU) {
			String type = "totalKills";

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());

			// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameDataManager.removeLeaderboardLocation(type);

				// Remove leaderboard
				GameManager.removeLeaderboard(type);

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
			}
		}

		// Confirm to remove top kills leaderboard
		else if (invID == InventoryID.TOP_KILLS_CONFIRM_MENU) {
			String type = "topKills";

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameDataManager.removeLeaderboardLocation(type);

				// Remove leaderboard
				GameManager.removeLeaderboard(type);

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());
			}
		}

		// Confirm to remove total gems leaderboard
		else if (invID == InventoryID.TOTAL_GEMS_CONFIRM_MENU) {
			String type = "totalGems";

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameDataManager.removeLeaderboardLocation(type);

				// Remove leaderboard
				GameManager.removeLeaderboard(type);

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
			}
		}

		// Confirm to remove top balance leaderboard
		else if (invID == InventoryID.TOP_BALANCE_CONFIRM_MENU) {
			String type = "topBalance";

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameDataManager.removeLeaderboardLocation(type);

				// Remove leaderboard
				GameManager.removeLeaderboard(type);

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
			}
		}

		// Confirm to remove top wave leaderboard
		else if (invID == InventoryID.TOP_WAVE_CONFIRM_MENU) {
			String type = "topWave";

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameDataManager.removeLeaderboardLocation(type);

				// Remove leaderboard
				GameManager.removeLeaderboard(type);

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());
			}
		}

		// Confirm to remove arena
		else if (invID == InventoryID.ARENA_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));

			// Remove arena data, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove data
				GameManager.removeArena(meta.getArena().getId());

				// Confirm and return
				PlayerManager.notifySuccess(player, "Arena removed!");
				player.openInventory(Inventories.createArenasDashboard());
			}
		}

		// Portal menu for an arena
		else if (invID == InventoryID.PORTAL_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					PlayerManager.notifySuccess(player, "Portal set!");
					player.openInventory(Inventories.createPortalMenu(arenaInstance));
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
				closeInv(player);
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
						player.openInventory(Inventories.createPortalConfirmMenu(arenaInstance));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No portal to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(arenaInstance));
		}

		// Leaderboard menu for an arena
		else if (invID == InventoryID.ARENA_BOARD_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createArenaBoardMenu(arenaInstance));
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
				closeInv(player);
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
					player.openInventory(Inventories.createArenaBoardConfirmMenu(arenaInstance));
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(arenaInstance));
		}

		// Player settings menu for an arena
		else if (invID == InventoryID.PLAYERS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				player.openInventory(Inventories.createPlayerSpawnMenu(meta.getArena()));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					player.openInventory(Inventories.createPlayersMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				player.openInventory(Inventories.createWaitingRoomMenu(meta.getArena()));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxPlayerMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMinPlayerMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Player spawn menu for an arena
		else if (invID == InventoryID.PLAYER_SPAWN_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					PlayerManager.notifySuccess(player, "Spawn set!");
					player.openInventory(Inventories.createPlayerSpawnMenu(meta.getArena()));
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
					closeInv(player);
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
						player.openInventory(Inventories.createSpawnConfirmMenu(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No player spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersMenu(meta.getArena()));
		}

		// Waiting room menu for an arena
		else if (invID == InventoryID.WAITING_ROOM_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					PlayerManager.notifySuccess(player, "Waiting room set!");
					player.openInventory(Inventories.createWaitingRoomMenu(meta.getArena()));
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
				closeInv(player);
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
						player.openInventory(Inventories.createWaitingConfirmMenu(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No waiting room to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersMenu(meta.getArena()));
		}

		// Max player menu for an arena
		else if (invID == InventoryID.MAX_PLAYER_MENU) {
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
					PlayerManager.notifyFailure(player, "Max players cannot be less than min players!");
					return;
				}

				arenaInstance.setMaxPlayers(--current);
				player.openInventory(Inventories.createMaxPlayerMenu(meta.getArena()));
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxPlayers(++current);
				player.openInventory(Inventories.createMaxPlayerMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersMenu(meta.getArena()));
		}

		// Min player menu for an arena
		else if (invID == InventoryID.MIN_PLAYER_MENU) {
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
				player.openInventory(Inventories.createMinPlayerMenu(meta.getArena()));
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
				player.openInventory(Inventories.createMinPlayerMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayersMenu(meta.getArena()));
		}

		// Mob settings menu for an arena
		else if (invID == InventoryID.MOBS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				player.openInventory(Inventories.createMonsterSpawnDashboard(meta.getArena()));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				player.openInventory(Inventories.createVillagerSpawnDashboard(meta.getArena()));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createSpawnTableMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Monster spawn dashboard for an arena
		else if (invID == InventoryID.MONSTER_SPAWN_DASHBOARD) {
			// Edit spawn
			if (buttonType == Material.ZOMBIE_HEAD)
				player.openInventory(Inventories.createMonsterSpawnMenu(
						meta.getArena(),
						Integer.parseInt(buttonName.split(" ")[2])
				));

			// Create new
			else if (buttonName.contains(CommunicationManager.format("&a&lNew ")))
				player.openInventory(Inventories.createMonsterSpawnMenu(
						meta.getArena(),
						meta.getArena().newMonsterSpawnID())
				);

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createMonsterSpawnDashboard(meta.getArena(), meta.getPage() - 1));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createMonsterSpawnDashboard(meta.getArena(), meta.getPage() + 1));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));
		}

		// Monster spawn menu for a specific spawn
		else if (invID == InventoryID.MONSTER_SPAWN_MENU) {
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
					closeInv(player);
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
						player.openInventory(Inventories.createMonsterSpawnConfirmMenu(meta.getArena(),
								meta.getId()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No monster spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMonsterSpawnDashboard(meta.getArena()));
		}

		// Villager spawn dashboard for an arena
		else if (invID == InventoryID.VILLAGER_SPAWN_DASHBOARD) {
			// Edit spawn
			if (buttonType == Material.POPPY)
				player.openInventory(Inventories.createVillagerSpawnMenu(
						meta.getArena(),
						Integer.parseInt(buttonName.split(" ")[2])
				));

			// Create new
			else if (buttonName.contains(CommunicationManager.format("&a&lNew ")))
				player.openInventory(Inventories.createVillagerSpawnMenu(
						meta.getArena(),
						meta.getArena().newVillagerSpawnID())
				);

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createVillagerSpawnDashboard(
						meta.getArena(),
						meta.getPage() - 1
				));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createVillagerSpawnDashboard(
						meta.getArena(),
						meta.getPage() + 1
				));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));
		}

		// Villager spawn menu for a specific spawn
		else if (invID == InventoryID.VILLAGER_SPAWN_MENU) {
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
					closeInv(player);
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
						player.openInventory(Inventories.createVillagerSpawnConfirmMenu(meta.getArena(),
								meta.getId()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				} else PlayerManager.notifyFailure(player, "No villager spawn to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createVillagerSpawnDashboard(meta.getArena()));
		}

		// Spawn table menu for an arena
		else if (invID == InventoryID.SPAWN_TABLE_MENU) {
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
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				return;
			}

			// Reload inventory
			player.openInventory(Inventories.createSpawnTableMenu(meta.getArena()));
		}

		// Shop settings menu for an arena
		else if (invID == InventoryID.SHOP_SETTINGS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					player.openInventory(arenaInstance.getCustomShopEditorMenu());
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle enchants shop
			else if (buttonName.contains("Enchant Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setEnchants(!arenaInstance.hasEnchants());
					player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle community chest
			else if (buttonName.contains("Community Chest:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCommunity(!arenaInstance.hasCommunity());
					player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					player.openInventory(Inventories.createShopSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Menu for editing a specific custom item
		else if (invID == InventoryID.CUSTOM_ITEMS_MENU) {
			int arenaID = meta.getArena().getId();
			ItemStack item = meta.getArena().getCustomShop().getItem(meta.getId());
			assert item != null;
			ItemMeta itemMeta = item.getItemMeta();
			assert itemMeta != null;
			List<String> lore = itemMeta.getLore();
			int price = -1;
			if (lore != null) {
				try {
					price = Integer.parseInt(lore.get(lore.size() - 1)
						.substring(6 + LanguageManager.messages.gems.length()));
				}
				catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
			} else {
				lore = new ArrayList<>();
			}

			// Set un-purchasable
			if (buttonName.contains("Toggle Un-purchasable")) {
				// Toggle off
				if (price < 0) {
					price = 0;
					lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
						": &a" + price));
				}

				// Toggle on
				else {
					lore.removeLast();
				}

				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Increase by 1
			else if (buttonName.contains("+1 gem")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price++;
					lore.removeLast();
				}

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Increase by 10
			else if (buttonName.contains("+10 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price += 10;
					lore.removeLast();
				}

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Increase by 100
			else if (buttonName.contains("+100 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price += 100;
					lore.removeLast();
				}

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Increase by 1000
			else if (buttonName.contains("+1000 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price += 1000;
					lore.removeLast();
				}

				// Check for max price
				if (price > 99999) {
					PlayerManager.notifyFailure(player, "Price cannot be above 99999 gems!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Delete item
			else if (buttonName.contains("DELETE")) {
				player.openInventory(Inventories.createCustomItemConfirmMenu(meta.getArena(), meta.getId()));
				return;
			}

			// Decrease by 1
			else if (buttonName.contains("-1 gem")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price--;
					lore.removeLast();
				}

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Decrease by 10
			else if (buttonName.contains("-10 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price -= 10;
					lore.removeLast();
				}

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Decrease by 100
			else if (buttonName.contains("-100 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price -= 100;
					lore.removeLast();
				}

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Decrease by 1000
			else if (buttonName.contains("-1000 gems")) {
				// Toggle from un-purchasable
				if (price < 0) {
					price = 0;
				}

				// Set new price
				else {
					price -= 1000;
					lore.removeLast();
				}

				// Check for min price
				if (price < 0) {
					PlayerManager.notifyFailure(player, "Price cannot be negative!");
					return;
				}

				lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems +
					": &a" + price));
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				ArenaDataManager.setCustomShopItem(arenaID, meta.getId(), item);
			}

			// Exit
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(meta.getArena().getCustomShopEditorMenu());
				return;
			}

			// Refresh GUI
			player.openInventory(Inventories.createCustomItemsMenu(meta.getArena(), meta.getId()));
		}

		// Game settings menu for an arena
		else if (invID == InventoryID.GAME_SETTINGS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxWaveMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaveTimeLimitMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(Inventories.createAllowedKitsMenu(arenaInstance, false));

			// Edit forced challenges
			else if (buttonName.contains("Forced Challenges"))
				player.openInventory(Inventories.createForcedChallengesMenu(arenaInstance, false));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyMultiplierMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle dynamic difficulty
			else if (buttonName.contains("Late Arrival:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setLateArrival(!arenaInstance.hasLateArrival());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle experience drop
			else if (buttonName.contains("Experience Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setExpDrop(!arenaInstance.hasExpDrop());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle item drop
			else if (buttonName.contains("Item Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemDrop(!arenaInstance.hasGemDrop());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit arena bounds
			else if (buttonName.contains("Arena Bounds"))
				player.openInventory(Inventories.createBoundsMenu(meta.getArena()));

			// Edit wolf cap
			else if (buttonName.contains("Wolf Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWolfCapMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit iron golem cap
			else if (buttonName.contains("Iron Golem Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createGolemCapMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				player.openInventory(Inventories.createSoundsMenu(meta.getArena()));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createCopySettingsMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createArenaMenu(meta.getArena()));
		}

		// Max wave menu for an arena
		else if (invID == InventoryID.MAX_WAVE_MENU) {
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

				player.openInventory(Inventories.createMaxWaveMenu(meta.getArena()));
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(-1);
				player.openInventory(Inventories.createMaxWaveMenu(meta.getArena()));
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setMaxWaves(1);
				player.openInventory(Inventories.createMaxWaveMenu(meta.getArena()));
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

				player.openInventory(Inventories.createMaxWaveMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Wave time limit menu for an arena
		else if (invID == InventoryID.WAVE_TIME_LIMIT_MENU) {
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

				player.openInventory(Inventories.createWaveTimeLimitMenu(meta.getArena()));
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(-1);
				player.openInventory(Inventories.createWaveTimeLimitMenu(meta.getArena()));
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaveTimeLimit(1);
				player.openInventory(Inventories.createWaveTimeLimitMenu(meta.getArena()));
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

				player.openInventory(Inventories.createWaveTimeLimitMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Difficulty label menu for an arena
		else if (invID == InventoryID.DIFFICULTY_LABEL_MENU) {
			Arena arenaInstance = meta.getArena();

			// Set to Easy
			if (buttonName.contains("Easy")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Easy");
				player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Medium");
				player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Hard");
				player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel("Insane");
				player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyLabel(null);
				player.openInventory(Inventories.createDifficultyLabelMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Difficulty multiplier menu for an arena
		else if (invID == InventoryID.DIFFICULTY_MULTIPLIER_MENU) {
			Arena arenaInstance = meta.getArena();

			// Set to 1
			if (buttonName.contains("1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(1);
				player.openInventory(Inventories.createDifficultyMultiplierMenu(meta.getArena()));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(2);
				player.openInventory(Inventories.createDifficultyMultiplierMenu(meta.getArena()));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(3);
				player.openInventory(Inventories.createDifficultyMultiplierMenu(meta.getArena()));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setDifficultyMultiplier(4);
				player.openInventory(Inventories.createDifficultyMultiplierMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Allowed kits display for an arena
		else if (invID == InventoryID.ALLOWED_KITS_DISPLAY_MENU) {
			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createArenaInfoMenu(meta.getArena()));
			}
		}

		// Allowed kits menu for an arena
		else if (invID == InventoryID.ALLOWED_KITS_MENU) {
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
				player.openInventory(Inventories.createAllowedKitsMenu(arenaInstance, false));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Forced challenges display for an arena
		else if (invID == InventoryID.FORCED_CHALLENGES_DISPLAY_MENU) {
			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createArenaInfoMenu(meta.getArena()));
			}
		}

		// Forced challenges menu for an arena
		else if (invID == InventoryID.FORCED_CHALLENGES_MENU) {
			String challenge = buttonName.substring(4);
			Arena arenaInstance = meta.getArena();
			List<String> forced = arenaInstance.getForcedChallenges();

			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));

			// Toggle a challenge
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (forced.contains(challenge))
					forced.remove(challenge);
				else forced.add(challenge);
				arenaInstance.setForcedChallenges(forced);
				player.openInventory(Inventories.createForcedChallengesMenu(arenaInstance, false));
			}
		}

		// Arena bounds menu for an arena
		else if (invID == InventoryID.ARENA_BOUNDS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Edit corner 1
			if (buttonName.contains("Corner 1"))
				player.openInventory(Inventories.createCorner1Menu(meta.getArena()));

			// Edit corner 2
			else if (buttonName.contains("Corner 2"))
				player.openInventory(Inventories.createCorner2Menu(meta.getArena()));

			// Toggle border particles
			else if (buttonName.contains("Border Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setBorderParticles(!arenaInstance.hasBorderParticles());
					player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Corner 1 menu for an arena
		else if (invID == InventoryID.CORNER_1_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
					player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
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
				closeInv(player);
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getCorner1() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createCorner1ConfirmMenu(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 1 to remove!");

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
		}

		// Corner 2 menu for an arena
		else if (invID == InventoryID.CORNER_2_MENU) {
			Arena arenaInstance = meta.getArena();

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
					player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
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
				closeInv(player);
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getCorner2() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createCorner2ConfirmMenu(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else PlayerManager.notifyFailure(player, "No corner 2 to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
		}

		// Wolf cap menu for an arena
		else if (invID == InventoryID.WOLF_CAP_MENU) {
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
				player.openInventory(Inventories.createWolfCapMenu(meta.getArena()));
			}

			// Increase wolf cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWolfCap(++current);
				player.openInventory(Inventories.createWolfCapMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Iron golem cap menu for an arena
		else if (invID == InventoryID.GOLEM_CAP_MENU) {
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

				arenaInstance.setGolemCap(--current);
				player.openInventory(Inventories.createGolemCapMenu(meta.getArena()));
			}

			// Increase iron golem cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setGolemCap(++current);
				player.openInventory(Inventories.createGolemCapMenu(meta.getArena()));
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Sound settings menu for an arena
		else if (invID == InventoryID.SOUNDS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveEndSound(!arenaInstance.hasWaveEndSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaitSoundMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle ability sound
			else if (buttonName.contains("Ability")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setAbilitySound(!arenaInstance.hasAbilitySound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				} else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
		}

		// Waiting sound menu for an arena
		else if (invID == InventoryID.WAITING_SOUND_MENU) {
			Arena arenaInstance = meta.getArena();

			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createSoundsMenu(meta.getArena()));

			// Set sound
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setWaitingSound(buttonName.toLowerCase().substring(4));
				player.openInventory(Inventories.createWaitSoundMenu(meta.getArena()));
			}
		}

		// Menu to copy game settings
		else if (invID == InventoryID.COPY_SETTINGS_MENU) {
			Arena arena1 = meta.getArena();

			// Copy existing arena
			if (buttonName.contains("Copy")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				Arena arena2;

				// Check for valid arena to copy
				try {
					arena2 = GameManager.getArena(buttonName.substring(9));
				} catch (ArenaNotFoundException err) {
					return;
				}

				// Copy settings from another arena
				arena1.copy(arena2);
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

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createCopySettingsMenu(meta.getArena(), meta.getPage() - 1));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createCopySettingsMenu(meta.getArena(), meta.getPage() + 1));

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				return;
			}

			// Save updates
			PlayerManager.notifySuccess(player, "Game settings copied!");
		}

		// In-game item shop menu
		else if (invID == InventoryID.SHOP_MENU) {
			Arena arenaInstance;

			// See if the player is in a game
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (ArenaNotFoundException err) {
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
					player.openInventory(Inventories.createEnchantShopMenu());
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
		else if (invID == InventoryID.MOCK_CUSTOM_SHOP_MENU) {
			if (buttonName.contains(LanguageManager.messages.exit))
				try {
					player.openInventory(Inventories.createArenaInfoMenu(GameManager.getArena(
							title.substring(6 + LanguageManager.names.customShop.length()))));
				} catch (ArenaNotFoundException ignored) {
				}
		}

		// In-game shops
		else if (invID == InventoryID.WEAPON_SHOP_MENU || invID == InventoryID.ARMOR_SHOP_MENU ||
				invID == InventoryID.CONSUMABLE_SHOP_MENU || invID == InventoryID.CUSTOM_SHOP_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createShopMenu(arenaInstance.getCurrentWave() / 10 + 1, arenaInstance));
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

			int cost = Integer.parseInt(lore.get(lore.size() - 1)
					.substring(6 + LanguageManager.messages.gems.length()));
			Random random = new Random();

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
				return;
			}

			// Remove cost meta
			buy = ItemManager.removeLastLore(buy);

			// Make unbreakable for blacksmith (not sharing)
			if ((Kit.blacksmith().setKitLevel(1).equals(gamer.getKit()) ||
					Kit.blacksmith().setKitLevel(1).equals(gamer.getKit2())) && !gamer.isSharing())
				buy = ItemManager.makeUnbreakable(buy);

			// Make unbreakable for successful blacksmith sharing
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.BLACKSMITH))) {
				buy = ItemManager.makeUnbreakable(buy);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}

			// Make splash potion for witch (not sharing)
			if ((Kit.witch().setKitLevel(1).equals(gamer.getKit()) ||
					Kit.witch().setKitLevel(1).equals(gamer.getKit2())) && !gamer.isSharing())
				buy = ItemManager.makeSplash(buy);

			// Make splash potion for successful witch sharing
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.WITCH))) {
				buy = ItemManager.makeSplash(buy);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if ((Kit.merchant().setKitLevel(1).equals(gamer.getKit()) ||
					Kit.merchant().setKitLevel(1).equals(gamer.getKit2())) && !gamer.isSharing())
				gamer.addGems(cost / 10);
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
				gamer.addGems(cost / 10);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}
			GameManager.createBoard(gamer);

			EntityEquipment equipment = Objects.requireNonNull(player.getPlayer()).getEquipment();
			Objects.requireNonNull(equipment);

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == buyType) &&
					(equipment.getHelmet() == null || equipment.getHelmet().getType() == Material.AIR)) {
				equipment.setHelmet(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.helmet);
			} else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == buyType) &&
					(equipment.getChestplate() == null || equipment.getChestplate().getType() == Material.AIR)) {
				equipment.setChestplate(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.chestplate);
			} else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == buyType) &&
					(equipment.getLeggings() == null || equipment.getLeggings().getType() == Material.AIR)) {
				equipment.setLeggings(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.leggings);
			} else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == buyType) &&
					(equipment.getBoots() == null || equipment.getBoots().getType() == Material.AIR)) {
				equipment.setBoots(buy);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.boots);
			} else {
				PlayerManager.giveItem(player, buy, LanguageManager.errors.inventoryFull);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
			}
		}
		else if (invID == InventoryID.ENCHANT_SHOP_MENU) {
			Arena arenaInstance;

			// Attempt to get arena
			try {
				arenaInstance = GameManager.getArena(player);
			} catch (ArenaNotFoundException err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createShopMenu(arenaInstance.getCurrentWave() / 10 + 1, arenaInstance));
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
				enchant = buy.getItemMeta().getDisplayName().split(" ")[1];
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
		else if (invID == InventoryID.PLAYER_STATS_MENU) {
			Player owner = meta.getPlayer();
			if (buttonName.contains(LanguageManager.messages.achievements))
				player.openInventory(Inventories.createPlayerAchievementsMenu(owner));
			else if (buttonName.contains(LanguageManager.messages.kits))
				player.openInventory(Inventories.createPlayerKitsMenu(owner, player.getName()));
			else if (buttonName.contains(LanguageManager.messages.reset))
				player.openInventory(Inventories.createResetStatsConfirmMenu(player));
		}

		// Achievements menu for a player
		else if (invID == InventoryID.PLAYER_ACHIEVEMENTS_MENU) {
			Player owner = meta.getPlayer();

			// Exit button
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayerStatsMenu(owner));

			// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createPlayerAchievementsMenu(owner, meta.getPage() - 1));

			// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createPlayerAchievementsMenu(owner, meta.getPage() + 1));
		}

		// Kits menu for a player
		else if (invID == InventoryID.PLAYER_KITS_MENU) {
			Player owner = meta.getPlayer();
			String name = owner.getName();
			UUID uuid = owner.getUniqueId();
			Kit kit = Kit.getKit(buttonName.substring(4));

			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createPlayerStatsMenu(owner));
				return;
			}

			// Check if requester is owner
			if (!name.equals(player.getName()))
				return;

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, "No kit of %s was found.", buttonName.substring(4));
				return;
			}

			// Cache player data
			int kitLevel = PlayerDataManager.getPlayerKitLevel(uuid, kit);
			int crystalBalance = PlayerDataManager.getPlayerCrystals(uuid);

			// Ignore if kit already maxed
			if (kitLevel == kit.getMaxLevel()) {
				return;
			}

			// Successful purchase
			if (crystalBalance >= kit.getPrice(++kitLevel)) {
				PlayerDataManager.setPlayerCrystals(uuid, crystalBalance - kit.getPrice(kitLevel));
				PlayerDataManager.setPlayerKitLevel(uuid, kit, kitLevel);
				PlayerManager.notifySuccess(
					player,
					kitLevel == 1 ? LanguageManager.confirms.kitBuy : LanguageManager.confirms.kitUpgrade
				);
				AchievementChecker.checkDefaultKitAchievements(player);
			}

			// Failed purchase
			else {
				PlayerManager.notifyFailure(
					player,
					kitLevel == 1 ? LanguageManager.errors.kitBuy : LanguageManager.errors.kitUpgrade
				);
			}

			player.openInventory(Inventories.createPlayerKitsMenu(owner, name));
		}

		// Reset player stats confirmation for a player
		else if (invID == InventoryID.RESET_STATS_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createPlayerStatsMenu(meta.getPlayer()));

			// Reset player stats
			else if (buttonName.contains("YES")) {
				// Remove stats
				PlayerDataManager.deletePlayerData(player.getUniqueId());

				// Reload leaderboards
				GameManager.refreshLeaderboards();

				// Confirm and return
				PlayerManager.notifySuccess(player, LanguageManager.confirms.reset);
				player.openInventory(Inventories.createPlayerStatsMenu(meta.getPlayer()));
			}
		}

		// Kit selection menu for an arena
		else if (invID == InventoryID.SELECT_KITS_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			Kit kit = Kit.getKit(buttonName.substring(4));
			UUID uuid = player.getUniqueId();

			// Leave if EXIT
			if (buttonName.contains(LanguageManager.messages.exit)) {
				closeInv(player);
				return;
			}

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, "No kit of %s was found.", buttonName.substring(4));
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == VDPlayer.Status.SPECTATOR)
				return;

			// Set proper kit for gamer
			if (PlayerDataManager.playerOwnsKit(uuid, kit) || kit.equals(Kit.orc()) ||
					kit.equals(Kit.farmer()) || kit.equals(Kit.none())) {
				gamer.setKit(kit.setKitLevel(PlayerDataManager.getPlayerKitLevel(uuid, kit)));
				PlayerManager.notifySuccess(player, LanguageManager.confirms.kitSelect);
			} else {
				PlayerManager.notifyFailure(player, LanguageManager.errors.kitSelect);
				return;
			}

			// Close inventory if no second kit and create scoreboard
			closeInv(player);
			GameManager.createBoard(gamer);
		}

		// Challenge selection menu for an arena
		else if (invID == InventoryID.SELECT_CHALLENGES_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameManager.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			Challenge challenge = Challenge.getChallenge(buttonName.substring(4));

			// Leave if EXIT
			if (buttonName.contains(LanguageManager.messages.exit)) {
				closeInv(player);
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == VDPlayer.Status.SPECTATOR)
				return;

			// Option for no challenge
			if (Challenge.none().equals(challenge)) {
				// Arena has forced challenges
				if (!arenaInstance.getForcedChallenges().isEmpty())
					PlayerManager.notifyFailure(player, LanguageManager.errors.hasForcedChallenges);

				else {
					gamer.resetChallenges();
					PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeAdd);
				}
			}

			// Remove a challenge
			else if (gamer.getChallenges().contains(challenge)) {
				// Arena forced the challenge
				if (challenge != null && arenaInstance.getForcedChallenges().contains(challenge.getName()))
					PlayerManager.notifyFailure(player, LanguageManager.errors.forcedChallenge);

				else {
					gamer.removeChallenge(challenge);
					PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeDelete);
				}
			}

			// Add a challenge
			else {
				gamer.addChallenge(challenge);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeAdd);
			}

			// Create scoreboard and update inventory
			GameManager.createBoard(gamer);
			player.openInventory(Inventories.createSelectChallengesMenu(gamer, arenaInstance));
		}

		// Stats menu for an arena
		else if (invID == InventoryID.ARENA_INFO_MENU) {
			if (buttonName.contains(LanguageManager.messages.customShopInv))
				player.openInventory(meta.getArena().getMockCustomShop());

			else if (buttonName.contains(LanguageManager.messages.allowedKits))
				player.openInventory(Inventories.createAllowedKitsMenu(meta.getArena(), true));

			else if (buttonName.contains(LanguageManager.messages.forcedChallenges))
				player.openInventory(Inventories.createForcedChallengesMenu(meta.getArena(), true));
		}

		// Menu for converting crystals
		else if (invID == InventoryID.CRYSTAL_CONVERT_MENU) {
			Player owner = meta.getPlayer();
			VDPlayer gamer;

			// Try to get VDPlayer
			try {
				gamer = GameManager.getArena(owner).getPlayer(owner);
			} catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}
			int gemBoost = gamer.getGemBoost();
			int crystalBalance = PlayerDataManager.getPlayerCrystals(owner.getUniqueId());

			// Reset
			if (buttonName.contains(LanguageManager.messages.reset)) {
				gamer.setGemBoost(0);
			}

			// Increase by 1
			else if (buttonName.contains("+1 ")) {
				gemBoost++;

				// Check for crystal balance
				if (gemBoost * 5 > crystalBalance) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buyGeneral);
					return;
				}

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Increase by 10
			else if (buttonName.contains("+10 ")) {
				gemBoost += 10;

				// Check for crystal balance
				if (gemBoost * 5 > crystalBalance) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buyGeneral);
					return;
				}

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Increase by 100
			else if (buttonName.contains("+100 ")) {
				gemBoost += 100;

				// Check for crystal balance
				if (gemBoost * 5 > crystalBalance) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buyGeneral);
					return;
				}

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Increase by 1000
			else if (buttonName.contains("+1000 ")) {
				gemBoost += 1000;

				// Check for crystal balance
				if (gemBoost * 5 > crystalBalance) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buyGeneral);
					return;
				}

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Decrease by 1
			else if (buttonName.contains("-1 ")) {
				gemBoost--;

				// Check for positive number
				if (gemBoost < 0)
					return;

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Decrease by 10
			else if (buttonName.contains("-10 ")) {
				gemBoost -= 10;

				// Check for positive number
				if (gemBoost < 0)
					return;

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Decrease by 100
			else if (buttonName.contains("-100 ")) {
				gemBoost -= 100;

				// Check for positive number
				if (gemBoost < 0)
					return;

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Decrease by 1000
			else if (buttonName.contains("-1000 ")) {
				gemBoost -= 1000;

				// Check for positive number
				if (gemBoost < 0)
					return;

				// Apply boost
				gamer.setGemBoost(gemBoost);
			}

			// Exit
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.closeInventory();
				return;
			}

			// Refresh GUI
			player.openInventory(Inventories.createCrystalConvertMenu(gamer));
		}
	}

	// Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		// Ignore non-plugin inventories
		if (!(e.getInventory().getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

		// Check for community chest with shop inside it
		if (meta.getInventoryID() == InventoryID.COMMUNITY_CHEST_INVENTORY && e.getInventory().contains(GameItems.shop())) {
			e.getInventory().removeItem(GameItems.shop());
			PlayerManager.giveItem((Player) e.getPlayer(), GameItems.shop(), LanguageManager.errors.inventoryFull);
		}
	}

	/**
	 * Closes the inventory of a player without creating a ghost item artifact.
	 * @param player Player to close inventory.
	 */
	private void closeInv(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, player::closeInventory, 1);
	}
}
