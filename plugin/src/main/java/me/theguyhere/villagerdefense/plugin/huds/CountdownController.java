package me.theguyhere.villagerdefense.plugin.huds;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to keep and control all boss bars used for countdowns.
 */
public class CountdownController {
	/**
	 * Collection of active countdown bars.
	 */
	private static final Map<Arena, BukkitRunnable> countdowns = new HashMap<>();

	public static void startWaveTimeLimitCountdown(Arena arena) {
		countdowns.put(arena, new BukkitRunnable() {
			double progress = 1;
			final BossBar countdownBar = Bukkit.createBossBar(
				CommunicationManager.format(
					new ColoredMessage(ChatColor.GREEN, LanguageManager.names.timeBar),
					Integer.toString(arena.getCurrentWave()),
					getFormattedTime(progress * arena.getAdjustedWaveTimeLimit())
				), BarColor.GREEN,
				BarStyle.SOLID
			);
			final double time = 1d / Calculator.minutesToSeconds(arena.getAdjustedWaveTimeLimit());
			boolean warning = false;

			@Override
			public void run() {
				// Start countdown if new
				if (progress == 1) {
					arena
						.getPlayers()
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Debug message to console
					CommunicationManager.debugInfo(
						"Starting wave time limit countdown for %s",
						CommunicationManager.DebugLevel.VERBOSE,
						arena.getName()
					);
				}

				// Trigger game end event
				else if (progress <= 0) {
					progress = 0;
					try {
						arena.endGame();
					}
					catch (ArenaException e) {
						arena.resetGame();
					}
				}

				// Update countdown bar
				else {
					// Update players to serve countdown
					countdownBar
						.getPlayers()
						.stream()
						.filter(player -> !arena
							.getVanillaPlayers()
							.contains(player))
						.forEach(countdownBar::removePlayer);
					arena
						.getPlayers()
						.stream()
						.filter(player ->
							!countdownBar
								.getPlayers()
								.contains(player.getPlayer()))
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Trigger one-minute warning
					if (progress - time <= time * Calculator.minutesToSeconds(1) && !warning) {
						countdownBar.setColor(BarColor.RED);
						if (!warning) {
							// Send warning
							arena
								.getPlayers()
								.forEach(player ->
									player
										.getPlayer()
										.sendTitle(new ColoredMessage(
												ChatColor.RED,
												LanguageManager.messages.oneMinuteWarning
											).toString(),
											null, Calculator.secondsToTicks(.5), Calculator.secondsToTicks(1.5),
											Calculator.secondsToTicks(.5)
										));

							// Set monsters, villagers, and alive players glowing when time is low
							arena.setMonsterGlow();
							arena.setVillagerGlow();
							arena.setPlayerGlow();

							warning = true;
						}
					}

					// Update visuals
					countdownBar.setProgress(progress);
					countdownBar.setTitle(CommunicationManager.format(
						new ColoredMessage(warning ? ChatColor.RED :
							ChatColor.GREEN, LanguageManager.names.timeBar),
						Integer.toString(arena.getCurrentWave()),
						getFormattedTime(progress * arena.getAdjustedWaveTimeLimit())
					));
				}

				// Count down
				progress -= time;
			}

			@Override
			public synchronized void cancel() throws IllegalStateException {
				super.cancel();
				countdownBar.removeAll();
			}
		});
		countdowns
			.get(arena)
			.runTaskTimer(Main.plugin, 0, Calculator.secondsToTicks(1));
	}

	public static void startWaitingCountdown(Arena arena) {
		countdowns.put(arena, new BukkitRunnable() {
			double progress = 1;
			final BossBar countdownBar = Bukkit.createBossBar(
				CommunicationManager.format(
					new ColoredMessage(ChatColor.YELLOW, LanguageManager.names.waitingBar),
					getFormattedTime(2)
				), BarColor.YELLOW,
				BarStyle.SEGMENTED_12
			);
			final double time = 1d / Calculator.minutesToSeconds(2);

			@Override
			public void run() {
				// Start countdown if new
				if (progress == 1) {
					arena
						.getPlayers()
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Debug message to console
					CommunicationManager.debugInfo(
						"Starting waiting countdown for %s",
						CommunicationManager.DebugLevel.VERBOSE,
						arena.getName()
					);
				}

				// Update countdown bar
				else {
					// Update players to serve countdown
					countdownBar
						.getPlayers()
						.stream()
						.filter(player -> !arena
							.getVanillaPlayers()
							.contains(player))
						.forEach(countdownBar::removePlayer);
					arena
						.getPlayers()
						.stream()
						.filter(player ->
							!countdownBar
								.getPlayers()
								.contains(player.getPlayer()))
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Update visuals
					countdownBar.setProgress(progress);
					countdownBar.setTitle(CommunicationManager.format(new ColoredMessage(
						ChatColor.YELLOW,
						LanguageManager.names.waitingBar
					), getFormattedTime(progress * 2)));
				}

				// Count down
				progress -= time;
			}

			@Override
			public synchronized void cancel() throws IllegalStateException {
				super.cancel();
				countdownBar.removeAll();
			}
		});
		countdowns
			.get(arena)
			.runTaskTimer(Main.plugin, 0, Calculator.secondsToTicks(1));
	}

	public static void startExpeditedWaitingCountdown(Arena arena) {
		countdowns.put(arena, new BukkitRunnable() {
			double progress = 1d / 12;
			final BossBar countdownBar = Bukkit.createBossBar(
				CommunicationManager.format(
					new ColoredMessage(ChatColor.YELLOW, LanguageManager.names.waitingBar),
					getFormattedTime(progress * 2)
				), BarColor.YELLOW,
				BarStyle.SEGMENTED_12
			);
			final double time = 1d / Calculator.minutesToSeconds(2);

			@Override
			public void run() {
				// Start countdown if new
				if (progress == 1) {
					arena
						.getPlayers()
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Debug message to console
					CommunicationManager.debugInfo(
						"Starting expedited waiting countdown for %s",
						CommunicationManager.DebugLevel.VERBOSE,
						arena.getName()
					);
				}

				// Update countdown bar
				else {
					// Update players to serve countdown
					countdownBar
						.getPlayers()
						.stream()
						.filter(player -> !arena
							.getVanillaPlayers()
							.contains(player))
						.forEach(countdownBar::removePlayer);
					arena
						.getPlayers()
						.stream()
						.filter(player ->
							!countdownBar
								.getPlayers()
								.contains(player.getPlayer()))
						.forEach(vdPlayer -> countdownBar.addPlayer(vdPlayer.getPlayer()));

					// Update visuals
					countdownBar.setProgress(progress);
					countdownBar.setTitle(CommunicationManager.format(new ColoredMessage(
						ChatColor.YELLOW,
						LanguageManager.names.waitingBar
					), getFormattedTime(progress * 2)));
				}

				// Count down
				progress -= time;
			}

			@Override
			public synchronized void cancel() throws IllegalStateException {
				super.cancel();
				countdownBar.removeAll();
			}
		});
		countdowns
			.get(arena)
			.runTaskTimer(Main.plugin, 0, Calculator.secondsToTicks(1));
	}

	public static void stopCountdown(Arena arena) {
		if (countdowns.containsKey(arena)) {
			countdowns
				.get(arena)
				.cancel();
			countdowns.remove(arena);
		}
	}

	private static String getFormattedTime(double time) {
		int minutes = (int) (time);
		int seconds = (int) ((time - minutes) * 60 + 0.5);
		return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
}
