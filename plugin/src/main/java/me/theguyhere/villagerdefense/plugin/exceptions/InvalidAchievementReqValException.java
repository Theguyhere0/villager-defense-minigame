package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementRequirement;
import me.theguyhere.villagerdefense.plugin.game.models.achievements.AchievementType;

/**
 * An exception thrown whenever the {@link AchievementRequirement} value is not valid for the corresponding
 * {@link AchievementType}.
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
