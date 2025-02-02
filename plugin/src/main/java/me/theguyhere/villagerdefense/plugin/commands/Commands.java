package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.game.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.exceptions.InvalidNameException;
import me.theguyhere.villagerdefense.plugin.entities.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.Tasks;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.visuals.Inventories;
import me.theguyhere.villagerdefense.plugin.data.listeners.ChatListener;
import me.theguyhere.villagerdefense.plugin.data.DataManager;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class Commands implements CommandExecutor {
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
							 String[] args) {
		try {
			// Check for "vd" as first argument
			if (!label.equalsIgnoreCase("vd"))
				return false;

			// Gather who sent command
			Player player;
			if (sender instanceof Player)
				player = (Player) sender;
			else player = null;

			// No additional arguments
			if (args.length == 0) {
				notifyCommandFailure(player, "/vd help", LanguageManager.errors.command);
				return true;
			}

			// Initialize some variables
			Arena arena;
			VDPlayer gamer;
			UUID id;
			StringBuilder name;
			Location location;
			String path;

			switch (args[0].toLowerCase()) {
				case "admin":
					// Admin panel
					if (args.length == 1) {
						// Check for player executing command
						if (player == null) {
							sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
							return true;
						}

						// Check for permission to use the command
						if (!player.hasPermission("vd.use")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						player.openInventory(Inventories.createMainMenu());
						return true;
					}

					// Admin commands
					else {
						switch (args[1].toLowerCase()) {
							case "lobby":
								// Incorrect format
								if (args.length != 3) {
									notifyCommandFailure(player,
											"/vd admin lobby [set, teleport, center, remove]",
											LanguageManager.messages.commandFormat);
									return true;
								}

								path = "lobby";
								location = DataManager.getConfigLocationNoRotation(path);

								// Display object menu options
								switch (args[2].toLowerCase()) {
									case "set":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										GameManager.saveLobby(player.getLocation());
										PlayerManager.notifySuccess(player, "Lobby set!");
										return true;

									case "teleport":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (location == null) {
											PlayerManager.notifyFailure(player, "No lobby to teleport to!");
											return true;
										}
										player.teleport(location);
										return true;

									case "center":
										if (location == null) {
											notifyFailure(player, "No lobby to center!");
											return true;
										}
										DataManager.centerConfigLocation(path);
										notifySuccess(player, "Lobby centered!");
										return true;

									case "remove":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (GameManager.getArenas().values().stream().filter(Objects::nonNull)
												.anyMatch(arenaInstance -> !arenaInstance.isClosed()))
											PlayerManager.notifyFailure(player,
													"All arenas must be closed to modify this!");
										else if (Main.getArenaData().contains("lobby"))
											player.openInventory(Inventories.createLobbyConfirmMenu());
										else PlayerManager.notifyFailure(player, "No lobby to remove!");
										return true;

									default:
										notifyCommandFailure(player,
												"/vd admin lobby [set, teleport, center, remove]",
												LanguageManager.messages.commandFormat);
										return true;
								}

							case "infoboard":
								// Incorrect format
								if (args.length == 2) {
									notifyCommandFailure(player,
											"/vd admin infoBoard [info board id, create] " +
													"[set, teleport, center, remove]",
											LanguageManager.messages.commandFormat);
									return true;
								}

								// Create new info board
								if (args[2].equalsIgnoreCase("create")) {
									// Check for player executing command
									if (player == null) {
										sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
										return true;
									}

									// Create new info board at location
									GameManager.setInfoBoard(player.getLocation(), GameManager.newInfoBoardID());
									PlayerManager.notifySuccess(player, "Info board set!");

									return true;
								}

								// Incorrect format or invalid ID
								if (args.length != 4) {
									notifyCommandFailure(player,
											"/vd admin infoBoard [info board id, create] " +
													"[set, teleport, center, remove]",
											LanguageManager.messages.commandFormat);
									return true;
								}
								int infoBoardID;
								try {
									infoBoardID = Integer.parseInt(args[2]);
								} catch (Exception e) {
									notifyCommandFailure(player,
											"/vd admin infoBoard [info board id, create] " +
													"[set, teleport, center, remove]",
											LanguageManager.messages.commandFormat);
									return true;
								}
								if (!Main.getArenaData().contains("infoBoard." + infoBoardID)) {
									notifyFailure(player, "Invalid info board id.");
									return true;
								}

								// Display object menu options
								path = "infoBoard." + infoBoardID;
								location = DataManager.getConfigLocationNoRotation(path);
								switch (args[3].toLowerCase()) {
									case "set":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										GameManager.setInfoBoard(player.getLocation(), infoBoardID);
										PlayerManager.notifySuccess(player, "Info board set!");
										return true;

									case "teleport":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (location == null) {
											PlayerManager.notifyFailure(player, "No info board to teleport to!");
											return true;
										}
										player.teleport(location);
										return true;

									case "center":
										if (location == null) {
											notifyFailure(player, "No info board to center!");
											return true;
										}
										GameManager.centerInfoBoard(infoBoardID);
										notifySuccess(player, "Info board centered!");
										return true;

									case "remove":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (Main.getArenaData().contains(path))
											player.openInventory(Inventories.createInfoBoardConfirmMenu(infoBoardID));
										else PlayerManager.notifyFailure(player, "No info board to remove!");
										return true;

									default:
										notifyCommandFailure(player,
												"/vd admin infoBoard [info board id, create] " +
														"[set, teleport, center, remove]",
												LanguageManager.messages.commandFormat);
										return true;
								}

							case "leaderboard":
								// Incorrect format
								if (args.length != 4) {
									notifyCommandFailure(player,
											"/vd admin leaderboard [leaderboard type] " +
													"[set, teleport, center, remove]",
											LanguageManager.messages.commandFormat);
									return true;
								}

								// Check for type validity
								String[] types = {"topBalance", "topKills", "topWave", "totalGems", "totalKills"};
								String type = args[2];
								if (Arrays.stream(types).noneMatch(realType -> realType.equals(type))) {
									notifyFailure(player, "Invalid leaderboard.");
									return true;
								}

								// Display object menu options
								path = "leaderboard." + type;
								location = DataManager.getConfigLocationNoRotation(path);
								switch (args[3].toLowerCase()) {
									case "set":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										GameManager.setLeaderboard(player.getLocation(), type);
										PlayerManager.notifySuccess(player, "Leaderboard set!");
										return true;

									case "teleport":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (location == null) {
											PlayerManager.notifyFailure(player, "No leaderboard to teleport to!");
											return true;
										}
										player.teleport(location);
										return true;

									case "center":
										if (location == null) {
											notifyFailure(player, "No leaderboard to center!");
											return true;
										}
										GameManager.centerLeaderboard(type);
										notifySuccess(player, "Leaderboard centered!");
										return true;

									case "remove":
										// Check for player executing command
										if (player == null) {
											sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
											return true;
										}

										if (Main.getArenaData().contains(path))
											switch (type) {
												case "topBalance":
													player.openInventory(Inventories.createTopBalanceConfirmMenu());
													break;
												case "topKills":
													player.openInventory(Inventories.createTopKillsConfirmMenu());
													break;
												case "topWave":
													player.openInventory(Inventories.createTopWaveConfirmMenu());
													break;
												case "totalGems":
													player.openInventory(Inventories.createTotalGemsConfirmMenu());
													break;
												case "totalKills":
													player.openInventory(Inventories.createTotalKillsConfirmMenu());
													break;
											}
										else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
										return true;

									default:
										notifyCommandFailure(player,
												"/vd admin leaderboard [leaderboard type] " +
														"[set, teleport, center, remove]",
												LanguageManager.messages.commandFormat);
										return true;
								}

							case "arena":
								// Incorrect format
								if (args.length < 4) {
									notifyCommandFailure(player, "/vd admin arena [operation]-" +
													"[value] [arena name]",
											LanguageManager.messages.commandFormat);
									return true;
								}

								// Get arena name
								name = new StringBuilder(args[3]);
								for (int i = 0; i < args.length - 4; i++)
									name.append(" ").append(args[i + 4]);

								// Check if this arena exists
								try {
									arena = GameManager.getArena(name.toString());
								} catch (ArenaNotFoundException e) {
									notifyFailure(player, LanguageManager.errors.noArena);
									return true;
								}

								// Close or open arena, otherwise check if arena is closed
								if (args[2].equalsIgnoreCase("close")) {
									// Check if arena is already closed
									if (arena.isClosed()) {
										notifyFailure(player, "Arena is already closed!");
										return true;
									}

									// Close arena
									arena.setClosed(true);

									// Notify console and possibly player
									notifySuccess(player, arena.getName() +  " was closed.");

									return true;
								}
								else if (args[2].equalsIgnoreCase("open")) {
									// Check if arena is already open
									if (!arena.isClosed()) {
										notifyFailure(player, "Arena is already open!");
										return true;
									}

									// No lobby
									if (!Main.getArenaData().contains("lobby")) {
										notifyFailure(player, "Arena cannot open without a lobby!");
										return true;
									}

									// No arena portal
									if (arena.getPortalLocation() == null) {
										notifyFailure(player, "Arena cannot open without a portal!");
										return true;
									}

									// No player spawn
									if (arena.getPlayerSpawn() == null) {
										notifyFailure(player, "Arena cannot open without a player spawn!");
										return true;
									}

									// No monster spawn
									if (arena.getMonsterSpawns().isEmpty()) {
										notifyFailure(player, "Arena cannot open without a monster spawn!");
										return true;
									}

									// No villager spawn
									if (arena.getVillagerSpawns().isEmpty()) {
										notifyFailure(player, "Arena cannot open without a villager spawn!");
										return true;
									}

									// No shops
									if (!arena.hasCustom() && !arena.hasNormal()) {
										notifyFailure(player, "Arena cannot open without a shop!");
										return true;
									}

									// Invalid arena bounds
									if (arena.getCorner1() == null || arena.getCorner2() == null ||
											!Objects.equals(arena.getCorner1().getWorld(), arena.getCorner2().getWorld())) {
										notifyFailure(player, "Arena cannot open without valid arena bounds!");
										return true;
									}

									// Open arena
									arena.setClosed(false);

									// Notify console and possibly player
									notifySuccess(player, arena.getName() +  " was opened.");

									return true;
								}
								else if (!arena.isClosed()) {
									notifyFailure(player, "Arena must be closed to modify this!");
									return true;
								}

								// Other operations
								else if (args[2].equalsIgnoreCase("rename")) {
									// Check for player executing command
									if (player == null) {
										sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
										return true;
									}

									// Prompt for new name
									ChatListener.ChatTask task = (msg) -> {
										// Check for cancelling
										if (msg.equalsIgnoreCase("cancel")) {
											PlayerManager.notifyAlert(player, "Arena naming cancelled.");
											return;
										}

										// Try updating name
										try {
											arena.setName(msg.trim());
											CommunicationManager.debugInfo("Name changed for arena %s!",
												CommunicationManager.DebugLevel.VERBOSE,
												arena
													.getPath()
													.substring(1)
											);
										}
										catch (InvalidNameException err) {
											if (arena.getName() == null)
												GameManager.removeArena(arena.getId());
											PlayerManager.notifyFailure(player, "Invalid arena name!");
										}
									};
									ChatListener.addTask(
										player, task, "Enter the new unique name for the arena chat, or type CANCEL to " +
											"quit:");

									return true;
								}
								else if (args[2].toLowerCase().startsWith("portal")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									location = arena.getPortalLocation();
									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "set":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											arena.setPortal(player.getLocation());
											PlayerManager.notifySuccess(player, "Portal set!");
											return true;

										case "teleport":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (location == null) {
												PlayerManager.notifyFailure(player, "No portal to teleport to!");
												return true;
											}
											player.teleport(location);
											return true;

										case "center":
											if (location == null) {
												notifyFailure(player, "No portal to center!");
												return true;
											}
											arena.centerPortal();
											notifySuccess(player, "Portal centered!");
											return true;

										case "remove":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (arena.getPortal() != null)
												player.openInventory(Inventories.createPortalConfirmMenu(arena));
											else PlayerManager.notifyFailure(player, "No portal to remove!");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("leaderboard")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									location = arena.getArenaBoardLocation();
									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "set":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											arena.setArenaBoard(player.getLocation());
											PlayerManager.notifySuccess(player, "Leaderboard set!");
											return true;

										case "teleport":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (location == null) {
												PlayerManager.notifyFailure(player,
														"No leaderboard to teleport to!");
												return true;
											}
											player.teleport(location);
											return true;

										case "center":
											if (location == null) {
												notifyFailure(player, "No leaderboard to center!");
												return true;
											}
											arena.centerArenaBoard();
											notifySuccess(player, "Leaderboard centered!");
											return true;

										case "remove":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (arena.getArenaBoard() != null)
												player.openInventory(Inventories.createArenaBoardMenu(arena));
											else PlayerManager.notifyFailure(player, "No leaderboard to remove!");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("playerspawn")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "set":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											arena.setPlayerSpawn(player.getLocation());
											PlayerManager.notifySuccess(player, "Spawn set!");
											return true;

										case "teleport":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											try {
												player.teleport(arena.getPlayerSpawn().getLocation());
											} catch (Exception e) {
												PlayerManager.notifyFailure(player, "No spawn to teleport to!");
											}
											return true;

										case "center":
											try {
												arena.centerPlayerSpawn();
												notifySuccess(player, "Spawn centered!");
											} catch (Exception e) {
												notifyFailure(player, "No spawn to center!");
											}
											return true;

										case "remove":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (arena.getPlayerSpawn() != null)
												player.openInventory(Inventories.createPlayerSpawnMenu(arena));
											else PlayerManager.notifyFailure(player, "No spawn to remove!");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("waitingroom")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									location = arena.getWaitingRoom();
									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "set":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											arena.setWaitingRoom(player.getLocation());
											PlayerManager.notifySuccess(player, "Waiting room set!");
											return true;

										case "teleport":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (location == null) {
												PlayerManager.notifyFailure(player,
														"No waiting room to teleport to!");
												return true;
											}
											player.teleport(location);
											return true;

										case "center":
											if (location == null) {
												notifyFailure(player, "No waiting room to center!");
												return true;
											}
											arena.centerWaitingRoom();
											notifySuccess(player, "Waiting room centered!");
											return true;

										case "remove":
											// Check for player executing command
											if (player == null) {
												sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
												return true;
											}

											if (arena.getWaitingRoom() != null)
												player.openInventory(Inventories.createWaitingConfirmMenu(arena));
											else PlayerManager.notifyFailure(player,
													"No waiting room to remove!");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("spawnparticles")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasSpawnParticles()) {
												notifyFailure(player, "Spawn particles are already on!");
												return true;
											}

											// Turn on
											arena.setSpawnParticles(true);

											// Notify console and possibly player
											notifySuccess(player, "Spawn particles are on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasSpawnParticles()) {
												notifyFailure(player, "Spawn particles are already off!");
												return true;
											}

											// Turn off
											arena.setSpawnParticles(false);

											// Notify console and possibly player
											notifySuccess(player, "Spawn particles are off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("maxplayers")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than min
									if (num < arena.getMinPlayers()) {
										notifyFailure(player, "Max players cannot be less than min players!");
										return true;
									}

									// Set new value
									arena.setMaxPlayers(num);
									notifySuccess(player, "Max players for " + arena.getName() + " set to " +
											num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("minplayers")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than 0
									if (num < 1) {
										notifyFailure(player, "Min players cannot be less than 1!");
										return true;
									}

									// Set new value
									arena.setMinPlayers(num);
									notifySuccess(player, "Min players for " + arena.getName() + " set to " +
											num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("monsterspawnparticles")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasMonsterParticles()) {
												notifyFailure(player,
														"Monster spawn particles are already on!");
												return true;
											}

											// Turn on
											arena.setMonsterParticles(true);

											// Notify console and possibly player
											notifySuccess(player, "Monster spawn particles are on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasMonsterParticles()) {
												notifyFailure(player,
														"Monster spawn particles are already off!");
												return true;
											}

											// Turn off
											arena.setMonsterParticles(false);

											// Notify console and possibly player
											notifySuccess(player, "Monster spawn particles are off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("villagerspawnparticles")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasVillagerParticles()) {
												notifyFailure(player,
														"Villager spawn particles are already on!");
												return true;
											}

											// Turn on
											arena.setVillagerParticles(true);

											// Notify console and possibly player
											notifySuccess(player, "Villager spawn particles are on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasVillagerParticles()) {
												notifyFailure(player,
														"Villager spawn particles are already off!");
												return true;
											}

											// Turn off
											arena.setVillagerParticles(false);

											// Notify console and possibly player
											notifySuccess(player, "Villager spawn particles are off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("dynamicmobcount")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasDynamicCount()) {
												notifyFailure(player, "Dynamic mob count is already on!");
												return true;
											}

											// Turn on
											arena.setDynamicCount(true);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic mob count is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasDynamicCount()) {
												notifyFailure(player, "Dynamic mob count is already off!");
												return true;
											}

											// Turn off
											arena.setDynamicCount(false);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic mob count is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("defaultshop")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasNormal()) {
												notifyFailure(player, "Default shop is already on!");
												return true;
											}

											// Turn on
											arena.setNormal(true);

											// Notify console and possibly player
											notifySuccess(player, "Default shop is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasNormal()) {
												notifyFailure(player, "Default shop is already off!");
												return true;
											}

											// Turn off
											arena.setNormal(false);

											// Notify console and possibly player
											notifySuccess(player, "Default shop is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("customshop")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasCustom()) {
												notifyFailure(player, "Custom shop is already on!");
												return true;
											}

											// Turn on
											arena.setCustom(true);

											// Notify console and possibly player
											notifySuccess(player, "Custom shop is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasCustom()) {
												notifyFailure(player, "Custom shop is already off!");
												return true;
											}

											// Turn off
											arena.setCustom(false);

											// Notify console and possibly player
											notifySuccess(player, "Custom shop is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("enchantshop")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasEnchants()) {
												notifyFailure(player, "Enchant shop is already on!");
												return true;
											}

											// Turn on
											arena.setEnchants(true);

											// Notify console and possibly player
											notifySuccess(player, "Enchant shop is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasEnchants()) {
												notifyFailure(player, "Enchant shop is already off!");
												return true;
											}

											// Turn off
											arena.setEnchants(false);

											// Notify console and possibly player
											notifySuccess(player, "Enchant shop is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("communitychest")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasCommunity()) {
												notifyFailure(player, "Community chest is already on!");
												return true;
											}

											// Turn on
											arena.setCommunity(true);

											// Notify console and possibly player
											notifySuccess(player, "Community chest is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasCommunity()) {
												notifyFailure(player, "Community chest is already off!");
												return true;
											}

											// Turn off
											arena.setCommunity(false);

											// Notify console and possibly player
											notifySuccess(player, "Community chest is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("dynamicprices")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasDynamicPrices()) {
												notifyFailure(player, "Dynamic prices are already on!");
												return true;
											}

											// Turn on
											arena.setDynamicPrices(true);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic prices are on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasDynamicPrices()) {
												notifyFailure(player, "Dynamic prices are already off!");
												return true;
											}

											// Turn off
											arena.setDynamicPrices(false);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic prices are off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("dynamictimelimmit")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasDynamicLimit()) {
												notifyFailure(player, "Dynamic time limit is already on!");
												return true;
											}

											// Turn on
											arena.setDynamicLimit(true);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic time limit is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasDynamicLimit()) {
												notifyFailure(player, "Dynamic time limit is already off!");
												return true;
											}

											// Turn off
											arena.setDynamicLimit(false);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic time limit is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("dynamicdifficulty")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasDynamicDifficulty()) {
												notifyFailure(player, "Dynamic difficulty is already on!");
												return true;
											}

											// Turn on
											arena.setDynamicDifficulty(true);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic difficulty is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasDynamicDifficulty()) {
												notifyFailure(player, "Dynamic difficulty is already off!");
												return true;
											}

											// Turn off
											arena.setDynamicDifficulty(false);

											// Notify console and possibly player
											notifySuccess(player, "Dynamic difficulty is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("latearrival")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasLateArrival()) {
												notifyFailure(player, "Late arrival is already on!");
												return true;
											}

											// Turn on
											arena.setLateArrival(true);

											// Notify console and possibly player
											notifySuccess(player, "Late arrival is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasLateArrival()) {
												notifyFailure(player, "Late arrival is already off!");
												return true;
											}

											// Turn off
											arena.setLateArrival(false);

											// Notify console and possibly player
											notifySuccess(player, "Late arrival is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("experiencedrop")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasExpDrop()) {
												notifyFailure(player, "Experience drop is already on!");
												return true;
											}

											// Turn on
											arena.setExpDrop(true);

											// Notify console and possibly player
											notifySuccess(player, "Experience drop is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasExpDrop()) {
												notifyFailure(player, "Experience drop is already off!");
												return true;
											}

											// Turn off
											arena.setExpDrop(false);

											// Notify console and possibly player
											notifySuccess(player, "Experience drop is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("itemdrop")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "on":
											// Check if already on
											if (arena.hasGemDrop()) {
												notifyFailure(player, "Item drop is already on!");
												return true;
											}

											// Turn on
											arena.setGemDrop(true);

											// Notify console and possibly player
											notifySuccess(player, "Item drop is on for " +
													arena.getName() + ".");

											return true;

										case "off":
											// Check if already off
											if (!arena.hasGemDrop()) {
												notifyFailure(player, "Item drop is already off!");
												return true;
											}

											// Turn off
											arena.setGemDrop(false);

											// Notify console and possibly player
											notifySuccess(player, "Item drop is off for " +
													arena.getName() + ".");

											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("maxwaves")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than 0 or is -1
									if (num < 1 && num != -1) {
										notifyFailure(player, "Max waves cannot be less than 1!");
										return true;
									}

									// Set new value
									arena.setMaxWaves(num);
									notifySuccess(player, "Max waves for " + arena.getName() + " set to " +
											num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("wavetimelimit")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than 0 or is -1
									if (num < 1 && num != -1) {
										notifyFailure(player, "Wave time limit cannot be less than 1!");
										return true;
									}

									// Set new value
									arena.setWaveTimeLimit(num);
									notifySuccess(player, "Wave time limit for " + arena.getName() +
											" set to " + num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("wolfcap")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than 0
									if (num < 1) {
										notifyFailure(player, "Wolf cap cannot be less than 1!");
										return true;
									}

									// Set new value
									arena.setWolfCap(num);
									notifySuccess(player, "Wolf cap for " + arena.getName() + " set to " +
											num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("golemcap")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									// Get value
									int num;
									try {
										num = Integer.parseInt(args[2].substring(args[2].indexOf("-") + 1));
									} catch (Exception e) {
										notifyFailure(player, "Invalid operation value.");
										return true;
									}

									// Check if greater than 0
									if (num < 1) {
										notifyFailure(player, "Iron golem cap cannot be less than 1!");
										return true;
									}

									// Set new value
									arena.setgolemCap(num);
									notifySuccess(player, "Iron golem cap for " + arena.getName() + " set to " +
											num + ".");
									return true;
								}
								else if (args[2].toLowerCase().startsWith("difficultylabel")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1).toLowerCase()) {
										case "easy":
											arena.setDifficultyLabel("Easy");
											notifySuccess(player, arena.getName() + " is set to Easy.");
											return true;

										case "medium":
											arena.setDifficultyLabel("Medium");
											notifySuccess(player, arena.getName() + " is set to Medium.");
											return true;

										case "hard":
											arena.setDifficultyLabel("Hard");
											notifySuccess(player, arena.getName() + " is set to Hard.");
											return true;

										case "insane":
											arena.setDifficultyLabel("Insane");
											notifySuccess(player, arena.getName() + " is set to Insane.");
											return true;

										case "none":
											arena.setDifficultyLabel(null);
											notifySuccess(player, arena.getName() + " is set to None.");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].toLowerCase().startsWith("difficultymultiplier")) {
									// Check for operation value
									if (!args[2].contains("-")) {
										notifyFailure(player, "Operation value required!");
										return true;
									}

									switch (args[2].substring(args[2].indexOf("-") + 1)) {
										case "1":
											arena.setDifficultyMultiplier(1);
											notifySuccess(player, "Difficulty multiplier for " +
													arena.getName() + " is set to 1.");
											return true;

										case "2":
											arena.setDifficultyMultiplier(2);
											notifySuccess(player, "Difficulty multiplier for " +
													arena.getName() + " is set to 2.");
											return true;

										case "3":
											arena.setDifficultyMultiplier(3);
											notifySuccess(player, "Difficulty multiplier for " +
													arena.getName() + " is set to 3.");
											return true;

										case "4":
											arena.setDifficultyMultiplier(4);
											notifySuccess(player, "Difficulty multiplier for " +
													arena.getName() + " is set to 4.");
											return true;

										default:
											notifyFailure(player, "Invalid operation value.");
											return true;
									}
								}
								else if (args[2].equalsIgnoreCase("remove")) {
									// Check for player executing command
									if (player == null) {
										sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
										return true;
									}

									if (arena.isClosed())
										player.openInventory(Inventories.createArenaConfirmMenu(arena));
									else PlayerManager.notifyFailure(player,
											"Arena must be closed to modify this!");
								}

							default:
								notifyCommandFailure(player,
										"/vd admin [lobby, infoBoard, leaderboard, arena]",
										LanguageManager.messages.commandFormat);
								return true;
						}
					}

				// Chat tutorial & wiki link
				case "help":
					// Send wiki link if sender is not player
					if (player == null) {
						CommunicationManager.debugInfo(
								String.format("%s: https://github.com/Theguyhere0/villager-defense-minigame/wiki",
								LanguageManager.messages.visitWiki), CommunicationManager.DebugLevel.QUIET);
						return true;
					}

					int page;

					// Try to get page number, or set page to 1
					try {
						page = Integer.parseInt(args[1]);
					} catch (Exception e) {
						page = 1;
					}

					switch (page) {
						case 2:
							player.sendMessage(CommunicationManager.format("&a<----- " +
									String.format(LanguageManager.messages.help, "Villager Defense") +
									" (2/3) ----->"));
							player.sendMessage(CommunicationManager.format("&l " +
									LanguageManager.messages.help2));
							player.sendMessage("");
							player.sendMessage(CommunicationManager.format("&6 " +
									LanguageManager.messages.help2a));
							break;
						case 3:
							player.sendMessage(CommunicationManager.format("&a<----- " +
									String.format(LanguageManager.messages.help, "Villager Defense") +
									" (3/3) ----->"));
							player.sendMessage(CommunicationManager.format("&l " +
									LanguageManager.messages.help3));
							player.sendMessage("");
							player.sendMessage(CommunicationManager.format("&6 " +
									LanguageManager.messages.infoAboutWiki));
							TextComponent message = new TextComponent(" " + LanguageManager.messages.visitWiki + "!");
							message.setBold(true);
							message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
									"https://github.com/Theguyhere0/villager-defense-minigame/wiki"));
							player.spigot().sendMessage(message);
							break;
						default:
							player.sendMessage(CommunicationManager.format("&a<----- " +
									String.format(LanguageManager.messages.help, "Villager Defense") +
									" (1/3) ----->"));
							player.sendMessage(CommunicationManager.format("&6 " +
									LanguageManager.messages.help1));
					}
					return true;

				// Player leaves a game
				case "leave":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
							Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));
					return true;

				// Player checks stats
				case "stats":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					if (args.length == 1)
						player.openInventory(Inventories.createPlayerStatsMenu(player));
					else if (Main.getPlayerData().contains(args[1]))
						player.openInventory(Inventories.createPlayerStatsMenu(
								Objects.requireNonNull(Bukkit.getPlayer(args[1]))));
					else PlayerManager.notifyFailure(player, LanguageManager.messages.noStats,
								new ColoredMessage(ChatColor.AQUA, args[1]));
					return true;

				// Player checks kits
				case "kits":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					player.openInventory(Inventories.createPlayerKitsMenu(player, player.getName()));
					return true;

				// Player checks achievements
				case "achievements":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					player.openInventory(Inventories.createPlayerAchievementsMenu(player));
					return true;

				// Player joins as phantom
				case "join":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					// Attempt to get arena and player
					try {
						arena = GameManager.getArena(player);
						gamer = arena.getPlayer(player);
					} catch (ArenaNotFoundException | PlayerNotFoundException err) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
						return true;
					}

					// Check if player owns the phantom kit if late arrival is not on
					if (!Main.getPlayerData().getBoolean(player.getUniqueId() + ".kits." + Kit.phantom().getName()) &&
							!arena.hasLateArrival()) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.phantomOwn);
						return true;
					}

					// Check if arena is not ending
					if (arena.getStatus() == ArenaStatus.ENDING) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.phantomArena);
						return true;
					}

					// Check for useful phantom use
					if (gamer.getStatus() != VDPlayer.Status.SPECTATOR) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.phantomPlayer);
						return true;
					}

					// Check for arena capacity if late arrival is on
					if (arena.hasLateArrival() && arena.getActiveCount() >= arena.getMaxPlayers()) {
						PlayerManager.notifyAlert(player, LanguageManager.messages.maxCapacity);
						return true;
					}

					// Let player join using phantom kit
					PlayerManager.teleportIntoAdventure(player, arena.getPlayerSpawn().getLocation());
					gamer.setStatus(VDPlayer.Status.ALIVE);
					arena.getTask().giveItems(gamer);
					GameManager.createBoard(gamer);
					gamer.setJoinedWave(arena.getCurrentWave());
					gamer.setKit(Kit.phantom());
					player.closeInventory();
					return true;

				// Change crystal balance
				case "crystals":
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.crystals")) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
						return true;
					}

					// Check for valid command format
					if (args.length != 3) {
						notifyCommandFailure(player, "/vd crystals [player] [change amount]",
								LanguageManager.messages.commandFormat);
						return true;
					}

					// Check for valid player
					try {
						id = Arrays.stream(Bukkit.getOfflinePlayers())
								.filter(oPlayer -> Objects.equals(oPlayer.getName(), args[1]))
								.collect(Collectors.toList()).get(0).getUniqueId();
					} catch (NullPointerException e) {
						notifyFailure(player, LanguageManager.errors.invalidPlayer);
						return true;
					}
					if (!Main.getPlayerData().contains(id.toString())) {
						notifyFailure(player, LanguageManager.errors.invalidPlayer);
						return true;
					}

					// Check for valid amount
					try {
						int amount = Integer.parseInt(args[2]);
						Main.getPlayerData().set(
								id + ".crystalBalance",
								Math.max(Main.getPlayerData().getInt(id + ".crystalBalance") + amount, 0)
						);
						Main.savePlayerData();
						if (player != null)
							PlayerManager.notifySuccess(
									player,
									LanguageManager.confirms.balanceSet,
									new ColoredMessage(ChatColor.AQUA, args[1]),
									new ColoredMessage(ChatColor.AQUA,
											Integer.toString(Main.getPlayerData().getInt(id + ".crystalBalance")))
							);
						else CommunicationManager.debugInfo(LanguageManager.confirms.balanceSet,
							CommunicationManager.DebugLevel.QUIET, args[1],
								Integer.toString(Main.getPlayerData().getInt(id + ".crystalBalance")));
					} catch (Exception e) {
						notifyFailure(player, LanguageManager.errors.integer);
					}
					return true;

				// Force start
				case "start":
					// Start current arena
					if (args.length == 1) {
						// Check for player executing command
						if (player == null) {
							sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
							return true;
						}

						// Check for permission to use the command
						if (!player.hasPermission("vd.start")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						// Attempt to get arena and player
						try {
							arena = GameManager.getArena(player);
							gamer = arena.getPlayer(player);
						} catch (Exception e) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
							return true;
						}

						// Check if player is an active player
						if (!arena.getActives().contains(gamer)) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.activePlayer);
							return true;
						}

						// Check if arena already started
						if (arena.getStatus() != ArenaStatus.WAITING) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.arenaInProgress);
							return true;
						}

						Tasks task = arena.getTask();
						Map<Runnable, Integer> tasks = task.getTasks();
						BukkitScheduler scheduler = Bukkit.getScheduler();

						// Bring game to quick start if not already
						if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
								!scheduler.isQueued(tasks.get(task.sec10))) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.startingSoon);
							return true;
						} else {
							// Remove all tasks
							tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
							tasks.clear();

							// Schedule accelerated countdown tasks
							task.sec10.run();
							tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
							tasks.put(task.sec5,
									scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5, Calculator.secondsToTicks(5)));
							tasks.put(task.start,
									scheduler.scheduleSyncDelayedTask(Main.plugin, task.start, Calculator.secondsToTicks(10)));
						}
					}

					// Start specific arena
					else {
						// Check for permission to use the command
						if (player != null && !player.hasPermission("vd.admin")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						name = new StringBuilder(args[1]);
						for (int i = 0; i < args.length - 2; i++)
							name.append(" ").append(args[i + 2]);

						// Check if this arena exists
						try {
							arena = GameManager.getArena(name.toString());
						} catch (ArenaNotFoundException e) {
							notifyFailure(player, LanguageManager.errors.noArena);
							return true;
						}

						// Check if arena already started
						if (arena.getStatus() != ArenaStatus.WAITING) {
							notifyFailure(player, LanguageManager.errors.arenaInProgress);
							return true;
						}

						// Check if there is at least 1 player
						if (arena.getActiveCount() == 0) {
							notifyFailure(player, LanguageManager.errors.arenaNoPlayers);
							return true;
						}

						Tasks task = arena.getTask();
						Map<Runnable, Integer> tasks = task.getTasks();
						BukkitScheduler scheduler = Bukkit.getScheduler();

						// Bring game to quick start if not already
						if (tasks.containsKey(task.full10) || tasks.containsKey(task.sec10) &&
								!scheduler.isQueued(tasks.get(task.sec10))) {
							if (player != null)
								PlayerManager.notifyFailure(player,
										LanguageManager.errors.startingSoon);
							else CommunicationManager.debugError(LanguageManager.errors.startingSoon,
									CommunicationManager.DebugLevel.QUIET);
							return true;
						} else {
							// Remove all tasks
							tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
							tasks.clear();

							// Schedule accelerated countdown tasks
							task.sec10.run();
							tasks.put(task.sec10, 0); // Dummy task id to note that quick start condition was hit
							tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
									Calculator.secondsToTicks(5)));
							tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
									Calculator.secondsToTicks(10)));

							// Notify console
							CommunicationManager.debugInfo(arena.getName() + " was force started.", CommunicationManager.DebugLevel.NORMAL);
						}
					}

					return true;

				// Force end
				case "end":
					// End current arena
					if (args.length == 1) {
						// Check for player executing command
						if (player == null) {
							sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
							return true;
						}

						// Check for permission to use the command
						if (!player.hasPermission("vd.admin")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						// Attempt to get arena
						try {
							arena = GameManager.getArena(player);
						} catch (ArenaNotFoundException e) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
							return true;
						}

						// Check if arena has a game in progress
						if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.noGameEnd);
							return true;
						}

						// Check if game is about to end
						if (arena.getStatus() == ArenaStatus.ENDING) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.endingSoon);
							return true;
						}

						// Force end
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
								Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

						// Notify console
						CommunicationManager.debugInfo(arena.getName() + " was force ended.", CommunicationManager.DebugLevel.NORMAL);
					}

					// End specific arena
					else {
						// Check for permission to use the command
						if (player != null && !player.hasPermission("vd.admin")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						name = new StringBuilder(args[1]);
						for (int i = 0; i < args.length - 2; i++)
							name.append(" ").append(args[i + 2]);

						// Check if this arena exists
						try {
							arena = GameManager.getArena(name.toString());
						} catch (ArenaNotFoundException e) {
							notifyFailure(player, LanguageManager.errors.noArena);
							return true;
						}

						// Check if arena has a game in progress
						if (arena.getStatus() != ArenaStatus.ACTIVE && arena.getStatus() != ArenaStatus.ENDING) {
							notifyFailure(player, LanguageManager.errors.noGameEnd);
							return true;
						}

						// Check if game is about to end
						if (arena.getStatus() == ArenaStatus.ENDING) {
							notifyFailure(player, LanguageManager.errors.endingSoon);
							return true;
						}

						// Force end
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
								Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

						// Notify console
						CommunicationManager.debugInfo(arena.getName() + " was force ended.", CommunicationManager.DebugLevel.NORMAL);

						return true;
					}

					return true;

				// Force delay start
				case "delay":
					// Delay current arena
					if (args.length == 1) {
						// Check for player executing command
						if (player == null) {
							sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
							return true;
						}

						// Check for permission to use the command
						if (!player.hasPermission("vd.start")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						// Attempt to get arena
						try {
							arena = GameManager.getArena(player);
						} catch (ArenaNotFoundException e) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.inGame);
							return true;
						}

						// Check if arena already started
						if (arena.getStatus() != ArenaStatus.WAITING) {
							PlayerManager.notifyFailure(player,
									LanguageManager.errors.arenaInProgress);
							return true;
						}

						Tasks task = arena.getTask();
						Map<Runnable, Integer> tasks = task.getTasks();
						BukkitScheduler scheduler = Bukkit.getScheduler();

						// Remove all tasks
						tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
						tasks.clear();

						// Reschedule countdown tasks
						task.min2.run();
						tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(Main.plugin, task.min1,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(1))));
						tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec30,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 30)));
						tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec10,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 10)));
						tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 5)));
						tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2))));
					}

					// Delay specific arena
					else {
						// Check for permission to use the command
						if (player != null && !player.hasPermission("vd.admin")) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
							return true;
						}

						name = new StringBuilder(args[1]);
						for (int i = 0; i < args.length - 2; i++)
							name.append(" ").append(args[i + 2]);

						// Check if this arena exists
						try {
							arena = GameManager.getArena(name.toString());
						} catch (ArenaNotFoundException e) {
							notifyFailure(player, LanguageManager.errors.noArena);
							return true;
						}

						// Check if arena already started
						if (arena.getStatus() != ArenaStatus.WAITING) {
							notifyFailure(player, LanguageManager.errors.arenaInProgress);
							return true;
						}

						// Check if there is at least 1 player
						if (arena.getActiveCount() == 0) {
							notifyFailure(player, LanguageManager.errors.emptyArena);
							return true;
						}

						Tasks task = arena.getTask();
						Map<Runnable, Integer> tasks = task.getTasks();
						BukkitScheduler scheduler = Bukkit.getScheduler();

						// Remove all tasks
						tasks.forEach((runnable, taskId) -> scheduler.cancelTask(taskId));
						tasks.clear();

						// Reschedule countdown tasks
						task.min2.run();
						tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(Main.plugin, task.min1,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(1))));
						tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec30,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 30)));
						tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec10,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 10)));
						tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2) - 5)));
						tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
								Calculator.secondsToTicks(Calculator.minutesToSeconds(2))));

						// Notify console
						CommunicationManager.debugInfo(arena.getName() + " was delayed.", CommunicationManager.DebugLevel.NORMAL);
					}

					return true;

				// Fix certain default files
				case "fix":
					boolean fixed = false;

					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
						return true;
					}

					// Check for correct format
					if (args.length > 1) {
						notifyCommandFailure(player, "/vd fix", LanguageManager.messages.commandFormat);
						return true;
					}

					// Check if config.yml is outdated
					int configVersion = Main.plugin.getConfig().getInt("version");
					if (configVersion < Main.configVersion)
						if (player != null)
							PlayerManager.notifyAlert(
									player,
									LanguageManager.messages.manualUpdateWarn,
									new ColoredMessage(ChatColor.AQUA, "config.yml")
							);
						else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
								"config.yml");

					// Check if arenaData.yml is outdated
					int arenaDataVersion = Main.plugin.getConfig().getInt("arenaData");
					if (arenaDataVersion < 4) {
						try {
							// Transfer portals
							Objects.requireNonNull(Main.getArenaData().getConfigurationSection("portal"))
									.getKeys(false).forEach(arenaID -> {
										DataManager.setConfigurationLocation("a" + arenaID + ".portal",
												DataManager.getConfigLocation("portal." + arenaID));
										Main.getArenaData().set("portal." + arenaID, null);
									});
							Main.getArenaData().set("portal", null);

							// Transfer arena boards
							Objects.requireNonNull(Main.getArenaData().getConfigurationSection("arenaBoard"))
									.getKeys(false).forEach(arenaID -> {
										DataManager.setConfigurationLocation("a" + arenaID + ".arenaBoard",
												DataManager.getConfigLocation("arenaBoard." + arenaID));
										Main.getArenaData().set("arenaBoard." + arenaID, null);
									});
							Main.getArenaData().set("arenaBoard", null);

							Main.saveArenaData();

							// Reload portals
							GameManager.refreshPortals();

							// Flip flag and update config.yml
							fixed = true;
							Main.plugin.getConfig().set("Main.getArenaData()", 4);
							Main.plugin.saveConfig();

							// Notify
							if (player != null)
								PlayerManager.notifySuccess(
										player,
										LanguageManager.confirms.autoUpdate,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml"),
										new ColoredMessage(ChatColor.AQUA, "4")
								);
							CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml", "4");
						} catch (Exception e) {
							if (player != null)
								PlayerManager.notifyAlert(
										player,
										LanguageManager.messages.manualUpdateWarn,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml")
								);
							else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml");
						}
					}
					if (arenaDataVersion < 5) {
						try {
							// Translate waiting sounds
							Objects.requireNonNull(Main.getArenaData().getConfigurationSection("")).getKeys(false)
									.forEach(key -> {
										String soundPath = key + ".sounds.waiting";
										if (key.charAt(0) == 'a' && key.length() < 4 && Main.getArenaData().contains(soundPath)) {
											int oldValue = Main.getArenaData().getInt(soundPath);
											switch (oldValue) {
												case 0:
													Main.getArenaData().set(soundPath, "cat");
													break;
												case 1:
													Main.getArenaData().set(soundPath, "blocks");
													break;
												case 2:
													Main.getArenaData().set(soundPath, "far");
													break;
												case 3:
													Main.getArenaData().set(soundPath, "strad");
													break;
												case 4:
													Main.getArenaData().set(soundPath, "mellohi");
													break;
												case 5:
													Main.getArenaData().set(soundPath, "ward");
													break;
												case 9:
													Main.getArenaData().set(soundPath, "chirp");
													break;
												case 10:
													Main.getArenaData().set(soundPath, "stal");
													break;
												case 11:
													Main.getArenaData().set(soundPath, "mall");
													break;
												case 12:
													Main.getArenaData().set(soundPath, "wait");
													break;
												case 13:
													Main.getArenaData().set(soundPath, "pigstep");
													break;
												default:
													Main.getArenaData().set(soundPath, "none");
											}
										}
									});
							Main.saveArenaData();

							// Flip flag and update config.yml
							fixed = true;
							Main.plugin.getConfig().set("Main.getArenaData()", 5);
							Main.plugin.saveConfig();

							// Notify
							if (player != null)
								PlayerManager.notifySuccess(
										player,
										LanguageManager.confirms.autoUpdate,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml"),
										new ColoredMessage(ChatColor.AQUA, "5")
								);
							CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml", "5");
						} catch (Exception e) {
							if (player != null)
								PlayerManager.notifyAlert(
										player,
										LanguageManager.messages.manualUpdateWarn,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml")
								);
							else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml");
						}
					}
					if (arenaDataVersion < 6) {
						try {
							// Take old data and put into new format
							Objects.requireNonNull(Main.getArenaData().getConfigurationSection("")).getKeys(false)
									.stream().filter(key -> key.contains("a") && key.length() < 4)
									.forEach(key -> {
										int arenaId = Integer.parseInt(key.substring(1));
										String newPath = "arena." + arenaId;

										// Single key-value pairs
										moveData(newPath + ".name", key + ".name");
										moveData(newPath + ".max", key + ".max");
										moveData(newPath + ".min", key + ".min");
										moveData(newPath + ".spawnTable", key + ".spawnTable");
										moveData(newPath + ".maxWaves", key + ".maxWaves");
										moveData(newPath + ".waveTimeLimit", key + ".waveTimeLimit");
										moveData(newPath + ".difficulty", key + ".difficulty");
										moveData(newPath + ".closed", key + ".closed");
										moveData(newPath + ".normal", key + ".normal");
										moveData(newPath + ".dynamicCount", key + ".dynamicCount");
										moveData(newPath + ".dynamicDifficulty", key + ".dynamicDifficulty");
										moveData(newPath + ".dynamicPrices", key + ".dynamicPrices");
										moveData(newPath + ".difficultyLabel", key + ".difficultyLabel");
										moveData(newPath + ".dynamicLimit", key + ".dynamicLimit");
										moveData(newPath + ".wolf", key + ".wolf");
										moveData(newPath + ".golem", key + ".golem");
										moveData(newPath + ".expDrop", key + ".expDrop");
										moveData(newPath + ".gemDrop", key + ".gemDrop");
										moveData(newPath + ".community", key + ".community");
										moveData(newPath + ".lateArrival", key + ".lateArrival");
										moveData(newPath + ".enchants", key + ".enchants");
										moveData(newPath + ".bannedKits", key + ".bannedKits");

										// Config sections
										moveSection(newPath + ".sounds", key + ".sounds");
										moveSection(newPath + ".particles", key + ".particles");
										moveSection(newPath + ".spawn", key + ".spawn");
										moveSection(newPath + ".waiting", key + ".waiting");
										moveSection(newPath + ".corner1", key + ".corner1");
										moveSection(newPath + ".corner2", key + ".corner2");
										moveSection(newPath + ".arenaBoard", key + ".arenaBoard");
										moveSection(newPath + ".portal", key + ".portal");

										// Nested sections
										moveNested(newPath + ".monster", key + ".monster");
										moveNested(newPath + ".monster", key + ".monsters");
										moveNested(newPath + ".villager", key + ".villager");
										moveNested(newPath + ".records", key + ".records");
										moveInventory(newPath + ".customShop", key + ".customShop");

										// Remove old structure
										Main.getArenaData().set(key, null);
									});

							// Flip flag and update config.yml
							fixed = true;
							Main.plugin.getConfig().set("Main.getArenaData()", 6);
							Main.plugin.saveConfig();

							// Notify
							if (player != null)
								PlayerManager.notifySuccess(
										player,
										LanguageManager.confirms.autoUpdate,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml"),
										new ColoredMessage(ChatColor.AQUA, "6")
								);
							CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml", "6");
						} catch (Exception e) {
							if (player != null)
								PlayerManager.notifyAlert(
										player,
										LanguageManager.messages.manualUpdateWarn,
										new ColoredMessage(ChatColor.AQUA, "Main.getArenaData().yml")
								);
							else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
									"Main.getArenaData().yml");
						}
					}

					// Check if playerData.yml is outdated
					if (Main.plugin.getConfig().getInt("playerData") < Main.playerDataVersion) {
						try {
							// Transfer player names to UUID
							Objects.requireNonNull(Main.getPlayerData().getConfigurationSection("")).getKeys(false)
									.forEach(key -> {
										if (!key.equals("loggers")) {
											Main.getPlayerData().set(
													Bukkit.getOfflinePlayer(key).getUniqueId().toString(),
													Main.getPlayerData().get(key)
											);
											Main.getPlayerData().set(key, null);
										}
									});
							Main.savePlayerData();

							// Reload everything
							GameManager.refreshAll();

							// Flip flag and update config.yml
							fixed = true;
							Main.plugin.getConfig().set("Main.getPlayerData()", 2);
							Main.plugin.saveConfig();

							// Notify
							if (player != null)
								PlayerManager.notifySuccess(
										player,
										LanguageManager.confirms.autoUpdate,
										new ColoredMessage(ChatColor.AQUA, "Main.getPlayerData().yml"),
										new ColoredMessage(ChatColor.AQUA, "2")
								);
							CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
									"Main.getPlayerData().yml", "2");
						} catch (Exception e) {
							if (player != null)
								PlayerManager.notifyAlert(
										player,
										LanguageManager.messages.manualUpdateWarn,
										new ColoredMessage(ChatColor.AQUA, "Main.getPlayerData().yml")
								);
							else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
									"Main.getPlayerData().yml");
						}
					}

					// Update default spawn table
					if (Main.plugin.getConfig().getInt("spawnTableStructure") < Main.spawnTableVersion ||
							Main.plugin.getConfig().getInt("spawnTableDefault") < Main.defaultSpawnVersion) {
						// Flip flag
						fixed = true;

						// Fix
						Main.plugin.saveResource("spawnTables/default.yml", true);
						Main.plugin.getConfig().set("spawnTableDefault", Main.defaultSpawnVersion);
						Main.plugin.saveConfig();

						// Notify
						if (player != null) {
							PlayerManager.notifySuccess(
									player,
									LanguageManager.confirms.autoUpdate,
									new ColoredMessage(ChatColor.AQUA, "default.yml"),
									new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.defaultSpawnVersion)));
							PlayerManager.notifyAlert(
									player,
									LanguageManager.messages.manualUpdateWarn,
									new ColoredMessage(ChatColor.AQUA, "All other spawn files")
							);
						}
						CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
								"default.yml", Integer.toString(Main.defaultSpawnVersion));
						CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
								"All other spawn files");
					}

					// Update default language file
					if (Main.plugin.getConfig().getInt("languageFile") < Main.languageFileVersion) {
						// Flip flag
						fixed = true;

						// Fix
						Main.plugin.saveResource("languages/en_US.yml", true);
						Main.plugin.getConfig().set("languageFile", Main.languageFileVersion);
						Main.plugin.saveConfig();

						// Notify
						if (player != null) {
							PlayerManager.notifySuccess(
									player,
									LanguageManager.confirms.autoUpdate,
									new ColoredMessage(ChatColor.AQUA, "en_US.yml"),
									new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.languageFileVersion)));
							PlayerManager.notifyAlert(
									player,
									LanguageManager.messages.manualUpdateWarn,
									new ColoredMessage(ChatColor.AQUA, "All other language files")
							);
							PlayerManager.notifyAlert(player, LanguageManager.messages.restartPlugin);
						}
						CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
								"en_US.yml", Integer.toString(Main.languageFileVersion));
						CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
								"All other language files");
						CommunicationManager.debugError(LanguageManager.messages.restartPlugin, CommunicationManager.DebugLevel.QUIET);
					}

					// Check if customEffects.yml is outdated
					if (Main.plugin.getConfig().getInt("customEffects") < 2) {
						try {
							// Modify threshold keys
							ConfigurationSection section = Main.getCustomEffects().getConfigurationSection("unlimited.onGameEnd");
							if (section != null)
								section.getKeys(false).stream().filter(key -> !key.contains("-") && !key.contains("<"))
										.forEach(key -> {
											if (Main.getCustomEffects().get("unlimited.onGameEnd.^" + key) != null)
												Main.getCustomEffects().set("unlimited.onGameEnd." + key, Main.getArenaData().get("unlimited.onGameEnd.^" + key));
											Main.saveCustomEffects();
										});

							// Flip flag and update config.yml
							fixed = true;
							Main.plugin.getConfig().set("customEffects", 2);
							Main.plugin.saveConfig();

							// Notify
							if (player != null)
								PlayerManager.notifySuccess(
										player,
										LanguageManager.confirms.autoUpdate,
										new ColoredMessage(ChatColor.AQUA, "customEffects.yml"),
										new ColoredMessage(ChatColor.AQUA, "2")
								);
							CommunicationManager.debugInfo(LanguageManager.confirms.autoUpdate, CommunicationManager.DebugLevel.QUIET,
									"customEffects.yml", "2");
						} catch (Exception e) {
							if (player != null)
								PlayerManager.notifyAlert(
										player,
										LanguageManager.messages.manualUpdateWarn,
										new ColoredMessage(ChatColor.AQUA, "customEffects.yml")
								);
							else CommunicationManager.debugError(LanguageManager.messages.manualUpdateWarn, CommunicationManager.DebugLevel.QUIET,
									"customEffects.yml");
						}
					}

					// Message to player depending on whether the command fixed anything, then reload if fixed
					if (!fixed) {
						if (player != null)
							PlayerManager.notifyAlert(player, LanguageManager.messages.noAutoUpdate);
						else CommunicationManager.debugInfo(LanguageManager.messages.noAutoUpdate, CommunicationManager.DebugLevel.QUIET);
					} else {
						// Notify of reload
						if (player != null)
							PlayerManager.notifyAlert(player, "Reloading plugin data");
						else CommunicationManager.debugInfo("Reloading plugin data", CommunicationManager.DebugLevel.QUIET);

						Main.plugin.reload();
					}

					return true;

