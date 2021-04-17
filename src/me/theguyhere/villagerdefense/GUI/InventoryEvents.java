package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.Tasks;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.Arena;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryEvents implements Listener {
	private final Main plugin;
	private final Game game;
	private final Inventories inv;
	private final Portal portal;
	private final Leaderboard leaderboard;
	private final InfoBoard infoBoard;
	private final ArenaBoard arenaBoard;
	private final Utils utils;
	private int arena = 0; // Keeps track of which arena for many of the menus
	private int oldSlot = 0;
	private String old = ""; // Old name to revert name back if cancelled during naming
	private boolean close; // Safe close toggle initialized to off

	// Constants for armor types
	private final Material[] HELMETS = {Material.LEATHER_HELMET, Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET,
			Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET
	};
	private final Material[] CHESTPLATES = {Material.LEATHER_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
			Material.NETHERITE_HELMET
	};
	private final Material[] LEGGINGS = {Material.LEATHER_LEGGINGS, Material.GOLDEN_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS
	};
	private final Material[] BOOTS = {Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
	};

	public InventoryEvents (Main plugin,
			Game game,
			Inventories inv,
			Portal portal,
			Leaderboard leaderboard,
			InfoBoard infoBoard,
			ArenaBoard arenaBoard) {
		this.plugin = plugin;
		this.game = game;
		this.inv = inv;
		this.portal = portal;
		this.leaderboard = leaderboard;
		this.infoBoard = infoBoard;
		this.arenaBoard = arenaBoard;
		utils = new Utils(plugin);
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

		// Cancel the event
		e.setCancelled(true);
	}
	
	// All click events in the inventories
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
		if (button == null)
			return;

		Material buttonType = button.getType();
		String buttonName = button.getItemMeta().getDisplayName();
		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		int num = slot;
		FileConfiguration config = plugin.getArenaData();

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

			// Open info boards menu
			else if (buttonName.contains("Info Boards"))
				openInv(player, inv.createInfoBoardInventory());

			// Open leaderboards menu
			else if (buttonName.contains("Leaderboards"))
				openInv(player, inv.createLeaderboardInventory());

			// Close inventory
			else if (buttonName.contains("EXIT"))
				player.closeInventory();

			arena = slot;
			old = config.getString("a" + arena + ".name");
		}

		// Lobby menu
		else if (title.contains(Utils.format("&2&lLobby"))) {
			String path = "lobby";

			// Create lobby
			if (buttonName.contains("Create Lobby")) {
				if (!config.contains(path)) {
					utils.setConfigurationLocation(path, player.getLocation());
					game.reloadLobby();
					player.sendMessage(Utils.notify("&aLobby set!"));
				} else player.sendMessage(Utils.notify("&cLobby already exists!"));
			}

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
				if (config.contains("lobby"))
					openInv(player, inv.createLobbyConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo lobby to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Info board menu
		else if (title.contains("Info Boards")) {
			// Edit board
			if (Arrays.asList(Inventories.INFO_BOARD_MATS).contains(buttonType)) {
				openInv(player, inv.createInfoBoardMenu(slot));
				oldSlot = slot;
			}

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createArenasInventory());
			}
		}

		// Info board menu for a specific board
		else if (title.contains("Info Board ")) {
			String path = "infoBoard." + oldSlot;

			// Create board
			if (buttonName.contains("Create"))
				if (!config.contains(path)) {
					infoBoard.createInfoBoard(player, oldSlot);
					player.sendMessage(Utils.notify("&aInfo board set!"));
				} else player.sendMessage(Utils.notify("&cInfo board already exists!"));


			// Teleport player to info board
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo info board to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center info board
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo info board to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				infoBoard.refreshInfoBoard(oldSlot);
				player.sendMessage(Utils.notify("&aInfo board centered!"));
			}

			// Remove info board
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createInfoBoardConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo info board to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createInfoBoardInventory());
		}

		// Leaderboard menu
		else if (title.contains("Leaderboards")) {
			if (buttonName.contains("Total Kills Leaderboard"))
				openInv(player, inv.createTotalKillsLeaderboardInventory());

			if (buttonName.contains("Top Kills Leaderboard"))
				openInv(player, inv.createTopKillsLeaderboardInventory());

			if (buttonName.contains("Total Gems Leaderboard"))
				openInv(player, inv.createTotalGemsLeaderboardInventory());

			if (buttonName.contains("Top Balance Leaderboard"))
				openInv(player, inv.createTopBalanceLeaderboardInventory());

			if (buttonName.contains("Top Wave Leaderboard"))
				openInv(player, inv.createTopWaveLeaderboardInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Total kills leaderboard menu
		else if (title.contains(Utils.format("&4&lTotal Kills Leaderboard"))) {
			String path = "leaderboard.totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				if (!config.contains(path)) {
					leaderboard.createLeaderboard(player, "totalKills");
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				leaderboard.refreshLeaderboard("totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createTotalKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createLeaderboardInventory());
		}

		// Top kills leaderboard menu
		else if (title.contains(Utils.format("&c&lTop Kills Leaderboard"))) {
			String path = "leaderboard.topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				if (!config.contains(path)) {
					leaderboard.createLeaderboard(player, "topKills");
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				leaderboard.refreshLeaderboard("topKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createTopKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createLeaderboardInventory());
		}

		// Total gems leaderboard menu
		else if (title.contains(Utils.format("&2&lTotal Gems Leaderboard"))) {
			String path = "leaderboard.totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				if (!config.contains(path)) {
					leaderboard.createLeaderboard(player, "totalGems");
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				leaderboard.refreshLeaderboard("totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createTotalGemsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createLeaderboardInventory());
		}

		// Top balance leaderboard menu
		else if (title.contains(Utils.format("&a&lTop Balance Leaderboard"))) {
			String path = "leaderboard.topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				if (!config.contains(path)) {
					leaderboard.createLeaderboard(player, "topBalance");
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				leaderboard.refreshLeaderboard("topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createTopBalanceConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createLeaderboardInventory());
		}

		// Top wave leaderboard menu
		else if (title.contains(Utils.format("&9&lTop Wave Leaderboard"))) {
			String path = "leaderboard.topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				if (!config.contains(path)) {
					leaderboard.createLeaderboard(player, "topWave");
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path);
				leaderboard.refreshLeaderboard("topWave");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					openInv(player, inv.createTopWaveConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createLeaderboardInventory());
		}

		// Naming inventory
		else if (title.contains("Arena ")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Get name of arena
			String name = config.getString("a" + arena + ".name");

			// If no name exists, set to nothing
			if (name == null)
				name = "";

			// Check for caps lock toggle
			boolean caps = arenaInstance.isCaps();
			if (caps)
				num += 36;

			// Letters and numbers
			if (Arrays.asList(Inventories.KEY_MATS).contains(buttonType)){
				config.set("a" + arena + ".name", name + Inventories.NAMES[num]);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createNamingInventory(arena));
			}

			// Spaces
			else if (buttonName.contains("Space")){
				config.set("a" + arena + ".name", name + Inventories.NAMES[72]);
				plugin.saveArenaData();
				arenaInstance.updateArena();
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
				config.set("a" + arena + ".name", name.substring(0, name.length() - 1));
				plugin.saveArenaData();
				arenaInstance.updateArena();
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
				old = config.getString("a" + arena + ".name");
				arenaInstance.setName(old);

				// Recreate portal if it exists
				if (config.contains("portal." + arena))
					portal.refreshHolo(arena, game);

				// Set default max players to 12 if it doesn't exist
				if (!config.contains("a" + arena + ".max"))
					config.set("a" + arena + ".max", 12);

				// Set default min players to 1 if it doesn't exist
				if (!config.contains("a" + arena + ".min"))
					config.set("a" + arena + ".min", 1);

				// Set default spawn table to default
				if (!config.contains("a" + arena + ".spawnTable"))
					config.set("a" + arena + ".spawnTable", "default");

				// Set default max waves to -1 if it doesn't exist
				if (!config.contains("a" + arena + ".maxWaves"))
					config.set("a" + arena + ".maxWaves", -1);

				// Set default wave time limit to -1 if it doesn't exist
				if (!config.contains("a" + arena + ".waveTimeLimit"))
					config.set("a" + arena + ".waveTimeLimit", -1);

				// Set default difficulty multiplier to 1 if it doesn't exist
				if (!config.contains("a" + arena + ".difficulty"))
					config.set("a" + arena + ".difficulty", 1);

				// Set default to closed if arena closed doesn't exist
				if (!config.contains("a" + arena + ".closed"))
					config.set("a" + arena + ".closed", true);

				plugin.saveArenaData();
				arenaInstance.updateArena();
				arenaInstance.updateArena();
			}

			// Cancel
			else if (buttonName.contains("CANCEL")) {
				config.set("a" + arena + ".name", old);
				plugin.saveArenaData();
				if (old == null)
					game.arenas.set(arena, null);
				openInv(player, inv.createArenasInventory());
			}
		}

		// Menu for an arena
		else if (title.contains(Utils.format("&2&lEdit "))) {
			Arena arenaInstance = game.arenas.get(arena);

			// Open name editor
			if (buttonName.contains("Edit Name")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createNamingInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Open portal menu
			else if (buttonName.contains("Portal and Leaderboard"))
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
				if (config.getBoolean("a" + arena + ".closed")) {
					// No lobby
					if (!config.contains("lobby")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a lobby!"));
						return;
					}

					// No arena portal
					if (!config.contains("portal." + arena)) {
						player.sendMessage(Utils.notify("&cArena cannot open without a portal!"));
						return;
					}

					// No player spawn
					if (!config.contains("a" + arena + ".spawn")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a player spawn!"));
						return;
					}

					// No monster spawn
					if (!config.contains("a" + arena + ".monster")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a monster spawn!"));
						return;
					}

					// No villager spawn
					if (!config.contains("a" + arena + ".villager")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a villager spawn!"));
						return;
					}

					// Open arena
					config.set("a" + arena + ".closed", false);
				}

				// Arena currently open
				else {
					// Set to closed
					config.set("a" + arena + ".closed", true);

					// Kick players
					arenaInstance.getPlayers().forEach(vdPlayer ->
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
									Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));
				}

				// Save perm data and update portal
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createArenaInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createArenaConfirmInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				String path = "portal." + arena;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove portal data, close arena
					config.set(path, null);
					config.set("a" + arena + ".closed", true);
					plugin.saveArenaData();
					arenaInstance.updateArena();

					// Remove portal
					portal.removePortalAll(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aPortal removed!"));
					openInv(player, inv.createPortalInventory(arena));
				}
			}

			// Confirm to remove leaderboard
			if (title.contains("Remove Leaderboard?")) {
				String path = "arenaBoard." + arena;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data, close arena
					config.set(path, null);
					plugin.saveArenaData();
					arenaInstance.updateArena();

					// Remove Portal
					arenaBoard.removeArenaBoard(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createPortalInventory(arena));
				}
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				String path = "a" + arena + ".spawn";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPlayerSpawnInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set(path, null);
					config.set("a" + arena + ".closed", true);
					plugin.saveArenaData();
					arenaInstance.updateArena();
					player.sendMessage(Utils.notify("&aSpawn removed!"));
					openInv(player, inv.createPlayerSpawnInventory(arena));
					portal.refreshHolo(arena, game);
				}
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				String path = "a" + arena + ".waiting";
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createWaitingRoomInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set(path, null);
					plugin.saveArenaData();
					arenaInstance.updateArena();
					player.sendMessage(Utils.notify("&aWaiting room removed!"));
					openInv(player, inv.createWaitingRoomInventory(arena));
					portal.refreshHolo(arena, game);
				}
			}

			// Confirm to remove monster spawn
			else if (title.contains("Remove Monster Spawn?")) {
				String path = "a" + arena + ".monster." + oldSlot;
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));

				// Remove the monster spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set(path, null);
					if (!config.contains("a" + arena + ".monster"))
						config.set("a" + arena + ".closed", true);
					plugin.saveArenaData();
					arenaInstance.updateArena();
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));
					portal.refreshHolo(arena, game);
				}
			}

			// Confirm to remove villager spawn
			else if (title.contains("Remove Villager Spawn?")) {
				String path = "a" + arena + ".villager." + oldSlot;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createVillagerSpawnMenu(arena, oldSlot));

				// Remove the villager spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set(path, null);
					if (!config.contains("a" + arena + ".villager"))
						config.set("a" + arena + ".closed", true);
					plugin.saveArenaData();
					arenaInstance.updateArena();
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
					openInv(player, inv.createVillagerSpawnMenu(arena, oldSlot));
					portal.refreshHolo(arena, game);
				}
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createLobbyInventory());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set("lobby", null);
					plugin.saveArenaData();
					game.reloadLobby();
					player.sendMessage(Utils.notify("&aLobby removed!"));
					openInv(player, inv.createLobbyInventory());
				}
			}

			// Confirm to remove info board
			else if (title.contains("Remove Info Board?")) {
				String path = "infoBoard." + oldSlot;

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createInfoBoardMenu(oldSlot));

				// Remove the info board, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove info board data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove info board
					infoBoard.removeInfoBoard(oldSlot);

					// Confirm and return
					player.sendMessage(Utils.notify("&aInfo board removed!"));
					openInv(player, inv.createInfoBoardMenu(oldSlot));
				}
			}

			// Confirm to remove total kills leaderboard
			else if (title.contains("Remove Total Kills Leaderboard?")) {
				String path = "leaderboard.totalKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createTotalKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("totalKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createTotalKillsLeaderboardInventory());
				}
			}

			// Confirm to remove top kills leaderboard
			else if (title.contains("Remove Top Kills Leaderboard?")) {
				String path = "leaderboard.topKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createTopKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createTopKillsLeaderboardInventory());
				}
			}

			// Confirm to remove total gems leaderboard
			else if (title.contains("Remove Total Gems Leaderboard?")) {
				String path = "leaderboard.totalGems";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createTotalGemsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("totalGems");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createTotalGemsLeaderboardInventory());
				}
			}

			// Confirm to remove top balance leaderboard
			else if (title.contains("Remove Top Balance Leaderboard?")) {
				String path = "leaderboard.topBalance";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createTopBalanceLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topBalance");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createTopBalanceLeaderboardInventory());
				}
			}

			// Confirm to remove top wave leaderboard
			else if (title.contains("Remove Top Wave Leaderboard?")) {
				String path = "leaderboard.topWave";

				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createTopWaveLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topWave");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createTopWaveLeaderboardInventory());
				}
			}

			// Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createArenaInventory(arena));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove data
					config.set("a" + arena, null);
					config.set("portal." + arena, null);
					plugin.saveArenaData();
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

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			String path = "portal." + arena;
			String path2 = "arenaBoard." + arena;
			Arena arenaInstance = game.arenas.get(arena);

			// Create portal
			if (buttonName.contains("Create Portal")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (!config.contains(path)) {
						portal.createPortal(player, arena, game);
						player.sendMessage(Utils.notify("&aPortal set!"));
					} else player.sendMessage(Utils.notify("&cPortal already exists!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Teleport player to portal
			else if (buttonName.contains("Teleport to Portal")) {
				Location location = utils.getConfigLocationNoRotation(path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo portal to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center portal
			else if (buttonName.contains("Center Portal")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (utils.getConfigLocationNoPitch(path) == null) {
						player.sendMessage(Utils.notify("&cNo portal to center!"));
						return;
					}
					utils.centerConfigLocation(path);
					portal.removePortalAll(arena);
					portal.loadPortal(utils.getConfigLocationNoPitch(path), arena, game);
					player.sendMessage(Utils.notify("&aPortal centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove portal
			else if (buttonName.contains("REMOVE PORTAL"))
				if (config.contains(path))
					if (config.getBoolean("a" + arena + ".closed"))
						openInv(player, inv.createPortalConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo portal to remove!"));

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				if (!config.contains(path2)) {
					arenaBoard.createArenaBoard(player, arenaInstance);
					player.sendMessage(Utils.notify("&aLeaderboard set!"));
				} else player.sendMessage(Utils.notify("&cLeaderboard already exists!"));
			}

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport to Leaderboard")) {
				Location location = utils.getConfigLocationNoRotation(path2);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to teleport to!"));
					return;
				}
				player.teleport(location);
				player.closeInventory();
			}

			// Center leaderboard
			else if (buttonName.contains("Center Leaderboard")) {
				if (utils.getConfigLocationNoPitch(path2) == null) {
					player.sendMessage(Utils.notify("&cNo leaderboard to center!"));
					return;
				}
				utils.centerConfigLocation(path2);
				arenaBoard.refreshArenaBoard(arena);
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE LEADERBOARD"))
				if (config.contains(path2))
					openInv(player, inv.createArenaBoardConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

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
			else if (buttonName.contains("Maximum")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createMaxPlayerInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Edit min players
			else if (buttonName.contains("Minimum")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createMinPlayerInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			String path = "a" + arena + ".spawn";

			// Create spawn
			if (buttonName.contains("Create"))
				if (config.getBoolean("a" + arena + ".closed"))
					if (!config.contains(path)) {
						utils.setConfigurationLocation(path, player.getLocation());
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aSpawn set!"));
					} else player.sendMessage(Utils.notify("&cPlayer spawn already exists!"));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

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
				if (config.getBoolean("a" + arena + ".closed")) {
					if (utils.getConfigLocationNoRotation(path) == null) {
						player.sendMessage(Utils.notify("&cNo player spawn to center!"));
						return;
					}
					utils.centerConfigLocation(path);
					player.sendMessage(Utils.notify("&aSpawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					if (config.getBoolean("a" + arena + ".closed"))
						openInv(player, inv.createSpawnConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo player spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			String path = "a" + arena + ".waiting";

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (!config.contains(path)) {
						utils.setConfigurationLocation(path, player.getLocation());
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aWaiting room set!"));
					} else player.sendMessage(Utils.notify("&cWaiting room already exists!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

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
			else if (buttonName.contains("Center"))
				if (config.getBoolean("a" + arena + ".closed")) {
					if (utils.getConfigLocationNoRotation(path) == null) {
						player.sendMessage(Utils.notify("&cNo waiting room to center!"));
						return;
					}
					utils.centerConfigLocation(path);
					player.sendMessage(Utils.notify("&aWaiting room centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Remove waiting room
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					if (config.getBoolean("a" + arena + ".closed"))
						openInv(player, inv.createWaitingConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo waiting room to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			int current = config.getInt("a" + arena + ".max");
			Arena arenaInstance = game.arenas.get(arena);

			// Decrease max players
			if (buttonName.contains("Decrease")) {
				// Check if max players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than 1!"));
					return;
				}

				// Check if max players is greater than min players
				if (current <= config.getInt("a" + arena + ".min")) {
					player.sendMessage(Utils.notify("&cMax players cannot be less than min player!"));
					return;
				}

				config.set("a" + arena + ".max", current - 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				config.set("a" + arena + ".max", current + 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8) {
				openInv(player, inv.createPlayersInventory(arena));
			}
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			int current = config.getInt("a" + arena + ".min");
			Arena arenaInstance = game.arenas.get(arena);

			// Decrease min players
			if (buttonName.contains("Decrease")) {
				// Check if min players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMin players cannot be less than 1!"));
					return;
				}

				config.set("a" + arena + ".min", current - 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMinPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase min players
			else if (buttonName.contains("Increase")) {
				// Check if min players is less than max players
				if (current >= config.getInt("a" + arena + ".max")) {
					player.sendMessage(Utils.notify("&cMin players cannot be greater than max player!"));
					return;
				}

				config.set("a" + arena + ".min", current + 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
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
			Arena arenaInstance = game.arenas.get(arena);

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

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createSpawnTableInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (config.getBoolean("a" + arena + ".closed")) {
					config.set("a" + arena +".dynamicCount",
							!config.getBoolean("a" + arena +".dynamicCount"));
					plugin.saveArenaData();
					arenaInstance.updateArena();
					openInv(player, inv.createMobsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (config.getBoolean("a" + arena + ".closed")) {
					config.set("a" + arena +".dynamicDifficulty",
							!config.getBoolean("a" + arena +".dynamicDifficulty"));
					plugin.saveArenaData();
					arenaInstance.updateArena();
					openInv(player, inv.createMobsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
			// Edit spawn
			if (Arrays.asList(Inventories.MONSTER_MATS).contains(buttonType)) {
				openInv(player, inv.createMonsterSpawnMenu(arena, slot));
				oldSlot = slot;
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createMobsInventory(arena));
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			String path = "a" + arena + ".monster." + oldSlot;

			// Create spawn
			if (buttonName.contains("Create Spawn")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (!config.contains(path)) {
						utils.setConfigurationLocation(path, player.getLocation());
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aMonster spawn set!"));
					} else player.sendMessage(Utils.notify("&cMonster spawn already exists!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

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

			// Center monster spawn
			else if (buttonName.contains("Center")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (utils.getConfigLocationNoRotation(path) == null) {
						player.sendMessage(Utils.notify("&cNo monster spawn to center!"));
						return;
					}
					utils.centerConfigLocation(path);
					player.sendMessage(Utils.notify("&aMonster spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					if (config.getBoolean("a" + arena + ".closed"))
						openInv(player, inv.createMonsterSpawnConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo monster spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createMonsterSpawnInventory(arena));
		}

		// Villager spawn menu for an arena
		else if (title.contains("Villager Spawns:")) {
			// Edit spawn
			if (Arrays.asList(Inventories.VILLAGER_MATS).contains(buttonType)) {
				openInv(player, inv.createVillagerSpawnMenu(arena, slot));
				oldSlot = slot;
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createMobsInventory(arena));
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			String path = "a" + arena + ".villager." + oldSlot;

			// Create spawn
			if (buttonName.contains("Create Spawn")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					if (!config.contains(path)) {
						utils.setConfigurationLocation(path, player.getLocation());
						game.arenas.get(arena).updateArena();
						player.sendMessage(Utils.notify("&aVillager spawn set!"));
					} else player.sendMessage(Utils.notify("&cVillager spawn already exists!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

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
				if (config.getBoolean("a" + arena + ".closed")) {
					if (utils.getConfigLocationNoRotation(path) == null) {
						player.sendMessage(Utils.notify("&cNo villager spawn to center!"));
						return;
					}
					utils.centerConfigLocation(path);
					player.sendMessage(Utils.notify("&aVillager spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					if (config.getBoolean("a" + arena + ".closed"))
						openInv(player, inv.createVillagerSpawnConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo villager spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createVillagerSpawnInventory(arena));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {

			// Default
			if (buttonName.contains("Default"))
				if (new File(plugin.getDataFolder() + "/spawnTables/default.yml").exists())
					config.set("a" + arena + ".spawnTable", "default");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 1
			else if (buttonName.contains("Option 1"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option1.yml").exists())
					config.set("a" + arena + ".spawnTable", "option1");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 2
			else if (buttonName.contains("Option 2"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option2.yml").exists())
					config.set("a" + arena + ".spawnTable", "option2");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 3
			else if (buttonName.contains("Option 3"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option3.yml").exists())
					config.set("a" + arena + ".spawnTable", "option3");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 4
			else if (buttonName.contains("Option 4"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option4.yml").exists())
					config.set("a" + arena + ".spawnTable", "option4");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 5
			else if (buttonName.contains("Option 5"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option5.yml").exists())
					config.set("a" + arena + ".spawnTable", "option5");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 6
			else if (buttonName.contains("Option 6"))
				if (new File(plugin.getDataFolder() + "/spawnTables/option6.yml").exists())
					config.set("a" + arena + ".spawnTable", "option6");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Custom
			else if (buttonName.contains("Custom"))
				if (new File(plugin.getDataFolder() + "/spawnTables/a" + arena + ".yml").exists())
					config.set("a" + arena + ".spawnTable", "custom");
				else player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createMobsInventory(arena));
				return;
			}

			// Save data and reload inventory
			plugin.saveArenaData();
			game.arenas.get(arena).updateArena();
			openInv(player, inv.createSpawnTableInventory(arena));
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
//			else if (buttonName.contains("Dynamic Prices:"))
			if (buttonName.contains("Dynamic Prices:")) {
				if (config.getBoolean("a" + arena + ".closed")) {
					config.set("a" + arena + ".dynamicPrices",
							!config.getBoolean("a" + arena + ".dynamicPrices"));
					plugin.saveArenaData();
					game.arenas.get(arena).updateArena();
					openInv(player, inv.createShopsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			// Change max waves
			if (buttonName.contains("Max Waves")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createMaxWaveInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit")) {
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createWaveTimeLimitInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (config.getBoolean("a" + arena + ".closed")) {
					config.set("a" + arena +".dynamicLimit",
							!config.getBoolean("a" + arena +".dynamicLimit"));
					plugin.saveArenaData();
					game.arenas.get(arena).updateArena();
					openInv(player, inv.createGameSettingsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit allowed kits
//			else if (buttonName.contains("Allowed Kits"))

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createDifficultyLabelInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createSoundsInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createDifficultyMultiplierInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (config.getBoolean("a" + arena + ".closed"))
					openInv(player, inv.createCopySettingsInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			String path = "a" + arena + ".maxWaves";
			int current = config.getInt(path);
			Arena arenaInstance = game.arenas.get(arena);

			// Decrease max waves
			if (buttonName.contains("Decrease")) {
				// Check if max waves is unlimited
				if (current == -1)
					config.set(path, 1);

				// Check if max waves is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax waves cannot be less than 1!"));
					return;
				} else config.set(path, current - 1);

				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				config.set(path, -1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				config.set(path, 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max waves
			else if (buttonName.contains("Increase")) {
				// Check if max waves is unlimited
				if (current == -1)
					config.set(path, 1);
				else config.set(path, current + 1);

				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8)
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			String path = "a" + arena + ".waveTimeLimit";
			int current = config.getInt(path);
			Arena arenaInstance = game.arenas.get(arena);

			// Decrease wave time limit
			if (buttonName.contains("Decrease")) {
				// Check if wave time limit is unlimited
				if (current == -1)
					config.set(path, 1);

				// Check if wave time limit is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cWave time limit cannot be less than 1!"));
					return;
				} else config.set(path, current - 1);

				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				config.set(path, -1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				config.set(path, 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase wave time limit
			else if (buttonName.contains("Increase")) {
				// Check if wave time limit is unlimited
				if (current == -1)
					config.set(path, 1);
				else config.set(path, current + 1);

				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8)
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			String path = "a" + arena + ".difficultyLabel";
			Arena arenaInstance = game.arenas.get(arena);

			// Set to Easy
			if (buttonName.contains("Easy")) {
				config.set(path, "Easy");
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				config.set(path, "Medium");
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				config.set(path, "Hard");
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				config.set(path, "Insane");
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				config.set(path, null);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8)
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			String path = "a" + arena + ".difficulty";
			Arena arenaInstance = game.arenas.get(arena);

			// Set to 1
			if (buttonName.contains("1")) {
				config.set(path, 1);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to 2
			if (buttonName.contains("2")) {
				config.set(path, 2);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to 3
			if (buttonName.contains("3")) {
				config.set(path, 3);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to 4
			if (buttonName.contains("4")) {
				config.set(path, 4);
				plugin.saveArenaData();
				arenaInstance.updateArena();
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (slot == 8)
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Menu to copy game settings
		if (title.contains("Copy Game Settings")) {
			String path = "a" + arena;
			String path2 = "a" + slot;

			// Copy settings from another arena
			if (buttonType == Material.WHITE_CONCRETE) {
				config.set(path + ".max", config.getInt(path2 + ".max"));
				config.set(path + ".min", config.getInt(path2 + ".min"));
				config.set(path + ".maxWaves", config.getInt(path2 + ".maxWaves"));
				config.set(path + ".waveTimeLimit", config.getInt(path2 + ".waveTimeLimit"));
				config.set(path + ".difficulty", config.getInt(path2 + ".difficulty"));
				config.set(path + ".dynamicCount", config.getBoolean(path2 + ".dynamicCount"));
				config.set(path + ".dynamicDifficulty", config.getBoolean(path2 + ".dynamicDifficulty"));
				config.set(path + ".dynamicLimit", config.getBoolean(path2 + ".dynamicLimit"));
				config.set(path + ".dynamicPrices", config.getBoolean(path2 + ".dynamicPrices"));
				config.set(path + ".difficultyLabel", config.getString(path2 + ".difficultyLabel"));
			}

			// Copy easy preset
			else if (buttonName.contains("Easy Preset")) {
				config.set(path + ".maxWaves", 80);
				config.set(path + ".waveTimeLimit", 5);
				config.set(path + ".difficulty", 1);
				config.set(path + ".dynamicCount", false);
				config.set(path + ".dynamicDifficulty", false);
				config.set(path + ".dynamicLimit", true);
				config.set(path + ".dynamicPrices", false);
				config.set(path + ".difficultyLabel", "Easy");
			}

			// Copy medium preset
			else if (buttonName.contains("Medium Preset")) {
				config.set(path + ".maxWaves", 70);
				config.set(path + ".waveTimeLimit", 4);
				config.set(path + ".difficulty", 2);
				config.set(path + ".dynamicCount", false);
				config.set(path + ".dynamicDifficulty", false);
				config.set(path + ".dynamicLimit", true);
				config.set(path + ".dynamicPrices", true);
				config.set(path + ".difficultyLabel", "Medium");
			}

			// Copy hard preset
			else if (buttonName.contains("Hard Preset")) {
				config.set(path + ".maxWaves", 60);
				config.set(path + ".waveTimeLimit", 3);
				config.set(path + ".difficulty", 3);
				config.set(path + ".dynamicCount", true);
				config.set(path + ".dynamicDifficulty", true);
				config.set(path + ".dynamicLimit", true);
				config.set(path + ".dynamicPrices", true);
				config.set(path + ".difficultyLabel", "Hard");
			}

			// Copy insane preset
			else if (buttonName.contains("Insane Preset")) {
				config.set(path + ".maxWaves", 50);
				config.set(path + ".waveTimeLimit", 3);
				config.set(path + ".difficulty", 4);
				config.set(path + ".dynamicCount", true);
				config.set(path + ".dynamicDifficulty", true);
				config.set(path + ".dynamicLimit", false);
				config.set(path + ".dynamicPrices", true);
				config.set(path + ".difficultyLabel", "Insane");
			}

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createGameSettingsInventory(arena));
				return;
			}

			// Save updates
			plugin.saveArenaData();
			game.arenas.get(arena).updateArena();
			portal.refreshHolo(arena, game);
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			// Edit win sound
//			if (buttonName.contains("Win"))

			// Edit lose sound
//			else if (buttonName.contains("Lose"))

			// Edit wave start sound
//			else if (buttonName.contains("Start"))

			// Edit wave finish sound
//			else if (buttonName.contains("Finish"))

			// Edit waiting music
//			else if (buttonName.contains("Waiting"))

			// Exit menu
//			else if (buttonName.contains("EXIT"))
			if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// In-game item shop menu
		else if (title.contains("Item Shop")) {
			// See if the player is in a game
			if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
				return;

			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);

			// Open weapon shop
			if (buttonName.contains("Weapon Shop"))
				openInv(player, arenaInstance.getWeaponShop());

			// Open armor shop
			else if (buttonName.contains("Armor Shop"))
				openInv(player, arenaInstance.getArmorShop());

			// Open consumables shop
			else if (buttonName.contains("Consumables Shop"))
				openInv(player, arenaInstance.getConsumeShop());

			// Open custom shop
//			else if (buttonName.contains("Custom Shop"))

		}

		// In-game shops
		else if (title.contains("Weapon Shop") || title.contains("Armor Shop") || title.contains("Consumables Shop") ||
				title.contains("Custom Shop")) {
			// See if the player is in a game
			if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player)))
				return;

			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
					.collect(Collectors.toList()).get(0);
			VDPlayer gamer = arenaInstance.getPlayer(player);

			// Return to main shop menu
			if (buttonName.contains("RETURN")) {
				player.openInventory(Inventories.createShop(arenaInstance.getCurrentWave() / 10 + 1));
				return;
			}

			ItemStack buy = e.getClickedInventory().getItem(e.getSlot()).clone();
			Material buyType = buy.getType();
			List<String> lore = buy.getItemMeta().getLore();
			int cost = Integer.parseInt(lore.get(lore.size() - 1).substring(10));

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				player.sendMessage(Utils.notify("&cYou can't afford this item!"));
				return;
			}

			// Remove cost meta
			buy = Utils.removeLastLore(buy);

			// Subtract from balance, update scoreboard, give item
			gamer.addGems(-cost);
			game.createBoard(gamer);

			EntityEquipment equipment = player.getPlayer().getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(HELMETS).anyMatch(mat -> mat == buyType) && equipment.getHelmet() == null) {
				equipment.setHelmet(buy);
				player.sendMessage(Utils.notify("&aHelmet equipped!"));
			} else if (Arrays.stream(CHESTPLATES).anyMatch(mat -> mat == buyType) &&
					equipment.getChestplate() == null) {
				equipment.setChestplate(buy);
				player.sendMessage(Utils.notify("&aChestplate equipped!"));
			} else if (Arrays.stream(LEGGINGS).anyMatch(mat -> mat == buyType) &&
					equipment.getLeggings() == null) {
				equipment.setLeggings(buy);
				player.sendMessage(Utils.notify("&aLeggings equipped!"));
			} else if (Arrays.stream(BOOTS).anyMatch(mat -> mat == buyType) && equipment.getBoots() == null) {
				equipment.setBoots(buy);
				player.sendMessage(Utils.notify("&aBoots equipped!"));
			} else {
				Utils.giveItem(player, buy);
				player.sendMessage(Utils.notify("&aItem purchased!"));
			}
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
			plugin.getArenaData().set("a" + arena + ".name", old);
			plugin.saveArenaData();
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
