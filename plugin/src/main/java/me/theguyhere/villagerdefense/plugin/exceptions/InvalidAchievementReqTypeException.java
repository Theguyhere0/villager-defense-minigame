package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementRequirement;

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
