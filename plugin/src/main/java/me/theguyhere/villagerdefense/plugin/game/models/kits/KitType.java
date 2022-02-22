package me.theguyhere.villagerdefense.plugin.game.models.kits;

/**
 * Kit types in Villager Defense. Possible types:<ul>
 *      <li>{@link #NONE}</li>
 *     <li>{@link #GIFT}</li>
 *     <li>{@link #ABILITY}</li>
 *     <li>{@link #EFFECT}</li>
 * </ul>
 */
public enum KitType {
    /** The default kit with no benefits.*/
    NONE,
    /** Kits give one-time benefits per game or respawn.*/
    GIFT,
    /** Kits give a special ability per respawn.*/
    ABILITY,
    /** Kits give players a permanent special effect.*/
    EFFECT
}
