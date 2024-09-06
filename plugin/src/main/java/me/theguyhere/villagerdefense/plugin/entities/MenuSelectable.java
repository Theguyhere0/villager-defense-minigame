package me.theguyhere.villagerdefense.plugin.entities;

import org.bukkit.inventory.ItemStack;

/**
 * A template for any entity that can be selected through a menu to interact with.
 */
public interface MenuSelectable {
	/**
	 * @return Itemstack representing the button for displaying and selecting the entity.
	 */
	ItemStack getDisplayButton();
}
