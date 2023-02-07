package me.theguyhere.villagerdefense.plugin.game.models.players;

/**
 * Status of players in Villager Defense. Possible status:<ul>
 *     <li>{@link #ALIVE}</li>
 *     <li>{@link #GHOST}</li>
 *     <li>{@link #SPECTATOR}</li>
 * </ul>
 */
public enum PlayerStatus {
    /** Player is alive and active in the game.*/
    ALIVE,
    /** Player is dead but active in the game.*/
    GHOST,
    /** Player is spectating in the game.*/
    SPECTATOR
}
