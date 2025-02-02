package me.theguyhere.villagerdefense.plugin.background.listeners;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.game.events.GameEndEvent;
import me.theguyhere.villagerdefense.plugin.arenas.events.WaveEndEvent;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.arenas.ArenaStatus;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEffectsListener implements Listener {
    @EventHandler
    public void onWaveComplete(WaveEndEvent e) {
        Arena arena = e.getArena();

        // Don't continue if the arena is not active
        if (arena.getStatus() != ArenaStatus.ACTIVE)
            return;

        // Ignore if not on at least wave 1
        if (arena.getCurrentWave() < 1)
            return;


        ConfigurationSection limited = Main.getCustomEffects()
                .getConfigurationSection("limited.onWaveComplete");
        ConfigurationSection unlimited = Main.getCustomEffects()
                .getConfigurationSection("unlimited.onWaveComplete");

        // Check custom effects for limited wave arenas
        if (arena.getMaxWaves() > 0 && limited != null)
            limited.getKeys(false).forEach(key -> {
                try {
                    String command = limited.getString(key);
                    if (arena.getCurrentWave() - 1 == Integer.parseInt(key) && command != null)
                        arena.getActives().forEach(player ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")));
                } catch (Exception ignored) {
                }
            });

        // Check custom effects for unlimited wave arenas
        if (arena.getMaxWaves() < 0 && unlimited != null)
            unlimited.getKeys(false).forEach(key -> {
                try {
                    String command = unlimited.getString(key);
                    if (arena.getCurrentWave() - 1 == Integer.parseInt(key) && command != null)
                        arena.getActives().forEach(player ->
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")));
                } catch (Exception ignored) {
                }
            });
    }

    @EventHandler
    public void onGameWin(GameEndEvent e) {
        Arena arena = e.getArena();
        ConfigurationSection section = Main.getCustomEffects()
                .getConfigurationSection("limited");

        // Check for limited waves
        if (arena.getMaxWaves() < 0)
            return;

        // Check for win
        if (arena.getCurrentWave() <= arena.getMaxWaves())
            return;

        // Schedule commands to run after win
        if (section != null)
            section.getStringList("onGameWin").forEach(command -> {
                if (command != null)
                    arena.getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                            Main.plugin,
                            () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    command.replace("%player%", player.getPlayer().getName())
                                            .replaceFirst("/", "")),
                            Calculator.secondsToTicks(12.5)
                    ));
            });
    }

    @EventHandler
    public void onGameLose(GameEndEvent e) {
        Arena arena = e.getArena();
        ConfigurationSection section = Main.getCustomEffects()
                .getConfigurationSection("limited");

        // Check for limited waves
        if (arena.getMaxWaves() < 0)
            return;

        // Check for lose
        if (arena.getCurrentWave() > arena.getMaxWaves())
            return;

        // Schedule commands to run after lose
        if (section != null)
            section.getStringList("onGameLose").forEach(command -> {
                if (command != null)
                    arena.getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                            Main.plugin,
                            () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    command.replace("%player%", player.getPlayer().getName())
                                            .replaceFirst("/", "")),
                            Calculator.secondsToTicks(12.5)
                    ));
            });
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Arena arena = e.getArena();
        ConfigurationSection section = Main.getCustomEffects()
                .getConfigurationSection("unlimited.onGameEnd");

        // Check for unlimited waves
        if (arena.getMaxWaves() > 0)
            return;

        // Schedule commands to run after end
        if (section != null)
            section.getKeys(false).forEach(key -> {
                try {
                    String command = section.getString(key);
                    assert command != null;

                    // Check upper boundaries
                    if (key.contains("<") && arena.getCurrentWave() < Integer.parseInt(key.substring(1)))
                        arena.getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                Main.plugin,
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")),
                                Calculator.secondsToTicks(12.5)
                        ));

                    // Check lower boundaries
                    else if (key.contains("^") && arena.getCurrentWave() > Integer.parseInt(key.substring(1)))
                        arena.getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                Main.plugin,
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")),
                                Calculator.secondsToTicks(12.5)
                        ));

                    // Check range
                    else if (key.contains("-") && arena.getCurrentWave() <= Integer.parseInt(key.split("-")[1]) &&
                            arena.getCurrentWave() >= Integer.parseInt(key.split("-")[0]))
                        arena.getActives().forEach(player -> Bukkit.getScheduler().scheduleSyncDelayedTask(
                                Main.plugin,
                                () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        command.replace("%player%", player.getPlayer().getName())
                                                .replaceFirst("/", "")),
                                Calculator.secondsToTicks(12.5)
                        ));
                } catch (Exception ignored) {
                }
            });
    }
}
