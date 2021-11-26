package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.GUI.InventoryItems;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.events.SignGUIEvent;
import me.theguyhere.villagerdefense.game.models.*;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.kits.Kit;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryListener implements Listener {
	private final Main plugin;
	private boolean close;

	// Constants for armor types
	public InventoryListener(Main plugin) {
		this.plugin = plugin;
	}

	// Prevent losing items by drag clicking in custom inventory
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Ignore clicks in player inventory
		if (e.getInventory().getType() == InventoryType.PLAYER)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains("Community Chest"))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// Prevent losing items by shift clicking in custom inventory
	@EventHandler
	public void onShiftClick(InventoryClickEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Check for shift click
		if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT)
			return;

		// Cancel the event if the inventory isn't the community chest, otherwise save the inventory
		if (!title.contains("Community Chest"))
			e.setCancelled(true);
		else {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			arenaInstance.setCommunityChest(e.getInventory());
		}
	}

	// All click events in the inventories
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		// Get inventory title
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Ignore null inventories
		if (e.getClickedInventory() == null)
			return;

		// Debugging info
		Utils.debugInfo("Inventory Item: " + e.getCurrentItem(), 2);
		Utils.debugInfo("Cursor Item: " + e.getCursor(), 2);
		Utils.debugInfo("Clicked Inventory: " + e.getClickedInventory(), 2);
		Utils.debugInfo("Inventory Name: " + title, 2);

		// Cancel the event if the inventory isn't the community chest or custom shop editor to prevent changing the GUI
		if (!title.contains("Community Chest") && !title.contains("Custom Shop Editor"))
			e.setCancelled(true);

		// Save community chest
		else if (title.contains("Community Chest")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
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
		FileConfiguration language = plugin.getLanguageData();

		// Custom shop editor for an arena
		if (title.contains("Custom Shop Editor:")) {
			Utils.debugInfo("Custom shop editor being used.", 2);
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			ItemStack cursor = e.getCursor();
			assert cursor != null;
			String path = "a" + meta.getInteger1() + ".customShop.";
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Exit menu
			if (InventoryItems.exit().equals(button)) {
				e.setCancelled(true);
				player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				return;
			}

			// Check for arena closure
			if (!arenaInstance.isClosed()) {
				player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
				Utils.giveItem(player, cursor.clone(), language.getString("inventoryFull"));
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
				// Set a new arena
				Game.arenas[slot] = new Arena(plugin, slot, new Tasks(plugin, slot));

				Inventories.nameArena(player, Game.arenas[slot]);
			}

			// Edit existing arena
			else if (buttonType == Material.LIME_CONCRETE)
				player.openInventory(Inventories.createArenaInventory(slot));

			// Open lobby menu
			else if (buttonName.contains("Lobby"))
				player.openInventory(Inventories.createLobbyInventory(plugin));

			// Open info boards menu
			else if (buttonName.contains("Info Boards"))
				player.openInventory(Inventories.createInfoBoardInventory(plugin));

			// Open leaderboards menu
			else if (buttonName.contains("Leaderboards"))
				player.openInventory(Inventories.createLeaderboardInventory());

			// Close inventory
			else if (buttonName.contains("EXIT"))
				player.closeInventory();
		}

		// Lobby menu
		else if (title.contains(Utils.format("&2&lLobby"))) {
			String path = "lobby";

			// Create lobby
			if (buttonName.contains("Create Lobby")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGame().reloadLobby();
				player.sendMessage(Utils.notify("&aLobby set!"));
				player.openInventory(Inventories.createLobbyInventory(plugin));
			}

			// Relocate lobby
			else if (buttonName.contains("Relocate Lobby")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGame().reloadLobby();
				player.sendMessage(Utils.notify("&aLobby relocated!"));
			}

			// Teleport player to lobby
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo lobby to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center lobby
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo lobby to center!"));
					return;
				}
				Utils.centerConfigLocation(plugin, path);
				player.sendMessage(Utils.notify("&aLobby centered!"));
			}

			// Remove lobby
			else if (buttonName.contains("REMOVE"))
				if (config.contains("lobby"))
					player.openInventory(Inventories.createLobbyConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo lobby to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Info board menu
		else if (title.contains("Info Boards")) {

			// Edit board
			if (Arrays.asList(Inventories.INFO_BOARD_MATS).contains(buttonType))
				player.openInventory(Inventories.createInfoBoardMenu(plugin, slot));

			// Exit menu
			else if (buttonName.contains("EXIT"))
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
				plugin.getGame().setInfoBoard(player.getLocation(), num);
				player.sendMessage(Utils.notify("&aInfo board set!"));
				player.openInventory(Inventories.createInfoBoardMenu(plugin, num));
			}

			// Relocate board
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				plugin.getGame().refreshInfoBoard(num);
				player.sendMessage(Utils.notify("&aInfo board relocated!"));
			}

			// Teleport player to info board
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo info board to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center info board
			else if (buttonName.contains("Center")) {
				if (Utils.getConfigLocationNoRotation(plugin, path) == null) {
					player.sendMessage(Utils.notify("&cNo info board to center!"));
					return;
				}
				plugin.getGame().centerInfoBoard(num);
				player.sendMessage(Utils.notify("&aInfo board centered!"));
			}

			// Remove info board
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createInfoBoardConfirmInventory(num));
				else player.sendMessage(Utils.notify("&cNo info board to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createInfoBoardInventory(plugin));
		}

		// Leaderboard menu
		else if (title.contains("Leaderboards")) {
			if (buttonName.contains("Total Kills Leaderboard"))
				player.openInventory(Inventories.createTotalKillsLeaderboardInventory(plugin));

			if (buttonName.contains("Top Kills Leaderboard"))
				player.openInventory(Inventories.createTopKillsLeaderboardInventory(plugin));

			if (buttonName.contains("Total Gems Leaderboard"))
				player.openInventory(Inventories.createTotalGemsLeaderboardInventory(plugin));

			if (buttonName.contains("Top Balance Leaderboard"))
				player.openInventory(Inventories.createTopBalanceLeaderboardInventory(plugin));

			if (buttonName.contains("Top Wave Leaderboard"))
				player.openInventory(Inventories.createTopWaveLeaderboardInventory(plugin));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenasInventory());
		}

		// Total kills leaderboard menu
		else if (title.contains(Utils.format("&4&lTotal Kills Leaderboard"))) {
			String path = "leaderboard.totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createTotalKillsLeaderboardInventory(plugin));
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				plugin.getGame().centerLeaderboard("totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTotalKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top kills leaderboard menu
		else if (title.contains(Utils.format("&c&lTop Kills Leaderboard"))) {
			String path = "leaderboard.topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topKills");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createTopKillsLeaderboardInventory(plugin));
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topKills");
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				plugin.getGame().refreshLeaderboard("topKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Total gems leaderboard menu
		else if (title.contains(Utils.format("&2&lTotal Gems Leaderboard"))) {
			String path = "leaderboard.totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createTotalGemsLeaderboardInventory(plugin));
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				plugin.getGame().refreshLeaderboard("totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTotalGemsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top balance leaderboard menu
		else if (title.contains(Utils.format("&a&lTop Balance Leaderboard"))) {
			String path = "leaderboard.topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createTopBalanceLeaderboardInventory(plugin));
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				plugin.getGame().refreshLeaderboard("topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopBalanceConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Top wave leaderboard menu
		else if (title.contains(Utils.format("&9&lTop Wave Leaderboard"))) {
			String path = "leaderboard.topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topWave");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createTopWaveLeaderboardInventory(plugin));
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				plugin.getGame().setLeaderboard(player.getLocation(), "topWave");
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				plugin.getGame().refreshLeaderboard("topWave");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(Inventories.createTopWaveConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createLeaderboardInventory());
		}

		// Naming inventory
		else if (title.contains("Arena ") && !title.contains("Bounds")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int num = slot;

			// Get new name buffer
			String newName = meta.getString();

			// Check for caps lock toggle
			boolean caps = arenaInstance.isCaps();
			if (caps)
				num += 36;

			// Letters and numbers
			if (Arrays.asList(Inventories.KEY_MATS).contains(buttonType))
				openInv(player, Inventories.createNamingInventory(meta.getInteger1(),
						newName + Inventories.NAMES[num]));

			// Spaces
			else if (buttonName.contains("Space"))
				openInv(player, Inventories.createNamingInventory(meta.getInteger1(),
						newName + Inventories.NAMES[72]));

			// Caps lock
			else if (buttonName.contains("CAPS LOCK")) {
				arenaInstance.flipCaps();
				openInv(player, Inventories.createNamingInventory(meta.getInteger1(), newName));
			}

			// Backspace
			else if (buttonName.contains("Backspace")) {
				if (newName.length() == 0)
					return;
				openInv(player, Inventories.createNamingInventory(meta.getInteger1(),
						newName.substring(0, newName.length() - 1)));
			}

			// Save
			else if (buttonName.contains("SAVE")) {
				try {
					arenaInstance.setName(newName);
				} catch (Exception ignored) {
					return;
				}

				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
			}

			// Cancel
			else if (buttonName.contains("CANCEL")) {
				if (arenaInstance.getName() == null) {
					Game.arenas[meta.getInteger1()] = null;
					openInv(player, Inventories.createArenasInventory());
				} else openInv(player, Inventories.createArenaInventory(meta.getInteger1()));
			}
		}

		// Menu for an arena
		else if (title.contains(Utils.format("&2&lEdit "))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed()) {
					Inventories.nameArena(player, arenaInstance);

//					player.openInventory(Inventories.createNamingInventory(meta.getInteger1(), arenaInstance.getName()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

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
						player.sendMessage(Utils.notify("&cArena cannot open without a lobby!"));
						return;
					}

					// No arena portal
					if (arenaInstance.getPortalLocation() == null) {
						player.sendMessage(Utils.notify("&cArena cannot open without a portal!"));
						return;
					}

					// No player spawn
					if (arenaInstance.getPlayerSpawn() == null) {
						player.sendMessage(Utils.notify("&cArena cannot open without a player spawn!"));
						return;
					}

					// No monster spawn
					if (arenaInstance.getMonsterSpawns().stream().noneMatch(Objects::nonNull)) {
						player.sendMessage(Utils.notify("&cArena cannot open without a monster spawn!"));
						return;
					}

					// No villager spawn
					if (arenaInstance.getVillagerSpawns().stream().noneMatch(Objects::nonNull)) {
						player.sendMessage(Utils.notify("&cArena cannot open without a villager spawn!"));
						return;
					}

					// No shops
					if (!arenaInstance.hasCustom() && !arenaInstance.hasNormal()) {
						player.sendMessage(Utils.notify("&cArena cannot open without a shop!"));
						return;
					}

					// Invalid arena bounds
					if (arenaInstance.getCorner1() == null || arenaInstance.getCorner2() == null ||
							!Objects.equals(arenaInstance.getCorner1().getWorld(), arenaInstance.getCorner2().getWorld())) {
						player.sendMessage(Utils.notify("&cArena cannot open without valid arena bounds!"));
						return;
					}

					// Open arena
					arenaInstance.setClosed(false);
				}

				// Arena currently open
				else {
					// Set to closed and update portal
					arenaInstance.setClosed(true);

					// Kick players
					arenaInstance.getPlayers().forEach(vdPlayer ->
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
									Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));
				}

				// Clear the arena
				Utils.clear(arenaInstance.getCorner1(), arenaInstance.getCorner2());

				// Save perm data
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createArenaConfirmInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
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
					Game.arenas[meta.getInteger1()].removePortal();

					// Confirm and return
					player.sendMessage(Utils.notify("&aPortal removed!"));
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
					Game.arenas[meta.getInteger1()].removeArenaBoard();

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
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
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setPlayerSpawn(null);
					arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aSpawn removed!"));
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
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setWaitingRoom(null);
					player.sendMessage(Utils.notify("&aWaiting room removed!"));
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
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setMonsterSpawn(meta.getInteger2(), null);
					if (arenaInstance.getMonsterSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
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
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setVillagerSpawn(meta.getInteger2(), null);
					if (arenaInstance.getVillagerSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
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
					player.openInventory(Game.arenas[meta.getInteger1()].getCustomShopEditor());
				}
			}

			// Confirm to remove corner 1
			else if (title.contains("Remove Corner 1?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createCorner1Inventory(meta.getInteger1()));

				// Remove corner 1, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setCorner1(null);
					arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aCorner 1 removed!"));
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
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					arenaInstance.setCorner2(null);
					arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aCorner 2 removed!"));
					player.openInventory(Inventories.createCorner2Inventory(meta.getInteger1()));
				}
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createLobbyInventory(plugin));

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set("lobby", null);
					plugin.saveArenaData();
					plugin.getGame().reloadLobby();
					player.sendMessage(Utils.notify("&aLobby removed!"));
					player.openInventory(Inventories.createLobbyInventory(plugin));
				}
			}

			// Confirm to remove info board
			else if (title.contains("Remove Info Board?")) {
				String path = "infoBoard." + meta.getInteger1();

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createInfoBoardMenu(plugin, meta.getInteger1()));

					// Remove the info board, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove info board data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove info board
					plugin.getGame().removeInfoBoard(meta.getInteger1());

					// Confirm and return
					player.sendMessage(Utils.notify("&aInfo board removed!"));
					player.openInventory(Inventories.createInfoBoardMenu(plugin, meta.getInteger1()));
				}
			}

			// Confirm to remove total kills leaderboard
			else if (title.contains("Remove Total Kills Leaderboard?")) {
				String path = "leaderboard.totalKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalKillsLeaderboardInventory(plugin));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGame().removeLeaderboard("totalKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(Inventories.createTotalKillsLeaderboardInventory(plugin));
				}
			}

			// Confirm to remove top kills leaderboard
			else if (title.contains("Remove Top Kills Leaderboard?")) {
				String path = "leaderboard.topKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopKillsLeaderboardInventory(plugin));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGame().removeLeaderboard("topKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(Inventories.createTopKillsLeaderboardInventory(plugin));
				}
			}

			// Confirm to remove total gems leaderboard
			else if (title.contains("Remove Total Gems Leaderboard?")) {
				String path = "leaderboard.totalGems";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTotalGemsLeaderboardInventory(plugin));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGame().removeLeaderboard("totalGems");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(Inventories.createTotalGemsLeaderboardInventory(plugin));
				}
			}

			// Confirm to remove top balance leaderboard
			else if (title.contains("Remove Top Balance Leaderboard?")) {
				String path = "leaderboard.topBalance";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopBalanceLeaderboardInventory(plugin));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGame().removeLeaderboard("topBalance");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(Inventories.createTopBalanceLeaderboardInventory(plugin));
				}
			}

			// Confirm to remove top wave leaderboard
			else if (title.contains("Remove Top Wave Leaderboard?")) {
				String path = "leaderboard.topWave";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createTopWaveLeaderboardInventory(plugin));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					plugin.getGame().removeLeaderboard("topWave");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(Inventories.createTopWaveLeaderboardInventory(plugin));
				}
			}

			// Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = Game.arenas[meta.getInteger1()];

					// Remove data
					arenaInstance.remove();
					Game.arenas[meta.getInteger1()] = null;

					// Confirm and return
					player.sendMessage(Utils.notify("&aArena removed!"));
					player.openInventory(Inventories.createArenasInventory());
				}
			}
		}

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					player.sendMessage(Utils.notify("&aPortal set!"));
					player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			
			// Relocate portal
			if (buttonName.contains("Relocate Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					player.sendMessage(Utils.notify("&aPortal relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to portal
			else if (buttonName.contains("Teleport to Portal")) {
				Location location = arenaInstance.getPortalLocation();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo portal to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center portal
			else if (buttonName.contains("Center Portal")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getPortal() == null) {
						player.sendMessage(Utils.notify("&cNo portal to center!"));
						return;
					}
					arenaInstance.centerPortal();
					player.sendMessage(Utils.notify("&aPortal centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove portal
			else if (buttonName.contains("REMOVE PORTAL"))
				if (arenaInstance.getPortal() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createPortalConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo portal to remove!"));

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(Inventories.createPortalInventory(meta.getInteger1()));
			}

			// Relocate leaderboard
			if (buttonName.contains("Relocate Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport to Leaderboard")) {
				Location location = arenaInstance.getArenaBoardLocation();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center Leaderboard")) {
				if (arenaInstance.getArenaBoard() == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				arenaInstance.centerArenaBoard();
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE LEADERBOARD"))
				if (arenaInstance.getArenaBoardLocation() != null)
					player.openInventory(Inventories.createArenaBoardConfirmInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					player.sendMessage(Utils.notify("&aSpawn set!"));
					player.openInventory(Inventories.createPlayerSpawnInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Create spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					player.sendMessage(Utils.notify("&aSpawn relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getPlayerSpawn();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo player spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center player spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getPlayerSpawn() == null) {
						player.sendMessage(Utils.notify("&cNo player spawn to center!"));
						return;
					}
					arenaInstance.centerPlayerSpawn();
					player.sendMessage(Utils.notify("&aSpawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getPlayerSpawn() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createSpawnConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo player spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					player.sendMessage(Utils.notify("&aWaiting room set!"));
					player.openInventory(Inventories.createWaitingRoomInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Relocate waiting room
			if (buttonName.contains("Relocate")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					player.sendMessage(Utils.notify("&aWaiting room relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Teleport player to waiting room
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getWaitingRoom();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo waiting room to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center waiting room
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getWaitingRoom() == null) {
						player.sendMessage(Utils.notify("&cNo waiting room to center!"));
						return;
					}
					arenaInstance.centerWaitingRoom();
					player.sendMessage(Utils.notify("&aWaiting room centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Remove waiting room
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getWaitingRoom() != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createWaitingConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo waiting room to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getMaxPlayers();
			
			// Decrease max players
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if max players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than 1!"));
					return;
				}

				// Check if max players is greater than min players
				if (current <= arenaInstance.getMinPlayers()) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than min player!"));
					return;
				}

				arenaInstance.setMaxPlayers(--current);
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxPlayers(++current);
				player.openInventory(Inventories.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getMinPlayers();

			// Decrease min players
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if min players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMin players cannot be less than 1!"));
					return;
				}

				arenaInstance.setMinPlayers(--current);
				player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
			}

			// Increase min players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if min players is less than max players
				if (current >= arenaInstance.getMaxPlayers()) {
					player.sendMessage(Utils.notify("&cMin players cannot be greater than max player!"));
					return;
				}

				arenaInstance.setMinPlayers(++current);
				player.openInventory(Inventories.createMinPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createPlayersInventory(meta.getInteger1()));
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				player.openInventory(Inventories.createMonsterSpawnInventory(meta.getInteger1()));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getInteger1()));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createSpawnTableInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
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
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aMonster spawn set!"));
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aMonster spawn relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getMonsterSpawn(meta.getInteger2());
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo monster spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center monster spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getInteger2()) == null) {
						player.sendMessage(Utils.notify("&cNo monster spawn to center!"));
						return;
					}
					arenaInstance.centerMonsterSpawn(meta.getInteger2());
					player.sendMessage(Utils.notify("&aMonster spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Set monster type
			else if (buttonName.contains("Type"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getInteger2()) == null) {
						player.sendMessage(Utils.notify("&cNo monster spawn to set type!"));
						return;
					}
					arenaInstance.setMonsterSpawnType(meta.getInteger2(),
							(arenaInstance.getMonsterSpawnType(meta.getInteger2()) + 1) % 3);
					player.openInventory(Inventories.createMonsterSpawnMenu(meta.getInteger1(),
							meta.getInteger2()));
					player.sendMessage(Utils.notify("&aMonster spawn type changed!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(meta.getInteger2()) != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createMonsterSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo monster spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
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
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createMobsInventory(meta.getInteger1()));
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
					player.openInventory(Inventories.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getVillagerSpawn(meta.getInteger2());
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo villager spawn to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getVillagerSpawn(meta.getInteger2()) == null) {
						player.sendMessage(Utils.notify("&cNo villager spawn to center!"));
						return;
					}
					arenaInstance.centerVillagerSpawn(meta.getInteger2());
					player.sendMessage(Utils.notify("&aVillager spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getVillagerSpawn(meta.getInteger2()) != null)
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createVillagerSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo villager spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createVillagerSpawnInventory(meta.getInteger1()));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Default
			if (buttonName.contains("Default")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("default"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 1
			else if (buttonName.contains("Option 1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option1"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 2
			else if (buttonName.contains("Option 2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option2"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 3
			else if (buttonName.contains("Option 3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option3"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 4
			else if (buttonName.contains("Option 4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option4"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 5
			else if (buttonName.contains("Option 5")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option5"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Option 6
			else if (buttonName.contains("Option 6")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option6"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Custom
			else if (buttonName.contains("Custom")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (!arenaInstance.setSpawnTableFile("custom"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT")) {
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
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					player.openInventory(arenaInstance.getCustomShopEditor());
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle enchants shop
			else if (buttonName.contains("Enchants Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setEnchants(!arenaInstance.hasEnchants());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle community chest
			else if (buttonName.contains("Community Chest:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCommunity(!arenaInstance.hasCommunity());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					player.openInventory(Inventories.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
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
					player.sendMessage(Utils.notify("&cPrice cannot be above 99999 gems!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be above 99999 gems!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be above 99999 gems!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be above 99999 gems!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be negative!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be negative!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be negative!"));
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
					player.sendMessage(Utils.notify("&cPrice cannot be negative!"));
					return;
				}

				itemMeta.setDisplayName(realName + String.format("%05d", price));
				item.setItemMeta(itemMeta);
				config.set(path + meta.getInteger2(), item);
			}

			// Exit
			else if (buttonName.contains("EXIT")) {
				player.openInventory(Game.arenas[meta.getInteger1()].getCustomShopEditor());
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
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(Inventories.createAllowedKitsInventory(meta.getInteger1(), false));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic difficulty
			else if (buttonName.contains("Late Arrival:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setLateArrival(!arenaInstance.hasLateArrival());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle experience drop
			else if (buttonName.contains("Experience Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setExpDrop(!arenaInstance.hasExpDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle item drop
			else if (buttonName.contains("Item Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemDrop(!arenaInstance.hasGemDrop());
					player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit arena bounds
			else if (buttonName.contains("Arena Bounds"))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));

			// Edit wolf cap
			else if (buttonName.contains("Wolf Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit iron golem cap
			else if (buttonName.contains("Iron Golem Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createCopySettingsInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenaInventory(meta.getInteger1()));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getMaxWaves();

			// Decrease max waves
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);

				// Check if max waves is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax waves cannot be less than 1!"));
					return;
				} else arenaInstance.setMaxWaves(--current);

				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxWaves(-1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxWaves(1);
				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Increase max waves
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);
				else arenaInstance.setMaxWaves(++current);

				player.openInventory(Inventories.createMaxWaveInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getWaveTimeLimit();

			// Decrease wave time limit
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);

				// Check if wave time limit is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cWave time limit cannot be less than 1!"));
					return;
				} else arenaInstance.setWaveTimeLimit(--current);

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWaveTimeLimit(-1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWaveTimeLimit(1);
				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Increase wave time limit
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);
				else arenaInstance.setWaveTimeLimit(++current);

				player.openInventory(Inventories.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Set to Easy
			if (buttonName.contains("Easy")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Easy");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Medium");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Hard");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Insane");
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel(null);
				player.openInventory(Inventories.createDifficultyLabelInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Set to 1
			if (buttonName.contains("1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(1);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(2);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(3);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(4);
				player.openInventory(Inventories.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Allowed kits display for an arena
		else if (title.contains("Allowed Kits: ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Exit menu
			if (buttonName.contains("EXIT")) {
				assert meta != null;
				player.openInventory(Inventories.createArenaInfoInventory(Game.arenas[meta.getInteger1()]));
			}
		}

		// Allowed kits menu for an arena
		else if (title.contains("Allowed Kits")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			String kit = buttonName.substring(4);
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			List<String> banned = arenaInstance.getBannedKits();

			// Toggle a kit
			if (!(kit.equals("Gift Kits") || kit.equals("Ability Kits") || kit.equals("Effect Kits") ||
					kit.equals("EXIT"))) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				if (banned.contains(kit))
					banned.remove(kit);
				else banned.add(kit);
				arenaInstance.setBannedKits(banned);
				player.openInventory(Inventories.createAllowedKitsInventory(meta.getInteger1(), false));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Arena bounds menu for an arena
		else if (title.contains("Arena Bounds")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

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
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Corner 1 menu for an arena
		else if (title.contains("Corner 1")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					player.sendMessage(Utils.notify("&aCorner 1 set!"));
					player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner1(player.getLocation());
					player.sendMessage(Utils.notify("&aCorner 1 set!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getCorner1();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo corner 1 to teleport to!"));
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
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo corner 1 to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
		}

		// Corner 2 menu for an arena
		else if (title.contains("Corner 2")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					player.sendMessage(Utils.notify("&aCorner 2 set!"));
					player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCorner2(player.getLocation());
					player.sendMessage(Utils.notify("&aCorner 2 set!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

				// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getCorner2();
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo corner 2 to teleport to!"));
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
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo corner 2 to remove!"));

				// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createBoundsInventory(meta.getInteger1()));
		}

		// Wolf cap menu for an arena
		else if (title.contains("Wolf Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getWolfCap();

			// Decrease wolf cap
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if wolf cap is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cWolf cap cannot be less than 1!"));
					return;
				}

				arenaInstance.setWolfCap(--current);
				player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
			}

			// Increase wolf cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWolfCap(++current);
				player.openInventory(Inventories.createWolfCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Iron golem cap menu for an arena
		else if (title.contains("Iron Golem Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];
			int current = arenaInstance.getGolemCap();

			// Decrease iron golem cap
			if (buttonName.contains("Decrease")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				// Check if iron golem cap is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cIron golem cap cannot be less than 1!"));
					return;
				}

				arenaInstance.setgolemCap(--current);
				player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
			}

			// Increase iron golem cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setgolemCap(++current);
				player.openInventory(Inventories.createGolemCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaitSoundInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle ability sound
			else if (buttonName.contains("Ability")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setAbilitySound(!arenaInstance.hasAbilitySound());
					player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
		}

		// Waiting sound menu for an arena
		else if (title.contains("Waiting Sound:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arenaInstance = Game.arenas[meta.getInteger1()];

			// Exit menu
			if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createSoundsInventory(meta.getInteger1()));

			// Set sound
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
			Arena arena1 = Game.arenas[meta.getInteger1()];

			if (slot < 45) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				Arena arena2 = Game.arenas[slot];

				// Copy settings from another arena
				if (buttonType == Material.WHITE_CONCRETE)
					arena1.copy(arena2);
				else return;
			}

			// Copy easy preset
			else if (buttonName.contains("Easy Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
			else if (buttonName.contains("EXIT")) {
				player.openInventory(Inventories.createGameSettingsInventory(meta.getInteger1()));
				return;
			}

			// Save updates
			player.sendMessage(Utils.notify("&aGame settings copied!"));
		}

		// In-game item shop menu
		else if (title.contains("Item Shop")) {
			// See if the player is in a game
			if (Arrays.stream(Game.arenas).filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
				return;

			Arena arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);

			// Open weapon shop
			if (buttonName.contains("Weapon Shop"))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getWeaponShop());
				else player.sendMessage(Utils.notify(language.getString("normalShopError")));

			// Open armor shop
			else if (buttonName.contains("Armor Shop"))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getArmorShop());
				else player.sendMessage(Utils.notify(language.getString("normalShopError")));

			// Open consumables shop
			else if (buttonName.contains("Consumables Shop"))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getConsumeShop());
				else player.sendMessage(Utils.notify(language.getString("normalShopError")));

			// Open enchants shop
			else if (buttonName.contains("Enchants Shop"))
				if (arenaInstance.hasEnchants())
					player.openInventory(Inventories.createEnchantsShop());
				else player.sendMessage(Utils.notify(language.getString("enchantsShopError")));

			// Open custom shop
			else if (buttonName.contains("Custom Shop"))
				if (arenaInstance.hasCustom())
					player.openInventory(arenaInstance.getCustomShop());
				else player.sendMessage(Utils.notify(language.getString("customShopError")));

			// Open community chest
			else if (buttonName.contains("Community Chest"))
				if (arenaInstance.hasCommunity())
					player.openInventory(arenaInstance.getCommunityChest());
				else player.sendMessage(Utils.notify(language.getString("communityChestError")));
		}

		// Mock custom shop for an arena
		else if (title.contains("Custom Shop:")) {
			String name = title.substring(19);
			Arena arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull)
					.filter(arena1 -> arena1.getName().equals(name)).collect(Collectors.toList()).get(0);
			if (buttonName.contains("EXIT"))
				player.openInventory(Inventories.createArenaInfoInventory(arenaInstance));
		}

		// In-game shops
		else if (title.contains("Weapon Shop") || title.contains("Armor Shop") || title.contains("Consumables Shop") ||
				title.contains("Custom Shop")) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
						.collect(Collectors.toList()).get(0);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains("EXIT")) {
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
				player.sendMessage(Utils.notify(language.getString("buyError")));
				return;
			}

			// Remove cost meta
			buy = Utils.removeLastLore(buy);

			// Make unbreakable for blacksmith
			if (gamer.getKit().getName().equals(Kit.blacksmith().getName()))
				buy = Utils.makeUnbreakable(buy);

			// Make splash potion for witch
			if (gamer.getKit().getName().equals(Kit.witch().getName()))
				buy = Utils.makeSplash(buy);

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (gamer.getKit().getName().equals(Kit.merchant().getName()))
				gamer.addGems(cost / 10);
			Game.createBoard(gamer);

			EntityEquipment equipment = Objects.requireNonNull(player.getPlayer()).getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(GameItems.HELMET_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getHelmet() == null) {
				equipment.setHelmet(buy);
				player.sendMessage(Utils.notify(language.getString("helmet")));
			} else if (Arrays.stream(GameItems.CHESTPLATE_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getChestplate() == null) {
				equipment.setChestplate(buy);
				player.sendMessage(Utils.notify(language.getString("chestplate")));
			} else if (Arrays.stream(GameItems.LEGGING_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getLeggings() == null) {
				equipment.setLeggings(buy);
				player.sendMessage(Utils.notify(language.getString("leggings")));
			} else if (Arrays.stream(GameItems.BOOTS_MATERIALS).anyMatch(mat -> mat == buyType) &&
					Objects.requireNonNull(equipment).getBoots() == null) {
				equipment.setBoots(buy);
				player.sendMessage(Utils.notify(language.getString("boots")));
			} else {
				Utils.giveItem(player, buy, language.getString("inventoryFull"));
				player.sendMessage(Utils.notify(language.getString("buy")));
			}
		}
		else if (title.contains("Enchants Shop")) {
			Arena arenaInstance;

			// Attempt to get arena
			try {
				arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
						.collect(Collectors.toList()).get(0);
			} catch (Exception err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains("EXIT")) {
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
				player.sendMessage(Utils.notify(language.getString("buyError")));
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
			switch (enchant) {
				case "Knockback":
					give = EnchantingBook.knockback();
					break;
				case "Sweeping":
					give = EnchantingBook.sweepingEdge();
					break;
				case "Smite":
					give = EnchantingBook.smite();
					break;
				case "Sharpness":
					give = EnchantingBook.sharpness();
					break;
				case "Fire":
					give = EnchantingBook.fireAspect();
					break;
				case "Punch":
					give = EnchantingBook.punch();
					break;
				case "Piercing":
					give = EnchantingBook.piercing();
					break;
				case "Quick":
					give = EnchantingBook.quickCharge();
					break;
				case "Power":
					give = EnchantingBook.power();
					break;
				case "Loyalty":
					give = EnchantingBook.loyalty();
					break;
				case "Flame":
					give = EnchantingBook.flame();
					break;
				case "Multishot":
					give = EnchantingBook.multishot();
					break;
				case "Infinity":
					give = EnchantingBook.infinity();
					break;
				case "Blast":
					give = EnchantingBook.blastProtection();
					break;
				case "Thorns":
					give = EnchantingBook.thorns();
					break;
				case "Projectile":
					give = EnchantingBook.projectileProtection();
					break;
				case "Protection":
					give = EnchantingBook.protection();
					break;
				case "Unbreaking":
					give = EnchantingBook.unbreaking();
					break;
				case "Mending":
					give = EnchantingBook.mending();
					break;
				default:
					return;
			}
			Utils.giveItem(player, give, language.getString("inventoryFull"));
			player.sendMessage(Utils.notify(language.getString("buy")));
		}

		// Stats menu for a player
		else if (title.contains("'s Stats")) {
			String name = title.substring(6, title.length() - 8);
			if (buttonName.contains("Kits"))
				player.openInventory(Inventories.createPlayerKitsInventory(plugin, name, player.getName()));
		}

		// Kits menu for a player
		else if (title.contains("'s Kits")) {
			FileConfiguration playerData = plugin.getPlayerData();
			String name = title.substring(6, title.length() - 7);
			Kit kit = Kit.getKit(buttonName.substring(4));
			String path = name + ".kits.";

			// Check if requester is owner
			if (!name.equals(player.getName()))
				return;

			// Check if selected kit retrieval failed
			if (kit == null) {
				Utils.debugError("No kit of " + buttonName.substring(4) + " was found.", 1);
				return;
			}

			// Single tier kits
			if (!kit.isMultiLevel()) {
				if (!playerData.getBoolean(path + kit.getName()))
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(1)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(1));
						playerData.set(path + kit.getName(), true);
						player.sendMessage(Utils.notify(language.getString("kitBuy")));
					} else player.sendMessage(Utils.notify(language.getString("kitBuyError")));
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
						player.sendMessage(Utils.notify(language.getString("kitBuy")));
					} else player.sendMessage(Utils.notify(language.getString("kitBuyError")));
				} else {
					if (playerData.getInt(name + ".crystalBalance") >= kit.getPrice(++kitLevel)) {
						playerData.set(name + ".crystalBalance",
								playerData.getInt(name + ".crystalBalance") - kit.getPrice(kitLevel));
						playerData.set(path + kit.getName(), kitLevel);
						player.sendMessage(Utils.notify(language.getString("kitUpgrade")));
					} else player.sendMessage(Utils.notify(language.getString("kitUpgradeError")));
				}
			}

			if (buttonName.contains("EXIT")) {
				player.openInventory(Inventories.createPlayerStatsInventory(plugin, name));
				return;
			}

			plugin.savePlayerData();
			player.openInventory(Inventories.createPlayerKitsInventory(plugin, name, name));
		}

		// Kit selection menu for an arena
		else if (title.contains(" Kits")) {
			FileConfiguration playerData = plugin.getPlayerData();
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull)
						.filter(arena1 -> arena1.hasPlayer(player)).collect(Collectors.toList()).get(0);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			Kit kit = Kit.getKit(buttonName.substring(4));
			String path = player.getName() + ".kits.";

			// Leave if EXIT
			if (buttonName.contains("EXIT")) {
				player.closeInventory();
				return;
			}

			// Check if selected kit retrieval failed
			if (kit == null) {
				Utils.debugError("No kit of " + buttonName.substring(4) + " was found.", 1);
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
					player.sendMessage(Utils.notify(language.getString("kitSelect")));
				} else {
					player.sendMessage(Utils.notify(language.getString("kitSelectError")));
					return;
				}
			}

			// Multiple tier kits
			else {
				if (playerData.getInt(path + kit.getName()) < 1) {
					player.sendMessage(Utils.notify(language.getString("kitSelectError")));
					return;
				}
				gamer.setKit(kit.setKitLevel(playerData.getInt(path + kit.getName())));
				player.sendMessage(Utils.notify(language.getString("kitSelect")));
			}

			// Close inventory and create scoreboard
			player.closeInventory();
			Game.createBoard(gamer);
		}

		// Challenge selection menu for an arena
		else if (title.contains(" Challenges")) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = Arrays.stream(Game.arenas).filter(Objects::nonNull)
						.filter(arena1 -> arena1.hasPlayer(player)).collect(Collectors.toList()).get(0);
				gamer = arenaInstance.getPlayer(player);
			} catch (Exception err) {
				return;
			}

			Challenge challenge = Challenge.getChallenge(buttonName.substring(4));

			// Leave if EXIT
			if (buttonName.contains("EXIT")) {
				player.closeInventory();
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == PlayerStatus.SPECTATOR)
				return;

			// Option for no challenge
			if (Challenge.none().equals(challenge)) {
				gamer.resetChallenges();
				player.sendMessage(Utils.notify(language.getString("challengeAdd")));
			}

			// Remove a challenge
			else if (gamer.getChallenges().contains(challenge)) {
				gamer.removeChallenge(challenge);
				player.sendMessage(Utils.notify(language.getString("challengeDelete")));
			}

			// Add a challenge
			else {
				gamer.addChallenge(challenge);
				player.sendMessage(Utils.notify(language.getString("challengeAdd")));
			}

			// Create scoreboard and update inventory
			Game.createBoard(gamer);
			openInv(player, Inventories.createSelectChallengesInventory(gamer, arenaInstance));
		}

		// Stats menu for an arena
		else if (title.contains("Info")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;

			if (buttonName.contains("Custom Shop Inventory"))
				player.openInventory(Game.arenas[meta.getInteger1()].getMockCustomShop());

			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(Inventories.createAllowedKitsInventory(meta.getInteger1(), true));
		}
	}

	// Ensures closing inventory doesn't mess up data
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		String title = e.getView().getTitle();

		// Ignore non-plugin inventories
		if (!title.contains(Utils.format("&k")))
			return;

		// Check for community chest with shop inside it
		if (title.contains("Community Chest") && e.getInventory().contains(GameItems.shop())) {
			e.getInventory().removeItem(GameItems.shop());
			Utils.giveItem((Player) e.getPlayer(), GameItems.shop(),
					plugin.getLanguageData().getString("inventoryFull"));
		}

		// Ignore if safe close toggle is on
		if (close)
			return;

		// Close safely for the inventory of concern
		if (title.contains("Arena ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			assert meta != null;
			Arena arena = Game.arenas[meta.getInteger1()];
			if (arena.getName() == null)
				Game.arenas[meta.getInteger1()] = null;
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
			Utils.debugInfo(String.format("Name changed for arena %d!", arena.getArena()), 2);
		} catch (Exception ignored) {
			player.sendMessage(Utils.notify(Utils.format("&cInvalid arena name!")));
			if (arena.getName() == null)
				Game.arenas[arena.getArena()] = null;
			return;
		}

		player.openInventory(Inventories.createArenaInventory(arena.getArena()));
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
