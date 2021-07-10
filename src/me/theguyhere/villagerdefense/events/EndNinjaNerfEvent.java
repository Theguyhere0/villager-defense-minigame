package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.game.models.VDPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EndNinjaNerfEvent extends Event implements Cancellable {
    private final VDPlayer gamer;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public EndNinjaNerfEvent(VDPlayer gamer) {
        this.gamer = gamer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public VDPlayer getGamer() {
        return gamer;
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
