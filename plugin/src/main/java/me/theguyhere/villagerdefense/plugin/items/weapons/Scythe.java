package me.theguyhere.villagerdefense.plugin.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import me.theguyhere.villagerdefense.plugin.entities.players.LegacyVDPlayer;
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

public abstract class Scythe extends VDWeapon {
	private static final String SCYTHE = "scythe";

	@NotNull
	public static ItemStack create(Tier tier, ScytheType type) {
		LoreBuilder loreBuilder = new LoreBuilder();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
		HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
		HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, SCYTHE);
		boolean enchant = false;

		// Set material
		Material mat;
		switch (type) {
			case REAPER:
				switch (tier) {
					case T1:
						mat = Material.WOODEN_HOE;
						break;
					case T2:
						mat = Material.STONE_HOE;
						break;
					case T3:
						mat = Material.IRON_HOE;
						break;
					default:
						mat = Material.GOLDEN_HOE;
				}
				break;
			case TIERED:
				switch (tier) {
					case T1:
						mat = Material.WOODEN_HOE;
						break;
					case T2:
						mat = Material.STONE_HOE;
						break;
					case T3:
						mat = Material.IRON_HOE;
						break;
					case T4:
						mat = Material.DIAMOND_HOE;
						break;
					case T5:
						mat = Material.NETHERITE_HOE;
						break;
					case T6:
						mat = Material.NETHERITE_HOE;
						enchant = true;
						break;
					default:
						mat = Material.GOLDEN_HOE;
				}
				break;
			default:
				mat = Material.GOLDEN_HOE;
		}

		// Set name
		String name;
		switch (type) {
			case REAPER:
				name = formatName(ChatColor.GREEN, LanguageManager.kits.reaper.items.scythe, tier);
				break;
			case TIERED:
				switch (tier) {
					case T1:
						name = formatName(LanguageManager.itemLore.scythes.t1.name, tier);
						break;
					case T2:
						name = formatName(LanguageManager.itemLore.scythes.t2.name, tier);
						break;
					case T3:
						name = formatName(LanguageManager.itemLore.scythes.t3.name, tier);
						break;
					case T4:
						name = formatName(LanguageManager.itemLore.scythes.t4.name, tier);
						break;
					case T5:
						name = formatName(LanguageManager.itemLore.scythes.t5.name, tier);
						break;
					case T6:
						name = formatName(LanguageManager.itemLore.scythes.t6.name, tier);
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
			case REAPER:
				description = LanguageManager.kits.reaper.items.scytheDesc;
				break;
			case TIERED:
				switch (tier) {
					case T1:
						description = LanguageManager.itemLore.scythes.t1.description;
						break;
					case T2:
						description = LanguageManager.itemLore.scythes.t2.description;
						break;
					case T3:
						description = LanguageManager.itemLore.scythes.t3.description;
						break;
					case T4:
						description = LanguageManager.itemLore.scythes.t4.description;
						break;
					case T5:
						description = LanguageManager.itemLore.scythes.t5.description;
						break;
					case T6:
						description = LanguageManager.itemLore.scythes.t6.description;
						break;
					default:
						description = "";
				}
				break;
			default:
				description = "";
		}
		loreBuilder
			.addDescription(description)
			.addSpace();

		// Set attack type
		persistentTags.put(ATTACK_TYPE_KEY, Attacker.AttackType.PENETRATING.toString());
		loreBuilder.addPenetratingAttackType();

		// Set main damage
		int damageLow, damageHigh;
		switch (type) {
			case REAPER:
				switch (tier) {
					case T1:
						damageLow = damageHigh = 19;
						break;
					case T2:
						damageLow = damageHigh = 22;
						break;
					case T3:
						damageLow = damageHigh = 25;
						break;
					default:
						damageLow = damageHigh = 0;
				}
				break;
			case TIERED:
				switch (tier) {
					case T1:
						damageLow = 22;
						damageHigh = 24;
						break;
					case T2:
						damageLow = 29;
						damageHigh = 32;
						break;
					case T3:
						damageLow = 36;
						damageHigh = 42;
						break;
					case T4:
						damageLow = 45;
						damageHigh = 53;
						break;
					case T5:
						damageLow = 55;
						damageHigh = 65;
						break;
					case T6:
						damageLow = 66;
						damageHigh = 78;
						break;
					default:
						damageLow = damageHigh = 0;
				}
				break;
			default:
				damageLow = damageHigh = 0;
		}
		if (damageLow == damageHigh) {
			persistentData.put(LegacyVDPlayer.AttackClass.MAIN.straight(), damageLow);
			loreBuilder.addMainDamage(Integer.toString(damageLow), Integer.toString(damageLow));
		}
		else {
			persistentData.put(LegacyVDPlayer.AttackClass.MAIN.low(), damageLow);
			persistentData.put(LegacyVDPlayer.AttackClass.MAIN.high(), damageHigh);
			loreBuilder.addMainDamage(damageLow + "-" + damageHigh, damageLow + "-" + damageHigh);
		}

		// Set crit damage
		if (damageLow == damageHigh) {
			persistentData.put(LegacyVDPlayer.AttackClass.CRITICAL.straight(), (int) (damageLow * 1.4));
			loreBuilder.addCriticalDamage(Integer.toString((int) (damageLow * 1.4)),
				Integer.toString((int) (damageLow * 1.4)));
		}
		else {
			persistentData.put(LegacyVDPlayer.AttackClass.CRITICAL.low(), (int) (damageLow * 1.4));
			persistentData.put(LegacyVDPlayer.AttackClass.CRITICAL.high(), (int) (damageHigh * 1.4));
			loreBuilder.addCriticalDamage((int) (damageLow * 1.4) + "-" + (int) (damageHigh * 1.4),
				(int) (damageLow * 1.4) + "-" + (int) (damageHigh * 1.4));
		}

		// Set attack speed, adjusting based on material
		double attackSpeedMod = 4;
		switch (mat) {
			case WOODEN_HOE:
			case GOLDEN_HOE:
				attackSpeedMod -= 1;
				break;
			case STONE_HOE:
				attackSpeedMod -= 2;
				break;
			case IRON_HOE:
				attackSpeedMod -= 3;
				break;
			case DIAMOND_HOE:
			case NETHERITE_HOE:
				attackSpeedMod -= 4;
				break;
			default:
				attackSpeedMod = 0;
		}
		attributes.put(
			Attribute.GENERIC_ATTACK_SPEED,
			new AttributeModifier(VDItem.MetaKey.ATTACK_SPEED.name(), attackSpeedMod,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);
		persistentData2.put(ATTACK_SPEED_KEY, 4d);
		loreBuilder.addAttackSpeed(4);

		// Set knockback
		attributes.put(
			Attribute.GENERIC_ATTACK_KNOCKBACK,
			new AttributeModifier(
				MetaKey.KNOCKBACK.name(), -2,
				AttributeModifier.Operation.ADD_NUMBER
			)
		);

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

		// Set price
		int price;
		if (type == ScytheType.TIERED) {
			switch (tier) {
				case T1:
					price = 295;
					break;
				case T2:
					price = 600;
					break;
				case T3:
					price = 980;
					break;
				case T4:
					price = 1480;
					break;
				case T5:
					price = 2100;
					break;
				case T6:
					price = 2850;
					break;
				default:
					price = -1;
			}
		}
		else {
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
		return SCYTHE.equals(value);
	}

	public enum ScytheType {
		REAPER,
		TIERED
	}
}
