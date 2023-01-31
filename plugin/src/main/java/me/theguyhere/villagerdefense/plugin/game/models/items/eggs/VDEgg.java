package me.theguyhere.villagerdefense.plugin.game.models.items.eggs;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDEgg extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return PetEgg.matches(toCheck);
    }
}
