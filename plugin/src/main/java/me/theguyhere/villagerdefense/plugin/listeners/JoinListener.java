package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.nms.common.NMSManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.plugin.tools.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.NMSVersion;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JoinListener implements Listener {
	private final NMSManager nmsManager = NMSVersion.getCurrent().getNmsManager();
	private final Main plugin;

	public JoinListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		ArenaManager.displayEverything(player);
		nmsManager.injectPacketListener(player, new PacketListenerImpl());

		// Get list of loggers from data file
		List<String> loggers = plugin.getPlayerData().getStringList("loggers");

		// Check if player is a logger
		if (loggers.contains(player.getName())) {
			CommunicationManager.debugInfo(player.getName() + " joined after logging mid-game.", 2);

			// Teleport them back to lobby
			PlayerManager.teleAdventure(player, ArenaManager.getLobby());
			loggers.remove(player.getName());
			plugin.getPlayerData().set("loggers", loggers);

			if (plugin.getConfig().getBoolean("keepInv")) {
				// Return player health, food, exp, and items
				if (plugin.getPlayerData().contains(player.getName() + ".health"))
					player.setHealth(plugin.getPlayerData().getDouble(player.getName() + ".health"));
				plugin.getPlayerData().set(player.getName() + ".health", null);
				if (plugin.getPlayerData().contains(player.getName() + ".food"))
					player.setFoodLevel(plugin.getPlayerData().getInt(player.getName() + ".food"));
				plugin.getPlayerData().set(player.getName() + ".food", null);
				if (plugin.getPlayerData().contains(player.getName() + ".saturation"))
					player.setSaturation((float) plugin.getPlayerData().getDouble(player.getName() + ".saturation"));
				plugin.getPlayerData().set(player.getName() + ".saturation", null);
				if (plugin.getPlayerData().contains(player.getName() + ".level")) {
					player.setLevel(plugin.getPlayerData().getInt(player.getName() + ".level"));
					plugin.getPlayerData().set(player.getName() + ".level", null);
				}
				if (plugin.getPlayerData().contains(player.getName() + ".exp")) {
					player.setExp((float) plugin.getPlayerData().getDouble(player.getName() + ".exp"));
					plugin.getPlayerData().set(player.getName() + ".exp", null);
				}
				if (plugin.getPlayerData().contains(player.getName() + ".inventory")) {
					Objects.requireNonNull(plugin.getPlayerData()
									.getConfigurationSection(player.getName() + ".inventory"))
							.getKeys(false)
							.forEach(num -> player.getInventory().setItem(Integer.parseInt(num),
									(ItemStack) plugin.getPlayerData().get(player.getName() + ".inventory." + num)));
					plugin.getPlayerData().set(player.getName() + ".inventory", null);
				}
			}

			plugin.savePlayerData();
		}

		// If the plugin setup is outdated, send message to admins
		if (plugin.isOutdated() && player.hasPermission("vd.admin"))
			PlayerManager.notify(player, plugin.getLanguageData().getString("outdatedError"));
	}
	
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		ArenaManager.displayAllPortals(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// Uninject player and make them leave from arena
		nmsManager.uninjectPacketListener(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));

		// Get list of loggers from data file and add player to it
		List<String> loggers = plugin.getPlayerData().getStringList("loggers");
		loggers.add(player.getName());

		// Add to list of loggers if in a game
		if (Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).anyMatch(arena -> arena.hasPlayer(player))) {
			CommunicationManager.debugInfo(player.getName() + " logged out mid-game.", 2);
			plugin.getPlayerData().set("loggers", loggers);
			plugin.savePlayerData();
		}
	}
}
