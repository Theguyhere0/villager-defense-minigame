package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDWeapon extends VDItem {
	public static final NamespacedKey ATTACK_TYPE_KEY = new NamespacedKey(Main.plugin, "attack-type");
	public static final NamespacedKey PIERCE_KEY = new NamespacedKey(Main.plugin, "pierce");
	public static final NamespacedKey ATTACK_SPEED_KEY = new NamespacedKey(Main.plugin, "attack-speed");
	public static final NamespacedKey PER_BLOCK_KEY = new NamespacedKey(Main.plugin, "per-block");

	public static boolean matches(ItemStack toCheck) {
		return Sword.matches(toCheck) || Axe.matches(toCheck) || Scythe.matches(toCheck) ||
			matchesRangedWeapon(toCheck) || OrcClub.matches(toCheck) || Potion.matches(toCheck);
	}

	public static boolean matchesClickableWeapon(ItemStack toCheck) {
		return matchesRangedWeapon(toCheck) || Potion.matches(toCheck);
	}

	public static boolean matchesRangedWeapon(ItemStack toCheck) {
		return Bow.matches(toCheck) || Crossbow.matches(toCheck);
	}
}
