package me.theguyhere.villagerdefense.plugin.data.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {
    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        String worldName = e.getWorld().getName();
        CommunicationManager.debugInfo("Loading world: %s", CommunicationManager.DebugLevel.VERBOSE, worldName);

        // Handle world loading after initialization
        if (Main.plugin.getUnloadedWorlds().contains(worldName)) {
            Main.plugin.loadWorld(worldName);
            Main.plugin.resetGameManager();
            GameManager.reloadLobby();
            GameManager.refreshAll();
        }
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
        GameManager.displayEverything(e.getPlayer());
    }
}
