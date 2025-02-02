package me.theguyhere.villagerdefense.plugin.game.models;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Getter
public class EnchantingBook extends ItemStack {
    private final Enchantment enchantToAdd;

    public EnchantingBook(ItemStack itemStack, Enchantment enchantToAdd) {
        super(itemStack);
        this.enchantToAdd = enchantToAdd;
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
            case "Knockback": return new EnchantingBook(knockback(), Enchantment.KNOCKBACK);
            case "Sweeping": return new EnchantingBook(sweepingEdge(), Enchantment.SWEEPING_EDGE);
            case "Smite": return new EnchantingBook(smite(), Enchantment.DAMAGE_UNDEAD);
            case "Sharpness": return new EnchantingBook(sharpness(), Enchantment.DAMAGE_ALL);
            case "Fire": return new EnchantingBook(fireAspect(), Enchantment.FIRE_ASPECT);
            case "Punch": return new EnchantingBook(punch(), Enchantment.ARROW_KNOCKBACK);
            case "Piercing": return new EnchantingBook(piercing(), Enchantment.PIERCING);
            case "Quick": return new EnchantingBook(quickCharge(), Enchantment.QUICK_CHARGE);
            case "Power": return new EnchantingBook(power(), Enchantment.ARROW_DAMAGE);
            case "Loyalty": return new EnchantingBook(loyalty(), Enchantment.LOYALTY);
            case "Flame": return new EnchantingBook(flame(), Enchantment.ARROW_FIRE);
            case "Multishot": return new EnchantingBook(multishot(), Enchantment.MULTISHOT);
            case "Infinity": return new EnchantingBook(infinity(), Enchantment.ARROW_INFINITE);
            case "Blast": return new EnchantingBook(blastProtection(), Enchantment.PROTECTION_EXPLOSIONS);
            case "Thorns": return new EnchantingBook(thorns(), Enchantment.THORNS);
            case "Projectile": return new EnchantingBook(projectileProtection(), Enchantment.PROTECTION_PROJECTILE);
            case "Protection": return new EnchantingBook(protection(), Enchantment.PROTECTION_ENVIRONMENTAL);
            case "Unbreaking": return new EnchantingBook(unbreaking(), Enchantment.DURABILITY);
            case "Mending": return new EnchantingBook(mending(), Enchantment.MENDING);
            default: return null;
        }
    }

    public static ItemStack knockback() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.knockback)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(), 
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack sweepingEdge() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.sweepingEdge)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack smite() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.smite)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack sharpness() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.sharpness)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack fireAspect() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.fireAspect)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack punch() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.punch)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack piercing() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.piercing)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack quickCharge() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.quickCharge)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack power() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.power)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack loyalty() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.loyalty)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack flame() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.flame)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_PURPLE, LanguageManager.messages.caution).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack multishot() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.multishot)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_PURPLE, LanguageManager.messages.caution).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack infinity() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.infinity)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_PURPLE, LanguageManager.messages.caution).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack blastProtection() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.blastProtection)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack thorns() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.thorns)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack projectileProtection() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.projectileProtection)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack protection() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.protection)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack unbreaking() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.unbreaking)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
    public static ItemStack mending() {
        return ItemManager.createItem(Material.ENCHANTED_BOOK,
                CommunicationManager.format("&a&l" + String.format(LanguageManager.names.enchantBook,
                        LanguageManager.enchants.mending)),
                ItemManager.BUTTON_FLAGS, ItemManager.glow(),
                new ColoredMessage(LanguageManager.messages.enchantInstruction).toString(),
                new ColoredMessage(ChatColor.DARK_PURPLE, LanguageManager.messages.caution).toString(),
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.warning).toString());
    }
}
