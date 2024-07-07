package me.theguyhere.villagerdefense.plugin.items.armor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Helmet extends VDArmor {
	private static final String HELMET = "helmet";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, HELMET);
		boolean enchant = false;

		// Set material
		Material mat;
		switch (tier) {
			case T1:
				mat = Material.LEATHER_HELMET;
				break;
			case T2:
				mat = Material.CHAINMAIL_HELMET;
				break;
			case T3:
				mat = Material.IRON_HELMET;
				break;
			case T4:
				mat = Material.DIAMOND_HELMET;
				break;
			case T5:
				mat = Material.NETHERITE_HELMET;
				break;
			case T6:
				mat = Material.NETHERITE_HELMET;
				enchant = true;
				break;
			default:
				mat = Material.GOLDEN_HELMET;
		}

		// Set name
		String name;
		switch (tier) {
			case T1:
				name = formatName(LanguageManager.itemLore.helmets.t1.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.helmets.t2.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.helmets.t3.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.helmets.t4.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.helmets.t5.name, tier);
				break;
			case T6:
				name = formatName(LanguageManager.itemLore.helmets.t6.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (tier) {
			case T1:
				description = LanguageManager.itemLore.helmets.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.helmets.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.helmets.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.helmets.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.helmets.t5.description;
				break;
			case T6:
				description = LanguageManager.itemLore.helmets.t6.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder.addDescription(description);

		// Add space in lore from name
		loreBuilder.addSpace();

		// Set armor
		int armor;
		switch (tier) {
			case T1:
				armor = 3;
				break;
			case T2:
				armor = 6;
				break;
			case T3:
				armor = 9;
				break;
			case T4:
				armor = 11;
				break;
			case T5:
				armor = 15;
				break;
			case T6:
				armor = 20;
				break;
			default:
				armor = 0;
		}
		persistentData.put(ARMOR_KEY, armor);
		loreBuilder.addArmor(armor);

		// Set toughness
		int toughness;
		switch (tier) {
			case T2:
				toughness = 1;
				break;
			case T3:
				toughness = 2;
				break;
			case T4:
				toughness = 3;
				break;
			case T5:
				toughness = 4;
				break;
			case T6:
				toughness = 6;
				break;
			default:
				toughness = 0;
		}
		persistentData.put(TOUGHNESS_KEY, toughness);
		loreBuilder.addToughness(toughness);

		// Set weight
		int weight;
		switch (tier) {
			case T1:
				weight = 1;
				break;
			case T2:
				weight = 2;
				break;
			case T3:
				weight = 3;
				break;
			case T4:
				weight = 4;
				break;
			case T5:
			case T6:
				weight = 5;
				break;
			default:
				weight = 0;
		}
		persistentData.put(WEIGHT_KEY, weight);
		loreBuilder.addWeight(weight);
		attributes.put(
			Attribute.GENERIC_KNOCKBACK_RESISTANCE,
			new AttributeModifier(VDItem.MetaKey.DUMMY.name(), 0,
				AttributeModifier.Operation.MULTIPLY_SCALAR_1
			)
		);
		attributes.put(
			Attribute.GENERIC_KNOCKBACK_RESISTANCE,
			new AttributeModifier(VDItem.MetaKey.DUMMY.name(), weight * .01,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);

		// Set price
		int price;
		switch (tier) {
			case T1:
				price = 285;
				break;
			case T2:
				price = 540;
				break;
			case T3:
				price = 820;
				break;
			case T4:
				price = 1045;
				break;
			case T5:
				price = 1435;
				break;
			case T6:
				price = 2040;
				break;
			default:
				price = -1;
		}
		persistentData.put(PRICE_KEY, price);
		if (price >= 0)
			loreBuilder
				.addSpace()
				.addPrice(price);

		// Create item
		ItemStack item = new ItemStackBuilder(mat, name)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setGlowingIfTrue(enchant)
			.setAttributes(attributes)
			.setPersistentData(persistentData)
			.setPersistentTags(persistentTags)
			.build();
		return ItemStackBuilder.makeUnbreakable(item);
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
		return HELMET.equals(value);
	}
}
