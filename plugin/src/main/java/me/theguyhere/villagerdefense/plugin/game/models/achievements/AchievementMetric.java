package me.theguyhere.villagerdefense.plugin.game.models.achievements;

import lombok.Getter;

/**
 * Metrics that achievements can track in Villager Defense.
 */
@Getter
public enum AchievementMetric {
    TOTAL_KILLS(AchievementType.HIGH_SCORE),
    TOP_KILLS(AchievementType.HIGH_SCORE),
    TOP_WAVE(AchievementType.HIGH_SCORE),
    TOP_BALANCE(AchievementType.HIGH_SCORE),
    TOTAL_GEMS(AchievementType.HIGH_SCORE),
    WAVE(AchievementType.INSTANCE),
    GEMS(AchievementType.INSTANCE),
    KIT(AchievementType.INSTANCE),
    CHALLENGE(AchievementType.INSTANCE),
    PLAYERS(AchievementType.INSTANCE),
    GHOSTS(AchievementType.INSTANCE),
    SPECTATORS(AchievementType.INSTANCE),
    ENEMIES(AchievementType.INSTANCE),
    KILLS(AchievementType.INSTANCE),
    ACTIVE_PLAYERS(AchievementType.INSTANCE),
    KIT_OWN(AchievementType.KIT);

    private final AchievementType type;

    AchievementMetric(AchievementType type) {
        this.type = type;
    }
}
