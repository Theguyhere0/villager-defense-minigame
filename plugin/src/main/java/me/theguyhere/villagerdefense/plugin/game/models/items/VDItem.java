package me.theguyhere.villagerdefense.plugin.game.models.items;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.game.models.items.abilities.VDAbility;
import me.theguyhere.villagerdefense.plugin.game.models.items.armor.VDArmor;
import me.theguyhere.villagerdefense.plugin.game.models.items.food.VDFood;
import me.theguyhere.villagerdefense.plugin.game.models.items.weapons.VDWeapon;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public abstract class VDItem {
    protected static final ColoredMessage ATTACK_TYPE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackType);
    protected static final ColoredMessage ATTACK_TYPE_NORMAL = new ColoredMessage(ChatColor.GREEN,
            LanguageManager.names.normal);
    protected static final ColoredMessage ATTACK_TYPE_PENETRATING = new ColoredMessage(ChatColor.YELLOW,
            LanguageManager.names.penetrating);
    protected static final ColoredMessage MAIN_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackMainDamage);
    protected static final ColoredMessage CRIT_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackCritDamage);
    protected static final ColoredMessage SWEEP_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackSweepDamage);
    protected static final ColoredMessage RANGE_DAMAGE = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackRangeDamage);
    protected static final ColoredMessage SPEED = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.attackSpeed);
    protected static final ColoredMessage ARMOR = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.armor);
    protected static final ColoredMessage TOUGHNESS = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.toughness);
    protected static final ColoredMessage WEIGHT = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.weight);

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
