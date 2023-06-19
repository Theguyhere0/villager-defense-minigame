package me.theguyhere.villagerdefense.plugin.displays;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NPCLeftClickEvent extends Event implements Cancellable {
	private final Player player;
	private final int npcId;
	private boolean isCancelled = false;
	private static final HandlerList HANDLERS = new HandlerList();

	public NPCLeftClickEvent(Player player, int npcId) {
		this.player = player;
		this.npcId = npcId;
	}

	public Player getPlayer() {
		return player;
	}

	public int getNpcId() {
		return npcId;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
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
