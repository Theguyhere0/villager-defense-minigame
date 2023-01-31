package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {
    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        String worldName = e.getWorld().getName();
        CommunicationManager.debugInfo("Loading world: %s", 2, worldName);

        // Handle world loading after initialization
        if (Main.getUnloadedWorlds().contains(worldName)) {
            Main.loadWorld(worldName);
            Main.resetGameManager();
            GameManager.reloadLobby();
            GameManager.refreshAll();
        }
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
        GameManager.displayEverything(e.getPlayer());
    }
}
