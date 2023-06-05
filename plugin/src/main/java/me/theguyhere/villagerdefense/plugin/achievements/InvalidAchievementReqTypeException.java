package me.theguyhere.villagerdefense.plugin.achievements;

/**
 * An exception thrown whenever the {@link AchievementRequirement} type doesn't match the {@link Achievement} type.
 */
@SuppressWarnings("unused")
class InvalidAchievementReqTypeException extends AchievementException {
    InvalidAchievementReqTypeException(String msg) {
        super(msg);
    }

    InvalidAchievementReqTypeException() {
        super();
    }
}
