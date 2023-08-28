package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ammo extends VDWeapon {
	private static final String AMMO = "ammo";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, AMMO);

		// Set name
		String name;
		switch (tier) {
			case T1:
				name = formatName(LanguageManager.itemLore.ammo.t1.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.ammo.t2.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.ammo.t3.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.ammo.t4.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.ammo.t5.name, tier);
				break;
			case T6:
				name = formatName(LanguageManager.itemLore.ammo.t6.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (tier) {
			case T1:
				description = LanguageManager.itemLore.ammo.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.ammo.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.ammo.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.ammo.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.ammo.t5.description;
				break;
			case T6:
				description = LanguageManager.itemLore.ammo.t6.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder
				.addDescription(description)
				.addSpace();

		// Set capacity
		int prevCapacity, currCapacity;
		switch (tier) {
			case T1:
				prevCapacity = currCapacity = 15;
				break;
			case T2:
				prevCapacity = 15;
				currCapacity = 25;
				break;
			case T3:
				prevCapacity = 25;
				currCapacity = 40;
				break;
			case T4:
				prevCapacity = 40;
				currCapacity = 55;
				break;
			case T5:
				prevCapacity = 55;
				currCapacity = 75;
				break;
			case T6:
				prevCapacity = 75;
				currCapacity = 90;
				break;
			default:
				prevCapacity = currCapacity = 0;
		}
		persistentData.put(MAX_CAPACITY_KEY, currCapacity);
		persistentData.put(CAPACITY_KEY, currCapacity);
		loreBuilder.addCapacity(prevCapacity, currCapacity);

		// Set refill rate
		double prevRefill, currRefill;
		switch (tier) {
			case T1:
				prevRefill = currRefill = 7.5;
				break;
			case T2:
				prevRefill = 7.5;
				currRefill = 6;
				break;
			case T3:
				prevRefill = 6;
				currRefill = 4.5;
				break;
			case T4:
				prevRefill = 4.5;
				currRefill = 3.5;
				break;
			case T5:
				prevRefill = 3.5;
				currRefill = 2.5;
				break;
			case T6:
				prevRefill = 2.5;
				currRefill = 2;
				break;
			default:
				prevRefill = currRefill = 0;
		}
		persistentData2.put(REFILL_KEY, currRefill);
		loreBuilder.addRefillRate(prevRefill, currRefill);

		// Set price
		int price;
		switch (tier) {
			case T1:
				price = 100;
				break;
			case T2:
				price = 210;
				break;
			case T3:
				price = 445;
				break;
			case T4:
				price = 785;
				break;
			case T5:
				price = 1500;
				break;
			case T6:
				price = 2250;
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
		return new ItemStackBuilder(Material.NETHER_STAR, name)
			.setLores(loreBuilder)
			.setButtonFlags()
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
		return AMMO.equals(value);
	}

	/**
	 * Updates the capacity of an ammo item by the delta.
	 *
	 * @param ammo  The item to update capacity for.
	 * @param delta Amount to update ammo capacity by.
	 * @return Whether the item remains or not.
	 */
	public static boolean updateCapacity(ItemStack ammo, int delta) {
		// Check for ammo
		if (!matches(ammo))
			return false;

		// Get data
		ItemMeta meta = Objects.requireNonNull(ammo.getItemMeta());
		Integer maxCap = meta
			.getPersistentDataContainer()
			.get(MAX_CAPACITY_KEY, PersistentDataType.INTEGER);
		Integer capacity = meta
			.getPersistentDataContainer()
			.get(CAPACITY_KEY, PersistentDataType.INTEGER);
		Double refill = meta
			.getPersistentDataContainer()
			.get(REFILL_KEY, PersistentDataType.DOUBLE);
		Long nextRefill = meta
			.getPersistentDataContainer()
			.get(NEXT_REFILL_KEY, PersistentDataType.LONG);
		AtomicInteger capIndex = new AtomicInteger();
		List<String> lores = Objects.requireNonNull(meta.getLore());
		lores.forEach(lore -> {
			if (lore.contains(LanguageManager.messages.capacity
				.replace("%s", "")))
				capIndex.set(lores.indexOf(lore));
		});

		// Destroy malformed items
		if (maxCap == null || capacity == null)
			return true;

		// Check if item needs to be removed
		capacity += delta;
		if (capacity <= 0 && refill == null)
			return true;

		// Update capacity data
		meta
			.getPersistentDataContainer()
			.set(CAPACITY_KEY, PersistentDataType.INTEGER, Math.min(capacity, maxCap));

		// Update refill data
		if (refill != null && (nextRefill == null && delta < 0 || capacity < maxCap && delta > 0)) {
			meta
				.getPersistentDataContainer()
				.set(NEXT_REFILL_KEY, PersistentDataType.LONG,
					System.currentTimeMillis() + Calculator.secondsToMillis(refill)
				);
		}
		else if (capacity >= maxCap)
			meta
				.getPersistentDataContainer()
				.remove(NEXT_REFILL_KEY);


		// Set new lore
		ChatColor color = capacity >= .75 * maxCap ? ChatColor.GREEN :
			(capacity <= .25 * maxCap ? ChatColor.RED : ChatColor.YELLOW);
		lores.set(capIndex.get(), CommunicationManager.format(CAPACITY, new ColoredMessage(
			color,
			Integer.toString(capacity)
		).toString() + new ColoredMessage(" / " + maxCap)));
		meta.setLore(lores);
		ammo.setItemMeta(meta);
		return false;
	}

	/**
	 * Attempts to trigger a refill of an ammo item.
	 *
	 * @param ammo  The item to update capacity for.
	 * @param boost Whether the player was boosted for double capacity regen.
	 */
	public static void updateRefill(ItemStack ammo, boolean boost) {
		// Check for ammo
		if (!matches(ammo))
			return;

		// Get data
		ItemMeta meta = Objects.requireNonNull(ammo.getItemMeta());
		Integer maxCap = meta
			.getPersistentDataContainer()
			.get(MAX_CAPACITY_KEY, PersistentDataType.INTEGER);
		Integer capacity = meta
			.getPersistentDataContainer()
			.get(CAPACITY_KEY, PersistentDataType.INTEGER);
		Double refill = meta
			.getPersistentDataContainer()
			.get(REFILL_KEY, PersistentDataType.DOUBLE);
		Long nextRefill = meta
			.getPersistentDataContainer()
			.get(NEXT_REFILL_KEY, PersistentDataType.LONG);

		// Ignore if malformed, full, or doesn't refill
		if (maxCap == null || capacity == null || refill == null || capacity >= maxCap || refill == 0)
			return;

		// Refill if past next refill time
		if (nextRefill == null || nextRefill < System.currentTimeMillis())
			updateCapacity(ammo, boost ? 2 : 1);
	}
}
