package me.theguyhere.villagerdefense.genListeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.theguyhere.villagerdefense.game.Portal;
import me.theguyhere.villagerdefense.tools.PacketReader;

public class Death implements Listener {
	private final Portal portal;
	private final PacketReader reader;
	
	public Death(Portal portal, PacketReader reader) {
		this.portal = portal;
		this.reader = reader;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		reader.uninject(event.getEntity());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (portal.getNPCs() == null)
			return;
		portal.addJoinPacket(event.getPlayer());
		
		reader.inject(event.getPlayer());
	}
}
