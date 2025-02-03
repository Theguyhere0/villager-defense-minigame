package me.theguyhere.villagerdefense.plugin.game.achievements.exceptions;

import me.theguyhere.villagerdefense.plugin.game.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.achievements.AchievementRequirement;

/**
 * An exception thrown whenever the {@link AchievementRequirement} type doesn't match the {@link Achievement} type.
 */
@SuppressWarnings("unused")
public class InvalidAchievementReqTypeException extends AchievementException {
    public InvalidAchievementReqTypeException(String msg) {
        super(msg);
    }

    public InvalidAchievementReqTypeException() {
        super();
    }
}
