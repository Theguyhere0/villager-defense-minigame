package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SuppressWarnings("SpellCheckingInspection")
public class GameItems {
	private static final boolean[] FLAGS = {false, false};

	// Standard game items
	public static @NotNull ItemStack shop() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {true, false};
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.EMERALD, Utils.format("&2&lItem Shop"), flags, enchants,
				Utils.format("&7&oResets every 10 rounds"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack kitSelector() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {true, false};
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.CHEST, Utils.format("&9&lKit Selection"), flags, enchants);

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack challengeSelector() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {true, false};
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.NETHER_STAR, Utils.format("&9&lChallenge Selection"), flags,
				enchants);

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack leave() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {true, false};
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.BARRIER, Utils.format("&c&lLEAVE"), flags, enchants);

		return item == null ? new ItemStack(Material.AIR) : item;
	}

	// Weapons
	public static @NotNull ItemStack sword(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.WOODEN_SWORD;
					price += 50;
				} else if (chance < .8) {
					mat = Material.STONE_SWORD;
					price += 120;
				} else {
					mat = Material.IRON_SWORD;
					price += 250;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.STONE_SWORD;
					price += 120;
				} else if (chance < .9) {
					mat = Material.IRON_SWORD;
					price += 250;
				} else {
					mat = Material.DIAMOND_SWORD;
					price += 500;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_SWORD;
					price += 250;
				} else if (chance < .85) {
					mat = Material.DIAMOND_SWORD;
					price += 500;
				} else {
					mat = Material.NETHERITE_SWORD;
					price += 700;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_SWORD;
					price += 250;
				} else if (chance < .75) {
					mat = Material.DIAMOND_SWORD;
					price += 500;
				} else {
					mat = Material.NETHERITE_SWORD;
					price += 700;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_SWORD;
					price += 500;
				} else {
					mat = Material.NETHERITE_SWORD;
					price += 700;
				}
				break;
			default:
				mat = Material.NETHERITE_SWORD;
				price += 700;
		}
		chance = r.nextDouble();

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
			default:
				if (chance < .6) {
					enchantments.put(Enchantment.DURABILITY, 3);
					price += 75;
				}
				break;
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

		// Set sweeping
		switch (level) {
			case 1:
				if (chance < .05) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 1);
					price += 50;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 1);
					price += 50;
				} else if (chance < .2) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				}
				break;
			case 3:
				if (chance < .2) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 1);
					price += 50;
				} else if (chance < .3) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 1);
					price += 50;
				} else if (chance < .3) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				} else if (chance < .4) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 3);
					price += 150;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 1);
					price += 50;
				} else if (chance < .25) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				} else if (chance < .45) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 3);
					price += 150;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				} else if (chance < .35) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 3);
					price += 150;
				} else if (chance < .45) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 4);
					price += 200;
				}
				break;
			default:
				if (chance < .05) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 2);
					price += 100;
				} else if (chance < .35) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 3);
					price += 150;
				} else if (chance < .5) {
					enchantments.put(Enchantment.SWEEPING_EDGE, 4);
					price += 200;
				}
				break;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack axe(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.WOODEN_AXE;
					price += 40;
				} else if (chance < .8) {
					mat = Material.STONE_AXE;
					price += 100;
				} else {
					mat = Material.IRON_AXE;
					price += 220;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.STONE_AXE;
					price += 100;
				} else if (chance < .9) {
					mat = Material.IRON_AXE;
					price += 220;
				} else {
					mat = Material.DIAMOND_AXE;
					price += 480;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_AXE;
					price += 220;
				} else if (chance < .85) {
					mat = Material.DIAMOND_AXE;
					price += 480;
				} else {
					mat = Material.NETHERITE_AXE;
					price += 700;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_AXE;
					price += 220;
				} else if (chance < .75) {
					mat = Material.DIAMOND_AXE;
					price += 480;
				} else {
					mat = Material.NETHERITE_AXE;
					price += 700;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_AXE;
					price += 480;
				} else {
					mat = Material.NETHERITE_AXE;
					price += 700;
				}
				break;
			default:
				mat = Material.NETHERITE_AXE;
				price += 700;
		}
		chance = r.nextDouble();

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

		// Set smite
		switch (level) {
			case 1:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 1);
					price += 60;
				} else if (chance < .3) {
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
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 1);
					price += 60;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 2);
					price += 120;
				} else if (chance < .45) {
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
				} else if (chance < .45) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				}
				break;
			case 5:
				if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .55) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 3);
					price += 180;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 6);
					price += 360;
				}
				break;
			case 7:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 4);
					price += 240;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				} else if (chance < .6) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 6);
					price += 360;
				}
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
					price += 300;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_UNDEAD, 6);
					price += 360;
				}
		}
		chance = r.nextDouble();

		// Set sharpness
		switch (level) {
			case 1:
				if (chance < .15) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				}
				break;
			case 2:
				if (chance < .2) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.DAMAGE_ALL, 1);
					price += 75;
				} else if (chance < .25) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .45) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.DAMAGE_ALL, 2);
					price += 150;
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_ALL, 3);
					price += 225;
				} else if (chance < .45) {
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
				} else if (chance < .35) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				} else if (chance < .55) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				}
				break;
			case 7:
				if (chance < .15) {
					enchantments.put(Enchantment.DAMAGE_ALL, 4);
					price += 300;
				} else if (chance < .4) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_ALL, 6);
					price += 450;
				}
				break;
			default:
				if (chance < .3) {
					enchantments.put(Enchantment.DAMAGE_ALL, 5);
					price += 375;
				} else if (chance < .5) {
					enchantments.put(Enchantment.DAMAGE_ALL, 6);
					price += 450;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack bow(int level) {
		Random r = new Random();
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 150;
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

		// Set punch
		switch (level) {
			case 1:
				if (chance < .3) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
					price += 30;
				}
				break;
			case 2:
				if (chance < .4) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
					price += 30;
				} else if (chance < .45) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				}
				break;
			case 3:
				if (chance < .3) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
					price += 30;
				} else if (chance < .5) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				}
				break;
			case 4:
				if (chance < .25) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
					price += 30;
				} else if (chance < .5) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				} else if (chance < .55) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 3);
					price += 90;
				}
				break;
			case 5:
				if (chance < .1) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 1);
					price += 30;
				} else if (chance < .45) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				} else if (chance < .6) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 3);
					price += 90;
				}
				break;
			case 6:
				if (chance < .4) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				} else if (chance < .55) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 3);
					price += 90;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
					price += 60;
				} else if (chance < .6) {
					enchantments.put(Enchantment.ARROW_KNOCKBACK, 3);
					price += 90;
				}
		}
		chance = r.nextDouble();

		// Set power
		switch (level) {
			case 1:
				if (chance < .15) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 1);
					price += 75;
				} else if (chance < .25) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 2);
					price += 150;
				}
				break;
			case 2:
				if (chance < .2) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 1);
					price += 75;
				} else if (chance < .35) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 2);
					price += 150;
				} else if (chance < .4) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 3);
					price += 225;
				}
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 1);
					price += 75;
				} else if (chance < .25) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 2);
					price += 150;
				} else if (chance < .45) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 3);
					price += 225;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 2);
					price += 150;
				} else if (chance < .35) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 3);
					price += 225;
				} else if (chance < .45) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 4);
					price += 300;
				}
				break;
			case 5:
				if (chance < .2) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 3);
					price += 225;
				} else if (chance < .35) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 4);
					price += 300;
				} else if (chance < .45) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 5);
					price += 375;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 3);
					price += 225;
				} else if (chance < .35) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 4);
					price += 300;
				} else if (chance < .55) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 5);
					price += 375;
				}
				break;
			case 7:
				if (chance < .15) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 4);
					price += 300;
				} else if (chance < .4) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 5);
					price += 375;
				} else if (chance < .5) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 6);
					price += 450;
				}
				break;
			default:
				if (chance < .3) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 5);
					price += 375;
				} else if (chance < .5) {
					enchantments.put(Enchantment.ARROW_DAMAGE, 6);
					price += 450;
				}
		}
		chance = r.nextDouble();

		// Set flame
		switch (level) {
			case 1:
			case 2:
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.ARROW_FIRE, 1);
					price += 100;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.ARROW_FIRE, 1);
					price += 100;
				}
				break;
			case 5:
				if (chance < .15) {
					enchantments.put(Enchantment.ARROW_FIRE, 1);
					price += 100;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.ARROW_FIRE, 1);
					price += 100;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.ARROW_FIRE, 1);
					price += 100;
				}
		}
		chance = r.nextDouble();

		// Set infinity
		switch (level) {
			case 1:
			case 2:
				break;
			case 3:
				if (chance < .05) {
					enchantments.put(Enchantment.ARROW_INFINITE, 1);
					price += 400;
				}
				break;
			case 4:
				if (chance < .1) {
					enchantments.put(Enchantment.ARROW_INFINITE, 1);
					price += 400;
				}
				break;
			case 5:
				if (chance < .15) {
					enchantments.put(Enchantment.ARROW_INFINITE, 1);
					price += 400;
				}
				break;
			case 6:
				if (chance < .2) {
					enchantments.put(Enchantment.ARROW_INFINITE, 1);
					price += 400;
				}
				break;
			default:
				if (chance < .25) {
					enchantments.put(Enchantment.ARROW_INFINITE, 1);
					price += 400;
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

		ItemStack item = Utils.createItem(Material.BOW, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
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

		ItemStack item = Utils.createItem(Material.CROSSBOW, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
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

		ItemStack item = Utils.createItem(Material.TRIDENT, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
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

		ItemStack item = Utils.createItem(Material.SHIELD, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}

	// Ammo
	public static @NotNull ItemStack arrows() {
		ItemStack item = Utils.createItems(Material.ARROW, 16, null, Utils.format("&2Gems: &a45"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsS() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.SLOWNESS), 8,
				null, Utils.format("&2Gems: &a50"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsD() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.INSTANT_DAMAGE), 8,
				null, Utils.format("&2Gems: &a70"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsW() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.WEAKNESS), 8,
				null, Utils.format("&2Gems: &a50"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsP() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW, new PotionData(PotionType.POISON), 16,
				null, Utils.format("&2Gems: &a60"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsSPlus() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.SLOWNESS, false, true), 8, null,
				Utils.format("&2Gems: &a125"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsDPlus() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.INSTANT_DAMAGE, false, true), 8, null,
				Utils.format("&2Gems: &a175"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsWPlus() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.WEAKNESS, false,true), 8, null,
				Utils.format("&2Gems: &a125"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack arrowsPPlus() {
		ItemStack item = Utils.createPotionItems(Material.TIPPED_ARROW,
				new PotionData(PotionType.POISON, false, true), 16, null,
				Utils.format("&2Gems: &a130"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack rockets() {
		ItemStack item = Utils.createItems(Material.FIREWORK_ROCKET, 4, null,
				Utils.format("&2Gems: &a50"));
		ItemMeta meta = item.getItemMeta();
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
		ItemMeta meta = item.getItemMeta();
		FireworkMeta fireworkMeta = (FireworkMeta) meta;

		List<String> lore = new ArrayList<>();
		lore.add(Utils.format("&2Gems: &a100"));
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
	public static @NotNull ItemStack helmet(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.LEATHER_HELMET;
					price += 55;
				} else if (chance < .8) {
					mat = Material.CHAINMAIL_HELMET;
					price += 140;
				} else {
					mat = Material.IRON_HELMET;
					price += 160;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.CHAINMAIL_HELMET;
					price += 140;
				} else if (chance < .9) {
					mat = Material.IRON_HELMET;
					price += 160;
				} else {
					mat = Material.DIAMOND_HELMET;
					price += 300;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_HELMET;
					price += 160;
				} else if (chance < .85) {
					mat = Material.DIAMOND_HELMET;
					price += 300;
				} else {
					mat = Material.NETHERITE_HELMET;
					price += 375;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_HELMET;
					price += 160;
				} else if (chance < .75) {
					mat = Material.DIAMOND_HELMET;
					price += 300;
				} else {
					mat = Material.NETHERITE_HELMET;
					price += 375;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_HELMET;
					price += 300;
				} else {
					mat = Material.NETHERITE_HELMET;
					price += 375;
				}
				break;
			default:
				mat = Material.NETHERITE_HELMET;
				price += 375;
		}
		chance = r.nextDouble();

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
		
		// Set thorns
		switch (level) {
			case 1:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				}
				break;
			case 3:
				if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 900;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .25) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			case 7:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .5) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .6) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
		}
		chance = r.nextDouble();
		
		// Set protections
		switch (level) {
			case 1:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				break;
			case 2:
				if (chance < .15)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .25)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 3:
				if (chance < .05)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 4:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .4)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				break;
			case 5:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .45)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			case 6:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			default:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
					case 0:
						enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
						price += 375;
						break;
					case 1:
						enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
						price += 250;
						break;
					case 2:
						enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
						price += 175;
						break;
					case 3:
						enchantments.put(Enchantment.PROTECTION_FIRE, 5);
						price += 125;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack chestplate(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.LEATHER_CHESTPLATE;
					price += 200;
				} else if (chance < .8) {
					mat = Material.CHAINMAIL_CHESTPLATE;
					price += 320;
				} else {
					mat = Material.IRON_CHESTPLATE;
					price += 420;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.CHAINMAIL_CHESTPLATE;
					price += 320;
				} else if (chance < .9) {
					mat = Material.IRON_CHESTPLATE;
					price += 420;
				} else {
					mat = Material.DIAMOND_CHESTPLATE;
					price += 550;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_CHESTPLATE;
					price += 420;
				} else if (chance < .85) {
					mat = Material.DIAMOND_CHESTPLATE;
					price += 550;
				} else {
					mat = Material.NETHERITE_CHESTPLATE;
					price += 650;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_CHESTPLATE;
					price += 420;
				} else if (chance < .75) {
					mat = Material.DIAMOND_CHESTPLATE;
					price += 550;
				} else {
					mat = Material.NETHERITE_CHESTPLATE;
					price += 650;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_CHESTPLATE;
					price += 550;
				} else {
					mat = Material.NETHERITE_CHESTPLATE;
					price += 650;
				}
				break;
			default:
				mat = Material.NETHERITE_CHESTPLATE;
				price += 650;
		}
		chance = r.nextDouble();

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

		// Set thorns
		switch (level) {
			case 1:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				}
				break;
			case 3:
				if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 900;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .25) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			case 7:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .5) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .6) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
		}
		chance = r.nextDouble();

		// Set protections
		switch (level) {
			case 1:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				break;
			case 2:
				if (chance < .15)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .25)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 3:
				if (chance < .05)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 4:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .4)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				break;
			case 5:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .45)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			case 6:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			default:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack leggings(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.LEATHER_LEGGINGS;
					price += 120;
				} else if (chance < .8) {
					mat = Material.CHAINMAIL_LEGGINGS;
					price += 250;
				} else {
					mat = Material.IRON_LEGGINGS;
					price += 350;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.CHAINMAIL_LEGGINGS;
					price += 250;
				} else if (chance < .9) {
					mat = Material.IRON_LEGGINGS;
					price += 350;
				} else {
					mat = Material.DIAMOND_LEGGINGS;
					price += 400;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_LEGGINGS;
					price += 350;
				} else if (chance < .85) {
					mat = Material.DIAMOND_LEGGINGS;
					price += 400;
				} else {
					mat = Material.NETHERITE_LEGGINGS;
					price += 475;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_LEGGINGS;
					price += 350;
				} else if (chance < .75) {
					mat = Material.DIAMOND_LEGGINGS;
					price += 400;
				} else {
					mat = Material.NETHERITE_LEGGINGS;
					price += 475;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_LEGGINGS;
					price += 400;
				} else {
					mat = Material.NETHERITE_LEGGINGS;
					price += 475;
				}
				break;
			default:
				mat = Material.NETHERITE_LEGGINGS;
				price += 475;
		}
		chance = r.nextDouble();

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

		// Set thorns
		switch (level) {
			case 1:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				}
				break;
			case 3:
				if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 900;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .25) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			case 7:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .5) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .6) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
		}
		chance = r.nextDouble();

		// Set protections
		switch (level) {
			case 1:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				break;
			case 2:
				if (chance < .15)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .25)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 3:
				if (chance < .05)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 4:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .4)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				break;
			case 5:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .45)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			case 6:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			default:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack boots(int level) {
		Random r = new Random();
		Material mat;
		HashMap<Enchantment, Integer> enchantments = new HashMap<>();
		int price = 0;
		double chance = r.nextDouble();

		// Set material
		switch (level) {
			case 1:
				if (chance < .4) {
					mat = Material.LEATHER_BOOTS;
					price += 45;
				} else if (chance < .8) {
					mat = Material.CHAINMAIL_BOOTS;
					price += 60;
				} else {
					mat = Material.IRON_BOOTS;
					price += 120;
				}
				break;
			case 2:
				if (chance < .4) {
					mat = Material.CHAINMAIL_BOOTS;
					price += 60;
				} else if (chance < .9) {
					mat = Material.IRON_BOOTS;
					price += 120;
				} else {
					mat = Material.DIAMOND_BOOTS;
					price += 275;
				}
				break;
			case 3:
				if (chance < .5) {
					mat = Material.IRON_BOOTS;
					price += 120;
				} else if (chance < .85) {
					mat = Material.DIAMOND_BOOTS;
					price += 275;
				} else {
					mat = Material.NETHERITE_BOOTS;
					price += 325;
				}
				break;
			case 4:
				if (chance < .1) {
					mat = Material.IRON_BOOTS;
					price += 120;
				} else if (chance < .75) {
					mat = Material.DIAMOND_BOOTS;
					price += 275;
				} else {
					mat = Material.NETHERITE_BOOTS;
					price += 325;
				}
				break;
			case 5:
				if (chance < .3) {
					mat = Material.DIAMOND_BOOTS;
					price += 275;
				} else {
					mat = Material.NETHERITE_BOOTS;
					price += 325;
				}
				break;
			default:
				mat = Material.NETHERITE_BOOTS;
				price += 325;
		}
		chance = r.nextDouble();

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

		// Set thorns
		switch (level) {
			case 1:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				}
				break;
			case 2:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				}
				break;
			case 3:
				if (chance < .2) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 900;
				}
				break;
			case 4:
				if (chance < .15) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .3) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 5:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 1);
					price += 45;
				} else if (chance < .25) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				}
				break;
			case 6:
				if (chance < .1) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .45) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			case 7:
				if (chance < .05) {
					enchantments.put(Enchantment.THORNS, 2);
					price += 90;
				} else if (chance < .35) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .5) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
				break;
			default:
				if (chance < .4) {
					enchantments.put(Enchantment.THORNS, 3);
					price += 135;
				} else if (chance < .6) {
					enchantments.put(Enchantment.THORNS, 4);
					price += 180;
				}
		}
		chance = r.nextDouble();

		// Set protections
		switch (level) {
			case 1:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				break;
			case 2:
				if (chance < .15)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .25)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 3:
				if (chance < .05)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
							price += 75;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 1);
							price += 50;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 1);
							price += 35;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 1);
							price += 25;
					}
				else if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				break;
			case 4:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
							price += 150;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 2);
							price += 100;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 2);
							price += 70;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 2);
							price += 50;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .4)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				break;
			case 5:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .35)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .45)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			case 6:
				if (chance < .1)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
							price += 225;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 3);
							price += 150;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 3);
							price += 105;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 3);
							price += 75;
					}
				else if (chance < .3)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
					}
				break;
			default:
				if (chance < .2)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
							price += 300;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
							price += 200;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
							price += 140;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 4);
							price += 100;
					}
				else if (chance < .5)
					switch (r.nextInt(4)) {
						case 0:
							enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
							price += 375;
							break;
						case 1:
							enchantments.put(Enchantment.PROTECTION_PROJECTILE, 5);
							price += 250;
							break;
						case 2:
							enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 5);
							price += 175;
							break;
						case 3:
							enchantments.put(Enchantment.PROTECTION_FIRE, 5);
							price += 125;
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

		ItemStack item = Utils.createItem(mat, null, FLAGS, enchantments,
				Utils.format("&2Gems: &a" + price));

		return item == null ? new ItemStack(Material.AIR) : item;
	}

	// Consumables
	public static @NotNull ItemStack totem() {
		ItemStack item = Utils.createItem(Material.TOTEM_OF_UNDYING, null, Utils.format("&2Gems: &a1000"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack gapple() {
		ItemStack item = Utils.createItem(Material.GOLDEN_APPLE, null, Utils.format("&2Gems: &a120"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack egapple() {
		ItemStack item = Utils.createItem(Material.ENCHANTED_GOLDEN_APPLE, null, Utils.format("&2Gems: &a300"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack gcarrot() {
		ItemStack item = Utils.createItem(Material.GOLDEN_CARROT, null, Utils.format("&2Gems: &a80"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack steak() {
		ItemStack item = Utils.createItems(Material.COOKED_BEEF, 2, null, Utils.format("&2Gems: &a60"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack mutton() {
		ItemStack item = Utils.createItems(Material.COOKED_MUTTON, 2, null,
				Utils.format("&2Gems: &a40"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack bread() {
		ItemStack item = Utils.createItems(Material.BREAD, 3, null, Utils.format("&2Gems: &a40"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack carrot() {
		ItemStack item = Utils.createItems(Material.CARROT, 5, null, Utils.format("&2Gems: &a30"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack beetroot() {
		ItemStack item = Utils.createItems(Material.BEETROOT, 8, null, Utils.format("&2Gems: &a25"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack health() {
		ItemStack item = Utils.createPotionItem(Material.POTION, new PotionData(PotionType.INSTANT_HEAL), null,
				Utils.format("&2Gems: &a50"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack health2() {
		ItemStack item = Utils.createPotionItem(Material.POTION,
				new PotionData(PotionType.INSTANT_HEAL, false, true), null,
				Utils.format("&2Gems: &a120"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack health3() {
		ItemStack item = Utils.createPotionItem(Material.LINGERING_POTION,
				new PotionData(PotionType.INSTANT_HEAL, false, true), null,
				Utils.format("&2Gems: &a200"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack strength() {
		ItemStack item = Utils.createPotionItem(Material.POTION, new PotionData(PotionType.STRENGTH), null,
				Utils.format("&2Gems: &a150"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack strength2() {
		ItemStack item = Utils.createPotionItem(Material.POTION,
				new PotionData(PotionType.STRENGTH, false, true), null,
				Utils.format("&2Gems: &a400"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack regen() {
		ItemStack item = Utils.createPotionItem(Material.POTION, new PotionData(PotionType.REGEN), null,
				Utils.format("&2Gems: &a175"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack regen2() {
		ItemStack item = Utils.createPotionItem(Material.POTION,
				new PotionData(PotionType.REGEN, false, true), null,
				Utils.format("&2Gems: &a450"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack speed() {
		ItemStack item = Utils.createPotionItem(Material.POTION, new PotionData(PotionType.SPEED), null,
				Utils.format("&2Gems: &a125"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack speed2() {
		ItemStack item = Utils.createPotionItem(Material.POTION, new PotionData(PotionType.SPEED, false, true),
				null, Utils.format("&2Gems: &a350"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack milk() {
		ItemStack item = Utils.createItem(Material.MILK_BUCKET, null, Utils.format("&2Gems: &a75"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack golem() {
		ItemStack item = Utils.createItem(Material.GHAST_SPAWN_EGG, Utils.format("&fIron Golem Spawn Egg"),
				Utils.format("&2Gems: &a500"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack wolf() {
		ItemStack item = Utils.createItem(Material.WOLF_SPAWN_EGG, null, Utils.format("&2Gems: &a250"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack smallCare() {
		ItemStack item = Utils.createItem(Material.COAL_BLOCK, Utils.format("&2Small Care Package"),
				Utils.format("&7Contents:"), Utils.format("&7 - &bOne &7level &b1 &7weapon"),
				Utils.format("&7 - &bOne &7level &b1 &7armor"), Utils.format("&2Gems: &a200"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack mediumCare() {
		ItemStack item = Utils.createItem(Material.IRON_BLOCK, Utils.format("&3Medium Care Package"),
				Utils.format("&7Contents:"), Utils.format("&7 - &bOne &7level &b2 &7weapon"),
				Utils.format("&7 - &bOne &7level &b2 &7armor"),
				Utils.format("&7 - &bOne &7level &b2 &7consumable"), Utils.format("&2Gems: &a500"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack largeCare() {
		ItemStack item = Utils.createItem(Material.DIAMOND_BLOCK, Utils.format("&9Large Care Package"),
				Utils.format("&7Contents:"), Utils.format("&7 - &bOne &7level &b4 &7weapon"),
				Utils.format("&7 - &bTwo &7level &b3 &7armor"),
				Utils.format("&7 - &bOne &7level &b3 &7consumable"), Utils.format("&2Gems: &a1200"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack extraCare() {
		ItemStack item = Utils.createItem(Material.BEACON, Utils.format("&bExtra Large Care Package"),
				Utils.format("&7Contents:"), Utils.format("&7 - &bOne &7level &b5 &7weapon"),
				Utils.format("&7 - &bOne &7level &b4 &7weapon"),
				Utils.format("&7 - &bOne &7level &b5 &7armor"),
				Utils.format("&7 - &bOne &7level &b4 &7armor"),
				Utils.format("&7 - &bTwo &7level &b4 &7consumables"), Utils.format("&2Gems: &a3000"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack experience() {
		ItemStack item = Utils.createItem(Material.EXPERIENCE_BOTTLE, null, Utils.format("&2Gems: &a75"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}

	// Kit abilities
	public static @NotNull ItemStack mage() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.PURPLE_DYE, Utils.format("&dMage Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack ninja() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.BLACK_DYE, Utils.format("&dNinja Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack templar() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.YELLOW_DYE, Utils.format("&dTemplar Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack warrior() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.RED_DYE, Utils.format("&dWarrior Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack knight() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.BROWN_DYE, Utils.format("&dKnight Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack priest() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.WHITE_DYE, Utils.format("&dPriest Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack siren() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.PINK_DYE, Utils.format("&dSiren Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack monk() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.GREEN_DYE, Utils.format("&dMonk Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}
	public static @NotNull ItemStack messenger() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		ItemStack item = Utils.createItem(Material.BLUE_DYE, Utils.format("&dMessenger Essence"), Utils.HIDE_ENCHANT_FLAGS,
				enchants, Utils.format("&7Right click to use ability"));

		return item == null ? new ItemStack(Material.AIR) : item;
	}

	// Categories of items
	public static final ItemStack[] ABILITY_ITEMS = new ItemStack[]{mage(), ninja(), templar(), warrior(), knight(),
			priest(), siren(), monk(), messenger()};
	public static final Material[] FOOD_MATERIALS = new Material[]{Material.BEETROOT, Material.CARROT, Material.BREAD,
			Material.MUTTON, Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GOLDEN_APPLE,
			Material.ENCHANTED_GOLDEN_APPLE};
	public static final Material[] HELMET_MATERIALS = {Material.LEATHER_HELMET, Material.GOLDEN_HELMET,
			Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
			Material.TURTLE_HELMET};
	public static final Material[] CHESTPLATE_MATERIALS = {Material.LEATHER_CHESTPLATE, Material.GOLDEN_CHESTPLATE,
			Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
			Material.NETHERITE_HELMET};
	public static final Material[] LEGGING_MATERIALS = {Material.LEATHER_LEGGINGS, Material.GOLDEN_LEGGINGS,
			Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS
	};
	public static final Material[] BOOTS_MATERIALS = {Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS,
			Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
	};
	public static final Material[] ARMOR_MATERIALS = new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
			Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
			Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE,
			Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS};
	public static final Material[] CARE_MATERIALS = new Material[]{Material.COAL_BLOCK, Material.IRON_BLOCK,
			Material.DIAMOND_BLOCK, Material.BEACON};
	public static final Material[] CLICKABLE_WEAPON_MATERIALS = new Material[]{Material.BOW, Material.CROSSBOW,
			Material.TRIDENT};
	public static final Material[] CLICKABLE_CONSUME_MATERIALS = new Material[]{Material.GLASS_BOTTLE,
			Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION, Material.EXPERIENCE_BOTTLE,
			Material.MILK_BUCKET, Material.GHAST_SPAWN_EGG, Material.WOLF_SPAWN_EGG};

	// Random generation of items
	public static @NotNull ItemStack randWeapon(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				if (chance < .4)
					return sword(level);
				else if (chance < .75)
					return axe(level);
				else if (chance < .9)
					return bow(level);
				else return arrows();
			case 2:
				if (chance < .35)
					return sword(level);
				else if (chance < .7)
					return axe(level);
				else if (chance < .8)
					return shield(level);
				else if (chance < .9)
					return bow(level);
				else if (chance < .96)
					return arrows();
				else return arrowsP();
			case 3:
				if (chance < .3)
					return sword(level);
				else if (chance < .6)
					return axe(level);
				else if (chance < .7)
					return bow(level);
				else if (chance < .8)
					return shield(level);
				else if (chance < .9)
					return crossbow(level);
				else if (chance < .93)
					return arrows();
				else if (chance < .96)
					return arrowsP();
				else if (chance < .98)
					return arrowsS();
				else if (chance < .99)
					return arrowsW();
				else return arrowsD();
			case 4:
				if (chance < .3)
					return sword(level);
				else if (chance < .55)
					return axe(level);
				else if (chance < .65)
					return bow(level);
				else if (chance < .75)
					return shield(level);
				else if (chance < .85)
					return crossbow(level);
				else if (chance < .9)
					return arrows();
				else if (chance < .93)
					return arrowsPPlus();
				else if (chance < .95)
					return arrowsS();
				else if (chance < .97)
					return arrowsW();
				else if (chance < .99)
					return arrowsD();
				else return rockets();
			case 5:
				if (chance < .2)
					return sword(level);
				else if (chance < .4)
					return axe(level);
				else if (chance < .5)
					return shield(level);
				else if (chance < .6)
					return bow(level);
				else if (chance < .7)
					return crossbow(level);
				else if (chance < .8)
					return trident(level);
				else if (chance < .85)
					return arrows();
				else if (chance < .89)
					return arrowsPPlus();
				else if (chance < .92)
					return arrowsSPlus();
				else if (chance < .94)
					return arrowsWPlus();
				else if (chance < .96)
					return arrowsD();
				else if (chance < .98)
					return rockets();
				else return rocketsPlus();
			default:
				if (chance < .2)
					return sword(level);
				else if (chance < .4)
					return axe(level);
				else if (chance < .5)
					return shield(level);
				else if (chance < .6)
					return bow(level);
				else if (chance < .7)
					return crossbow(level);
				else if (chance < .8)
					return trident(level);
				else if (chance < .85)
					return arrows();
				else if (chance < .89)
					return arrowsPPlus();
				else if (chance < .92)
					return arrowsSPlus();
				else if (chance < .95)
					return arrowsWPlus();
				else if (chance < .98)
					return arrowsDPlus();
				else return rocketsPlus();
		}
	}
	public static @NotNull ItemStack randRange(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				return bow(level);
			case 2:
				if (chance < .5)
					return bow(level);
				else if (chance < .75)
					return crossbow(level);
				else return shield(level);
			case 3:
				if (chance < .33)
					return bow(level);
				else if (chance < .67)
					return crossbow(level);
				else return shield(level);
			case 4:
				if (chance < .3)
					return bow(level);
				else if (chance < .6)
					return crossbow(level);
				else if (chance < .9)
					return shield(level);
				else return trident(level);
			default:
				if (chance < .25)
					return bow(level);
				else if (chance < .5)
					return crossbow(level);
				else if (chance < .75)
					return shield(level);
				else return trident(level);
		}
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
	public static @NotNull ItemStack randArmor(int level) {
		Random r = new Random();
		int chance = r.nextInt(4);
		switch (chance) {
			case 0:
				return helmet(level);
			case 1:
				return chestplate(level);
			case 2:
				return leggings(level);
			default:
				return boots(level);
		}

	}
	public static @NotNull ItemStack randConsumable(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				if (chance < .25)
					return beetroot();
				else if (chance < .5)
					return carrot();
				else if (chance < .65)
					return bread();
				else if (chance < .725)
					return health();
				else if (chance < .775)
					return speed();
				else if (chance < .85)
					return wolf();
				else if (chance < .9)
					return experience();
				else return smallCare();
			case 2:
				if (chance < .15)
					return carrot();
				else if (chance < .3)
					return bread();
				else if (chance < .4)
					return mutton();
				else if (chance < .45)
					return steak();
				else if (chance < .475)
					return milk();
				else if (chance < .55)
					return health();
				else if (chance < .6)
					return speed();
				else if (chance < .65)
					return strength();
				else if (chance < .7)
					return regen();
				else if (chance < .775)
					return wolf();
				else if (chance < .825)
					return experience();
				else if (chance < .9)
					return smallCare();
				else return mediumCare();
			case 3:
				if (chance < .1)
					return bread();
				else if (chance < .2)
					return mutton();
				else if (chance < .3)
					return steak();
				else if (chance < .4)
					return gcarrot();
				else if (chance < .425)
					return milk();
				else if (chance < .475)
					return health2();
				else if (chance < .525)
					return speed2();
				else if (chance < .575)
					return strength();
				else if (chance < .625)
					return regen();
				else if (chance < .725)
					return wolf();
				else if (chance < .775)
					return golem();
				else if (chance < .825)
					return experience();
				else if (chance < .875)
					return smallCare();
				else if (chance < .925)
					return mediumCare();
				else return largeCare();
			case 4:
				if (chance < .1)
					return mutton();
				else if (chance < .2)
					return steak();
				else if (chance < .3)
					return gcarrot();
				else if (chance < .4)
					return gapple();
				else if (chance < .42)
					return milk();
				else if (chance < .45)
					return health2();
				else if (chance < .475)
					return health3();
				else if (chance < .525)
					return speed2();
				else if (chance < .575)
					return strength2();
				else if (chance < .625)
					return regen2();
				else if (chance < .7)
					return wolf();
				else if (chance < .775)
					return golem();
				else if (chance < .825)
					return experience();
				else if (chance < .85)
					return mediumCare();
				else if (chance < .925)
					return largeCare();
				else return extraCare();
			case 5:
				if (chance < .05)
					return mutton();
				else if (chance < .15)
					return steak();
				else if (chance < .25)
					return gcarrot();
				else if (chance < .35)
					return gapple();
				else if (chance < .4)
					return egapple();
				else if (chance < .45)
					return totem();
				else if (chance < .47)
					return milk();
				else if (chance < .5)
					return health2();
				else if (chance < .525)
					return health3();
				else if (chance < .575)
					return speed2();
				else if (chance < .625)
					return strength2();
				else if (chance < .675)
					return regen2();
				else if (chance < .75)
					return wolf();
				else if (chance < .825)
					return golem();
				else if (chance < .875)
					return experience();
				else if (chance < .925)
					return largeCare();
				else return extraCare();
			default:
				if (chance < .05)
					return steak();
				else if (chance < .2)
					return gcarrot();
				else if (chance < .3)
					return gapple();
				else if (chance < .4)
					return egapple();
				else if (chance < .45)
					return totem();
				else if (chance < .47)
					return milk();
				else if (chance < .5)
					return health2();
				else if (chance < .525)
					return health3();
				else if (chance < .575)
					return speed2();
				else if (chance < .625)
					return strength2();
				else if (chance < .675)
					return regen2();
				else if (chance < .75)
					return wolf();
				else if (chance < .825)
					return golem();
				else if (chance < .875)
					return experience();
				else if (chance < .925)
					return largeCare();
				else return extraCare();
		}
	}
	public static @NotNull ItemStack randFood(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				if (chance < .4)
					return beetroot();
				else if (chance < .8)
					return carrot();
				else return bread();
			case 2:
				if (chance < .325)
					return carrot();
				else if (chance < .65)
					return bread();
				else if (chance < .85)
					return mutton();
				else return steak();
			case 3:
				if (chance < .25)
					return bread();
				else if (chance < .5)
					return mutton();
				else if (chance < .75)
					return steak();
				else return gcarrot();
			case 4:
				if (chance < .25)
					return mutton();
				else if (chance < .5)
					return steak();
				else if (chance < .75)
					return gcarrot();
				else return gapple();
			default:
				if (chance < .15)
					return mutton();
				else if (chance < .4)
					return steak();
				else if (chance < .65)
					return gcarrot();
				else if (chance < .9)
					return gapple();
				else return egapple();
		}
	}
	public static @NotNull ItemStack randOther(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				if (chance < .35)
					return smallCare();
				else if (chance < .65)
					return wolf();
				else if (chance < .75)
					return experience();
				else if (chance < .9)
					return health();
				else return speed();
			case 2:
				if (chance < .15)
					return smallCare();
				else if (chance < .45)
					return mediumCare();
				else if (chance < .6)
					return wolf();
				else if (chance < .7)
					return experience();
				else if (chance < .75)
					return milk();
				else if (chance < .85)
					return health();
				else if (chance < .9)
					return speed();
				else if (chance < .95)
					return strength();
				else return regen();
			case 3:
				if (chance < .15)
					return mediumCare();
				else if (chance < .4)
					return largeCare();
				else if (chance < .5)
					return wolf();
				else if (chance < .6)
					return golem();
				else if (chance < .7)
					return experience();
				else if (chance < .75)
					return milk();
				else if (chance < .85)
					return health2();
				else if (chance < .9)
					return speed2();
				else if (chance < .95)
					return strength();
				else return regen();
			case 4:
				if (chance < .15)
					return largeCare();
				else if (chance < .4)
					return extraCare();
				else if (chance < .5)
					return wolf();
				else if (chance < .6)
					return golem();
				else if (chance < .7)
					return experience();
				else if (chance < .75)
					return milk();
				else if (chance < .85)
					return health2();
				else if (chance < .9)
					return speed2();
				else if (chance < .95)
					return strength2();
				else return regen2();
			default:
				if (chance < .3)
					return extraCare();
				else if (chance < .45)
					return wolf();
				else if (chance < .6)
					return golem();
				else if (chance < .7)
					return experience();
				else if (chance < .75)
					return milk();
				else if (chance < .85)
					return health2();
				else if (chance < .9)
					return speed2();
				else if (chance < .95)
					return strength2();
				else return regen2();
		}
	}
	public static @NotNull ItemStack randCare(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				return smallCare();
			case 2:
				if (chance < .35)
					return smallCare();
				else return mediumCare();
			case 3:
				if (chance < .35)
					return mediumCare();
				else return largeCare();
			case 4:
				if (chance < .35)
					return largeCare();
				else return extraCare();
			default:
				return extraCare();
		}
	}
	public static @NotNull ItemStack randNotCare(int level) {
		Random r = new Random();
		double chance = r.nextDouble();
		switch (level) {
			case 1:
				if (chance < .25)
					return beetroot();
				else if (chance < .5)
					return carrot();
				else if (chance < .7)
					return bread();
				else if (chance < .8)
					return health();
				else if (chance < .85)
					return speed();
				else if (chance < .95)
					return wolf();
				else return experience();
			case 2:
				if (chance < .2)
					return carrot();
				else if (chance < .35)
					return bread();
				else if (chance < .475)
					return mutton();
				else if (chance < .55)
					return steak();
				else if (chance < .575)
					return milk();
				else if (chance < .625)
					return health();
				else if (chance < .675)
					return speed();
				else if (chance < .725)
					return strength();
				else if (chance < .775)
					return regen();
				else if (chance < .9)
					return wolf();
				else return experience();
			case 3:
				if (chance < .125)
					return bread();
				else if (chance < .25)
					return mutton();
				else if (chance < .35)
					return steak();
				else if (chance < .45)
					return gcarrot();
				else if (chance < .475)
					return milk();
				else if (chance < .525)
					return health2();
				else if (chance < .575)
					return speed2();
				else if (chance < .65)
					return strength();
				else if (chance < .7)
					return regen();
				else if (chance < .8)
					return wolf();
				else if (chance < .9)
					return golem();
				else return experience();
			case 4:
				if (chance < .1)
					return mutton();
				else if (chance < .225)
					return steak();
				else if (chance < .35)
					return gcarrot();
				else if (chance < .45)
					return gapple();
				else if (chance < .47)
					return milk();
				else if (chance < .52)
					return health2();
				else if (chance < .55)
					return health3();
				else if (chance < .6)
					return speed2();
				else if (chance < .65)
					return strength2();
				else if (chance < .7)
					return regen2();
				else if (chance < .8)
					return wolf();
				else if (chance < .9)
					return golem();
				else return experience();
			case 5:
				if (chance < .05)
					return mutton();
				else if (chance < .15)
					return steak();
				else if (chance < .25)
					return gcarrot();
				else if (chance < .35)
					return gapple();
				else if (chance < .4)
					return egapple();
				else if (chance < .45)
					return totem();
				else if (chance < .475)
					return milk();
				else if (chance < .55)
					return health2();
				else if (chance < .6)
					return health3();
				else if (chance < .65)
					return speed2();
				else if (chance < .7)
					return strength2();
				else if (chance < .75)
					return regen2();
				else if (chance < .85)
					return wolf();
				else if (chance < .925)
					return golem();
				else return experience();
			default:
				if (chance < .05)
					return steak();
				else if (chance < .2)
					return gcarrot();
				else if (chance < .3)
					return gapple();
				else if (chance < .4)
					return egapple();
				else if (chance < .45)
					return totem();
				else if (chance < .47)
					return milk();
				else if (chance < .52)
					return health2();
				else if (chance < .55)
					return health3();
				else if (chance < .6)
					return speed2();
				else if (chance < .65)
					return strength2();
				else if (chance < .7)
					return regen2();
				else if (chance < .8)
					return wolf();
				else if (chance < .9)
					return golem();
				else return experience();
		}
	}
}
