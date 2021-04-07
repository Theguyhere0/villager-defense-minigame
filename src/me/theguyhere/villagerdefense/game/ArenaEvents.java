package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.customEvents.*;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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

        spawnVillagers(arena);
        spawnMonsters(arena);
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
            player.getPlayer().sendMessage(Utils.format("&6You made it to arena.getCurrentWave() &b" +
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
        for (int i = 0;
             i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".vlgr") - arena.getVillagers();
             i++) {
            int num = r.nextInt(arena.getVillagerSpawns().size());
            Villager n = (Villager) arena.getVillagerSpawns().get(num).getWorld()
                    .spawnEntity(arena.getVillagerSpawns().get(num), EntityType.VILLAGER);
            n.setCustomName(Utils.format("&aVD" + arena.getArena() +": Villager"));
            arena.incrementVillagers();
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)));
    }

    // Spawns monsters randomly
    private void spawnMonsters(Arena arena) {
        Random r = new Random();

        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".zomb"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Zombie n = (Zombie) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.ZOMBIE);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Zombie"));
                    n.setCanPickupItems(false);
                    n.getEquipment().setItemInMainHand(null);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 1);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".husk"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Husk n = (Husk) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.HUSK);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Husk"));
                    n.setCanPickupItems(false);
                    n.getEquipment().setItemInMainHand(null);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 2);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wskl"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    WitherSkeleton n = (WitherSkeleton) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.WITHER_SKELETON);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Wither Skeleton"));
                    n.setCanPickupItems(false);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 3);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".brut"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    PiglinBrute n = (PiglinBrute) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.PIGLIN_BRUTE);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Piglin Brute"));
                    n.setCanPickupItems(false);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 4);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".vind"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Vindicator n = (Vindicator) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.VINDICATOR);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Vindicator"));
                    n.setCanPickupItems(false);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 5);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".spid"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Spider n = (Spider) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.SPIDER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Spider"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 6);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".cspd"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    CaveSpider n = (CaveSpider) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.CAVE_SPIDER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Cave Spider"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 7);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wtch"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Witch n = (Witch) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.WITCH);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Witch"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 8);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".skel"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Skeleton n = (Skeleton) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.SKELETON);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Skeleton"));
                    n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                    n.getEquipment().setItemInMainHandDropChance(0);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 9);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".stry"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Stray n = (Stray) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.STRAY);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Stray"));
                    n.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                    n.getEquipment().setItemInMainHandDropChance(0);
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 10);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".blze"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Blaze n = (Blaze) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.BLAZE);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Blaze"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 11);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".ghst"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Ghast n = (Ghast) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.GHAST);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Ghast"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 12);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".pill"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Pillager n = (Pillager) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.PILLAGER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Pillager"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 13);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".slim"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Slime n = (Slime) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.SLIME);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Slime"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 14);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".mslm"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    MagmaCube n = (MagmaCube) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.MAGMA_CUBE);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Magma Cube"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 15);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".crpr"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Creeper n = (Creeper) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.CREEPER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Creeper"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 16);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".phtm"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Phantom n = (Phantom) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.PHANTOM);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Phantom"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 17);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".evok"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Evoker n = (Evoker) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.EVOKER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Evoker"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 18);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".hgln"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Hoglin n = (Hoglin) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.HOGLIN);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Hoglin"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 19);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".rvgr"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Ravager n = (Ravager) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.RAVAGER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Ravager"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 20);
        }
        for (int i = 0; i < plugin.getConfig().getInt("waves.wave" + arena.getCurrentWave() + ".wthr"); i++) {
            int num = r.nextInt(arena.getMonsterSpawns().size());
            new BukkitRunnable() {

                @Override
                public void run() {
                    Wither n = (Wither) Bukkit.getWorld(arena.getMonsterSpawns().get(num).getWorld().getName())
                            .spawnEntity(arena.getMonsterSpawns().get(num), EntityType.WITHER);
                    n.setCustomName(Utils.format("&cVD" + arena.getArena() + ": Wither"));
                    arena.incrementEnemies();
                }

            }.runTaskLater(plugin, 21);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ReloadBoardsEvent(arena)), 22);
    }
}
