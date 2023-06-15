package me.theguyhere.villagerdefense.plugin.items;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.items.menuItems.VDMenuItem;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class VDItem {
	protected static final ColoredMessage DURABILITY = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.durability
	);
	public static final NamespacedKey PRICE_KEY = new NamespacedKey(Main.plugin, "price");
	public static final NamespacedKey MAX_DURABILITY_KEY = new NamespacedKey(Main.plugin, "max-durability");
	public static final NamespacedKey DURABILITY_KEY = new NamespacedKey(Main.plugin, "durability");
	protected static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(Main.plugin, "item-type");
	public static final NamespacedKey INVISIBLE = new NamespacedKey(Main.plugin, "invisible");

	public static boolean matches(ItemStack toCheck) {
		return VDAbility.matches(toCheck) || VDArmor.matches(toCheck) || VDFood.matches(toCheck) ||
			VDWeapon.matches(toCheck) || VDMenuItem.matches(toCheck);
	}

	/**
	 * Wears down an item's durability incrementally.
	 *
	 * @param item The item to wear down.
	 * @return Whether the item remains or not.
	 */
	public static boolean updateDurability(ItemStack item) {
		return updateDurability(item, -1);
	}

	/**
	 * Wears down an item's durability by a certain percentage of its max durability. A negative value wears down the
	 * item incrementally.
	 *
	 * @param item          The item to wear down.
	 * @param damagePercent The percent of the item's max durability to wear down.
	 * @return Whether the item remains or not.
	 */
	public static boolean updateDurability(ItemStack item, double damagePercent) {
		// Ignore non-relevant items
		if (!(VDWeapon.matches(item) || VDArmor.matches(item)))
			return true;

		// Get data
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		Integer maxDur = meta
			.getPersistentDataContainer()
			.get(MAX_DURABILITY_KEY, PersistentDataType.INTEGER);
		Integer durability = meta
			.getPersistentDataContainer()
			.get(DURABILITY_KEY, PersistentDataType.INTEGER);
		AtomicInteger durIndex = new AtomicInteger();
		List<String> lores = Objects.requireNonNull(meta.getLore());
		lores.forEach(lore -> {
			if (lore.contains(LanguageManager.messages.durability
				.replace("%s", "")))
				durIndex.set(lores.indexOf(lore));
		});

		// Destroy malformed items
		if (maxDur == null || durability == null)
			return false;

		// Ignore unbreakable items
		if (maxDur == 0)
			return true;

		// Update and check for used up item
		if (damagePercent < 0)
			durability--;
		else durability += (int) Math.round(damagePercent * maxDur);
		Damageable damage = (Damageable) meta;
		if (durability <= 0)
			return false;

		// Update durability data
		meta
			.getPersistentDataContainer()
			.set(DURABILITY_KEY, PersistentDataType.INTEGER, durability);

		// Set new lore
		ChatColor color = durability >= .75 * maxDur ? ChatColor.GREEN :
			(durability <= .25 * maxDur ? ChatColor.RED : ChatColor.YELLOW);
		lores.set(durIndex.get(), CommunicationManager.format(
			DURABILITY,
			new ColoredMessage(color, Integer.toString(durability)).toString() +
				new ColoredMessage(" / " + maxDur)
		));
		meta.setLore(lores);

		// Set damage indicator
		damage.setDamage((int) item
			.getType()
			.getMaxDurability() - (int) (durability * 1. / maxDur *
			item
				.getType()
				.getMaxDurability()));

		item.setItemMeta(meta);
		return true;
	}

	public static String formatName(ChatColor color, String name, Tier tier) {
		return CommunicationManager.format(
			new ColoredMessage(color, name),
			tier.getLabel()
		);
	}

	public static String formatName(String name, Tier tier) {
		return CommunicationManager.format(
			new ColoredMessage(name),
			tier.getLabel()
		);
	}

	public enum Tier {
		SET(new ColoredMessage(ChatColor.YELLOW, "[S]")),
		T0(new ColoredMessage(ChatColor.AQUA, "[T0]")),
		T1(new ColoredMessage(ChatColor.AQUA, "[T1]")),
		T2(new ColoredMessage(ChatColor.AQUA, "[T2]")),
		T3(new ColoredMessage(ChatColor.AQUA, "[T3]")),
		T4(new ColoredMessage(ChatColor.AQUA, "[T4]")),
		T5(new ColoredMessage(ChatColor.AQUA, "[T5]")),
		T6(new ColoredMessage(ChatColor.AQUA, "[T6]")),
		UNIQUE(new ColoredMessage(ChatColor.LIGHT_PURPLE, "[U]"));

		private final ColoredMessage label;

		Tier(ColoredMessage label) {
			this.label = label;
		}

		public ColoredMessage getLabel() {
			return label;
		}
	}

	public enum MetaKey {
		DAMAGE,
		KNOCKBACK,
		PER_BLOCK,
		ORIGIN_LOCATION,
		ATTACK_SPEED,
		DUMMY
	}
}
