package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.JoinArenaEvent;
import me.theguyhere.villagerdefense.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.events.RightClickNPCEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClickPortalListener implements Listener {
	private final Main plugin;
	
	public ClickPortalListener(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onRightClick(RightClickNPCEvent event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(plugin.getPortal().getNPCs()).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Send out event of player joining
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit.getPluginManager().callEvent(new JoinArenaEvent(event.getPlayer(),
						plugin.getGame().arenas.get(arena))));
	}

	@EventHandler
	public void onLeftClick(LeftClickNPCEvent event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(plugin.getPortal().getNPCs()).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Open inventory
		event.getPlayer().openInventory(plugin.getInventories().createArenaInfoInventory(
				plugin.getGame().arenas.get(arena)
		));
	}
}
