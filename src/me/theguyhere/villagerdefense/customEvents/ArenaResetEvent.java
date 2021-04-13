package me.theguyhere.villagerdefense.customEvents;

import me.theguyhere.villagerdefense.game.models.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaResetEvent extends Event implements Cancellable {
    private final Arena arena;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public ArenaResetEvent(Arena arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
