package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.tools.PacketReader;

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
		if (plugin.getPortal().getNPCs() == null)
			return;
		plugin.getPortal().addJoinPacket(event.getPlayer());
		
		plugin.getReader().inject(event.getPlayer());
	}
}
