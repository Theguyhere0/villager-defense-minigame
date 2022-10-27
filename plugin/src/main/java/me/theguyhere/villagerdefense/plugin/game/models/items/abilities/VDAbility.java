package me.theguyhere.villagerdefense.plugin.game.models.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public abstract class VDAbility extends VDItem {
    protected static final ColoredMessage COOLDOWN = new ColoredMessage(ChatColor.BLUE,
            String.format(LanguageManager.messages.cooldown, LanguageManager.messages.seconds));
    protected static final ColoredMessage DURATION = new ColoredMessage(ChatColor.BLUE,
            String.format(LanguageManager.messages.duration, LanguageManager.messages.seconds));
    protected static final ColoredMessage EFFECT = new ColoredMessage(ChatColor.BLUE,
            LanguageManager.messages.effect);
    protected static final ColoredMessage RANGE = new ColoredMessage(ChatColor.BLUE,
            String.format(LanguageManager.messages.range, LanguageManager.messages.blocks));

    public static boolean matches(ItemStack toCheck) {
        return MageAbility.matches(toCheck) || NinjaAbility.matches(toCheck) || TemplarAbility.matches(toCheck) ||
                WarriorAbility.matches(toCheck) || KnightAbility.matches(toCheck) || PriestAbility.matches(toCheck) ||
                SirenAbility.matches(toCheck) || MonkAbility.matches(toCheck) || MessengerAbility.matches(toCheck);
    }
}
