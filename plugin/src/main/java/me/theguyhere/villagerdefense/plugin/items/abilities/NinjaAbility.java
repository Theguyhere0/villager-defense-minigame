package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class NinjaAbility extends VDAbility {
	private static final String NINJA_ABILITY = "ninja-ability";

	@NotNull
	public static ItemStack create(Tier tier) {
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, NINJA_ABILITY);

		// Set name
		String name;
		switch (tier) {
			case T0:
				name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.ninja.name, tier);
				break;
			case T1:
				name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.ninja.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.ninja.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.ninja.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.ninja.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.ninja.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		List<String> lores = new ArrayList<>(getDescription(tier));

		// Add space in lore from name, followed by instructions for usage
		lores.add("");
		lores.add(new ColoredMessage(LanguageManager.messages.rightClick).toString());

		// Set effect
		String effect;
		switch (tier) {
			case T0:
			case T1:
			case T2:
			case T3:
			case T4:
			case T5:
				effect = LanguageManager.kits.ninja.effect;
				break;
			default:
				effect = null;
		}
		if (effect != null)
			lores.addAll(CommunicationManager.formatDescriptionList(ChatColor.LIGHT_PURPLE,
				CommunicationManager.format(EFFECT, new ColoredMessage(
					ChatColor.LIGHT_PURPLE,
					effect
				)), Constants.LORE_CHAR_LIMIT
			));

		// Set duration
		double duration;
		switch (tier) {
			case T0:
				duration = 5;
				break;
			case T1:
				duration = 9.8;
				break;
			case T2:
				duration = 12.8;
				break;
			case T3:
				duration = 17.7;
				break;
			case T4:
				duration = 21.3;
				break;
			case T5:
				duration = 28;
				break;
			default:
				duration = 0;
		}
		persistentData2.put(DURATION_KEY, duration);
		if (duration > 0)
			lores.add(CommunicationManager.format(DURATION, new ColoredMessage(
				ChatColor.DARK_AQUA,
				Double.toString(duration)
			)));

		// Set cooldown
		double cooldown;
		switch (tier) {
			case T0:
				cooldown = 45;
				break;
			case T1:
				cooldown = 44.2;
				break;
			case T2:
				cooldown = 43.5;
				break;
			case T3:
				cooldown = 41.4;
				break;
			case T4:
				cooldown = 40.3;
				break;
			case T5:
				cooldown = 35;
				break;
			default:
				cooldown = 0;
		}
		persistentData2.put(COOLDOWN_KEY, cooldown);
		if (cooldown > 0)
			lores.add(CommunicationManager.format(COOLDOWN, Double.toString(cooldown)));

		// Set price
		int price = getPrice(tier);
		persistentData.put(PRICE_KEY, price);
		if (price >= 0) {
			lores.add("");
			lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));
		}

		// Create item
		return new ItemStackBuilder(
			Material.BLACK_DYE,
			name
		)
			.setLores(lores.toArray(new String[0]))
			.setHideEnchantFlags()
			.setGlowingIfTrue(true)
			.setPersistentData(persistentData)
			.setPersistentData2(persistentData2)
			.setPersistentTags(persistentTags)
			.build();
	}

	public static boolean matches(ItemStack toCheck) {
		if (toCheck == null)
			return false;
		ItemMeta meta = toCheck.getItemMeta();
		if (meta == null)
			return false;
		String value = meta
			.getPersistentDataContainer()
			.get(ITEM_TYPE_KEY, PersistentDataType.STRING);
		if (value == null)
			return false;
		return NINJA_ABILITY.equals(value);
	}
}
