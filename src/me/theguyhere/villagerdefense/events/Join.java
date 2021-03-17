package me.theguyhere.villagerdefense.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.PacketReader;
import me.theguyhere.villagerdefense.game.Game;

public class Join implements Listener {
	private final Portal portal;
	private final PacketReader reader;
	private final Game game;
	
	public Join(Portal portal, PacketReader reader, Game game) {
		this.portal = portal;
		this.reader = reader;
		this.game = game;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (portal.getNPCs() == null)
			return;
		portal.addJoinPacket(e.getPlayer());
		reader.inject(e.getPlayer());
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
		reader.uninject(player);
		game.leave(player);
	}
}
