package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;

/**
 * The base class for exception related to {@link Achievement}.
 */
public abstract class AchievementException extends Exception {
    public AchievementException(String msg) {
        super(msg);
    }

    public AchievementException() {
        super();
    }
}
