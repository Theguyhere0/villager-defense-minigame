package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.GUI.Inventories;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Tasks;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class Commands implements CommandExecutor {
	Main plugin;

	public Commands(Main plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("vd")) {
			FileConfiguration language = plugin.getLanguageData();

			Player player;

			if (sender instanceof Player)
				player = (Player) sender;
			else player = null;

			// No arguments
			if (args.length == 0) {
				if (player != null)
					PlayerManager.notify(player, language.getString("commandError"));
				else CommunicationManager.debugError("Invalid command. Use 'vd help' for more info.",0);
				return true;
			}

			// Admin panel
			if (args[0].equalsIgnoreCase("admin")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				// Check for permission to use the command
				if (!player.hasPermission("vd.use")) {
					PlayerManager.notify(player, language.getString("permissionError"));
					return true;
				}

				player.openInventory(Inventories.createArenasInventory());
				return true;
			}
			
			// Redirects to wiki for help
			if (args[0].equalsIgnoreCase("help")) {
				if (player != null) {
					PlayerManager.notify(player, language.getString("info"));
					TextComponent message = new TextComponent("Visit the wiki!");
					message.setBold(true);
					message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
							"https://github.com/Theguyhere0/villager-defense-minigame/wiki"));
					player.spigot().sendMessage(message);

				} else CommunicationManager.debugInfo(
						"Visit the wiki: https://github.com/Theguyhere0/villager-defense-minigame/wiki",
						0);
				return true;
			}
			
			// Player leaves a game
			if (args[0].equalsIgnoreCase("leave")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));
				return true;
			}

			// Player checks stats
			if (args[0].equalsIgnoreCase("stats")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				if (args.length == 1)
					player.openInventory(Inventories.createPlayerStatsInventory(plugin, player.getName()));
				else if (plugin.getPlayerData().contains(args[1]))
					player.openInventory(Inventories.createPlayerStatsInventory(plugin, args[1]));
				else {
					try {
						PlayerManager.notify(player, 
								String.format(Objects.requireNonNull(language.getString("noStats")), args[1]));
					} catch (Exception e) {
						CommunicationManager.debugError("The language file is missing the attribute 'noStats'!", 1);
					}
				}
				return true;
			}

			// Player checks kits
			if (args[0].equalsIgnoreCase("kits")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				player.openInventory(Inventories.createPlayerKitsInventory(plugin, player.getName(), player.getName()));
				return true;
			}

			// Player joins as phantom
			if (args[0].equalsIgnoreCase("join")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				FileConfiguration playerData = plugin.getPlayerData();
				Arena arena;
				VDPlayer gamer;

				// Attempt to get arena and player
				try {
					arena = GameManager.getArena(player);
					gamer = arena.getPlayer(player);
				} catch (Exception err) {
					PlayerManager.notify(player, language.getString("inGameError"));
					return true;
				}

				// Check if player owns the phantom kit if late arrival is not on
				if (!playerData.getBoolean(player.getName() + ".kits." + Kit.phantom().getName()) &&
						!arena.hasLateArrival()) {
					PlayerManager.notify(player, language.getString("phantomOwnError"));
					return true;
				}

				// Check if arena is not ending
				if (arena.getStatus() == ArenaStatus.ENDING) {
					PlayerManager.notify(player, language.getString("phantomArenaError"));
					return true;
				}

				// Check for useful phantom use
				if (gamer.getStatus() != PlayerStatus.SPECTATOR) {
					PlayerManager.notify(player, language.getString("phantomPlayerError"));
					return true;
				}

				// Check for arena capacity if late arrival is on
				if (arena.hasLateArrival() && arena.getActiveCount() >= arena.getMaxPlayers()) {
					PlayerManager.notify(player, language.getString("maxCapacity"));
					return true;
				}

				// Let player join using phantom kit
				PlayerManager.teleAdventure(player, arena.getPlayerSpawn().getLocation());
				gamer.setStatus(PlayerStatus.ALIVE);
				arena.getTask().giveItems(gamer);
				GameManager.createBoard(gamer);
				gamer.setJoinedWave(arena.getCurrentWave());
				gamer.setKit(Kit.phantom());
				player.closeInventory();
				return true;
			}

			// Change crystal balance
			if (args[0].equalsIgnoreCase("crystals")) {
				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.crystals")) {
					PlayerManager.notify(player, language.getString("permissionError"));
					return true;
				}

				// Check for valid command format
				if (args.length != 3) {
					if (player != null)
						PlayerManager.notify(player, "&cCommand format: /vd crystals [player] [change amount]");
					else CommunicationManager.debugError("Command format: 'vd crystals [player] [change amount]'", 0);
					return true;
				}

				// Check for valid player
				if (!plugin.getPlayerData().contains(args[1])) {
					if (player != null)
						PlayerManager.notify(player, "&cInvalid player!");
					else CommunicationManager.debugError("Invalid player!", 0);
					return true;
				}

				// Check for valid amount
				try {
					int amount = Integer.parseInt(args[2]);
					plugin.getPlayerData().set(args[1] + ".crystalBalance",
							Math.max(plugin.getPlayerData().getInt(args[1] + ".crystalBalance") + amount, 0));
					plugin.savePlayerData();
					if (player != null)
						PlayerManager.notify(player, "&a" + args[1] +"'s crystal balance was set to " +
								plugin.getPlayerData().getInt(args[1] + ".crystalBalance"));
					else CommunicationManager.debugInfo(args[1] +"'s crystal balance was set to " +
							plugin.getPlayerData().getInt(args[1] + ".crystalBalance"), 0);
					return true;
				} catch (Exception e) {
					if (player != null)
						PlayerManager.notify(player, "&cAmount must be an integer!");
					else CommunicationManager.debugError("Amount must be an integer!", 0);
					return true;
				}
			}

			// Force start
			if (args[0].equalsIgnoreCase("start")) {
				// Start current arena
				if (args.length == 1) {
					// Check for player executing command
					if (player == null) {
						sender.sendMessage("Bad console!");
						return true;
					}

					// Check for permission to use the command
					if (!player.hasPermission("vd.start")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					Arena arena;
					VDPlayer gamer;

					// Attempt to get arena and player
					try {
						arena = GameManager.getArena(player);
						gamer = arena.getPlayer(player);
					} catch (Exception e) {
						PlayerManager.notify(player, language.getString("forceStartError1"));
						return true;
					}

					// Check if player is an active player
					if (!arena.getActives().contains(gamer)) {
						PlayerManager.notify(player, language.getString("forceStartError2"));
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						PlayerManager.notify(player, language.getString("forceStartError3"));
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Bring game to quick start if not already
					if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
							!scheduler.isQueued(tasks.get(task.sec10))) {
						PlayerManager.notify(player, language.getString("forceStartError4"));
						return true;
					} else {
						// Remove all tasks
						tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
						tasks.clear();

						// Schedule accelerated countdown tasks
						task.sec10.run();
						tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
						tasks.put(task.sec5,
								scheduler.scheduleSyncDelayedTask(plugin, task.sec5, Utils.secondsToTicks(5)));
						tasks.put(task.start,
								scheduler.scheduleSyncDelayedTask(plugin, task.start, Utils.secondsToTicks(10)));
					}
				}

				// Start specific arena
				else {
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);
					Arena arena;

					// Check if this arena exists
					try {
						arena = GameManager.getArena(name.toString());
					} catch (Exception e) {
						if (player != null)
							PlayerManager.notify(player, "&cNo arena with this name exists!");
						else CommunicationManager.debugError("No arena with this name exists!", 0);
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						if (player != null)
							PlayerManager.notify(player, language.getString("forceStartError3"));
						else CommunicationManager.debugError("The arena already has a game in progress!", 0);
						return true;
					}

					// Check if there is at least 1 player
					if (arena.getActiveCount() == 0) {
						if (player != null)
							PlayerManager.notify(player, "&cThe arena needs at least 1 player to start!");
						else CommunicationManager.debugError("The arena needs at least 1 player to start!", 0);
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Bring game to quick start if not already
					if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
							!scheduler.isQueued(tasks.get(task.sec10))) {
						if (player != null)
							PlayerManager.notify(player, language.getString("forceStartError4"));
						else CommunicationManager.debugError("The game is already starting soon!", 0);
						return true;
					} else {
						// Remove all tasks
						tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
						tasks.clear();

						// Schedule accelerated countdown tasks
						task.sec10.run();
						tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
						tasks.put(task.sec5,
								scheduler.scheduleSyncDelayedTask(plugin, task.sec5, Utils.secondsToTicks(5)));
						tasks.put(task.start,
								scheduler.scheduleSyncDelayedTask(plugin, task.start, Utils.secondsToTicks(10)));

						// Notify console
						CommunicationManager.debugInfo("Arena " + arena.getArena() + " was force started.", 1);
					}
				}

				return true;
			}

			// Force end
			if (args[0].equalsIgnoreCase("end")) {
				// End current arena
				if (args.length == 1) {
					// Check for player executing command
					if (player == null) {
						sender.sendMessage("Bad console!");
						return true;
					}

					// Check for permission to use the command
					if (!player.hasPermission("vd.admin")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					Arena arena;

					// Attempt to get arena
					try {
						arena = GameManager.getArena(player);
					} catch (Exception e) {
						PlayerManager.notify(player, language.getString("forceStartError1"));
						return true;
					}

					// Check if arena has a game in progress
					if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
						PlayerManager.notify(player, "&cNo game to end!");
						return true;
					}

					// Check if game is about to end
					if (arena.getStatus() == ArenaStatus.ENDING) {
						PlayerManager.notify(player, "&cGame about to end!");
						return true;
					}

					// Force end
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

					// Notify console
					CommunicationManager.debugInfo("Arena " + arena.getArena() + " was force ended.", 1);
				}

				else {
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);
					Arena arena;

					// Check if this arena exists
					try {
						arena = GameManager.getArena(name.toString());
					} catch (Exception e) {
						if (player != null)
							PlayerManager.notify(player, "&cNo arena with this name exists!");
						else CommunicationManager.debugError("No arena with this name exists!", 0);
						return true;
					}

					// Check if arena has a game in progress
					if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
						if (player != null)
							PlayerManager.notify(player, "&cNo game to end!");
						else CommunicationManager.debugError("No game to end!", 0);
						return true;
					}

					// Check if game is about to end
					if (arena.getStatus() == ArenaStatus.ENDING) {
						if (player != null)
							PlayerManager.notify(player, "&cGame about to end!");
						else CommunicationManager.debugError("Game about to end!", 0);
						return true;
					}

					// Force end
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

					// Notify console
					CommunicationManager.debugInfo("Arena " + arena.getArena() + " was force ended.", 1);

					return true;
				}
			}

			// Force delay start
			if (args[0].equalsIgnoreCase("delay")) {
				// Delay current arena
				if (args.length == 1) {
					// Check for player executing command
					if (player == null) {
						sender.sendMessage("Bad console!");
						return true;
					}

					// Check for permission to use the command
					if (!player.hasPermission("vd.start")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					Arena arena;

					// Attempt to get arena
					try {
						arena = GameManager.getArena(player);
					} catch (Exception e) {
						PlayerManager.notify(player, language.getString("forceStartError1"));
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						PlayerManager.notify(player, language.getString("forceStartError3"));
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Remove all tasks
					tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
					tasks.clear();

					// Reschedule countdown tasks
					task.min2.run();
					tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(plugin, task.min1,
							Utils.secondsToTicks(Utils.minutesToSeconds(1))));
					tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(plugin, task.sec30,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 30)));
					tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 10)));
					tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 5)));
					tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start,
							Utils.secondsToTicks(Utils.minutesToSeconds(2))));
				}

				// Delay specific arena
				else {
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						PlayerManager.notify(player, language.getString("permissionError"));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);
					Arena arena;

					// Check if this arena exists
					try {
						arena = GameManager.getArena(name.toString());
					} catch (Exception e) {
						if (player != null)
							PlayerManager.notify(player, "&cNo arena with this name exists!");
						else CommunicationManager.debugError("No arena with this name exists!", 0);
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						if (player != null)
							PlayerManager.notify(player, language.getString("forceStartError3"));
						else CommunicationManager.debugError("The arena already has a game in progress!",
								0);
						return true;
					}

					// Check if there is at least 1 player
					if (arena.getActiveCount() == 0) {
						if (player != null)
							PlayerManager.notify(player, "&cThe arena has no players!");
						else CommunicationManager.debugError("The arena has no players!", 0);
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Remove all tasks
					tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
					tasks.clear();

					// Reschedule countdown tasks
					task.min2.run();
					tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(plugin, task.min1,
							Utils.secondsToTicks(Utils.minutesToSeconds(1))));
					tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(plugin, task.sec30,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 30)));
					tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 10)));
					tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5,
							Utils.secondsToTicks(Utils.minutesToSeconds(2) - 5)));
					tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start,
							Utils.secondsToTicks(Utils.minutesToSeconds(2))));

					// Notify console
					CommunicationManager.debugInfo("Arena " + arena.getArena() + " was delayed.", 1);
				}

				return true;
			}

			// Fix certain default files
			if (args[0].equalsIgnoreCase("fix")) {
				boolean fixed = false;

				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.admin")) {
					PlayerManager.notify(player, language.getString("permissionError"));
					return true;
				}

				// Check for correct format
				if (args.length > 1) {
					if (player != null)
						PlayerManager.notify(player, "&cCommand format: /vd fix");
					else CommunicationManager.debugError("Command format: 'vd fix'", 0);
					return true;
				}

				// Check if plugin.yml is outdated
				if (plugin.getConfig().getInt("version") < plugin.configVersion)
					if (player != null)
						PlayerManager.notify(player, "&cplugin.yml must be updated manually.");
					else CommunicationManager.debugError("plugin.yml must be updated manually.", 0);

				// Check if arenaData.yml is outdated
				int arenaDataVersion = plugin.getConfig().getInt("arenaData");
				if (arenaDataVersion < 4) {
					try {
						// Transfer portals
						Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("portal"))
								.getKeys(false).forEach(arenaID -> {
									Location location = DataManager.getConfigLocation(plugin, "portal." + arenaID);
									DataManager.setConfigurationLocation(plugin, "a" + arenaID + ".portal", location);
									plugin.getArenaData().set("portal." + arenaID, null);
								});
						plugin.getArenaData().set("portal", null);

						// Transfer arena boards
						Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("arenaBoard"))
								.getKeys(false).forEach(arenaID -> {
									Location location = DataManager.getConfigLocation(plugin, "arenaBoard." + arenaID);
									DataManager.setConfigurationLocation(plugin, "a" + arenaID + ".arenaBoard", location);
									plugin.getArenaData().set("arenaBoard." + arenaID, null);
								});
						plugin.getArenaData().set("arenaBoard", null);

						plugin.saveArenaData();

						// Reload portals
						GameManager.refreshPortals();

						// Flip flag and update config.yml
						fixed = true;
						plugin.getConfig().set("arenaData", 4);
						plugin.saveConfig();

						// Notify
						if (player != null)
							PlayerManager.notify(player,
									"&aarenaData.yml has been automatically updated to version 4.");
						CommunicationManager.debugInfo(
								"arenaData.yml has been automatically updated to version 4.", 0);
					} catch (Exception e) {
						if (player != null)
							PlayerManager.notify(player, "&carenaData.yml must be updated manually.");
						else CommunicationManager.debugError("arenaData.yml must be updated manually.",
								0);
					}
				}
				else if (arenaDataVersion < plugin.arenaDataVersion) {
					if (player != null)
						PlayerManager.notify(player, "&carenaData.yml must be updated manually.");
					else CommunicationManager.debugError("arenaData.yml must be updated manually.", 0);
				}

				// Check if playerData.yml is outdated
				if (plugin.getConfig().getInt("playerData") < plugin.playerDataVersion)
					if (player != null)
						PlayerManager.notify(player, "&cplayerData.yml must be updated manually.");
					else CommunicationManager.debugError("playerData.yml must be updated manually.", 0);

				// Update default spawn table
				if (plugin.getConfig().getInt("spawnTableStructure") < plugin.spawnTableVersion ||
						plugin.getConfig().getInt("spawnTableDefault") < plugin.defaultSpawnVersion) {
					// Flip flag
					fixed = true;

					// Fix
					plugin.saveResource("default.yml", true);
					plugin.getConfig().set("spawnTableStructure", plugin.spawnTableVersion);
					plugin.getConfig().set("spawnTableDefault", plugin.defaultSpawnVersion);
					plugin.saveConfig();

					// Notify
					if (player != null)
						PlayerManager.notify(player, "&adefault.yml has been automatically updated.");
					CommunicationManager.debugInfo("default.yml has been automatically updated.", 0);
				}

				// Update default language file
				if (plugin.getConfig().getInt("languageFile") < plugin.languageFileVersion) {
					// Flip flag
					fixed = true;

					// Fix
					plugin.saveResource("languages/en_US.yml", true);
					plugin.getConfig().set("languageFile", plugin.languageFileVersion);
					plugin.saveConfig();

					// Notify
					if (player != null)
						PlayerManager.notify(player, "&aen_US.yml has been automatically updated. " +
								"Please restart the plugin.");
					CommunicationManager.debugInfo("en_US.yml has been automatically updated. " +
									"Please restart the plugin.",
							0);
				}

				// Message to player depending on whether the command fixed anything
				if (!fixed)
					if (player != null)
						PlayerManager.notify(player, "There was nothing that could be updated automatically.");
					else CommunicationManager.debugInfo("There was nothing that could be updated automatically.",
							0);

				return true;
			}

			// Change plugin debug level
			if (args[0].equalsIgnoreCase("debug")) {
				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.admin")) {
					PlayerManager.notify(player, language.getString("permissionError"));
					return true;
				}

				// Check for correct format
				if (args.length != 2) {
					if (player != null)
						PlayerManager.notify(player, "&cCommand format: /vd debug [debug level (0-3)]");
					else CommunicationManager.debugError("Command format: /vd debug [debug level (0-3)]", 0);
					return true;
				}

				// Set debug level
				try {
					CommunicationManager.setDebugLevel(Integer.parseInt(args[1]));
				} catch (Exception e) {
					if (player != null)
						PlayerManager.notify(player, "&cCommand format: /vd debug [debug level (0-3)]");
					else CommunicationManager.debugError("Command format: /vd debug [debug level (0-3)]", 0);
					return true;
				}

				// Notify
				if (player != null)
					PlayerManager.notify(player, "&aDebug level set to " + args[1] + ".");
				else CommunicationManager.debugInfo("Debug level set to " + args[1] + ".", 0);

				return true;
			}

			// Player kills themselves
			if (args[0].equalsIgnoreCase("die")) {
				// Check for player executing command
				if (player == null) {
					sender.sendMessage("Bad console!");
					return true;
				}

				// Check for player in a game
				if (!GameManager.checkPlayer(player)) {
					PlayerManager.notify(player, language.getString("leaveError"));
					return true;
				}

				// Check for player in an active game
				if (Arrays.stream(GameManager.getArenas()).filter(Objects::nonNull)
						.filter(arena -> arena.getStatus() == ArenaStatus.ACTIVE)
						.noneMatch(arena -> arena.hasPlayer(player))) {
					PlayerManager.notify(player, language.getString("suicideActiveError"));
					return true;
				}

				// Check for alive player
				try {
					if (GameManager.getArena(player).getPlayer(player).getStatus() != PlayerStatus.ALIVE) {
						PlayerManager.notify(player, language.getString("suicideError"));
						return true;
					}
				} catch (PlayerNotFoundException err) {
					PlayerManager.notify(player, language.getString("suicideError"));
					return true;
				}

				// Create a player death and make sure it gets detected
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
						Bukkit.getPluginManager().callEvent(new EntityDamageEvent(player,
								EntityDamageEvent.DamageCause.SUICIDE, 99)));
				return true;
			}

			// No valid command sent
			if (player != null)
				PlayerManager.notify(player, language.getString("commandError"));
			else CommunicationManager.debugError("Invalid command. Use 'vd help' for more info.", 0);
			return true;
		}
		return false;
	}
}
