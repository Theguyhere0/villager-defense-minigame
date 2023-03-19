package me.theguyhere.villagerdefense.plugin.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TemplarAbility extends VDAbility {
    @NotNull
    public static ItemStack create(Tier tier) {
        // Set name
        String name;
        switch (tier) {
            case T0:
                name = formatName(LanguageManager.itemLore.essences.t0.name, LanguageManager.kits.templar.name, tier);
                break;
            case T1:
                name = formatName(LanguageManager.itemLore.essences.t1.name, LanguageManager.kits.templar.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.essences.t2.name, LanguageManager.kits.templar.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.essences.t3.name, LanguageManager.kits.templar.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.essences.t4.name, LanguageManager.kits.templar.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.essences.t5.name, LanguageManager.kits.templar.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        List<String> lores = new ArrayList<>(getDescription(tier));

        // Add space in lore from name, followed by instructions for usage
        lores.add("");
        lores.add(new ColoredMessage(LanguageManager.messages.rightClick).toString());

        // Set effect
        String effect;
        switch (tier) {
            case T0:
            case T1:
                effect = String.format(LanguageManager.kits.templar.effect, "100");
                break;
            case T2:
            case T3:
                effect = String.format(LanguageManager.kits.templar.effect, "200");
                break;
            case T4:
            case T5:
                effect = String.format(LanguageManager.kits.templar.effect, "300");
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

        // Set cooldown
        double cooldown;
        switch (tier) {
            case T0:
                cooldown = 35;
                break;
            case T1:
                cooldown = 34.2;
                break;
            case T2:
                cooldown = 33.5;
                break;
            case T3:
                cooldown = 31.4;
                break;
            case T4:
                cooldown = 30.3;
                break;
            case T5:
                cooldown = 25;
                break;
            default:
                cooldown = 0;
        }
        if (cooldown > 0)
            lores.add(CommunicationManager.format(COOLDOWN, Double.toString(cooldown)));

        // Set price
        int price = getPrice(tier);
        if (price >= 0) {
            lores.add("");
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));
        }

        // Create item
        return ItemFactory.createItem(
                Material.YELLOW_DYE,
                name,
                ItemFactory.HIDE_ENCHANT_FLAGS,
                ItemFactory.glow(),
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
        return toCheck.getType() == Material.YELLOW_DYE && lore.stream().anyMatch(line -> line.contains(
                EFFECT.toString().replace("%s", "")));
    }
}
