package me.theguyhere.villagerdefense.plugin.items.food;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
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

public abstract class ShopFood extends VDFood {
	private static final String SHOP_FOOD = "shop-food";

	@NotNull
	public static ItemStack create(Tier tier, ShopFoodType type) {
		List<String> lores = new ArrayList<>();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, SHOP_FOOD);

		// Set material and count
		Material mat;
		int count;
		switch (type) {
			case CAPPLE:
				mat = Material.GOLDEN_APPLE;
				count = 1;
				break;
			case GAPPLE:
				mat = Material.ENCHANTED_GOLDEN_APPLE;
				count = 1;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						mat = Material.COOKIE;
						count = 8;
						break;
					case T2:
						mat = Material.BREAD;
						count = 5;
						break;
					case T3:
						mat = Material.COOKED_BEEF;
						count = 3;
						break;
					case T4:
						mat = Material.GOLDEN_CARROT;
						count = 2;
						break;
					default:
						mat = Material.GUNPOWDER;
						count = 1;
				}
				break;
			case TOTEM:
				mat = Material.TOTEM_OF_UNDYING;
				count = 1;
				break;
			default:
				mat = Material.GUNPOWDER;
				count = 1;
		}

		// Set name
		String name;
		switch (type) {
			case CAPPLE:
				name = formatName(LanguageManager.itemLore.shopFood.capple.name, tier);
				break;
			case GAPPLE:
				name = formatName(LanguageManager.itemLore.shopFood.gapple.name, tier);
				break;
			case TIERED:
				switch (tier) {
					case T1:
						name = formatName(LanguageManager.itemLore.shopFood.t1.name, tier);
						break;
					case T2:
						name = formatName(LanguageManager.itemLore.shopFood.t2.name, tier);
						break;
					case T3:
						name = formatName(LanguageManager.itemLore.shopFood.t3.name, tier);
						break;
					case T4:
						name = formatName(LanguageManager.itemLore.shopFood.t4.name, tier);
						break;
					default:
						name = "";
				}
				break;
			case TOTEM:
				name = formatName(LanguageManager.itemLore.shopFood.totem.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (type) {
			case CAPPLE:
				description = LanguageManager.itemLore.shopFood.capple.description;
				break;
			case GAPPLE:
				description = LanguageManager.itemLore.shopFood.gapple.description;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						description = LanguageManager.itemLore.shopFood.t1.description;
						break;
					case T2:
						description = LanguageManager.itemLore.shopFood.t2.description;
						break;
					case T3:
						description = LanguageManager.itemLore.shopFood.t3.description;
						break;
					case T4:
						description = LanguageManager.itemLore.shopFood.t4.description;
						break;
					default:
						description = "";
				}
				break;
			case TOTEM:
				description = LanguageManager.itemLore.shopFood.totem.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			lores.addAll(CommunicationManager.formatDescriptionList(
				ChatColor.GRAY, description, Constants.LORE_CHAR_LIMIT));

		// Add space in lore from name
		lores.add("");

		// Set health heal
		int health;
		switch (type) {
			case CAPPLE:
				health = 100;
				break;
			case GAPPLE:
				health = 150;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						health = 10;
						break;
					case T2:
						health = 30;
						break;
					case T3:
						health = 75;
						break;
					case T4:
						health = 125;
						break;
					default:
						health = 0;
				}
				break;
			default:
				health = 0;
		}
		persistentData.put(HEALTH_KEY, health);
		if (health > 0)
			lores.add(new ColoredMessage(ChatColor.RED, "+" + health + " " + Constants.HP).toString());

		// Set absorption heal
		int absorption;
		switch (type) {
			case CAPPLE:
				absorption = 50;
				break;
			case GAPPLE:
				absorption = 80;
				break;
			case TOTEM:
				absorption = 200;
				break;
			default:
				absorption = 0;
		}
		persistentData.put(ABSORPTION_KEY, absorption);
		if (absorption > 0)
			lores.add(new ColoredMessage(ChatColor.GOLD, "+" + absorption + " " + Constants.HP).toString());

		// Set hunger heal
		int hunger;
		switch (type) {
			case CAPPLE:
				hunger = 3;
				break;
			case GAPPLE:
				hunger = 6;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						hunger = 1;
						break;
					case T2:
						hunger = 3;
						break;
					case T3:
						hunger = 5;
						break;
					case T4:
						hunger = 7;
						break;
					default:
						hunger = 0;
				}
				break;
			case TOTEM:
				hunger = 1;
				break;
			default:
				hunger = 0;
		}
		persistentData.put(HUNGER_KEY, hunger);
		if (hunger > 0)
			lores.add(new ColoredMessage(ChatColor.BLUE, "+" + hunger + " " + Constants.HUNGER).toString());

		// Set price
		int price;
		switch (type) {
			case CAPPLE:
				price = 150;
				break;
			case GAPPLE:
				price = 250;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						price = 20;
						break;
					case T2:
						price = 45;
						break;
					case T3:
						price = 75;
						break;
					case T4:
						price = 100;
						break;
					default:
						price = -1;
				}
				break;
			case TOTEM:
				price = 500;
				break;
			default:
				price = -1;
		}
		persistentData.put(PRICE_KEY, price);
		if (price >= 0) {
			lores.add("");
			lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));
		}

		return ItemFactory.setAmount(ItemFactory.createItem(mat, name, ItemFactory.BUTTON_FLAGS, null, lores,
			null, persistentData, null, persistentTags
		), count);
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
		return SHOP_FOOD.equals(value);
	}

	public enum ShopFoodType {
		CAPPLE,
		GAPPLE,
		TIERED,
		TOTEM,
	}
}
