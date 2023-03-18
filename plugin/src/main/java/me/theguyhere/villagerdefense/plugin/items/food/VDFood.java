package me.theguyhere.villagerdefense.plugin.items.food;

import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDFood extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return ShopFood.matches(toCheck) || FarmerCarrot.matches(toCheck);
    }
}
