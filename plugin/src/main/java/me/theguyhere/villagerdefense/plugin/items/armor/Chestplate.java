package me.theguyhere.villagerdefense.plugin.items.armor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.plugin.Main;
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

public abstract class Chestplate extends VDArmor {
	private static final String CHESTPLATE = "chestplate";

	@NotNull
	public static ItemStack create(Tier tier, ChestplateType type) {
		LoreBuilder loreBuilder = new LoreBuilder();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, CHESTPLATE);
		boolean enchant = false;

		// Set material
		Material mat;
		switch (type) {
			case TAILOR:
				mat = Material.LEATHER_CHESTPLATE;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						mat = Material.LEATHER_CHESTPLATE;
						break;
					case T2:
						mat = Material.CHAINMAIL_CHESTPLATE;
						break;
					case T3:
						mat = Material.IRON_CHESTPLATE;
						break;
					case T4:
						mat = Material.DIAMOND_CHESTPLATE;
						break;
					case T5:
						mat = Material.NETHERITE_CHESTPLATE;
						break;
					case T6:
						mat = Material.NETHERITE_CHESTPLATE;
						enchant = true;
						break;
					default:
						mat = Material.GOLDEN_CHESTPLATE;
				}
				break;
			default:
				mat = Material.GOLDEN_CHESTPLATE;
		}

		// Set name
		String name;
		switch (type) {
			case TAILOR:
				name = formatName(LanguageManager.kits.tailor.items.chestplate, tier);
				break;
			case TIERED:
				switch (tier) {
					case T1:
						name = formatName(LanguageManager.itemLore.chestplates.t1.name, tier);
						break;
					case T2:
						name = formatName(LanguageManager.itemLore.chestplates.t2.name, tier);
						break;
					case T3:
						name = formatName(LanguageManager.itemLore.chestplates.t3.name, tier);
						break;
					case T4:
						name = formatName(LanguageManager.itemLore.chestplates.t4.name, tier);
						break;
					case T5:
						name = formatName(LanguageManager.itemLore.chestplates.t5.name, tier);
						break;
					case T6:
						name = formatName(LanguageManager.itemLore.chestplates.t6.name, tier);
						break;
					default:
						name = "";
				}
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (type) {
			case TAILOR:
				description = LanguageManager.kits.tailor.items.chestplateDesc;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						description = LanguageManager.itemLore.chestplates.t1.description;
						break;
					case T2:
						description = LanguageManager.itemLore.chestplates.t2.description;
						break;
					case T3:
						description = LanguageManager.itemLore.chestplates.t3.description;
						break;
					case T4:
						description = LanguageManager.itemLore.chestplates.t4.description;
						break;
					case T5:
						description = LanguageManager.itemLore.chestplates.t5.description;
						break;
					case T6:
						description = LanguageManager.itemLore.chestplates.t6.description;
						break;
					default:
						description = "";
				}
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
		switch (type) {
			case TAILOR:
				armor = 4;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						armor = 5;
						break;
					case T2:
						armor = 8;
						break;
					case T3:
						armor = 11;
						break;
					case T4:
						armor = 14;
						break;
					case T5:
						armor = 19;
						break;
					case T6:
						armor = 23;
						break;
					default:
						armor = 0;
				}
				break;
			default:
				armor = 0;
		}
		persistentData.put(ARMOR_KEY, armor);
		loreBuilder.addArmor(armor);

		// Set toughness
		int toughness;
		switch (type) {
			case TIERED:
				switch (tier) {
					case T2:
						toughness = 1;
						break;
					case T3:
						toughness = 3;
						break;
					case T4:
						toughness = 6;
						break;
					case T5:
						toughness = 8;
						break;
					case T6:
						toughness = 10;
						break;
					default:
						toughness = 0;
				}
				break;
			default:
				toughness = 0;
		}
		persistentData.put(TOUGHNESS_KEY, toughness);
		loreBuilder.addToughness(toughness);

		// Set weight
		int weight;
		switch (type) {
			case TAILOR:
				weight = 1;
				break;
			case TIERED:
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

		// Set durability
		int durability;
		switch (type) {
			case TIERED:
				switch (tier) {
					case T1:
						durability = 85;
						break;
					case T2:
						durability = 145;
						break;
					case T3:
						durability = 225;
						break;
					case T4:
						durability = 310;
						break;
					case T5:
						durability = 425;
						break;
					case T6:
						durability = 475;
						break;
					default:
						durability = 0;
				}
				break;
			default:
				durability = 0;
		}
		persistentData.put(MAX_DURABILITY_KEY, durability);
		persistentData.put(DURABILITY_KEY, durability);
		loreBuilder.addDurability(durability);

		// Set price
		int price;
		switch (type) {
			case TIERED:
				switch (tier) {
					case T1:
					case T2:
					case T3:
					case T4:
					case T5:
					case T6:
						price = calculateTieredPrice(durability, armor, toughness);
						break;
					default:
						price = -1;
				}
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
		if (durability == 0)
			return ItemStackBuilder.makeUnbreakable(item);
		else return item;
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
		return CHESTPLATE.equals(value);
	}

	public enum ChestplateType {
		TAILOR,
		TIERED,
	}
}