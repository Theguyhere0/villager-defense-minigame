package me.theguyhere.villagerdefense.plugin.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class Axe extends VDWeapon {
	private static final String AXE = "axe";

	@NotNull
	public static ItemStack create(Tier tier) {
		LoreBuilder loreBuilder = new LoreBuilder();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, AXE);
		boolean enchant = false;

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
				enchant = true;
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
		loreBuilder
			.addDescription(description)
			.addSpace();

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, IndividualAttackType.CRUSHING.toString());
		loreBuilder.addCrushingAttackType();

		// Set main damage
		int damageLow, damageHigh;
		switch (tier) {
			case T1:
				damageLow = 62;
				damageHigh = 115;
				break;
			case T2:
				damageLow = 82;
				damageHigh = 145;
				break;
			case T3:
				damageLow = 106;
				damageHigh = 178;
				break;
			case T4:
				damageLow = 134;
				damageHigh = 216;
				break;
			case T5:
				damageLow = 164;
				damageHigh = 257;
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
			persistentData.put(VDPlayer.AttackClass.CRITICAL.low(), (int) (damageLow * 1.25));
			persistentData.put(VDPlayer.AttackClass.CRITICAL.high(), (int) (damageHigh * 1.5));
			loreBuilder.addCriticalDamage((int) (damageLow * 1.25) + "-" + (int) (damageHigh * 1.5),
				(int) (damageLow * 1.25) + "-" + (int) (damageHigh * 1.5));
		}

		// Set attack speed
		attributes.put(
			Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(VDItem.MetaKey.ATTACK_SPEED.name(), -3.2,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);
		persistentData2.put(ATTACK_SPEED_KEY, 0.8);
		loreBuilder.addAttackSpeed(0.8);

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
				durability = 150;
				break;
			case T2:
				durability = 225;
				break;
			case T3:
				durability = 315;
				break;
			case T4:
				durability = 410;
				break;
			case T5:
				durability = 490;
				break;
			case T6:
				durability = 540;
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
				price = 300;
				break;
			case T2:
				price = 515;
				break;
			case T3:
				price = 830;
				break;
			case T4:
				price = 1245;
				break;
			case T5:
				price = 1710;
				break;
			case T6:
				price = 2180;
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
		return AXE.equals(value);
	}
}
