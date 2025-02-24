package me.theguyhere.villagerdefense.plugin.commands;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.commands.exceptions.CommandException;
import me.theguyhere.villagerdefense.plugin.data.ArenaDataManager;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.data.PlayerDataManager;
import me.theguyhere.villagerdefense.plugin.data.exceptions.UpdateFailedException;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
class CommandFixFiles {
	static void execute(String[] args, CommandSender sender) throws CommandException {
		// Guard clauses
		if (!GuardClause.checkArg(args, 0, VDCommandExecutor.Argument.FIX.getArg()))
			return;

		boolean fixed = false;
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
				ArenaDataManager.updateToVersion4();

				// Reload portals
				GameManager.refreshPortals();

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin.getConfig().set("arenaData", 4);
				Main.plugin.saveConfig();

				// Notify
				if (player != null)
					PlayerManager.notifySuccess(
						player,
						LanguageManager.confirms.autoUpdate,
						new ColoredMessage(ChatColor.AQUA, "arenaData.yml"),
						new ColoredMessage(ChatColor.AQUA, "4")
					);
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"arenaData.yml", "4");
			} catch (UpdateFailedException e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}
		if (arenaDataVersion < 5 && !arenaAbort) {
			try {
				ArenaDataManager.updateToVersion5();

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin.getConfig().set("arenaData", 5);
				Main.plugin.saveConfig();

				// Notify
				if (player != null)
					PlayerManager.notifySuccess(
						player,
						LanguageManager.confirms.autoUpdate,
						new ColoredMessage(ChatColor.AQUA, "arenaData.yml"),
						new ColoredMessage(ChatColor.AQUA, "5")
					);
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"arenaData.yml", "5");
			} catch (UpdateFailedException e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}
		if (arenaDataVersion < 6 && !arenaAbort) {
			try {
				ArenaDataManager.updateToVersion6();

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin.getConfig().set("arenaData", 6);
				Main.plugin.saveConfig();

				// Notify
				if (player != null)
					PlayerManager.notifySuccess(
						player,
						LanguageManager.confirms.autoUpdate,
						new ColoredMessage(ChatColor.AQUA, "arenaData.yml"),
						new ColoredMessage(ChatColor.AQUA, "6")
					);
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"arenaData.yml", "6");
			} catch (UpdateFailedException e) {
				arenaAbort = true;
				notifyManualUpdate(player, "arenaData.yml");
			}
		}
		if (arenaDataVersion < 7 && !arenaAbort) {
			try {
				ArenaDataManager.updateToVersion7();

				// Flip flag and update config.yml
				fixed = true;
				Main.plugin.getConfig().set("arenaData", 7);
				Main.plugin.saveConfig();

				// Notify
				if (player != null)
					PlayerManager.notifySuccess(
						player,
						LanguageManager.confirms.autoUpdate,
						new ColoredMessage(ChatColor.AQUA, "arenaData.yml"),
						new ColoredMessage(ChatColor.AQUA, "7")
					);
				CommunicationManager.debugInfo(CommunicationManager.DebugLevel.QUIET, LanguageManager.confirms.autoUpdate,
					"arenaData.yml", "7");
			} catch (UpdateFailedException e) {
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
				PlayerDataManager.updateToVersion2();

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
			catch (UpdateFailedException e) {
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
