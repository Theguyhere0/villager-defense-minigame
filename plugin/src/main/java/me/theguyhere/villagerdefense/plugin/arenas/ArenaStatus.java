package me.theguyhere.villagerdefense.plugin.arenas;

/**
 * Status of arenas in Villager Defense. Possible status:<ul>
 *     <li>{@link #WAITING}</li>
 *     <li>{@link #ACTIVE}</li>
 *     <li>{@link #ENDING}</li>
 * </ul>
 */
public enum ArenaStatus {
    /** Arena is waiting for players to start.*/
    WAITING,
    /** Arena is ongoing.*/
    ACTIVE,
    /** Arena is ending the current game.*/
    ENDING
}
