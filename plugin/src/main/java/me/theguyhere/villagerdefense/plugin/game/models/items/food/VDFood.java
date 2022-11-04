package me.theguyhere.villagerdefense.plugin.game.models.items.food;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDFood extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return ShopFood.matches(toCheck);
    }
}
