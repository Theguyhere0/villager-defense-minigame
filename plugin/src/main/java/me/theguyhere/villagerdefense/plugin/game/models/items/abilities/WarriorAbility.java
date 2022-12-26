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

public abstract class WarriorAbility extends VDAbility {
    @NotNull
    public static ItemStack create(Tier tier) {
        List<String> lores = new ArrayList<>();

        // Set name
        String name;
        switch (tier) {
            case T0:
                name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.warrior.name, tier);
                break;
            case T1:
                name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.warrior.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.warrior.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.warrior.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.warrior.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.warrior.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (tier) {
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
        switch (tier) {
            case T0:
            case T1:
                effect = String.format(LanguageManager.kits.warrior.effect, "+10%");
                break;
            case T2:
            case T3:
                effect = String.format(LanguageManager.kits.warrior.effect, "+20%");
                break;
            case T4:
            case T5:
                effect = String.format(LanguageManager.kits.warrior.effect, "+30%");
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
        switch (tier) {
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
        switch (tier) {
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
        switch (tier) {
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
        switch (tier) {
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
                Material.RED_DYE,
                name,
                ItemManager.HIDE_ENCHANT_FLAGS,
                ItemManager.glow(),
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
        return toCheck.getType() == Material.RED_DYE && lore.stream().anyMatch(line -> line.contains(
                EFFECT.toString().replace("%s", "")));
    }
}
