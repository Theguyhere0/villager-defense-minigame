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

public abstract class VDItem {
	static final ColoredMessage DURABILITY = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.durability
	);
	public static final NamespacedKey PRICE_KEY = new NamespacedKey(Main.plugin, "price");
	protected static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey(Main.plugin, "item-type");
	public static final NamespacedKey DURATION_KEY = new NamespacedKey(Main.plugin, "duration");
	public static final NamespacedKey INVISIBLE = new NamespacedKey(Main.plugin, "invisible");

	public static boolean matches(ItemStack toCheck) {
		return VDAbility.matches(toCheck) || VDArmor.matches(toCheck) || VDFood.matches(toCheck) ||
			VDWeapon.matches(toCheck) || VDMenuItem.matches(toCheck);
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
