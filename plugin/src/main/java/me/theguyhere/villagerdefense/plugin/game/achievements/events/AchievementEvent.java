package me.theguyhere.villagerdefense.plugin.game.achievements.events;

import lombok.Getter;
import me.theguyhere.villagerdefense.plugin.game.achievements.Achievement;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AchievementEvent extends Event implements Cancellable {
    @Getter
    private final Player player;
    @Getter
    private final Achievement achievement;
    private boolean isCancelled = false;
    private static final HandlerList HANDLERS = new HandlerList();

    AchievementEvent(Player player, Achievement achievement) {
        this.player = player;
        this.achievement = achievement;
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
