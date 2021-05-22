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
	private boolean close;

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

		// Cancel the event
		e.setCancelled(true);

		// Ignore clicks in player inventory
		if (e.getClickedInventory().getType() == InventoryType.PLAYER)
			return;

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
		FileConfiguration config = plugin.getArenaData();
		FileConfiguration language = plugin.getLanguageData();

		// Arena inventory
		if (title.contains("Villager Defense Arenas")) {
			// Create new arena with naming inventory
			if (buttonType == Material.RED_CONCRETE) {
				game.arenas.set(slot, new Arena(plugin, slot, new Tasks(plugin, game, slot, portal)));
				player.openInventory(inv.createNamingInventory(slot, ""));
			}

			// Edit existing arena
			else if (buttonType == Material.LIME_CONCRETE)
				player.openInventory(inv.createArenaInventory(slot));

			// Open lobby menu
			else if (buttonName.contains("Lobby"))
				player.openInventory(inv.createLobbyInventory());

			// Open info boards menu
			else if (buttonName.contains("Info Boards"))
				player.openInventory(inv.createInfoBoardInventory());

			// Open leaderboards menu
			else if (buttonName.contains("Leaderboards"))
				player.openInventory(inv.createLeaderboardInventory());

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
				game.reloadLobby();
				player.sendMessage(Utils.notify("&aLobby set!"));
				player.openInventory(inv.createLobbyInventory());
			}

			// Relocate lobby
			else if (buttonName.contains("Relocate Lobby")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				game.reloadLobby();
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
					player.openInventory(inv.createLobbyConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo lobby to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenasInventory());
		}

		// Info board menu
		else if (title.contains("Info Boards")) {
			// Edit board
			if (Arrays.asList(Inventories.INFO_BOARD_MATS).contains(buttonType))
				player.openInventory(inv.createInfoBoardMenu(slot));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenasInventory());
		}

		// Info board menu for a specific board
		else if (title.contains("Info Board ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			int num = meta.getInteger1();
			String path = "infoBoard." + num;

			// Create board
			if (buttonName.contains("Create")) {
				infoBoard.createInfoBoard(player, num);
				player.sendMessage(Utils.notify("&aInfo board set!"));
				player.openInventory(inv.createInfoBoardMenu(num));
			}

			// Relocate board
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				infoBoard.refreshInfoBoard(num);
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
				Utils.centerConfigLocation(plugin, path);
				infoBoard.refreshInfoBoard(num);
				player.sendMessage(Utils.notify("&aInfo board centered!"));
			}

			// Remove info board
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createInfoBoardConfirmInventory(num));
				else player.sendMessage(Utils.notify("&cNo info board to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createInfoBoardInventory());
		}

		// Leaderboard menu
		else if (title.contains("Leaderboards")) {
			if (buttonName.contains("Total Kills Leaderboard"))
				player.openInventory(inv.createTotalKillsLeaderboardInventory());

			if (buttonName.contains("Top Kills Leaderboard"))
				player.openInventory(inv.createTopKillsLeaderboardInventory());

			if (buttonName.contains("Total Gems Leaderboard"))
				player.openInventory(inv.createTotalGemsLeaderboardInventory());

			if (buttonName.contains("Top Balance Leaderboard"))
				player.openInventory(inv.createTopBalanceLeaderboardInventory());

			if (buttonName.contains("Top Wave Leaderboard"))
				player.openInventory(inv.createTopWaveLeaderboardInventory());

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenasInventory());
		}

		// Total kills leaderboard menu
		else if (title.contains(Utils.format("&4&lTotal Kills Leaderboard"))) {
			String path = "leaderboard.totalKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				leaderboard.createLeaderboard(player, "totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createTotalKillsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				leaderboard.refreshLeaderboard("totalKills");
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
				Utils.centerConfigLocation(plugin, path);
				leaderboard.refreshLeaderboard("totalKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createTotalKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createLeaderboardInventory());
		}

		// Top kills leaderboard menu
		else if (title.contains(Utils.format("&c&lTop Kills Leaderboard"))) {
			String path = "leaderboard.topKills";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				leaderboard.createLeaderboard(player, "topKills");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createTopKillsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				leaderboard.refreshLeaderboard("topKills");
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
				Utils.centerConfigLocation(plugin, path);
				leaderboard.refreshLeaderboard("topKills");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createTopKillsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createLeaderboardInventory());
		}

		// Total gems leaderboard menu
		else if (title.contains(Utils.format("&2&lTotal Gems Leaderboard"))) {
			String path = "leaderboard.totalGems";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				leaderboard.createLeaderboard(player, "totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createTotalGemsLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				leaderboard.refreshLeaderboard("totalGems");
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
				Utils.centerConfigLocation(plugin, path);
				leaderboard.refreshLeaderboard("totalGems");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createTotalGemsConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createLeaderboardInventory());
		}

		// Top balance leaderboard menu
		else if (title.contains(Utils.format("&a&lTop Balance Leaderboard"))) {
			String path = "leaderboard.topBalance";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				leaderboard.createLeaderboard(player, "topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createTopBalanceLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				leaderboard.refreshLeaderboard("topBalance");
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
				Utils.centerConfigLocation(plugin, path);
				leaderboard.refreshLeaderboard("topBalance");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createTopBalanceConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createLeaderboardInventory());
		}

		// Top wave leaderboard menu
		else if (title.contains(Utils.format("&9&lTop Wave Leaderboard"))) {
			String path = "leaderboard.topWave";

			// Create leaderboard
			if (buttonName.contains("Create")) {
				leaderboard.createLeaderboard(player, "topWave");
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createTopWaveLeaderboardInventory());
			}

			// Relocate leaderboard
			else if (buttonName.contains("Relocate")) {
				Utils.setConfigurationLocation(plugin, path, player.getLocation());
				leaderboard.refreshLeaderboard("topWave");
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
				Utils.centerConfigLocation(plugin, path);
				leaderboard.refreshLeaderboard("topWave");
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE"))
				if (config.contains(path))
					player.openInventory(inv.createTopWaveConfirmInventory());
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createLeaderboardInventory());
		}

		// Naming inventory
		else if (title.contains("Arena ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
			int num = slot;

			// Get new name buffer
			String newName = meta.getString();

			// Check for caps lock toggle
			boolean caps = arenaInstance.isCaps();
			if (caps)
				num += 36;

			// Letters and numbers
			if (Arrays.asList(Inventories.KEY_MATS).contains(buttonType))
				openInv(player, inv.createNamingInventory(meta.getInteger1(),
						newName + Inventories.NAMES[num]));

			// Spaces
			else if (buttonName.contains("Space"))
				openInv(player, inv.createNamingInventory(meta.getInteger1(), newName + Inventories.NAMES[72]));

			// Caps lock
			else if (buttonName.contains("CAPS LOCK")) {
				arenaInstance.flipCaps();
				openInv(player, inv.createNamingInventory(meta.getInteger1(), newName));
			}

			// Backspace
			else if (buttonName.contains("Backspace")) {
				if (newName.length() == 0)
					return;
				openInv(player, inv.createNamingInventory(meta.getInteger1(),
						newName.substring(0, newName.length() - 1)));
			}

			// Save
			else if (buttonName.contains("SAVE")) {
				// Check if name is not empty
				if (newName.length() == 0) {
					player.sendMessage(Utils.notify("&cName cannot be empty!"));
					return;
				} else arenaInstance.setName(newName);

				player.openInventory(inv.createArenaInventory(meta.getInteger1()));

				// Set default max players to 12 if it doesn't exist
				if (arenaInstance.getMaxPlayers() == 0)
					arenaInstance.setMaxPlayers(12);

				// Set default min players to 1 if it doesn't exist
				if (arenaInstance.getMinPlayers() == 0)
					arenaInstance.setMinPlayers(1);

				// Set default wolf cap to 5 if it doesn't exist
				if (arenaInstance.getWolfCap() == 0)
					arenaInstance.setWolfCap(5);

				// Set default iron golem cap to 2 if it doesn't exist
				if (arenaInstance.getGolemCap() == 0)
					arenaInstance.setgolemCap(2);

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
				if (!config.contains("a" + meta.getInteger1() + ".closed"))
					arenaInstance.setClosed(true);

				// Set default sound options
				if (!config.contains("a" + meta.getInteger1() + ".sounds")) {
					arenaInstance.setWinSound(true);
					arenaInstance.setLoseSound(true);
					arenaInstance.setWaveStartSound(true);
					arenaInstance.setWaveFinishSound(true);
					arenaInstance.setGemSound(true);
					arenaInstance.setPlayerDeathSound(true);
					arenaInstance.setWaitingSound(14);
				}

				// Set default shop toggle
				if (!config.contains("a" + meta.getInteger1() + ".normal"))
					arenaInstance.setNormal(true);

				// Set default gem drop toggle
				if (!config.contains("a" + meta.getInteger1() + ".gemDrop"))
					arenaInstance.setGemDrop(true);

				// Set default experience drop toggle
				if (!config.contains("a" + meta.getInteger1() + ".expDrop"))
					arenaInstance.setExpDrop(true);

				// Set default particle toggles
				if (!config.contains("a" + meta.getInteger1() + ".particles.spawn"))
					arenaInstance.setSpawnParticles(true);
				if (!config.contains("a" + meta.getInteger1() + ".particles.monster"))
					arenaInstance.setMonsterParticles(true);
				if (!config.contains("a" + meta.getInteger1() + ".particles.villager"))
					arenaInstance.setVillagerParticles(true);

				// Recreate portal if it exists
				if (arenaInstance.getPortal() != null)
					portal.refreshHolo(meta.getInteger1(), game);
			}

			// Cancel
			else if (buttonName.contains("CANCEL")) {
				if (arenaInstance.getName() == null) {
					game.arenas.set(meta.getInteger1(), null);
					openInv(player, inv.createArenasInventory());
				} else openInv(player, inv.createArenaInventory(meta.getInteger1()));
			}
		}

		// Menu for an arena
		else if (title.contains(Utils.format("&2&lEdit "))) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Open name editor
			if (buttonName.contains("Edit Name"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createNamingInventory(meta.getInteger1(), arenaInstance.getName()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open portal menu
			else if (buttonName.contains("Portal and Leaderboard"))
				player.openInventory(inv.createPortalInventory(meta.getInteger1()));

			// Open player menu
			else if (buttonName.contains("Player Settings"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));

			// Open mob menu
			else if (buttonName.contains("Mob Settings"))
				player.openInventory(inv.createMobsInventory(meta.getInteger1()));

			// Open shop menu
			else if (buttonName.contains("Shop Settings"))
				player.openInventory(inv.createShopsInventory(meta.getInteger1()));

			// Open game settings menu
			else if (buttonName.contains("Game Settings"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));

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
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Open arena remove confirmation menu
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createArenaConfirmInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Return to arenas menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenasInventory());
		}

		// Confirmation menus
		else if (title.contains("Remove")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Confirm to remove portal
			if (title.contains("Remove Portal?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createPortalInventory(meta.getInteger1()));

				// Remove the portal, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					// Remove portal data, close arena
					arenaInstance.setPortal(null);
					arenaInstance.setClosed(true);

					// Remove portal
					portal.removePortalAll(meta.getInteger1());

					// Confirm and return
					player.sendMessage(Utils.notify("&aPortal removed!"));
					player.openInventory(inv.createPortalInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove leaderboard
			else if (title.contains("Remove Leaderboard?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createPortalInventory(meta.getInteger1()));

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					// Remove leaderboard data, close arena
					arenaInstance.setArenaBoard(null);

					// Remove Portal
					arenaBoard.removeArenaBoard(meta.getInteger1());

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createPortalInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove player spawn
			else if (title.contains("Remove Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createPlayerSpawnInventory(meta.getInteger1()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					arenaInstance.setPlayerSpawn(null);
					arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aSpawn removed!"));
					player.openInventory(inv.createPlayerSpawnInventory(meta.getInteger1()));
					portal.refreshHolo(meta.getInteger1(), game);
				}
			}

			// Confirm to remove waiting room
			else if (title.contains("Remove Waiting Room?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createWaitingRoomInventory(meta.getInteger1()));

				// Remove spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					arenaInstance.setWaitingRoom(null);
					player.sendMessage(Utils.notify("&aWaiting room removed!"));
					player.openInventory(inv.createWaitingRoomInventory(meta.getInteger1()));
				}
			}

			// Confirm to remove monster spawn
			else if (title.contains("Remove Monster Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));

				// Remove the monster spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					arenaInstance.setMonsterSpawn(meta.getInteger2(), null);
					if (arenaInstance.getMonsterSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
					player.openInventory(inv.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));
					portal.refreshHolo(meta.getInteger1(), game);
				}
			}

			// Confirm to remove villager spawn
			else if (title.contains("Remove Villager Spawn?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));

				// Remove the villager spawn, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					arenaInstance.setVillagerSpawn(meta.getInteger2(), null);
					if (arenaInstance.getVillagerSpawns().stream().noneMatch(Objects::nonNull))
						arenaInstance.setClosed(true);
					player.sendMessage(Utils.notify("&aMob spawn removed!"));
					player.openInventory(inv.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));
					portal.refreshHolo(meta.getInteger1(), game);
				}
			}

			// Confirm to remove lobby
			else if (title.contains("Remove Lobby?")) {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createLobbyInventory());

				// Remove the lobby, then return to previous menu
				else if (buttonName.contains("YES")) {
					config.set("lobby", null);
					plugin.saveArenaData();
					game.reloadLobby();
					player.sendMessage(Utils.notify("&aLobby removed!"));
					player.openInventory(inv.createLobbyInventory());
				}
			}

			// Confirm to remove info board
			else if (title.contains("Remove Info Board?")) {
				String path = "infoBoard." + meta.getInteger1();

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createInfoBoardMenu(meta.getInteger1()));

				// Remove the info board, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove info board data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove info board
					infoBoard.removeInfoBoard(meta.getInteger1());

					// Confirm and return
					player.sendMessage(Utils.notify("&aInfo board removed!"));
					player.openInventory(inv.createInfoBoardMenu(meta.getInteger1()));
				}
			}

			// Confirm to remove total kills leaderboard
			else if (title.contains("Remove Total Kills Leaderboard?")) {
				String path = "leaderboard.totalKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createTotalKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("totalKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createTotalKillsLeaderboardInventory());
				}
			}

			// Confirm to remove top kills leaderboard
			else if (title.contains("Remove Top Kills Leaderboard?")) {
				String path = "leaderboard.topKills";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createTopKillsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topKills");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createTopKillsLeaderboardInventory());
				}
			}

			// Confirm to remove total gems leaderboard
			else if (title.contains("Remove Total Gems Leaderboard?")) {
				String path = "leaderboard.totalGems";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createTotalGemsLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("totalGems");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createTotalGemsLeaderboardInventory());
				}
			}

			// Confirm to remove top balance leaderboard
			else if (title.contains("Remove Top Balance Leaderboard?")) {
				String path = "leaderboard.topBalance";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createTopBalanceLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topBalance");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createTopBalanceLeaderboardInventory());
				}
			}

			// Confirm to remove top wave leaderboard
			else if (title.contains("Remove Top Wave Leaderboard?")) {
				String path = "leaderboard.topWave";

				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createTopWaveLeaderboardInventory());

				// Remove the leaderboard, then return to previous menu
				else if (buttonName.contains("YES")) {
					// Remove leaderboard data
					config.set(path, null);
					plugin.saveArenaData();

					// Remove leaderboard
					leaderboard.removeLeaderboard("topWave");

					// Confirm and return
					player.sendMessage(Utils.notify("&aLeaderboard removed!"));
					player.openInventory(inv.createTopWaveLeaderboardInventory());
				}
			}

			// Confirm to remove arena
			else {
				// Return to previous menu
				if (buttonName.contains("NO"))
					player.openInventory(inv.createArenaInventory(meta.getInteger1()));

				// Remove arena data, then return to previous menu
				else if (buttonName.contains("YES")) {
					Arena arenaInstance = game.arenas.get(meta.getInteger1());

					// Remove data
					arenaInstance.remove();
					game.arenas.set(meta.getInteger1(), null);

					// Remove displays
					portal.removePortalAll(meta.getInteger1());
					arenaBoard.removeArenaBoard(meta.getInteger1());

					// Confirm and return
					player.sendMessage(Utils.notify("&aArena removed!"));
					player.openInventory(inv.createArenasInventory());
				}
			}
		}

		// Portal and leaderboard menu for an arena
		else if (title.contains("Portal/LBoard:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Create portal
			if (buttonName.contains("Create Portal"))
				if (arenaInstance.isClosed()) {
					portal.createPortal(player, meta.getInteger1(), game);
					player.sendMessage(Utils.notify("&aPortal set!"));
					player.openInventory(inv.createPortalInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			
			// Relocate portal
			if (buttonName.contains("Relocate Portal"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPortal(player.getLocation());
					portal.refreshPortal(meta.getInteger1(), game);
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
					portal.refreshPortal(meta.getInteger1(), game);
					player.sendMessage(Utils.notify("&aPortal centered!"));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Remove portal
			else if (buttonName.contains("REMOVE PORTAL"))
				if (arenaInstance.getPortal() != null)
					if (arenaInstance.isClosed())
						player.openInventory(inv.createPortalConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo portal to remove!"));

			// Create leaderboard
			if (buttonName.contains("Create Leaderboard")) {
				arenaBoard.createArenaBoard(player, arenaInstance);
				player.sendMessage(Utils.notify("&aLeaderboard set!"));
				player.openInventory(inv.createPortalInventory(meta.getInteger1()));
			}

			// Relocate leaderboard
			if (buttonName.contains("Relocate Leaderboard")) {
				arenaInstance.setArenaBoard(player.getLocation());
				arenaBoard.refreshArenaBoard(meta.getInteger1());
				player.sendMessage(Utils.notify("&aLeaderboard relocated!"));
			}

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
				arenaBoard.refreshArenaBoard(meta.getInteger1());
				player.sendMessage(Utils.notify("&aLeaderboard centered!"));
			}

			// Remove leaderboard
			else if (buttonName.contains("REMOVE LEADERBOARD"))
				if (arenaInstance.getArenaBoard() != null)
					player.openInventory(inv.createArenaBoardConfirmInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cNo leaderboard to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
		}

		// Player settings menu for an arena
		else if (title.contains("Player Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Open player spawn editor
			if (buttonName.contains("Player Spawn"))
				player.openInventory(inv.createPlayerSpawnInventory(meta.getInteger1()));

			// Toggle player spawn particles
			else if (buttonName.contains("Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setSpawnParticles(!arenaInstance.hasSpawnParticles());
					player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open waiting room editor
			else if (buttonName.contains("Waiting"))
				player.openInventory(inv.createWaitingRoomInventory(meta.getInteger1()));

			// Edit max players
			else if (buttonName.contains("Maximum"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createMaxPlayerInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit min players
			else if (buttonName.contains("Minimum"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createMinPlayerInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit wolf cap
			else if (buttonName.contains("Wolf Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createWolfCapInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit iron golem cap
			else if (buttonName.contains("Iron Golem Cap"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createGolemCapInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
		}

		// Player spawn menu for an arena
		else if (title.contains("Player Spawn:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerSpawn(player.getLocation());
					player.sendMessage(Utils.notify("&aSpawn set!"));
					player.openInventory(inv.createPlayerSpawnInventory(meta.getInteger1()));
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
						player.openInventory(inv.createSpawnConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo player spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Waiting room menu for an arena
		else if (title.contains("Waiting Room:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Create waiting room
			if (buttonName.contains("Create")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaitingRoom(player.getLocation());
					player.sendMessage(Utils.notify("&aWaiting room set!"));
					player.openInventory(inv.createWaitingRoomInventory(meta.getInteger1()));
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
						player.openInventory(inv.createWaitingConfirmInventory(meta.getInteger1()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo waiting room to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Max player menu for an arena
		else if (title.contains("Maximum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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
				player.openInventory(inv.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Increase max players
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxPlayers(++current);
				player.openInventory(inv.createMaxPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Min player menu for an arena
		else if (title.contains("Minimum Players:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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
				player.openInventory(inv.createMinPlayerInventory(meta.getInteger1()));
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
				player.openInventory(inv.createMinPlayerInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Wolf cap menu for an arena
		else if (title.contains("Wolf Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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
				player.openInventory(inv.createWolfCapInventory(meta.getInteger1()));
			}

			// Increase wolf cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWolfCap(++current);
				player.openInventory(inv.createWolfCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Iron golem cap menu for an arena
		else if (title.contains("Iron Golem Cap:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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
				player.openInventory(inv.createGolemCapInventory(meta.getInteger1()));
			}

			// Increase iron golem cap
			else if (buttonName.contains("Increase")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setgolemCap(++current);
				player.openInventory(inv.createGolemCapInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createPlayersInventory(meta.getInteger1()));
		}

		// Mob settings menu for an arena
		else if (title.contains("Mob Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Open monster spawns editor
			if (buttonName.contains("Monster Spawns"))
				player.openInventory(inv.createMonsterSpawnInventory(meta.getInteger1()));

			// Toggle monster spawn particles
			else if (buttonName.contains("Monster Spawn Particles:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterParticles(!arenaInstance.hasMonsterParticles());
					player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Open villager spawns editor
			else if (buttonName.contains("Villager Spawns"))
				player.openInventory(inv.createVillagerSpawnInventory(meta.getInteger1()));

			// Toggle villager spawn particles
			else if (buttonName.contains("Villager Spawn Particles"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerParticles(!arenaInstance.hasVillagerParticles());
					player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				}
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit spawn table
			else if (buttonName.contains("Spawn Table"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createSpawnTableInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic mob count
			else if (buttonName.contains("Dynamic Mob Count:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicCount(!arenaInstance.hasDynamicCount());
					player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic difficulty
			else if (buttonName.contains("Dynamic Difficulty:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicDifficulty(!arenaInstance.hasDynamicDifficulty());
					player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle experience drop
			else if (buttonName.contains("Experience Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setExpDrop(!arenaInstance.hasExpDrop());
					player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
		}

		// Monster spawn menu for an arena
		else if (title.contains("Monster Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Edit spawn
			if (Arrays.asList(Inventories.MONSTER_MATS).contains(buttonType))
				player.openInventory(inv.createMonsterSpawnMenu(meta.getInteger1(), slot));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createMobsInventory(meta.getInteger1()));
		}

		// Monster spawn menu for a specific spawn
		else if (title.contains("Monster Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setMonsterSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aMonster spawn set!"));
					player.openInventory(inv.createMonsterSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate Spawn"))
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

			// Remove spawn
			else if (buttonName.contains("REMOVE"))
				if (arenaInstance.getMonsterSpawn(meta.getInteger2()) != null)
					if (arenaInstance.isClosed())
						player.openInventory(inv.createMonsterSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo monster spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createMonsterSpawnInventory(meta.getInteger1()));
		}

		// Villager spawn menu for an arena
		else if (title.contains("Villager Spawns:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Edit spawn
			if (Arrays.asList(Inventories.VILLAGER_MATS).contains(buttonType))
				player.openInventory(inv.createVillagerSpawnMenu(meta.getInteger1(), slot));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createMobsInventory(meta.getInteger1()));
		}

		// Villager spawn menu for a specific spawn
		else if (title.contains("Villager Spawn ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Create spawn
			if (buttonName.contains("Create Spawn"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setVillagerSpawn(meta.getInteger2(), player.getLocation());
					player.sendMessage(Utils.notify("&aVillager spawn set!"));
					player.openInventory(inv.createVillagerSpawnMenu(meta.getInteger1(), meta.getInteger2()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Relocate spawn
			if (buttonName.contains("Relocate Spawn"))
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
						player.openInventory(inv.createVillagerSpawnConfirmInventory(meta.getInteger1(),
								meta.getInteger2()));
					else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
				else player.sendMessage(Utils.notify("&cNo villager spawn to remove!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createVillagerSpawnInventory(meta.getInteger1()));
		}

		// Spawn table menu for an arena
		else if (title.contains("Spawn Table:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

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
				player.openInventory(inv.createMobsInventory(meta.getInteger1()));
				return;
			}

			// Reload inventory
			player.openInventory(inv.createSpawnTableInventory(meta.getInteger1()));
		}

		// Shop settings menu for an arena
		else if (title.contains("Shop Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Open custom shop editor
			if (buttonName.contains("Edit Custom Shop"))
				if (arenaInstance.isClosed())
					player.openInventory(arenaInstance.getCustomShopEditor());
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle default shop
			else if (buttonName.contains("Default Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setNormal(!arenaInstance.hasNormal());
					player.openInventory(inv.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle custom shop
			else if (buttonName.contains("Custom Shop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setCustom(!arenaInstance.hasCustom());
					player.openInventory(inv.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic prices
			else if (buttonName.contains("Dynamic Prices:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicPrices(!arenaInstance.hasDynamicPrices());
					player.openInventory(inv.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle gem drop
			else if (buttonName.contains("Gem Drop:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemDrop(!arenaInstance.hasGemDrop());
					player.openInventory(inv.createShopsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
		}

		// Custom shop editor for an arena
		else if (title.contains("Custom Shop Editor:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			ItemStack cursor = e.getCursor();
			String path = "a" + meta.getInteger1() + ".customShop.";
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Exit menu
			if (buttonName.contains("EXIT")) {
				player.openInventory(inv.createShopsInventory(meta.getInteger1()));
				return;
			}

			// Check for arena closure
			if (!arenaInstance.isClosed()) {
				player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
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
			player.openInventory(arenaInstance.getCustomShopEditor());
		}

		// Game settings menu for an arena
		else if (title.contains("Game Settings:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Change max waves
			if (buttonName.contains("Max Waves"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createMaxWaveInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Change wave time limit
			else if (buttonName.contains("Wave Time Limit"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createWaveTimeLimitInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle dynamic wave time limit
			else if (buttonName.contains("Dynamic Time Limit:"))
				if (arenaInstance.isClosed()) {
					arenaInstance.setDynamicLimit(!arenaInstance.hasDynamicLimit());
					player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit allowed kits
			else if (buttonName.contains("Allowed Kits"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createAllowedKitsInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit difficulty label
			else if (buttonName.contains("Difficulty Label"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit overall difficulty multiplier
			else if (buttonName.contains("Difficulty Multiplier"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createDifficultyMultiplierInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Edit sounds
			else if (buttonName.contains("Sounds"))
				player.openInventory(inv.createSoundsInventory(meta.getInteger1()));

			// Copy game settings from another arena or a preset
			else if (buttonName.contains("Copy Game Settings"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createCopySettingsInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInventory(meta.getInteger1()));
		}

		// Max wave menu for an arena
		else if (title.contains("Maximum Waves:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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

				player.openInventory(inv.createMaxWaveInventory(meta.getInteger1()));
			}

			// Set max waves to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxWaves(-1);
				player.openInventory(inv.createMaxWaveInventory(meta.getInteger1()));
			}

			// Reset max waves to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setMaxWaves(1);
				player.openInventory(inv.createMaxWaveInventory(meta.getInteger1()));
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

				player.openInventory(inv.createMaxWaveInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Wave time limit menu for an arena
		else if (title.contains("Wave Time Limit:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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

				player.openInventory(inv.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Set wave time limit to unlimited
			if (buttonName.contains("Unlimited")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWaveTimeLimit(-1);
				player.openInventory(inv.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Reset wave time limit to 1
			if (buttonName.contains("Reset")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWaveTimeLimit(1);
				player.openInventory(inv.createWaveTimeLimitInventory(meta.getInteger1()));
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

				player.openInventory(inv.createWaveTimeLimitInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty label menu for an arena
		else if (title.contains("Difficulty Label:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Set to Easy
			if (buttonName.contains("Easy")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Easy");
				player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Set to Medium
			if (buttonName.contains("Medium")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Medium");
				player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Set to Hard
			if (buttonName.contains("Hard")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Hard");
				player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Set to Insane
			if (buttonName.contains("Insane")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel("Insane");
				player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Set to nothing
			if (buttonName.contains("None")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyLabel(null);
				player.openInventory(inv.createDifficultyLabelInventory(meta.getInteger1()));
				portal.refreshHolo(meta.getInteger1(), game);
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Difficulty multiplier menu for an arena
		else if (title.contains("Difficulty Multiplier:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Set to 1
			if (buttonName.contains("1")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(1);
				player.openInventory(inv.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 2
			if (buttonName.contains("2")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(2);
				player.openInventory(inv.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 3
			if (buttonName.contains("3")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(3);
				player.openInventory(inv.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Set to 4
			if (buttonName.contains("4")) {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setDifficultyMultiplier(4);
				player.openInventory(inv.createDifficultyMultiplierInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Allowed kits display for an arena
		else if (title.contains("Allowed Kits: ")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			// Exit menu
			if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInfoInventory(game.arenas.get(meta.getInteger1())));
		}

		// Allowed kits menu for an arena
		else if (title.contains("Allowed Kits")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			String kit = buttonName.substring(4);
			Arena arenaInstance = game.arenas.get(meta.getInteger1());
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
				player.openInventory(inv.createAllowedKitsInventory(meta.getInteger1()));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Sound settings menu for an arena
		else if (title.contains("Sounds:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Toggle win sound
			if (buttonName.contains("Win")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWinSound(!arenaInstance.hasWinSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle lose sound
			else if (buttonName.contains("Lose")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setLoseSound(!arenaInstance.hasLoseSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave start sound
			else if (buttonName.contains("Start")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveStartSound(!arenaInstance.hasWaveStartSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle wave finish sound
			else if (buttonName.contains("Finish")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setWaveFinishSound(!arenaInstance.hasWaveFinishSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Edit waiting sound
			else if (buttonName.contains("Waiting"))
				if (arenaInstance.isClosed())
					player.openInventory(inv.createWaitSoundInventory(meta.getInteger1()));
				else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));

			// Toggle gem pickup sound
			else if (buttonName.contains("Gem")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setGemSound(!arenaInstance.hasGemSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Toggle player death sound
			else if (buttonName.contains("Death")) {
				if (arenaInstance.isClosed()) {
					arenaInstance.setPlayerDeathSound(!arenaInstance.hasPlayerDeathSound());
					player.openInventory(inv.createSoundsInventory(meta.getInteger1()));
				} else player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
			}

			// Exit menu
			else if (buttonName.contains("EXIT"))
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
		}

		// Waiting sound menu for an arena
		else if (title.contains("Waiting Sound:")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arenaInstance = game.arenas.get(meta.getInteger1());

			// Exit menu
			if (buttonName.contains("EXIT"))
				player.openInventory(inv.createSoundsInventory(meta.getInteger1()));

			// Set sound
			else {
				// Check for arena closure
				if (!arenaInstance.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				arenaInstance.setWaitingSound(slot);
				player.openInventory(inv.createWaitSoundInventory(meta.getInteger1()));
			}
		}

		// Menu to copy game settings
		else if (title.contains("Copy Game Settings")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arena1 = game.arenas.get(meta.getInteger1());

			if (slot < 45) {
				// Check for arena closure
				if (!arena1.isClosed()) {
					player.sendMessage(Utils.notify("&cArena must be closed to modify this!"));
					return;
				}

				Arena arena2 = game.arenas.get(slot);

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
				player.openInventory(inv.createGameSettingsInventory(meta.getInteger1()));
				return;
			}

			// Save updates
			portal.refreshHolo(meta.getInteger1(), game);
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
					player.openInventory(arenaInstance.getWeaponShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open armor shop
			else if (buttonName.contains("Armor Shop"))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getArmorShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open consumables shop
			else if (buttonName.contains("Consumables Shop"))
				if (arenaInstance.hasNormal())
					player.openInventory(arenaInstance.getConsumeShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("normalShopError")));

			// Open custom shop
			else if (buttonName.contains("Custom Shop"))
				if (arenaInstance.hasCustom())
					player.openInventory(arenaInstance.getCustomShop());
				else player.sendMessage(Utils.notify("&c" + language.getString("customShopError")));
		}

		// Mock custom shop for an arena
		else if (title.contains("Custom Shop:")) {
			String name = title.substring(19);
			Arena arenaInstance = game.arenas.stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.getName().equals(name)).collect(Collectors.toList()).get(0);
			if (buttonName.contains("EXIT"))
				player.openInventory(inv.createArenaInfoInventory(arenaInstance));
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
				gamer.addGems(cost / 10);
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
				player.openInventory(inv.createPlayerKitsInventory(name, player.getName()));
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
				player.openInventory(inv.createPlayerStatsInventory(name));
				return;
			}

			plugin.savePlayerData();
			player.openInventory(inv.createPlayerKitsInventory(name, name));
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
					game.createBoard(gamer);
				}
				player.closeInventory();
				return;
			}

			// Ignore spectators from here on out
			if (gamer.isSpectating())
				return;

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
				game.createBoard(gamer);
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
				game.createBoard(gamer);
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
				game.createBoard(gamer);
			}

			// No kit
			if (kit.equals("None")) {
				gamer.setKit("None");
				player.sendMessage(Utils.notify("&a" + language.getString("kitSelect")));
				player.closeInventory();
				game.createBoard(gamer);
			}

			// Close inventory
			if (buttonName.contains("EXIT"))
				player.closeInventory();
		}

		// Stats menu for an arena
		else if (title.contains("Info")) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();

			if (buttonName.contains("Custom Shop Inventory"))
				player.openInventory(game.arenas.get(meta.getInteger1()).getMockCustomShop());

			else if (buttonName.contains("Allowed Kits"))
				player.openInventory(inv.createMockAllowedKitsInventory(meta.getInteger1()));
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
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			Arena arena = game.arenas.get(meta.getInteger1());
			if (arena.getName() == null)
				game.arenas.set(meta.getInteger1(), null);
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
