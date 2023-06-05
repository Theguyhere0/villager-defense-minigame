package me.theguyhere.villagerdefense.plugin.achievements;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AchievementEvent extends Event implements Cancellable {
    private final Player player;
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

    public Player getPlayer() {
        return player;
    }

    public Achievement getAchievement() {
        return achievement;
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
