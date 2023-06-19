package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDWeapon extends VDItem {
	public static final NamespacedKey ATTACK_TYPE_KEY = new NamespacedKey(Main.plugin, "attack-type");
	public static final NamespacedKey PIERCE_KEY = new NamespacedKey(Main.plugin, "pierce");
	public static final NamespacedKey ATTACK_SPEED_KEY = new NamespacedKey(Main.plugin, "attack-speed");
	public static final NamespacedKey AMMO_COST_KEY = new NamespacedKey(Main.plugin, "ammo-cost");
	public static final ColoredMessage CAPACITY = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.capacity
	);
	public static final NamespacedKey MAX_CAPACITY_KEY = new NamespacedKey(Main.plugin, "max-capacity");
	public static final NamespacedKey CAPACITY_KEY = new NamespacedKey(Main.plugin, "capacity");
	public static final NamespacedKey REFILL_KEY = new NamespacedKey(Main.plugin, "refill");
	public static final NamespacedKey NEXT_REFILL_KEY = new NamespacedKey(Main.plugin, "next-refill");
	public static final NamespacedKey PER_BLOCK_KEY = new NamespacedKey(Main.plugin, "per-block");

	public static boolean matches(ItemStack toCheck) {
		return matchesNoAmmo(toCheck) || Ammo.matches(toCheck);
	}

	public static boolean matchesNoAmmo(ItemStack toCheck) {
		return Sword.matches(toCheck) || Axe.matches(toCheck) || Scythe.matches(toCheck) ||
			Bow.matches(toCheck) || Crossbow.matches(toCheck) || OrcClub.matches(toCheck) || Potion.matches(toCheck);
	}

	public static boolean matchesClickableWeapon(ItemStack toCheck) {
		return Bow.matches(toCheck) || Crossbow.matches(toCheck) || Potion.matches(toCheck);
	}

	public static boolean matchesAmmoWeapon(ItemStack toCheck) {
		return Bow.matches(toCheck) || Crossbow.matches(toCheck);
	}
}
