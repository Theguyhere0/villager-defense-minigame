package me.theguyhere.villagerdefense.plugin.achievements.exceptions;

/**
 * An exception thrown whenever the {@link me.theguyhere.villagerdefense.plugin.achievements.AchievementRequirement} type doesn't match the {@link me.theguyhere.villagerdefense.plugin.achievements.Achievement} type.
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
