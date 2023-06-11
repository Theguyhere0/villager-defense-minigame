package me.theguyhere.villagerdefense.plugin.background;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.guis.InventoryMeta;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		CommunicationManager.debugInfo("InventoryDragEvent inventory type: " + e.getInventory().getType(),
			CommunicationManager.DebugLevel.VERBOSE);
		if (e.getInventory().getHolder() instanceof InventoryMeta) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			CommunicationManager.debugInfo(
				"InventoryDragEvent custom inventory: " + meta.getInventoryID(),
				CommunicationManager.DebugLevel.VERBOSE
			);
		}
		else CommunicationManager.debugInfo("InventoryDragEvent custom inventory: Not custom",
			CommunicationManager.DebugLevel.VERBOSE);
		CommunicationManager.debugInfo("InventoryDragEvent inventory slots: " + e.getInventorySlots(),
			CommunicationManager.DebugLevel.VERBOSE);
		CommunicationManager.debugInfo("InventoryDragEvent raw slots: " + e.getRawSlots(),
			CommunicationManager.DebugLevel.VERBOSE);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		CommunicationManager.debugInfo("InventoryClickEvent inventory type: " + e.getInventory().getType(),
			CommunicationManager.DebugLevel.VERBOSE);
		CommunicationManager.debugInfo("InventoryClickEvent click type: " + e.getClick(),
			CommunicationManager.DebugLevel.VERBOSE);
		CommunicationManager.debugInfo(
			"InventoryClickEvent inventory item: " + e.getCurrentItem(),
			CommunicationManager.DebugLevel.VERBOSE
		);
		CommunicationManager.debugInfo("InventoryClickEvent cursor item: " + e.getCursor(),
			CommunicationManager.DebugLevel.VERBOSE);
		CommunicationManager.debugInfo(
			"InventoryClickEvent clicked inventory: " + e.getClickedInventory(),
			CommunicationManager.DebugLevel.VERBOSE
		);
		CommunicationManager.debugInfo("InventoryClickEvent inventory name: ",
			CommunicationManager.DebugLevel.VERBOSE, e.getView().getTitle());

		if (e.getInventory().getHolder() instanceof InventoryMeta) {
			InventoryMeta meta = (InventoryMeta) e.getInventory().getHolder();
			CommunicationManager.debugInfo(
				"InventoryClickEvent custom inventory: " + meta.getInventoryID(),
				CommunicationManager.DebugLevel.VERBOSE
			);
		}
		else CommunicationManager.debugInfo("InventoryClickEvent custom inventory: Not custom",
			CommunicationManager.DebugLevel.VERBOSE);
	}
}
