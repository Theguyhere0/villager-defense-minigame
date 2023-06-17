package me.theguyhere.villagerdefense.plugin.items.eggs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDIronGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDSnowGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDCat;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDDog;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
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

public abstract class VDEgg extends VDItem {
	private static final String EGG = "egg";

	@NotNull
	public static ItemStack create(int level, EggType type) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, EGG);

		// Set material
		Material mat;
		switch (type) {
			case CAT:
				mat = Material.CAT_SPAWN_EGG;
				break;
			case DOG:
				mat = Material.WOLF_SPAWN_EGG;
				break;
			case HORSE:
				mat = Material.HORSE_SPAWN_EGG;
				break;
			case IRON_GOLEM:
				mat = Material.SKELETON_HORSE_SPAWN_EGG;
				break;
			case SNOW_GOLEM:
				mat = Material.GHAST_SPAWN_EGG;
				break;
			default:
				mat = Material.EGG;
		}

		// Set name
		String name;
		switch (type) {
			case CAT:
				name = CommunicationManager.format(
					new ColoredMessage(LanguageManager.messages.eggName),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
					new ColoredMessage(LanguageManager.mobs.cat)
				);
				break;
			case DOG:
				name = CommunicationManager.format(
					new ColoredMessage(LanguageManager.messages.eggName),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
					new ColoredMessage(LanguageManager.mobs.dog)
				);
				break;
			case HORSE:
				name = CommunicationManager.format(
					new ColoredMessage(LanguageManager.messages.eggName),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
					new ColoredMessage(LanguageManager.mobs.horse)
				);
				break;
			case IRON_GOLEM:
				name = CommunicationManager.format(
					new ColoredMessage(LanguageManager.messages.eggName),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
					new ColoredMessage(LanguageManager.mobs.ironGolem)
				);
				break;
			case SNOW_GOLEM:
				name = CommunicationManager.format(
					new ColoredMessage(LanguageManager.messages.eggName),
					new ColoredMessage(ChatColor.AQUA, Integer.toString(level)),
					new ColoredMessage(LanguageManager.mobs.snowGolem)
				);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (type) {
			case CAT:
				description = LanguageManager.mobLore.cat;
				break;
			case DOG:
				description = LanguageManager.mobLore.dog;
				break;
			case HORSE:
				description = LanguageManager.mobLore.horse;
				break;
			case IRON_GOLEM:
				description = LanguageManager.mobLore.ironGolem;
				break;
			case SNOW_GOLEM:
				description = LanguageManager.mobLore.snowGolem;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder.addDescription(description);

		// Add space in lore from name
		loreBuilder.addSpace();

		// Set health
		int prevHealth, currHealth;
		switch (type) {
			case CAT:
				prevHealth = VDCat.getHealth(level - 1);
				currHealth = VDCat.getHealth(level);
				break;
			case DOG:
				prevHealth = VDDog.getHealth(level - 1);
				currHealth = VDDog.getHealth(level);
				break;
			case HORSE:
				prevHealth = VDHorse.getHealth(level - 1);
				currHealth = VDHorse.getHealth(level);
				break;
			case IRON_GOLEM:
				prevHealth = VDIronGolem.getHealth(level - 1);
				currHealth = VDIronGolem.getHealth(level);
				break;
			case SNOW_GOLEM:
				prevHealth = VDSnowGolem.getHealth(level - 1);
				currHealth = VDSnowGolem.getHealth(level);
				break;
			default:
				prevHealth = currHealth = 0;
		}
		loreBuilder.addHealthIndicator(prevHealth, currHealth);

		// Set armor
		int prevArmor, currArmor;
		switch (type) {
			case CAT:
				prevArmor = VDCat.getArmor(level - 1);
				currArmor = VDCat.getArmor(level);
				break;
			case DOG:
				prevArmor = VDDog.getArmor(level - 1);
				currArmor = VDDog.getArmor(level);
				break;
			case HORSE:
				prevArmor = VDHorse.getArmor(level - 1);
				currArmor = VDHorse.getArmor(level);
				break;
			case IRON_GOLEM:
				prevArmor = VDIronGolem.getArmor(level - 1);
				currArmor = VDIronGolem.getArmor(level);
				break;
			default:
				prevArmor = currArmor = 0;
		}
		loreBuilder.addArmorIndicator(prevArmor, currArmor);

		// Set toughness
		int prevToughness, currToughness;
		switch (type) {
			case CAT:
				prevToughness = (int) (VDCat.getToughness(level - 1) * 100);
				currToughness = (int) (VDCat.getToughness(level) * 100);
				break;
			case DOG:
				prevToughness = (int) (VDDog.getToughness(level - 1) * 100);
				currToughness = (int) (VDDog.getToughness(level) * 100);
				break;
			case HORSE:
				prevToughness = (int) (VDHorse.getToughness(level - 1) * 100);
				currToughness = (int) (VDHorse.getToughness(level) * 100);
				break;
			case SNOW_GOLEM:
				prevToughness = (int) (VDSnowGolem.getToughness(level - 1) * 100);
				currToughness = (int) (VDSnowGolem.getToughness(level) * 100);
				break;
			default:
				prevToughness = currToughness = 0;
		}
		loreBuilder.addToughnessIndicator(prevToughness, currToughness);

		// Set attack
		int prevAttackLow, prevAttackHigh, currAttackLow, currAttackHigh;
		switch (type) {
			case DOG:
				prevAttackLow = (int) (VDDog.getDamage(level - 1) * .9);
				prevAttackHigh = (int) (VDDog.getDamage(level - 1) * 1.1);
				currAttackLow = (int) (VDDog.getDamage(level) * .9);
				currAttackHigh = (int) (VDDog.getDamage(level) * 1.1);
				break;
			case HORSE:
				prevAttackLow = (int) (VDHorse.getDamage(level - 1) * .8);
				prevAttackHigh = (int) (VDHorse.getDamage(level - 1) * 1.2);
				currAttackLow = (int) (VDHorse.getDamage(level) * .8);
				currAttackHigh = (int) (VDHorse.getDamage(level) * 1.2);
				break;
			case IRON_GOLEM:
				prevAttackLow = (int) (VDIronGolem.getDamage(level - 1) * .8);
				prevAttackHigh = (int) (VDIronGolem.getDamage(level - 1) * 1.2);
				currAttackLow = (int) (VDIronGolem.getDamage(level) * .8);
				currAttackHigh = (int) (VDIronGolem.getDamage(level) * 1.2);
				break;
			case SNOW_GOLEM:
				prevAttackLow = (int) (VDSnowGolem.getDamage(level - 1) * .8);
				prevAttackHigh = (int) (VDSnowGolem.getDamage(level - 1) * 1.2);
				currAttackLow = (int) (VDSnowGolem.getDamage(level) * .8);
				currAttackHigh = (int) (VDSnowGolem.getDamage(level) * 1.2);
				break;
			default:
				prevAttackLow = prevAttackHigh = currAttackLow = currAttackHigh = 0;
		}
		loreBuilder.addAttackIndicator(prevAttackLow, prevAttackHigh, currAttackLow, currAttackHigh);

		// Set effect
		switch (type) {
			case CAT:
				String heal;
				int prevHeal = VDCat.getHeal(level - 1);
				int currHeal = VDCat.getHeal(level);
				if (level > 1)
					heal = "+" + prevHeal + Constants.HP + Constants.UPGRADE + "+" + currHeal + Constants.HP;
				else heal = "+" + currHeal + Constants.HP;
				loreBuilder.addEffect(LanguageManager.messages.catEffect, heal, "10");
				break;
			case HORSE:
				String damageBoost;
				int prevDamageBoost = (int) (VDHorse.getDamageBoost(level - 1) * 100);
				int currDamageBoost = (int) (VDHorse.getDamageBoost(level) * 100);
				if (level > 1)
					damageBoost = "+" + prevDamageBoost + "%" + Constants.UPGRADE + "+" + currDamageBoost + "%";
				else damageBoost = "+" + currDamageBoost + "%";
				loreBuilder.addEffect(LanguageManager.messages.horseEffect, damageBoost);
				break;
		}

		// Set price
		int price;
		switch (type) {
			case CAT:
				switch (level) {
					case 1:
						price = 350;
						break;
					case 2:
						price = 450;
						break;
					case 3:
						price = 600;
						break;
					case 4:
						price = 800;
						break;
					case 5:
						price = 1000;
						break;
					default:
						price = -1;
				}
				break;
			case DOG:
				switch (level) {
					case 1:
						price = 250;
						break;
					case 2:
						price = 350;
						break;
					case 3:
						price = 500;
						break;
					case 4:
						price = 750;
						break;
					case 5:
						price = 1000;
						break;
					default:
						price = -1;
				}
				break;
			case HORSE:
				switch (level) {
					case 1:
						price = 650;
						break;
					case 2:
						price = 800;
						break;
					case 3:
						price = 1000;
						break;
					case 4:
						price = 1250;
						break;
					default:
						price = -1;
				}
				break;
			case IRON_GOLEM:
				switch (level) {
					case 1:
						price = 1000;
						break;
					case 2:
						price = 1250;
						break;
					case 3:
						price = 1600;
						break;
					case 4:
						price = 2000;
						break;
					default:
						price = -1;
				}
				break;
			case SNOW_GOLEM:
				switch (level) {
					case 1:
						price = 1100;
						break;
					case 2:
						price = 1300;
						break;
					case 3:
						price = 1600;
						break;
					case 4:
						price = 2000;
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

		return new ItemStackBuilder(mat, name)
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
		return EGG.equals(value);
	}

	public enum EggType {
		CAT,
		DOG,
		HORSE,
		IRON_GOLEM,
		SNOW_GOLEM
	}
}
