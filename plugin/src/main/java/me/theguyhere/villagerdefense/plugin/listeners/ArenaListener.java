package me.theguyhere.villagerdefense.plugin.listeners;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.events.JoinArenaEvent;
import me.theguyhere.villagerdefense.plugin.events.LeaveArenaEvent;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaException;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.exceptions.PlayerNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.models.Challenge;
import me.theguyhere.villagerdefense.plugin.game.models.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementChecker;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.plugin.game.models.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import me.theguyhere.villagerdefense.plugin.tools.PlayerManager;
import me.theguyhere.villagerdefense.plugin.tools.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.UUID;

public class ArenaListener implements Listener {
    @EventHandler
    public void onJoin(JoinArenaEvent e) {
        Player player = e.getPlayer();

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

        // Store survival stats if turned on
        if (Main.plugin.getConfig().getBoolean("keepInv"))
            PlayerManager.cacheSurvivalStats(player);

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
            VDPlayer fighter = new VDPlayer(player, arena, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();
            Main.getVillagersTeam().addEntry(player.getUniqueId().toString());

            // Add forced challenges
            arena.getForcedChallengeIDs().forEach(challenge ->
                    fighter.addChallenge(Challenge.getChallengeByID(challenge)));

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
            CommunicationManager.debugInfo("%s joined %s", 2, player.getName(), arena.getName());
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
            VDPlayer fighter = new VDPlayer(player, arena, false);
            arena.getPlayers().add(fighter);
            arena.refreshPortal();
            Main.getVillagersTeam().addEntry(player.getUniqueId().toString());

            // Add forced challenges
            arena.getForcedChallengeIDs().forEach(challenge ->
                    fighter.addChallenge(Challenge.getChallengeByID(challenge)));

            // Give them a game board
            GameManager.createBoard(fighter);

            // Give them starting items
            fighter.giveItems();

            // Debug message to console
            CommunicationManager.debugInfo("%s joined %s", 2, player.getName(), arena.getName());

            // Don't touch task updating
            return;
        }

        // Join players as spectators if arena is full or game already started
        else {
            // Teleport to arena and give time limit bar
            PlayerManager.teleSpectator(player, spawn);
            arena.addPlayerToTimeLimitBar(player);

            // Update player tracking and in-game stats
            arena.getPlayers().add(new VDPlayer(player, arena, true));
            arena.refreshPortal();

            // Debug message to console
            CommunicationManager.debugInfo("%s is spectating %s", 2, player.getName(), arena.getName());

            // Don't touch task updating
            return;
        }

        // Waiting condition
        try {
            arena.startNotifyWaiting();
        } catch (ArenaException ignored) {
        }

        // Quick start condition
        try {
            if (players == arena.getMaxPlayers())
                arena.expediteCountDown();
        } catch (ArenaException err) {
            return;
        }

        // Normal start condition
        try {
            arena.startCountDown();

            // Notify console
            CommunicationManager.debugInfo("%s has started countdown.", 2, arena.getName());
        } catch (ArenaException ignored) {
        }
    }

    @EventHandler
    public void onLeave(LeaveArenaEvent e) {
        Player player = e.getPlayer();
        Arena arena;
        VDPlayer gamer;

        // Attempt to get arena and player
        try {
            arena = GameManager.getArena(player);
            gamer = arena.getPlayer(player);
        } catch (ArenaNotFoundException | PlayerNotFoundException err) {
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
            UUID playerID = player.getUniqueId();
            // Remove from team
            Main.getVillagersTeam().removeEntry(playerID.toString());

            // Update player stats
            PlayerManager.setTotalKills(playerID, PlayerManager.getTotalKills(playerID) + gamer.getKills());
            if (PlayerManager.getTopKills(playerID) < gamer.getKills())
                PlayerManager.setTopKills(playerID, gamer.getKills());

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

                // Apply vault economy multiplier, if active
                if (Main.hasCustomEconomy()) {
                    reward = (int) (reward * Main.plugin.getConfig().getDouble("vaultEconomyMult"));
                    bonus = (int) (bonus * Main.plugin.getConfig().getDouble("vaultEconomyMult"));
                }

                // Give rewards and notify
                PlayerManager.depositCrystalBalance(playerID, reward + bonus);
                PlayerManager.notifySuccess(
                        player,
                        LanguageManager.messages.crystalsEarned,
                        new ColoredMessage(ChatColor.AQUA, String.format("%d (+%d)", reward, bonus)),
                        new ColoredMessage(ChatColor.AQUA, LanguageManager.names.crystals)
                );
            }

            // Mark VDPlayer as left
            gamer.setStatus(PlayerStatus.LEFT);

            // Check if arena can no longer start
            try {
                arena.startNotifyWaiting();
            } catch (ArenaException ignored) {
            }

            // Checks if the game has ended because no players are left
            if (arena.getAlive() == 0)
                try {
                    arena.endGame();
                } catch (ArenaException ignored) {
                }
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

        // Return player survival stats
        if (Main.plugin.getConfig().getBoolean("keepInv") && player.isOnline())
            PlayerManager.returnSurvivalStats(player);

        // Refresh the game portal
        arena.refreshPortal();

        // Refresh all displays for the player
        GameManager.displayEverything(player);

        // Debug message to console
        CommunicationManager.debugInfo("%s left %s", 2, player.getName(), arena.getName());
    }
}
