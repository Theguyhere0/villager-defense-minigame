package me.theguyhere.villagerdefense.plugin.game.models.items.abilities;

import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDAbility extends VDItem {
    public static boolean matches(ItemStack toCheck) {
        return MageAbility.matches(toCheck) || NinjaAbility.matches(toCheck) || TemplarAbility.matches(toCheck) ||
                WarriorAbility.matches(toCheck) || KnightAbility.matches(toCheck) || PriestAbility.matches(toCheck) ||
                SirenAbility.matches(toCheck) || MonkAbility.matches(toCheck) || MessengerAbility.matches(toCheck);
    }
}
