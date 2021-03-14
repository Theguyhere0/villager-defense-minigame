package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.DynamicHolo;
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
	private final DynamicHolo HOLO;
	private final PacketReader reader;
	private final Game game;
	
	public Join(NPC NPC, DynamicHolo HOLO, PacketReader reader, Game game) {
		this.NPC = NPC;
		this.HOLO = HOLO;
		this.reader = reader;
		this.game = game;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (NPC.getNPCs() == null)
			return;
		if (NPC.getNPCs().isEmpty())
			return;
		NPC.addJoinPacket(e.getPlayer());
		HOLO.addJoinPacket(e.getPlayer());
		
		reader.inject(e.getPlayer());
	}
	
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		if (NPC.getNPCs() == null)
			return;
		if (NPC.getNPCs().isEmpty())
			return;
		NPC.addJoinPacket(e.getPlayer());
		HOLO.addJoinPacket(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		reader.uninject(player);
		game.leave(player);
	}
}
