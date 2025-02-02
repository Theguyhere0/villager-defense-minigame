package me.theguyhere.villagerdefense.plugin.game.kits.events;

import lombok.Getter;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EndNinjaNerfEvent extends Event implements Cancellable {
    @Getter
    private final VDPlayer gamer;
    private boolean isCancelled;
    private static final HandlerList HANDLERS = new HandlerList();

    public EndNinjaNerfEvent(VDPlayer gamer) {
        this.gamer = gamer;
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
    public void setCancelled(boolean b) {
        isCancelled = b;
    }
}
