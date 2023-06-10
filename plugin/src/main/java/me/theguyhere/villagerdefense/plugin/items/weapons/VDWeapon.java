package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDWeapon extends VDItem {
	protected static final ColoredMessage ATTACK_TYPE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackType
	);
	public static final NamespacedKey ATTACK_TYPE_KEY = new NamespacedKey(Main.plugin, "attack-type");
	protected static final ColoredMessage ATTACK_TYPE_NORMAL = new ColoredMessage(
		ChatColor.GREEN,
		LanguageManager.names.normal
	);
	protected static final ColoredMessage ATTACK_TYPE_CRUSHING = new ColoredMessage(
		ChatColor.YELLOW,
		LanguageManager.names.crushing
	);
	protected static final ColoredMessage ATTACK_TYPE_PENETRATING = new ColoredMessage(
		ChatColor.RED,
		LanguageManager.names.penetrating
	);
	protected static final ColoredMessage MAIN_DAMAGE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackMainDamage
	);
	protected static final ColoredMessage CRIT_DAMAGE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackCritDamage
	);
	protected static final ColoredMessage SWEEP_DAMAGE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackSweepDamage
	);
	protected static final ColoredMessage RANGE_DAMAGE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackRangeDamage
	);
	protected static final ColoredMessage PIERCE = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.pierce
	);
	public static final NamespacedKey PIERCE_KEY = new NamespacedKey(Main.plugin, "pierce");
	protected static final ColoredMessage SPEED = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.attackSpeed
	);
	public static final NamespacedKey ATTACK_SPEED_KEY = new NamespacedKey(Main.plugin, "attack-speed");
	protected static final ColoredMessage AMMO_COST = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.ammoCost
	);
	public static final NamespacedKey AMMO_COST_KEY = new NamespacedKey(Main.plugin, "ammo-cost");
	protected static final ColoredMessage CAPACITY = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.capacity
	);
	public static final NamespacedKey MAX_CAPACITY_KEY = new NamespacedKey(Main.plugin, "max-capacity");
	public static final NamespacedKey CAPACITY_KEY = new NamespacedKey(Main.plugin, "capacity");
	protected static final ColoredMessage REFILL = new ColoredMessage(
		ChatColor.BLUE,
		String.format(LanguageManager.messages.refill, LanguageManager.messages.seconds)
	);
	public static final NamespacedKey REFILL_KEY = new NamespacedKey(Main.plugin, "refill");
	public static final NamespacedKey NEXT_REFILL_KEY = new NamespacedKey(Main.plugin, "next-refill");
	public static final NamespacedKey PER_BLOCK_KEY = new NamespacedKey(Main.plugin, "per-block");

	public static boolean matches(ItemStack toCheck) {
		return matchesNoAmmo(toCheck) || Ammo.matches(toCheck);
	}

	public static boolean matchesNoAmmo(ItemStack toCheck) {
		return Sword.matches(toCheck) || Axe.matches(toCheck) || Scythe.matches(toCheck) ||
			Bow.matches(toCheck) || Crossbow.matches(toCheck) || OrcClub.matches(toCheck);
	}

	public static boolean matchesClickableWeapon(ItemStack toCheck) {
		return Bow.matches(toCheck) || Crossbow.matches(toCheck);
	}

	public static boolean matchesAmmoWeapon(ItemStack toCheck) {
		return Bow.matches(toCheck) || Crossbow.matches(toCheck);
	}
}
