package me.theguyhere.villagerdefense.game;

import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class GameItems {
	// Shop
	public static ItemStack shop() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {true, false};
		enchants.put(Enchantment.DURABILITY, 1);

		return Utils.createItem(Material.GOLD_INGOT, Utils.format("&2&lItem Shop"), flags, enchants, "", Utils.format("&7&oResets every 10 rounds"));
	}

	// Swords
	public static ItemStack wSword() {
		return Utils.createItem(Material.WOODEN_SWORD, null, Utils.format("&2Gems: &a  50"));
	}
	public static ItemStack wSwordK2() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		boolean[] flags = {false, false};
		enchants.put(Enchantment.KNOCKBACK, 2);
		
		return Utils.createItem(Material.WOODEN_SWORD, null, flags, enchants, Utils.format("&2Gems: &a 110"));
	}
	public static ItemStack wSwordU2() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 2);
		boolean[] flags = {false, false};

		return Utils.createItem(Material.WOODEN_SWORD, null, flags, enchants, Utils.format("&2Gems: &a 100"));
	}
	public static ItemStack wSwordSh2() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DAMAGE_ALL, 2);
		boolean[] flags = {false, false};

		return Utils.createItem(Material.WOODEN_SWORD, null, flags, enchants, Utils.format("&2Gems: &a 200"));
	}
	public static ItemStack wSwordSm2() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DAMAGE_UNDEAD, 2);
		boolean[] flags = {false, false};

		return Utils.createItem(Material.WOODEN_SWORD, null, flags, enchants, Utils.format("&2Gems: &a 170"));
	}
	public static ItemStack sSword() {
		return Utils.createItem(Material.STONE_SWORD, null, Utils.format("&2Gems: &a 120"));
	}
	public static ItemStack sSwordK1Sh1() {
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 225");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sSwordU1Sm1() {
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 205");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sSwordSh2() {
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 270");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sSwordSm3() {
		ItemStack item = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 300");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iSword() {
		return Utils.createItem(Material.IRON_SWORD, null, Utils.format("&2Gems: &a 250"));
	}
	public static ItemStack iSwordK2() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 310");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iSwordU2Sm1() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 370");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iSwordSh2U1() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 425");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iSwordF1K1() {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 380");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dSword() {
		return Utils.createItem(Material.DIAMOND_SWORD, null, Utils.format("&2Gems: &a 500"));
	}
	public static ItemStack dSwordK2() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 560");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dSwordU2Sh2() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 700");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dSwordF1K1Sw1() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 680");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dSwordSm3K1() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 710");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dSwordM1Sm1() {
		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 810");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nSword() {
		return Utils.createItem(Material.NETHERITE_SWORD, null, Utils.format("&2Gems: &a 700"));
	}
	public static ItemStack nSwordK3() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 790");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nSwordU3Sh2() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 925");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nSwordF2K2Sh1() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1035");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nSwordSm4Sw2() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1040");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 4, true);
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nSwordLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1775");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
		meta.addEnchant(Enchantment.MENDING, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack ripperSword() {
		ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1250");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 15, true);
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 4, true);
		
		item.setItemMeta(meta);
		
		return item;
	}

