package me.theguyhere.villagerdefense.plugin.items.food;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class FarmerCarrot extends VDFood {
	private static final String FARMER_CARROT = "farmer-carrot";

	@NotNull
	public static ItemStack create() {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, FARMER_CARROT);

		// Set description
		loreBuilder
			.addDescription(LanguageManager.kits.farmer.items.carrotDesc)
			.addSpace();

		// Set health heal
		int health = 15;
		persistentData.put(HEALTH_KEY, health);
		loreBuilder.addHealthGain(health);

		// Set hunger heal
		int hunger = 2;
		persistentData.put(HUNGER_KEY, health);
		loreBuilder.addHungerGain(hunger);

		return new ItemStackBuilder(
			Material.CARROT,
			VDItem.formatName(ChatColor.GREEN, LanguageManager.kits.farmer.items.carrot, VDItem.Tier.UNIQUE)
		)
			.setAmount(3)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setPersistentData(persistentData)
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
		return FARMER_CARROT.equals(value);
	}
}
