package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeathListener implements Listener {
	private final Main plugin;

	public DeathListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		plugin.getReader().uninject(e.getEntity());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Game.displayAllPortals(e.getPlayer());
		Game.displayAllArenaBoards(e.getPlayer());
		Game.displayAllInfoBoards(e.getPlayer());
		plugin.getReader().inject(e.getPlayer());
	}
}
