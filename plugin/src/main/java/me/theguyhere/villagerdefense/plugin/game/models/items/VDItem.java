package me.theguyhere.villagerdefense.plugin.game.models.items;

import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.VDWeapon;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public abstract class VDItem {
    // Gaussian level randomization for most ordinary stuff
    protected static int getLevel(double difficulty) {
        Random r = new Random();
        return Math.max((int) (Math.max(difficulty, 1.5) * (1 + .2 * Math.max(Math.min(r.nextGaussian(), 3), -3)) + .5),
                1); // Mean 100%, SD 50%, restrict 40% - 160%, min mean 3
    }

    public static boolean matches(ItemStack toCheck) {
        return VDAbility.matches(toCheck) || VDArmor.matches(toCheck) || VDFood.matches(toCheck) ||
                VDWeapon.matches(toCheck);
    }
}
