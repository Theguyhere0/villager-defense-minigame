package me.theguyhere.villagerdefense.plugin.achievements.exceptions;

/**
 * The base class for exceptions related to {@link me.theguyhere.villagerdefense.plugin.achievements.Achievement}.
 */
@SuppressWarnings("unused")
abstract class AchievementException extends Exception {
    AchievementException(String msg) {
        super(msg);
    }

    AchievementException() {
        super();
    }
}
