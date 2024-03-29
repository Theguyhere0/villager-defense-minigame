package me.theguyhere.villagerdefense.plugin.events;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WaveStartEvent extends Event implements Cancellable {
    private final Arena arena;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public WaveStartEvent(Arena arena) {
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
