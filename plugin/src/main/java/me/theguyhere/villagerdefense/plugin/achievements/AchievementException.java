package me.theguyhere.villagerdefense.plugin.achievements;

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