//	Axes
	public static ItemStack wAxe() {
		return Utils.createItem(Material.WOODEN_AXE, null, Utils.format("&2Gems: &a  40"));
	}
	public static ItemStack wAxeU2() {
		ItemStack item = new ItemStack(Material.WOODEN_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  90");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack wAxeSh2() {
		ItemStack item = new ItemStack(Material.WOODEN_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 190");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack wAxeSm2() {
		ItemStack item = new ItemStack(Material.WOODEN_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 160");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sAxe() {
		ItemStack item = new ItemStack(Material.STONE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 100");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sAxeU1Sm1() {
		ItemStack item = new ItemStack(Material.STONE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 185");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sAxeSh2() {
		ItemStack item = new ItemStack(Material.STONE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 250");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack sAxeSm3() {
		ItemStack item = new ItemStack(Material.STONE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 280");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iAxe() {
		ItemStack item = new ItemStack(Material.IRON_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 220");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iAxeSh2U1() {
		ItemStack item = new ItemStack(Material.IRON_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 395");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iAxeSm2K1() {
		ItemStack item = new ItemStack(Material.IRON_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 370");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 2, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iAxeF1K1() {
		ItemStack item = new ItemStack(Material.IRON_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 350");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxe() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 480");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxeK2() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 540");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxeU2Sh2() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 680");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxeF2K1() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 710");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxeSm3K1() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 690");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dAxeM1Sm1() {
		ItemStack item = new ItemStack(Material.DIAMOND_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 790");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxe() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 700");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxeK3() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 790");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxeU3Sh2() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 925");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxeF2K2Sh1() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1035");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxeSm5() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1000");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_UNDEAD, 5, false);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nAxeLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1835");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
		meta.addEnchant(Enchantment.FIRE_ASPECT, 3, false);
		meta.addEnchant(Enchantment.DURABILITY, 3, false);
		meta.addEnchant(Enchantment.MENDING, 1, true);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack shredderAxe() {
		ItemStack item = new ItemStack(Material.GOLDEN_AXE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1250");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 25, false);
		
		item.setItemMeta(meta);
		
		return item;
	}

//	Bows
	public static ItemStack bow() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 150");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack bowPo2() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 300");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack bowPo1Pu1() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 255");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
		meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack bowPo4() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 450");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack bowPu3U2F() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 390");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 3, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack bowLoaded() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1160");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);
		meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack strongBow() {
		ItemStack item = new ItemStack(Material.BOW);
		ItemMeta meta = item.getItemMeta();
		Damageable dam = (Damageable) meta;

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1000");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.ARROW_DAMAGE, 10, false);
		meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 3, true);
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, true);
		dam.setDamage(325);

		item.setItemMeta(meta);
		
		return item;
	}
	
//	Crossbows
	public static ItemStack cbow() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 300");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowQ2() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 420");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.QUICK_CHARGE, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowPi5() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 550");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PIERCING, 5, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowMu() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 420");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.MULTISHOT, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowMu3U2() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 710");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.MULTISHOT, 3, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowQ4Mu() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 660");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.QUICK_CHARGE, 4, false);
		meta.addEnchant(Enchantment.MULTISHOT, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cbowLoaded() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1100");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PIERCING, 5, true);
		meta.addEnchant(Enchantment.QUICK_CHARGE, 5, false);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack shotgun() {
		ItemStack item = new ItemStack(Material.CROSSBOW);
		ItemMeta meta = item.getItemMeta();
		Damageable dam = (Damageable) meta;

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1000");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.MULTISHOT, 5, false);
		meta.addEnchant(Enchantment.PIERCING, 5, true);
		dam.setDamage(276);

		item.setItemMeta(meta);
		
		return item;
	}
	
//	Tridents
	public static ItemStack trident() {
		ItemStack item = new ItemStack(Material.TRIDENT);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 800");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LOYALTY, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack tridentK2() {
		ItemStack item = new ItemStack(Material.TRIDENT);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 860");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LOYALTY, 1, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 2, false);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack tridentSh2() {
		ItemStack item = new ItemStack(Material.TRIDENT);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 950");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LOYALTY, 1, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 2, false);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack tridentSh5() {
		ItemStack item = new ItemStack(Material.TRIDENT);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1200");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.LOYALTY, 2, true);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 5, false);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack tridentLoaded() {
		ItemStack item = new ItemStack(Material.TRIDENT);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1650");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DAMAGE_ALL, 7, false);
		meta.addEnchant(Enchantment.LOYALTY, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	
//	Arrows
	public static ItemStack arrows() {
		ItemStack item = new ItemStack(Material.ARROW, 16);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  45");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack arrowsS() {
		ItemStack item = new ItemStack(Material.SPECTRAL_ARROW, 8);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  30");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack arrowsD() {
		ItemStack item = new ItemStack(Material.TIPPED_ARROW, 8);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  70");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack arrowsW() {
		ItemStack item = new ItemStack(Material.TIPPED_ARROW, 8);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  50");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.WEAKNESS));

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack arrowsP() {
		ItemStack item = new ItemStack(Material.TIPPED_ARROW, 8);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  40");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.POISON));

		item.setItemMeta(meta);
		
		return item;
	}

//	Helmets
	public static ItemStack lHelmet() {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  55");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack lHelmetP2() {
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 205");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cHelmet() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 140");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cHelmetU2() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 190");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cHelmetPp3() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 290");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iHelmet() {
		ItemStack item = new ItemStack(Material.IRON_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 140");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iHelmetP3() {
		ItemStack item = new ItemStack(Material.IRON_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 365");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iHelmetT2() {
		ItemStack item = new ItemStack(Material.IRON_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 230");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dHelmet() {
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 300");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dHelmetB4() {
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 440");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dHelmetP2() {
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 450");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dHelmetPp3T2() {
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 540");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dHelmetF4U3() {
		ItemStack item = new ItemStack(Material.DIAMOND_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 475");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 4, true);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nHelmet() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 375");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nHelmetP4() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 675");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nHelmetT3M() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 760");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nHelmetPp5U2() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 675");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 5, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nHelmetLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1135");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, false);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}

//	Chestplates
	public static ItemStack lChestplate() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 200");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack lChestplateP2() {
		ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 350");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cChestplate() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 320");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cChestplateU2() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 370");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cChestplatePp3() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 470");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iChestplate() {
		ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 420");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iChestplateP3() {
		ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 645");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iChestplateT2() {
		ItemStack item = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 510");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dChestplate() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 550");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dChestplateB4() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 690");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dChestplateP2() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 700");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dChestplatePp3T2() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 790");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dChestplateF4U3() {
		ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 725");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 4, true);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nChestplate() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 650");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nChestplateP4() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 950");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nChestplateT3M() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1035");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nChestplatePp5U2() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 950");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 5, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nChestplateLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1410");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, false);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}

