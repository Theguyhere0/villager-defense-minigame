package me.theguyhere.villagerdefense.genListeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.tools.PacketReader;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public class Join implements Listener {
	private final Main plugin;
	private final Portal portal;
	private final PacketReader reader;
	private final Game game;

	public Join(Main plugin, Portal portal, PacketReader reader, Game game) {
		this.plugin = plugin;
		this.portal = portal;
		this.reader = reader;
		this.game = game;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (portal.getNPCs() == null)
			return;
		Player player = e.getPlayer();
		portal.addJoinPacket(player);
		reader.inject(player);

		// Get list of loggers from data file
		List<String> loggers = plugin.getPlayerData().getStringList("loggers");

		if (loggers.contains(player.getName())) {
			Utils.teleAdventure(player, game.getLobby());
			loggers.remove(player.getName());
			plugin.getPlayerData().set("loggers", loggers);
			plugin.savePlayerData();
		}
	}
	
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		if (portal.getNPCs() == null)
			return;
		portal.addJoinPacket(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// Uninject player and make them leave from arena
		reader.uninject(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));

		// Get list of loggers from data file and add player to it
		List<String> loggers = plugin.getPlayerData().getStringList("loggers");
		loggers.add(player.getName());

		// Add to list of loggers if in a game
		if (game.arenas.stream().filter(Objects::nonNull).anyMatch(arena -> arena.hasPlayer(player))) {
			plugin.getPlayerData().set("loggers", loggers);
			plugin.savePlayerData();
		}
	}
}
