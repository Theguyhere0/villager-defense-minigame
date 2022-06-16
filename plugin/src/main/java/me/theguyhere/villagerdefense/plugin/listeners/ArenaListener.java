package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.*;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.Mobs;
import me.theguyhere.villagerdefense.plugin.game.models.Tasks;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.*;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.DataManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    @EventHandler
    public void onJoin(JoinArenaEvent e) {
        Player player = e.getPlayer();
        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Ignore if player is already in a game somehow
        if (GameManager.checkPlayer(player)) {
            e.setCancelled(true);
            PlayerManager.notifyFailure(player, LanguageManager.errors.join);
            return;
        }

        Arena arena = e.getArena();
        Location spawn;
        Location waiting;

        // Check if arena is closed
        if (arena.isClosed()) {
            PlayerManager.notifyFailure(player, LanguageManager.errors.close);
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
            PlayerManager.notifyFailure(player, LanguageManager.errors.fatal);
            return;
        }

        // Set waiting room to spawn if absent
        if (waiting == null)
            waiting = spawn;

        int players = arena.getActiveCount();

        if (Main.plugin.getConfig().getBoolean("keepInv")) {
            // Save player exp and items before going into arena
            Main.getPlayerData().set(player.getUniqueId() + ".health", player.getHealth());
            Main.getPlayerData().set(player.getUniqueId() + ".food", player.getFoodLevel());
            Main.getPlayerData().set(player.getUniqueId() + ".saturation", (double) player.getSaturation());
            Main.getPlayerData().set(player.getUniqueId() + ".level", player.getLevel());
            Main.getPlayerData().set(player.getUniqueId() + ".exp", (double) player.getExp());
            for (int i = 0; i < player.getInventory().getContents().length; i++)
                Main.getPlayerData().set(player.getUniqueId() + ".inventory." + i, player.getInventory().getContents()[i]);
            Main.savePlayerData();
        }

        // Prepares player to enter arena if it doesn't exceed max capacity and if the arena is still waiting
        if (players < arena.getMaxPlayers() && arena.getStatus() == ArenaStatus.WAITING) {
            // Teleport to arena or waiting room
            PlayerManager.teleAdventure(player, waiting);
            player.setInvulnerable(true);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    PlayerManager.notifyAlert(gamer.getPlayer(), String.format(LanguageManager.messages.join,
                            player.getName())));

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();

            // Add forced challenges
            arena.getForcedChallenges().forEach(challenge -> fighter.addChallenge(Challenge.getChallenge(challenge)));

            // Give them a game board
            GameManager.createBoard(fighter);

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

            PlayerManager.giveChoiceItems(fighter);

            // Debug message to console
            CommunicationManager.debugInfo(player.getName() + " joined " + arena.getName(), 2);
        }

        // Enter arena if late arrival is allowed
        else if (players < arena.getMaxPlayers() && arena.getStatus() == ArenaStatus.ACTIVE && arena.hasLateArrival()) {
            // Teleport to arena
            PlayerManager.teleAdventure(player, spawn);

            // Notify everyone in the arena
            arena.getPlayers().forEach(gamer ->
                    PlayerManager.notifyAlert(gamer.getPlayer(), String.format(LanguageManager.messages.join,
                            player.getName())));

            // Update player tracking and in-game stats
            VDPlayer fighter = new VDPlayer(player, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();

            // Add forced challenges
            arena.getForcedChallenges().forEach(challenge -> fighter.addChallenge(Challenge.getChallenge(challenge)));

            // Give them a game board
            GameManager.createBoard(fighter);

            // Give them starting items
            arena.getTask().giveItems(fighter);

            // Debug message to console
            CommunicationManager.debugInfo(player.getName() + " joined " + arena.getName(), 2);

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
            CommunicationManager.debugInfo(player.getName() + " is spectating " + arena.getName(),
                    2);

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
            tasks.put(task.waiting, scheduler.scheduleSyncRepeatingTask(Main.plugin, task.waiting, 0,
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
            tasks.put(task.min1, scheduler.scheduleSyncDelayedTask(Main.plugin, task.min1,
                    Utils.secondsToTicks(Utils.minutesToSeconds(1))));
            tasks.put(task.sec30, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec30,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 30)));
            tasks.put(task.sec10, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec10,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 10)));
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5,
                    Utils.secondsToTicks(Utils.minutesToSeconds(2) - 5)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start,
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
            tasks.put(task.sec5, scheduler.scheduleSyncDelayedTask(Main.plugin, task.sec5, Utils.secondsToTicks(5)));
            tasks.put(task.start, scheduler.scheduleSyncDelayedTask(Main.plugin, task.start, Utils.secondsToTicks(10)));
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

        // Play wave end sound if not just starting
        if (arena.hasWaveFinishSound() && arena.getCurrentWave() != 0)
                for (VDPlayer vdPlayer : arena.getPlayers()) {
                    vdPlayer.getPlayer().playSound(arena.getPlayerSpawn().getLocation(),
                            Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, .75f);
                }

        FileConfiguration playerData = Main.getPlayerData();

        // Update player stats
        for (VDPlayer active : arena.getActives())
            if (playerData.getInt(active.getID() + ".topWave") < arena.getCurrentWave())
                playerData.set(active.getID() + ".topWave", arena.getCurrentWave());
        Main.savePlayerData();

        // Debug message to console
        CommunicationManager.debugInfo("" + arena.getName() + " completed wave " + arena.getCurrentWave(),
                2);

        // Win condition
        if (arena.getCurrentWave() == arena.getMaxWaves()) {
            arena.incrementCurrentWave();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
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
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, task.updateBar, 0,
                        Utils.secondsToTicks(1)));

        // Set arena as spawning
        arena.setSpawningMonsters(true);
        arena.setSpawningVillagers(true);

        // Schedule and record calibration task
        task.getTasks().put(task.calibrate, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, task.calibrate,
                0, Utils.secondsToTicks(1)));

        // Spawn mobs
        spawnVillagers(arena);
        spawnMonsters(arena);
        spawnBosses(arena);

        // Debug message to console
        CommunicationManager.debugInfo("" + arena.getName() + " started wave " + arena.getCurrentWave(),
                2);
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = GameManager.getArena(player);
            if (arena == null)
                return;
            gamer = arena.getPlayer(player);
        } catch (Exception err) {
            e.setCancelled(true);
            PlayerManager.notifyFailure(player, LanguageManager.errors.notInGame);
            return;
        }

        // Stop playing possible ending sound
        player.stopSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
        if (arena.getWaitingSound() != null)
            player.stopSound(arena.getWaitingSound());

        // Not spectating
        if (gamer.getStatus() != PlayerStatus.SPECTATOR) {
            FileConfiguration playerData = Main.getPlayerData();

            // Update player stats
            playerData.set(player.getUniqueId() + ".totalKills",
                    playerData.getInt(player.getUniqueId() + ".totalKills") + gamer.getKills());
            if (playerData.getInt(player.getUniqueId() + ".topKills") < gamer.getKills())
                playerData.set(player.getUniqueId() + ".topKills", gamer.getKills());
            Main.savePlayerData();

            // Check for achievements
            AchievementChecker.checkDefaultHighScoreAchievements(player);
            AchievementChecker.checkDefaultInstanceAchievements(gamer);

            // Refresh leaderboards
            GameManager.refreshLeaderboards();

            // Remove the player from the arena and time limit bar if exists
            arena.getPlayers().remove(gamer);
            if (arena.getTimeLimitBar() != null)
                arena.removePlayerFromTimeLimitBar(gamer.getPlayer());

            // Remove pets
            WorldManager.getPets(player).forEach(Entity::remove);

            // Notify people in arena player left
            arena.getPlayers().forEach(fighter ->
                    PlayerManager.notifyAlert(fighter.getPlayer(),
                            String.format(LanguageManager.messages.leaveArena, player.getName())));

            int actives = arena.getActiveCount();

            // Notify spectators of open spot if late arrival is on and there is a spot open
            if (arena.hasLateArrival() && actives < arena.getMaxPlayers())
                arena.getSpectators().forEach(spectator ->
                    PlayerManager.notifyAlert(spectator.getPlayer(),
                            String.format(LanguageManager.messages.late, player.getName())));

            // Sets them up for teleport to lobby
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            PlayerManager.teleAdventure(player, GameManager.getLobby());

            // Give persistent rewards if it applies
            if (arena.getCurrentWave() != 0 && arena.getStatus() == ArenaStatus.ACTIVE) {
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
                Main.getPlayerData().set(player.getUniqueId() + ".crystalBalance",
                        Main.getPlayerData().getInt(player.getUniqueId() + ".crystalBalance") + reward);
                Main.getPlayerData().set(player.getUniqueId() + ".crystalBalance",
                        Main.getPlayerData().getInt(player.getUniqueId() + ".crystalBalance") + bonus);
                PlayerManager.notifySuccess(player, LanguageManager.messages.crystalsEarned,
                        ChatColor.AQUA, String.format("%d (+%d)", reward, bonus), LanguageManager.names.crystals);
            }

            Tasks task = arena.getTask();
            Map<Runnable, Integer> tasks = task.getTasks();
            BukkitScheduler scheduler = Bukkit.getScheduler();
            List<Runnable> toRemove = new ArrayList<>();

            // Mark VDPlayer as left
            gamer.setStatus(PlayerStatus.LEFT);

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
                    tasks.put(task.waiting, scheduler.scheduleSyncRepeatingTask(Main.plugin, task.waiting, 0,
                            Utils.secondsToTicks(60)));
            }

            // Checks if the game has ended because no players are left
            if (arena.getAlive() == 0 && arena.getStatus() == ArenaStatus.ACTIVE)
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () ->
                        Bukkit.getPluginManager().callEvent(new GameEndEvent(arena)));
        }

        // Spectating
        else {
            // Remove the player from the arena
            arena.getPlayers().remove(gamer);

            // Sets them up for teleport to lobby
            PlayerManager.teleAdventure(player, GameManager.getLobby());

            // Mark VDPlayer as left
            gamer.setStatus(PlayerStatus.LEFT);
        }

        // Return player health, food, exp, and items
        if (Main.plugin.getConfig().getBoolean("keepInv") && player.isOnline()) {
            if (Main.getPlayerData().contains(player.getUniqueId() + ".health"))
                player.setHealth(Main.getPlayerData().getDouble(player.getUniqueId() + ".health"));
            Main.getPlayerData().set(player.getUniqueId() + ".health", null);
            if (Main.getPlayerData().contains(player.getUniqueId() + ".food"))
                player.setFoodLevel(Main.getPlayerData().getInt(player.getUniqueId() + ".food"));
            Main.getPlayerData().set(player.getUniqueId() + ".food", null);
            if (Main.getPlayerData().contains(player.getUniqueId() + ".saturation"))
                player.setSaturation((float) Main.getPlayerData().getDouble(player.getUniqueId() + ".saturation"));
            Main.getPlayerData().set(player.getUniqueId() + ".saturation", null);
            if (Main.getPlayerData().contains(player.getUniqueId() + ".level"))
                player.setLevel(Main.getPlayerData().getInt(player.getUniqueId() + ".level"));
            Main.getPlayerData().set(player.getUniqueId() + ".level", null);
            if (Main.getPlayerData().contains(player.getUniqueId() + ".exp"))
                player.setExp((float) Main.getPlayerData().getDouble(player.getUniqueId() + ".exp"));
            Main.getPlayerData().set(player.getUniqueId() + ".exp", null);
            if (Main.getPlayerData().contains(player.getUniqueId() + ".inventory"))
                Objects.requireNonNull(Main.getPlayerData()
                                .getConfigurationSection(player.getUniqueId() + ".inventory"))
                        .getKeys(false)
                        .forEach(num -> player.getInventory().setItem(Integer.parseInt(num),
                                (ItemStack) Main.getPlayerData().get(player.getUniqueId() + ".inventory." + num)));
            Main.getPlayerData().set(player.getUniqueId() + ".inventory", null);
            Main.savePlayerData();
        }

        // Refresh the game portal
        arena.refreshPortal();

        // Refresh all displays for the player
        GameManager.displayEverything(player);

        // Debug message to console
        CommunicationManager.debugInfo(player.getName() + " left " + arena.getName(), 2);
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Arena arena = e.getArena();

        // Set the arena to ending
        arena.setStatus(ArenaStatus.ENDING);

        // Notify players that the game has ended (Title)
        arena.getPlayers().forEach(player ->
                player.getPlayer().sendTitle(CommunicationManager.format("&4&l" +
                        LanguageManager.messages.gameOver), " ", Utils.secondsToTicks(.5),
                        Utils.secondsToTicks(2.5), Utils.secondsToTicks(1)));

        // Notify players that the game has ended (Chat)
        arena.getPlayers().forEach(player ->
                PlayerManager.notifyAlert(player.getPlayer(), LanguageManager.messages.end,
                        ChatColor.AQUA, Integer.toString(arena.getCurrentWave() - 1), "10"));

        // Set all players to invincible
        arena.getAlives().forEach(player -> player.getPlayer().setInvulnerable(true));

        // Play sound if turned on and arena is either not winning or has unlimited waves
        if (arena.hasLoseSound() && (arena.getCurrentWave() <= arena.getMaxWaves() || arena.getMaxWaves() < 0)) {
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
                        CommunicationManager.format("&a" + LanguageManager.messages.record), null,
                        Utils.secondsToTicks(.5), Utils.secondsToTicks(3.5), Utils.secondsToTicks(1)));
                arena.refreshArenaBoard();
            }

            // Give persistent rewards
            arena.getActives().forEach(vdPlayer -> {
                // Calculate reward from difficulty multiplier, wave, kills, and gem balance
                int reward = (5 * arena.getDifficultyMultiplier()) *
                        (Math.max(arena.getCurrentWave() - vdPlayer.getJoinedWave() - 1, 0));
                reward += vdPlayer.getKills();
                reward += (vdPlayer.getGems() + 25) / 50;

                // Calculate challenge bonuses
                int bonus = 0;
                for (Challenge challenge : vdPlayer.getChallenges())
                    bonus += challenge.getBonus();
                bonus = (int) (reward * bonus / 100d);

                // Give rewards and notify
                Main.getPlayerData().set(vdPlayer.getID() + ".crystalBalance",
                        Main.getPlayerData().getInt(vdPlayer.getID() + ".crystalBalance") + reward);
                Main.getPlayerData().set(vdPlayer.getID() + ".crystalBalance",
                        Main.getPlayerData().getInt(vdPlayer.getID() + ".crystalBalance") + bonus);
                PlayerManager.notifySuccess(vdPlayer.getPlayer(),
                        LanguageManager.messages.crystalsEarned,
                        ChatColor.AQUA, String.format("%d (+%d)", reward, bonus), LanguageManager.names.crystals);
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> e.getArena().getTask().kickPlayers.run(),
                Utils.secondsToTicks(10));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> e.getArena().getTask().reset.run(),
                Utils.secondsToTicks(12));

        // Debug message to console
        CommunicationManager.debugInfo("" + arena.getName() + " is ending.", 2);
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
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

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
        List<Location> spawns = arena.getVillagerSpawns().stream().map(ArenaSpawn::getLocation)
                .collect(Collectors.toList());

        for (int i = 0; i < toSpawn; i++) {
            Location spawn = spawns.get(r.nextInt(spawns.size()));
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setVillager(arena,
                    (Villager) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.VILLAGER)
            ), delay);
            delay += r.nextInt(spawnDelay(i));

            // Manage spawning state
            if (i + 1 >= toSpawn)
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningVillagers(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningVillagers(true), delay);
        }
    }

    // Spawns monsters randomly
    private void spawnMonsters(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

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
        for (ArenaSpawn arenaSpawn : arena.getMonsterSpawns()) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_AIR)
                grounds.add(arenaSpawn.getLocation());
        }

        List<Location> airs = new ArrayList<>();
        for (ArenaSpawn arenaSpawn : arena.getMonsterSpawns()) {
            if (arenaSpawn.getSpawnType() != ArenaSpawnType.MONSTER_GROUND)
                airs.add(arenaSpawn.getLocation());
        }

        // Default to all spawns if dedicated spawns are empty
        if (grounds.isEmpty())
            grounds = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation).collect(Collectors.toList());
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
                case "zomb":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setZombie(arena,
                            (Zombie) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOMBIE)
                    ), delay);
                    break;
                case "husk":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setHusk(arena,
                            (Husk) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.HUSK)
                    ), delay);
                    break;
                case "wskl":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWitherSkeleton(arena,
                            (WitherSkeleton) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.WITHER_SKELETON)
                    ), delay);
                    break;
                case "brut":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setBrute(arena,
                            (PiglinBrute) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.PIGLIN_BRUTE)
                    ), delay);
                    break;
                case "vind":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setVindicator(arena,
                            (Vindicator) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.VINDICATOR)
                    ), delay);
                    break;
                case "spid":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSpider(arena,
                            (Spider) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SPIDER)
                    ), delay);
                    break;
                case "cspd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setCaveSpider(arena,
                            (CaveSpider) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.CAVE_SPIDER)
                    ), delay);
                    break;
                case "wtch":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWitch(arena,
                            (Witch) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.WITCH)
                    ), delay);
                    break;
                case "skel":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSkeleton(arena,
                            (Skeleton) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SKELETON)
                    ), delay);
                    break;
                case "stry":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setStray(arena,
                            (Stray) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.STRAY)
                    ), delay);
                    break;
                case "drwd":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setDrowned(arena,
                            (Drowned) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.DROWNED)
                    ), delay);
                    break;
                case "blze":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setBlaze(arena,
                            (Blaze) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.BLAZE)
                    ), delay);
                    break;
                case "ghst":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setGhast(arena,
                            (Ghast) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.GHAST)
                    ), delay);
                    break;
                case "pill":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setPillager(arena,
                            (Pillager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.PILLAGER)
                    ), delay);
                    break;
                case "slim":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setSlime(arena,
                            (Slime) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.SLIME)
                    ), delay);
                    break;
                case "mslm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setMagmaCube(arena,
                            (MagmaCube) Objects.requireNonNull(ground.getWorld())
                                    .spawnEntity(ground, EntityType.MAGMA_CUBE)
                    ), delay);
                    break;
                case "crpr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setCreeper(arena,
                            (Creeper) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.CREEPER)
                    ), delay);
                    break;
                case "phtm":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setPhantom(arena,
                            (Phantom) Objects.requireNonNull(air.getWorld()).spawnEntity(air, EntityType.PHANTOM)
                    ), delay);
                    break;
                case "evok":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setEvoker(arena,
                            (Evoker) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.EVOKER)
                    ), delay);
                    break;
                case "hgln":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setZoglin(arena,
                            (Zoglin) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.ZOGLIN)
                    ), delay);
                    break;
                case "rvgr":
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setRavager(arena,
                            (Ravager) Objects.requireNonNull(ground.getWorld()).spawnEntity(ground, EntityType.RAVAGER)
                    ), delay);
            }

            // Manage spawning state
            if (i + 1 >= (int) (data.getConfig().getInt(wave + ".count.m") * countMultiplier))
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Spawn bosses randomly
    private void spawnBosses(Arena arena) {
        DataManager data;

        // Get spawn table
        if (arena.getSpawnTableFile().equals("custom"))
            data = new DataManager("spawnTables/" + arena.getPath() + ".yml");
        else data = new DataManager("spawnTables/" + arena.getSpawnTableFile() + ".yml");

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
        List<Location> spawns = arena.getMonsterSpawns().stream().map(ArenaSpawn::getLocation)
                .collect(Collectors.toList());
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
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> Mobs.setWither(arena,
                            (Wither) Objects.requireNonNull(spawn.getWorld()).spawnEntity(spawn, EntityType.WITHER)
                    ), delay);
                    break;
            }

            // Manage spawning state
            if (i + 1 >= data.getConfig().getInt(wave + ".count.b"))
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(false), delay);
            else Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, () -> arena.setSpawningMonsters(true), delay);
        }
    }

    // Function for spawn delay
    private int spawnDelay(int index) {
        int result = (int) (60 * Math.pow(Math.E, - index / 60D));
        return result == 0 ? 1 : result;
    }
}
