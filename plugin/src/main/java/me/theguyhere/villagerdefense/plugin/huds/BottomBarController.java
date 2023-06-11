package me.theguyhere.villagerdefense.plugin.huds;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to manage the bottom bars of each player.
 */
public class BottomBarController {
	// Collection of active bottom bar runnables.
	private static final Map<Player, BukkitRunnable> runnables = new HashMap<>();

	// Collection of texts to display on the bottom bar.
	private static final Map<Player, String> texts = new HashMap<>();

	public static void startBottomBar(Player player, String text) {
		// Only start if not already started
		if (!runnables.containsKey(player)) {
			texts.put(player, text);
			runnables.put(player, new BukkitRunnable() {
				@Override
				public void run() {
					player
						.spigot()
						.sendMessage(
							ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(texts.get(player))
						);
				}
			});
		}
		runnables
			.get(player)
			.runTaskTimer(Main.plugin, 0, 1);
	}

	public static void updateBottomBar(Player player, String text) {
		texts.put(player, text);
	}

	public static void stopBottomBar(Player player) {
		// Only stop if it existed in the first place
		if (runnables.containsKey(player)) {
			runnables
				.get(player)
				.cancel();
			runnables.remove(player);
			texts.remove(player);
		}
	}

	public static void startStatusBar(VDPlayer player) {
		// Only start if not already started
		if (!runnables.containsKey(player.getPlayer())) {
			runnables.put(player.getPlayer(), new BukkitRunnable() {
				@Override
				public void run() {
					player
						.getPlayer()
						.spigot()
						.sendMessage(
							ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(player.getStatusBar())
						);
				}
			});
		}
		runnables
			.get(player.getPlayer())
			.runTaskTimer(Main.plugin, 0, 1);
	}
}
