package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.*;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ArenaEvents implements Listener {
    private final Main plugin;
    private final Game game;
    private final Portal portal;

    public ArenaEvents(Main plugin, Game game, Portal portal) {
        this.plugin = plugin;
        this.game = game;
        this.portal = portal;
    }

    @EventHandler
    public void onJoin(JoinArenaEvent e) {
        Player player = e.getPlayer();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Ignore if player is already in a game somehow
        if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player))) {
            e.setCancelled(true);
            return;
        }

        Arena arena = e.getArena();
        Location location;

        // Try to get player spawn
        try {
            location = arena.getPlayerSpawn();
        } catch (Exception err) {
            err.printStackTrace();
            player.sendMessage(Utils.format("&cSomething went wrong"));
            return;
        }

        // Check if arena is closed
        if (arena.isClosed()) {
            player.sendMessage(Utils.format("&cArena is closed."));
            e.setCancelled(true);
            return;
        }

        int players = arena.getActiveCount();

        // First player joining the arena
        if (players == 0) {
            // Get all nearby entities in the arena and clear them out
            Utils.clear(location);
        }

        // Prepares player to enter arena if it doesn't exceed max capacity or if the arena hasn't already started
        if (players < arena.getMaxPlayers() && !arena.isActive()) {
            // Teleport to arena
            Utils.prepTeleAdventure(player);
            player.teleport(location);

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            portal.refreshHolo(game.arenas.indexOf(arena), game);

            // Give them a game board
            game.createBoard(fighter);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    gamer.getPlayer().sendMessage(Utils.format("&a" + player.getName() + " joined the arena.")));
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena
            Utils.prepTeleSpectator(player);
            player.teleport(location);

            // Update player tracking and in-game stats
            arena.getPlayers().add(new VDPlayer(player, true));
            portal.refreshHolo(game.arenas.indexOf(arena), game);

            // Don't touch task updating
            return;
        }

        players = arena.getActiveCount();

        // Get task object and mapping of active runnables to ids
        Tasks task = arena.getTask();
        Map<Runnable, Integer> tasks = task.getTasks();

        // Waiting condition
        if (players < arena.getMinPlayers() &&
                (tasks.isEmpty() || !scheduler.isCurrentlyRunning(tasks.get(task.waiting))) &&
                !tasks.containsKey(task.full10)) {
            // Remove other tasks that's not the waiting task
            tasks.forEach((runnable, id) -> {
                if (!runnable.equals(task.waiting)) {
                    scheduler.cancelTask(id);
                    tasks.remove(runnable);
                }
            });

            // Schedule and record the waiting task
            tasks.put(task.waiting, scheduler.scheduleSyncRepeatingTask(plugin, task.waiting, 0, 1200));
        }

        // Can start condition
        else if (players < arena.getMaxPlayers() && !tasks.containsKey(task.full10) &&
                !tasks.containsKey(task.min1)) {
            // Remove the waiting task if it exists
            if (tasks.containsKey(task.waiting)) {
                scheduler.cancelTask(tasks.get(task.waiting));
                tasks.remove(task.waiting);
            }

            // Schedule all the countdown tasks
            task.min2.run();
            tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(plugin, task.min1, 1200));
            tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(plugin, task.sec30, 1800));
            tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10, 2200));
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5, 2300));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start, 2400));
        }

        // Quick start condition
        else if (tasks.isEmpty() || scheduler.isCurrentlyRunning(tasks.get(task.sec10))) {
            // Remove all tasks
            tasks.forEach((runnable, id) -> {
                scheduler.cancelTask(id);
                tasks.remove(runnable);
            });

            // Schedule accelerated countdown tasks
            task.full10.run();
            tasks.put(task.full10, 0); // Dummy task id to note that quick start condition was hit
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5, 100));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start, 200));
        }
    }

    @EventHandler
    public void onWaveEnd(WaveEndEvent e) {
        Arena arena = e.getArena();

        // Don't continue if the arena is ending
        if (arena.isEnding()) {
            e.setCancelled(true);
            return;
        }

        // TEMPORARY win condition
        if (arena.getCurrentWave() == 12)
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

        // Start the next wave
        else arena.getTask().wave.run();
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();

        // Check if the player is playing in a game
        if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) {
            e.setCancelled(true);
            player.sendMessage(Utils.format("&cYou are not in a game!"));
            return;
        }

        Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                .collect(Collectors.toList()).get(0);
        VDPlayer gamer = arena.getPlayer(player);

        // Not spectating
        if (!gamer.isSpectating()) {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Notify people in arena player left
            arena.getPlayers().forEach(fighter ->
                    fighter.getPlayer().sendMessage(Utils.format("&c" + player.getName() + " left the arena.")));

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            if (game.getLobby() != null) {
                Utils.prepTeleAdventure(player);
                player.teleport(game.getLobby());
            }

            // Kill them to leave the game
            else {
                player.getInventory().clear();
                player.setHealth(0);
            }

            // Checks if the game has ended because no players are left
            if (arena.getAlive() == 0 && arena.isActive())
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));

            // Refresh the game portal
            portal.refreshHolo(game.arenas.indexOf(arena), game);
        }

        // Spectating
        else {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Sets them up for teleport to lobby
            if (game.getLobby() != null) {
                Utils.prepTeleAdventure(player);
                player.teleport(game.getLobby());
            }

            // Kill them to leave the game
            else {
                player.getInventory().clear();
                player.setHealth(0);
            }

            // Refresh the game portal
            portal.refreshHolo(game.arenas.indexOf(arena), game);
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Arena arena = e.getArena();

        // Set the arena to ending
        arena.flipEnding();
        portal.refreshHolo(game.arenas.indexOf(arena), game);

        // Notify players that the game has ended
        arena.getPlayers().forEach(player ->
            player.getPlayer().sendMessage(Utils.format("&6You made it to round &b" +
                    arena.getCurrentWave() + "&6! Ending in 10 seconds.")));

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ArenaResetEvent(arena)), 200);
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent e) {
        e.getArena().getTask().reset.run();
    }

    @EventHandler
    public void onBoardReload(ReloadBoardsEvent e) {
        e.getArena().getTask().updateBoards.run();
    }
}
