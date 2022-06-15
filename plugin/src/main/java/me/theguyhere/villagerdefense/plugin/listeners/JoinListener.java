package me.theguyhere.villagerdefense.plugin.listeners;

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
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

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
			CommunicationManager.debugInfo(player.getName() + " joined after logging mid-game.", 2);

			// Teleport them back to lobby
			PlayerManager.teleAdventure(player, GameManager.getLobby());
			loggers.remove(player.getUniqueId().toString());
			playerData.set("loggers", loggers);

			if (Main.plugin.getConfig().getBoolean("keepInv")) {
				// Return player health, food, exp, and items
				if (playerData.contains(player.getUniqueId() + ".health"))
					player.setHealth(playerData.getDouble(player.getUniqueId() + ".health"));
				playerData.set(player.getUniqueId() + ".health", null);
				if (playerData.contains(player.getUniqueId() + ".food"))
					player.setFoodLevel(playerData.getInt(player.getUniqueId() + ".food"));
				playerData.set(player.getUniqueId() + ".food", null);
				if (playerData.contains(player.getUniqueId() + ".saturation"))
					player.setSaturation((float) playerData.getDouble(player.getUniqueId() +
							".saturation"));
				playerData.set(player.getUniqueId() + ".saturation", null);
				if (playerData.contains(player.getUniqueId() + ".level")) {
					player.setLevel(playerData.getInt(player.getUniqueId() + ".level"));
					playerData.set(player.getUniqueId() + ".level", null);
				}
				if (playerData.contains(player.getUniqueId() + ".exp")) {
					player.setExp((float) playerData.getDouble(player.getUniqueId() + ".exp"));
					playerData.set(player.getUniqueId() + ".exp", null);
				}
				if (playerData.contains(player.getUniqueId() + ".inventory")) {
					Objects.requireNonNull(playerData
									.getConfigurationSection(player.getUniqueId() + ".inventory"))
							.getKeys(false)
							.forEach(num -> player.getInventory().setItem(Integer.parseInt(num),
									(ItemStack) playerData.get(player.getUniqueId() + ".inventory." + num)));
					playerData.set(player.getUniqueId() + ".inventory", null);
				}
			}

			Main.savePlayerData();
		}

		// If the plugin setup is outdated, send message to admins
		if (Main.isOutdated() && player.hasPermission("vd.admin"))
			PlayerManager.notifyFailure(player, LanguageManager.errors.outdated, ChatColor.AQUA, "/vd fix");

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
			CommunicationManager.debugInfo(player.getName() + " logged out mid-game.", 2);
			Main.getPlayerData().set("loggers", loggers);
			Main.savePlayerData();
		}
	}
}
