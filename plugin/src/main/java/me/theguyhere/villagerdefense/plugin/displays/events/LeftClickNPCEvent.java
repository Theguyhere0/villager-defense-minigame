package me.theguyhere.villagerdefense.plugin.displays.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class LeftClickNPCEvent extends Event implements Cancellable {
	@Getter
    private final Player player;
	@Getter
    private final int npcId;
	private boolean isCancelled;
	private static final HandlerList HANDLERS = new HandlerList();

	public LeftClickNPCEvent(Player player, int npcId) {
		this.player = player;
		this.npcId = npcId;
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
