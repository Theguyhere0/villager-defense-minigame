package me.theguyhere.villagerdefense.plugin.items.armor;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDArmor extends VDItem {
	protected static final ColoredMessage ARMOR = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.armor
	);
	public static final NamespacedKey ARMOR_KEY = new NamespacedKey(Main.plugin, "armor");
	protected static final ColoredMessage TOUGHNESS = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.toughness
	);
	public static final NamespacedKey TOUGHNESS_KEY = new NamespacedKey(Main.plugin, "toughness");
	protected static final ColoredMessage WEIGHT = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.weight
	);
	public static final NamespacedKey WEIGHT_KEY = new NamespacedKey(Main.plugin, "weight");

	public static boolean matches(ItemStack toCheck) {
		return Helmet.matches(toCheck) || Chestplate.matches(toCheck) || Leggings.matches(toCheck) ||
			Boots.matches(toCheck);
	}
}
