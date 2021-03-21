package me.theguyhere.villagerdefense.customEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaResetEvent extends Event implements Cancellable {
    private final int arena;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public ArenaResetEvent(int arena) {
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public int getArena() {
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
