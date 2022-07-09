package me.theguyhere.villagerdefense.plugin.game.models.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class GameItems {
	private static final boolean[] FLAGS = {false, false};
	private static final String ATTACK_SPEED = "attackSpeed";
	private static final String DUMMY = "dummy";

	// Item lore constants
	private static final ColoredMessage ATTACK_TYPE = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackType);
	private static final ColoredMessage ATTACK_TYPE_NORMAL = new ColoredMessage(ChatColor.GREEN,
			LanguageManager.names.normal);
	private static final ColoredMessage ATTACK_TYPE_PENETRATING = new ColoredMessage(ChatColor.YELLOW,
			LanguageManager.names.penetrating);
	private static final ColoredMessage MAIN_DAMAGE = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackMainDamage);
	private static final ColoredMessage CRIT_DAMAGE = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackCritDamage);
	private static final ColoredMessage SWEEP_DAMAGE = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackSweepDamage);
	private static final ColoredMessage RANGE_DAMAGE = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackRangeDamage);
	private static final ColoredMessage SPEED = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.attackSpeed);
	private static final ColoredMessage ARMOR = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.armor);
	private static final ColoredMessage TOUGHNESS = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.toughness);
	private static final ColoredMessage WEIGHT = new ColoredMessage(ChatColor.BLUE,
			LanguageManager.messages.weight);

	// Categories of items
	public static ItemStack[] ABILITY_ITEMS;
	public static Material[] FOOD_MATERIALS;
	public static Material[] ARMOR_MATERIALS;
	public static Material[] WEAPON_MATERIALS;
	public static Material[] CLICKABLE_WEAPON_MATERIALS;
	public static Material[] CLICKABLE_CONSUME_MATERIALS;

	public static void init() {
		// Initialize constant arrays
		ABILITY_ITEMS = new ItemStack[]{mage(), ninja(), templar(), warrior(), knight(),
				priest(), siren(), monk(), messenger()};
		FOOD_MATERIALS = new Material[]{Material.BEETROOT, Material.CARROT, Material.BREAD,
				Material.MUTTON, Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GOLDEN_APPLE,
				Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING};
		ARMOR_MATERIALS = new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
				Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
				Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_HELMET,
				Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
				Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
				Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
				Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS,
				Material.NETHERITE_BOOTS, Material.TURTLE_HELMET};
		WEAPON_MATERIALS = new Material[]{Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SWORD, Material.BOW,
				Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_SWORD, Material.STONE_AXE, Material.STONE_HOE,
				Material.STONE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_SWORD, Material.DIAMOND_AXE,
				Material.DIAMOND_HOE, Material.DIAMOND_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE,
				Material.NETHERITE_SWORD, Material.BOW, Material.CROSSBOW, Material.TRIDENT};
		CLICKABLE_WEAPON_MATERIALS = new Material[]{Material.BOW, Material.CROSSBOW,
				Material.TRIDENT};
		CLICKABLE_CONSUME_MATERIALS = new Material[]{Material.GHAST_SPAWN_EGG, Material.WOLF_SPAWN_EGG};
	}

	// Standard game items
	public static @NotNull ItemStack shop() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.EMERALD,
				CommunicationManager.format("&2&l" + LanguageManager.names.itemShop),
				ItemManager.HIDE_ENCHANT_FLAGS, enchants,
				CommunicationManager.format("&7&o" + String.format(LanguageManager.messages.itemShopDesc, "5"))
		);
	}
	public static @NotNull ItemStack kitSelector() {
		return ItemManager.createItem(Material.CHEST,
				CommunicationManager.format("&9&l" + LanguageManager.names.kitSelection));
	}
	public static @NotNull ItemStack challengeSelector() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(Material.NETHER_STAR,
				CommunicationManager.format("&9&l" + LanguageManager.names.challengeSelection),
				ItemManager.HIDE_ENCHANT_FLAGS, enchants);
	}
	public static @NotNull ItemStack boostToggle(boolean boosted) {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(Material.FIREWORK_ROCKET,
				CommunicationManager.format("&b&l" + LanguageManager.names.boosts + ": " +
						getToggleStatus(boosted)),
				ItemManager.HIDE_ENCHANT_FLAGS, enchants);
	}
	public static @NotNull ItemStack shareToggle(boolean sharing) {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(Material.DISPENSER,
				CommunicationManager.format("&b&l" + LanguageManager.names.effectShare + ": " +
						getToggleStatus(sharing)),
				ItemManager.HIDE_ENCHANT_FLAGS, enchants);
	}
	public static @NotNull ItemStack crystalConverter() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(Material.DIAMOND,
				CommunicationManager.format("&b&l" + String.format(LanguageManager.names.crystalConverter,
						LanguageManager.names.crystal)), ItemManager.HIDE_ENCHANT_FLAGS, enchants);
	}
	public static @NotNull ItemStack leave() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(Material.BARRIER,
				CommunicationManager.format("&c&l" + LanguageManager.messages.leave),
				ItemManager.HIDE_ENCHANT_FLAGS, enchants);
	}

	// Placeholders
	public static @NotNull ItemStack levelPlaceholder(int level) {
		return ItemManager.createItem(Material.GUNPOWDER, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.levelPlaceholder),
						Integer.toString(level)), ItemManager.BUTTON_FLAGS, null);
	}
	public static @NotNull ItemStack duplicatePlaceholder() {
		return ItemManager.createItem(Material.GUNPOWDER,
				new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.duplicatePlaceholder).toString(),
				ItemManager.BUTTON_FLAGS, null);
	}

	// Weapons
	public static @NotNull ItemStack starterSword() {
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Add space in lore from name
		lores.add("");

		// Set attack type
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

		// Set main damage
		lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
				Integer.toString(15))));

		// Set crit damage
		lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
				Integer.toString(20))));

		// Set sweep damage
		lores.add(CommunicationManager.format(SWEEP_DAMAGE, new ColoredMessage(ChatColor.LIGHT_PURPLE,
				Integer.toString(5))));

		// Set attack speed
		attributes.put(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier(ATTACK_SPEED, -2.5, AttributeModifier.Operation.ADD_NUMBER));
		lores.add(CommunicationManager.format(SPEED, Double.toString(1.5)));

		// Set dummy damage
		attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(Material.WOODEN_SWORD,
				new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.starterSword).toString(),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack sword(double difficulty) {
		int level = getLevel(difficulty);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.WOODEN_SWORD;
				break;
			case 1:
				mat = Material.STONE_SWORD;
				break;
			case 2:
				mat = Material.IRON_SWORD;
				break;
			case 3:
				mat = Material.DIAMOND_SWORD;
				break;
			default:
				mat = Material.NETHERITE_SWORD;
		}

		// Add space in lore from name
		lores.add("");

		// Set attack type
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

		// Set main damage
		int damageLow = 15 + 5 * ((level - 1) / 2);
		int damageHigh = 25 + 5 * (level / 2);
		lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
				damageLow + "-" + damageHigh)));

		// Set crit damage
		lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
				(int) (damageLow * 1.5) + "-" + (int) (damageHigh * 1.25))));

		// Set sweep damage
		lores.add(CommunicationManager.format(SWEEP_DAMAGE, new ColoredMessage(ChatColor.LIGHT_PURPLE,
				(damageLow / 2) + "-" + ((damageHigh - 5) / 2))));

		// Set attack speed
		attributes.put(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier(ATTACK_SPEED, -2.5, AttributeModifier.Operation.ADD_NUMBER));
		lores.add(CommunicationManager.format(SPEED, Double.toString(1.5)));

		// Set dummy damage
		attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		// Set price
		int price = (int) (150 + 50 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
				new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.sword), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack axe(double difficulty) {
		int level = Math.max(getLevel(difficulty) - 1, 1);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.WOODEN_AXE;
				break;
			case 1:
				mat = Material.STONE_AXE;
				break;
			case 2:
				mat = Material.IRON_AXE;
				break;
			case 3:
				mat = Material.DIAMOND_AXE;
				break;
			default:
				mat = Material.NETHERITE_AXE;
		}

		// Add space in lore from name
		lores.add("");

		// Set attack type
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

		// Set main damage
		int damageLow = 20 + 5 * ((level - 1) / 2);
		int damageHigh = 40 + 10 * (level / 2);
		lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
				damageLow + "-" + damageHigh)));

		// Set crit damage
		lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
				(int) (damageLow * 1.25) + "-" + (int) (damageHigh * 1.5))));

		// Set attack speed
		attributes.put(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier(ATTACK_SPEED, -3.15, AttributeModifier.Operation.ADD_NUMBER));
		lores.add(CommunicationManager.format(SPEED, Double.toString(0.85)));

		// Set dummy damage
		attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		// Set price
		int price = (int) (175 + 55 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.axe), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack scythe(double difficulty) {
		int level = Math.max(getLevel(difficulty) - 3, 1);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.WOODEN_HOE;
				break;
			case 1:
				mat = Material.STONE_HOE;
				break;
			case 2:
				mat = Material.IRON_HOE;
				break;
			case 3:
				mat = Material.DIAMOND_HOE;
				break;
			default:
				mat = Material.NETHERITE_HOE;
		}

		// Add space in lore from name
		lores.add("");

		// Set attack type
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_PENETRATING));

		// Set main damage
		int damageLow = 6 + 2 * ((level - 1) / 2);
		int damageHigh = 12 + 3 * (level / 2);
		lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
				damageLow + "-" + damageHigh)));

		// Set crit damage
		lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
				(int) (damageLow * 1.4) + "-" + (int) (damageHigh * 1.4))));

		// Set attack speed
		attributes.put(Attribute.GENERIC_ATTACK_SPEED,
				new AttributeModifier(ATTACK_SPEED, -.5, AttributeModifier.Operation.ADD_NUMBER));
		lores.add(CommunicationManager.format(SPEED, Double.toString(3.5)));

		// Set dummy damage
		attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

		// Set price
		int price = (int) (190 + 60 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.scythe), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack bow(double difficulty) {
		int level = Math.max(getLevel(difficulty) - 2, 1);
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Add space in lore from name
		lores.add("");

		// Set attack type
		lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

		// Set main damage
		int damageLow = 2 + (level - 1) / 2;
		int damageHigh = 3 + level / 2;
		lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
				String.format(LanguageManager.messages.perBlock, damageLow + "-" + damageHigh))));

		// Note attack speed (can't really change it)
		lores.add(CommunicationManager.format(SPEED, Double.toString(1)));

		// Set price
		int price = (int) (185 + 55 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(Material.BOW, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.bow), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack crossbow(int level) {
		Random r = new Random();
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 300;
		double chance = r.nextDouble();

		// Set unbreaking
		switch (level) {
			case 1:
				if (chance < .4) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				}
				break;
			case 2:
				if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 3:
				if (chance < .1) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .7) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 4:
				if (chance < .05) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .9) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 5:
				if (chance < .4) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 6:
				if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .9) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 7:
				if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 8:
				if (chance < .75) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				} else if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 4);
					price += 100;
				}
				break;
			default:
				if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				} else {
					enchantments.put(Enchantment.DURABILITY, 4);
					price += 100;
				}
		}
		chance = r.nextDouble();

		// Set quick charge
		switch (level) {
			case 1:
				if (chance < .3) {
					enchantments.put(Enchantment.QUICK_CHARGE, 1);
					price += 60;
				}
				break;
			case 2:
				if (chance < .4) {
					enchantments.put(Enchantment.QUICK_CHARGE, 1);
					price += 60;
				} else if (chance < .45) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				}
				break;
			case 3:
				if (chance < .3) {
					enchantments.put(Enchantment.QUICK_CHARGE, 1);
					price += 60;
				} else if (chance < .5) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				}
				break;
			case 4:
				if (chance < .25) {
					enchantments.put(Enchantment.QUICK_CHARGE, 1);
					price += 60;
				} else if (chance < .5) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				} else if (chance < .55) {
					enchantments.put(Enchantment.QUICK_CHARGE, 3);
					price += 180;
				}
				break;
			case 5:
				if (chance < .1) {
					enchantments.put(Enchantment.QUICK_CHARGE, 1);
					price += 60;
				} else if (chance < .45) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				} else if (chance < .6) {
					enchantments.put(Enchantment.QUICK_CHARGE, 3);
					price += 180;
				}
				break;
			case 6:
				if (chance < .4) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				} else if (chance < .55) {
					enchantments.put(Enchantment.QUICK_CHARGE, 3);
					price += 180;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.QUICK_CHARGE, 2);
					price += 120;
				} else if (chance < .6) {
					enchantments.put(Enchantment.QUICK_CHARGE, 3);
					price += 180;
				}
		}
		chance = r.nextDouble();

		// Set piercing
		switch (level) {
			case 1:
				if (chance < .15) {
					enchantments.put(Enchantment.PIERCING, 1);
					price += 50;
				} else if (chance < .25) {
					enchantments.put(Enchantment.PIERCING, 2);
					price += 100;
				}
				break;
			case 2:
				if (chance < .2) {
					enchantments.put(Enchantment.PIERCING, 1);
					price += 50;
				} else if (chance < .35) {
					enchantments.put(Enchantment.PIERCING, 2);
					price += 100;
				} else if (chance < .4) {
					enchantments.put(Enchantment.PIERCING, 3);
					price += 150;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.PIERCING, 1);
					price += 50;
				} else if (chance < .25) {
					enchantments.put(Enchantment.PIERCING, 2);
					price += 100;
				} else if (chance < .45) {
					enchantments.put(Enchantment.PIERCING, 3);
					price += 150;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.PIERCING, 2);
					price += 100;
				} else if (chance < .35) {
					enchantments.put(Enchantment.PIERCING, 3);
					price += 150;
				} else if (chance < .45) {
					enchantments.put(Enchantment.PIERCING, 4);
					price += 200;
				}
				break;
			case 5:
				if (chance < .2) {
					enchantments.put(Enchantment.PIERCING, 3);
					price += 150;
				} else if (chance < .35) {
					enchantments.put(Enchantment.PIERCING, 4);
					price += 200;
				} else if (chance < .45) {
					enchantments.put(Enchantment.PIERCING, 5);
					price += 250;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.PIERCING, 3);
					price += 150;
				} else if (chance < .35) {
					enchantments.put(Enchantment.PIERCING, 4);
					price += 200;
				} else if (chance < .55) {
					enchantments.put(Enchantment.PIERCING, 5);
					price += 250;
				}
				break;
			case 7:
				if (chance < .15) {
					enchantments.put(Enchantment.PIERCING, 4);
					price += 200;
				} else if (chance < .4) {
					enchantments.put(Enchantment.PIERCING, 5);
					price += 250;
				} else if (chance < .5) {
					enchantments.put(Enchantment.PIERCING, 6);
					price += 300;
				}
				break;
			default:
				if (chance < .3) {
					enchantments.put(Enchantment.PIERCING, 5);
					price += 250;
				} else if (chance < .5) {
					enchantments.put(Enchantment.PIERCING, 6);
					price += 300;
				}
		}
		chance = r.nextDouble();

		// Set multishot
		switch (level) {
			case 1:
			case 2:
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.MULTISHOT, 1);
					price += 120;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.MULTISHOT, 1);
					price += 120;
				}
				break;
			case 5:
				if (chance < .15) {
					enchantments.put(Enchantment.MULTISHOT, 1);
					price += 120;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.MULTISHOT, 1);
					price += 120;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.MULTISHOT, 1);
					price += 120;
				}
		}
		chance = r.nextDouble();

		// Set mending
		switch (level) {
			case 1:
			case 2:
			case 3:
				break;
			case 4:
				if (chance < .05) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			case 5:
				if (chance < .1) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			case 6:
				if (chance < .15) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			default:
				if (chance < .2) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
		}

		return ItemManager.createItem(Material.CROSSBOW, null, FLAGS, enchantments,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
						price));
	}
	public static @NotNull ItemStack trident(int level) {
		Random r = new Random();
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 700;
		double chance = r.nextDouble();

		// Set unbreaking
		switch (level) {
			case 1:
				if (chance < .4) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				}
				break;
			case 2:
				if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 3:
				if (chance < .1) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .7) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 4:
				if (chance < .05) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 25;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .9) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 5:
				if (chance < .4) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 6:
				if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 50;
				} else if (chance < .9) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 7:
				if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
			case 8:
				if (chance < .75) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				} else if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 4);
					price += 100;
				}
				break;
			default:
				if (chance < .8) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				} else {
					enchantments.put(Enchantment.DURABILITY, 4);
					price += 100;
				}
		}
		chance = r.nextDouble();

		// Set loyalty
		switch (level) {
			case 1:
			case 2:
			case 3:
				enchantments.put(Enchantment.LOYALTY, 1);
				price += 100;
				break;
			case 4:
				if (chance < .75) {
					enchantments.put(Enchantment.LOYALTY, 1);
					price += 100;
				} else {
					enchantments.put(Enchantment.LOYALTY, 2);
					price += 200;
				}
				break;
			case 5:
				if (chance < .5) {
					enchantments.put(Enchantment.LOYALTY, 1);
					price += 100;
				} else if (chance < .8) {
					enchantments.put(Enchantment.LOYALTY, 2);
					price += 200;
				} else {
					enchantments.put(Enchantment.LOYALTY, 3);
					price += 300;
				}
				break;
			case 6:
				if (chance < .3) {
					enchantments.put(Enchantment.LOYALTY, 1);
					price += 100;
				} else if (chance < .7) {
					enchantments.put(Enchantment.LOYALTY, 2);
					price += 200;
				} else {
					enchantments.put(Enchantment.LOYALTY, 3);
					price += 300;
				}
				break;
			default:
				if (chance < .2) {
					enchantments.put(Enchantment.LOYALTY, 1);
					price += 100;
				} else if (chance < .5) {
					enchantments.put(Enchantment.LOYALTY, 2);
					price += 200;
				} else {
					enchantments.put(Enchantment.LOYALTY, 3);
					price += 300;
				}
		}
		chance = r.nextDouble();

		// Set knockback
		switch (level) {
			case 1:
				if (chance < .3) {
					enchantments.put(Enchantment.KNOCKBACK, 1);
					price += 30;
				}
				break;
			case 2:
				if (chance < .4) {
					enchantments.put(Enchantment.KNOCKBACK, 1);
					price += 30;
				} else if (chance < .45) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				}
				break;
			case 3:
				if (chance < .3) {
					enchantments.put(Enchantment.KNOCKBACK, 1);
					price += 30;
				} else if (chance < .5) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				}
				break;
			case 4:
				if (chance < .25) {
					enchantments.put(Enchantment.KNOCKBACK, 1);
					price += 30;
				} else if (chance < .5) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				} else if (chance < .55) {
					enchantments.put(Enchantment.KNOCKBACK, 3);
					price += 90;
				}
				break;
			case 5:
				if (chance < .1) {
					enchantments.put(Enchantment.KNOCKBACK, 1);
					price += 30;
				} else if (chance < .45) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				} else if (chance < .6) {
					enchantments.put(Enchantment.KNOCKBACK, 3);
					price += 90;
				}
				break;
			case 6:
				if (chance < .4) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				} else if (chance < .55) {
					enchantments.put(Enchantment.KNOCKBACK, 3);
					price += 90;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.KNOCKBACK, 2);
					price += 60;
				} else if (chance < .6) {
					enchantments.put(Enchantment.KNOCKBACK, 3);
					price += 90;
				}
		}
		chance = r.nextDouble();

		// Set smite
		switch (level) {
			case 1:
				if (chance < .15) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 1);
					price += 60;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 2);
					price += 120;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 1);
					price += 60;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 2);
					price += 120;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 1);
					price += 60;
				} else if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 2);
					price += 120;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 2);
					price += 120;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				}
				break;
			case 5:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				}
		}
		chance = r.nextDouble();

		// Set sharpness
		switch (level) {
			case 1:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				}
				break;
			case 5:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				}
				break;
			default:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				}
		}
		chance = r.nextDouble();

		// Set fire aspect
		switch (level) {
			case 1:
				if (chance < .1) {
					enchantments.put(Enchantment.FIRE_ASPECT, 1);
					price += 100;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.FIRE_ASPECT, 1);
					price += 100;
				} else if (chance < .2) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				}
				break;
			case 3:
				if (chance < .15) {
					enchantments.put(Enchantment.FIRE_ASPECT, 1);
					price += 100;
				} else if (chance < .25) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.FIRE_ASPECT, 1);
					price += 100;
				} else if (chance < .25) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				} else if (chance < .3) {
					enchantments.put(Enchantment.FIRE_ASPECT, 3);
					price += 300;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.FIRE_ASPECT, 1);
					price += 100;
				} else if (chance < .25) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				} else if (chance < .35) {
					enchantments.put(Enchantment.FIRE_ASPECT, 3);
					price += 300;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				} else if (chance < .35) {
					enchantments.put(Enchantment.FIRE_ASPECT, 3);
					price += 300;
				}
				break;
			default:
				if (chance < .1) {
					enchantments.put(Enchantment.FIRE_ASPECT, 2);
					price += 200;
				} else if (chance < .3) {
					enchantments.put(Enchantment.FIRE_ASPECT, 3);
					price += 300;
				}
		}
		chance = r.nextDouble();

		// Set mending
		switch (level) {
			case 1:
			case 2:
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			case 5:
				if (chance < .15) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 250;
				}
		}

		return ItemManager.createItem(Material.TRIDENT, null, FLAGS, enchantments,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
						price));
	}
	public static @NotNull ItemStack shield(int level) {
		Random r = new Random();
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 500;
		double chance = r.nextDouble();

		// Set unbreaking
		switch (level) {
			case 1:
				break;
			case 2:
				if (chance < .2) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 100;
				}
				break;
			case 3:
				if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 100;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 200;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 100;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 200;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 300;
				}
				break;
			case 5:
				if (chance < .1) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 100;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 200;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 300;
				}
				break;
			default:
				if (chance < .1) {
					enchantments.put(Enchantment.DURABILITY, 1);
					price += 100;
				} else if (chance < .3) {
					enchantments.put(Enchantment.DURABILITY, 2);
					price += 200;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 300;
				}
		}
		chance = r.nextDouble();

		// Set mending
		switch (level) {
			case 1:
			case 2:
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 400;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 400;
				}
				break;
			case 5:
				if (chance < .15) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 400;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 400;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.MENDING, 1);
					price += 400;
				}
		}

		return ItemManager.createItem(Material.SHIELD, null, FLAGS, enchantments,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
						price));
	}

	// Ammo
	public static @NotNull ItemStack arrows() {
		return ItemManager.createItems(Material.ARROW, 16, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a45"));
	}
	public static @NotNull ItemStack arrowsS() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.SLOWNESS),
				8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a50"));
	}
	public static @NotNull ItemStack arrowsD() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.INSTANT_DAMAGE),
				8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a70"));
	}
	public static @NotNull ItemStack arrowsW() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.WEAKNESS),
				8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a50"));
	}
	public static @NotNull ItemStack arrowsP() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.POISON),
				16, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a60"));
	}
	public static @NotNull ItemStack arrowsSPlus() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.SLOWNESS, false, true), 8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a125"));
	}
	public static @NotNull ItemStack arrowsDPlus() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.INSTANT_DAMAGE, false, true), 8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a175"));
	}
	public static @NotNull ItemStack arrowsWPlus() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.WEAKNESS, false,true), 8, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a125"));
	}
	public static @NotNull ItemStack arrowsPPlus() {
		return ItemManager.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.POISON, false, true), 16, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a130"));
	}
	public static @NotNull ItemStack rockets() {
		ItemStack item = ItemManager.createItems(Material.FIREWORK_ROCKET, 4, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a50"));
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		for (int i = 0; i < 3; i++) {
			fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE)
					.build());
		}
		fireworkMeta.setPower(3);

		item.setItemMeta(meta);

		return item;
	}
	public static @NotNull ItemStack rocketsPlus() {
		ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, 4);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		List<String> lore = new ArrayList<>();
		lore.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a100"));
		meta.setLore(lore);
		for (int i = 0; i < 9; i++) {
			fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE)
					.build());
		}
		fireworkMeta.setPower(9);

		item.setItemMeta(meta);

		return item;
	}

	// Armor
	public static @NotNull ItemStack helmet(double difficulty) {
		int level = getLevel(difficulty);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.LEATHER_HELMET;
				break;
			case 1:
				mat = Material.CHAINMAIL_HELMET;
				break;
			case 2:
				mat = Material.IRON_HELMET;
				break;
			case 3:
				mat = Material.DIAMOND_HELMET;
				break;
			default:
				mat = Material.NETHERITE_HELMET;
		}

		// Add space in lore from name
		lores.add("");

		// Set armor
		int armor = 1 + level;
		lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));

		// Set toughness
		int toughness = Math.max((level - 12)/ 2, 0);
		if (toughness > 0)
			lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
					toughness + "%")));

		// Set weight
		int weight = ((level - 1) / 8) * 5;
		lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
				Integer.toString(weight))));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, weight * .01, AttributeModifier.Operation.ADD_NUMBER));

		// Set price
		int price = (int) (75 + 45 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.helmet), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack chestplate(double difficulty) {
		int level = getLevel(difficulty);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.LEATHER_CHESTPLATE;
				break;
			case 1:
				mat = Material.CHAINMAIL_CHESTPLATE;
				break;
			case 2:
				mat = Material.IRON_CHESTPLATE;
				break;
			case 3:
				mat = Material.DIAMOND_CHESTPLATE;
				break;
			default:
				mat = Material.NETHERITE_CHESTPLATE;
		}

		// Add space in lore from name
		lores.add("");

		// Set armor
		int armor = 3 + level;
		lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));

		// Set toughness
		int toughness = Math.max(level - 12, 0);
		if (toughness > 0)
			lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
					toughness + "%")));

		// Set weight
		int weight = ((level - 1) / 8) * 5;
		lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
				Integer.toString(weight))));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, weight * .01, AttributeModifier.Operation.ADD_NUMBER));

		// Set price
		int price = (int) (120 + 60 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.chestplate), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack leggings(double difficulty) {
		int level = getLevel(difficulty);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.LEATHER_LEGGINGS;
				break;
			case 1:
				mat = Material.CHAINMAIL_LEGGINGS;
				break;
			case 2:
				mat = Material.IRON_LEGGINGS;
				break;
			case 3:
				mat = Material.DIAMOND_LEGGINGS;
				break;
			default:
				mat = Material.NETHERITE_LEGGINGS;
		}

		// Add space in lore from name
		lores.add("");

		// Set armor
		int armor = 2 + level;
		lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));

		// Set toughness
		int toughness = Math.max(level - 16, 0);
		if (toughness > 0)
			lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
					toughness + "%")));

		// Set weight
		int weight = ((level - 1) / 8) * 5;
		lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
				Integer.toString(weight))));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, weight * .01, AttributeModifier.Operation.ADD_NUMBER));

		// Set price
		int price = (int) (100 + 55 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.leggings), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}
	public static @NotNull ItemStack boots(double difficulty) {
		int level = getLevel(difficulty);
		Material mat;
		List<String> lores = new ArrayList<>();
		Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

		// Set material
		switch ((level - 1) / 8) {
			case 0:
				mat = Material.LEATHER_BOOTS;
				break;
			case 1:
				mat = Material.CHAINMAIL_BOOTS;
				break;
			case 2:
				mat = Material.IRON_BOOTS;
				break;
			case 3:
				mat = Material.DIAMOND_BOOTS;
				break;
			default:
				mat = Material.NETHERITE_BOOTS;
		}

		// Add space in lore from name
		lores.add("");

		// Set armor
		lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(level))));

		// Set toughness
		int toughness = Math.max((level - 16)/ 2, 0);
		if (toughness > 0)
			lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
					toughness + "%")));

		// Set weight
		int weight = ((level - 1) / 8) * 5;
		lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
				Integer.toString(weight))));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, 0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
		attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
				new AttributeModifier(DUMMY, weight * .01, AttributeModifier.Operation.ADD_NUMBER));

		// Set price
		int price = (int) (60 + 40 * level * Math.pow(Math.E, (level - 1) / 50d));
		lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
				price));

		// Set name, make unbreakable, and return
		return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
						new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.boots), Integer.toString(level)),
				ItemManager.BUTTON_FLAGS, null, lores, attributes));
	}

	// Food
	public static @NotNull ItemStack totem() {
		return ItemManager.createItem(Material.TOTEM_OF_UNDYING, null,
				new ColoredMessage(ChatColor.GOLD, "+100 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a900"));
	}
	public static @NotNull ItemStack egapple() {
		return ItemManager.createItem(Material.ENCHANTED_GOLDEN_APPLE, null,
				new ColoredMessage(ChatColor.RED, "+60 " + Utils.HP).toString(),
				new ColoredMessage(ChatColor.GOLD, "+15 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a575"));
	}
	public static @NotNull ItemStack gapple() {
		return ItemManager.createItem(Material.GOLDEN_APPLE, null,
				new ColoredMessage(ChatColor.RED, "+40 " + Utils.HP).toString(),
				new ColoredMessage(ChatColor.GOLD, "+10 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a400"));
	}
	public static @NotNull ItemStack gcarrot() {
		return ItemManager.createItem(Material.GOLDEN_CARROT, null,
				new ColoredMessage(ChatColor.RED, "+50 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a330"));
	}
	public static @NotNull ItemStack steak() {
		return ItemManager.createItem(Material.COOKED_BEEF, null,
				new ColoredMessage(ChatColor.RED, "+40 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a275"));
	}
	public static @NotNull ItemStack mutton() {
		return ItemManager.createItem(Material.COOKED_MUTTON,  null,
				new ColoredMessage(ChatColor.RED, "+25 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a175"));
	}
	public static @NotNull ItemStack bread() {
		return ItemManager.createItem(Material.BREAD, null,
				new ColoredMessage(ChatColor.RED, "+15 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a110"));
	}
	public static @NotNull ItemStack carrot() {
		return ItemManager.createItem(Material.CARROT, null,
				new ColoredMessage(ChatColor.RED, "+10 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a75"));
	}
	public static @NotNull ItemStack beetroot() {
		return ItemManager.createItem(Material.BEETROOT, null,
				new ColoredMessage(ChatColor.RED, "+5 " + Utils.HP).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a40"));
	}

	// Other consumables
	public static @NotNull ItemStack golem() {
		return ItemManager.createItem(Material.GHAST_SPAWN_EGG,
				new ColoredMessage(ChatColor.WHITE, LanguageManager.names.golemEgg).toString(),
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a500"));
	}
	public static @NotNull ItemStack wolf() {
		return ItemManager.createItem(Material.WOLF_SPAWN_EGG, null,
				CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a250"));
	}

	// Kit abilities
	public static @NotNull ItemStack mage() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.PURPLE_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.mage.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString());
	}
	public static @NotNull ItemStack ninja() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.BLACK_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.ninja.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack templar() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.YELLOW_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.templar.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack warrior() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.RED_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.warrior.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack knight() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.BROWN_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.knight.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack priest() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.WHITE_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.priest.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack siren() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.PINK_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.siren.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack monk() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.GREEN_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.monk.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}
	public static @NotNull ItemStack messenger() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		return ItemManager.createItem(
				Material.BLUE_DYE,
				new ColoredMessage(
						ChatColor.LIGHT_PURPLE,
						LanguageManager.kits.messenger.name + " " + LanguageManager.names.essence
				).toString(),
				ItemManager.HIDE_ENCHANT_FLAGS,
				enchants,
				new ColoredMessage(LanguageManager.messages.rightClick).toString()
		);
	}

	public static @NotNull ItemStack randAmmo(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				return arrows();
			case 2:
				if (chance < .6)
					return arrows();
				else return arrowsP();
			case 3:
				if (chance < .3)
					return arrows();
				else if (chance < .6)
					return arrowsP();
				else if (chance < .8)
					return arrowsS();
				else if (chance < .9)
					return arrowsW();
				else return arrowsD();
			case 4:
				if (chance < .35)
					return arrows();
				else if (chance < .55)
					return arrowsPPlus();
				else if (chance < .7)
					return arrowsSPlus();
				else if (chance < .8)
					return arrowsW();
				else if (chance < .9)
					return arrowsD();
				else return rockets();
			default:
				if (chance < .25)
					return arrows();
				else if (chance < .5)
					return arrowsPPlus();
				else if (chance < .7)
					return arrowsSPlus();
				else if (chance < .8)
					return arrowsWPlus();
				else if (chance < .9)
					return arrowsDPlus();
				else return rocketsPlus();
		}
	}
	public static @NotNull ItemStack randOther(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				return wolf();
			case 3:
				if (chance < .15)
					return wolf();
				else return golem();
			default:
				return golem();
		}
	}

	// Easy way to get a string for a toggle status
	private static String getToggleStatus(boolean status) {
		String toggle;
		if (status)
			toggle = "&a&l" + LanguageManager.messages.onToggle;
		else toggle = "&c&l" + LanguageManager.messages.offToggle;
		return toggle;
	}

	// Gaussian level randomization for most ordinary stuff
	private static int getLevel(double difficulty) {
		Random r = new Random();
		return Math.max((int) (Math.max(difficulty, 1.5) * (1 + .2 * Math.max(Math.min(r.nextGaussian(), 3), -3)) + .5),
				1); // Mean 100%, SD 50%, restrict 40% - 160%, min mean 3
	}
}