//				// Change plugin debug level
//				case "debug":
//					// Check for permission to use the command
//					if (player != null && !player.hasPermission("vd.admin")) {
//						PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
//						return true;
//					}
//
//					// Check for correct format
//					if (args.length != 2) {
//						notifyCommandFailure(player, "/vd debug [debug level (0-3)]",
//								LanguageManager.messages.commandFormat);
//						return true;
//					}
//
//					// Set debug level
//					try {
//						CommunicationManager.setDebugLevel(Integer.parseInt(args[1]));
//					} catch (Exception e) {
//						notifyCommandFailure(player, "/vd debug [debug level (0-3)]",
//								LanguageManager.messages.commandFormat);
//						return true;
//					}
//
//					// Notify
//					if (player != null)
//						PlayerManager.notifySuccess(
//								player,
//								LanguageManager.messages.debugLevelSet,
//								new ColoredMessage(ChatColor.AQUA, args[1])
//						);
//					else CommunicationManager.debugInfo(LanguageManager.messages.debugLevelSet, CommunicationManager.DebugLevel.QUIET, args[1]);
//
//					return true;

				// Player kills themselves
				case "die":
					// Check for player executing command
					if (player == null) {
						sender.sendMessage(LanguageManager.errors.playerOnlyCommand);
						return true;
					}

					// Check for player in a game
					if (!GameManager.checkPlayer(player)) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.notInGame);
						return true;
					}

					// Check for player in an active game
					if (GameManager.getArenas().values().stream().filter(Objects::nonNull)
							.filter(arena1 -> arena1.getStatus() == ArenaStatus.ACTIVE)
							.noneMatch(arena1 -> arena1.hasPlayer(player))) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.suicideActive);
						return true;
					}

					// Check for alive player
					try {
						if (GameManager.getArena(player).getPlayer(player).getStatus() != VDPlayer.Status.ALIVE) {
							PlayerManager.notifyFailure(player, LanguageManager.errors.suicide);
							return true;
						}
					} catch (PlayerNotFoundException err) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.suicide);
						return true;
					} catch (ArenaNotFoundException err) {
						return true;
					}

					// Create a player death and make sure it gets detected
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
							Bukkit.getPluginManager().callEvent(new EntityDamageEvent(player,
									EntityDamageEvent.DamageCause.SUICIDE, 99)));
					return true;

				// Reload internal plugin data
				case "reload":
					// Check for permission to use the command
					if (player != null && !player.hasPermission("vd.admin")) {
						PlayerManager.notifyFailure(player, LanguageManager.errors.permission);
						return true;
					}

					// Notify of reload
					if (player != null)
						PlayerManager.notifyAlert(player, "Reloading plugin data");
					else CommunicationManager.debugInfo("Reloading plugin data", CommunicationManager.DebugLevel.QUIET);

					Main.plugin.reload();
					return true;

				// No valid command sent
				default:
					notifyCommandFailure(player, "/vd help", LanguageManager.errors.command);
					return true;
			}
		} catch (NullPointerException e) {
			CommunicationManager.debugError("The language file is missing some attributes, please update it!",
					CommunicationManager.DebugLevel.QUIET);
		}
		return false;
	}

	private void moveData(String to, String from) {
		if (Main.getArenaData().get(from) != null)
			Main.getArenaData().set(to, Main.getArenaData().get(from));
	}

	private void moveSection(String to, String from) {
		if (Main.getArenaData().contains(from))
			Objects.requireNonNull(Main.getArenaData().getConfigurationSection(from)).getKeys(false).forEach(key ->
					moveData(to + "." + key, from + "." + key));
	}

	private void moveNested(String to, String from) {
		if (Main.getArenaData().contains(from))
			Objects.requireNonNull(Main.getArenaData().getConfigurationSection(from)).getKeys(false).forEach(key ->
					moveSection(to + "." + key, from + "." + key));
	}

	private void moveInventory(String to, String from) {
		if (Main.getArenaData().contains(from))
			Objects.requireNonNull(Main.getArenaData().getConfigurationSection(from)).getKeys(false).forEach(key ->
					Main.getArenaData().set(to + "." + key, Main.getArenaData().getItemStack(from + "." + key)));
	}

	private void notifyCommandFailure(Player player, String command, String message) {
		if (player != null)
			PlayerManager.notifyFailure(player, message, new ColoredMessage(ChatColor.AQUA, command));
		else CommunicationManager.debugError(message, CommunicationManager.DebugLevel.QUIET, command.substring(1));
	}

	private void notifyFailure(Player player, String message) {
		if (player != null)
			PlayerManager.notifyFailure(player, message);
		else CommunicationManager.debugError(message, CommunicationManager.DebugLevel.QUIET);
	}

	private void notifySuccess(Player player, String message) {
		if (player != null)
			PlayerManager.notifySuccess(player, message);
		else CommunicationManager.debugInfo(message, CommunicationManager.DebugLevel.QUIET);
	}
}
