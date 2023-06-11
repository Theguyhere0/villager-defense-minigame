package me.theguyhere.villagerdefense.plugin.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemFactory;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Axe extends VDWeapon {
	private static final String AXE = "axe";

	@NotNull
	public static ItemStack create(Tier tier) {
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<Enchantment, Integer> enchant = new HashMap<>();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, AXE);

		// Set material
		Material mat;
		switch (tier) {
			case T1:
				mat = Material.WOODEN_AXE;
				break;
			case T2:
				mat = Material.STONE_AXE;
				break;
			case T3:
				mat = Material.IRON_AXE;
				break;
			case T4:
				mat = Material.DIAMOND_AXE;
				break;
			case T5:
				mat = Material.NETHERITE_AXE;
				break;
			case T6:
				mat = Material.NETHERITE_AXE;
				enchant.put(Enchantment.DURABILITY, 3);
				break;
			default:
				mat = Material.GOLDEN_AXE;
		}

		// Set name
		String name;
		switch (tier) {
			case T1:
				name = formatName(LanguageManager.itemLore.axes.t1.name, tier);
				break;
			case T2:
				name = formatName(LanguageManager.itemLore.axes.t2.name, tier);
				break;
			case T3:
				name = formatName(LanguageManager.itemLore.axes.t3.name, tier);
				break;
			case T4:
				name = formatName(LanguageManager.itemLore.axes.t4.name, tier);
				break;
			case T5:
				name = formatName(LanguageManager.itemLore.axes.t5.name, tier);
				break;
			case T6:
				name = formatName(LanguageManager.itemLore.axes.t6.name, tier);
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (tier) {
			case T1:
				description = LanguageManager.itemLore.axes.t1.description;
				break;
			case T2:
				description = LanguageManager.itemLore.axes.t2.description;
				break;
			case T3:
				description = LanguageManager.itemLore.axes.t3.description;
				break;
			case T4:
				description = LanguageManager.itemLore.axes.t4.description;
				break;
			case T5:
				description = LanguageManager.itemLore.axes.t5.description;
				break;
			case T6:
				description = LanguageManager.itemLore.axes.t6.description;
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
		persistentTags.put(ATTACK_TYPE_KEY, IndividualAttackType.CRUSHING.toString());
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_CRUSHING));

		// Set main damage
		int damageLow, damageHigh;
		switch (tier) {
			case T1:
				damageLow = 50;
				damageHigh = 90;
				break;
			case T2:
				damageLow = 75;
				damageHigh = 120;
				break;
			case T3:
				damageLow = 100;
				damageHigh = 165;
				break;
			case T4:
				damageLow = 130;
				damageHigh = 200;
				break;
			case T5:
				damageLow = 165;
				damageHigh = 240;
				break;
			case T6:
				damageLow = 200;
				damageHigh = 300;
				break;
			default:
				damageLow = damageHigh = 0;
		}
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.MAIN.straight(), damageLow);
			lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(
				ChatColor.RED,
				Integer.toString(damageLow)
			)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.MAIN.low(), damageLow);
			persistentData.put(VDPlayer.AttackClass.MAIN.high(), damageHigh);
			lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(
				ChatColor.RED,
				damageLow + "-" + damageHigh
			)));
		}

		// Set crit damage
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.CRITICAL.straight(), (int) (damageLow * 1.5));
			lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(
				ChatColor.DARK_PURPLE,
				Integer.toString((int) (damageLow * 1.5))
			)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.CRITICAL.low(), (int) (damageLow * 1.25));
			persistentData.put(VDPlayer.AttackClass.CRITICAL.high(), (int) (damageHigh * 1.5));
			lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(
				ChatColor.DARK_PURPLE,
				(int) (damageLow * 1.25) + "-" + (int) (damageHigh * 1.5)
			)));
		}

		// Set attack speed
		attributes.put(
			Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(VDItem.MetaKey.ATTACK_SPEED.name(), -3.2,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);
		persistentData2.put(ATTACK_SPEED_KEY, 0.8);
		lores.add(CommunicationManager.format(SPEED, Double.toString(0.8)));

		// Set dummy damage
		attributes.put(
			Attribute.GENERIC_ATTACK_DAMAGE,
			new AttributeModifier(VDItem.MetaKey.DUMMY.name(), 0,
				AttributeModifier.Operation.MULTIPLY_SCALAR_1
			)
		);
		attributes.put(
			Attribute.GENERIC_ATTACK_DAMAGE,
			new AttributeModifier(VDItem.MetaKey.DUMMY.name(), 19,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);

		// Set durability
		int durability;
		switch (tier) {
			case T1:
				durability = 180;
				break;
			case T2:
				durability = 335;
				break;
			case T3:
				durability = 495;
				break;
			case T4:
				durability = 650;
				break;
			case T5:
				durability = 785;
				break;
			case T6:
				durability = 900;
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
					Calculator.roundToNearest(Math.pow(durability, 0.75) * (damageHigh + damageLow) / 2 / 17, 5);
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
		ItemStack item = ItemFactory.createItem(mat, name, ItemFactory.BUTTON_FLAGS, enchant, lores, attributes,
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
		String value = meta
			.getPersistentDataContainer()
			.get(ITEM_TYPE_KEY, PersistentDataType.STRING);
		if (value == null)
			return false;
		return AXE.equals(value);
	}
}
