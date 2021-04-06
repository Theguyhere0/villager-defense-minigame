package me.theguyhere.villagerdefense.genListeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.LeaveArenaEvent;
import me.theguyhere.villagerdefense.game.Portal;
import me.theguyhere.villagerdefense.tools.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Join implements Listener {
	private final Main plugin;
	private final Portal portal;
	private final PacketReader reader;

	public Join(Main plugin, Portal portal, PacketReader reader) {
		this.plugin = plugin;
		this.portal = portal;
		this.reader = reader;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (portal.getNPCs() == null)
			return;
		portal.addJoinPacket(e.getPlayer());
		reader.inject(e.getPlayer());
	}
	
	@EventHandler
	public void onPortal(PlayerChangedWorldEvent e) {
		if (portal.getNPCs() == null)
			return;
		portal.addJoinPacket(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		reader.uninject(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
				Bukkit.getPluginManager().callEvent(new LeaveArenaEvent(player)));
	}
}
