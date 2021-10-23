package me.theguyhere.villagerdefense.game.models;

import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class EnchantingBook extends ItemStack {
    private final Enchantment enchantToAdd;

    public EnchantingBook(ItemStack itemStack, Enchantment enchantToAdd) {
        super(itemStack);
        this.enchantToAdd = enchantToAdd;
    }

    public Enchantment getEnchantToAdd() {
        return enchantToAdd;
    }

    public static EnchantingBook check(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.ENCHANTED_BOOK)
            return null;

        String enchant;

        // Gather enchant from name
        try {
            enchant = Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName().split(" ")[2];
        } catch (Exception e) {
            return null;
        }

        // Assign to known enchanting books
        switch (enchant) {
            case "Knockback":
                return new EnchantingBook(knockback(), Enchantment.KNOCKBACK);
            case "Sweeping":
                return new EnchantingBook(sweepingEdge(), Enchantment.SWEEPING_EDGE);
            case "Smite":
                return new EnchantingBook(smite(), Enchantment.DAMAGE_UNDEAD);
            case "Sharpness":
                return new EnchantingBook(sharpness(), Enchantment.DAMAGE_ALL);
            case "Fire":
                return new EnchantingBook(fireAspect(), Enchantment.FIRE_ASPECT);
            case "Punch":
                return new EnchantingBook(punch(), Enchantment.ARROW_KNOCKBACK);
            case "Piercing":
                return new EnchantingBook(piercing(), Enchantment.PIERCING);
            case "Quick":
                return new EnchantingBook(quickCharge(), Enchantment.QUICK_CHARGE);
            case "Power":
                return new EnchantingBook(power(), Enchantment.ARROW_DAMAGE);
            case "Loyalty":
                return new EnchantingBook(loyalty(), Enchantment.LOYALTY);
            case "Flame":
                return new EnchantingBook(flame(), Enchantment.ARROW_FIRE);
            case "Multishot":
                return new EnchantingBook(multishot(), Enchantment.MULTISHOT);
            case "Infinity":
                return new EnchantingBook(infinity(), Enchantment.ARROW_INFINITE);
            case "Unbreaking":
                return new EnchantingBook(unbreaking(), Enchantment.DURABILITY);
            case "Mending":
                return new EnchantingBook(mending(), Enchantment.MENDING);
            default:
                return null;
        }
    }

    public static ItemStack knockback() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Knockback"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack sweepingEdge() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Sweeping Edge"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack smite() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Smite"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack sharpness() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Sharpness"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack fireAspect() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Fire Aspect"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack punch() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Punch"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack piercing() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Piercing"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack quickCharge() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Quick Charge"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack power() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Power"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack loyalty() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Loyalty"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack flame() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Flame"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&5CAUTION: CAN'T INCREASE LEVEL"), Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack multishot() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Multishot"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&5CAUTION: CAN'T INCREASE LEVEL"), Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack infinity() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Infinity"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&5CAUTION: CAN'T INCREASE LEVEL"), Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack unbreaking() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Unbreaking"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
    public static ItemStack mending() {
        return Utils.createItem(Material.ENCHANTED_BOOK, Utils.format("&a&lBook of Mending"),
                Utils.BUTTON_FLAGS, Utils.glow(), Utils.format("&7Drop onto another item to enchant"),
                Utils.format("&5CAUTION: CAN'T INCREASE LEVEL"), Utils.format("&4WARNING: WORKS ON ANY ITEM"));
    }
}
