package me.theguyhere.villagerdefense.plugin.game.achievements;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.data.PlayerDataManager;
import me.theguyhere.villagerdefense.plugin.game.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.achievements.exceptions.InvalidAchievementReqValException;
import me.theguyhere.villagerdefense.plugin.game.challenges.Challenge;
import me.theguyhere.villagerdefense.plugin.game.GameManager;
import me.theguyhere.villagerdefense.plugin.game.Arena;
import me.theguyhere.villagerdefense.plugin.game.ArenaStatus;
import me.theguyhere.villagerdefense.plugin.entities.VDPlayer;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import me.theguyhere.villagerdefense.plugin.game.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AchievementChecker {
    private static boolean verifyHighScoreAchievement(Achievement achievement, Player player) {
        // Verify correct achievement type
        if (achievement.getType() != Achievement.Type.HIGH_SCORE)
            return false;

        UUID uuid = player.getUniqueId();
        List<Boolean> targets = new ArrayList<>();

        // Verify each requirement
        for (AchievementRequirement requirement : achievement.getRequirements()) {
            try {
                if (requirement.getMetric() == AchievementMetric.TOP_BALANCE) {
                    if (PlayerDataManager.getPlayerStat(uuid, "topBalance") < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.TOP_KILLS) {
                    if (PlayerDataManager.getPlayerStat(uuid, "topKills") < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);

                }
                if (requirement.getMetric() == AchievementMetric.TOP_WAVE) {
                    if (PlayerDataManager.getPlayerStat(uuid, "topWave") < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                if (requirement.getMetric() == AchievementMetric.TOTAL_GEMS) {
                    if (PlayerDataManager.getPlayerStat(uuid, "totalGems") < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                if (requirement.getMetric() == AchievementMetric.TOTAL_KILLS) {
                    if (PlayerDataManager.getPlayerStat(uuid, "totalKills") < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
            } catch (InvalidAchievementReqValException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        }

        if (achievement.isAnd())
            return !targets.contains(false);
        else return targets.contains(true);
    }

    private static boolean verifyInstanceAchievement(Achievement achievement, VDPlayer player) {
        // Verify correct achievement type
        if (achievement.getType() != Achievement.Type.INSTANCE)
            return false;

        Arena arena;
        try {
            arena = GameManager.getArena(player.getPlayer());
        } catch (ArenaNotFoundException e) {
            return false;
        }
        List<Boolean> targets = new ArrayList<>();

        // Verify each requirement
        for (AchievementRequirement requirement : achievement.getRequirements()) {
            try {
                if (requirement.getMetric() == AchievementMetric.WAVE) {
                    if (arena.getCurrentWave() < requirement.getInteger() && arena.getStatus() == ArenaStatus.ENDING ||
                            arena.getCurrentWave() <= requirement.getInteger() &&
                                    arena.getStatus() == ArenaStatus.ACTIVE)
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.GEMS) {
                    if (player.getGems() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.KIT) {
                    if (!requirement.getString().equals(player.getKit().getName()) &&
                            !requirement.getString().equals(player.getKit2().getName()))
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.CHALLENGE) {
                    if (!player.getChallenges().contains(Challenge.getChallenge(requirement.getString())))
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.PLAYERS) {
                    if (arena.getAlive() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.GHOSTS) {
                    if (arena.getGhostCount() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.SPECTATORS) {
                    if (arena.getSpectatorCount() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.ENEMIES) {
                    if (arena.getEnemies() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.KILLS) {
                    if (player.getKills() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
                else if (requirement.getMetric() == AchievementMetric.ACTIVE_PLAYERS) {
                    if (arena.getActiveCount() < requirement.getInteger())
                        targets.add(false);
                    else targets.add(true);
                }
            } catch (InvalidAchievementReqValException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        }

        if (achievement.isAnd())
            return !targets.contains(false);
        else return targets.contains(true);
    }

    private static boolean verifyKitAchievement(Achievement achievement, Player player) {
        // Verify correct achievement type
        if (achievement.getType() != Achievement.Type.KIT)
            return false;

        List<Boolean> targets = new ArrayList<>();

        // Verify each requirement
        for (AchievementRequirement requirement : achievement.getRequirements()) {
            try {
                if (PlayerDataManager.getPlayerKitLevel(player.getUniqueId(),
                    Objects.requireNonNull(Kit.getKit(requirement.getString())))
                    < requirement.getInteger()) {
                    targets.add(false);
                }
                else {
                    targets.add(true);
                }
            } catch (InvalidAchievementReqValException | NullPointerException e) {
                CommunicationManager.debugErrorShouldNotHappen();
            }
        }

        if (achievement.isAnd())
            return !targets.contains(false);
        else return targets.contains(true);
    }

    private static void notifyAchievement(Achievement achievement, Player player) {
        PlayerManager.notifySuccess(
                player,
                LanguageManager.confirms.achievement,
                new ColoredMessage(ChatColor.AQUA, achievement.getName())
        );
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
    }

    private static void notifyReward(Achievement achievement, Player player) {
        // Check for crystal reward
        if (achievement.getReward().getType() == AchievementReward.Type.CRYSTAL) {
            // Add crystals
            UUID uuid = player.getUniqueId();
            PlayerDataManager.setPlayerCrystals(uuid, PlayerDataManager.getPlayerCrystals(uuid) +
                achievement.getReward().getValue());
            PlayerManager.notifySuccess(
                    player,
                    LanguageManager.confirms.crystalAdd,
                    new ColoredMessage(ChatColor.AQUA, Integer.toString(achievement.getReward().getValue()))
            );
        }

        else if (achievement.getReward().getType() == AchievementReward.Type.BOOST)
            // Notify of boost
            PlayerManager.notifySuccess(player, LanguageManager.confirms.boostAdd);
    }

    public static void checkHighScoreAchievement(Achievement achievement, Player player) {
        UUID uuid = player.getUniqueId();
        List<String> achievements = PlayerDataManager.getPlayerAchievements(uuid);

        // Check if player already has achievement
        if (achievements.contains(achievement.getID()))
            return;

        // Give achievement if achievement is met
        if (verifyHighScoreAchievement(achievement, player)) {
            // Record achievement
            achievements.add(achievement.getID());
            PlayerDataManager.setPlayerAchievements(uuid, achievements);

            // Notify player of achievement and rewards
            notifyAchievement(achievement, player);
            notifyReward(achievement, player);
        }
    }

    public static void checkInstanceAchievement(Achievement achievement, VDPlayer player) {
        // Protect from null players when player quits
        if (player.getPlayer() == null)
            return;

        UUID uuid = player.getID();
        List<String> achievements = PlayerDataManager.getPlayerAchievements(uuid);

        // Check if player already has achievement
        if (achievements.contains(achievement.getID()))
            return;

        // Give achievement if achievement is met
        if (verifyInstanceAchievement(achievement, player)) {
            // Record achievement
            achievements.add(achievement.getID());
            PlayerDataManager.setPlayerAchievements(uuid, achievements);

            // Notify player of achievement and rewards
            notifyAchievement(achievement, player.getPlayer());
            notifyReward(achievement, player.getPlayer());
        }
    }

    public static void checkKitAchievement(Achievement achievement, Player player) {
        UUID uuid = player.getUniqueId();
        List<String> achievements = PlayerDataManager.getPlayerAchievements(uuid);

        // Check if player already has achievement
        if (achievements.contains(achievement.getID()))
            return;

        // Give achievement if achievement is met
        if (verifyKitAchievement(achievement, player)) {
            // Record achievement
            achievements.add(achievement.getID());
            PlayerDataManager.setPlayerAchievements(uuid, achievements);

            // Notify player of achievement and rewards
            notifyAchievement(achievement, player);
            notifyReward(achievement, player);
        }
    }

    public static void checkDefaultHighScoreAchievements(Player player) {
        checkHighScoreAchievement(Achievement.topBalance1(), player);
        checkHighScoreAchievement(Achievement.topBalance2(), player);
        checkHighScoreAchievement(Achievement.topBalance3(), player);
        checkHighScoreAchievement(Achievement.topBalance4(), player);
        checkHighScoreAchievement(Achievement.topBalance5(), player);
        checkHighScoreAchievement(Achievement.topBalance6(), player);
        checkHighScoreAchievement(Achievement.topBalance7(), player);
        checkHighScoreAchievement(Achievement.topBalance8(), player);
        checkHighScoreAchievement(Achievement.topBalance9(), player);
        checkHighScoreAchievement(Achievement.topKills1(), player);
        checkHighScoreAchievement(Achievement.topKills2(), player);
        checkHighScoreAchievement(Achievement.topKills3(), player);
        checkHighScoreAchievement(Achievement.topKills4(), player);
        checkHighScoreAchievement(Achievement.topKills5(), player);
        checkHighScoreAchievement(Achievement.topKills6(), player);
        checkHighScoreAchievement(Achievement.topKills7(), player);
        checkHighScoreAchievement(Achievement.topKills8(), player);
        checkHighScoreAchievement(Achievement.topKills9(), player);
        checkHighScoreAchievement(Achievement.topWave1(), player);
        checkHighScoreAchievement(Achievement.topWave2(), player);
        checkHighScoreAchievement(Achievement.topWave3(), player);
        checkHighScoreAchievement(Achievement.topWave4(), player);
        checkHighScoreAchievement(Achievement.topWave5(), player);
        checkHighScoreAchievement(Achievement.topWave6(), player);
        checkHighScoreAchievement(Achievement.topWave7(), player);
        checkHighScoreAchievement(Achievement.topWave8(), player);
        checkHighScoreAchievement(Achievement.topWave9(), player);
        checkHighScoreAchievement(Achievement.totalGems1(), player);
        checkHighScoreAchievement(Achievement.totalGems2(), player);
        checkHighScoreAchievement(Achievement.totalGems3(), player);
        checkHighScoreAchievement(Achievement.totalGems4(), player);
        checkHighScoreAchievement(Achievement.totalGems5(), player);
        checkHighScoreAchievement(Achievement.totalGems6(), player);
        checkHighScoreAchievement(Achievement.totalGems7(), player);
        checkHighScoreAchievement(Achievement.totalGems8(), player);
        checkHighScoreAchievement(Achievement.totalGems9(), player);
        checkHighScoreAchievement(Achievement.totalKills1(), player);
        checkHighScoreAchievement(Achievement.totalKills2(), player);
        checkHighScoreAchievement(Achievement.totalKills3(), player);
        checkHighScoreAchievement(Achievement.totalKills4(), player);
        checkHighScoreAchievement(Achievement.totalKills5(), player);
        checkHighScoreAchievement(Achievement.totalKills6(), player);
        checkHighScoreAchievement(Achievement.totalKills7(), player);
        checkHighScoreAchievement(Achievement.totalKills8(), player);
        checkHighScoreAchievement(Achievement.totalKills9(), player);
    }

    public static void checkDefaultInstanceAchievements(VDPlayer player) {
        checkInstanceAchievement(Achievement.allChallenges(), player);
        checkInstanceAchievement(Achievement.alone(), player);
        checkInstanceAchievement(Achievement.amputeeAlone(), player);
        checkInstanceAchievement(Achievement.amputeeBalance(), player);
        checkInstanceAchievement(Achievement.amputeeKills(), player);
        checkInstanceAchievement(Achievement.amputeeWave(), player);
        checkInstanceAchievement(Achievement.blindAlone(), player);
        checkInstanceAchievement(Achievement.blindBalance(), player);
        checkInstanceAchievement(Achievement.blindKills(), player);
        checkInstanceAchievement(Achievement.blindWave(), player);
        checkInstanceAchievement(Achievement.clumsyAlone(), player);
        checkInstanceAchievement(Achievement.clumsyBalance(), player);
        checkInstanceAchievement(Achievement.clumsyKills(), player);
        checkInstanceAchievement(Achievement.clumsyWave(), player);
        checkInstanceAchievement(Achievement.dwarfAlone(), player);
        checkInstanceAchievement(Achievement.dwarfBalance(), player);
        checkInstanceAchievement(Achievement.dwarfKills(), player);
        checkInstanceAchievement(Achievement.dwarfWave(), player);
        checkInstanceAchievement(Achievement.explosiveAlone(), player);
        checkInstanceAchievement(Achievement.explosiveBalance(), player);
        checkInstanceAchievement(Achievement.explosiveKills(), player);
        checkInstanceAchievement(Achievement.explosiveWave(), player);
        checkInstanceAchievement(Achievement.featherweightAlone(), player);
        checkInstanceAchievement(Achievement.featherweightBalance(), player);
        checkInstanceAchievement(Achievement.featherweightKills(), player);
        checkInstanceAchievement(Achievement.featherweightWave(), player);
        checkInstanceAchievement(Achievement.nakedAlone(), player);
        checkInstanceAchievement(Achievement.nakedBalance(), player);
        checkInstanceAchievement(Achievement.nakedKills(), player);
        checkInstanceAchievement(Achievement.nakedWave(), player);
        checkInstanceAchievement(Achievement.pacifistAlone(), player);
        checkInstanceAchievement(Achievement.pacifistBalance(), player);
        checkInstanceAchievement(Achievement.pacifistKills(), player);
        checkInstanceAchievement(Achievement.pacifistWave(), player);
        checkInstanceAchievement(Achievement.pacifistUhc(), player);
        checkInstanceAchievement(Achievement.uhcAlone(), player);
        checkInstanceAchievement(Achievement.uhcBalance(), player);
        checkInstanceAchievement(Achievement.uhcKills(), player);
        checkInstanceAchievement(Achievement.uhcWave(), player);
    }

    public static void checkDefaultKitAchievements(Player player) {
        checkKitAchievement(Achievement.allAbility(), player);
        checkKitAchievement(Achievement.allEffect(), player);
        checkKitAchievement(Achievement.allGift(), player);
        checkKitAchievement(Achievement.allKits(), player);
        checkKitAchievement(Achievement.allMaxedAbility(), player);
        checkKitAchievement(Achievement.maxedAbility(), player);
    }
}
