package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaSpawn;
import me.theguyhere.villagerdefense.plugin.arenas.IllegalArenaNameException;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.displays.Leaderboard;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.huds.SidebarManager;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDIronGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDSnowGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDCat;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDDog;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
import me.theguyhere.villagerdefense.plugin.individuals.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.Boots;
import me.theguyhere.villagerdefense.plugin.items.armor.Chestplate;
import me.theguyhere.villagerdefense.plugin.items.armor.Helmet;
import me.theguyhere.villagerdefense.plugin.items.armor.Leggings;
import me.theguyhere.villagerdefense.plugin.items.menuItems.Shop;
import me.theguyhere.villagerdefense.plugin.items.weapons.Ammo;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class InventoryListener implements Listener {
	// Manage drag click events in custom inventories
	@EventHandler
	public void onDragCustom(InventoryDragEvent e) {
		// Ignore non-plugin inventories
		if (!(e
			.getInventory()
			.getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e
			.getInventory()
			.getHolder();

		// Cancel the event if drag isn't enabled
		if (!meta.isDragEnabled())
			e.setCancelled(true);

		// Save community chest
		if (meta.getInventoryID() == InventoryID.COMMUNITY_CHEST_INVENTORY)
			meta
				.getArena()
				.setCommunityChest(e.getInventory());
	}

	// Prevent losing items by shift clicking or number keying in custom inventory
	@EventHandler
	public void onCustomShiftClick(InventoryClickEvent e) {
		// Ignore non-plugin inventories
		if (!(e
			.getInventory()
			.getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e
			.getInventory()
			.getHolder();

		// Check for shift click
		if (e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT &&
			!e
				.getClick()
				.isKeyboardClick())
			return;

		// Cancel the event if drag isn't enabled
		if (!meta.isDragEnabled()) {
			e.setCancelled(true);
			return;
		}

		// For community chest
		if (meta.getInventoryID() == InventoryID.COMMUNITY_CHEST_INVENTORY) {
			// Prevent shop or abilities from moving around
			if (Shop.matches(e.getCurrentItem()) || VDAbility.matches(e.getCurrentItem())) {
				e.setCancelled(true);
				return;
			}

			// Save inventory
			meta
				.getArena()
				.setCommunityChest(e.getInventory());
		}
	}

	// Prevent items from being removed from the game
	@EventHandler
	public void onDragOther(InventoryDragEvent e) {
		// Ignore plugin inventories
		if (e
			.getInventory()
			.getHolder() instanceof InventoryMeta)
			return;

		// Ignore clicks in player inventory
		if (e
			.getInventory()
			.getType() == InventoryType.CRAFTING)
			return;

		// Ignore players that aren't part of an arena
		Player player = (Player) e.getWhoClicked();
		try {
			GameController.getArena(player);
		}
		catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event
		e.setCancelled(true);
	}

	@EventHandler
	public void onClickOther(InventoryClickEvent e) {
		// Ignore plugin inventories
		if (e
			.getInventory()
			.getHolder() instanceof InventoryMeta)
			return;

		// Ignore clicks in player inventory
		if (e
			.getInventory()
			.getType() == InventoryType.PLAYER || e
			.getView()
			.getType() == InventoryType.CRAFTING)
			return;

		// Ignore players that aren't part of an arena
		Player player = (Player) e.getWhoClicked();
		try {
			GameController.getArena(player);
		}
		catch (ArenaNotFoundException err) {
			return;
		}

		// Cancel event
		e.setCancelled(true);
	}

	// All click events in the inventories
	@EventHandler
	public void onCustomClick(InventoryClickEvent e) {
		// Ignore non-plugin inventories
		if (!(e
			.getInventory()
			.getHolder() instanceof InventoryMeta))
			return;
		InventoryMeta meta = (InventoryMeta) e
			.getInventory()
			.getHolder();
		InventoryID invID = meta.getInventoryID();

		// Cancel the event if click isn't enabled
		if (!meta.isClickEnabled())
			e.setCancelled(true);

		// Community chest
		if (invID == InventoryID.COMMUNITY_CHEST_INVENTORY) {
			// Prevent shop or abilities from moving around
			if (Shop.matches(e.getCurrentItem()) || VDAbility.matches(e.getCurrentItem()))
				e.setCancelled(true);

			// Save community chest
			meta
				.getArena()
				.setCommunityChest(e.getInventory());
			return;
		}

		// Ignore null inventories
		if (e.getClickedInventory() == null)
			return;

		// Ignore clicks in player inventory
		if (e
			.getClickedInventory()
			.getType() == InventoryType.PLAYER)
			return;

		// Get button information
		ItemStack button = e.getCurrentItem();
		Material buttonType;
		String buttonName;
		Player player = (Player) e.getWhoClicked();

		// Ignore null items
		if (button == null)
			return;

		// Get button type and name
		buttonType = button.getType();
		buttonName = Objects
			.requireNonNull(button.getItemMeta())
			.getDisplayName();

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
					player.openInventory(Inventories.createArenaMenu(GameController.getArena(buttonName.substring(9))));
				}
				catch (ArenaNotFoundException ignored) {
				}

				// Create new arena with naming inventory
			else if (buttonName.contains(CommunicationManager.format("&a&lNew "))) {
				Arena arena = new Arena(GameController.newArenaID());

				// Set a new arena
				GameController.addArena(GameController.newArenaID(), arena);
				NMSVersion
					.getCurrent()
					.getNmsManager()
					.nameArena(player, arena.getName(), arena.getId());
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
			String path = "lobby";

			// Create lobby
			if (buttonName.contains("Create Lobby")) {
				DataManager.setConfigurationLocation(path, player.getLocation());
				GameController.reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby set!");
				player.openInventory(Inventories.createLobbyMenu());
			}

			// Relocate lobby
			else if (buttonName.contains("Relocate Lobby")) {
				DataManager.setConfigurationLocation(path, player.getLocation());
				GameController.reloadLobby();
				PlayerManager.notifySuccess(player, "Lobby relocated!");
			}

			// Teleport player to lobby
			else if (buttonName.contains("Teleport")) {
				Location location = DataManager.getConfigLocationNoRotation(path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No lobby to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center lobby
			else if (buttonName.contains("Center")) {
				Location location = DataManager.getConfigLocationNoRotation(path);
				if (location == null) {
					PlayerManager.notifyFailure(player, "No lobby to center!");
					return;
				}
				DataManager.centerConfigLocation(path);
				PlayerManager.notifySuccess(player, "Lobby centered!");
			}

			// Remove lobby
			else if (buttonName.contains("REMOVE"))
				if (GameController
					.getArenas()
					.values()
					.stream()
					.filter(Objects::nonNull)
					.anyMatch(arena -> !arena.isClosed()))
					PlayerManager.notifyFailure(player, "All arenas must be closed to modify this!");
				else if (GameController.getLobby() != null)
					player.openInventory(Inventories.createLobbyConfirmMenu());
				else PlayerManager.notifyFailure(player, "No lobby to remove!");

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
				player.openInventory(Inventories.createInfoBoardMenu(GameController.newInfoBoardID()));

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
				GameController.setInfoBoard(player.getLocation(), id);
				PlayerManager.notifySuccess(player, "Info board set!");
				player.openInventory(Inventories.createInfoBoardMenu(id));
			}

			// Relocate board
			else if (buttonName.contains("Relocate")) {
				GameController.setInfoBoard(player.getLocation(), id);
				PlayerManager.notifySuccess(player, "Info board relocated!");
			}

			// Teleport player to info board
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getInfoBoard(id)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No info board to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center info board
			else if (buttonName.contains("Center")) {
				if (GameController
					.getInfoBoard(id)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No info board to center!");
					return;
				}
				GameController.centerInfoBoard(id);
				PlayerManager.notifySuccess(player, "Info board centered!");
			}

			// Remove info board
			else if (buttonName.contains("REMOVE"))
				if (GameController
					.getInfoBoard(id)
					.getLocation() != null)
					player.openInventory(Inventories.createInfoBoardConfirmMenu(id));
				else PlayerManager.notifyFailure(player, "No info board to remove!");

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
			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOTAL_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOTAL_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getLeaderboard(Leaderboard.TOTAL_KILLS)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameController
					.getLeaderboard(Leaderboard.TOTAL_KILLS)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameController.centerLeaderboard(Leaderboard.TOTAL_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (GameController.getLeaderboard(Leaderboard.TOTAL_KILLS) != null)
					player.openInventory(Inventories.createTotalKillsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top kills leaderboard menu
		else if (invID == InventoryID.TOP_KILLS_LEADERBOARD_MENU) {
			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getLeaderboard(Leaderboard.TOP_KILLS)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameController
					.getLeaderboard(Leaderboard.TOP_KILLS)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameController.centerLeaderboard(Leaderboard.TOP_KILLS);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (GameController.getLeaderboard(Leaderboard.TOP_KILLS) != null)
					player.openInventory(Inventories.createTopKillsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Total gems leaderboard menu
		else if (invID == InventoryID.TOTAL_GEMS_LEADERBOARD_MENU) {
			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOTAL_GEMS);
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOTAL_GEMS);
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getLeaderboard(Leaderboard.TOTAL_GEMS)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameController
					.getLeaderboard(Leaderboard.TOTAL_GEMS)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameController.centerLeaderboard(Leaderboard.TOTAL_GEMS);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (GameController.getLeaderboard(Leaderboard.TOTAL_GEMS) != null)
					player.openInventory(Inventories.createTotalGemsConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top balance leaderboard menu
		else if (invID == InventoryID.TOP_BALANCE_LEADERBOARD_MENU) {
			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_BALANCE);
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_BALANCE);
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getLeaderboard(Leaderboard.TOP_BALANCE)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameController
					.getLeaderboard(Leaderboard.TOP_BALANCE)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameController.centerLeaderboard(Leaderboard.TOP_BALANCE);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (GameController.getLeaderboard(Leaderboard.TOP_BALANCE) != null)
					player.openInventory(Inventories.createTopBalanceConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Top wave leaderboard menu
		else if (invID == InventoryID.TOP_WAVE_LEADERBOARD_MENU) {
			// Create leaderboard
			if (buttonName.contains("Create")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_WAVE);
				PlayerManager.notifySuccess(player, "Leaderboard set!");
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				GameController.setLeaderboard(player.getLocation(), Leaderboard.TOP_WAVE);
				PlayerManager.notifySuccess(player, "Leaderboard relocated!");
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = GameController
					.getLeaderboard(Leaderboard.TOP_WAVE)
					.getLocation();
				if (location == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
					return;
				}
				player.teleport(location);
				closeInv(player);
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				if (GameController
					.getLeaderboard(Leaderboard.TOP_WAVE)
					.getLocation() == null) {
					PlayerManager.notifyFailure(player, "No leaderboard to center!");
					return;
				}
				GameController.centerLeaderboard(Leaderboard.TOP_WAVE);
				PlayerManager.notifySuccess(player, "Leaderboard centered!");
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (GameController.getLeaderboard(Leaderboard.TOP_WAVE) != null)
					player.openInventory(Inventories.createTopWaveConfirmMenu());
				else PlayerManager.notifyFailure(player, "No leaderboard to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createLeaderboardDashboard());
		}

		// Menu for an arena
		else if (invID == InventoryID.ARENA_MENU) {
			Arena arenaInstance = meta.getArena();

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed())
					NMSVersion
						.getCurrent()
						.getNmsManager()
						.nameArena(
							player,
							arenaInstance.getName(),
							arenaInstance.getId()
						);
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

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

				// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));

				// Toggle arena close
			else if (buttonName.contains("Close")) {
				// Arena currently closed
				if (arenaInstance.isClosed()) {
					// No lobby
					if (GameController.getLobby() == null) {
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
					if (arenaInstance
						.getMonsterSpawns()
						.isEmpty()) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a monster spawn!");
						return;
					}

					// No villager spawn
					if (arenaInstance
						.getVillagerSpawns()
						.isEmpty()) {
						PlayerManager.notifyFailure(player, "Arena cannot open without a villager spawn!");
						return;
					}

					// Invalid arena bounds
					if (arenaInstance.getCorner1() == null || arenaInstance.getCorner2() == null ||
						!Objects.equals(
							arenaInstance
								.getCorner1()
								.getWorld(),
							arenaInstance
								.getCorner2()
								.getWorld()
						)) {
						PlayerManager.notifyFailure(player, "Arena cannot open without valid arena bounds!");
						return;
					}

					// Outdated file configs
					if (Main.isOutdated()) {
						PlayerManager.notifyFailure(
							player,
							"Arena cannot open when file configurations are outdated!"
						);
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
				meta
					.getArena()
					.removePortal();

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
				meta
					.getArena()
					.removeArenaBoard();

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
				if (arenaInstance
					.getMonsterSpawns()
					.isEmpty())
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
				if (arenaInstance
					.getVillagerSpawns()
					.isEmpty())
					arenaInstance.setClosed(true);
				PlayerManager.notifySuccess(player, "Mob spawn removed!");
				player.openInventory(Inventories.createVillagerSpawnDashboard(meta.getArena()));
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
				GameController.saveLobby(null);
				GameController.reloadLobby();
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
				GameController.removeInfoBoard(meta.getId());

				// Confirm and return
				PlayerManager.notifySuccess(player, "Info board removed!");
				player.openInventory(Inventories.createInfoBoardDashboard());
			}
		}

		// Confirm to remove total kills leaderboard
		else if (invID == InventoryID.TOTAL_KILLS_CONFIRM_MENU) {

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameController.removeLeaderboard(Leaderboard.TOTAL_KILLS);

				// Remove leaderboard
				GameController.removeLeaderboard("totalKills");

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTotalKillsLeaderboardMenu());
			}
		}

		// Confirm to remove top kills leaderboard
		else if (invID == InventoryID.TOP_KILLS_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameController.removeLeaderboard(Leaderboard.TOP_KILLS);

				// Remove leaderboard
				GameController.removeLeaderboard("topKills");

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTopKillsLeaderboardMenu());
			}
		}

		// Confirm to remove total gems leaderboard
		else if (invID == InventoryID.TOTAL_GEMS_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameController.removeLeaderboard(Leaderboard.TOTAL_GEMS);

				// Remove leaderboard
				GameController.removeLeaderboard("totalGems");

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTotalGemsLeaderboardMenu());
			}
		}

		// Confirm to remove top balance leaderboard
		else if (invID == InventoryID.TOP_BALANCE_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameController.removeLeaderboard(Leaderboard.TOP_BALANCE);

				// Remove leaderboard
				GameController.removeLeaderboard("topBalance");

				// Confirm and return
				PlayerManager.notifySuccess(player, "Leaderboard removed!");
				player.openInventory(Inventories.createTopBalanceLeaderboardMenu());
			}
		}

		// Confirm to remove top wave leaderboard
		else if (invID == InventoryID.TOP_WAVE_CONFIRM_MENU) {
			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createTopWaveLeaderboardMenu());

				// Remove the leaderboard, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove leaderboard data
				GameController.removeLeaderboard(Leaderboard.TOP_WAVE);

				// Remove leaderboard
				GameController.removeLeaderboard("topWave");

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
				GameController.removeArena(meta
					.getArena()
					.getId());

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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate portal
			if (buttonName.contains("Relocate Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					PlayerManager.notifySuccess(player, "Portal relocated!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Create spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					PlayerManager.notifySuccess(player, "Spawn relocated!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				try {
					player.teleport(arenaInstance
						.getPlayerSpawn()
						.getLocation());
					closeInv(player);
				}
				catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No player spawn to teleport to!");
				}
			}

			// Center player spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getPlayerSpawn() != null) {
						arenaInstance.centerPlayerSpawn();
						PlayerManager.notifySuccess(player, "Spawn centered!");
					}
					else PlayerManager.notifyFailure(player, "No player spawn to center!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getPlayerSpawn() != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createSpawnConfirmMenu(meta.getArena()));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				}
				else PlayerManager.notifyFailure(player, "No player spawn to remove!");

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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Relocate waiting room
			if (buttonName.contains("Relocate")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					PlayerManager.notifySuccess(player, "Waiting room relocated!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

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

			else if (buttonName.contains("Villager Type:"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createVillagerTypeMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

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
						meta
							.getArena()
							.newMonsterSpawnID()
					)
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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Monster spawn relocated!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance
						.getMonsterSpawn(meta.getId())
						.getLocation());
					closeInv(player);
				}
				catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No monster spawn to teleport to!");
				}

				// Center monster spawn
			else if (buttonName.contains("Center"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
						arenaInstance.centerMonsterSpawn(meta.getId());
						PlayerManager.notifySuccess(player, "Monster spawn centered!");
					}
					else PlayerManager.notifyFailure(player, "No monster spawn to center!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Set monster type
			else if (buttonName.contains("Type"))
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
						arenaInstance.setMonsterSpawnType(
							meta.getId(),
							(arenaInstance.getMonsterSpawnType(meta.getId()) + 1) % 3
						);
						player.openInventory(Inventories.createMonsterSpawnMenu(
							meta.getArena(),
							meta.getId()
						));
						PlayerManager.notifySuccess(player, "Monster spawn type changed!");
					}
					else PlayerManager.notifyFailure(player, "No monster spawn to set type!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(meta.getId()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createMonsterSpawnConfirmMenu(
							meta.getArena(),
							meta.getId()
						));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				}
				else PlayerManager.notifyFailure(player, "No monster spawn to remove!");

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
						meta
							.getArena()
							.newVillagerSpawnID()
					)
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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getId(), player.getLocation());
					PlayerManager.notifySuccess(player, "Villager spawn set!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Teleport player to spawn
			else if (buttonName.contains("Teleport"))
				try {
					player.teleport(arenaInstance
						.getVillagerSpawn(meta.getId())
						.getLocation());
					closeInv(player);
				}
				catch (NullPointerException err) {
					PlayerManager.notifyFailure(player, "No villager spawn to teleport to!");
				}

				// Center villager spawn
			else if (buttonName.contains("Center")) {
				if (arenaInstance.isClosed()) {
					if (arenaInstance.getVillagerSpawn(meta.getId()) != null) {
						arenaInstance.centerVillagerSpawn(meta.getId());
						PlayerManager.notifySuccess(player, "Villager spawn centered!");
					}
					else PlayerManager.notifyFailure(player, "No villager spawn to center!");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getVillagerSpawn(meta.getId()) != null) {
					if (arenaInstance.isClosed())
						player.openInventory(Inventories.createVillagerSpawnConfirmMenu(
							meta.getArena(),
							meta.getId()
						));
					else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				}
				else PlayerManager.notifyFailure(player, "No villager spawn to remove!");

				// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createVillagerSpawnDashboard(meta.getArena()));
		}

		// Villager type menu for an arena
		else if (invID == InventoryID.VILLAGER_TYPE_MENU) {
			Arena arenaInstance = meta.getArena();

			if (buttonName.contains("Desert")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("desert");
			}
			if (buttonName.contains("Jungle")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("jungle");
			}
			if (buttonName.contains("Plains")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("plains");
			}
			if (buttonName.contains("Savanna")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("savanna");
			}
			if (buttonName.contains("Snow")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("snow");
			}
			if (buttonName.contains("Swamp")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("swamp");
			}
			if (buttonName.contains("Taiga")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arenaInstance.setVillagerType("taiga");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				return;
			}

			// Reload inventory
			player.openInventory(Inventories.createVillagerTypeMenu(meta.getArena()));
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
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 1
			else if (buttonName.contains("Option 1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option1"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 2
			else if (buttonName.contains("Option 2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option2"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 3
			else if (buttonName.contains("Option 3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option3"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 4
			else if (buttonName.contains("Option 4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option4"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 5
			else if (buttonName.contains("Option 5")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option5"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Option 6
			else if (buttonName.contains("Option 6")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("option6"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Custom
			else if (buttonName.contains("Custom")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (!arenaInstance.setSpawnTableFile("custom"))
					PlayerManager.notifyFailure(player, "File does not exist!");
			}

			// Exit menu
			else if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createMobsMenu(meta.getArena()));
				return;
			}

			// Reload inventory
			player.openInventory(Inventories.createSpawnTableMenu(meta.getArena()));
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
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

			// Toggle community chest
			if (buttonName.contains("Community Chest:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCommunity(!arenaInstance.hasCommunity());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

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
			else if (buttonName.contains("Late Arrival:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setLateArrival(!arenaInstance.hasLateArrival());
					player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Edit arena bounds
			else if (buttonName.contains("Arena Bounds"))
				player.openInventory(Inventories.createBoundsMenu(meta.getArena()));

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
				}
				else arenaInstance.setMaxWaves(--current);

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
				}
				else arenaInstance.setWaveTimeLimit(--current);

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
			Kit kit = Kit.getKitByName(buttonName.substring(4));
			Arena arenaInstance = meta.getArena();
			List<String> banned = arenaInstance.getBannedKitIDs();

			// Toggle a kit
			if (kit != null) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (banned.contains(kit.getID()))
					banned.remove(kit.getID());
				else banned.add(kit.getID());
				arenaInstance.setBannedKitIDs(banned);
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
			Challenge challenge = Challenge.getChallengeByName(buttonName.substring(4));
			Arena arenaInstance = meta.getArena();
			List<String> forced = arenaInstance.getForcedChallengeIDs();

			// Exit menu
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGameSettingsMenu(meta.getArena()));

				// Toggle a challenge
			else if (challenge != null) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				if (forced.contains(challenge.getID()))
					forced.remove(challenge.getID());
				else forced.add(challenge.getID());
				arenaInstance.setForcedChallengeIDs(forced);
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

				// Stretch bounds
			else if (buttonName.contains("Stretch Bounds")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.stretchBounds();
					PlayerManager.notifySuccess(player, "Arena bounds for " + arenaInstance.getName() +
						" have been stretched.");
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

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
				if (!arenaInstance.isClosed())
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else if (GameController
					.getArenas()
					.values()
					.stream()
					.anyMatch(arena -> !arena.equals(arenaInstance) &&
						arena
							.getBounds()
							.contains(player
								.getLocation()
								.toVector())))
					PlayerManager.notifyFailure(player, "Arena bounds cannot intersect another arena!");
				else {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
					player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
				}

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (!arenaInstance.isClosed())
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else if (GameController
					.getArenas()
					.values()
					.stream()
					.anyMatch(arena -> !arena.equals(arenaInstance) &&
						arena
							.getBounds()
							.contains(player
								.getLocation()
								.toVector())))
					PlayerManager.notifyFailure(player, "Arena bounds cannot intersect another arena!");
				else {
					arenaInstance.setCorner1(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 1 set!");
				}

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
				if (!arenaInstance.isClosed())
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else if (GameController
					.getArenas()
					.values()
					.stream()
					.anyMatch(arena -> !arena.equals(arenaInstance) &&
						arena
							.getBounds()
							.contains(player
								.getLocation()
								.toVector())))
					PlayerManager.notifyFailure(player, "Arena bounds cannot intersect another arena!");
				else {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
					player.openInventory(Inventories.createBoundsMenu(meta.getArena()));
				}

			// Relocate spawn
			if (buttonName.contains("Relocate"))
				if (!arenaInstance.isClosed())
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
				else if (GameController
					.getArenas()
					.values()
					.stream()
					.anyMatch(arena -> !arena.equals(arenaInstance) &&
						arena
							.getBounds()
							.contains(player
								.getLocation()
								.toVector())))
					PlayerManager.notifyFailure(player, "Arena bounds cannot intersect another arena!");
				else {
					arenaInstance.setCorner2(player.getLocation());
					PlayerManager.notifySuccess(player, "Corner 2 set!");
				}

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

		// Sound settings menu for an arena
		else if (invID == InventoryID.SOUNDS_MENU) {
			Arena arenaInstance = meta.getArena();

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(Inventories.createWaitSoundMenu(meta.getArena()));
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");

				// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
			}

			// Toggle ability sound
			else if (buttonName.contains("Ability")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setAbilitySound(!arenaInstance.hasAbilitySound());
					player.openInventory(Inventories.createSoundsMenu(meta.getArena()));
				}
				else PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
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

				arenaInstance.setWaitingSound(buttonName
					.toLowerCase()
					.substring(4));
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
					arena2 = GameController.getArena(buttonName.substring(9));
				}
				catch (ArenaNotFoundException err) {
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

				arena1.setMaxWaves(25);
				arena1.setWaveTimeLimit(5);
				arena1.setDifficultyMultiplier(1);
				arena1.setDynamicLimit(true);
				arena1.setDifficultyLabel("Easy");
			}

			// Copy medium preset
			else if (buttonName.contains("Medium Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(30);
				arena1.setWaveTimeLimit(4);
				arena1.setDifficultyMultiplier(2);
				arena1.setDynamicLimit(true);
				arena1.setDifficultyLabel("Medium");
			}

			// Copy hard preset
			else if (buttonName.contains("Hard Preset")) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					PlayerManager.notifyFailure(player, "Arena must be closed to modify this!");
					return;
				}

				arena1.setMaxWaves(35);
				arena1.setWaveTimeLimit(3);
				arena1.setDifficultyMultiplier(3);
				arena1.setDynamicLimit(true);
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
				arena1.setDynamicLimit(false);
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
			VDPlayer gamer;

			// See if the player is in a game
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Open sword shop
			if (buttonName.contains(LanguageManager.names.swordShop))
				player.openInventory(Inventories.createSwordShopMenu(arenaInstance));

				// Open axe shop
			else if (buttonName.contains(LanguageManager.names.axeShop))
				player.openInventory(Inventories.createAxeShopMenu(arenaInstance));

				// Open scythe shop
			else if (buttonName.contains(LanguageManager.names.scytheShop))
				player.openInventory(Inventories.createScytheShopMenu(arenaInstance));

				// Open bow shop
			else if (buttonName.contains(LanguageManager.names.bowShop))
				player.openInventory(Inventories.createBowShopMenu(arenaInstance));

				// Open crossbow shop
			else if (buttonName.contains(LanguageManager.names.crossbowShop))
				player.openInventory(Inventories.createCrossbowShopMenu(arenaInstance));

				// Open ammo shop
			else if (buttonName.contains(LanguageManager.names.ammoShop))
				player.openInventory(Inventories.createAmmoUpgradeShopMenu(arenaInstance, gamer));

				// Open helmet shop
			else if (buttonName.contains(LanguageManager.names.helmetShop))
				player.openInventory(Inventories.createHelmetShopMenu(arenaInstance));

				// Open chestplate shop
			else if (buttonName.contains(LanguageManager.names.chestplateShop))
				player.openInventory(Inventories.createChestplateShopMenu(arenaInstance));

				// Open leggings shop
			else if (buttonName.contains(LanguageManager.names.leggingsShop))
				player.openInventory(Inventories.createLeggingsShopMenu(arenaInstance));

				// Open boots shop
			else if (buttonName.contains(LanguageManager.names.bootsShop))
				player.openInventory(Inventories.createBootsShopMenu(arenaInstance));

				// Open consumables shop
			else if (buttonName.contains(LanguageManager.names.consumableShop))
				player.openInventory(Inventories.createConsumableShopMenu(arenaInstance));

				// Open pet shop
			else if (buttonName.contains(CommunicationManager.format(LanguageManager.names.petShop,
				Integer.toString(gamer.getRemainingPetSlots()), Integer.toString(gamer.getPetSlots())
			)))
				player.openInventory(Inventories.createPetShopMenu(arenaInstance, gamer));

				// Open golem shop
			else if (buttonName.contains(LanguageManager.names.golemShop))
				player.openInventory(Inventories.createGolemShopMenu(arenaInstance));

				// Open ability upgrade shop
			else if (buttonName.contains(LanguageManager.names.abilityUpgradeShop))
				player.openInventory(Inventories.createAbilityUpgradeShopMenu(arenaInstance, gamer));

				// Open community chest
			else if (buttonName.contains(LanguageManager.names.communityChest)) {
				if (arenaInstance.hasCommunity())
					player.openInventory(arenaInstance.getCommunityChest());
				else PlayerManager.notifyFailure(player, LanguageManager.errors.communityChest);
			}
		}

		// In-game shops
		else if (InventoryID.isInGameShop(invID)) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createShopMenu(arenaInstance, gamer));
				return;
			}

			// Ignore null items
			if (e
				.getClickedInventory()
				.getItem(e.getSlot()) == null)
				return;

			ItemStack buy = Objects
				.requireNonNull(e
					.getClickedInventory()
					.getItem(e.getSlot()))
				.clone();
			Integer cost = Objects
				.requireNonNull(buy.getItemMeta())
				.getPersistentDataContainer()
				.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);

			// Ignore un-purchasable items
			if (cost == null)
				return;

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
				return;
			}

			// Remove cost meta
			buy = ItemStackBuilder.removeLastLore(ItemStackBuilder.removeLastLore(buy));

			// Make unbreakable for blacksmith (not sharing)
			if (Kit
				.blacksmith()
				.setKitLevel(1)
				.equals(gamer.getKit()) && !gamer.isSharing())
				buy = ItemStackBuilder.makeUnbreakable(buy);

			// Make unbreakable for successful blacksmith sharing
			Random random = new Random();
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.BLACKSMITH))) {
				buy = ItemStackBuilder.makeUnbreakable(buy);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (Kit
				.merchant()
				.setKitLevel(1)
				.equals(gamer.getKit()) && !gamer.isSharing())
				gamer.addGems(cost / 10);
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
				gamer.addGems(cost / 10);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}
			SidebarManager.updateActivePlayerSidebar(gamer);

			EntityEquipment equipment = Objects
				.requireNonNull(player.getPlayer())
				.getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Helmet.matches(buy) && Objects
				.requireNonNull(equipment)
				.getHelmet() == null) {
				equipment.setHelmet(buy);
				gamer.updateArmor();
				PlayerManager.notifySuccess(player, LanguageManager.confirms.helmet);
			}
			else if (Chestplate.matches(buy) && Objects
				.requireNonNull(equipment)
				.getChestplate() == null) {
				equipment.setChestplate(buy);
				gamer.updateArmor();
				PlayerManager.notifySuccess(player, LanguageManager.confirms.chestplate);
			}
			else if (Leggings.matches(buy) && Objects
				.requireNonNull(equipment)
				.getLeggings() == null) {
				equipment.setLeggings(buy);
				gamer.updateArmor();
				PlayerManager.notifySuccess(player, LanguageManager.confirms.leggings);
			}
			else if (Boots.matches(buy) && Objects
				.requireNonNull(equipment)
				.getBoots() == null) {
				equipment.setBoots(buy);
				gamer.updateArmor();
				PlayerManager.notifySuccess(player, LanguageManager.confirms.boots);
			}
			else {
				PlayerManager.giveItem(player, buy, LanguageManager.errors.inventoryFull);
				PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
			}
		}

		// Pet shop
		else if (invID == InventoryID.PET_SHOP_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Edit existing
			if (buttonName.contains(LanguageManager.messages.mobName
				.replace("%s", "")
				.trim()))
				player.openInventory(Inventories.createPetManagerMenu(arenaInstance, gamer,
					e.getSlot() - meta.getId()
				));

				// Create new
			else if (buttonName.contains(CommunicationManager.format("&a&lNew ")))
				player.openInventory(Inventories.createNewPetMenu(arenaInstance, gamer));

				// Return to main shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createShopMenu(arenaInstance, gamer));
		}

		// New pet menu
		else if (invID == InventoryID.NEW_PET_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Add pet
			if (buttonName.contains(LanguageManager.mobs.dog) || buttonName.contains(LanguageManager.mobs.cat) ||
				buttonName.contains(LanguageManager.mobs.horse)) {
				ItemStack buy = Objects
					.requireNonNull(e
						.getClickedInventory()
						.getItem(e.getSlot()))
					.clone();
				Integer cost = Objects
					.requireNonNull(buy.getItemMeta())
					.getPersistentDataContainer()
					.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);
				Random random = new Random();
				if (cost == null)
					return;

				// Check if they can afford the item
				if (!gamer.canAfford(cost)) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
					return;
				}

				// Subtract from balance, apply rebate, and update scoreboard
				gamer.addGems(-cost);
				if (Kit
					.merchant()
					.setKitLevel(1)
					.equals(gamer.getKit()) && !gamer.isSharing())
					gamer.addGems(cost / 10);
				if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
					gamer.addGems(cost / 10);
					PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				}
				SidebarManager.updateActivePlayerSidebar(gamer);

				// Spawn pet
				if (buttonName.contains(LanguageManager.mobs.dog))
					gamer.addPet(new VDDog(arenaInstance, player.getLocation(), gamer, 1));
				if (buttonName.contains(LanguageManager.mobs.cat))
					gamer.addPet(new VDCat(arenaInstance, player.getLocation(), gamer, 1));
				if (buttonName.contains(LanguageManager.mobs.horse))
					gamer.addPet(new VDHorse(arenaInstance, player.getLocation(), gamer, 1));
				player.openInventory(Inventories.createPetManagerMenu(arenaInstance, gamer,
					gamer
						.getPets()
						.size() - 1
				));
			}

			// Return to pet shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPetShopMenu(arenaInstance, gamer));
		}

		// Pet manager menu
		else if (invID == InventoryID.PET_MANAGER_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Upgrade the pet
			if (buttonName.contains(LanguageManager.messages.eggName
				.replace("%s", "")
				.trim())) {
				ItemStack buy = Objects
					.requireNonNull(e
						.getClickedInventory()
						.getItem(e.getSlot()))
					.clone();
				Integer cost = Objects
					.requireNonNull(buy.getItemMeta())
					.getPersistentDataContainer()
					.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);
				Random random = new Random();
				if (cost == null)
					return;

				// Check if they can afford the item
				if (!gamer.canAfford(cost)) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
					return;
				}

				// Subtract from balance, apply rebate, and update scoreboard
				gamer.addGems(-cost);
				if (Kit
					.merchant()
					.setKitLevel(1)
					.equals(gamer.getKit()) && !gamer.isSharing())
					gamer.addGems(cost / 10);
				if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
					gamer.addGems(cost / 10);
					PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				}
				SidebarManager.updateActivePlayerSidebar(gamer);

				// Upgrade pet
				gamer
					.getPets()
					.get(meta.getId())
					.incrementLevel();
				player.openInventory(Inventories.createPetManagerMenu(arenaInstance, gamer, meta.getId()));
			}

			// Remove the pet
			else if (buttonName.contains(LanguageManager.messages.removePet))
				player.openInventory(Inventories.createPetConfirmMenu(arenaInstance, player.getUniqueId(),
					meta.getId()
				));

				// Return to pet shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPetShopMenu(arenaInstance, gamer));
		}

		// Pet removal confirmation
		else if (invID == InventoryID.PET_CONFIRM_MENU) {
			Arena arenaInstance = meta.getArena();
			VDPlayer gamer;
			try {
				gamer = arenaInstance.getPlayer(meta.getPlayerID());
			}
			catch (PlayerNotFoundException err) {
				return;
			}

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createPetManagerMenu(arenaInstance, gamer, meta.getId()));

				// Remove pet, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove data
				gamer.removePet(meta.getId());

				// Confirm and return
				PlayerManager.notifySuccess(player, LanguageManager.confirms.petRemove);
				player.openInventory(Inventories.createPetShopMenu(arenaInstance, gamer));
			}
		}

		// Golem shop
		else if (invID == InventoryID.GOLEM_SHOP_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Edit existing
			if (buttonName.contains(LanguageManager.messages.mobName
				.replace("%s", "")
				.trim()))
				player.openInventory(Inventories.createGolemManagerMenu(
					arenaInstance,
					e.getSlot() - meta.getId()
				));

				// Create new
			else if (buttonName.contains(CommunicationManager.format("&a&lNew ")))
				player.openInventory(Inventories.createNewGolemMenu(arenaInstance));

				// Return to main shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createShopMenu(arenaInstance, gamer));
		}

		// New pet menu
		else if (invID == InventoryID.NEW_GOLEM_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Add golem
			if (buttonName.contains(LanguageManager.mobs.ironGolem) ||
				buttonName.contains(LanguageManager.mobs.snowGolem)) {
				ItemStack buy = Objects
					.requireNonNull(e
						.getClickedInventory()
						.getItem(e.getSlot()))
					.clone();
				Integer cost = Objects
					.requireNonNull(buy.getItemMeta())
					.getPersistentDataContainer()
					.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);
				Random random = new Random();
				if (cost == null)
					return;

				// Check if they can afford the item
				if (!gamer.canAfford(cost)) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
					return;
				}

				// Subtract from balance, apply rebate, and update scoreboard
				gamer.addGems(-cost);
				if (Kit
					.merchant()
					.setKitLevel(1)
					.equals(gamer.getKit()) && !gamer.isSharing())
					gamer.addGems(cost / 10);
				if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
					gamer.addGems(cost / 10);
					PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				}
				SidebarManager.updateActivePlayerSidebar(gamer);

				// Spawn golem
				ArenaSpawn spawn = arenaInstance
					.getVillagerSpawns()
					.get(arenaInstance
						.getGolems()
						.size());
				if (buttonName.contains(LanguageManager.mobs.ironGolem))
					arenaInstance.addGolem(new VDIronGolem(arenaInstance, spawn.getLocation(), 1));
				if (buttonName.contains(LanguageManager.mobs.snowGolem))
					arenaInstance.addGolem(new VDSnowGolem(arenaInstance, spawn.getLocation(), 1));
				player.openInventory(Inventories.createGolemManagerMenu(
					arenaInstance,
					arenaInstance
						.getGolems()
						.size() - 1
				));
			}

			// Return to golem shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGolemShopMenu(arenaInstance));
		}

		// Golem manager menu
		else if (invID == InventoryID.GOLEM_MANAGER_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Upgrade the golem
			if (buttonName.contains(LanguageManager.messages.eggName
				.replace("%s", "")
				.trim())) {
				ItemStack buy = Objects
					.requireNonNull(e
						.getClickedInventory()
						.getItem(e.getSlot()))
					.clone();
				Integer cost = Objects
					.requireNonNull(buy.getItemMeta())
					.getPersistentDataContainer()
					.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);
				Random random = new Random();
				if (cost == null)
					return;

				// Check if they can afford the item
				if (!gamer.canAfford(cost)) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
					return;
				}

				// Subtract from balance, apply rebate, and update scoreboard
				gamer.addGems(-cost);
				if (Kit
					.merchant()
					.setKitLevel(1)
					.equals(gamer.getKit()) && !gamer.isSharing())
					gamer.addGems(cost / 10);
				if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
					gamer.addGems(cost / 10);
					PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
				}
				SidebarManager.updateActivePlayerSidebar(gamer);

				// Upgrade golem
				arenaInstance
					.getGolems()
					.get(meta.getId())
					.incrementLevel();
				player.openInventory(Inventories.createGolemManagerMenu(arenaInstance, meta.getId()));
			}

			// Remove the golem
			else if (buttonName.contains(LanguageManager.messages.removeGolem))
				player.openInventory(Inventories.createGolemConfirmMenu(arenaInstance, meta.getId()));

				// Return to golem shop menu
			else if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createGolemShopMenu(arenaInstance));
		}

		// Golem removal confirmation
		else if (invID == InventoryID.GOLEM_CONFIRM_MENU) {
			Arena arenaInstance = meta.getArena();

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createGolemManagerMenu(arenaInstance, meta.getId()));

				// Remove golem, then return to previous menu
			else if (buttonName.contains("YES")) {
				// Remove data
				VDGolem golem = arenaInstance
					.getGolems()
					.get(meta.getId());
				golem.kill();
				arenaInstance.removeMob(golem.getID());
				arenaInstance
					.getGolems()
					.remove(meta.getId());

				// Confirm and return
				PlayerManager.notifySuccess(player, LanguageManager.confirms.golemRemove);
				player.openInventory(Inventories.createGolemShopMenu(arenaInstance));
			}
		}

		// Ability upgrade shop
		else if (invID == InventoryID.ABILITY_UPGRADE_SHOP_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createShopMenu(arenaInstance, gamer));
				return;
			}

			// Ignore null items
			if (e
				.getClickedInventory()
				.getItem(e.getSlot()) == null)
				return;

			ItemStack buy = Objects
				.requireNonNull(e
					.getClickedInventory()
					.getItem(e.getSlot()))
				.clone();
			Integer cost = Objects
				.requireNonNull(buy.getItemMeta())
				.getPersistentDataContainer()
				.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);

			// Ignore un-purchasable items
			if (cost == null)
				return;

			Random random = new Random();

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
				return;
			}

			// Remove cost meta
			buy = ItemStackBuilder.removeLastLore(ItemStackBuilder.removeLastLore(buy));

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (Kit
				.merchant()
				.setKitLevel(1)
				.equals(gamer.getKit()) && !gamer.isSharing())
				gamer.addGems(cost / 10);
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
				gamer.addGems(cost / 10);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}
			SidebarManager.updateActivePlayerSidebar(gamer);

			// Update player stats and shop
			gamer.incrementTieredEssenceLevel();
			player.openInventory(Inventories.createAbilityUpgradeShopMenu(arenaInstance, gamer));

			// Find and upgrade, otherwise give
			for (int i = 1; i < 46; i++) {
				if (VDAbility.matches(player
					.getInventory()
					.getItem(i))) {
					player
						.getInventory()
						.setItem(i, buy);
					PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
					return;
				}
			}
			PlayerManager.giveItem(player, buy, LanguageManager.errors.inventoryFull);
			PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
		}

		// Ammo upgrade shop
		else if (invID == InventoryID.AMMO_UPGRADE_SHOP_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			// Return to main shop menu
			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createShopMenu(arenaInstance, gamer));
				return;
			}

			// Ignore null items
			if (e
				.getClickedInventory()
				.getItem(e.getSlot()) == null)
				return;

			ItemStack buy = Objects
				.requireNonNull(e
					.getClickedInventory()
					.getItem(e.getSlot()))
				.clone();
			Integer cost = Objects
				.requireNonNull(buy.getItemMeta())
				.getPersistentDataContainer()
				.get(VDItem.PRICE_KEY, PersistentDataType.INTEGER);

			// Ignore un-purchasable items
			if (cost == null)
				return;

			Random random = new Random();

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				PlayerManager.notifyFailure(player, LanguageManager.errors.buy);
				return;
			}

			// Remove cost meta
			buy = ItemStackBuilder.removeLastLore(ItemStackBuilder.removeLastLore(buy));

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (Kit
				.merchant()
				.setKitLevel(1)
				.equals(gamer.getKit()) && !gamer.isSharing())
				gamer.addGems(cost / 10);
			if (random.nextDouble() > Math.pow(.75, arenaInstance.effectShareCount(Kit.EffectType.MERCHANT))) {
				gamer.addGems(cost / 10);
				PlayerManager.notifySuccess(player, LanguageManager.messages.effectShare);
			}
			SidebarManager.updateActivePlayerSidebar(gamer);

			// Update player stats and shop
			gamer.incrementTieredAmmoLevel();
			player.openInventory(Inventories.createAmmoUpgradeShopMenu(arenaInstance, gamer));

			// Find and upgrade, otherwise give
			for (int i = 1; i < 46; i++) {
				if (Ammo.matches(player
					.getInventory()
					.getItem(i))) {
					player
						.getInventory()
						.setItem(i, buy);
					PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
					return;
				}
			}
			PlayerManager.giveItem(player, buy, LanguageManager.errors.inventoryFull);
			PlayerManager.notifySuccess(player, LanguageManager.confirms.buy);
		}

		// Stats menu for a player
		else if (invID == InventoryID.PLAYER_STATS_MENU) {
			UUID id = meta.getPlayerID();
			if (buttonName.contains(LanguageManager.messages.achievements))
				player.openInventory(Inventories.createPlayerAchievementsMenu(id));
			else if (buttonName.contains(LanguageManager.messages.kits))
				player.openInventory(Inventories.createPlayerKitsMenu(id, player.getUniqueId()));
			else if (buttonName.contains(LanguageManager.messages.reset) && id.equals(player.getUniqueId()))
				player.openInventory(Inventories.createResetStatsConfirmMenu(id));
		}

		// Achievements menu for a player
		else if (invID == InventoryID.PLAYER_ACHIEVEMENTS_MENU) {
			UUID id = meta.getPlayerID();

			// Exit button
			if (buttonName.contains(LanguageManager.messages.exit))
				player.openInventory(Inventories.createPlayerStatsMenu(id, player.getUniqueId()));

				// Previous page
			else if (button.equals(InventoryButtons.previousPage()))
				player.openInventory(Inventories.createPlayerAchievementsMenu(id, meta.getPage() - 1));

				// Next page
			else if (button.equals(InventoryButtons.nextPage()))
				player.openInventory(Inventories.createPlayerAchievementsMenu(id, meta.getPage() + 1));
		}

		// Kits menu for a player
		else if (invID == InventoryID.PLAYER_KITS_MENU) {
			UUID ownerID = meta.getPlayerID();
			Kit kit = Kit.getKitByName(buttonName.substring(4));

			if (buttonName.contains(LanguageManager.messages.exit)) {
				player.openInventory(Inventories.createPlayerStatsMenu(ownerID, player.getUniqueId()));
				return;
			}

			// Check if requester is owner
			if (!ownerID.equals(player.getUniqueId()))
				return;

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError("No kit of %s was found.", CommunicationManager.DebugLevel.NORMAL,
					buttonName.substring(4)
				);
				return;
			}

			int balance = PlayerManager.getCrystalBalance(ownerID);

			// Single tier kits
			if (!kit.isMultiLevel() && !PlayerManager.hasSingleTierKit(ownerID, kit.getID())) {
				if (balance >= kit.getPrice(1)) {
					PlayerManager.withdrawCrystalBalance(ownerID, kit.getPrice(1));
					PlayerManager.addSingleTierKit(ownerID, kit.getID());
					PlayerManager.notifySuccess(player, LanguageManager.confirms.kitBuy);
					AchievementChecker.checkDefaultKitAchievements(player);
				}
				else PlayerManager.notifyFailure(player, LanguageManager.errors.kitBuy);
			}

			// Multiple tier kits
			else {
				int kitLevel = PlayerManager.getMultiTierKitLevel(ownerID, kit.getID());
				if (kitLevel == kit.getMaxLevel())
					return;
				else if (kitLevel == 0) {
					if (balance >= kit.getPrice(++kitLevel)) {
						PlayerManager.withdrawCrystalBalance(ownerID, kit.getPrice(kitLevel));
						PlayerManager.setMultiTierKitLevel(ownerID, kit.getID(), kitLevel);
						PlayerManager.notifySuccess(player, LanguageManager.confirms.kitBuy);
						AchievementChecker.checkDefaultKitAchievements(player);
					}
					else PlayerManager.notifyFailure(player, LanguageManager.errors.kitBuy);
				}
				else {
					if (balance >= kit.getPrice(++kitLevel)) {
						PlayerManager.withdrawCrystalBalance(ownerID, kit.getPrice(kitLevel));
						PlayerManager.setMultiTierKitLevel(ownerID, kit.getID(), kitLevel);
						PlayerManager.notifySuccess(player, LanguageManager.confirms.kitUpgrade);
						AchievementChecker.checkDefaultKitAchievements(player);
					}
					else PlayerManager.notifyFailure(player, LanguageManager.errors.kitUpgrade);
				}
			}

			player.openInventory(Inventories.createPlayerKitsMenu(ownerID, ownerID));
		}

		// Reset player stats confirmation for a player
		else if (invID == InventoryID.RESET_STATS_CONFIRM_MENU) {
			UUID ownerID = meta.getPlayerID();

			// Return to previous menu
			if (buttonName.contains("NO"))
				player.openInventory(Inventories.createPlayerStatsMenu(ownerID, player.getUniqueId()));

				// Reset player stats
			else if (buttonName.contains("YES")) {
				// Remove stats
				PlayerManager.resetPlayerData(player.getUniqueId());

				// Reload leaderboards
				GameController.refreshLeaderboards();

				// Confirm and return
				PlayerManager.notifySuccess(player, LanguageManager.confirms.reset);
				player.openInventory(Inventories.createPlayerStatsMenu(ownerID, player.getUniqueId()));
			}
		}

		// Kit selection menu for an arena
		else if (invID == InventoryID.SELECT_KITS_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			Kit kit = Kit.getKitByName(buttonName.substring(4));

			// Leave if EXIT
			if (buttonName.contains(LanguageManager.messages.exit)) {
				closeInv(player);
				return;
			}

			// Check if selected kit retrieval failed
			if (kit == null) {
				CommunicationManager.debugError("No kit of %s was found.", CommunicationManager.DebugLevel.NORMAL,
					buttonName.substring(4)
				);
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == VDPlayer.Status.SPECTATOR)
				return;

			// Single tier kits
			if (!kit.isMultiLevel()) {
				if (PlayerManager.hasSingleTierKit(player.getUniqueId(), kit.getID()) || kit.equals(Kit.orc()) ||
					kit.equals(Kit.farmer()) || kit.equals(Kit.none())) {
					gamer.setKit(kit.setKitLevel(1));
					PlayerManager.notifySuccess(player, LanguageManager.confirms.kitSelect);
				}
				else {
					PlayerManager.notifyFailure(player, LanguageManager.errors.kitSelect);
					return;
				}
			}

			// Multiple tier kits
			else {
				int kitLevel = PlayerManager.getMultiTierKitLevel(player.getUniqueId(), kit.getID());
				if (kitLevel < 1) {
					PlayerManager.notifyFailure(player, LanguageManager.errors.kitSelect);
					return;
				}
				gamer.setKit(kit.setKitLevel(kitLevel));
				PlayerManager.notifySuccess(player, LanguageManager.confirms.kitSelect);
			}

			// Close inventory and create scoreboard
			closeInv(player);
			SidebarManager.updateActivePlayerSidebar(gamer);
		}

		// Challenge selection menu for an arena
		else if (invID == InventoryID.SELECT_CHALLENGES_MENU) {
			Arena arenaInstance;
			VDPlayer gamer;

			// Attempt to get arena and player
			try {
				arenaInstance = GameController.getArena(player);
				gamer = arenaInstance.getPlayer(player);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}

			Challenge challenge = Challenge.getChallengeByName(buttonName.substring(4));

			// Leave if EXIT
			if (buttonName.contains(LanguageManager.messages.exit)) {
				closeInv(player);
				return;
			}

			// Ignore spectators from here on out
			if (gamer.getStatus() == VDPlayer.Status.SPECTATOR)
				return;

			// Option for no challenge
			if (Challenge
				.none()
				.equals(challenge)) {
				// Arena has forced challenges
				if (arenaInstance
					.getForcedChallengeIDs()
					.size() > 0)
					PlayerManager.notifyFailure(player, LanguageManager.errors.hasForcedChallenges);

				else {
					gamer.resetChallenges();
					PlayerManager.notifySuccess(player, LanguageManager.confirms.challengeAdd);
				}
			}

			// Remove a challenge
			else if (gamer
				.getChallenges()
				.contains(challenge)) {
				// Arena forced the challenge
				if (challenge != null && arenaInstance
					.getForcedChallengeIDs()
					.contains(challenge.getID()))
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
			SidebarManager.updateActivePlayerSidebar(gamer);
			player.openInventory(Inventories.createSelectChallengesMenu(gamer, arenaInstance));
		}

		// Stats menu for an arena
		else if (invID == InventoryID.ARENA_INFO_MENU) {
			if (buttonName.contains(LanguageManager.messages.allowedKits))
				player.openInventory(Inventories.createAllowedKitsMenu(meta.getArena(), true));

			else if (buttonName.contains(LanguageManager.messages.forcedChallenges))
				player.openInventory(Inventories.createForcedChallengesMenu(meta.getArena(), true));
		}

		// Menu for converting crystals
		else if (invID == InventoryID.CRYSTAL_CONVERT_MENU) {
			Player owner = Bukkit.getPlayer(meta.getPlayerID());
			VDPlayer gamer;

			// Try to get VDPlayer
			try {
				gamer = GameController
					.getArena(owner)
					.getPlayer(owner);
			}
			catch (ArenaNotFoundException | PlayerNotFoundException err) {
				return;
			}
			int gemBoost = gamer.getGemBoost();
			int conversionRatio;
			if (Main.hasCustomEconomy())
				conversionRatio = Math.max((int) (5 * Main.plugin
					.getConfig()
					.getDouble("vaultEconomyMult")), 1);
			else conversionRatio = 5;
			int balance = PlayerManager.getCrystalBalance(meta.getPlayerID());

			// Reset
			if (buttonName.contains(LanguageManager.messages.reset)) {
				gamer.setGemBoost(0);
			}

			// Increase by 1
			else if (buttonName.contains("+1 ")) {
				gemBoost++;

				// Check for crystal balance
				if (gemBoost * conversionRatio > balance) {
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
				if (gemBoost * conversionRatio > balance) {
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
				if (gemBoost * conversionRatio > balance) {
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
				if (gemBoost * conversionRatio > balance) {
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

	// Handles arena naming
	@EventHandler
	public void onRename(SignGUIEvent e) {
		Arena arena = e.getArena();
		Player player = e.getPlayer();

		// Try updating name
		try {
			arena.setName(e.getLines()[2]);
			CommunicationManager.debugInfo("Name changed for arena %s!", CommunicationManager.DebugLevel.VERBOSE,
				arena
					.getPath()
					.substring(1)
			);
		}
		catch (IllegalArenaNameException err) {
			if (err
				.getMessage()
				.equals("Same"))
				player.openInventory(Inventories.createArenaMenu(arena));
			else {
				if (arena.getName() == null)
					GameController.removeArena(arena.getId());
				PlayerManager.notifyFailure(player, "Invalid arena name!");
			}
			return;
		}

		player.openInventory(Inventories.createArenaMenu(arena));
	}

	/**
	 * Closes the inventory of a player without creating a ghost item artifact.
	 *
	 * @param player Player to close inventory.
	 */
	private void closeInv(Player player) {
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, player::closeInventory, 1);
	}
}
