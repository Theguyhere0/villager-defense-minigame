package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.*;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.Map;
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
        int arena = e.getArena();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Ignore if player is already in a game somehow
        if (game.playing.stream().anyMatch(p -> p.getPlayer().equals(player))) {
            e.setCancelled(true);
            return;
        }

        // Check if arena is closed
        if (plugin.getData().getBoolean("a" + arena + ".closed")) {
            player.sendMessage(Utils.format("&cArena is closed."));
            e.setCancelled(true);
            return;
        }

        Location location = new Location(
					Bukkit.getWorld(plugin.getData().getString("a" + arena + ".spawn.world")),
					plugin.getData().getDouble("a" + arena + ".spawn.x"),
					plugin.getData().getDouble("a" + arena + ".spawn.y"),
					plugin.getData().getDouble("a" + arena + ".spawn.z"));
        int players = plugin.getData().getInt("a" + arena + ".players.playing");
        int min = plugin.getData().getInt("a" + arena + ".min");
        int max = plugin.getData().getInt("a" + arena + ".max");

        // First player joining the arena
        if (players == 0) {
            // Get all nearby entities in the arena and clear them out
            if (location.getWorld() == null) {
                System.out.println("Error: Location's world is null for join method");
                player.sendMessage(Utils.format("&cSomething went wrong"));
                return;
            }
            clearArena(location);

            // Initialize arena data
            game.actives.add(new Arena(arena, new Tasks(plugin, game, arena, portal)));
        }

        // Prepares player to enter arena if it doesn't exceed max capacity or if the arena hasn't already started
        if (players < max && !plugin.getData().getBoolean("a" + arena + ".active")) {
            // Teleport to arena
            Utils.prepTeleAdventure(player);
            player.teleport(location);

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, arena, false);
            game.playing.add(fighter);
            plugin.getData().set("a" + arena + ".players.playing",
                    plugin.getData().getInt("a" + arena + ".players.playing") + 1);
            plugin.saveData();
            players = plugin.getData().getInt("a" + arena + ".players.playing");
            portal.refreshHolo(arena);

            // Give them a game board
            game.createBoard(fighter);

            // Notify everyone in the arena
            game.playing.forEach(gamer -> {
                if (gamer.getArena() == arena)
                    gamer.getPlayer().sendMessage(Utils.format("&a" + player.getName() + " joined the arena."));
            });
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena
            Utils.prepTeleSpectator(player);
            player.teleport(location);

            // Update player tracking and in-game stats
            game.playing.add(new VDPlayer(player, arena, true));
            plugin.getData().set("a" + arena + ".players.spectating",
                    plugin.getData().getInt("a" + arena + ".players.spectating") + 1);
            plugin.saveData();
            portal.refreshHolo(arena);

            // Don't touch task updating
            return;
        }

        // Get task object and mapping of active runnables to ids
        Arena arenaInstance = game.actives.stream()
                .filter(r -> r.getArena() == arena).collect(Collectors.toList()).get(0);
        Tasks task = arenaInstance.getTask();
        Map<Runnable, Integer> tasks = task.getTasks();

        // Waiting condition
        if (players < min && (tasks.isEmpty() || !scheduler.isCurrentlyRunning(tasks.get(task.waiting))) &&
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
        else if (players < max && !tasks.containsKey(task.full10) && !tasks.containsKey(task.min1)) {
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
        Arena arena = game.actives.stream().filter(r -> r.getArena() == e.getArena()).collect(Collectors.toList())
                .get(0);

        // Don't continue if the arena is ending
        if (arena.isEnding()) {
            e.setCancelled(true);
            return;
        }

        // TEMPORARY win condition
        if (plugin.getData().getInt("a" + arena + ".currentWave") == 12) {
            arena.flipEnding();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(e.getArena())));
        }

        // Start the next wave
        arena.getTask().wave.run();
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();

        // Check if the player is playing in a game
        if (game.playing.stream().noneMatch(p -> p.getPlayer().equals(player))) {
            e.setCancelled(true);
            return;
        }

        VDPlayer gamer = game.playing.stream().filter(p -> p.getPlayer().equals(player)).collect(Collectors.toList())
                .get(0);

        // Check if player is playing
        if (gamer == null) {
            player.sendMessage(Utils.format("&cYou are not in a game!"));
            return;
        }

        int arena = gamer.getArena();

        // Not spectating
        if (!gamer.isSpectating()) {
            // Update player tracking and in-game data
            game.playing.remove(gamer);
            plugin.getData().set("a" + arena + ".players.playing",
                    plugin.getData().getInt("a" + arena + ".players.playing") - 1);
            plugin.saveData();

            // Notify people in arena player left
            game.playing.forEach(fighter -> {
                if (fighter.getArena() == arena)
                    fighter.getPlayer().sendMessage(Utils.format("&c" + player.getName() + " left the arena."));
            });

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            if (plugin.getData().contains("lobby")) {
                Utils.prepTeleAdventure(player);
                Location location = new Location(Bukkit.getWorld(plugin.getData().getString("lobby.world")),
                        plugin.getData().getDouble("lobby.x"), plugin.getData().getDouble("lobby.y"),
                        plugin.getData().getDouble("lobby.z"));
                player.teleport(location);
            }

            // Kill them to leave the game
            else {
                player.getInventory().clear();
                player.setHealth(0);
            }

            // Checks if the game has ended because no players are left
            if (game.playing.stream().filter(p -> p.getPlayer().equals(player)).collect(Collectors.toList())
                    .toArray().length == 0)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)), 0);

            // Refresh the game portal
            portal.refreshHolo(arena);
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        int arena = e.getArena();

        // Set the arena to ending
        game.actives.stream().filter(r -> r.getArena() == e.getArena()).collect(Collectors.toList())
                .get(0).flipEnding();

        // Notify players that the game has ended
        game.playing.forEach(player -> {
            if (player.getArena() == arena)
                player.getPlayer().sendMessage(Utils.format("&6You made it to round &b" +
                        plugin.getData().getInt("a" + arena + ".currentWave") + "&6! Ending in 10 seconds."));
        });

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ArenaResetEvent(arena)), 200);
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent e) {
        game.actives.stream().filter(r -> r.getArena() == e.getArena()).collect(Collectors.toList()).get(0)
                .getTask().reset.run();
    }

    @EventHandler
    public void onBoardReload(ReloadBoardsEvent e) {
        game.actives.stream().filter(r -> r.getArena() == e.getArena()).collect(Collectors.toList())
                .get(0).getTask().updateBoards.run();
    }

    private void clearArena(Location location) {
        // Get all entities near spawn
        Collection<Entity> ents = location.getWorld().getNearbyEntities(location, 200, 200, 100);

        // Clear the arena for living entities
        ents.forEach(ent -> {
            if (ent instanceof LivingEntity && !(ent instanceof Player))
                if (ent.getName().contains("VD")) ((LivingEntity) ent).setHealth(0);
        });

        // Clear the arena for items
        ents.forEach(ent -> {
            if (ent instanceof Item) ent.remove();
        });
    }
}
