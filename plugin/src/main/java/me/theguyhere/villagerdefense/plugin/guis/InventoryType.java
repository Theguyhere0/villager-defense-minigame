package me.theguyhere.villagerdefense.plugin.guis;

/**
 * Type of custom inventory. Possible inventory types:<ul>
 *     <li>{@link #COLLECTOR}</li>
 *     <li>{@link #CONTROLLED}</li>
 *     <li>{@link #DISPENSER}</li>
 *     <li>{@link #DISPLAY}</li>
 *     <li>{@link #FREE}</li>
 *     <li>{@link #MENU}</li>
 * </ul>
 */
public enum InventoryType {
    /**
     * A {@link #CONTROLLED} inventory that allows items to be put in but not be taken out.
     */
    COLLECTOR,
    /**
     * An inventory with removable or non-removable elements
     */
    CONTROLLED,
    /**
     * A {@link #CONTROLLED} inventory that allows items to be taken out but not be put in.
     */
    DISPENSER,
    /**
     * An inventory with no clickable elements whatsoever.
     */
    DISPLAY,
    /**
     * An inventory that has no special control over inventory space.
     */
    FREE,
    /**
     * An inventory with clickable or non-clickable elements but not removable elements.
     */
    MENU
}
