package me.theguyhere.villagerdefense.plugin.background;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.arenas.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.game.GameController;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * The listener that handles background events.
 */
public class BackgroundListener implements Listener {

	@Override
	public boolean checkInvisible(ItemStack itemStack) {
		PersistentDataContainer dataContainer = Objects
			.requireNonNull(itemStack.getItemMeta())
			.getPersistentDataContainer();
		Boolean invisible = dataContainer.get(VDItem.INVISIBLE, PersistentDataType.BOOLEAN);
		if (invisible == null)
			return false;
		else return invisible;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		GameController.displayEverything(player);
		FileConfiguration playerData = Main.getPlayerData();

		// Get list of loggers from data file
		List<String> loggers = playerData.getStringList("loggers");

		// Check if player is a logger
		if (loggers.contains(player
			.getUniqueId()
			.toString())) {
			CommunicationManager.debugInfo("%s joined after disconnecting mid-game.",
				CommunicationManager.DebugLevel.VERBOSE, player.getName()
			);

			// Teleport them back to lobby
			PlayerManager.teleAdventure(player, GameController.getLobby());
			loggers.remove(player
				.getUniqueId()
				.toString());
			playerData.set("loggers", loggers);

			// Return player health, food, exp, and items
			if (Main.plugin
				.getConfig()
				.getBoolean("keepInv"))
				PlayerManager.returnSurvivalStats(player);

			Main.savePlayerData();
		}

		// If the plugin setup is outdated, send message to admins
		if (Main.isOutdated() && player.hasPermission("vd.admin"))
			PlayerManager.notifyFailure(player, LanguageManager.errors.outdated,
				new ColoredMessage(ChatColor.AQUA, "/vd fix")
			);

		// Check for achievements
		AchievementChecker.checkDefaultHighScoreAchievements(player);
		AchievementChecker.checkDefaultKitAchievements(player);
	}

	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		GameController.displayAllPortals(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// Make player leave the arena
		Bukkit
			.getScheduler()
			.scheduleSyncDelayedTask(Main.plugin, () ->
				Bukkit
					.getPluginManager()
					.callEvent(new LeaveArenaEvent(player)));

		// Get list of loggers from data file and add player to it
		List<String> loggers = Main
			.getPlayerData()
			.getStringList("loggers");
		loggers.add(player
			.getUniqueId()
			.toString());

		// Add to list of loggers if in a game
		if (GameController.checkPlayer(player)) {
			CommunicationManager.debugInfo("%s disconnected mid-game.", CommunicationManager.DebugLevel.VERBOSE,
				player.getName()
			);
			Main
				.getPlayerData()
				.set("loggers", loggers);
			Main.savePlayerData();
		}
	}

	@EventHandler
	public void onWorldLoadEvent(WorldLoadEvent e) {
		String worldName = e
			.getWorld()
			.getName();

		// Handle world loading after initialization
		if (Main
			.getUnloadedWorlds()
			.contains(worldName)) {
			Main.loadWorld(worldName);
			Main.resetGameManager();
			GameController.reloadLobby();
			GameController.refreshAll();
		}
	}

	@EventHandler
	public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
		GameController.displayEverything(e.getPlayer());
	}
}
