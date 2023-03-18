package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.JoinArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.plugin.events.RightClickNPCEvent;
import me.theguyhere.villagerdefense.plugin.GameController;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.inventories.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.stream.Collectors;

public class ClickPortalListener implements Listener {
	@EventHandler
	public void onRightClick(RightClickNPCEvent e) {
		Arena arena;

		// Try to get arena from npc
		try {
			arena = GameController.getArenas().values().stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.getPortal() != null)
					.filter(arena1 -> arena1.getPortal().getNpc().getEntityID() == e.getNpcId())
					.collect(Collectors.toList()).get(0);
		} catch (Exception err) {
			err.printStackTrace();
			return;
		}

		// Send out event of player joining
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit.getPluginManager().callEvent(new JoinArenaEvent(e.getPlayer(), arena)));
	}

	@EventHandler
	public void onLeftClick(LeftClickNPCEvent e) {
		Arena arena;

		// Try to get arena from npc
		try {
			arena = GameController.getArenas().values().stream().filter(Objects::nonNull)
					.filter(arena1 -> arena1.getPortal() != null)
					.filter(arena1 -> arena1.getPortal().getNpc().getEntityID() == e.getNpcId())
					.collect(Collectors.toList()).get(0);
		} catch (Exception err) {
			err.printStackTrace();
			return;
		}

		// Open inventory
		e.getPlayer().openInventory(Inventories.createArenaInfoMenu(arena));
	}
}
