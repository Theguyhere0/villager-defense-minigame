package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Crossbow extends VDWeapon {
	private static final String CROSSBOW = "crossbow";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, CROSSBOW);
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
				name = formatName(LanguageManager.itemLore.crossbows.t1.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.crossbows.t2.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.crossbows.t3.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.crossbows.t4.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.crossbows.t5.name, tier);
				break;
			case T6:
				name = formatName(LanguageManager.itemLore.crossbows.t6.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (tier) {
			case T1:
				description = LanguageManager.itemLore.crossbows.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.crossbows.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.crossbows.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.crossbows.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.crossbows.t5.description;
				break;
			case T6:
				description = LanguageManager.itemLore.crossbows.t6.description;
				break;
			default:
				description = "";
		}
		if (!description.isEmpty())
			loreBuilder
				.addDescription(description)
				.addSpace();

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, Attacker.AttackType.CRUSHING.toString());
		loreBuilder.addNormalAttackType();

		// Set range damage
		int damageLow, damageHigh;
		switch (tier) {
			case T1:
				damageLow = 97;
				damageHigh = 153;
				break;
			case T2:
				damageLow = 124;
				damageHigh = 196;
				break;
			case T3:
				damageLow = 111;
				damageHigh = 175;
				break;
			case T4:
				damageLow = 135;
				damageHigh = 215;
				break;
			case T5:
				damageLow = 147;
				damageHigh = 232;
				break;
			case T6:
				damageLow = 167;
				damageHigh = 264;
				break;
			default:
				damageLow = damageHigh = 0;
		}
		if (damageLow == damageHigh) {
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.straight(), damageLow);
			loreBuilder.addRangeDamage(Integer.toString(damageLow), Integer.toString(damageLow), true);
		}
		else {
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.low(), damageLow);
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.high(), damageHigh);
			loreBuilder.addRangeDamage(damageLow + "-" + damageHigh, damageLow + "-" + damageHigh, true);
		}

		// Set pierce
		int pierce;
		switch (tier) {
			case T1:
			case T2:
				pierce = 1;
				break;
			case T3:
			case T4:
				pierce = 2;
				break;
			case T5:
				pierce = 3;
				break;
			case T6:
				pierce = 4;
				break;
			default:
				pierce = 0;
		}
		persistentData.put(PIERCE_KEY, pierce);
		loreBuilder.addPierce(pierce);

		// Set attack speed
		persistentData2.put(ATTACK_SPEED_KEY, 0.85);
		loreBuilder.addAttackSpeed(0.85);

		// Set price
		int price;
		switch (tier) {
			case T1:
				price = 535;
				break;
			case T2:
				price = 905;
				break;
			case T3:
				price = 1375;
				break;
			case T4:
				price = 1950;
				break;
			case T5:
				price = 2670;
				break;
			case T6:
				price = 3525;
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
		ItemStack item = new ItemStackBuilder(Material.CROSSBOW, name)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setGlowingIfTrue(enchant)
			.setPersistentData(persistentData)
			.setPersistentData2(persistentData2)
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
		return CROSSBOW.equals(value);
	}
}
