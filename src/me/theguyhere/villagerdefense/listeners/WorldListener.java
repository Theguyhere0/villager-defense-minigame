package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {
    private final Main plugin;

    public WorldListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent e) {
        plugin.debugInfo("Loading world: " + e.getWorld(), 2);
        plugin.getGame().reloadLobby();
        plugin.loadPortals();
        plugin.getLeaderboard().loadLeaderboards();
        plugin.getInfoBoard().loadInfoBoards();
        plugin.getArenaBoard().loadArenaBoards();
    }
}
