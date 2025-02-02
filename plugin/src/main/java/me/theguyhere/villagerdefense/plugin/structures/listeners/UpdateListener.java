package me.theguyhere.villagerdefense.plugin.structures.listeners;

import me.theguyhere.villagerdefense.plugin.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class UpdateListener implements Listener {
    @EventHandler
    public void onPortal(PlayerChangedWorldEvent e) {
        GameManager.displayAllPortals(e.getPlayer());
    }
}
