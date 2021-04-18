package me.theguyhere.villagerdefense.game.models;

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
        kitPrices.put("Reaper", new Integer[]{1000, 2500, 6000});
        kitPrices.put("Phantom", new Integer[]{6000});
        kitPrices.put("Mage", new Integer[]{5000, 8000, 11000});
        kitPrices.put("Ninja", new Integer[]{4000, 7500, 10000});
        kitPrices.put("Templar", new Integer[]{4500, 8500, 11000});
        kitPrices.put("Warrior", new Integer[]{5000, 8500, 11000});
        kitPrices.put("Knight", new Integer[]{6000, 9000, 12000});
        kitPrices.put("Priest", new Integer[]{6000, 9000, 12000});
        kitPrices.put("Siren", new Integer[]{5500, 8000, 12000});
        kitPrices.put("Monk", new Integer[]{3500, 6500, 10000});
        kitPrices.put("Messenger", new Integer[]{4500, 8000, 11000});
        kitPrices.put("Blacksmith", new Integer[]{2500});
        kitPrices.put("Witch", new Integer[]{2500});
        kitPrices.put("Merchant", new Integer[]{4000});
        kitPrices.put("Vampire", new Integer[]{5500});
        kitPrices.put("Giant", new Integer[]{6000, 9000});
    }

    public int getPrice(String kit) {
        return kitPrices.get(kit)[0];
    }

    public int getPrice(String kit, int level) {
        return kitPrices.get(kit)[level - 1];
    }

    // Kit items
    public static ItemStack orc() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.KNOCKBACK, 5);
        return Utils.createItem(Material.STICK, Utils.format("&aOrc's Club"), FLAGS, enchants);
    }
    public static ItemStack farmer() {
        return Utils.createItems(Material.CARROT, 5, Utils.format("&aFarmer's Carrots"));
    }
    public static ItemStack soldier() {
        return Utils.createItem(Material.STONE_SWORD, Utils.format("&aSoldier's Sword"));
    }
    public static ItemStack tailorHelmet() {
        return Utils.createItem(Material.LEATHER_HELMET, Utils.format("&aTailor's Helmet"));
    }
    public static ItemStack tailorChestplate() {
        return Utils.createItem(Material.LEATHER_CHESTPLATE, Utils.format("&aTailor's Chestplate"));
    }
    public static ItemStack tailorLeggings() {
        return Utils.createItem(Material.LEATHER_LEGGINGS, Utils.format("&aTailor's Leggings"));
    }
    public static ItemStack tailorBoots() {
        return Utils.createItem(Material.LEATHER_BOOTS, Utils.format("&aTailor's Boots"));
    }
    public static ItemStack alchemistSpeed() {
        return Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.SPEED),
                Utils.format("&aAlchemist's Speed Potion"));
    }
    public static ItemStack alchemistRegeneration() {
        return Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.REGEN),
                Utils.format("&aAlchemist's Regeneration Potion"));
    }
    public static ItemStack alchemistStrength() {
        return Utils.createPotionItem(Material.SPLASH_POTION, new PotionData(PotionType.STRENGTH),
                Utils.format("&aAlchemist's Strength Potion"));
    }
    public static ItemStack summoner1() {
        return Utils.createItem(Material.WOLF_SPAWN_EGG, Utils.format("&aSummoner's Wolf Spawn Egg"));
    }
    public static ItemStack summoner2() {
        return Utils.createItems(Material.WOLF_SPAWN_EGG, 2, Utils.format("&aSummoner's Wolf Spawn Egg"));
    }
    public static ItemStack summoner3() {
        return Utils.createItem(Material.GHAST_SPAWN_EGG, Utils.format("&aSummoner's Iron Golem Spawn Egg"));
    }
    public static ItemStack reaper1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 3);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static ItemStack reaper2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 4);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static ItemStack reaper3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DAMAGE_ALL, 5);
        return Utils.createItem(Material.NETHERITE_HOE, Utils.format("&aReaper's Scythe"), FLAGS, enchants);
    }
    public static ItemStack mage1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PURPLE_DYE, Utils.format("&dLevel 1 Mage Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 1 experience levels"));
    }
    public static ItemStack mage2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PURPLE_DYE, Utils.format("&dLevel 2 Mage Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack mage3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PURPLE_DYE, Utils.format("&dLevel 3 Mage Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack ninja1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLACK_DYE, Utils.format("&dLevel 1 Ninja Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 1 experience levels"));
    }
    public static ItemStack ninja2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLACK_DYE, Utils.format("&dLevel 2 Ninja Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 1 experience levels"));
    }
    public static ItemStack ninja3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLACK_DYE, Utils.format("&dLevel 3 Ninja Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 1 experience levels"));
    }
    public static ItemStack templar1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.YELLOW_DYE, Utils.format("&dLevel 1 Templar Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack templar2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.YELLOW_DYE, Utils.format("&dLevel 2 Templar Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack templar3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.YELLOW_DYE, Utils.format("&dLevel 3 Templar Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack warrior1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.RED_DYE, Utils.format("&dLevel 1 Warrior Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack warrior2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.RED_DYE, Utils.format("&dLevel 2 Warrior Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack warrior3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.RED_DYE, Utils.format("&dLevel 3 Warrior Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack knight1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BROWN_DYE, Utils.format("&dLevel 1 Knight Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack knight2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BROWN_DYE, Utils.format("&dLevel 2 Knight Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack knight3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BROWN_DYE, Utils.format("&dLevel 3 Knight Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack priest1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.WHITE_DYE, Utils.format("&dLevel 1 Priest Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack priest2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.WHITE_DYE, Utils.format("&dLevel 2 Priest Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack priest3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.WHITE_DYE, Utils.format("&dLevel 3 Priest Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack siren1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PINK_DYE, Utils.format("&dLevel 1 Siren Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack siren2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PINK_DYE, Utils.format("&dLevel 2 Siren Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack siren3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.PINK_DYE, Utils.format("&dLevel 3 Siren Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack monk1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.GREEN_DYE, Utils.format("&dLevel 1 Monk Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack monk2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.GREEN_DYE, Utils.format("&dLevel 2 Monk Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack monk3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.GREEN_DYE, Utils.format("&dLevel 3 Monk Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }
    public static ItemStack messenger1() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLUE_DYE, Utils.format("&dLevel 1 Messenger Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 2 experience levels"));
    }
    public static ItemStack messenger2() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLUE_DYE, Utils.format("&dLevel 2 Messenger Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 4 experience levels"));
    }
    public static ItemStack messenger3() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return Utils.createItem(Material.BLUE_DYE, Utils.format("&dLevel 3 Messenger Essence"), FLAGS2, enchants,
                Utils.format("&7Right click to use ability"), Utils.format("&7Consumes 6 experience levels"));
    }

}
