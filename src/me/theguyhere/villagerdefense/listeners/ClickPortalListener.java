package me.theguyhere.villagerdefense.listeners;

import me.theguyhere.villagerdefense.GUI.Inventories;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.events.JoinArenaEvent;
import me.theguyhere.villagerdefense.events.LeftClickNPCEvent;
import me.theguyhere.villagerdefense.events.RightClickNPCEvent;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.Game;
import me.theguyhere.villagerdefense.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClickPortalListener implements Listener {
	@EventHandler
	public void onRightClick(RightClickNPCEvent event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(Game.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
					.map(Portal::getNPC).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Send out event of player joining
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
				Bukkit.getPluginManager().callEvent(new JoinArenaEvent(event.getPlayer(),
						Game.arenas[arena])));
	}

	@EventHandler
	public void onLeftClick(LeftClickNPCEvent event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(Game.arenas).filter(Objects::nonNull).map(Arena::getPortal).filter(Objects::nonNull)
					.map(Portal::getNPC).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Open inventory
		event.getPlayer().openInventory(Inventories.createArenaInfoInventory(
				Game.arenas[arena]
		));
	}
}
