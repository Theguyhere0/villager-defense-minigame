package me.theguyhere.villagerdefense.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.theguyhere.villagerdefense.NPC;
import me.theguyhere.villagerdefense.PacketReader;
import me.theguyhere.villagerdefense.game.Game;

public class Join implements Listener {
	private final NPC NPC;
	private final PacketReader reader;
	private final Game game;
	
	public Join(NPC NPC, PacketReader reader, Game game) {
		this.NPC = NPC;
		this.reader = reader;
		this.game = game;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (NPC.getNPCs() == null)
			return;
		if (NPC.getNPCs().isEmpty())
			return;
		NPC.addJoinPacket(event.getPlayer());
		
		reader.inject(event.getPlayer());
	}
	
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent event) {
		if (NPC.getNPCs() == null)
			return;
		if (NPC.getNPCs().isEmpty())
			return;
		NPC.addJoinPacket(event.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		reader.uninject(player);
		game.leave(player);
	}
}
