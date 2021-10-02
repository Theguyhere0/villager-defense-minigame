package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.displays.Portal;
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
	public void onDeath(PlayerDeathEvent event) {
		plugin.getReader().uninject(event.getEntity());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Portal.addJoinPacket(event.getPlayer());
		
		plugin.getReader().inject(event.getPlayer());
	}
}
