package me.theguyhere.villagerdefense.plugin.achievements;

/**
 * An exception thrown whenever the {@link AchievementRequirement} value is not valid for the corresponding
 * {@link Achievement.Type}.
 */
@SuppressWarnings("unused")
class InvalidAchievementReqValException extends AchievementException {
    InvalidAchievementReqValException(String msg) {
        super(msg);
    }

    InvalidAchievementReqValException() {
        super();
    }
}
