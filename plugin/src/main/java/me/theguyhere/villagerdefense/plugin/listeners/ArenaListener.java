package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.*;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameItems;
import me.theguyhere.villagerdefense.plugin.game.models.Mobs;
import me.theguyhere.villagerdefense.plugin.game.models.Tasks;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.*;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaListener implements Listener {
    private final Main plugin;

    public ArenaListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(JoinArenaEvent e) {
        Player player = e.getPlayer();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        FileConfiguration language = plugin.getLanguageData();

        // Ignore if player is already in a game somehow
        if (Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).anyMatch(a -> a.hasPlayer(player))) {
            e.setCancelled(true);
            PlayerManager.notify(player, language.getString("joinError"));
            return;
        }

        Arena arena = e.getArena();
        Location spawn;
        Location waiting;

        // Check if arena is closed
        if (arena.isClosed()) {
            PlayerManager.notify(player, language.getString("closeError"));
            e.setCancelled(true);
            return;
        }

        // Try to get waiting room
        try {
            waiting = arena.getWaitingRoom();
        } catch (Exception err) {
            waiting = null;
        }

        // Try to get player spawn
        try {
            spawn = arena.getPlayerSpawn().getLocation();
        } catch (Exception err) {
            err.printStackTrace();
            PlayerManager.notify(player, language.getString("fatalError"));
            return;
        }

        // Set waiting room to spawn if absent
        if (waiting == null)
            waiting = spawn;

        int players = arena.getActiveCount();

        if (plugin.getConfig().getBoolean("keepInv")) {
            // Save player exp and items before going into arena
            plugin.getPlayerData().set(player.getName() + ".health", player.getHealth());
            plugin.getPlayerData().set(player.getName() + ".food", player.getFoodLevel());
            plugin.getPlayerData().set(player.getName() + ".saturation", (double) player.getSaturation());
            plugin.getPlayerData().set(player.getName() + ".level", player.getLevel());
            plugin.getPlayerData().set(player.getName() + ".exp", (double) player.getExp());
            for (int i = 0; i < player.getInventory().getContents().length; i++)
                plugin.getPlayerData().set(player.getName() + ".inventory." + i, player.getInventory().getContents()[i]);
            plugin.savePlayerData();
        }

        // Prepares player to enter arena if it doesn't exceed max capacity and if the arena is still waiting
        if (players < arena.getMaxPlayers() && arena.getStatus() == ArenaStatus.WAITING) {
            // Teleport to arena or waiting room
            PlayerManager.teleAdventure(player, waiting);
            player.setInvulnerable(true);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    PlayerManager.notify(gamer.getPlayer(),
                            String.format(Objects.requireNonNull(language.getString("join")),
                            player.getName())));

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();

            // Give them a game board
            ArenaManager.createBoard(fighter);

            // Clear arena
            WorldManager.clear(arena.getCorner1(), arena.getCorner2());

            // Play waiting music
            if (arena.getWaitingSound() != null)
                try {
                    if (arena.getWaitingRoom() != null)
                        player.playSound(arena.getWaitingRoom(), arena.getWaitingSound(), 4, 1);
                    else player.playSound(arena.getPlayerSpawn().getLocation(), arena.getWaitingSound(), 4, 1);
                } catch (Exception err) {
                    CommunicationManager.debugError(err.getMessage(), 0);
                }

            // Give player choice options
            player.getInventory().setItem(2, GameItems.kitSelector());
            player.getInventory().setItem(4, GameItems.challengeSelector());
            player.getInventory().setItem(6, GameItems.leave());

            // Debug message to console
            CommunicationManager.debugInfo(player.getName() + "joined Arena " + arena.getArena(), 2);
        }

        // Enter arena if late arrival is allowed
        else if (players < arena.getMaxPlayers() && arena.getStatus() == ArenaStatus.ACTIVE && arena.hasLateArrival()) {
            // Teleport to arena
            PlayerManager.teleAdventure(player, spawn);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    PlayerManager.notify(gamer.getPlayer(), String.format(
                            Objects.requireNonNull(language.getString("join")),
                            player.getName())));

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();

            // Give them a game board
            ArenaManager.createBoard(fighter);

            // Give them starting items
            arena.getTask().giveItems(fighter);

            // Debug message to console
            CommunicationManager.debugInfo(player.getName() + "joined Arena " + arena.getArena(), 2);

            // Don't touch task updating
            return;
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena and give time limit bar
            PlayerManager.teleSpectator(player, spawn);
            arena.addPlayerToTimeLimitBar(player);

            // Update player tracking and in-game stats
            arena.getPlayers().add(new VDPlayer(player, true));
            arena.refreshPortal();

            // Debug message to console
            CommunicationManager.debugInfo(player.getName() + "is spectating Arena " + arena.getArena(), 2);

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
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 30)));
            tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(plugin, task.sec10,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 10)));
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(plugin, task.sec5,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 5)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(plugin, task.start,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2))));
        }

        // Quick start condition
        else if (players == arena.getMaxPlayers() && !tasks.containsKey(task.full10) &&
                !(tasks.containsKey(task.sec10) && !scheduler.isQueued(tasks.get(task.sec10)))) {
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

        // Don't continue if the arena is not active
        if (arena.getStatus() != ArenaStatus.ACTIVE) {
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

        // Remove calibration task
        if (tasks.containsKey(task.calibrate)) {
            Bukkit.getScheduler().cancelTask(tasks.get(task.calibrate));
            tasks.remove(task.calibrate);
        }

        // Play wave end sound
        if (arena.hasWaveFinishSound() && arena.getCurrentWave() != 0)
                for (VDPlayer vdPlayer : arena.getPlayers()) {
                    vdPlayer.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
                            Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, .75f);
                }

        FileConfiguration playerData = plugin.getPlayerData();

        // Update player stats
        for (VDPlayer active : arena.getActives())
            if (playerData.getInt(active.getPlayer().getName() + ".topWave") < arena.getCurrentWave())
                playerData.set(active.getPlayer().getName() + ".topWave", arena.getCurrentWave());
        plugin.savePlayerData();

        // Debug message to console
        CommunicationManager.debugInfo("Arena " + arena.getArena() + " completed wave " + arena.getCurrentWave(), 2);

        // Win condition
        if (arena.getCurrentWave() == arena.getMaxWaves()) {
            arena.incrementCurrentWave();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                    Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
            if (arena.hasWinSound()) {
                for (VDPlayer vdPlayer : arena.getPlayers()) {
                    vdPlayer.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
                }
            }
        }

        // Start the next wave
        else arena.getTask().wave.run();
    }

    @EventHandler
    public void onWaveStart(WaveStartEvent e) {
        Arena arena = e.getArena();

        // Don't continue if the arena is not active
        if (arena.getStatus() != ArenaStatus.ACTIVE || arena.getCurrentWave() == 0) {
            e.setCancelled(true);
            return;
        }

        // Play wave start sound
        if (arena.hasWaveStartSound()) {
            for (VDPlayer vdPlayer : arena.getPlayers()) {
                vdPlayer.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
                        Sound.ENTITY_ENDER_DRAGON_GROWL, 10, .25f);
            }
        }

        Tasks task = arena.getTask();

        // Start wave count down
        if (arena.getWaveTimeLimit() != -1)
            task.getTasks().put(task.updateBar,
                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task.updateBar, 0, Utils.secondsToTicks(1)));

        // Schedule and record calibration task
        task.getTasks().put(task.calibrate, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task.calibrate, 0,
                Utils.secondsToTicks(10)));

        // Spawn mobs
        spawnVillagers(arena);
        spawnMonsters(arena);
        spawnBosses(arena);

        // Debug message to console
        CommunicationManager.debugInfo("Arena " + arena.getArena() + " started wave " + arena.getCurrentWave(), 2);
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = Arrays.stream(ArenaManager.arenas).filter(Objects::nonNull).filter(a ->
                    a.hasPlayer(player)).toList().get(0);
            gamer = arena.getPlayer(player);
        } catch (Exception err) {
            e.setCancelled(true);
            PlayerManager.notify(player, plugin.getLanguageData().getString("leaveError"));
            return;
        }

        // Stop playing possible ending sound
        player.stopSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
        if (arena.getWaitingSound() != null)
            player.stopSound(arena.getWaitingSound());

        // Mark VDPlayer as left
        gamer.setStatus(PlayerStatus.LEFT);

        // Not spectating
        if (gamer.getStatus() != PlayerStatus.SPECTATOR) {
            FileConfiguration playerData = plugin.getPlayerData();

            // Update player stats
            playerData.set(player.getName() + ".totalKills",
                    playerData.getInt(player.getName() + ".totalKills") + gamer.getKills());
            if (playerData.getInt(player.getName() + ".topKills") < gamer.getKills())
                playerData.set(player.getName() + ".topKills", gamer.getKills());
            plugin.savePlayerData();

            // Refresh leaderboards
            plugin.getArenaManager().refreshLeaderboards();

            // Remove the player from the arena and time limit bar if exists
            arena.getPlayers().remove(gamer);
            if (arena.getTimeLimitBar() != null)
                arena.removePlayerFromTimeLimitBar(gamer.getPlayer());

            // Remove pets
            WorldManager.getPets(player).forEach(Entity::remove);

            // Notify people in arena player left
            arena.getPlayers().forEach(fighter ->
                    PlayerManager.notify(fighter.getPlayer(), String.format(
                            Objects.requireNonNull(plugin.getLanguageData().getString("leave")),
                            player.getName())));

            int actives = arena.getActiveCount();

            // Notify spectators of open spot if late arrival is on and there is a spot open
            if (arena.hasLateArrival() && actives < arena.getMaxPlayers())
                arena.getSpectators().forEach(spectator ->
                    PlayerManager.notify(spectator.getPlayer(), String.format(
                            Objects.requireNonNull(plugin.getLanguageData().getString("late")),
                            player.getName())));

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            PlayerManager.teleAdventure(player, ArenaManager.getLobby());

            // Give persistent rewards if it applies
            if (arena.getCurrentWave() != 0) {
                // Calculate reward from difficulty multiplier, wave, kills, and gem balance
                int reward = (10 + 5 * arena.getDifficultyMultiplier()) *
                        (Math.max(arena.getCurrentWave() - gamer.getJoinedWave() - 1, 0));
                reward += gamer.getKills();
                reward += (gamer.getGems() + 5) / 10;

                // Calculate challenge bonuses
                int bonus = 0;
                for (Challenge challenge : gamer.getChallenges())
                    bonus += challenge.getBonus();
                bonus = (int) (reward * bonus / 100d);

                // Give rewards and notify
                plugin.getPlayerData().set(player.getName() + ".crystalBalance",
                        plugin.getPlayerData().getInt(player.getName() + ".crystalBalance") + reward);
                plugin.getPlayerData().set(player.getName() + ".crystalBalance",
                        plugin.getPlayerData().getInt(player.getName() + ".crystalBalance") + bonus);
                PlayerManager.notify(player, String.format(
                        Objects.requireNonNull(plugin.getLanguageData().getString("crystals")),
                        reward, bonus));
            }

            Tasks task = arena.getTask();
            Map<Runnable, Integer> tasks = task.getTasks();
            BukkitScheduler scheduler = Bukkit.getScheduler();
            List<Runnable> toRemove = new ArrayList<>();

            // Check if arena can no longer start
            if (actives < arena.getMinPlayers() && arena.getStatus() == ArenaStatus.WAITING) {
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
            if (arena.getAlive() == 0 && arena.getStatus() == ArenaStatus.ACTIVE)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
        }

        // Spectating
        else {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Sets them up for teleport to lobby
            PlayerManager.teleAdventure(player, ArenaManager.getLobby());
        }

        // Return player health, food, exp, and items
        if (plugin.getConfig().getBoolean("keepInv") && player.isOnline()) {
            if (plugin.getPlayerData().contains(player.getName() + ".health"))
                player.setHealth(plugin.getPlayerData().getDouble(player.getName() + ".health"));
            plugin.getPlayerData().set(player.getName() + ".health", null);
            if (plugin.getPlayerData().contains(player.getName() + ".food"))
                player.setFoodLevel(plugin.getPlayerData().getInt(player.getName() + ".food"));
            plugin.getPlayerData().set(player.getName() + ".food", null);
            if (plugin.getPlayerData().contains(player.getName() + ".saturation"))
                player.setSaturation((float) plugin.getPlayerData().getDouble(player.getName() + ".saturation"));
            plugin.getPlayerData().set(player.getName() + ".saturation", null);
            if (plugin.getPlayerData().contains(player.getName() + ".level"))
                player.setLevel(plugin.getPlayerData().getInt(player.getName() + ".level"));
            plugin.getPlayerData().set(player.getName() + ".level", null);
            if (plugin.getPlayerData().contains(player.getName() + ".exp"))
                player.setExp((float) plugin.getPlayerData().getDouble(player.getName() + ".exp"));
            plugin.getPlayerData().set(player.getName() + ".exp", null);
            if (plugin.getPlayerData().contains(player.getName() + ".inventory"))
                Objects.requireNonNull(plugin.getPlayerData()
                                .getConfigurationSection(player.getName() + ".inventory"))
                        .getKeys(false)
                        .forEach(num -> player.getInventory().setItem(Integer.parseInt(num),
                                (ItemStack) plugin.getPlayerData().get(player.getName() + ".inventory." + num)));
            plugin.getPlayerData().set(player.getName() + ".inventory", null);
            plugin.savePlayerData();
        }

        // Refresh the game portal
        arena.refreshPortal();

        // Refresh all displays for the player
        ArenaManager.displayEverything(player);

        // Debug message to console
        CommunicationManager.debugInfo(player.getName() + " left Arena " + arena.getArena(), 2);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Arena arena = e.getArena();
        FileConfiguration language = plugin.getLanguageData();

        // Set the arena to ending
        arena.setStatus(ArenaStatus.ENDING);

        // Notify players that the game has ended (Title)
        try {
            arena.getPlayers().forEach(player ->
                    player.getPlayer().sendTitle(CommunicationManager.format(
                            Objects.requireNonNull(plugin.getLanguageData().getString("gameOver"))),
                            "", Utils.secondsToTicks(.5), Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));
        } catch (Exception err) {
            CommunicationManager.debugError("The key 'gameOver' is either missing or corrupt in the active language file",
                    1);
        }

        // Notify players that the game has ended (Chat)
        try {
            arena.getPlayers().forEach(player ->
                    PlayerManager.notify(player.getPlayer(), String.format(
                            Objects.requireNonNull(language.getString("end")),
                            arena.getCurrentWave() - 1)));
        } catch (Exception err) {
            CommunicationManager.debugError("The key 'end' is either missing or corrupt in the active language file",
                    1);
        }

        // Set all players to invincible
        arena.getAlives().forEach(player -> player.getPlayer().setInvulnerable(true));

        // Play sound if turned on
        if (arena.hasLoseSound()) {
            for (VDPlayer vdPlayer : arena.getPlayers()) {
                vdPlayer.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
                        Sound.ENTITY_ENDER_DRAGON_DEATH, 10, .5f);
            }
        }

        if (arena.getActiveCount() > 0) {
            // Check for record
            if (arena.checkNewRecord(new ArenaRecord(arena.getCurrentWave() - 1, arena.getActives().stream()
                    .map(vdPlayer -> vdPlayer.getPlayer().getName()).collect(Collectors.toList())))) {
                arena.getPlayers().forEach(player -> player.getPlayer().sendTitle(
                        CommunicationManager.format(language.getString("record")), null, Utils.secondsToTicks(.5),
                        Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));
                arena.refreshArenaBoard();
            }

            // Give persistent rewards
            arena.getActives().forEach(vdPlayer -> {
                // Calculate reward from difficulty multiplier, wave, kills, and gem balance
                int reward = (10 + 5 * arena.getDifficultyMultiplier()) *
                        (Math.max(arena.getCurrentWave() - vdPlayer.getJoinedWave() - 1, 0));
                reward += vdPlayer.getKills();
                reward += (vdPlayer.getGems() + 5) / 10;

                // Calculate challenge bonuses
                int bonus = 0;
                for (Challenge challenge : vdPlayer.getChallenges())
                    bonus += challenge.getBonus();
                bonus = (int) (reward * bonus / 100d);

                // Give rewards and notify
                plugin.getPlayerData().set(vdPlayer.getPlayer().getName() + ".crystalBalance",
                        plugin.getPlayerData().getInt(vdPlayer.getPlayer().getName() + ".crystalBalance") + reward);
                plugin.getPlayerData().set(vdPlayer.getPlayer().getName() + ".crystalBalance",
                        plugin.getPlayerData().getInt(vdPlayer.getPlayer().getName() + ".crystalBalance") + bonus);
                PlayerManager.notify(vdPlayer.getPlayer(), String.format(
                        Objects.requireNonNull(plugin.getLanguageData().getString("crystals")),
                        reward, bonus));
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
        if (tasks.containsKey(task.calibrate)) {
            Bukkit.getScheduler().cancelTask(tasks.get(task.calibrate));
            tasks.remove(task.calibrate);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ArenaResetEvent(arena)), Utils.secondsToTicks(10));


        // Debug message to console
        CommunicationManager.debugInfo("Arena " + arena.getArena() + " is ending.", 2);
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent e) {
        e.getArena().getTask().reset.run();
    }

    @EventHandler
    public void onBoardReload(ReloadBoardsEvent e) {
        e.getArena().getTask().updateBoards.run();
    }

    @EventHandler
    public void onEndNinjaNerfEvent(EndNinjaNerfEvent e) {
        if (e.getGamer().getStatus() != PlayerStatus.LEFT)
            e.getGamer().exposeArmor();
    }

    // Spawns villagers randomly
    private void spawnVillagers(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
        else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Get count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.hasDynamicCount())
            countMultiplier = 1;

        int toSpawn = Math.max((int) (data.getConfig().getInt(wave + ".count.v") * countMultiplier), 1)
                - arena.getVillagers();
        List<Location> spawns = arena.getVillagerSpawns().stream().map(ArenaSpawn::getLocation).toList();

        for (int i = 0; i < toSpawn; i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setVillager(plugin, arena,
                    (Villager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.VILLAGER)
            ), delay);
            delay += r.nextInt(spawnDelay(i));

            // Manage spawning state
            if (i + 1 >= toSpawn)
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningVillagers(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningVillagers(true), delay);
        }
    }

    // Spawns monsters randomly
    private void spawnMonsters(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
        else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Check for greater than 0 count
        if (data.getConfig().getInt(wave + ".count.m") == 0)
            return;

        // Calculate count multiplier
        double countMultiplier = Math.log((arena.getActiveCount() + 7) / 10d) + 1;
        if (!arena.hasDynamicCount())
            countMultiplier = 1;

        String path = wave + ".mtypes";
        List<String> typeRatio = new ArrayList<>();

        // Split spawns by type
        List<Location> grounds = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            try {
                if (arena.getMonsterSpawn(i).getLocation() != null && arena.getMonsterSpawnType(i) != 2)
                    grounds.add(arena.getMonsterSpawn(i).getLocation());
            } catch (NullPointerException ignored) {
            }
        if (grounds.isEmpty())
            grounds = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());
        List<Location> airs = new ArrayList<>();
        for (int i = 0; i < 9; i++)
            try {
                if (arena.getMonsterSpawn(i).getLocation() != null && arena.getMonsterSpawnType(i) != 1)
                    airs.add(arena.getMonsterSpawn(i).getLocation());
            } catch (NullPointerException ignored) {
            }
        if (airs.isEmpty())
            airs = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());

        // Get monster type ratio
        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
            for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                typeRatio.add(type);
        });

        // Spawn monsters
        for (int i = 0; i < Math.max((int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier), 1); i++) {
            // Get spawn locations
            Location ground = grounds.get(r.nextInt(grounds.size()));
            Location air = airs.get(r.nextInt(airs.size()));

            // Update delay
            delay += r.nextInt(spawnDelay(i));

            switch (typeRatio.get(r.nextInt(typeRatio.size()))) {
                case "zomb" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setZombie(plugin, arena,
                        (Zombie) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOMBIE)
                ), delay);
                case "husk" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setHusk(plugin, arena,
                        (Husk) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.HUSK)
                ), delay);
                case "wskl" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWitherSkeleton(plugin, arena,
                        (WitherSkeleton) Objects.requireNonNull(ground.getWorld())
                                .spawnEntity(ground, EntityType.WITHER_SKELETON)
                ), delay);
                case "brut" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setBrute(plugin, arena,
                        (PiglinBrute) Objects.requireNonNull(ground.getWorld())
                                .spawnEntity(ground, EntityType.PIGLIN_BRUTE)
                ), delay);
                case "vind" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setVindicator(plugin, arena,
                        (Vindicator) Objects.requireNonNull(ground.getWorld())
                                .spawnEntity(ground, EntityType.VINDICATOR)
                ), delay);
                case "spid" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSpider(plugin, arena,
                        (Spider) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SPIDER)
                ), delay);
                case "cspd" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setCaveSpider(plugin, arena,
                        (CaveSpider) Objects.requireNonNull(ground.getWorld())
                                .spawnEntity(ground, EntityType.CAVE_SPIDER)
                ), delay);
                case "wtch" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWitch(plugin, arena,
                        (Witch) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.WITCH)
                ), delay);
                case "skel" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSkeleton(plugin, arena,
                        (Skeleton) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SKELETON)
                ), delay);
                case "stry" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setStray(plugin, arena,
                        (Stray) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.STRAY)
                ), delay);
                case "drwd" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setDrowned(plugin, arena,
                        (Drowned) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.DROWNED)
                ), delay);
                case "blze" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setBlaze(plugin, arena,
                        (Blaze) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.BLAZE)
                ), delay);
                case "ghst" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setGhast(plugin, arena,
                        (Ghast) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.GHAST)
                ), delay);
                case "pill" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setPillager(plugin, arena,
                        (Pillager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.PILLAGER)
                ), delay);
                case "slim" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setSlime(plugin, arena,
                        (Slime) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SLIME)
                ), delay);
                case "mslm" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setMagmaCube(plugin, arena,
                        (MagmaCube) Objects.requireNonNull(ground.getWorld())
                                .spawnEntity(ground, EntityType.MAGMA_CUBE)
                ), delay);
                case "crpr" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setCreeper(plugin, arena,
                        (Creeper) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.CREEPER)
                ), delay);
                case "phtm" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setPhantom(plugin, arena,
                        (Phantom) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.PHANTOM)
                ), delay);
                case "evok" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setEvoker(plugin, arena,
                        (Evoker) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.EVOKER)
                ), delay);
                case "hgln" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setZoglin(plugin, arena,
                        (Zoglin) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOGLIN)
                ), delay);
                case "rvgr" -> Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setRavager(plugin, arena,
                        (Ravager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.RAVAGER)
                ), delay);
            }

            // Manage spawning state
            if (i + 1 >= (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier))
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Spawn bosses randomly
    private void spawnBosses(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager(plugin, "spawnTables/a" + arena.getArena() + ".yml");
        else data = new DataManager(plugin, "spawnTables/" + arena.getSpawnTableFile() + ".yml");

        Random r = new Random();
        int delay = 0;
        String wave = Integer.toString(arena.getCurrentWave());
        if (!data.getConfig().contains(wave))
            if (data.getConfig().contains("freePlay"))
                wave = "freePlay";
            else wave = "1";

        // Check for greater than 0 count
        if (data.getConfig().getInt(wave + ".count.b") == 0)
            return;

        String path = wave + ".btypes";
        List<Location> spawns = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).toList();
        List<String> typeRatio = new ArrayList<>();

        // Get monster type ratio
        Objects.requireNonNull(data.getConfig().getConfigurationSection(path)).getKeys(false)
                .forEach(type -> {
                    for (int i = 0; i < data.getConfig().getInt(path + "." + type); i++)
                        typeRatio.add(type);
                });

        // Spawn bosses
        for (int i = 0; i < data.getConfig().getInt(wave + ".count.b"); i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));

            // Update delay
            delay += r.nextInt(spawnDelay(i)) * 10;

            switch (typeRatio.get(r.nextInt(typeRatio.size()))) {
                case "w":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Mobs.setWither(plugin, arena,
                            (Wither) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.WITHER)
                    ), delay);
                    break;
            }

            // Manage spawning state
            if (i + 1 >= data.getConfig().getInt(wave + ".count.b"))
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Function for spawn delay
    private int spawnDelay(int index) {
        int result = (int) (60 * Math.pow(Math.E, - index / 60D));
        return result == 0 ? 1 : result;
    }
}
