package me.theguyhere.villagerdefense;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.events.GameEndEvent;
import me.theguyhere.villagerdefense.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.Tasks;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.game.models.kits.Kit;
import me.theguyhere.villagerdefense.game.models.players.PlayerNotFoundException;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.tools.Utils;
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
import java.util.stream.Collectors;

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
					player.sendMessage(Utils.notify(language.getString("commandError")));
				else Utils.debugError("Invalid command. Use 'vd help' for more info.",0);
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
					player.sendMessage(Utils.notify(language.getString("permissionError")));
					return true;
				}

				player.openInventory(Inventories.createArenasInventory());
				return true;
			}
			
			// Redirects to wiki for help
			if (args[0].equalsIgnoreCase("help")) {
				if (player != null) {
					player.sendMessage(Utils.notify(language.getString("info")));
					TextComponent message = new TextComponent("Visit the wiki!");
					message.setBold(true);
					message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
							"https://github.com/Theguyhere0/villager-defense-minigame/wiki"));
					player.spigot().sendMessage(message);

				} else Utils.debugInfo(
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
						player.sendMessage(Utils.notify(
								String.format(Objects.requireNonNull(language.getString("noStats")), args[1])));
					} catch (Exception e) {
						Utils.debugError("The language file is missing the attribute 'noStats'!", 1);
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
					arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.filter(arena1 -> arena1.hasPlayer(player))
							.collect(Collectors.toList()).get(0);
					gamer = arena.getPlayer(player);
				} catch (Exception err) {
					player.sendMessage(Utils.notify(language.getString("inGameError")));
					return true;
				}

				// Check if player owns the phantom kit if late arrival is not on
				if (!playerData.getBoolean(player.getName() + ".kits." + Kit.phantom().getName()) &&
						!arena.hasLateArrival()) {
					player.sendMessage(Utils.notify(language.getString("phantomOwnError")));
					return true;
				}

				// Check if arena is not ending
				if (arena.getStatus() == ArenaStatus.ENDING) {
					player.sendMessage(Utils.notify(language.getString("phantomArenaError")));
					return true;
				}

				// Check for useful phantom use
				if (gamer.getStatus() != PlayerStatus.SPECTATOR) {
					player.sendMessage(Utils.notify(language.getString("phantomPlayerError")));
					return true;
				}

				// Check for arena capacity if late arrival is on
				if (arena.hasLateArrival() && arena.getActiveCount() >= arena.getMaxPlayers()) {
					player.sendMessage(Utils.notify(language.getString("maxCapacity")));
					return true;
				}

				// Let player join using phantom kit
				Utils.teleAdventure(player, arena.getPlayerSpawn());
				gamer.setStatus(PlayerStatus.ALIVE);
				arena.getTask().giveItems(gamer);
				Game.createBoard(gamer);
				gamer.setJoinedWave(arena.getCurrentWave());
				gamer.setKit(Kit.phantom());
				player.closeInventory();
				return true;
			}

			// Change crystal balance
			if (args[0].equalsIgnoreCase("crystals")) {
				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.crystals")) {
					player.sendMessage(Utils.notify(language.getString("permissionError")));
					return true;
				}

				// Check for valid command format
				if (args.length != 3) {
					if (player != null)
						player.sendMessage(Utils.notify("&cCommand format: /vd crystals [player] [change amount]"));
					else Utils.debugError("Command format: 'vd crystals [player] [change amount]'", 0);
					return true;
				}

				// Check for valid player
				if (!plugin.getPlayerData().contains(args[1])) {
					if (player != null)
						player.sendMessage(Utils.notify("&cInvalid player!"));
					else Utils.debugError("Invalid player!", 0);
					return true;
				}

				// Check for valid amount
				try {
					int amount = Integer.parseInt(args[2]);
					plugin.getPlayerData().set(args[1] + ".crystalBalance",
							Math.max(plugin.getPlayerData().getInt(args[1] + ".crystalBalance") + amount, 0));
					plugin.savePlayerData();
					if (player != null)
						player.sendMessage(Utils.notify("&a" + args[1] +"'s crystal balance was set to " +
								plugin.getPlayerData().getInt(args[1] + ".crystalBalance")));
					else Utils.debugInfo(args[1] +"'s crystal balance was set to " +
							plugin.getPlayerData().getInt(args[1] + ".crystalBalance"), 0);
					return true;
				} catch (Exception e) {
					if (player != null)
						player.sendMessage(Utils.notify("&cAmount must be an integer!"));
					else Utils.debugError("Amount must be an integer!", 0);
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
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					Arena arena;
					VDPlayer gamer;

					// Attempt to get arena and player
					try {
						arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
								.filter(arena1 -> arena1.hasPlayer(player))
								.collect(Collectors.toList()).get(0);
						gamer = arena.getPlayer(player);
					} catch (Exception e) {
						player.sendMessage(Utils.notify(language.getString("forceStartError1")));
						return true;
					}

					// Check if player is an active player
					if (!arena.getActives().contains(gamer)) {
						player.sendMessage(Utils.notify(language.getString("forceStartError2")));
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						player.sendMessage(Utils.notify(language.getString("forceStartError3")));
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Bring game to quick start if not already
					if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
							!scheduler.isQueued(tasks.get(task.sec10))) {
						player.sendMessage(Utils.notify(language.getString("forceStartError4")));
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
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);

					// Check if this arena exists
					if (Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.noneMatch(arena -> arena.getName().equals(name.toString()))) {
						if (player != null)
							player.sendMessage(Utils.notify("&cNo arena with this name exists!"));
						else Utils.debugError("No arena with this name exists!", 0);
						return true;
					}

					Arena arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.filter(arena1 -> arena1.hasPlayer(player))
							.collect(Collectors.toList()).get(0);

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						if (player != null)
							player.sendMessage(Utils.notify(language.getString("forceStartError3")));
						else Utils.debugError("The arena already has a game in progress!", 0);
						return true;
					}

					// Check if there is at least 1 player
					if (arena.getActiveCount() == 0) {
						if (player != null)
							player.sendMessage(Utils.notify("&cThe arena needs at least 1 player to start!"));
						else Utils.debugError("The arena needs at least 1 player to start!", 0);
						return true;
					}

					Tasks task = arena.getTask();
					Map<Runnable, Integer> tasks = task.getTasks();
					BukkitScheduler scheduler = Bukkit.getScheduler();

					// Bring game to quick start if not already
					if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
							!scheduler.isQueued(tasks.get(task.sec10))) {
						if (player != null)
							player.sendMessage(Utils.notify(language.getString("forceStartError4")));
						else Utils.debugError("The game is already starting soon!", 0);
						return true;
					} else {
						// Remove all tasks
						tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
						tasks.clear();

						// Schedule accelerated countdown tasks
						task.full10.run();
						tasks.put(task.full10, 0); // Dummy task id to note that quick start condition was hit
						tasks.put(task.sec5,
								scheduler.scheduleSyncDelayedTask(plugin, task.sec5, Utils.secondsToTicks(5)));
						tasks.put(task.start,
								scheduler.scheduleSyncDelayedTask(plugin, task.start, Utils.secondsToTicks(10)));

						// Notify console
						Utils.debugInfo("Arena " + arena.getArena() + " was force started.", 1);
					}
				}

				return true;
			}

			// Force end
			if (args[0].equalsIgnoreCase("end")) {
				// Delay current arena
				if (args.length == 1) {
					// Check for player executing command
					if (player == null) {
						sender.sendMessage("Bad console!");
						return true;
					}

					// Check for permission to use the command
					if (!player.hasPermission("vd.admin")) {
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					Arena arena;

					// Attempt to get arena
					try {
						arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
								.filter(arena1 -> arena1.hasPlayer(player))
								.collect(Collectors.toList()).get(0);
					} catch (Exception e) {
						player.sendMessage(Utils.notify(language.getString("forceStartError1")));
						return true;
					}

					// Check if arena has a game in progress
					if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
						player.sendMessage(Utils.notify("&cNo game to end!"));
						return true;
					}

					// Check if game is about to end
					if (arena.getStatus() == ArenaStatus.ENDING) {
						player.sendMessage(Utils.notify("&cGame about to end!"));
						return true;
					}

					// Force end
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

					// Notify console
					Utils.debugInfo("Arena " + arena.getArena() + " was force ended.", 1);
				}

				else {
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);

					// Check if this arena exists
					if (Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.noneMatch(arena -> arena.getName().equals(name.toString()))) {
						if (player != null)
							player.sendMessage(Utils.notify("&cNo arena with this name exists!"));
						else Utils.debugError("No arena with this name exists!", 0);
						return true;
					}

					Arena arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.filter(arena1 -> arena1.hasPlayer(player))
							.collect(Collectors.toList()).get(0);

					// Check if arena has a game in progress
					if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
						if (player != null)
							player.sendMessage(Utils.notify("&cNo game to end!"));
						else Utils.debugError("No game to end!", 0);
						return true;
					}

					// Check if game is about to end
					if (arena.getStatus() == ArenaStatus.ENDING) {
						if (player != null)
							player.sendMessage(Utils.notify("&cGame about to end!"));
						else Utils.debugError("Game about to end!", 0);
						return true;
					}

					// Force end
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
							Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

					// Notify console
					Utils.debugInfo("Arena " + arena.getArena() + " was force ended.", 1);

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
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					Arena arena;

					// Attempt to get arena
					try {
						arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
								.filter(arena1 -> arena1.hasPlayer(player))
								.collect(Collectors.toList()).get(0);
					} catch (Exception e) {
						player.sendMessage(Utils.notify(language.getString("forceStartError1")));
						return true;
					}

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						player.sendMessage(Utils.notify(language.getString("forceStartError3")));
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
						player.sendMessage(Utils.notify(language.getString("permissionError")));
						return true;
					}

					StringBuilder name = new StringBuilder(args[1]);
					for (int i = 0; i < args.length - 2; i++)
						name.append(" ").append(args[i + 2]);

					// Check if this arena exists
					if (Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.noneMatch(arena -> arena.getName().equals(name.toString()))) {
						if (player != null)
							player.sendMessage(Utils.notify("&cNo arena with this name exists!"));
						else Utils.debugError("No arena with this name exists!", 0);
						return true;
					}

					Arena arena = Arrays.stream(Game.arenas).filter(Objects::nonNull)
							.filter(arena1 -> arena1.hasPlayer(player))
							.collect(Collectors.toList()).get(0);

					// Check if arena already started
					if (arena.getStatus() != ArenaStatus.WAITING) {
						if (player != null)
							player.sendMessage(Utils.notify(language.getString("forceStartError3")));
						else Utils.debugError("The arena already has a game in progress!", 0);
						return true;
					}

					// Check if there is at least 1 player
					if (arena.getActiveCount() == 0) {
						if (player != null)
							player.sendMessage(Utils.notify("&cThe arena has no players!"));
						else Utils.debugError("The arena has no players!", 0);
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
					Utils.debugInfo("Arena " + arena.getArena() + " was delayed.", 1);
				}

				return true;
			}

			// Fix certain default files
			if (args[0].equalsIgnoreCase("fix")) {
				boolean fixed = false;

				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.admin")) {
					player.sendMessage(Utils.notify(language.getString("permissionError")));
					return true;
				}

				// Check for correct format
				if (args.length > 1) {
					if (player != null)
						player.sendMessage(Utils.notify("&cCommand format: /vd fix"));
					else Utils.debugError("Command format: 'vd fix'", 0);
					return true;
				}

				// Check if plugin.yml is outdated
				if (plugin.getConfig().getInt("version") < plugin.configVersion)
					if (player != null)
						player.sendMessage(Utils.notify("&cplugin.yml must be updated manually."));
					else Utils.debugError("plugin.yml must be updated manually.", 0);

				// Check if arenaData.yml is outdated
				if (plugin.getConfig().getInt("arenaData") < 4) {
					try {
						// Transfer portals
						Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("portal"))
								.getKeys(false).forEach(arenaID -> {
							Location location = Utils.getConfigLocation(plugin, "portal." + arenaID);
							plugin.getArenaData().set("portal." + arenaID, null);
							Utils.setConfigurationLocation(plugin, "a" + arenaID + ".portal", location);
						});
						plugin.getArenaData().set("portal", null);

						// Transfer arena boards
						Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("arenaBoard"))
								.getKeys(false).forEach(arenaID -> {
									Location location = Utils.getConfigLocation(plugin, "arenaBoard." + arenaID);
									plugin.getArenaData().set("arenaBoard." + arenaID, null);
									Utils.setConfigurationLocation(plugin, "a" + arenaID + ".arenaBoard", location);
								});
						plugin.getArenaData().set("arenaBoard", null);

						plugin.saveArenaData();

						// Reload portals
						Portal.refreshPortals();

						// Flip flag and update config.yml
						fixed = true;
						plugin.getConfig().set("arenaData", plugin.arenaDataVersion);
						plugin.saveConfig();

						// Notify
						if (player != null)
							player.sendMessage(Utils.notify("&aarenaData.yml has been automatically updated."));
						Utils.debugInfo("arenaData.yml has been automatically updated.", 0);
					} catch (Exception e) {
						if (player != null)
							player.sendMessage(Utils.notify("&carenaData.yml must be updated manually."));
						else Utils.debugError("arenaData.yml must be updated manually.", 0);
					}
				} else if (plugin.getConfig().getInt("arenaData") < plugin.arenaDataVersion) {
					if (player != null)
						player.sendMessage(Utils.notify("&carenaData.yml must be updated manually."));
					else Utils.debugError("arenaData.yml must be updated manually.", 0);
				}

				// Check if playerData.yml is outdated
				if (plugin.getConfig().getInt("playerData") < plugin.playerDataVersion)
					if (player != null)
						player.sendMessage(Utils.notify("&cplayerData.yml must be updated manually."));
					else Utils.debugError("playerData.yml must be updated manually.", 0);

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
						player.sendMessage(Utils.notify("&adefault.yml has been automatically updated."));
					Utils.debugInfo("default.yml has been automatically updated.", 0);
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
						player.sendMessage(Utils.notify("&aen_US.yml has been automatically updated. " +
								"Please restart the plugin."));
					Utils.debugInfo("en_US.yml has been automatically updated. Please restart the plugin.",
							0);
				}

				// Message to player depending on whether the command fixed anything
				if (!fixed)
					if (player != null)
						player.sendMessage(Utils.notify("There was nothing that could be updated automatically."));
					else Utils.debugInfo("There was nothing that could be updated automatically.", 0);

				return true;
			}

			// Change plugin debug level
			if (args[0].equalsIgnoreCase("debug")) {
				// Check for permission to use the command
				if (player != null && !player.hasPermission("vd.admin")) {
					player.sendMessage(Utils.notify(language.getString("permissionError")));
					return true;
				}

				// Check for correct format
				if (args.length != 2) {
					if (player != null)
						player.sendMessage(Utils.notify("&cCommand format: /vd debug [debug level (0-3)]"));
					else Utils.debugError("Command format: /vd debug [debug level (0-3)]", 0);
					return true;
				}

				// Set debug level
				try {
					Main.setDebugLevel(Integer.parseInt(args[1]));
				} catch (Exception e) {
					if (player != null)
						player.sendMessage(Utils.notify("&cCommand format: /vd debug [debug level (0-3)]"));
					else Utils.debugError("Command format: /vd debug [debug level (0-3)]", 0);
					return true;
				}

				// Notify
				if (player != null)
					player.sendMessage(Utils.notify("&aDebug level set to " + args[1] + "."));
				else Utils.debugInfo("Debug level set to " + args[1] + ".", 0);

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
				if (Arrays.stream(Game.arenas).filter(Objects::nonNull).noneMatch(arena -> arena.hasPlayer(player))) {
					player.sendMessage(Utils.notify(language.getString("leaveError")));
					return true;
				}

				// Check for player in an active game
				if (Arrays.stream(Game.arenas).filter(Objects::nonNull)
						.filter(arena -> arena.getStatus() == ArenaStatus.ACTIVE)
						.noneMatch(arena -> arena.hasPlayer(player))) {
					player.sendMessage(Utils.notify(language.getString("suicideActiveError")));
					return true;
				}

				// Check for alive player
				try {
					if (Arrays.stream(Game.arenas).filter(Objects::nonNull).filter(arena -> arena.hasPlayer(player))
							.collect(Collectors.toList()).get(0).getPlayer(player).getStatus() != PlayerStatus.ALIVE) {
						player.sendMessage(Utils.notify(language.getString("suicideError")));
						return true;
					}
				} catch (PlayerNotFoundException err) {
					player.sendMessage(Utils.notify(language.getString("suicideError")));
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
				player.sendMessage(Utils.notify(language.getString("commandError")));
			else Utils.debugError("Invalid command. Use 'vd help' for more info.", 0);
			return true;
		}
		return false;
	}
}
