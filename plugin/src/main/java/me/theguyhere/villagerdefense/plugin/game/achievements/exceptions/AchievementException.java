package me.theguyhere.villagerdefense.plugin.game.achievements.exceptions;

import me.theguyhere.villagerdefense.plugin.game.achievements.Achievement;

/**
 * The base class for exceptions related to {@link Achievement}.
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
