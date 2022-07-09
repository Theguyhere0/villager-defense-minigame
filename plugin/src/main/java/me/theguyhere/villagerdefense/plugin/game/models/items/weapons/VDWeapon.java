package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDWeapon extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return matchesNoAmmo(toCheck) || Arrow.matches(toCheck);
    }

    public static boolean matchesNoAmmo(ItemStack toCheck) {
        return StarterSword.matches(toCheck) || Sword.matches(toCheck) || Axe.matches(toCheck) ||
                Scythe.matches(toCheck) || Bow.matches(toCheck) || Crossbow.matches(toCheck);
    }

    public static boolean matchesClickableWeapon(ItemStack toCheck) {
        return Bow.matches(toCheck) || Crossbow.matches(toCheck);
    }
}