//	Leggings
	public static ItemStack lLeggings() {
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 120");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack lLeggingsP2() {
		ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 290");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cLeggings() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 250");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cLeggingsU2() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 300");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cLeggingsPp3() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 400");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iLeggings() {
		ItemStack item = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 350");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iLeggingsP3() {
		ItemStack item = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 575");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iLeggingsT2() {
		ItemStack item = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 460");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dLeggings() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 400");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dLeggingsB4() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 540");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dLeggingsP2() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 550");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dLeggingsPp3T2() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 640");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dLeggingsF4U3() {
		ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 575");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 4, true);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nLeggings() {
		ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 475");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nLeggingsP4() {
		ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 775");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nLeggingsT3M() {
		ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 860");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nLeggingsPp5U2() {
		ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 775");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 5, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nLeggingsLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1235");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, false);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}

//	Boots
	public static ItemStack lBoots() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  45");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack lBootsP2() {
		ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 195");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cBoots() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  60");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cBootsU2() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 110");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack cBootsPp3() {
		ItemStack item = new ItemStack(Material.CHAINMAIL_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 210");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iBoots() {
		ItemStack item = new ItemStack(Material.IRON_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 120");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iBootsP3() {
		ItemStack item = new ItemStack(Material.IRON_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 345");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack iBootsT2() {
		ItemStack item = new ItemStack(Material.IRON_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 210");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dBoots() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 275");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dBootsB4() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 415");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dBootsP2() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 425");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dBootsPp3T2() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 515");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
		meta.addEnchant(Enchantment.THORNS, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack dBootsF4U3() {
		ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 450");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_FIRE, 4, true);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nBoots() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 325");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nBootsP4() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 625");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nBootsT3M() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 710");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nBootsPp5U2() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 625");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_PROJECTILE, 5, false);
		meta.addEnchant(Enchantment.DURABILITY, 2, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack nBootsLoaded() {
		ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "1085");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, false);
		meta.addEnchant(Enchantment.THORNS, 3, true);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	
//	Shields
	public static ItemStack shield() {
		return Utils.createItem(Material.SHIELD, null, Utils.format("&2Gems: &a 500"));
	}
	public static ItemStack shieldU1() {
		ItemStack item = new ItemStack(Material.SHIELD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 600");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack shieldU3() {
		ItemStack item = new ItemStack(Material.SHIELD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 800");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.DURABILITY, 3, true);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack shieldM() {
		ItemStack item = new ItemStack(Material.SHIELD);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 900");
		meta.setLore(lore);
		meta.addEnchant(Enchantment.MENDING, 1, true);

		item.setItemMeta(meta);
		
		return item;
	}
	
//	Consumables
	public static ItemStack totem() {
		return Utils.createItem(Material.TOTEM_OF_UNDYING, null, Utils.format("&2Gems: &a 350"));
	}
	public static ItemStack gapple() {
		return Utils.createItem(Material.GOLDEN_APPLE, null, Utils.format("&2Gems: &a  75"));
	}
	public static ItemStack egapple() {
		return Utils.createItem(Material.ENCHANTED_GOLDEN_APPLE, null, Utils.format("&2Gems: &a 175"));
	}
	public static ItemStack gcarrot() {
		return Utils.createItem(Material.GOLDEN_CARROT, null, Utils.format("&2Gems: &a  60"));
	}
	public static ItemStack steak() {
		return Utils.createItem(Material.COOKED_BEEF, null, Utils.format("&2Gems: &a  30"));
	}
	public static ItemStack bread() {
		ItemStack item = new ItemStack(Material.BREAD, 3);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  45");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack carrot() {
		ItemStack item = new ItemStack(Material.CARROT, 4);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  35");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack beetroot() {
		ItemStack item = new ItemStack(Material.BEETROOT, 5);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  25");
		meta.setLore(lore);

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack health() {
		ItemStack item = new ItemStack(Material.SPLASH_POTION);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;
		
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  50");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack strength() {
		ItemStack item = new ItemStack(Material.POTION);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + " 100");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.STRENGTH));

		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack speed() {
		ItemStack item = new ItemStack(Material.POTION);
		ItemMeta meta = item.getItemMeta();
		PotionMeta pot = (PotionMeta) meta;

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_GREEN + "Gems: " + ChatColor.GREEN + "  75");
		meta.setLore(lore);
		pot.setBasePotionData(new PotionData(PotionType.SPEED));

		item.setItemMeta(meta);
		
		return item;
	}


//	All lists for weapon section
	public static ItemStack[] weapon0 = {
			wSword(), wSwordK2(), wSwordU2(), wSwordSh2(), wSwordSm2(), sSword(), sSwordK1Sh1(), sSwordU1Sm1(), sSwordSh2(), sSwordSm3(),
			iSword(), wAxe(), wAxeU2(), wAxeSh2(), wAxeSm2(), sAxe(), sAxeU1Sm1(), sAxeSh2(), sAxeSm3(), iAxe(), arrows(), bow()
		};
	public static ItemStack[] weapon1 = {
			wSwordSh2(), sSwordK1Sh1(), sSwordU1Sm1(), sSwordSh2(), sSwordSm3(), iSword(), iSwordK2(), iSwordU2Sm1(), iSwordSh2U1(),
			iSwordF1K1(), dSword(), sAxeSh2(), sAxeSm3(), iAxe(), iAxeSh2U1(), iAxeSm2K1(), iAxeF1K1(), dAxe(), arrows(), bowPo2(), bowPo1Pu1(), bowPo4(),
			bowPu3U2F(), cbow(), cbowQ2(), cbowMu(), arrowsS(), arrowsP()
		};
	public static ItemStack[] weapon2 = {
			sSwordSm3(), iSwordK2(), iSwordU2Sm1(), iSwordSh2U1(), iSwordF1K1(), dSword(), dSwordK2(), dSwordU2Sh2(), dSwordF1K1Sw1(), dSwordSm3K1(), nSword(),
			nSwordK3(), iAxeSh2U1(), iAxeF1K1(), dAxe(), dAxeK2(), dAxeU2Sh2(), dAxeF2K1(), dAxeSm3K1(), dAxeM1Sm1(), nAxe(), nAxeK3(),
			bowPo2(), bowPo4(), bowPu3U2F(), cbowQ2(), cbowPi5(), cbowMu(), cbowMu3U2(), cbowQ4Mu(), trident(), arrows(), arrowsS(), arrowsP(), arrowsW()
		};
	public static ItemStack[] weapon3 = {
			dSword(), dSwordK2(), dSwordU2Sh2(), dSwordF1K1Sw1(), dSwordSm3K1(), dSwordM1Sm1(), nSword(), nSwordK3(), nSwordU3Sh2(), nSwordF2K2Sh1(), nSwordSm4Sw2(),
			dAxe(), dAxeK2(), dAxeU2Sh2(), dAxeF2K1(), dAxeSm3K1(), dAxeM1Sm1(), nAxe(), nAxeK3(), nAxeU3Sh2(), nAxeF2K2Sh1(), nAxeSm5(),
			bowPo4(), bowLoaded(), strongBow(), cbowPi5(), cbowMu3U2(), cbowQ4Mu(), cbowLoaded(), shotgun(), trident(), tridentK2(), tridentSh2(),
			arrows(), arrowsS(), arrowsP(), arrowsW(), arrowsD()
		};
	public static ItemStack[] weapon4 = {
			dSwordU2Sh2(), dSwordSm3K1(), dSwordM1Sm1(), nSword(), nSwordK3(), nSwordU3Sh2(), nSwordF2K2Sh1(), nSwordSm4Sw2(), nSwordLoaded(), ripperSword(),
			dAxeF2K1(), dAxeM1Sm1(), nAxe(), nAxeK3(), nAxeU3Sh2(), nAxeF2K2Sh1(), nAxeSm5(), nAxeLoaded(), shredderAxe(),
			bowLoaded(), strongBow(), cbowMu3U2(), cbowLoaded(), shotgun(), trident(), tridentK2(), tridentSh2(), tridentSh5(), tridentLoaded(),
			arrowsS(), arrowsP(), arrowsW(), arrowsD()
		};
	public static ItemStack[] weapon5 = {
			nSword(),  nSwordU3Sh2(), nSwordF2K2Sh1(), nSwordSm4Sw2(), nSwordLoaded(), ripperSword(), nAxeU3Sh2(), nAxeF2K2Sh1(), nAxeSm5(), nAxeLoaded(), shredderAxe(),
			bowLoaded(), strongBow(), cbowLoaded(), shotgun(), tridentSh2(), tridentSh5(), tridentLoaded(), arrowsS(), arrowsP(), arrowsW(), arrowsD()
		};

//	All lists for armor section
	public static ItemStack[] armor0 = {
			lHelmet(), lHelmetP2(), cHelmet(), cHelmetU2(), cHelmetPp3(), iHelmet(), iHelmetT2(),
			lChestplate(), lChestplateP2(), cChestplate(), cChestplateU2(), cChestplatePp3(), iChestplate(), iChestplateT2(),
			lLeggings(), lLeggingsP2(), cLeggings(), cLeggingsU2(), cLeggingsPp3(), iLeggings(), iLeggingsT2(),
			lBoots(), lBootsP2(), cBoots(), cBootsU2(), cBootsPp3(), iBoots(), iBootsT2()
		};
	public static ItemStack[] armor1 = {
			cHelmetPp3(), iHelmet(), iHelmetP3(), iHelmetT2(), dHelmet(), dHelmetB4(), dHelmetP2(), dHelmetF4U3(), nHelmet(),
			cChestplatePp3(), iChestplate(), iChestplateP3(), iChestplateT2(), dChestplate(), dChestplateB4(), dChestplateP2(), dChestplateF4U3(), nChestplate(),
			cLeggingsPp3(), iLeggings(), iLeggingsP3(), iLeggingsT2(), dLeggings(), dLeggingsB4(), dLeggingsP2(), dLeggingsF4U3(), nLeggings(),
			cBootsPp3(), iBoots(), iBootsP3(), iBootsT2(), dBoots(), dBootsB4(), dBootsP2(), dBootsF4U3(), nBoots(), shield()
		};
	public static ItemStack[] armor2 = {
			iHelmetP3(), dHelmet(), dHelmetB4(), dHelmetP2(), dHelmetPp3T2(), dHelmetF4U3(), nHelmet(), nHelmetP4(), nHelmetPp5U2(),
			iChestplateP3(), dChestplate(), dChestplateB4(), dChestplateP2(), dChestplatePp3T2(), dChestplateF4U3(), nChestplate(), nChestplateP4(), nChestplatePp5U2(),
			iLeggingsP3(), dLeggings(), dLeggingsB4(), dLeggingsP2(), dLeggingsPp3T2(), dLeggingsF4U3(), nLeggings(), nLeggingsP4(), nLeggingsPp5U2(),
			iBootsP3(), dBoots(), dBootsB4(), dBootsP2(), dBootsPp3T2(), dBootsF4U3(), nBoots(), nBootsP4(), nBootsPp5U2(), shield(), shieldU1()
		};
	public static ItemStack[] armor3 = {
			dHelmetP2(), dHelmetPp3T2(), dHelmetF4U3(), nHelmet(), nHelmetP4(), nHelmetPp5U2(), nHelmetT3M(), nHelmetPp5U2(),
			dChestplateP2(), dChestplatePp3T2(), dChestplateF4U3(), nChestplate(), nChestplateP4(), nChestplatePp5U2(), nChestplateT3M(), nChestplatePp5U2(),
			dLeggingsP2(), dLeggingsPp3T2(), dLeggingsF4U3(), nLeggings(), nLeggingsP4(), nLeggingsPp5U2(), nLeggingsT3M(), nLeggingsPp5U2(),
			dBootsP2(), dBootsPp3T2(), dBootsF4U3(), nBoots(), nBootsP4(), nBootsPp5U2(), nBootsT3M(), nBootsPp5U2(), shield(), shieldU1(), shieldU3(), shieldM()
		};
	public static ItemStack[] armor4 = {
			nHelmet(), nHelmetP4(), nHelmetPp5U2(), nHelmetT3M(), nHelmetPp5U2(), nHelmetLoaded(),
			nChestplate(), nChestplateP4(), nChestplatePp5U2(), nChestplateT3M(), nChestplatePp5U2(), nChestplateLoaded(),
			nLeggings(), nLeggingsP4(), nLeggingsPp5U2(), nLeggingsT3M(), nLeggingsPp5U2(), nLeggingsLoaded(),
			nBoots(), nBootsP4(), nBootsPp5U2(), nBootsT3M(), nBootsPp5U2(), nBootsLoaded(), shieldU1(), shieldU3(), shieldM()
		};
	public static ItemStack[] armor5 = {
			nHelmet(), nHelmetP4(), nHelmetPp5U2(), nHelmetT3M(), nHelmetPp5U2(), nHelmetLoaded(),
			nChestplate(), nChestplateP4(), nChestplatePp5U2(), nChestplateT3M(), nChestplatePp5U2(), nChestplateLoaded(),
			nLeggings(), nLeggingsP4(), nLeggingsPp5U2(), nLeggingsT3M(), nLeggingsPp5U2(), nLeggingsLoaded(),
			nBoots(), nBootsP4(), nBootsPp5U2(), nBootsT3M(), nBootsPp5U2(), nBootsLoaded(), shieldU3(), shieldM()
		};

//	All lists for consumables
	public static ItemStack[] consume0 = {
			bread(), carrot(), beetroot(), health(), speed(), beetroot(), beetroot()
		};
	public static ItemStack[] consume1 = {
			bread(), gcarrot(), steak(), health(), speed(), strength(), bread(), bread()
		};
	public static ItemStack[] consume2 = {
			gapple(), bread(), gcarrot(), steak(), health(), speed(), strength(), gcarrot()
		};
	public static ItemStack[] consume3 = {
			gapple(), egapple(), gcarrot(), steak(), health(), speed(), strength()
		};
	public static ItemStack[] consume4 = {
			gapple(), egapple(), gcarrot(), health(), speed(), strength(), totem()
		};
	public static ItemStack[] consume5 = {
			egapple(), gcarrot(), health(), speed(), strength(), totem()
		};

//	Lists of all lists
	public static ItemStack[][] weapon = {weapon0, weapon1, weapon2, weapon3, weapon4, weapon5};
	public static ItemStack[][] armor = {armor0, armor1, armor2, armor3, armor4, armor5};
	public static ItemStack[][] consume = {consume0, consume1, consume2, consume3, consume4, consume5};

}
