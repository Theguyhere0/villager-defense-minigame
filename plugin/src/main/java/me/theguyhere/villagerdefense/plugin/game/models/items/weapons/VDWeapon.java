package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class VDWeapon extends VDItem {
    protected static final ColoredMessage ATTACK_TYPE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackType);
    protected static final ColoredMessage ATTACK_TYPE_NORMAL = new ColoredMessage(ChatColor.GREEN,
            LanguageManager.names.normal);
    protected static final ColoredMessage ATTACK_TYPE_CRUSHING = new ColoredMessage(ChatColor.YELLOW,
            LanguageManager.names.crushing);
    protected static final ColoredMessage ATTACK_TYPE_PENETRATING = new ColoredMessage(ChatColor.RED,
            LanguageManager.names.penetrating);
    protected static final ColoredMessage MAIN_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackMainDamage);
    protected static final ColoredMessage CRIT_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackCritDamage);
    protected static final ColoredMessage SWEEP_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackSweepDamage);
    protected static final ColoredMessage RANGE_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackRangeDamage);
    protected static final ColoredMessage PIERCE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.pierce);
    protected static final ColoredMessage SPEED = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackSpeed);
    protected static final ColoredMessage AMMO_COST = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.ammoCost);
    protected static final ColoredMessage CAPACITY = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.capacity);
    protected static final ColoredMessage REFILL = new ColoredMessage(ChatColor.BLUE,
            String.format(LanguageManager.messages.refill, LanguageManager.messages.seconds));
    protected static final ColoredMessage NEXT_REFILL = new ColoredMessage(ChatColor.BLUE,
            String.format(LanguageManager.messages.nextRefill, LanguageManager.messages.seconds));

    public static boolean matches(ItemStack toCheck) {
        return matchesNoAmmo(toCheck) || Ammo.matches(toCheck);
    }

    public static boolean matchesNoAmmo(ItemStack toCheck) {
        return Sword.matches(toCheck) || Axe.matches(toCheck) || Scythe.matches(toCheck) ||
                Bow.matches(toCheck) || Crossbow.matches(toCheck);
    }

    public static boolean matchesClickableWeapon(ItemStack toCheck) {
        return Bow.matches(toCheck) || Crossbow.matches(toCheck);
    }

    public static boolean matchesAmmoWeapon(ItemStack toCheck) {
        return Bow.matches(toCheck) || Crossbow.matches(toCheck);
    }
}
