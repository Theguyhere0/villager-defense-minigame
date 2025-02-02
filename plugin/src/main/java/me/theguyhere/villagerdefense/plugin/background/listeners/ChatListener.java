package me.theguyhere.villagerdefense.plugin.background.listeners;

import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ChatListener implements Listener {
	public interface ChatTask {
		void process(String msg);
	}
	private static final HashMap<Player, ChatTask> tasks = new HashMap<>();

	public static void addTask(Player player, ChatTask chatTask, String instructions) {
		PlayerManager.notifyAlert(player, instructions);
		tasks.put(player, chatTask);
	}

	public static void wipeTasks() {
		tasks.clear();
	}

	// Read chat messages for string inputs
	@EventHandler
	public void onPlayerMessage(AsyncPlayerChatEvent e) {
		ChatTask toProcess = tasks.get(e.getPlayer());
		if (toProcess != null) {
			toProcess.process(e.getMessage());
			tasks.remove(e.getPlayer());
			e.setCancelled(true);
		}
	}

	// Remove task for players that leave
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		tasks.remove(e.getPlayer());
	}
}
