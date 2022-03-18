package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.tools.Utils;
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
        Utils.debugInfo("Loading world: " + e.getWorld(), 2);
        plugin.getGame().reloadLobby();
        plugin.getGame().refreshAll();
    }

    @EventHandler
    public void onPlayerChangeWorldEvent(PlayerChangedWorldEvent e) {
        Game.displayEverything(e.getPlayer());
    }
}
