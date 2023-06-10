package me.theguyhere.villagerdefense.plugin.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
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

public abstract class Sword extends VDWeapon {
	private static final String SWORD = "sword";

	@NotNull
	public static ItemStack create(Tier tier, SwordType type) {
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<Enchantment, Integer> enchant = new HashMap<>();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, SWORD);

		// Set material
		Material mat;
		switch (type) {
			case SOLDIER:
				mat = Material.WOODEN_SWORD;
				break;
			case TIERED:
				switch (tier) {
					case T0:
					case T1:
						mat = Material.WOODEN_SWORD;
						break;
					case T2:
						mat = Material.STONE_SWORD;
						break;
					case T3:
						mat = Material.IRON_SWORD;
						break;
					case T4:
						mat = Material.DIAMOND_SWORD;
						break;
					case T5:
						mat = Material.NETHERITE_SWORD;
						break;
					case T6:
						mat = Material.NETHERITE_SWORD;
						enchant.put(Enchantment.DURABILITY, 3);
						break;
					default:
						mat = Material.GOLDEN_SWORD;
				}
				break;
			default:
				mat = Material.GOLDEN_SWORD;
		}

		// Set name
		String name;
		switch (type) {
			case SOLDIER:
				name = formatName(ChatColor.GREEN, LanguageManager.kits.soldier.items.sword, tier);
				break;
			case TIERED:
				switch (tier) {
					case T0:
						name = formatName(LanguageManager.itemLore.swords.starter.name, tier);
						break;
					case T1:
						name = formatName(LanguageManager.itemLore.swords.t1.name, tier);
						break;
					case T2:
						name = formatName(LanguageManager.itemLore.swords.t2.name, tier);
						break;
					case T3:
						name = formatName(LanguageManager.itemLore.swords.t3.name, tier);
						break;
					case T4:
						name = formatName(LanguageManager.itemLore.swords.t4.name, tier);
						break;
					case T5:
						name = formatName(LanguageManager.itemLore.swords.t5.name, tier);
						break;
					case T6:
						name = formatName(LanguageManager.itemLore.swords.t6.name, tier);
						break;
					default:
						name = "";
				}
				break;
			default:
				name = "";
		}

		// Set description
		String description;
		switch (type) {
			case SOLDIER:
				description = LanguageManager.kits.soldier.items.swordDesc;
				break;
			case TIERED:
				switch (tier) {
					case T0:
						description = LanguageManager.itemLore.swords.starter.description;
						break;
					case T1:
						description = LanguageManager.itemLore.swords.t1.description;
						break;
					case T2:
						description = LanguageManager.itemLore.swords.t2.description;
						break;
					case T3:
						description = LanguageManager.itemLore.swords.t3.description;
						break;
					case T4:
						description = LanguageManager.itemLore.swords.t4.description;
						break;
					case T5:
						description = LanguageManager.itemLore.swords.t5.description;
						break;
					case T6:
						description = LanguageManager.itemLore.swords.t6.description;
						break;
					default:
						description = "";
				}
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

		// Set main damage
		int damageLow, damageHigh;
		switch (type) {
			case SOLDIER:
				damageLow = damageHigh = 40;
				break;
			case TIERED:
				switch (tier) {
					case T0:
						damageLow = damageHigh = 30;
						break;
					case T1:
						damageLow = 35;
						damageHigh = 50;
						break;
					case T2:
						damageLow = 45;
						damageHigh = 70;
						break;
					case T3:
						damageLow = 65;
						damageHigh = 85;
						break;
					case T4:
						damageLow = 85;
						damageHigh = 100;
						break;
					case T5:
						damageLow = 95;
						damageHigh = 120;
						break;
					case T6:
						damageLow = 110;
						damageHigh = 150;
						break;
					default:
						damageLow = damageHigh = 0;
				}
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
			persistentData.put(VDPlayer.AttackClass.CRITICAL.low(), (int) (damageLow * 1.5));
			persistentData.put(VDPlayer.AttackClass.CRITICAL.high(), (int) (damageHigh * 1.25));
			lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(
				ChatColor.DARK_PURPLE,
				(int) (damageLow * 1.5) + "-" + (int) (damageHigh * 1.25)
			)));
		}

		// Set sweep damage
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.SWEEP.straight(), damageLow / 2);
			lores.add(CommunicationManager.format(SWEEP_DAMAGE, new ColoredMessage(
				ChatColor.LIGHT_PURPLE,
				Integer.toString(damageLow / 2)
			)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.SWEEP.low(), damageLow / 2);
			persistentData.put(VDPlayer.AttackClass.SWEEP.high(), (damageHigh - 5) / 2);
			lores.add(CommunicationManager.format(SWEEP_DAMAGE, new ColoredMessage(
				ChatColor.LIGHT_PURPLE,
				(damageLow / 2) + "-" + ((damageHigh - 5) / 2)
			)));
		}

		// Set attack speed
		attributes.put(
			Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(VDItem.MetaKey.ATTACK_SPEED.name(), -2.5,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);
		persistentData2.put(ATTACK_SPEED_KEY, 1.5);
		lores.add(CommunicationManager.format(SPEED, Double.toString(1.5)));

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
		switch (type) {
			case TIERED:
				switch (tier) {
					case T1:
						durability = 200;
						break;
					case T2:
						durability = 375;
						break;
					case T3:
						durability = 550;
						break;
					case T4:
						durability = 725;
						break;
					case T5:
						durability = 875;
						break;
					case T6:
						durability = 1000;
						break;
					default:
						durability = 0;
				}
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
		switch (type) {
			case TIERED:
				switch (tier) {
					case T1:
						price = 175;
						break;
					case T2:
						price = 260;
						break;
					case T3:
						price = 380;
						break;
					case T4:
						price = 475;
						break;
					case T5:
						price = 650;
						break;
					case T6:
						price = 925;
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
		String value = meta.getPersistentDataContainer().get(ITEM_TYPE_KEY, PersistentDataType.STRING);
		if (value == null)
			return false;
		return SWORD.equals(value);
	}

	public enum SwordType {
		SOLDIER,
		TIERED,
	}
}
