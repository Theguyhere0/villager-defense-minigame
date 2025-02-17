package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.YAMLManager;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Executes command to attempt fixing and updating files.
 */
@SuppressWarnings("deprecation")
class CommandFixFiles {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.FIX.getArg()))
			return;

		boolean fixed = false;
		FileConfiguration playerData = Main.getPlayerData();
		FileConfiguration arenaData = Main.getArenaData();
		FileConfiguration customEffects = Main.getCustomEffects();
		Player player = sender instanceof Player ? (Player) sender : null;

		// Check if config.yml is outdated
		if (Main.plugin
			.getConfig()
			.getInt("version") < Main.configVersion)
			notifyManualUpdate(player, "config.yml");

		// Check if arenaData.yml is outdated
		int arenaDataVersion = Main.plugin
			.getConfig()
			.getInt("arenaData");
		boolean arenaAbort = false;
		if (arenaDataVersion < 4) {
			try {
				// Transfer portals
				Objects.requireNonNull(Main.getArenaData().getConfigurationSection("portal"))
					.getKeys(false).forEach(arenaID -> {
						YAMLManager.setConfigurationLocation("a" + arenaID + ".portal",
							YAMLManager.getConfigLocation("portal." + arenaID));
						Main.getArenaData().set("portal." + arenaID, null);
					});
				Main.getArenaData().set("portal", null);

				// Transfer arena boards
				Objects.requireNonNull(Main.getArenaData().getConfigurationSection("arenaBoard"))
					.getKeys(false).forEach(arenaID -> {
						YAMLManager.setConfigurationLocation("a" + arenaID + ".arenaBoard",
							YAMLManager.getConfigLocation("arenaBoard." + arenaID));
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
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"Main.getArenaData().yml", "4");
			} catch (Exception e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}
		if (arenaDataVersion < 5 && !arenaAbort) {
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
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"Main.getArenaData().yml", "5");
			} catch (Exception e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}
		if (arenaDataVersion < 6 && !arenaAbort) {
			try {
				// Take old data and put into new format
				Objects.requireNonNull(Main.getArenaData().getConfigurationSection("")).getKeys(false)
					.stream().filter(key -> key.contains("a") && key.length() < 4)
					.forEach(key -> {
						int arenaId = Integer.parseInt(key.substring(1));
						String newPath = "arena." + arenaId;

						// Single key-value pairs
						moveData(arenaData, newPath + ".name", key + ".name");
						moveData(arenaData, newPath + ".max", key + ".max");
						moveData(arenaData, newPath + ".min", key + ".min");
						moveData(arenaData, newPath + ".spawnTable", key + ".spawnTable");
						moveData(arenaData, newPath + ".maxWaves", key + ".maxWaves");
						moveData(arenaData, newPath + ".waveTimeLimit", key + ".waveTimeLimit");
						moveData(arenaData, newPath + ".difficulty", key + ".difficulty");
						moveData(arenaData, newPath + ".closed", key + ".closed");
						moveData(arenaData, newPath + ".normal", key + ".normal");
						moveData(arenaData, newPath + ".dynamicCount", key + ".dynamicCount");
						moveData(arenaData, newPath + ".dynamicDifficulty", key + ".dynamicDifficulty");
						moveData(arenaData, newPath + ".dynamicPrices", key + ".dynamicPrices");
						moveData(arenaData, newPath + ".difficultyLabel", key + ".difficultyLabel");
						moveData(arenaData, newPath + ".dynamicLimit", key + ".dynamicLimit");
						moveData(arenaData, newPath + ".wolf", key + ".wolf");
						moveData(arenaData, newPath + ".golem", key + ".golem");
						moveData(arenaData, newPath + ".expDrop", key + ".expDrop");
						moveData(arenaData, newPath + ".gemDrop", key + ".gemDrop");
						moveData(arenaData, newPath + ".community", key + ".community");
						moveData(arenaData, newPath + ".lateArrival", key + ".lateArrival");
						moveData(arenaData, newPath + ".enchants", key + ".enchants");
						moveData(arenaData, newPath + ".bannedKits", key + ".bannedKits");

						// Config sections
						moveSection(arenaData, newPath + ".sounds", key + ".sounds");
						moveSection(arenaData, newPath + ".particles", key + ".particles");
						moveSection(arenaData, newPath + ".spawn", key + ".spawn");
						moveSection(arenaData, newPath + ".waiting", key + ".waiting");
						moveSection(arenaData, newPath + ".corner1", key + ".corner1");
						moveSection(arenaData, newPath + ".corner2", key + ".corner2");
						moveSection(arenaData, newPath + ".arenaBoard", key + ".arenaBoard");
						moveSection(arenaData, newPath + ".portal", key + ".portal");

						// Nested sections
						moveNested(arenaData, newPath + ".monster", key + ".monster");
						moveNested(arenaData, newPath + ".monster", key + ".monsters");
						moveNested(arenaData, newPath + ".villager", key + ".villager");
						moveNested(arenaData, newPath + ".records", key + ".records");
						moveInventory(arenaData, newPath + ".customShop", key + ".customShop");

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
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"Main.getArenaData().yml", "6");
			} catch (Exception e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}

		// Check if playerData.yml is outdated
		int playerDataVersion = Main.plugin
			.getConfig()
			.getInt("playerData");
		boolean playerAbort = false;
		if (playerDataVersion < 2) {
			try {
				// Transfer player names to UUID
				Objects
					.requireNonNull(playerData.getConfigurationSection(""))
					.getKeys(false)
					.forEach(key -> {
						if (!key.equals("loggers")) {
							playerData.set(
								Bukkit
									.getOfflinePlayer(key)
									.getUniqueId()
									.toString(),
								playerData.get(key)
							);
							playerData.set(key, null);
						}
					});
				Main.savePlayerData();

				// Reload everything
				GameManager.refreshAll();

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin
					.getConfig()
					.set("playerData", 2);
				Main.plugin.saveConfig();

				// Notify
				notifyAutoUpdate(player, "playerData.yml", 2);
			}
			catch (Exception e) {
				playerAbort = true;
				notifyManualUpdate(player, "playerData.yml");
			}
		}

		// Update default spawn table
		if (Main.plugin
			.getConfig()
			.getInt("spawnTableStructure") < Main.spawnTableVersion ||
			Main.plugin
				.getConfig()
				.getInt("spawnTableDefault") < Main.defaultSpawnVersion) {
			// Flip flag
			fixed = true;

			// Fix
			Main.plugin.saveResource("spawnTables/default.yml", true);
			Main.plugin
				.getConfig()
				.set("spawnTableDefault", Main.defaultSpawnVersion);
			Main.plugin.saveConfig();

			// Notify
			if (player != null) {
				PlayerManager.notifySuccess(
					player,
					LanguageManager.confirms.autoUpdate,
					new ColoredMessage(ChatColor.AQUA, "default.yml"),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.defaultSpawnVersion))
				);
				PlayerManager.notifyAlert(
					player,
					LanguageManager.messages.manualUpdateWarn,
					new ColoredMessage(ChatColor.AQUA, "All other spawn files")
				);
			}
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
				"default.yml", Integer.toString(Main.defaultSpawnVersion)
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, LanguageManager.messages.manualUpdateWarn,
				"All other spawn files"
			);
		}

		// Check if spawn table structure can be considered updated
		boolean noCustomSpawnTables = false;
		try (Stream<Path> stream = Files.list(Paths.get(Main.plugin
			.getDataFolder()
			.getPath() + "/spawnTables"))) {
			noCustomSpawnTables = stream.count() < 2;
		}
		catch (IOException ignored) {
		}
		if (noCustomSpawnTables && Main.plugin
			.getConfig()
			.getInt("spawnTableStructure") < Main.spawnTableVersion) {
			// Flip flag
			fixed = true;

			// Fix
			Main.plugin
				.getConfig()
				.set("spawnTableStructure", Main.spawnTableVersion);
			Main.plugin.saveConfig();

			// Notify
			if (player != null) {
				PlayerManager.notifySuccess(
					player,
					LanguageManager.confirms.autoUpdate,
					new ColoredMessage(ChatColor.AQUA, "Spawn tables"),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.spawnTableVersion))
				);
			}
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
				"Spawn tables", Integer.toString(Main.spawnTableVersion)
			);
		}

		// Update default language file
		if (Main.plugin
			.getConfig()
			.getInt("languageFile") < Main.languageFileVersion) {
			// Flip flag
			fixed = true;

			// Fix
			Main.plugin.saveResource("languages/en_US.yml", true);
			Main.plugin
				.getConfig()
				.set("languageFile", Main.languageFileVersion);
			Main.plugin.saveConfig();

			// Notify
			if (player != null) {
				PlayerManager.notifySuccess(
					player,
					LanguageManager.confirms.autoUpdate,
					new ColoredMessage(ChatColor.AQUA, "en_US.yml"),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(Main.languageFileVersion))
				);
				PlayerManager.notifyAlert(
					player,
					LanguageManager.messages.manualUpdateWarn,
					new ColoredMessage(ChatColor.AQUA, "All other language files")
				);
				PlayerManager.notifyAlert(player, LanguageManager.messages.restartPlugin);
			}
			CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
				"en_US.yml", Integer.toString(Main.languageFileVersion)
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, LanguageManager.messages.manualUpdateWarn,
				"All other language files"
			);
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, LanguageManager.messages.restartPlugin
			);
		}

		// Check if customEffects.yml is outdated
		int customEffectsVersion = Main.plugin
			.getConfig()
			.getInt("customEffects");
		boolean customAbort = false;
		if (customEffectsVersion < 2) {
			try {
				// Modify threshold keys
				String path = "unlimited.onGameEnd";
				ConfigurationSection section = customEffects.getConfigurationSection(path);
				if (section != null)
					section
						.getKeys(false)
						.stream()
						.filter(key -> !key.contains("-") && !key.contains("<"))
						.forEach(key -> {
							moveData(customEffects, path + ".^" + key, path + "." + key);
							Main.saveCustomEffects();
						});

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin
					.getConfig()
					.set("customEffects", 2);
				Main.plugin.saveConfig();

				// Notify
				notifyAutoUpdate(player, "customEffects.yml", 2);
			}
			catch (Exception e) {
				customAbort = true;
				notifyManualUpdate(player, "customEffects.yml");
			}
		}

		// Message to player depending on whether the command fixed anything, then reload if fixed
		if (!fixed) {
			if (player != null)
				PlayerManager.notifyAlert(player, LanguageManager.messages.noAutoUpdate);
			else
				CommunicationManager.debugInfo(
					CommunicationManager.DebugLevel.QUIET, LanguageManager.messages.noAutoUpdate
				);
		}
		else {
			// Notify of reload
			if (player != null)
				PlayerManager.notifyAlert(player, "Reloading plugin data...");
			else CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, "Reloading plugin data...");

			Main.plugin.reload();
		}
	}

	private static void moveData(FileConfiguration config, String to, String from) {
		if (config.get(from) != null) {
			config.set(to, config.get(from));
			config.set(from, null);
		}
	}

	private static void moveSection(FileConfiguration config, String to, String from) {
		if (config.contains(from)) {
			Objects
				.requireNonNull(config.getConfigurationSection(from))
				.getKeys(false)
				.forEach(key ->
					moveData(config, to + "." + key, from + "." + key));
			config.set(from, null);
		}
	}

	private static void moveNested(FileConfiguration config, String to, String from) {
		if (config.contains(from)) {
			Objects
				.requireNonNull(config.getConfigurationSection(from))
				.getKeys(false)
				.forEach(key ->
					moveSection(config, to + "." + key, from + "." + key));
			config.set(from, null);
		}
	}

	private static void moveInventory(FileConfiguration config, String to, String from) {
		if (config.contains(from)) {
			Objects
				.requireNonNull(config.getConfigurationSection(from))
				.getKeys(false)
				.forEach(key ->
					config.set(to + "." + key, config.getItemStack(from + "." + key)));
			config.set(from, null);
		}
	}

	private static void notifyManualUpdate(Player player, String file) {
		if (player != null)
			PlayerManager.notifyAlert(
				player,
				LanguageManager.messages.manualUpdateWarn,
				new ColoredMessage(ChatColor.AQUA, file)
			);
		else
			CommunicationManager.debugError(CommunicationManager.DebugLevel.QUIET, LanguageManager.messages.manualUpdateWarn,
				file
			);
	}

	private static void notifyAutoUpdate(Player player, String file, int version) {
		if (player != null)
			PlayerManager.notifySuccess(
				player,
				LanguageManager.confirms.autoUpdate,
				new ColoredMessage(ChatColor.AQUA, file),
				new ColoredMessage(ChatColor.AQUA, Integer.toString(version))
			);
		CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
			file, Integer.toString(version)
		);
	}
}
