package me.theguyhere.villagerdefense.plugin.entities;

import org.bukkit.inventory.ItemStack;

/**
 * A template for any entity that can be upgraded by a player. Interaction through a menu is implied.
 */
public interface Upgradeable extends MenuSelectable {
	/**
	 * @return Itemstack representing the button for upgrading the entity.
	 */
	@Deprecated
	ItemStack getUpgradeButton();

	@Deprecated
	boolean isMaxed();

	/**
	 * Upgrades the entity by incrementing its level.
	 */
	void upgrade();
}
