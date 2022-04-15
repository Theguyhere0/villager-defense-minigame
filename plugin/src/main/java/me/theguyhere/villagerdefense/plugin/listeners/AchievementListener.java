package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AchievementListener implements Listener {
    private final Main plugin;

    public AchievementListener(Main plugin) {
        this.plugin = plugin;
    }

    // Check achievements for joining players in case they were missed
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

    }
}
