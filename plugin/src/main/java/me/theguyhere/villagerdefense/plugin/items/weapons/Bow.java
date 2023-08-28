package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Bow extends VDWeapon {
	private static final String BOW = "bow";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, BOW);
		boolean enchant = false;

		// Possibly set enchant
		switch (tier) {
			case T5:
			case T6:
				enchant = true;
		}

		// Set name
		String name;
		switch (tier) {
			case T1:
				name = formatName(LanguageManager.itemLore.bows.t1.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.bows.t2.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.bows.t3.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.bows.t4.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.bows.t5.name, tier);
				break;
			case T6:
				name = formatName(LanguageManager.itemLore.bows.t6.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (tier) {
			case T1:
				description = LanguageManager.itemLore.bows.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.bows.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.bows.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.bows.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.bows.t5.description;
				break;
			case T6:
				description = LanguageManager.itemLore.bows.t6.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder
				.addDescription(description)
				.addSpace();

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, IndividualAttackType.NORMAL.toString());
		loreBuilder.addNormalAttackType();

		// Set range damage
		int damageLow, damageHigh;
		switch (tier) {
			case T1:
				damageLow = 6;
				damageHigh = 12;
				break;
			case T2:
				damageLow = 8;
				damageHigh = 15;
				break;
			case T3:
				damageLow = 12;
				damageHigh = 18;
				break;
			case T4:
				damageLow = 14;
				damageHigh = 23;
				break;
			case T5:
				damageLow = 17;
				damageHigh = 28;
				break;
			case T6:
				damageLow = 20;
				damageHigh = 32;
				break;
			default:
				damageLow = damageHigh = 0;
		}
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.RANGE.straight(), damageLow);
			loreBuilder.addRangeDamage(Integer.toString(damageLow), Integer.toString(damageLow), true);
		}
		else {
			persistentData.put(VDPlayer.AttackClass.RANGE.low(), damageLow);
			persistentData.put(VDPlayer.AttackClass.RANGE.high(), damageHigh);
			loreBuilder.addRangeDamage(damageLow + "-" + damageHigh, damageLow + "-" + damageHigh, true);
		}

		// Add tag for per-block damage
		persistentTags.put(PER_BLOCK_KEY, "");

		// Set attack speed
		persistentData2.put(ATTACK_SPEED_KEY, 0.65);
		loreBuilder.addAttackSpeed(0.65);

		// Set ammo cost
		persistentData.put(AMMO_COST_KEY, 2);
		loreBuilder.addAmmoCost(2);

		// Set durability
		int durability;
		switch (tier) {
			case T1:
				durability = 60;
				break;
			case T2:
				durability = 90;
				break;
			case T3:
				durability = 125;
				break;
			case T4:
				durability = 160;
				break;
			case T5:
				durability = 200;
				break;
			case T6:
				durability = 225;
				break;
			default:
				durability = 0;
		}
		persistentData.put(MAX_DURABILITY_KEY, durability);
		persistentData.put(DURABILITY_KEY, durability);
		loreBuilder.addDurability(durability);

		// Set price
		int price;
		switch (tier) {
			case T1:
				price = 215;
				break;
			case T2:
				price = 420;
				break;
			case T3:
				price = 785;
				break;
			case T4:
				price = 1275;
				break;
			case T5:
				price = 1990;
				break;
			case T6:
				price = 2650;
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
		ItemStack item = new ItemStackBuilder(Material.BOW, name)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setGlowingIfTrue(enchant)
			.setPersistentData(persistentData)
			.setPersistentData2(persistentData2)
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
		return BOW.equals(value);
	}
}
