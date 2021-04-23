package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.InfoBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.*;
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
	private final Kits kits = new Kits();
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

		// Get material and name of button
		Material buttonType;
		String buttonName;
		if (button == null) {
			buttonType = null;
			buttonName = "";
		} else {
			buttonType = button.getType();
		 	buttonName = button.getItemMeta().getDisplayName();
		}

		Player player = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		int num = slot;
		FileConfiguration config = plugin.getArenaData();
		FileConfiguration language = plugin.getLanguageData();

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
					Utils.setConfigurationLocation(plugin, path, player.getLocation());
					game.reloadLobby();
					player.sendMessage(Utils.notify("&aLobby set!"));
				} else player.sendMessage(Utils.notify("&cLobby already exists!"));
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
				Location location = Utils.getConfigLocationNoRotation(plugin, path);
				if (location == null) {
					player.sendMessage(Utils.notify("&cNo info board to center!"));
					return;
				}
				Utils.centerConfigLocation(plugin, path);
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
				Utils.centerConfigLocation(plugin, path);
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
				Utils.centerConfigLocation(plugin, path);
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
				Utils.centerConfigLocation(plugin, path);
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
				Utils.centerConfigLocation(plugin, path);
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
				Utils.centerConfigLocation(plugin, path);
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
			String name = arenaInstance.getName();

			// If no name exists, set to nothing
			if (name == null)
				name = "";

			// Check for caps lock toggle
			boolean caps = arenaInstance.isCaps();
			if (caps)
				num += 36;

			// Letters and numbers
			if (Arrays.asList(Inventories.KEY_MATS).contains(buttonType)){
				arenaInstance.setName(name + Inventories.NAMES[num]);
				openInv(player, inv.createNamingInventory(arena));
			}

			// Spaces
			else if (buttonName.contains("Space")){
				arenaInstance.setName(name + Inventories.NAMES[72]);
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
				arenaInstance.setName(name.substring(0, name.length() - 1));
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
				old = arenaInstance.getName();

				// Set default max players to 12 if it doesn't exist
				if (arenaInstance.getMaxPlayers() == 0)
					arenaInstance.setMaxPlayers(12);

				// Set default min players to 1 if it doesn't exist
				if (arenaInstance.getMinPlayers() == 0)
					arenaInstance.setMinPlayers(1);

				// Set default spawn table to default
				if (arenaInstance.getSpawnTableFile() == null)
					arenaInstance.setSpawnTableFile("default");

				// Set default max waves to -1 if it doesn't exist
				if (arenaInstance.getMaxWaves() == 0)
					arenaInstance.setMaxWaves(-1);

				// Set default wave time limit to -1 if it doesn't exist
				if (arenaInstance.getWaveTimeLimit() == 0)
					arenaInstance.setWaveTimeLimit(-1);

				// Set default difficulty multiplier to 1 if it doesn't exist
				if (arenaInstance.getDifficultyMultiplier() == 0)
					arenaInstance.setDifficultyMultiplier(1);

				// Set default to closed if arena closed doesn't exist
				if (!config.contains("a" + arena + ".closed"))
					arenaInstance.setClosed(true);

				// Set default sound options
				if (!config.contains("a" + arena + ".sounds")) {
					arenaInstance.setWinSound(true);
					arenaInstance.setLoseSound(true);
					arenaInstance.setWaveStartSound(true);
					arenaInstance.setWaveFinishSound(true);
					arenaInstance.setGemSound(true);
					arenaInstance.setPlayerDeathSound(true);
					arenaInstance.setWaitingSound(14);
				}

				// Set default shop toggle
				if (!config.contains("a" + arena + ".normal"))
					arenaInstance.setNormal(true);

				// Set default particle toggles
				if (!config.contains("a" + arena + ".particles.spawn"))
					arenaInstance.setSpawnParticles(true);
				if (!config.contains("a" + arena + ".particles.monster"))
					arenaInstance.setMonsterParticles(true);
				if (!config.contains("a" + arena + ".particles.villager"))
					arenaInstance.setVillagerParticles(true);

				// Recreate portal if it exists
				if (arenaInstance.getPortal() != null)
					portal.refreshHolo(arena, game);
			}

			// Cancel
			else if (buttonName.contains("CANCEL")) {
				arenaInstance.setName(old);
				if (old == null)
					game.arenas.set(arena, null);
				openInv(player, inv.createArenasInventory());
			}
		}

		// Menu for an arena
		else if (title.contains(Utils.format("&2&lEdit "))) {
			Arena arenaInstance = game.arenas.get(arena);

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createNamingInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

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
				if (arenaInstance.isClosed()) {
					// No lobby
					if (!config.contains("lobby")) {
						player.sendMessage(Utils.notify("&cArena cannot open without a lobby!"));
						return;
					}

					// No arena portal
					if (arenaInstance.getPortal() == null) {
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

					// Open arena
					arenaInstance.setClosed(false);
				}

				// Arena currently open
				else {
					// Set to closed
					arenaInstance.setClosed(true);

					// Kick players
					arenaInstance.getPlayers().forEach(vdPlayer ->
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
									Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(vdPlayer.getPlayer()))));
				}

				// Save perm data and update portal
				openInv(player, inv.createArenaInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createArenaConfirmInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenasInventory());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove portal data, close arena
					arenaInstance.setPortal(null);
					arenaInstance.setClosed(true);

					// Remove portal
					portal.removePortalAll(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aPortal removed!"));
					openInv(player, inv.createPortalInventory(arena));
				}
			}

			// Confirm to remove leaderboard
			if (title.contains("Remove Leaderboard?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPortalInventory(arena));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data, close arena
					arenaInstance.setArenaBoard(null);

					// Remove Portal
					arenaBoard.removeArenaBoard(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					openInv(player, inv.createPortalInventory(arena));
				}
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createPlayerSpawnInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					arenaInstance.setPlayerSpawn(null);
					arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aSpawn removed!"));
					openInv(player, inv.createPlayerSpawnInventory(arena));
					portal.refreshHolo(arena, game);
				}
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					openInv(player, inv.createWaitingRoomInventory(arena));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					arenaInstance.setWaitingRoom(null);
					player.sendMessage(Utils.notify("&aWaiting room removed!"));
					openInv(player, inv.createWaitingRoomInventory(arena));
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
					arenaInstance.setMonsterSpawn(oldSlot, null);
					if (arenaInstance.getMonsterSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
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
					arenaInstance.setVillagerSpawn(oldSlot, null);
					if (arenaInstance.getVillagerSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
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
					arenaInstance.remove();
					game.arenas.set(arena, null);

					// Remove displays
					portal.removePortalAll(arena);
					arenaBoard.removeArenaBoard(arena);

					// Confirm and return
					player.sendMessage(Utils.notify("&aArena removed!"));
					openInv(player, inv.createArenasInventory());
					close = true;
				}
			}
		}

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			String path2 = "arenaBoard." + arena;
			Arena arenaInstance = game.arenas.get(arena);

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					portal.createPortal(player, arena, game);
					player.sendMessage(Utils.notify("&aPortal set!"));
					openInv(player, inv.createPortalInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			
			// Relocate portal
			if (buttonName.contains("Relocate Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					portal.refreshPortal(arena, game);
					player.sendMessage(Utils.notify("&aPortal relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to portal
			else if (buttonName.contains("Teleport to Portal")) {
				Location location = arenaInstance.getPortal();
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
					portal.refreshPortal(arena, game);
					player.sendMessage(Utils.notify("&aPortal centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove portal
			else if (buttonName.contains("REMOVE PORTAL"))
				if (arenaInstance.getPortal() != null)
					if (arenaInstance.isClosed())
						openInv(player, inv.createPortalConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo portal to remove!"));

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaBoard.createArenaBoard(player, arenaInstance);
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				openInv(player, inv.createPortalInventory(arena));
			}

			// Relocate portal
			if (buttonName.contains("Relocate Leaderboard"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setArenaBoard(player.getLocation());
					arenaBoard.refreshArenaBoard(arena);
					player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to leaderboard
			else if (buttonName.contains("Teleport to Leaderboard")) {
				Location location = arenaInstance.getArenaBoard();
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
				arenaBoard.refreshArenaBoard(arena);
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE LEADERBOARD"))
				if (arenaInstance.getArenaBoard() != null)
					openInv(player, inv.createArenaBoardConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				openInv(player, inv.createPlayerSpawnInventory(arena));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					openInv(player, inv.createPlayersInventory(arena));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				openInv(player, inv.createWaitingRoomInventory(arena));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createMaxPlayerInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createMinPlayerInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					player.sendMessage(Utils.notify("&aSpawn set!"));
					openInv(player, inv.createPlayerSpawnInventory(arena));
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
						openInv(player, inv.createSpawnConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo player spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					player.sendMessage(Utils.notify("&aWaiting room set!"));
					openInv(player, inv.createWaitingRoomInventory(arena));
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
						openInv(player, inv.createWaitingConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo waiting room to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			Arena arenaInstance = game.arenas.get(arena);
			int current = arenaInstance.getMaxPlayers();
			
			// Decrease max players
			if (buttonName.contains("Decrease")) {
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
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				arenaInstance.setMaxPlayers(++current);
				openInv(player, inv.createMaxPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			Arena arenaInstance = game.arenas.get(arena);
			int current = arenaInstance.getMinPlayers();

			// Decrease min players
			if (buttonName.contains("Decrease")) {
				// Check if min players is greater than 1
				if (current <= 1) {
					player.sendMessage(Utils.notify("&cMin players cannot be less than 1!"));
					return;
				}

				arenaInstance.setMinPlayers(--current);
				openInv(player, inv.createMinPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase min players
			else if (buttonName.contains("Increase")) {
				// Check if min players is less than max players
				if (current >= arenaInstance.getMaxPlayers()) {
					player.sendMessage(Utils.notify("&cMin players cannot be greater than max player!"));
					return;
				}

				arenaInstance.setMinPlayers(++current);
				openInv(player, inv.createMinPlayerInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createPlayersInventory(arena));
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				openInv(player, inv.createMonsterSpawnInventory(arena));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					openInv(player, inv.createMobsInventory(arena));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				openInv(player, inv.createVillagerSpawnInventory(arena));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					openInv(player, inv.createMobsInventory(arena));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createSpawnTableInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					openInv(player, inv.createMobsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
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
			Arena arenaInstance = game.arenas.get(arena);

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(oldSlot, player.getLocation());
					player.sendMessage(Utils.notify("&aMonster spawn set!"));
					openInv(player, inv.createMonsterSpawnMenu(arena, oldSlot));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(oldSlot, player.getLocation());
					player.sendMessage(Utils.notify("&aMonster spawn relocated!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getMonsterSpawn(oldSlot);
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
					if (arenaInstance.getMonsterSpawn(oldSlot) == null) {
						player.sendMessage(Utils.notify("&cNo monster spawn to center!"));
						return;
					}
					arenaInstance.centerMonsterSpawn(oldSlot);
					player.sendMessage(Utils.notify("&aMonster spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(oldSlot) != null)
					if (arenaInstance.isClosed())
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
			Arena arenaInstance = game.arenas.get(arena);

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(oldSlot, player.getLocation());
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
					openInv(player, inv.createVillagerSpawnMenu(arena, oldSlot));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(oldSlot, player.getLocation());
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Teleport player to spawn
			else if (buttonName.contains("Teleport")) {
				Location location = arenaInstance.getVillagerSpawn(oldSlot);
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
					if (arenaInstance.getVillagerSpawn(oldSlot) == null) {
						player.sendMessage(Utils.notify("&cNo villager spawn to center!"));
						return;
					}
					arenaInstance.centerVillagerSpawn(oldSlot);
					player.sendMessage(Utils.notify("&aVillager spawn centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getVillagerSpawn(oldSlot) != null)
					if (arenaInstance.isClosed())
						openInv(player, inv.createVillagerSpawnConfirmInventory());
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo villager spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createVillagerSpawnInventory(arena));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Default
			if (buttonName.contains("Default"))
				if (!arenaInstance.setSpawnTableFile("default"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 1
			else if (buttonName.contains("Option 1"))
				if (!arenaInstance.setSpawnTableFile("option1"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 2
			else if (buttonName.contains("Option 2"))
				if (!arenaInstance.setSpawnTableFile("option2"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 3
			else if (buttonName.contains("Option 3"))
				if (!arenaInstance.setSpawnTableFile("option3"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 4
			else if (buttonName.contains("Option 4"))
				if (!arenaInstance.setSpawnTableFile("option4"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 5
			else if (buttonName.contains("Option 5"))
				if (!arenaInstance.setSpawnTableFile("option5"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Option 6
			else if (buttonName.contains("Option 6"))
				if (!arenaInstance.setSpawnTableFile("option6"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Custom
			else if (buttonName.contains("Custom"))
				if (!arenaInstance.setSpawnTableFile("custom"))
					player.sendMessage(Utils.notify("&cFile doesn't exist!"));

			// Exit menu
			else if (buttonName.contains("EXIT")) {
				openInv(player, inv.createMobsInventory(arena));
				return;
			}

			// Reload inventory
			openInv(player, inv.createSpawnTableInventory(arena));
		}

		// Shop settings menu for an arena
		else if (title.contains("Shop Settings:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					openInv(player, arenaInstance.getCustomShopEditor());
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					openInv(player, inv.createShopsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					openInv(player, inv.createShopsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					openInv(player, inv.createShopsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Custom shop editor for an arena
		else if (title.contains("Custom Shop Editor:")) {
			ItemStack cursor = e.getCursor();
			String path = "a" + arena + ".customShop.";

			// Exit menu
			if (buttonName.contains("EXIT")) {
				openInv(player, inv.createShopsInventory(arena));
				return;
			}

			// Remove item from slot
			if (cursor.getType() == Material.AIR)
				if (buttonType != null)
					config.set(path + slot, null);
				else return;

			// Check for valid item
			else {
				try {
					Integer.parseInt(cursor.getItemMeta().getDisplayName()
							.substring(cursor.getItemMeta().getDisplayName().length() - 5));
					config.set(path + slot, cursor);
				} catch (Exception err) {
					player.sendMessage(Utils.notify(
							"&cItem must have numbers as the last 5 characters of the item name!"));
					return;
				}
			}

			// Save changes and refresh GUI
			plugin.saveArenaData();
			Utils.giveItem(player, cursor.clone(), language.getString("inventoryFull"));
			player.setItemOnCursor(new ItemStack(Material.AIR));
			openInv(player, game.arenas.get(arena).getCustomShopEditor());
		}

		// Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createMaxWaveInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createWaveTimeLimitInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					openInv(player, inv.createGameSettingsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createAllowedKitsInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createDifficultyLabelInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createDifficultyMultiplierInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				openInv(player, inv.createSoundsInventory(arena));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createCopySettingsInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInventory(arena));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			Arena arenaInstance = game.arenas.get(arena);
			int current = arenaInstance.getMaxWaves();

			// Decrease max waves
			if (buttonName.contains("Decrease")) {
				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);

				// Check if max waves is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cMax waves cannot be less than 1!"));
					return;
				} else arenaInstance.setMaxWaves(--current);

				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				arenaInstance.setMaxWaves(-1);
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				arenaInstance.setMaxWaves(1);
				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase max waves
			else if (buttonName.contains("Increase")) {
				// Check if max waves is unlimited
				if (current == -1)
					arenaInstance.setMaxWaves(1);
				else arenaInstance.setMaxPlayers(++current);

				openInv(player, inv.createMaxWaveInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			Arena arenaInstance = game.arenas.get(arena);
			int current = arenaInstance.getWaveTimeLimit();

			// Decrease wave time limit
			if (buttonName.contains("Decrease")) {
				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);

				// Check if wave time limit is greater than 1
				else if (current <= 1) {
					player.sendMessage(Utils.notify("&cWave time limit cannot be less than 1!"));
					return;
				} else arenaInstance.setWaveTimeLimit(--current);

				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				arenaInstance.setWaveTimeLimit(-1);
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				arenaInstance.setWaveTimeLimit(1);
				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Increase wave time limit
			else if (buttonName.contains("Increase")) {
				// Check if wave time limit is unlimited
				if (current == -1)
					arenaInstance.setWaveTimeLimit(1);
				else arenaInstance.setWaveTimeLimit(++current);

				openInv(player, inv.createWaveTimeLimitInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Set to Easy
			if (buttonName.contains("Easy")) {
				arenaInstance.setDifficultyLabel("Easy");
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				arenaInstance.setDifficultyLabel("Medium");
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				arenaInstance.setDifficultyLabel("Hard");
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				arenaInstance.setDifficultyLabel("Insane");
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				arenaInstance.setDifficultyLabel(null);
				openInv(player, inv.createDifficultyLabelInventory(arena));
				portal.refreshHolo(arena, game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Set to 1
			if (buttonName.contains("1")) {
				arenaInstance.setDifficultyMultiplier(1);
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				arenaInstance.setDifficultyMultiplier(2);
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				arenaInstance.setDifficultyMultiplier(3);
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				arenaInstance.setDifficultyMultiplier(4);
				openInv(player, inv.createDifficultyMultiplierInventory(arena));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Allowed kits menu for an arena
		else if (title.contains("Allowed Kits")) {
			String kit = buttonName.substring(4);
			Arena arenaInstance = game.arenas.get(arena);
			List<String> banned = arenaInstance.getBannedKits();

			// Toggle a kit
			if (!(kit.equals("Gift Kits") || kit.equals("Ability Kits") || kit.equals("Effect Kits") ||
					kit.equals("EXIT"))) {
				if (banned.contains(kit))
					banned.remove(kit);
				else banned.add(kit);
				arenaInstance.setBannedKits(banned);
				openInv(player, inv.createAllowedKitsInventory(arena));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					openInv(player, inv.createWaitSoundInventory(arena));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					openInv(player, inv.createSoundsInventory(arena));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				openInv(player, inv.createGameSettingsInventory(arena));
		}

		// Waiting sound menu for an arena
		else if (title.contains("Waiting Sound:")) {
			Arena arenaInstance = game.arenas.get(arena);

			// Exit menu
			if (buttonName.contains("EXIT"))
				openInv(player, inv.createSoundsInventory(arena));

			// Set sound
			else {
				arenaInstance.setWaitingSound(slot);
				openInv(player, inv.createWaitSoundInventory(arena));
			}
		}

		// Menu to copy game settings
		else if (title.contains("Copy Game Settings")) {
			Arena arena1 = game.arenas.get(arena);

			if (slot < 45) {
				Arena arena2 = game.arenas.get(slot);

				// Copy settings from another arena
				if (buttonType == Material.WHITE_CONCRETE)
					arena1.copy(arena2);
			}

			// Copy easy preset
			else if (buttonName.contains("Easy Preset")) {
				arena1.setMaxWaves(80);
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
				arena1.setMaxWaves(70);
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
				arena1.setMaxWaves(50);
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
				openInv(player, inv.createGameSettingsInventory(arena));
				return;
			}

			// Save updates
			portal.refreshHolo(arena, game);
			player.sendMessage(Utils.notify("&aGame settings copied!"));
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
				if (arenaInstance.hasNormal())
					openInv(player, arenaInstance.getWeaponShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open armor shop
			else if (buttonName.contains("Armor Shop"))
				if (arenaInstance.hasNormal())
					openInv(player, arenaInstance.getArmorShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open consumables shop
			else if (buttonName.contains("Consumables Shop"))
				if (arenaInstance.hasNormal())
					openInv(player, arenaInstance.getConsumeShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open custom shop
			else if (buttonName.contains("Custom Shop"))
				if (arenaInstance.hasCustom())
					openInv(player, arenaInstance.getCustomShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("customShopError")));
		}

		// Mock custom shop for an arena
		else if (title.contains("Custom Shop:")) {
			String name = title.substring(19);
			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.getName().equals(name)).collect(Collectors.toList()).get(0);
			if (buttonName.contains("EXIT"))
				openInv(player, inv.createArenaInfoInventory(arenaInstance));
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
			if (buttonName.contains("EXIT")) {
				player.openInventory(Inventories.createShop(arenaInstance.getCurrentWave() / 10 + 1, arenaInstance));
				return;
			}

			// Ignore null items
			if (e.getClickedInventory().getItem(e.getSlot()) == null)
				return;

			ItemStack buy = e.getClickedInventory().getItem(e.getSlot()).clone();
			Material buyType = buy.getType();
			List<String> lore = buy.getItemMeta().getLore();
			int cost = Integer.parseInt(lore.get(lore.size() - 1).substring(10));

			// Check if they can afford the item
			if (!gamer.canAfford(cost)) {
				player.sendMessage(Utils.notify("&c" + language.getString("buyError")));
				return;
			}

			// Remove cost meta
			buy = Utils.removeLastLore(buy);

			// Make unbreakable for blacksmith
			if (gamer.getKit().equals("Blacksmith"))
				buy = Utils.makeUnbreakable(buy);

			// Make splash potion for witch
			if (gamer.getKit().equals("Witch"))
				buy = Utils.makeSplash(buy);

			// Subtract from balance, apply rebate, and update scoreboard
			gamer.addGems(-cost);
			if (gamer.getKit().equals("Merchant"))
				gamer.addGems(cost / 20);
			game.createBoard(gamer);

			EntityEquipment equipment = player.getPlayer().getEquipment();

			// Equip armor if possible, otherwise put in inventory, otherwise drop at feet
			if (Arrays.stream(HELMETS).anyMatch(mat -> mat == buyType) && equipment.getHelmet() == null) {
				equipment.setHelmet(buy);
				player.sendMessage(Utils.notify("&a" + language.getString("helmet")));
			} else if (Arrays.stream(CHESTPLATES).anyMatch(mat -> mat == buyType) &&
					equipment.getChestplate() == null) {
				equipment.setChestplate(buy);
				player.sendMessage(Utils.notify("&a" + language.getString("chestplate")));
			} else if (Arrays.stream(LEGGINGS).anyMatch(mat -> mat == buyType) &&
					equipment.getLeggings() == null) {
				equipment.setLeggings(buy);
				player.sendMessage(Utils.notify("&a" + language.getString("leggings")));
			} else if (Arrays.stream(BOOTS).anyMatch(mat -> mat == buyType) && equipment.getBoots() == null) {
				equipment.setBoots(buy);
				player.sendMessage(Utils.notify("&a" + language.getString("boots")));
			} else {
				Utils.giveItem(player, buy, language.getString("inventoryFull"));
				player.sendMessage(Utils.notify("&a" + language.getString("buy")));
			}
		}

		// Stats menu for a player
		else if (title.contains("'s Stats")) {
			String name = title.substring(6, title.length() - 8);
			if (buttonName.contains("Kits"))
				openInv(player, inv.createPlayerKitsInventory(name, player.getName()));
		}

		// Kits menu for a player
		else if (title.contains("'s Kits")) {
			FileConfiguration playerData = plugin.getPlayerData();
			String name = title.substring(6, title.length() - 7);
			String kit = buttonName.substring(4);
			String path = name + ".kits.";

			// Check if requester is owner
			if (name.equals(player.getName())) {
				// Single tier kits
				if (kit.equals("Soldier") || kit.equals("Tailor") || kit.equals("Alchemist") || kit.equals("Trader")
				|| kit.equals("Phantom") || kit.equals("Blacksmith") || kit.equals("Witch") || kit.equals("Merchant")
				|| kit.equals("Vampire"))
					if (!playerData.getBoolean(path + kit))
						if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit)) {
							playerData.set(name + ".crystalBalance",
									playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit));
							playerData.set(path + kit, true);
							player.sendMessage(Utils.notify("&a" + language.getString("kitBuy")));
						} else player.sendMessage(Utils.notify("&c" + language.getString("kitBuyError")));

				// Double tier kits
				if (kit.equals("Giant"))
					switch (playerData.getInt(path + kit)) {
						case 1:
							if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit, 2)) {
								playerData.set(name + ".crystalBalance",
										playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit, 2));
								playerData.set(path + kit, 2);
								player.sendMessage(Utils.notify("&a" + language.getString("kitUpgrade")));
							} else player.sendMessage(Utils.notify("&c" +
									language.getString("kitUpgradeError")));
							break;
						case 2:
							return;
						default:
							if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit, 1)) {
								playerData.set(name + ".crystalBalance",
										playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit, 1));
								playerData.set(path + kit, 1);
								player.sendMessage(Utils.notify("&a" + language.getString("kitBuy")));
							} else player.sendMessage(Utils.notify("&c" + language.getString("kitBuyError")));
					}

				// Triple tier kits
				if (kit.equals("Summoner") || kit.equals("Reaper") || kit.equals("Mage") || kit.equals("Ninja") ||
						kit.equals("Templar") || kit.equals("Warrior") || kit.equals("Knight") || kit.equals("Priest")
				|| kit.equals("Siren") || kit.equals("Monk") || kit.equals("Messenger"))
					switch (playerData.getInt(path + kit)) {
						case 1:
							if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit, 2)) {
								playerData.set(name + ".crystalBalance",
										playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit, 2));
								playerData.set(path + kit, 2);
								player.sendMessage(Utils.notify("&a" + language.getString("kitUpgrade")));
							} else player.sendMessage(Utils.notify("&c" +
									language.getString("kitUpgradeError")));
							break;
						case 2:
							if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit, 3)) {
								playerData.set(name + ".crystalBalance",
										playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit, 3));
								playerData.set(path + kit, 3);
								player.sendMessage(Utils.notify("&a" + language.getString("kitUpgrade")));
							} else player.sendMessage(Utils.notify("&c" +
									language.getString("kitUpgradeError")));
							break;
						case 3:
							return;
						default:
							if (playerData.getInt(name + ".crystalBalance") >= kits.getPrice(kit, 1)) {
								playerData.set(name + ".crystalBalance",
										playerData.getInt(name + ".crystalBalance") - kits.getPrice(kit, 1));
								playerData.set(path + kit, 1);
								player.sendMessage(Utils.notify("&a" + language.getString("kitBuy")));
							} else player.sendMessage(Utils.notify("&c" + language.getString("kitBuyError")));
					}
			}

			if (buttonName.contains("EXIT")) {
				openInv(player, inv.createPlayerStatsInventory(name));
				return;
			}

			plugin.savePlayerData();
			openInv(player, inv.createPlayerKitsInventory(name, name));
		}

		// Kit selection menu for an arena
		else if (title.contains(" Kits")) {
			FileConfiguration playerData = plugin.getPlayerData();
			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.hasPlayer(player)).collect(Collectors.toList()).get(0);
			VDPlayer gamer = arenaInstance.getPlayer(player);
			String kit = buttonName.substring(4);
			String path = player.getName() + ".kits.";

			// Check for useful phantom selection
			if (gamer.isSpectating() && playerData.getBoolean(path + "Phantom") &&
					kit.equals("Phantom")) {
				if (arenaInstance.isEnding())
					player.sendMessage(Utils.notify("&c" + language.getString("phantomError")));
				else {
					Utils.teleAdventure(player, arenaInstance.getPlayerSpawn());
					gamer.flipSpectating();
					arenaInstance.getTask().giveItems(gamer);
				}
				player.closeInventory();
				return;
			}

			// Single tier kits
			if (kit.equals("Orc") || kit.equals("Farmer") || kit.equals("Soldier") || kit.equals("Tailor") ||
					kit.equals("Alchemist") || kit.equals("Trader") || kit.equals("Phantom") || kit.equals("Blacksmith")
					|| kit.equals("Witch") || kit.equals("Merchant") || kit.equals("Vampire")) {
				if (playerData.getBoolean(path + kit) || kit.equals("Orc") || kit.equals("Farmer")) {
					gamer.setKit(kit);
					player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
				} else {
					player.sendMessage(Utils.notify("&c" + language.getString("kitSelectError")));
					return;
				}
				player.closeInventory();
			}

			// Double tier kits
			if (kit.equals("Giant")) {
				switch (playerData.getInt(path + kit)) {
					case 1:
						gamer.setKit(kit + 1);
						player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
						break;
					case 2:
						gamer.setKit(kit + 2);
						player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
						break;
					default:
						player.sendMessage(Utils.notify("&c" + language.getString("kitSelectError")));
						return;
				}
				player.closeInventory();
			}

			// Triple tier kits
			if (kit.equals("Summoner") || kit.equals("Reaper") || kit.equals("Mage") || kit.equals("Ninja") ||
					kit.equals("Templar") || kit.equals("Warrior") || kit.equals("Knight") || kit.equals("Priest")
					|| kit.equals("Siren") || kit.equals("Monk") || kit.equals("Messenger")) {
				switch (playerData.getInt(path + kit)) {
					case 1:
						gamer.setKit(kit + 1);
						player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
						break;
					case 2:
						gamer.setKit(kit + 2);
						player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
						break;
					case 3:
						gamer.setKit(kit + 3);
						player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
						break;
					default:
						player.sendMessage(Utils.notify("&c" + language.getString("kitSelectError")));
						return;
				}
				player.closeInventory();
			}

			// No kit
			if (kit.equals("None")) {
				gamer.setKit(null);
				player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
				player.closeInventory();
			}

			// Close inventory
			if (buttonName.contains("EXIT"))
				player.closeInventory();
		}

		// Stats menu for an arena
		else if (title.contains("Info")) {
			String name = title.substring(6, title.length() - 5);
			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.getName().equals(name)).collect(Collectors.toList()).get(0);
			if (buttonName.contains("Custom Shop Inventory"))
				openInv(player, arenaInstance.getMockCustomShop());
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
