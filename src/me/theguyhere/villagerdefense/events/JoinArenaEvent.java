package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JoinArenaEvent extends Event implements Cancellable {
    private final Player player;
    private final Arena arena;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public JoinArenaEvent(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
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
