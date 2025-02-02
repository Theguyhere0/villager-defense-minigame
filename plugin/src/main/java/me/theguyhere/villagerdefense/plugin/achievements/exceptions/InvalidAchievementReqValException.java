package me.theguyhere.villagerdefense.plugin.achievements.exceptions;

/**
 * An exception thrown whenever the {@link me.theguyhere.villagerdefense.plugin.achievements.AchievementRequirement} value is not valid for the corresponding
 * {@link me.theguyhere.villagerdefense.plugin.achievements.Achievement.Type}.
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
