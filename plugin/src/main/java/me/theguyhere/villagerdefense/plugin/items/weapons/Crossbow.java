package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
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

public abstract class Crossbow extends VDWeapon {
	private static final String CROSSBOW = "crossbow";

	@NotNull
	public static ItemStack create(Tier tier) {
		List<String> lores = new ArrayList<>();
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
			lores.addAll(CommunicationManager.formatDescriptionList(
				ChatColor.GRAY, description, Constants.LORE_CHAR_LIMIT));

		// Add space in lore from name
		lores.add("");

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, IndividualAttackType.NORMAL.toString());
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

		// Set range damage
		int damageLow, damageHigh;
		switch (tier) {
			case T1:
				damageLow = 75;
				damageHigh = 120;
				break;
			case T2:
				damageLow = 90;
				damageHigh = 155;
				break;
			case T3:
				damageLow = 110;
				damageHigh = 200;
				break;
			case T4:
				damageLow = 150;
				damageHigh = 225;
				break;
			case T5:
				damageLow = 180;
				damageHigh = 290;
				break;
			case T6:
				damageLow = 200;
				damageHigh = 350;
				break;
			default:
				damageLow = damageHigh = 0;
		}
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.RANGE.straight(), damageLow);
			lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(
				ChatColor.DARK_AQUA,
				Integer.toString(damageLow)
			)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.RANGE.low(), damageLow);
			persistentData.put(VDPlayer.AttackClass.RANGE.high(), damageHigh);
			lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(
				ChatColor.DARK_AQUA,
				damageLow + "-" + damageHigh
			)));
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
		lores.add(CommunicationManager.format(PIERCE, new ColoredMessage(
			ChatColor.GOLD,
			Integer.toString(pierce)
		)));

		// Set attack speed
		persistentData2.put(ATTACK_SPEED_KEY, 0.5);
		lores.add(CommunicationManager.format(SPEED, Double.toString(0.5)));

		// Set ammo cost
		persistentData.put(AMMO_COST_KEY, 3);
		lores.add(CommunicationManager.format(AMMO_COST, new ColoredMessage(ChatColor.RED, Integer.toString(3))));

		// Set durability
		int durability;
		switch (tier) {
			case T1:
				durability = 70;
				break;
			case T2:
				durability = 105;
				break;
			case T3:
				durability = 155;
				break;
			case T4:
				durability = 190;
				break;
			case T5:
				durability = 240;
				break;
			case T6:
				durability = 280;
				break;
			default:
				durability = 0;
		}
		persistentData.put(MAX_DURABILITY_KEY, durability);
		persistentData.put(DURABILITY_KEY, durability);
		lores.add(CommunicationManager.format(
			DURABILITY,
			new ColoredMessage(ChatColor.GREEN, Integer.toString(durability)).toString() +
				new ColoredMessage(ChatColor.WHITE, " / " + durability)
		));

		// Set price
		int price;
		switch (tier) {
			case T1:
			case T2:
			case T3:
			case T4:
			case T5:
			case T6:
				price =
					Calculator.roundToNearest(
						Math.pow(durability, 0.5) * Math.log(pierce * 10) * (damageHigh + damageLow) / 2 / 7.5, 5);
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
		ItemStack item = new ItemStackBuilder(Material.CROSSBOW, name)
			.setLores(lores.toArray(new String[0]))
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
		return CROSSBOW.equals(value);
	}
}
