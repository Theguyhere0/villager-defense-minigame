package me.theguyhere.villagerdefense.plugin.game.models.achievements;

/**
 * Achievement types in Villager Defense. Possible types:<ul>
 *     <li>{@link #HIGH_SCORE}</li>
 *     <li>{@link #INSTANCE}</li>
 *     <li>{@link #KIT}</li>
 * </ul>
 */
public enum AchievementType {
    /** Achievements that observe high scores.*/
    HIGH_SCORE,
    /** Achievements that observe stats after each game.*/
    INSTANCE,
    /** Achievements that observe kit status.*/
    KIT
}
