package me.theguyhere.villagerdefense.plugin.items.eggs;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDIronGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.golems.VDSnowGolem;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDCat;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDDog;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.pets.VDHorse;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
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

public abstract class VDEgg extends VDItem {
	private static final String EGG = "egg";

	@NotNull
	public static ItemStack create(int level, EggType type) {
		List<String> lores = new ArrayList<>();
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
			lores.addAll(CommunicationManager.formatDescriptionList(
				ChatColor.GRAY, description, Constants.LORE_CHAR_LIMIT));

		// Add space in lore from name
		lores.add("");

		// Set health
		int[] health = new int[2];
		switch (type) {
			case CAT:
				health[0] = VDCat.getHealth(level - 1);
				health[1] = VDCat.getHealth(level);
				break;
			case DOG:
				health[0] = VDDog.getHealth(level - 1);
				health[1] = VDDog.getHealth(level);
				break;
			case HORSE:
				health[0] = VDHorse.getHealth(level - 1);
				health[1] = VDHorse.getHealth(level);
				break;
			case IRON_GOLEM:
				health[0] = VDIronGolem.getHealth(level - 1);
				health[1] = VDIronGolem.getHealth(level);
				break;
			case SNOW_GOLEM:
				health[0] = VDSnowGolem.getHealth(level - 1);
				health[1] = VDSnowGolem.getHealth(level);
				break;
		}
		if (health[1] > 0) {
			if (level > 1)
				lores.add(new ColoredMessage(ChatColor.RED, Constants.HP + " " + health[0] + Constants.UPGRADE +
					health[1]).toString());
			else lores.add(new ColoredMessage(ChatColor.RED, Constants.HP + " " + health[1]).toString());
		}

		// Set armor
		int[] armor = new int[2];
		switch (type) {
			case CAT:
				armor[0] = VDCat.getArmor(level - 1);
				armor[1] = VDCat.getArmor(level);
				break;
			case DOG:
				armor[0] = VDDog.getArmor(level - 1);
				armor[1] = VDDog.getArmor(level);
				break;
			case HORSE:
				armor[0] = VDHorse.getArmor(level - 1);
				armor[1] = VDHorse.getArmor(level);
				break;
			case IRON_GOLEM:
				armor[0] = VDIronGolem.getArmor(level - 1);
				armor[1] = VDIronGolem.getArmor(level);
				break;
		}
		if (armor[1] > 0) {
			if (level > 1)
				lores.add(new ColoredMessage(ChatColor.AQUA, Constants.ARMOR + " " + armor[0] + Constants.UPGRADE +
					armor[1]).toString());
			else lores.add(new ColoredMessage(ChatColor.AQUA, Constants.ARMOR + " " + armor[1]).toString());
		}

		// Set toughness
		int[] toughness = new int[2];
		switch (type) {
			case CAT:
				toughness[0] = (int) (VDCat.getToughness(level - 1) * 100);
				toughness[1] = (int) (VDCat.getToughness(level) * 100);
				break;
			case DOG:
				toughness[0] = (int) (VDDog.getToughness(level - 1) * 100);
				toughness[1] = (int) (VDDog.getToughness(level) * 100);
				break;
			case HORSE:
				toughness[0] = (int) (VDHorse.getToughness(level - 1) * 100);
				toughness[1] = (int) (VDHorse.getToughness(level) * 100);
				break;
			case SNOW_GOLEM:
				toughness[0] = (int) (VDSnowGolem.getToughness(level - 1) * 100);
				toughness[1] = (int) (VDSnowGolem.getToughness(level) * 100);
				break;
		}
		if (toughness[1] > 0) {
			if (level > 1)
				lores.add(new ColoredMessage(ChatColor.DARK_AQUA, Constants.TOUGH + " " + toughness[0] + "%" +
					Constants.UPGRADE + armor[1] + "%").toString());
			else lores.add(new ColoredMessage(ChatColor.DARK_AQUA, Constants.TOUGH + " " + toughness[1] + "%")
				.toString());
		}

		// Set attack
		int[] attack = new int[4];
		switch (type) {
			case DOG:
				attack[0] = (int) (VDDog.getDamage(level - 1) * .9);
				attack[1] = (int) (VDDog.getDamage(level - 1) * 1.1);
				attack[2] = (int) (VDDog.getDamage(level) * .9);
				attack[3] = (int) (VDDog.getDamage(level) * 1.1);
				break;
			case HORSE:
				attack[0] = (int) (VDHorse.getDamage(level - 1) * .8);
				attack[1] = (int) (VDHorse.getDamage(level - 1) * 1.2);
				attack[2] = (int) (VDHorse.getDamage(level) * .8);
				attack[3] = (int) (VDHorse.getDamage(level) * 1.2);
				break;
			case IRON_GOLEM:
				attack[0] = (int) (VDIronGolem.getDamage(level - 1) * .8);
				attack[1] = (int) (VDIronGolem.getDamage(level - 1) * 1.2);
				attack[2] = (int) (VDIronGolem.getDamage(level) * .8);
				attack[3] = (int) (VDIronGolem.getDamage(level) * 1.2);
				break;
			case SNOW_GOLEM:
				attack[0] = (int) (VDSnowGolem.getDamage(level - 1) * .8);
				attack[1] = (int) (VDSnowGolem.getDamage(level - 1) * 1.2);
				attack[2] = (int) (VDSnowGolem.getDamage(level) * .8);
				attack[3] = (int) (VDSnowGolem.getDamage(level) * 1.2);
				break;
		}
		if (attack[2] > 0) {
			if (level > 1)
				lores.add(new ColoredMessage(ChatColor.GREEN, Constants.DAMAGE + " " + attack[0] + "-" + attack[1] +
					Constants.UPGRADE + attack[2] + "-" + attack[3])
					.toString());
			else lores.add(new ColoredMessage(ChatColor.GREEN, Constants.DAMAGE + " " + attack[2] + "-" + attack[3])
				.toString());
		}


		// Set effect
		String effect;
		int[] effectValue = new int[2];
		switch (type) {
			case CAT:
				effect = new ColoredMessage(ChatColor.BLUE, LanguageManager.messages.effect).toString();
				effectValue[0] = VDCat.getHeal(level - 1);
				effectValue[1] = VDCat.getHeal(level);
				if (level > 1)
					effect = String.format(effect, CommunicationManager.format(
						new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.catEffect),
						new ColoredMessage(ChatColor.RED, "+" + effectValue[0] + Constants.HP + Constants.UPGRADE +
							"+" + effectValue[1] + Constants.HP),
						new ColoredMessage(ChatColor.AQUA, "10")
					));
				else effect = String.format(effect, CommunicationManager.format(
					new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.catEffect),
					new ColoredMessage(ChatColor.RED, "+" + effectValue[1] + Constants.HP),
					new ColoredMessage(ChatColor.AQUA, "10")
				));
				break;
			case HORSE:
				effect = new ColoredMessage(ChatColor.BLUE, LanguageManager.messages.effect).toString();
				effectValue[0] = (int) (VDHorse.getDamageBoost(level - 1) * 100);
				effectValue[1] = (int) (VDHorse.getDamageBoost(level) * 100);
				if (level > 1)
					effect = String.format(effect, CommunicationManager.format(
						new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.horseEffect),
						new ColoredMessage(ChatColor.RED, "+" + effectValue[0] + "%" + Constants.UPGRADE +
							"+" + effectValue[1] + "%")
					));
				else effect = String.format(effect, CommunicationManager.format(
					new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.horseEffect),
					new ColoredMessage(ChatColor.RED, "+" + effectValue[1] + "%")
				));

				break;
			default:
				effect = "";
		}
		if (effectValue[1] > 0)
			lores.add(effect);

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
		if (price >= 0) {
			lores.add("");
			lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));
		}

		return ItemFactory.createItem(mat, name, ItemFactory.BUTTON_FLAGS, null, lores, null,
			persistentData, null, persistentTags
		);
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
