package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.*;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
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

        // First player joining the arena
        if (players == 0) {
            // Get all nearby entities in the arena and clear them out
            Utils.clear(location);
        }

        // Prepares player to enter arena if it doesn't exceed max capacity or if the arena hasn't already started
        if (players < arena.getMaxPlayers() && !arena.isActive()) {
            // Teleport to arena or waiting room
            Utils.prepTeleAdventure(player);
            player.setInvulnerable(true);
            player.teleport(location);

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            portal.refreshHolo(game.arenas.indexOf(arena), game);

            // Give them a game board
            game.createBoard(fighter);

            // Makes sure players have full saturation when the game starts
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Utils.secondsToTicks(9999), 0));

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    gamer.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&a joined the arena.")));
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena or waiting room
            Utils.prepTeleSpectator(player);
            player.teleport(location);

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
                    Utils.secondsToTicks(60)));
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
            tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(plugin, task.min1, Utils.secondsToTicks(60)));
            tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(plugin, task.sec30, Utils.secondsToTicks(90)));
            tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10, Utils.secondsToTicks(110)));
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5, Utils.secondsToTicks(115)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start, Utils.secondsToTicks(120)));
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
    public void onWaveStart(WaveStartEvent e) {
        Arena arena = e.getArena();

        // Don't continue if the arena is ending
        if (arena.isEnding()) {
            e.setCancelled(true);
            return;
        }

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
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Notify people in arena player left
            arena.getPlayers().forEach(fighter ->
                    fighter.getPlayer().sendMessage(Utils.notify("&b" + player.getName() + "&c left the arena.")));

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            Utils.prepTeleAdventure(player);
            player.teleport(game.getLobby());

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

            // Refresh the game portal
            portal.refreshHolo(game.arenas.indexOf(arena), game);
        }

        // Spectating
        else {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Sets them up for teleport to lobby
            Utils.prepTeleAdventure(player);
            player.teleport(game.getLobby());

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
            player.getPlayer().sendMessage(Utils.notify("&6You made it to wave &b" +
                    arena.getCurrentWave() + "&6! Ending in 10 seconds.")));

        // Reset the arena
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
        Random r = new Random();
        int delay = 0;
        int toSpawn = plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".vlgr")
                - arena.getVillagers();
        List<Location> spawns = arena.getVillagerSpawns();

        for (int i = 0; i < toSpawn; i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Mobs.setVillager(plugin, arena,
                        (Villager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.VILLAGER));
            }, delay);
        }
    }

    // Spawns monsters randomly
    private void spawnMonsters(Arena arena) {
        Random r = new Random();
        int delay = 0;
        List<Location> spawns = arena.getMonsterSpawns();

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".zomb"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Zombie n = (Zombie) spawn.getWorld().spawnEntity(spawn, EntityType.ZOMBIE);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".husk"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Husk n = (Husk) spawn.getWorld().spawnEntity(spawn, EntityType.HUSK);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wskl"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                WitherSkeleton n = (WitherSkeleton) spawn.getWorld().spawnEntity(spawn, EntityType.WITHER_SKELETON);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".brut"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                PiglinBrute n = (PiglinBrute) spawn.getWorld().spawnEntity(spawn, EntityType.PIGLIN_BRUTE);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".vind"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Vindicator n = (Vindicator) spawn.getWorld().spawnEntity(spawn, EntityType.VINDICATOR);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".spid"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Spider n = (Spider) spawn.getWorld().spawnEntity(spawn, EntityType.SPIDER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".cspd"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                CaveSpider n = (CaveSpider) spawn.getWorld().spawnEntity(spawn, EntityType.CAVE_SPIDER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wtch"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Witch n = (Witch) spawn.getWorld().spawnEntity(spawn, EntityType.WITCH);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".skel"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Skeleton n = (Skeleton) spawn.getWorld().spawnEntity(spawn, EntityType.SKELETON);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".stry"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Stray n = (Stray) spawn.getWorld().spawnEntity(spawn, EntityType.STRAY);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".blze"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Blaze n = (Blaze) spawn.getWorld().spawnEntity(spawn, EntityType.BLAZE);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".ghst"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Ghast n = (Ghast) spawn.getWorld().spawnEntity(spawn, EntityType.GHAST);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".pill"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Pillager n = (Pillager) spawn.getWorld().spawnEntity(spawn, EntityType.PILLAGER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setCanPickupItems(false);
                n.getEquipment().setItemInMainHand(null);
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".slim"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Slime n = (Slime) spawn.getWorld().spawnEntity(spawn, EntityType.SLIME);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".mslm"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                MagmaCube n = (MagmaCube) spawn.getWorld().spawnEntity(spawn, EntityType.MAGMA_CUBE);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".crpr"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Creeper n = (Creeper) spawn.getWorld().spawnEntity(spawn, EntityType.CREEPER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".phtm"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Phantom n = (Phantom) spawn.getWorld().spawnEntity(spawn, EntityType.PHANTOM);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".evok"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Evoker n = (Evoker) spawn.getWorld().spawnEntity(spawn, EntityType.EVOKER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".hgln"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Hoglin n = (Hoglin) spawn.getWorld().spawnEntity(spawn, EntityType.HOGLIN);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".rvgr"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Ravager n = (Ravager) spawn.getWorld().spawnEntity(spawn, EntityType.RAVAGER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
        delay = 0;

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wthr"); i++) {
            delay += r.nextInt(spawnDelay(i));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location spawn = spawns.get(r.nextInt(spawns.size()));
                Wither n = (Wither) spawn.getWorld().spawnEntity(spawn, EntityType.WITHER);
                n.setCustomName(Utils.healthBar(1, 1, 5));
                n.setCustomNameVisible(true);
                n.setMetadata("VD", new FixedMetadataValue(plugin, arena.getArena()));
                n.setRemoveWhenFarAway(false);
                arena.incrementEnemies();
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena));
            }, delay);
        }
    }

    // Function for spawn delay
    private int spawnDelay(int index) {
        int result = (int) (40 * Math.pow(Math.E, - index / 10D));
        return result == 0 ? 1 : result;
    }
}
