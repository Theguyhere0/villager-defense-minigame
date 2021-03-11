package me.theguyhere.villagerdefense.events;

import me.theguyhere.villagerdefense.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.Game;

public class ClickNPC implements Listener {
	Main plugin;
	Game game;
	
	public ClickNPC(Main plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	@EventHandler
	public void onclick(RightClickNPC event) {
		String name = event.getNPC().getName();
		Location location;
		Player player = event.getPlayer();
		try {
			location = new Location(Bukkit.getWorld(plugin.getData().getString("data.a" + name + ".spawn.world")),
					plugin.getData().getDouble("data.a" + name + ".spawn.x"), plugin.getData().getDouble("data.a" + name + ".spawn.y"),
					plugin.getData().getDouble("data.a" + name + ".spawn.z"));
		} catch (Exception e) {
			player.sendMessage(Utils.format("&cError: Arena not set up."));
			return;
		}
		if (plugin.getData().contains("data.a" + name + ".mob"))
			game.join(player, name, location);
		else player.sendMessage(Utils.format("&cError: Arena not set up."));
	}
}
