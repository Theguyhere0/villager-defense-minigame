package me.theguyhere.villagerdefense.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.theguyhere.villagerdefense.NPC;
import me.theguyhere.villagerdefense.PacketReader;

public class Death implements Listener {
	private final NPC NPC;
	private final PacketReader reader;
	
	public Death(NPC NPC, PacketReader reader) {
		this.NPC = NPC;
		this.reader = reader;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		reader.uninject(event.getEntity());
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (NPC.getNPCs() == null)
			return;
		if (NPC.getNPCs().isEmpty())
			return;
		NPC.addJoinPacket(event.getPlayer());
		
		reader.inject(event.getPlayer());
	}
}
