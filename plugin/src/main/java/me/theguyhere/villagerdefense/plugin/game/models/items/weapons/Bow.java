package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

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

public abstract class Bow extends VDWeapon {
    @NotNull
    public static ItemStack create(Tier tier) {
        List<String> lores = new ArrayList<>();

        // Set name
        String name;
        switch (tier) {
            case T1:
                name = formatName(LanguageManager.itemLore.bows.t1.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.bows.t2.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.bows.t3.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.bows.t4.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.bows.t5.name, tier);
                break;
            case T6:
                name = formatName(LanguageManager.itemLore.bows.t6.name, tier);
                break;
            case T7:
                name = formatName(LanguageManager.itemLore.bows.t7.name, tier);
                break;
            case T8:
                name = formatName(LanguageManager.itemLore.bows.t8.name, tier);
                break;
            case T9:
                name = formatName(LanguageManager.itemLore.bows.t9.name, tier);
                break;
            case T10:
                name = formatName(LanguageManager.itemLore.bows.t10.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (tier) {
            case T1:
                description = LanguageManager.itemLore.bows.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.bows.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.bows.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.bows.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.bows.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.bows.t6.description;
                break;
            case T7:
                description = LanguageManager.itemLore.bows.t7.description;
                break;
            case T8:
                description = LanguageManager.itemLore.bows.t8.description;
                break;
            case T9:
                description = LanguageManager.itemLore.bows.t9.description;
                break;
            case T10:
                description = LanguageManager.itemLore.bows.t10.description;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

        // Set range damage
        int damageLow, damageHigh;
        switch (tier) {
            case T1:
                damageLow = 6;
                damageHigh = 12;
                break;
            case T2:
                damageLow = 6;
                damageHigh = 14;
                break;
            case T3:
                damageLow = 8;
                damageHigh = 15;
                break;
            case T4:
                damageLow = 9;
                damageHigh = 18;
                break;
            case T5:
                damageLow = 12;
                damageHigh = 18;
                break;
            case T6:
                damageLow = 12;
                damageHigh = 21;
                break;
            case T7:
                damageLow = 14;
                damageHigh = 23;
                break;
            case T8:
                damageLow = 15;
                damageHigh = 26;
                break;
            case T9:
                damageLow = 17;
                damageHigh = 28;
                break;
            case T10:
                damageLow = 20;
                damageHigh = 30;
                break;
            default:
                damageLow = damageHigh = 0;
        }
        if (damageLow == damageHigh)
            lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                    String.format(LanguageManager.messages.perBlock, Integer.toString(damageLow)))));
        else lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                String.format(LanguageManager.messages.perBlock, damageLow + "-" + damageHigh))));

        // Set attack speed
        lores.add(CommunicationManager.format(SPEED, Double.toString(1)));

        // Set ammo cost
        lores.add(CommunicationManager.format(AMMO_COST, new ColoredMessage(ChatColor.RED, Integer.toString(1))));

        // Set durability
        int durability;
        switch (tier) {
            case T1:
                durability = 100;
                break;
            case T2:
                durability = 140;
                break;
            case T3:
                durability = 165;
                break;
            case T4:
                durability = 190;
                break;
            case T5:
                durability = 275;
                break;
            case T6:
                durability = 320;
                break;
            case T7:
                durability = 380;
                break;
            case T8:
                durability = 550;
                break;
            case T9:
                durability = 660;
                break;
            case T10:
                durability = 850;
                break;
            default: durability = 0;
        }
        lores.add(CommunicationManager.format(DURABILITY,
                new ColoredMessage(ChatColor.GREEN, Integer.toString(durability)).toString() +
                        new ColoredMessage(ChatColor.WHITE, " / " + durability)));

        // Set price
        int price;
        switch (tier) {
            case T1:
                price = 225;
                break;
            case T2:
                price = 290;
                break;
            case T3:
                price = 335;
                break;
            case T4:
                price = 390;
                break;
            case T5:
                price = 490;
                break;
            case T6:
                price = 540;
                break;
            case T7:
                price = 610;
                break;
            case T8:
                price = 760;
                break;
            case T9:
                price = 880;
                break;
            case T10:
                price = 1160;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        // Create item
        ItemStack item = ItemManager.createItem(Material.BOW, name, ItemManager.BUTTON_FLAGS, null, lores);
        if (durability == 0)
            return ItemManager.makeUnbreakable(item);
        else return item;
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
        return toCheck.getType() == Material.BOW && lore.stream().anyMatch(line -> line.contains(
                RANGE_DAMAGE.toString().replace("%s", "")));
    }
}