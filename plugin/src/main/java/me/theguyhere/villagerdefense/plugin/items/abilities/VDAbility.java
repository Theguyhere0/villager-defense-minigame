package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDAbility extends VDItem {
	public static final NamespacedKey COOLDOWN_KEY = new NamespacedKey(Main.plugin, "cooldown");
	public static final NamespacedKey RANGE_KEY = new NamespacedKey(Main.plugin, "abilityRange");

	public static ItemStack createAbility(String kitID, Tier tier) {
		if (Kit
			.knight()
			.getID()
			.equals(kitID))
			return KnightAbility.create(tier);
		else if (Kit
			.mage()
			.getID()
			.equals(kitID))
			return MageAbility.create(tier);
		else if (Kit
			.messenger()
			.getID()
			.equals(kitID))
			return MessengerAbility.create(tier);
		else if (Kit
			.monk()
			.getID()
			.equals(kitID))
			return MonkAbility.create(tier);
		else if (Kit
			.ninja()
			.getID()
			.equals(kitID))
			return NinjaAbility.create(tier);
		else if (Kit
			.priest()
			.getID()
			.equals(kitID))
			return PriestAbility.create(tier);
		else if (Kit
			.siren()
			.getID()
			.equals(kitID))
			return SirenAbility.create(tier);
		else if (Kit
			.templar()
			.getID()
			.equals(kitID))
			return TemplarAbility.create(tier);
		else if (Kit
			.warrior()
			.getID()
			.equals(kitID))
			return WarriorAbility.create(tier);
		else return null;
	}

	public static String formatName(String itemName, String abilityName, Tier tier) {
		return CommunicationManager.format(
			new ColoredMessage(itemName),
			new ColoredMessage(ChatColor.LIGHT_PURPLE, abilityName),
			tier.getLabel()
		);
	}

	protected static int getPrice(Tier tier) {
		switch (tier) {
			case T1:
				return 350;
			case T2:
				return 750;
			case T3:
				return 1200;
			case T4:
				return 1600;
			case T5:
				return 2000;
			default:
				return -1;
		}
	}

	protected static String getDescription(Tier tier) {
		switch (tier) {
			case T0:
				return LanguageManager.itemLore.essences.t0.description;
			case T1:
				return LanguageManager.itemLore.essences.t1.description;
			case T2:
				return LanguageManager.itemLore.essences.t2.description;
			case T3:
				return LanguageManager.itemLore.essences.t3.description;
			case T4:
				return LanguageManager.itemLore.essences.t4.description;
			case T5:
				return LanguageManager.itemLore.essences.t5.description;
			default:
				return "";
		}
	}

	public static boolean matches(ItemStack toCheck) {
		return MageAbility.matches(toCheck) || NinjaAbility.matches(toCheck) || TemplarAbility.matches(toCheck) ||
			WarriorAbility.matches(toCheck) || KnightAbility.matches(toCheck) || PriestAbility.matches(toCheck) ||
			SirenAbility.matches(toCheck) || MonkAbility.matches(toCheck) || MessengerAbility.matches(toCheck);
	}
}