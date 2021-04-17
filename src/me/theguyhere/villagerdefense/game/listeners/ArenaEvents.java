package me.theguyhere.villagerdefense.game.listeners;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.*;
import me.theguyhere.villagerdefense.game.*;
import me.theguyhere.villagerdefense.game.displays.ArenaBoard;
import me.theguyhere.villagerdefense.game.displays.Leaderboard;
import me.theguyhere.villagerdefense.game.displays.Portal;
import me.theguyhere.villagerdefense.game.models.*;
import me.theguyhere.villagerdefense.tools.DataManager;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaEvents implements Listener {
    private final Main plugin;
    private final Game game;
    private final Portal portal;
    private final Leaderboard leaderboard;
    private final ArenaBoard arenaBoard;

    public ArenaEvents(Main plugin, Game game, Portal portal, Leaderboard leaderboard, ArenaBoard arenaBoard) {
        this.plugin = plugin;
        this.game = game;
        this.portal = portal;
        this.leaderboard = leaderboard;
        this.arenaBoard = arenaBoard;
    }

    @EventHandler
    public void onJoin(JoinArenaEvent e) {
        Player player = e.getPlayer();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Ignore if player is already in a game somehow
        if (game.arenas.stream().filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player))) {
            e.setCancelled(true);
            player.sendMessage(Utils.notify("&cYou're already in a game???"));
            return;
        }

        Arena arena = e.getArena();
        Location location;

        // Try to get waiting room
        try {
            location = arena.getWaitingRoom();
        } catch (Exception err) {
            location = null;
        }

        // Try to get player spawn
        if (location == null)
            try {
                location = arena.getPlayerSpawn();
            } catch (Exception err) {
                err.printStackTrace();
                player.sendMessage(Utils.notify("&cSomething went wrong"));
                return;
            }

        // Check if arena is closed
        if (arena.isClosed()) {
            player.sendMessage(Utils.notify("&cArena is closed."));
            e.setCancelled(true);
            return;
        }

        int players = arena.getActiveCount();

        // Save player exp and items before going into arena
        plugin.getPlayerData().set(player.getName() + ".level", player.getLevel());
        plugin.getPlayerData().set(player.getName() + ".exp", (double) player.getExp());
        for (int i = 0; i < player.getInventory().getContents().length; i++)
            plugin.getPlayerData().set(player.getName() + ".inventory." + i, player.getInventory().getContents()[i]);
        plugin.savePlayerData();

        // First player joining the arena
        if (players == 0) {
            // Get all nearby entities in the arena and clear them out
            Utils.clear(location);
        }

        // Prepares player to enter arena if it doesn't exceed max capacity or if the arena hasn't already started
        if (players < arena.getMaxPlayers() && !arena.isActive()) {
            // Teleport to arena or waiting room
            Utils.teleAdventure(player, location);
            player.setInvulnerable(true);

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            portal.refreshHolo(game.arenas.indexOf(arena), game);

            // Give them a game board
            game.createBoard(fighter);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    gamer.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&a joined the arena.")));
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena or waiting room
            Utils.teleSpectator(player, arena.getPlayerSpawn());

            // Update player tracking and in-game stats
            arena.getPlayers().add(new VDPlayer(player, true));
            portal.refreshHolo(game.arenas.indexOf(arena), game);

            // Don't touch task updating
            return;
        }

        players = arena.getActiveCount();
        Tasks task = arena.getTask();
        Map<Runnable, Integer> tasks = task.getTasks();
        List<Runnable> toRemove = new ArrayList<>();

        // Waiting condition
        if (players < arena.getMinPlayers() &&
                (tasks.isEmpty() || !scheduler.isCurrentlyRunning(tasks.get(task.waiting))) &&
                !tasks.containsKey(task.full10)) {

            // Remove other tasks that's not the waiting task
            tasks.forEach((runnable, id) -> {
                if (!runnable.equals(task.waiting)) toRemove.add(runnable);
            });
            toRemove.forEach(r -> {
                scheduler.cancelTask(tasks.get(r));
                tasks.remove(r);
            });

            // Schedule and record the waiting task
            tasks.put(task.waiting, scheduler.scheduleSyncRepeatingTask(plugin, task.waiting, 0,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1))));
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
            tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(plugin, task.min1,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1))));
            tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(plugin, task.sec30,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1) - 30)));
            tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1) - 10)));
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1) - 5)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2))));
        }

        // Quick start condition
        else if (tasks.isEmpty() || scheduler.isCurrentlyRunning(tasks.get(task.sec10))) {
            // Remove all tasks
            tasks.forEach((runnable, id) -> scheduler.cancelTask(id));
            tasks.clear();

            // Schedule accelerated countdown tasks
            task.full10.run();
            tasks.put(task.full10, 0); // Dummy task id to note that quick start condition was hit
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5, Utils.secondsToTicks(5)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start, Utils.secondsToTicks(10)));
        }
    }

    @EventHandler
    public void onWaveEnd(WaveEndEvent e) {
        Arena arena = e.getArena();
        DataManager data = new DataManager(plugin, "spawnTables/default.yml");

        // Don't continue if the arena is ending
        if (arena.isEnding()) {
            e.setCancelled(true);
            return;
        }

        Tasks task = arena.getTask();
        Map<Runnable, Integer> tasks = task.getTasks();

        // Remove time limit bar
        if (tasks.containsKey(task.updateBar)) {
            Bukkit.getScheduler().cancelTask(tasks.get(task.updateBar));
            tasks.remove(task.updateBar);
            arena.removeTimeLimitBar();
        }

        FileConfiguration playerData = plugin.getPlayerData();

        // Update player stats
        for (VDPlayer active : arena.getActives())
            if (playerData.getInt(active.getPlayer().getName() + ".topWave") < arena.getCurrentWave())
                playerData.set(active.getPlayer().getName() + ".topWave", arena.getCurrentWave());
        plugin.savePlayerData();

        // Win condition
        if (arena.getCurrentWave() == arena.getMaxWaves() ||
                !data.getConfig().contains(Integer.toString(arena.getCurrentWave() + 1))) {
            arena.incrementCurrentWave();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
        }

        // Start the next wave
        else arena.getTask().wave.run();
    }

    @EventHandler
    public void onWaveStart(WaveStartEvent e) {
        Arena arena = e.getArena();

        // Don't continue if the arena is ending
        if (arena.isEnding() || arena.getCurrentWave() == 0) {
            e.setCancelled(true);
            return;
        }

        Tasks task = arena.getTask();

        // Start wave count down
        if (arena.getWaveTimeLimit() != -1)
            task.getTasks().put(task.updateBar,
                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task.updateBar, 0, Utils.secondsToTicks(1)));

        // Spawn mobs
        spawnVillagers(arena);
        spawnMonsters(arena);
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();

        // Check if the player is playing in a game
        if (game.arenas.stream().filter(Objects::nonNull).noneMatch(a -> a.hasPlayer(player))) {
            e.setCancelled(true);
            player.sendMessage(Utils.notify("&cYou are not in a game!"));
            return;
        }

        Arena arena = game.arenas.stream().filter(Objects::nonNull).filter(a -> a.hasPlayer(player))
                .collect(Collectors.toList()).get(0);
        VDPlayer gamer = arena.getPlayer(player);

        // Not spectating
        if (!gamer.isSpectating()) {
            FileConfiguration playerData = plugin.getPlayerData();

            // Update player stats
            playerData.set(player.getName() + ".totalKills",
                    playerData.getInt(player.getName() + ".totalKills") + gamer.getKills());
            if (playerData.getInt(player.getName() + ".topKills") < gamer.getKills())
                playerData.set(player.getName() + ".topKills", gamer.getKills());
            plugin.savePlayerData();

            // Refresh leaderboards
            leaderboard.refreshLeaderboards();

            // Remove the player from the arena and time limit bar if exists
            arena.getPlayers().remove(gamer);
            if (arena.getTimeLimitBar() != null)
                arena.removePlayerFromTimeLimitBar(gamer.getPlayer());

            // Notify people in arena player left
            arena.getPlayers().forEach(fighter ->
                    fighter.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&c left the arena.")));

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            Utils.teleAdventure(player, game.getLobby());

            Tasks task = arena.getTask();
            Map<Runnable, Integer> tasks = task.getTasks();
            BukkitScheduler scheduler = Bukkit.getScheduler();
            List<Runnable> toRemove = new ArrayList<>();
            int actives = arena.getActiveCount();

            // Check if arena can no longer start
            if (actives < arena.getMinPlayers() && !arena.isActive()) {
                // Remove other tasks that's not the waiting task
                tasks.forEach((runnable, id) -> {
                    if (actives == 0 || !runnable.equals(task.waiting)) toRemove.add(runnable);
                });
                toRemove.forEach(r -> {
                    scheduler.cancelTask(tasks.get(r));
                    tasks.remove(r);
                });

                // Schedule and record the waiting task if appropriate
                if (actives != 0)
                    tasks.put(task.waiting, scheduler.scheduleSyncRepeatingTask(plugin, task.waiting, 0,
                            Utils.secondsToTicks(60)));
            }

            // Checks if the game has ended because no players are left
            if (arena.getAlive() == 0 && arena.isActive())
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
        }

        // Spectating
        else {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Sets them up for teleport to lobby
            Utils.teleAdventure(player, game.getLobby());
        }

        // Return player exp and items
        if (player.isOnline()) {
            if (plugin.getPlayerData().contains(player.getName() + ".level"))
                player.setLevel(plugin.getPlayerData().getInt(player.getName() + ".level"));
            plugin.getPlayerData().set(player.getName() + ".level", null);
            if (plugin.getPlayerData().contains(player.getName() + ".exp"))
                player.setExp((float) plugin.getPlayerData().getDouble(player.getName() + ".exp"));
            plugin.getPlayerData().set(player.getName() + ".exp", null);
            if (plugin.getPlayerData().contains(player.getName() + ".inventory"))
                plugin.getPlayerData().getConfigurationSection(player.getName() + ".inventory").getKeys(false)
                        .forEach(num -> player.getInventory().setItem(Integer.parseInt(num),
                                (ItemStack) plugin.getPlayerData().get(player.getName() + ".inventory." + num)));
            plugin.getPlayerData().set(player.getName() + ".inventory", null);
            plugin.savePlayerData();
        }

        // Refresh the game portal
        portal.refreshHolo(game.arenas.indexOf(arena), game);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Arena arena = e.getArena();

        // Set the arena to ending
        arena.flipEnding();
        portal.refreshHolo(game.arenas.indexOf(arena), game);

        // Notify players that the game has ended
        arena.getPlayers().forEach(player ->
            player.getPlayer().sendMessage(Utils.notify("&6You defeated up to wave &b" +
                    (arena.getCurrentWave() - 1) + "&6! Ending in 10 seconds.")));

        if (arena.getActiveCount() > 0) {
            // Check for record
            if (arena.checkNewRecord(new ArenaRecord(arena.getCurrentWave() - 1, arena.getActives().stream()
                    .map(vdPlayer -> vdPlayer.getPlayer().getName()).collect(Collectors.toList())))) {
                arena.getPlayers().forEach(player -> player.getPlayer().sendTitle(
                        Utils.format("&6New arena record!"), null, Utils.secondsToTicks(.5),
                        Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));
                arenaBoard.refreshArenaBoard(arena.getArena());
            }

            // Give persistent rewards
            arena.getActives().forEach(vdPlayer -> {
                // Calculate reward from difficulty multiplier, wave, kills, and gem balance
                int reward = (10 + 5 * arena.getDifficultyMultiplier()) * (arena.getCurrentWave() - 1);
                reward += vdPlayer.getKills();
                reward += (vdPlayer.getGems() + 5) / 10;

                // Give rewards and notify
                plugin.getPlayerData().set(vdPlayer.getPlayer().getName() + ".crystalBalance",
                        plugin.getPlayerData().getInt(vdPlayer.getPlayer().getName() + ".crystalBalance") + reward);
                vdPlayer.getPlayer().sendMessage(Utils.notify("&6You earned &b" + reward + " crystals &6this game!"));
            });
        }

        Tasks task = arena.getTask();
        Map<Runnable, Integer> tasks = task.getTasks();

        // Reset the arena
        if (tasks.containsKey(task.updateBar)) {
            Bukkit.getScheduler().cancelTask(tasks.get(task.updateBar));
            tasks.remove(task.updateBar);
            arena.removeTimeLimitBar();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ArenaResetEvent(arena)), Utils.secondsToTicks(10));
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent e) {
        e.getArena().getTask().reset.run();
    }

    @EventHandler
    public void onBoardReload(ReloadBoardsEvent e) {
        e.getArena().getTask().updateBoards.run();
    }

    // Spawns villagers randomly
    private void spawnVillagers(Arena arena) {
        DataManager data;

        // Put arena in spawning state
        arena.setSpawning(true);

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
        else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;

        // Get count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.isDynamicCount())
            countMultiplier = 1;

        int toSpawn = (int) (data.getConfig().getInt(arena.getCurrentWave() + ".count.v") * countMultiplier)
                - arena.getVillagers();
        List<Location> spawns = arena.getVillagerSpawns();

        for (int i = 0; i < toSpawn; i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setVillager(plugin, arena,
                    (Villager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.VILLAGER)
            ), delay);
            delay += r.nextInt(spawnDelay(i));
        }
    }

    // Spawns monsters randomly
    private void spawnMonsters(Arena arena) {
        DataManager data;

        // Put the arena in spawning state
        arena.setSpawning(true);

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
        else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        int wave = arena.getCurrentWave();

        // Calculate count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.isDynamicCount())
            countMultiplier = 1;

        String path = wave + ".mtypes";
        List<Location> spawns = arena.getMonsterSpawns();
        List<String> typeRatio = new ArrayList<>();

        // Get monster type ratio
        data.getConfig().getConfigurationSection(path).getKeys(false)
                .forEach(type -> {
            for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                typeRatio.add(type);
        });

        // Spawn monsters
        for (int i = 0; i < (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier); i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));

            // Update delay
            delay += r.nextInt(spawnDelay(i));

            switch (typeRatio.get(r.nextInt(typeRatio.size()))) {
                case "zomb":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setZombie(plugin, arena,
                            (Zombie) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.ZOMBIE)
                    ), delay);
                    break;
                case "husk":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setHusk(plugin, arena,
                            (Husk) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.HUSK)
                    ), delay);
                    break;
                case "wskl":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWitherSkeleton(plugin, arena,
                            (WitherSkeleton) Objects.requireNonNull(spawn.getWorld())
                                    .spawnEntity(spawn, EntityType.WITHER_SKELETON)
                    ), delay);
                    break;
                case "brut":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setBrute(plugin, arena,
                            (PiglinBrute) Objects.requireNonNull(spawn.getWorld())
                                    .spawnEntity(spawn, EntityType.PIGLIN_BRUTE)
                    ), delay);
                    break;
                case "vind":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setVindicator(plugin, arena,
                            (Vindicator) Objects.requireNonNull(spawn.getWorld())
                                    .spawnEntity(spawn, EntityType.VINDICATOR)
                    ), delay);
                    break;
                case "spid":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSpider(plugin, arena,
                            (Spider) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.SPIDER)
                    ), delay);
                    break;
                case "cspd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setCaveSpider(plugin, arena,
                            (CaveSpider) Objects.requireNonNull(spawn.getWorld())
                                    .spawnEntity(spawn, EntityType.CAVE_SPIDER)
                    ), delay);
                    break;
                case "wtch":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWitch(plugin, arena,
                            (Witch) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.WITCH)
                    ), delay);
                    break;
                case "skel":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSkeleton(plugin, arena,
                            (Skeleton) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.SKELETON)
                    ), delay);
                    break;
                case "stry":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setStray(plugin, arena,
                            (Stray) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.STRAY)
                    ), delay);
                    break;
                case "drwd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setDrowned(plugin, arena,
                            (Drowned) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.DROWNED)
                    ), delay);
                    break;
                case "blze":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setBlaze(plugin, arena,
                            (Blaze) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.BLAZE)
                    ), delay);
                    break;
                case "ghst":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setGhast(plugin, arena,
                            (Ghast) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.GHAST)
                    ), delay);
                    break;
                case "pill":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setPillager(plugin, arena,
                            (Pillager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.PILLAGER)
                    ), delay);
                    break;
                case "slim":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSlime(plugin, arena,
                            (Slime) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.SLIME)
                    ), delay);
                    break;
                case "mslm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setMagmaCube(plugin, arena,
                            (MagmaCube) Objects.requireNonNull(spawn.getWorld())
                                    .spawnEntity(spawn, EntityType.MAGMA_CUBE)
                    ), delay);
                    break;
                case "crpr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setCreeper(plugin, arena,
                            (Creeper) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.CREEPER)
                    ), delay);
                    break;
                case "phtm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setPhantom(plugin, arena,
                            (Phantom) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.PHANTOM)
                    ), delay);
                    break;
                case "evok":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setEvoker(plugin, arena,
                            (Evoker) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.EVOKER)
                    ), delay);
                    break;
                case "hgln":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setHoglin(plugin, arena,
                            (Hoglin) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.HOGLIN)
                    ), delay);
                    break;
                case "rvgr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setRavager(plugin, arena,
                            (Ravager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.RAVAGER)
                    ), delay);
                    break;
                case "wthr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWither(plugin, arena,
                            (Wither) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.WITHER)
                    ), delay);
            }

            // Bring arena out of spawning state
            if (i + 1 >= (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier))
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawning(false), delay);
        }
    }

    // Function for spawn delay
    private int spawnDelay(int index) {
        int result = (int) (60 * Math.pow(Math.E, - index / 10D));
        return result == 0 ? 1 : result;
    }
}
