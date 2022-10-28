package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class JoinListener implements Listener {
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		GameManager.displayEverything(player);
		nmsManager.injectPacketListener(player, new PacketListenerImp());
		FileConfiguration playerData = Main.getPlayerData();

		// Get list of loggers from data file
		List<String> loggers = playerData.getStringList("loggers");

		// Check if player is a logger
		if (loggers.contains(player.getUniqueId().toString())) {
			CommunicationManager.debugInfo("%s joined after logging mid-game.", 2, player.getName());

			// Teleport them back to lobby
			PlayerManager.teleAdventure(player, GameManager.getLobby());
			loggers.remove(player.getUniqueId().toString());
			playerData.set("loggers", loggers);

			// Return player health, food, exp, and items
			if (Main.plugin.getConfig().getBoolean("keepInv"))
				PlayerManager.returnSurvivalStats(player);

			Main.savePlayerData();
		}

		// If the plugin setup is outdated, send message to admins
		if (Main.isOutdated() && player.hasPermission("vd.admin"))
			PlayerManager.notifyFailure(player, LanguageManager.errors.outdated,
					new ColoredMessage(ChatColor.AQUA, "/vd fix"));

		// Check for achievements
		AchievementChecker.checkDefaultHighScoreAchievements(player);
		AchievementChecker.checkDefaultKitAchievements(player);
	}

	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		GameManager.displayAllPortals(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// Uninject player and make them leave from arena
		nmsManager.uninjectPacketListener(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
				Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));

		// Get list of loggers from data file and add player to it
		List<String> loggers = Main.getPlayerData().getStringList("loggers");
		loggers.add(player.getUniqueId().toString());

		// Add to list of loggers if in a game
		if (GameManager.checkPlayer(player)) {
			CommunicationManager.debugInfo("%s logged out mid-game.", 2, player.getName());
			Main.getPlayerData().set("loggers", loggers);
			Main.savePlayerData();
		}
	}
}
