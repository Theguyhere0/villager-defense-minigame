package me.theguyhere.villagerdefense.game.listeners;

import me.theguyhere.villagerdefense.customEvents.JoinArenaEvent;
import me.theguyhere.villagerdefense.customEvents.RightClickNPCEvent;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.displays.Portal;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.theguyhere.villagerdefense.Main;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ClickPortalEvents implements Listener {
	private final Game game;
	private final Portal portal;
	
	public ClickPortalEvents(Game game, Portal portal) {
		this.game = game;
		this.portal = portal;
	}
	
	@EventHandler
	public void onclick(RightClickNPCEvent event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(portal.getNPCs()).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Send out event of player joining
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit.getPluginManager().callEvent(new JoinArenaEvent(event.getPlayer(), game.arenas.get(arena))));
	}
}
