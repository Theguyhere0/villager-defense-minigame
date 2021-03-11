package me.theguyhere.villagerdefense.events;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RightClickNPC extends Event implements Cancellable {
	private final Player player;
	private final EntityPlayer npc;
	private boolean isCancelled;
	private static final HandlerList HANDLERS = new HandlerList();
	
	public RightClickNPC(Player player, EntityPlayer npc) {
		this.player = player;
		this.npc = npc;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public EntityPlayer getNPC() {
		return npc;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg) {
		isCancelled = arg;
	}
}
