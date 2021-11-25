package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;
import java.util.Objects;

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
		Arrays.stream(Game.arenas).filter(Objects::nonNull).map(Arena::getPortal)
				.filter(Objects::nonNull).forEach(portal -> portal.displayForPlayer(e.getPlayer()));
		
		plugin.getReader().inject(e.getPlayer());
	}
}
