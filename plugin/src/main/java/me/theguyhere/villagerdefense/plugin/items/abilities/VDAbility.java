package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class VDAbility extends VDItem {
	protected static final ColoredMessage COOLDOWN = new ColoredMessage(
		ChatColor.BLUE,
		String.format(LanguageManager.messages.cooldown, LanguageManager.messages.seconds)
	);
	public static final NamespacedKey COOLDOWN_KEY = new NamespacedKey(Main.plugin, "cooldown");
	protected static final ColoredMessage DURATION = new ColoredMessage(
		ChatColor.BLUE,
		String.format(LanguageManager.messages.duration, LanguageManager.messages.seconds)
	);
	public static final NamespacedKey DURATION_KEY = new NamespacedKey(Main.plugin, "duration");
	protected static final ColoredMessage EFFECT = new ColoredMessage(
		ChatColor.BLUE,
		LanguageManager.messages.effect
	);
	protected static final ColoredMessage RANGE = new ColoredMessage(
		ChatColor.BLUE,
		String.format(LanguageManager.messages.range, LanguageManager.messages.blocks)
	);
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

	protected static List<String> getDescription(Tier tier) {
		String description;
		switch (tier) {
			case T0:
				description = LanguageManager.itemLore.essences.t0.description;
				break;
			case T1:
				description = LanguageManager.itemLore.essences.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.essences.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.essences.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.essences.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.essences.t5.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			return CommunicationManager.formatDescriptionList(
				ChatColor.GRAY, description, Constants.LORE_CHAR_LIMIT);
		else return new ArrayList<>();
	}

	// Modify the cooldown of an ability
	@NotNull
	public static ItemStack modifyCooldown(ItemStack itemStack, double modifier) {
		ItemStack item = itemStack.clone();
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		List<String> lore = Objects.requireNonNull(meta.getLore());
		double cooldown = 0;
		int index = 0;
		for (int i = 0; i < lore.size(); i++) {
			if (lore
				.get(i)
				.contains(LanguageManager.messages.cooldown
					.replace("%s", ""))) {
				cooldown = Double.parseDouble(lore
					.get(i)
					.substring(2 + LanguageManager.messages.cooldown.length())
					.replace(ChatColor.BLUE.toString(), "")
					.replace(LanguageManager.messages.seconds.substring(3), ""));
				index = i;
			}
		}

		if (index == 0 || cooldown == 0)
			return item;

		cooldown *= modifier;
		lore.set(index, CommunicationManager.format(COOLDOWN, String.format("%.2f", cooldown)));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static boolean matches(ItemStack toCheck) {
		return MageAbility.matches(toCheck) || NinjaAbility.matches(toCheck) || TemplarAbility.matches(toCheck) ||
			WarriorAbility.matches(toCheck) || KnightAbility.matches(toCheck) || PriestAbility.matches(toCheck) ||
			SirenAbility.matches(toCheck) || MonkAbility.matches(toCheck) || MessengerAbility.matches(toCheck);
	}
}