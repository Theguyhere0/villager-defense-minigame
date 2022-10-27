package me.theguyhere.villagerdefense.plugin.game.models.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.game.models.items.VDItem;
import me.theguyhere.villagerdefense.plugin.game.models.kits.Kit;
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

    public static ItemStack createAbility(String kitID, String type) {
        if (Kit.knight().getID().equals(kitID))
            return KnightAbility.create(KnightAbility.AbilityType.valueOf(type));
        else if (Kit.mage().getID().equals(kitID))
            return MageAbility.create(MageAbility.AbilityType.valueOf(type));
        else if (Kit.messenger().getID().equals(kitID))
            return MessengerAbility.create(MessengerAbility.AbilityType.valueOf(type));
        else if (Kit.monk().getID().equals(kitID))
            return MonkAbility.create(MonkAbility.AbilityType.valueOf(type));
        else if (Kit.ninja().getID().equals(kitID))
            return NinjaAbility.create(NinjaAbility.AbilityType.valueOf(type));
        else if (Kit.priest().getID().equals(kitID))
            return PriestAbility.create(PriestAbility.AbilityType.valueOf(type));
        else if (Kit.siren().getID().equals(kitID))
            return SirenAbility.create(SirenAbility.AbilityType.valueOf(type));
        else if (Kit.templar().getID().equals(kitID))
            return TemplarAbility.create(TemplarAbility.AbilityType.valueOf(type));
        else if (Kit.warrior().getID().equals(kitID))
            return WarriorAbility.create(WarriorAbility.AbilityType.valueOf(type));
        else return null;
    }

    public static boolean matches(ItemStack toCheck) {
        return MageAbility.matches(toCheck) || NinjaAbility.matches(toCheck) || TemplarAbility.matches(toCheck) ||
                WarriorAbility.matches(toCheck) || KnightAbility.matches(toCheck) || PriestAbility.matches(toCheck) ||
                SirenAbility.matches(toCheck) || MonkAbility.matches(toCheck) || MessengerAbility.matches(toCheck);
    }
}