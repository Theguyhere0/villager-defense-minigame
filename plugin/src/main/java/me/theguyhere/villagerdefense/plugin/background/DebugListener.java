package me.theguyhere.villagerdefense.plugin.background;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * The listener that displays debugging information from events.
 */
public class DebugListener implements Listener {
	@EventHandler
	public void onWorldLoadEvent(WorldLoadEvent e) {
		String worldName = e.getWorld().getName();
		CommunicationManager.debugInfo("Loading world: %s", CommunicationManager.DebugLevel.VERBOSE, worldName);
	}
}
