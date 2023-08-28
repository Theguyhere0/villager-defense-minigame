package me.theguyhere.villagerdefense.plugin.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Sword extends VDWeapon {
	private static final String SWORD = "sword";

	@NotNull
	public static ItemStack create(Tier tier, SwordType type) {
		LoreBuilder loreBuilder = new LoreBuilder();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, SWORD);
		boolean enchant = false;

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
						enchant = true;
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
			loreBuilder
				.addDescription(description)
				.addSpace();

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, IndividualAttackType.NORMAL.toString());
		loreBuilder.addNormalAttackType();

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
			loreBuilder.addMainDamage(Integer.toString(damageLow), Integer.toString(damageLow));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.MAIN.low(), damageLow);
			persistentData.put(VDPlayer.AttackClass.MAIN.high(), damageHigh);
			loreBuilder.addMainDamage(damageLow + "-" + damageHigh, damageLow + "-" + damageHigh);
		}

		// Set crit damage
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.CRITICAL.straight(), (int) (damageLow * 1.5));
			loreBuilder.addCriticalDamage(Integer.toString((int) (damageLow * 1.5)),
				Integer.toString((int) (damageLow * 1.5)));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.CRITICAL.low(), (int) (damageLow * 1.5));
			persistentData.put(VDPlayer.AttackClass.CRITICAL.high(), (int) (damageHigh * 1.25));
			loreBuilder.addCriticalDamage((int) (damageLow * 1.5) + "-" + (int) (damageHigh * 1.25),
				(int) (damageLow * 1.5) + "-" + (int) (damageHigh * 1.25));
		}

		// Set sweep damage
		if (damageLow == damageHigh) {
			persistentData.put(VDPlayer.AttackClass.SWEEP.straight(), damageLow / 2);
			loreBuilder.addSweepDamage(Integer.toString(damageLow / 2), Integer.toString(damageLow / 2));
		}
		else {
			persistentData.put(VDPlayer.AttackClass.SWEEP.low(), damageLow / 2);
			persistentData.put(VDPlayer.AttackClass.SWEEP.high(), (damageHigh - 5) / 2);
			loreBuilder.addSweepDamage((damageLow / 2) + "-" + ((damageHigh - 5) / 2),
				(damageLow / 2) + "-" + ((damageHigh - 5) / 2));
		}

		// Set attack speed
		attributes.put(
			Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(VDItem.MetaKey.ATTACK_SPEED.name(), -2.5,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);
		persistentData2.put(ATTACK_SPEED_KEY, 1.5);
		loreBuilder.addAttackSpeed(1.5);

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
						durability = 780;
						break;
					case T6:
						durability = 900;
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
		loreBuilder.addDurability(durability);

		// Set price
		int price;
		switch (type) {
			case TIERED:
				switch (tier) {
					case T1:
						price = 210;
						break;
					case T2:
						price = 450;
						break;
					case T3:
						price = 790;
						break;
					case T4:
						price = 1190;
						break;
					case T5:
						price = 1590;
						break;
					case T6:
						price = 2135;
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

		// Create item
		ItemStack item = new ItemStackBuilder(mat, name)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setGlowingIfTrue(enchant)
			.setAttributes(attributes)
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
		return SWORD.equals(value);
	}

	public enum SwordType {
		SOLDIER,
		TIERED,
	}
}
