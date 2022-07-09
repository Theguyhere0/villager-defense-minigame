package me.theguyhere.villagerdefense.plugin.game.models.items.food;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDFood extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return Beetroot.matches(toCheck) || Carrot.matches(toCheck) || Bread.matches(toCheck) ||
                Mutton.matches(toCheck) || Steak.matches(toCheck) || GoldenCarrot.matches(toCheck) ||
                GoldenApple.matches(toCheck) || EnchantedApple.matches(toCheck) || Totem.matches(toCheck);
    }
}
