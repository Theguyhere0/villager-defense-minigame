package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class SirenAbility extends VDAbility {
	private static final String SIREN_ABILITY = "siren-ability";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, SIREN_ABILITY);

		// Set name
		String name;
		switch (tier) {
			case T0:
				name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.siren.name, tier);
				break;
			case T1:
				name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.siren.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.siren.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.siren.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.siren.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.siren.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		loreBuilder.addDescription(getDescription(tier));

		// Add space in lore from name, followed by instructions for usage
		loreBuilder
			.addSpace()
			.addDescription(new ColoredMessage(LanguageManager.messages.rightClick).toString());

		// Set effect
		String effect;
		switch (tier) {
			case T0:
			case T1:
				effect = String.format(LanguageManager.kits.siren.effect, "-15%", "-0%");
				break;
			case T2:
				effect = String.format(LanguageManager.kits.siren.effect, "-15%", "-0%" + Constants.UPGRADE + "-10%");
				break;
			case T3:
				effect = String.format(LanguageManager.kits.siren.effect, "-15%", "-10%");
				break;
			case T4:
				effect = String.format(LanguageManager.kits.siren.effect, "-15%" + Constants.UPGRADE + "-30%", "-10%");
				break;
			case T5:
				effect = String.format(LanguageManager.kits.siren.effect, "-30%", "-10%");
				break;
			default:
				effect = null;
		}
		loreBuilder.addEffect(effect);

		// Set range
		double prevRange, currRange;
		switch (tier) {
			case T0:
				prevRange = currRange = 3.5;
				break;
			case T1:
				prevRange = 3.5;
				currRange = 4;
				break;
			case T2:
				prevRange = 4;
				currRange = 4.5;
				break;
			case T3:
				prevRange = 4.5;
				currRange = 5;
				break;
			case T4:
				prevRange = 5;
				currRange = 5.5;
				break;
			case T5:
				prevRange = 5.5;
				currRange = 6;
				break;
			default:
				prevRange = currRange = 0;
		}
		persistentData2.put(RANGE_KEY, currRange);
		loreBuilder.addRange(prevRange, currRange);

		// Set duration
		double prevDuration, currDuration;
		switch (tier) {
			case T0:
				prevDuration = currDuration = 5;
				break;
			case T1:
				prevDuration = 5;
				currDuration = 9.8;
				break;
			case T2:
				prevDuration = 9.8;
				currDuration = 12.8;
				break;
			case T3:
				prevDuration = 12.8;
				currDuration = 17.7;
				break;
			case T4:
				prevDuration = 17.7;
				currDuration = 21.3;
				break;
			case T5:
				prevDuration = 21.3;
				currDuration = 28;
				break;
			default:
				prevDuration = currDuration = 0;
		}
		persistentData2.put(DURATION_KEY, currDuration);
		loreBuilder.addDuration(prevDuration, currDuration);

		// Set cooldown
		double prevCooldown, currCooldown;
		switch (tier) {
			case T0:
				prevCooldown = currCooldown = 30;
				break;
			case T1:
				prevCooldown = 30;
				currCooldown = 29.2;
				break;
			case T2:
				prevCooldown = 29.2;
				currCooldown = 28.5;
				break;
			case T3:
				prevCooldown = 28.5;
				currCooldown = 26.4;
				break;
			case T4:
				prevCooldown = 26.4;
				currCooldown = 25.3;
				break;
			case T5:
				prevCooldown = 25.3;
				currCooldown = 20;
				break;
			default:
				prevCooldown = currCooldown = 0;
		}
		persistentData2.put(COOLDOWN_KEY, currCooldown);
		loreBuilder.addCooldown(prevCooldown, currCooldown);

		// Set price
		int price = getPrice(tier);
		persistentData.put(PRICE_KEY, price);
		if (price >= 0)
			loreBuilder
				.addSpace()
				.addPrice(price);

		// Create item
		return new ItemStackBuilder(
			Material.PINK_DYE,
			name
		)
			.setLores(loreBuilder)
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
		return SIREN_ABILITY.equals(value);
	}
}
