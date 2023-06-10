package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.Calculator;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Ammo extends VDWeapon {
	private static final String AMMO = "ammo";

	@NotNull
	public static ItemStack create(Tier tier) {
		List<String> lores = new ArrayList<>();
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
			lores.addAll(CommunicationManager.formatDescriptionList(
				ChatColor.GRAY, description, Constants.LORE_CHAR_LIMIT));

		// Add space in lore from name
		lores.add("");

		// Set capacity
		int capacity;
		switch (tier) {
			case T1:
				capacity = 15;
				break;
			case T2:
				capacity = 25;
				break;
			case T3:
				capacity = 40;
				break;
			case T4:
				capacity = 55;
				break;
			case T5:
				capacity = 75;
				break;
			case T6:
				capacity = 100;
				break;
			default:
				capacity = 0;
		}
		persistentData.put(MAX_CAPACITY_KEY, capacity);
		persistentData.put(CAPACITY_KEY, capacity);
		lores.add(CommunicationManager.format(
			CAPACITY,
			new ColoredMessage(ChatColor.GREEN, Integer.toString(capacity)).toString() +
				new ColoredMessage(ChatColor.WHITE, " / " + capacity)
		));

		// Set refill rate
		double refill;
		switch (tier) {
			case T1:
				refill = 7.5;
				break;
			case T2:
				refill = 6;
				break;
			case T3:
				refill = 4.5;
				break;
			case T4:
				refill = 3.5;
				break;
			case T5:
				refill = 2.5;
				break;
			case T6:
				refill = 2;
				break;
			default:
				refill = 0;
		}
		persistentData2.put(REFILL_KEY, refill);
		if (refill > 0)
			lores.add(CommunicationManager.format(REFILL, Double.toString(refill)));

		// Set price
		int price;
		switch (tier) {
			case T1:
				price = 75;
				break;
			case T2:
				price = 150;
				break;
			case T3:
				price = 275;
				break;
			case T4:
				price = 450;
				break;
			case T5:
				price = 500;
				break;
			case T6:
				price = 650;
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

		// Create item
		return ItemFactory.createItem(Material.NETHER_STAR, name, ItemFactory.BUTTON_FLAGS, null, lores,
			null, persistentData, persistentData2, persistentTags
		);
	}

	public static boolean matches(ItemStack toCheck) {
		if (toCheck == null)
			return false;
		ItemMeta meta = toCheck.getItemMeta();
		if (meta == null)
			return false;
		String value = meta.getPersistentDataContainer().get(ITEM_TYPE_KEY, PersistentDataType.STRING);
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
		Integer maxCap = meta.getPersistentDataContainer().get(MAX_CAPACITY_KEY, PersistentDataType.INTEGER);
		Integer capacity = meta.getPersistentDataContainer().get(CAPACITY_KEY, PersistentDataType.INTEGER);
		Double refill = meta.getPersistentDataContainer().get(REFILL_KEY, PersistentDataType.DOUBLE);
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
		meta.getPersistentDataContainer().set(CAPACITY_KEY, PersistentDataType.INTEGER, capacity);

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
		Integer maxCap = meta.getPersistentDataContainer().get(MAX_CAPACITY_KEY, PersistentDataType.INTEGER);
		Integer capacity = meta.getPersistentDataContainer().get(CAPACITY_KEY, PersistentDataType.INTEGER);
		Double refill = meta.getPersistentDataContainer().get(REFILL_KEY, PersistentDataType.DOUBLE);
		Long nextRefill = meta.getPersistentDataContainer().get(NEXT_REFILL_KEY, PersistentDataType.LONG);

		// Ignore if malformed, full, or doesn't refill
		if (maxCap == null || capacity == null || refill == null || capacity >= maxCap || refill == 0)
			return;

		// Refill if past next refill time
		if (nextRefill == null || nextRefill < System.currentTimeMillis()) {
			meta.getPersistentDataContainer().set(NEXT_REFILL_KEY, PersistentDataType.LONG,
				System.currentTimeMillis() + Calculator.secondsToMillis(refill)
			);
			updateCapacity(ammo, boost ? 2 : 1);
		}

		// Perform updates
		ammo.setItemMeta(meta);
	}
}
