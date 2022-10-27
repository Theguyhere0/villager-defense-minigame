package me.theguyhere.villagerdefense.plugin.game.models.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PriestAbility extends VDAbility {
    @NotNull
    public static ItemStack create(AbilityType type) {
        List<String> lores = new ArrayList<>();

        // Set name
        String name;
        switch (type) {
            case T0:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t0.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T0]")
                );
                break;
            case T1:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t1.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T1]")
                );
                break;
            case T2:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t2.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T2]")
                );
                break;
            case T3:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t3.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T3]")
                );
                break;
            case T4:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t4.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T4]")
                );
                break;
            case T5:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.essences.t5.name),
                        new ColoredMessage(ChatColor.LIGHT_PURPLE, LanguageManager.kits.priest.name),
                        new ColoredMessage(ChatColor.AQUA, "[T5]")
                );
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case T0:
                description = LanguageManager.itemLore.essences.t0.description;
                break;
            case T1:
                description = LanguageManager.itemLore.essences.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.essences.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.essences.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.essences.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.essences.t5.description;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name, followed by instructions for usage
        lores.add("");
        lores.add(new ColoredMessage(LanguageManager.messages.rightClick).toString());

        // Set effect
        String effect;
        switch (type) {
            case T0:
            case T1:
                effect = String.format(LanguageManager.kits.priest.effect, "+5");
                break;
            case T2:
            case T3:
                effect = String.format(LanguageManager.kits.priest.effect, "+10");
                break;
            case T4:
            case T5:
                effect = String.format(LanguageManager.kits.priest.effect, "+15");
                break;
            default:
                effect = null;
        }
        if (effect != null)
            lores.addAll(CommunicationManager.formatDescriptionList(ChatColor.LIGHT_PURPLE,
                    CommunicationManager.format(EFFECT, new ColoredMessage(ChatColor.LIGHT_PURPLE,
                            effect)), Utils.LORE_CHAR_LIMIT));

        // Set range
        double range;
        switch (type) {
            case T0:
                range = 2.5;
                break;
            case T1:
                range = 3;
                break;
            case T2:
                range = 3.5;
                break;
            case T3:
                range = 4;
                break;
            case T4:
                range = 4.5;
                break;
            case T5:
                range = 5;
                break;
            default:
                range = 0;
        }
        if (range > 0)
            lores.add(CommunicationManager.format(RANGE, new ColoredMessage(ChatColor.DARK_PURPLE,
                    Double.toString(range))));

        // Set duration
        double duration;
        switch (type) {
            case T0:
                duration = 5;
                break;
            case T1:
                duration = 9.8;
                break;
            case T2:
                duration = 12.8;
                break;
            case T3:
                duration = 17.7;
                break;
            case T4:
                duration = 21.3;
                break;
            case T5:
                duration = 28;
                break;
            default:
                duration = 0;
        }
        if (duration > 0)
            lores.add(CommunicationManager.format(DURATION, new ColoredMessage(ChatColor.DARK_AQUA,
                    Double.toString(duration))));

        // Set cooldown
        double cooldown;
        switch (type) {
            case T0:
                cooldown = 45;
                break;
            case T1:
                cooldown = 44.2;
                break;
            case T2:
                cooldown = 43.5;
                break;
            case T3:
                cooldown = 41.4;
                break;
            case T4:
                cooldown = 40.3;
                break;
            case T5:
                cooldown = 35;
                break;
            default:
                cooldown = 0;
        }
        if (cooldown > 0)
            lores.add(CommunicationManager.format(COOLDOWN, Double.toString(cooldown)));

        // Set price
        int price;
        switch (type) {
            case T1:
                price = 500;
                break;
            case T2:
                price = 1000;
                break;
            case T3:
                price = 1500;
                break;
            case T4:
                price = 2000;
                break;
            case T5:
                price = 3000;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        // Create item
        return ItemManager.createItem(
                Material.WHITE_DYE,
                name,
                ItemManager.HIDE_ENCHANT_FLAGS,
                ItemManager.dummyEnchant(),
                lores
        );
    }

    public static boolean matches(ItemStack toCheck) {
        if (toCheck == null)
            return false;
        ItemMeta meta = toCheck.getItemMeta();
        if (meta == null)
            return false;
        List<String> lore = meta.getLore();
        if (lore == null)
            return false;
        return toCheck.getType() == Material.WHITE_DYE && lore.stream().anyMatch(line -> line.contains(
                EFFECT.toString().replace("%s", "")));
    }

    public enum AbilityType{
        T0,
        T1,
        T2,
        T3,
        T4,
        T5
    }
}
