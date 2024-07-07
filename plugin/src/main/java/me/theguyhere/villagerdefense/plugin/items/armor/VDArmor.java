package me.theguyhere.villagerdefense.plugin.items.armor;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDArmor extends VDItem {
	public static final NamespacedKey ARMOR_KEY = new NamespacedKey(Main.plugin, "armor");
	public static final NamespacedKey TOUGHNESS_KEY = new NamespacedKey(Main.plugin, "toughness");
	public static final NamespacedKey WEIGHT_KEY = new NamespacedKey(Main.plugin, "weight");

	public static boolean matches(ItemStack toCheck) {
		return Helmet.matches(toCheck) || Chestplate.matches(toCheck) || Leggings.matches(toCheck) ||
			Boots.matches(toCheck);
	}
}
