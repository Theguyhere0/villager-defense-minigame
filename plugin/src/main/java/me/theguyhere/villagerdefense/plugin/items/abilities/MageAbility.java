package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import me.theguyhere.villagerdefense.plugin.items.weapons.VDWeapon;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class MageAbility extends VDAbility {
	private static final String MAGE_ABILITY = "mage-ability";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, MAGE_ABILITY);

		// Set name
		String name;
		switch (tier) {
			case T0:
				name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.mage.name, tier);
				break;
			case T1:
				name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.mage.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.mage.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.mage.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.mage.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.mage.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		loreBuilder.addDescription(getDescription(tier));

		// Add space in lore from name, followed by instructions for usage
		loreBuilder
			.addSpace()
			.addDescription(new ColoredMessage(LanguageManager.messages.rightClick).toString());

		// Set effect
		String effect;
		switch (tier) {
			case T0:
			case T1:
			case T2:
			case T3:
			case T4:
			case T5:
				effect = LanguageManager.kits.mage.effect;
				break;
			default:
				effect = null;
		}
		loreBuilder.addEffect(effect);

		// Set attack type
		persistentTags.put(VDWeapon.ATTACK_TYPE_KEY, Attacker.AttackType.NORMAL.toString());
		loreBuilder.addNormalAttackType();

		// Set damage
		int prevDamageLow, prevDamageHigh, currDamageLow, currDamageHigh;
		switch (tier) {
			case T0:
				prevDamageLow = currDamageLow = 75;
				prevDamageHigh = currDamageHigh = 90;
				break;
			case T1:
				prevDamageLow = 75;
				currDamageLow = 90;
				prevDamageHigh = 90;
				currDamageHigh = 115;
				break;
			case T2:
				prevDamageLow = 90;
				currDamageLow = 105;
				prevDamageHigh = 115;
				currDamageHigh = 140;
				break;
			case T3:
				prevDamageLow = 105;
				currDamageLow = 130;
				prevDamageHigh = 140;
				currDamageHigh = 170;
				break;
			case T4:
				prevDamageLow = 130;
				currDamageLow = 150;
				prevDamageHigh = 170;
				currDamageHigh = 200;
				break;
			case T5:
				prevDamageLow = 150;
				currDamageLow = 175;
				prevDamageHigh = 200;
				currDamageHigh = 225;
				break;
			default:
				prevDamageLow = prevDamageHigh = currDamageLow = currDamageHigh = 0;
		}
		String prevDamage, currDamage;
		if (currDamageLow == currDamageHigh) {
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.straight(), currDamageLow);
			currDamage = Integer.toString(currDamageLow);
		}
		else {
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.low(), currDamageLow);
			persistentData.put(LegacyVDPlayer.AttackClass.RANGE.high(), currDamageHigh);
			currDamage = currDamageLow + "-" + currDamageHigh;
		}
		if (prevDamageLow == prevDamageHigh)
			prevDamage = Integer.toString(prevDamageLow);
		else prevDamage = prevDamageLow + "-" + prevDamageHigh;
		loreBuilder.addRangeDamage(prevDamage, currDamage, false);

		// Set cooldown
		double prevCooldown, currCooldown;
		switch (tier) {
			case T0:
				prevCooldown = currCooldown = 12;
				break;
			case T1:
				prevCooldown = 12;
				currCooldown = 11.3;
				break;
			case T2:
				prevCooldown = 11.3;
				currCooldown = 10.4;
				break;
			case T3:
				prevCooldown = 10.4;
				currCooldown = 8.8;
				break;
			case T4:
				prevCooldown = 8.8;
				currCooldown = 6.5;
				break;
			case T5:
				prevCooldown = 6.5;
				currCooldown = 3;
				break;
			default:
				prevCooldown = currCooldown = 0;
		}
		persistentData2.put(COOLDOWN_KEY, currCooldown);
		loreBuilder.addCooldown(prevCooldown, currCooldown);

		// Set price
		int price = getPrice(tier);
		persistentData.put(PRICE_KEY, price);
		if (price >= 0)
			loreBuilder
				.addSpace()
				.addPrice(price);

		// Create item
		return new ItemStackBuilder(
			Material.PURPLE_DYE,
			name
		)
			.setLores(loreBuilder)
			.setHideEnchantFlags()
			.setGlowingIfTrue(true)
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
		return MAGE_ABILITY.equals(value);
	}
}
