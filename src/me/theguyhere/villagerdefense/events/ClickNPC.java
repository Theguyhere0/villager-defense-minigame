package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.Portal;
import me.theguyhere.villagerdefense.tools.Utils;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.Game;

import javax.sound.sampled.Port;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClickNPC implements Listener {
	private final Main plugin;
	private final Game game;
	private final Portal portal;
	
	public ClickNPC(Main plugin, Game game, Portal portal) {
		this.plugin = plugin;
		this.game = game;
		this.portal = portal;
	}
	
	@EventHandler
	public void onclick(RightClickNPC event) {
		int arena;

		// Try to get arena from npc
		try {
			arena = Arrays.stream(portal.getNPCs()).collect(Collectors.toList()).indexOf(event.getNPC());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Player player = event.getPlayer();

		// Initiate player join if arena isn't closed
		if (!plugin.getData().getBoolean("a" + arena + ".closed"))
			game.join(player, Integer.toString(arena), new Location(
					Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
					plugin.getData().getDouble("a" + arena + ".spawn.x"),
					plugin.getData().getDouble("a" + arena + ".spawn.y"),
					plugin.getData().getDouble("a" + arena + ".spawn.z")));
		else player.sendMessage(Utils.format("&cArena is closed."));
	}
}
