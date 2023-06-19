package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Potion extends VDWeapon {
	private static final String POTION = "potion";

	@NotNull
	public static ItemStack create(PotionType type) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, POTION);

		// Set material
		Material mat;
		switch (type) {
			case ALCHEMIST_REGEN:
				mat = Material.SPLASH_POTION;
				break;
			default:
				mat = Material.POTION;
		}

		// Set potion data
		PotionData potionData;
		switch (type) {
			case ALCHEMIST_REGEN:
				potionData = new PotionData(org.bukkit.potion.PotionType.REGEN);
				break;
			case ALCHEMIST_SPEED:
				potionData = new PotionData(org.bukkit.potion.PotionType.SPEED);
				break;
			case ALCHEMIST_STRENGTH:
				potionData = new PotionData(org.bukkit.potion.PotionType.STRENGTH);
				break;
			default:
				potionData = new PotionData(org.bukkit.potion.PotionType.WATER);
		}

		// Set name
		String name;
		switch (type) {
			case ALCHEMIST_REGEN:
				name = formatName(ChatColor.GREEN, LanguageManager.kits.alchemist.items.regen, Tier.UNIQUE);
				break;
			case ALCHEMIST_SPEED:
				name = formatName(ChatColor.GREEN, LanguageManager.kits.alchemist.items.speed, Tier.UNIQUE);
				break;
			case ALCHEMIST_STRENGTH:
				name = formatName(ChatColor.GREEN, LanguageManager.kits.alchemist.items.strength, Tier.UNIQUE);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (type) {
			case ALCHEMIST_REGEN:
			case ALCHEMIST_SPEED:
			case ALCHEMIST_STRENGTH:
				description = LanguageManager.kits.alchemist.items.potionDesc;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder
				.addDescription(description)
				.addSpace();

		// Set effect
		String effect;
		switch (type) {
			case ALCHEMIST_REGEN:
				effect = String.format(LanguageManager.kits.priest.effect, "+5");
				break;
			case ALCHEMIST_SPEED:
				effect = String.format(LanguageManager.kits.messenger.effect, "+20%");
				break;
			case ALCHEMIST_STRENGTH:
				effect = String.format(LanguageManager.kits.warrior.effect, "+10%");
				break;
			default:
				effect = null;
		}
		loreBuilder.addEffect(effect);

		// Set duration
		double duration;
		switch (type) {
			case ALCHEMIST_REGEN:
				duration = 30;
				break;
			case ALCHEMIST_SPEED:
				duration = 90;
				break;
			case ALCHEMIST_STRENGTH:
				duration = 60;
				break;
			default:
				duration = 0;
		}
		persistentData2.put(DURATION_KEY, duration);
		loreBuilder.addDuration(duration, duration);

		// Create item
		return new ItemStackBuilder(mat, name)
			.setPotionData(potionData)
			.setLores(loreBuilder)
			.setButtonFlags()
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
		return POTION.equals(value);
	}

	public enum PotionType {
		ALCHEMIST_REGEN,
		ALCHEMIST_SPEED,
		ALCHEMIST_STRENGTH
	}

}
