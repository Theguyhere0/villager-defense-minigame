package me.theguyhere.villagerdefense.plugin.items.armor;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class VDArmor extends VDItem {
    protected static final ColoredMessage ARMOR = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.armor);
    protected static final ColoredMessage TOUGHNESS = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.toughness);
    protected static final ColoredMessage WEIGHT = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.weight);

    public static boolean matches(ItemStack toCheck) {
        return Helmet.matches(toCheck) || Chestplate.matches(toCheck) || Leggings.matches(toCheck) ||
                Boots.matches(toCheck);
    }
}
