package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.arenas.ArenaManager;
import me.theguyhere.villagerdefense.tools.CommunicationManager;
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
        plugin.getArenaManager().reloadLobby();
        plugin.getArenaManager().refreshAll();
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
        ArenaManager.displayEverything(e.getPlayer());
    }
}
