package me.theguyhere.villagerdefense.game.models;

import com.sun.istack.internal.NotNull;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class Kits {
    private final Map<String, Integer[]> kitPrices = new HashMap<>();
    private static final boolean[] FLAGS = {false, false};
    private static final boolean[] FLAGS2 = {true, false};

    public Kits() {
        kitPrices.put("Soldier", new Integer[]{250});
        kitPrices.put("Tailor", new Integer[]{300});
        kitPrices.put("Alchemist", new Integer[]{400});
        kitPrices.put("Trader", new Integer[]{500});
        kitPrices.put("Summoner", new Integer[]{750, 1750, 4500});
        kitPrices.put("Reaper", new Integer[]{750, 2000, 4000});
        kitPrices.put("Phantom", new Integer[]{6000});
        kitPrices.put("Mage", new Integer[]{3500, 7500, 13000});
        kitPrices.put("Ninja", new Integer[]{4000, 8000, 14000});
        kitPrices.put("Templar", new Integer[]{3500, 8000, 12500});
        kitPrices.put("Warrior", new Integer[]{5000, 9000, 14000});
        kitPrices.put("Knight", new Integer[]{4500, 8500, 13000});
        kitPrices.put("Priest", new Integer[]{5000, 9000, 15000});
        kitPrices.put("Siren", new Integer[]{4000, 8000, 13500});
        kitPrices.put("Monk", new Integer[]{3000, 7000, 11000});
        kitPrices.put("Messenger", new Integer[]{4000, 8000, 12000});
        kitPrices.put("Blacksmith", new Integer[]{7500});
        kitPrices.put("Witch", new Integer[]{2500});
        kitPrices.put("Merchant", new Integer[]{4000});
        kitPrices.put("Vampire", new Integer[]{6000});
        kitPrices.put("Giant", new Integer[]{5000, 8000});
    }

    public int getPrice(String kit) {
        return kitPrices.get(kit)[0];
    }

    public int getPrice(String kit, int level) {
        return kitPrices.get(kit)[level - 1];
    }

    // Kit items
    public static @NotNull ItemStack orc() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        return Utils.createItem(Material.STICK, Utils.format("&aOrc's Club"), FLAGS, enchants);
    }
    public static @NotNull ItemStack farmer() {
        return Utils.createItems(Material.CARROT, 5, Utils.format("&aFarmer's Carrots"));
    }
    public static @NotNull ItemStack soldier() {
        return Utils.createItem(Material.STONE_SWORD, Utils.format("&aSoldier's Sword"));
    }
    public static @NotNull ItemStack tailorHelmet() {
        return Utils.createItem(Material.LEATHER_HELMET, Utils.format("&aTailor's Helmet"));
    }
    public static @NotNull ItemStack tailorChestplate() {
        return Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&aTailor's Chestplate"));
    }
    public static @NotNull ItemStack tailorLeggings() {
        return Utils.createItem(Material.LEATHER_LEGGINGS, Utils.format("&aTailor's Leggings"));
    }
    public static @NotNull ItemStack tailorBoots() {
        return Utils.createItem(Material.LEATHER_BOOTS, Utils.format("&aTailor's Boots"));
    }
    public static @NotNull ItemStack alchemistSpeed() {
        return Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.SPEED),
                Utils.format("&aAlchemist's Speed Potion"));
    }
    public static @NotNull ItemStack alchemistHealth() {
        return Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.INSTANT_HEAL),
                Utils.format("&aAlchemist's Health Potion"));
    }
    public static @NotNull ItemStack summoner1() {
        return Utils.createItem(Material.WOLF_SPAWN_EGG, Utils.format("&aSummoner's Wolf Spawn Egg"));
    }
    public static @NotNull ItemStack summoner2() {
        return Utils.createItems(Material.WOLF_SPAWN_EGG, 2, Utils.format("&aSummoner's Wolf Spawn Egg"));
    }
    public static @NotNull ItemStack summoner3() {
        return Utils.createItem(Material.GHAST_SPAWN_EGG, Utils.format("&aSummoner's Iron Golem Spawn Egg"));
    }
    public static @NotNull ItemStack reaper1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 3);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static @NotNull ItemStack reaper2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 5);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static @NotNull ItemStack reaper3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 8);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static @NotNull ItemStack mage() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PURPLE_DYE, Utils.format("&dMage Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack ninja() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLACK_DYE, Utils.format("&dNinja Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack templar() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.YELLOW_DYE, Utils.format("&dTemplar Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack warrior() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.RED_DYE, Utils.format("&dWarrior Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack knight() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BROWN_DYE, Utils.format("&dKnight Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack priest() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.WHITE_DYE, Utils.format("&dPriest Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack siren() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PINK_DYE, Utils.format("&dSiren Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack monk() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.GREEN_DYE, Utils.format("&dMonk Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
    public static @NotNull ItemStack messenger() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLUE_DYE, Utils.format("&dMessenger Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"));
    }
}
