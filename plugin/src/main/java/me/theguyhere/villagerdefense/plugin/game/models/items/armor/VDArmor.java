package me.theguyhere.villagerdefense.plugin.game.models.items.armor;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDArmor extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return Helmet.matches(toCheck) || Chestplate.matches(toCheck) || Leggings.matches(toCheck) ||
                Boots.matches(toCheck);
    }
}
