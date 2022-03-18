package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {
    private final Main plugin;

    public WorldListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        CommunicationManager.debugInfo("Loading world: " + e.getWorld(), 2);
        String worldName = e.getWorld().getName();

        // Handle world loading after initialization
        if (plugin.getUnloadedWorlds().contains(worldName)) {
            plugin.loadWorld(worldName);
            plugin.resetGameManager();
            plugin.getGameManager().reloadLobby();
            plugin.getGameManager().refreshAll();
        }
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
        GameManager.displayEverything(e.getPlayer());
    }
}
