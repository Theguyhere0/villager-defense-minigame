package me.theguyhere.villagerdefense.plugin.achievements;

import lombok.Getter;

/**
 * Metrics that achievements can track in Villager Defense.
 */
@Getter
public enum AchievementMetric {
    TOTAL_KILLS(Achievement.Type.HIGH_SCORE),
    TOP_KILLS(Achievement.Type.HIGH_SCORE),
    TOP_WAVE(Achievement.Type.HIGH_SCORE),
    TOP_BALANCE(Achievement.Type.HIGH_SCORE),
    TOTAL_GEMS(Achievement.Type.HIGH_SCORE),
    WAVE(Achievement.Type.INSTANCE),
    GEMS(Achievement.Type.INSTANCE),
    KIT(Achievement.Type.INSTANCE),
    CHALLENGE(Achievement.Type.INSTANCE),
    PLAYERS(Achievement.Type.INSTANCE),
    GHOSTS(Achievement.Type.INSTANCE),
    SPECTATORS(Achievement.Type.INSTANCE),
    ENEMIES(Achievement.Type.INSTANCE),
    KILLS(Achievement.Type.INSTANCE),
    ACTIVE_PLAYERS(Achievement.Type.INSTANCE),
    KIT_OWN(Achievement.Type.KIT);

    private final Achievement.Type type;

    AchievementMetric(Achievement.Type type) {
        this.type = type;
    }
}
