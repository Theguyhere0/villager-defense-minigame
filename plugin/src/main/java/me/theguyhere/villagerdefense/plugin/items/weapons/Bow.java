package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Bow extends VDWeapon {
	private static final String BOW = "bow";

	@NotNull
	public static ItemStack create(Tier tier) {
		List<String> lores = new ArrayList<>();
		HashMap<Enchantment, Integer> enchant = new HashMap<>();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, BOW);

		// Possibly set enchant
		switch (tier) {
			case T5:
			case T6:
				enchant.put(Enchantment.DURABILITY, 3);
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
			lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(
				ChatColor.DARK_AQUA,
				CommunicationManager.format(LanguageManager.messages.perBlock, Integer.toString(damageLow))
			)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.RANGE.low(), damageLow);
			persistentData.put(VDPlayer.AttackClass.RANGE.high(), damageHigh);
			lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(
				ChatColor.DARK_AQUA,
				String.format(LanguageManager.messages.perBlock, damageLow + "-" + damageHigh)
			)));
		}

		// Add tag for per-block damage
		persistentTags.put(PER_BLOCK_KEY, "");

		// Set attack speed
		persistentData2.put(ATTACK_SPEED_KEY, 1d);
		lores.add(CommunicationManager.format(SPEED, Double.toString(1)));

		// Set ammo cost
		persistentData.put(AMMO_COST_KEY, 2);
		lores.add(CommunicationManager.format(AMMO_COST, new ColoredMessage(ChatColor.RED, Integer.toString(2))));

		// Set durability
		int durability;
		switch (tier) {
			case T1:
				durability = 120;
				break;
			case T2:
				durability = 175;
				break;
			case T3:
				durability = 250;
				break;
			case T4:
				durability = 320;
				break;
			case T5:
				durability = 460;
				break;
			case T6:
				durability = 660;
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
				price = 225;
				break;
			case T2:
				price = 335;
				break;
			case T3:
				price = 490;
				break;
			case T4:
				price = 610;
				break;
			case T5:
				price = 880;
				break;
			case T6:
				price = 1160;
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
		ItemStack item = ItemFactory.createItem(Material.BOW, name, ItemFactory.BUTTON_FLAGS, enchant, lores, null,
			persistentData, persistentData2, persistentTags
		);
		if (durability == 0)
			return ItemFactory.makeUnbreakable(item);
		else return item;
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
		return BOW.equals(value);
	}
}
