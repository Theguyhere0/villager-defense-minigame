package me.theguyhere.villagerdefense.plugin.game.achievements.exceptions;

import me.theguyhere.villagerdefense.plugin.game.achievements.Achievement;
import me.theguyhere.villagerdefense.plugin.game.achievements.AchievementRequirement;

/**
 * An exception thrown whenever the {@link AchievementRequirement} value is not valid for the corresponding
 * {@link Achievement.Type}.
 */
@SuppressWarnings("unused")
public class InvalidAchievementReqValException extends AchievementException {
    public InvalidAchievementReqValException(String msg) {
        super(msg);
    }

    public InvalidAchievementReqValException() {
        super();
    }
}
