package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Crossbow extends VDWeapon {
    @NotNull
    public static ItemStack create(Tier tier) {
        List<String> lores = new ArrayList<>();
        HashMap<Enchantment, Integer> enchant = new HashMap<>();

        // Possibly set enchant
        switch (tier) {
            case T5:
            case T6:
                enchant.put(Enchantment.DURABILITY, 3);
        }

        // Set name
        String name;
        switch (tier) {
            case T1:
                name = formatName(LanguageManager.itemLore.crossbows.t1.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.crossbows.t2.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.crossbows.t3.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.crossbows.t4.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.crossbows.t5.name, tier);
                break;
            case T6:
                name = formatName(LanguageManager.itemLore.crossbows.t6.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (tier) {
            case T1:
                description = LanguageManager.itemLore.crossbows.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.crossbows.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.crossbows.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.crossbows.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.crossbows.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.crossbows.t6.description;
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
                damageLow = 75;
                damageHigh = 120;
                break;
            case T2:
                damageLow = 90;
                damageHigh = 155;
                break;
            case T3:
                damageLow = 110;
                damageHigh = 200;
                break;
            case T4:
                damageLow = 150;
                damageHigh = 225;
                break;
            case T5:
                damageLow = 180;
                damageHigh = 290;
                break;
            case T6:
                damageLow = 200;
                damageHigh = 350;
                break;
            default:
                damageLow = damageHigh = 0;
        }
        if (damageLow == damageHigh)
            lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                    Integer.toString(damageLow))));
        else lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                damageLow + "-" + damageHigh)));

        // Set pierce
        int pierce;
        switch (tier) {
            case T1:
            case T2:
                pierce = 1;
                break;
            case T3:
            case T4:
                pierce = 2;
                break;
            case T5:
                pierce = 3;
                break;
            case T6:
                pierce = 4;
                break;
            default:
                pierce = 0;
        }
        lores.add(CommunicationManager.format(PIERCE, new ColoredMessage(ChatColor.GOLD,
                Integer.toString(pierce))));

        // Set attack speed
        lores.add(CommunicationManager.format(SPEED, Double.toString(0.5)));

        // Set ammo cost
        lores.add(CommunicationManager.format(AMMO_COST, new ColoredMessage(ChatColor.RED, Integer.toString(3))));

        // Set durability
        int durability;
        switch (tier) {
            case T1:
                durability = 90;
                break;
            case T2:
                durability = 135;
                break;
            case T3:
                durability = 170;
                break;
            case T4:
                durability = 240;
                break;
            case T5:
                durability = 335;
                break;
            case T6:
                durability = 480;
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
                price = 270;
                break;
            case T2:
                price = 350;
                break;
            case T3:
                price = 470;
                break;
            case T4:
                price = 590;
                break;
            case T5:
                price = 730;
                break;
            case T6:
                price = 900;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        // Create item
        ItemStack item = ItemManager.createItem(Material.CROSSBOW, name, ItemManager.BUTTON_FLAGS, enchant, lores);
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
        return toCheck.getType() == Material.CROSSBOW && lore.stream().anyMatch(line -> line.contains(
                RANGE_DAMAGE.toString().replace("%s", "")));
    }

    public static int getPierce(ItemStack crossbow) {
        AtomicInteger pierce = new AtomicInteger();
        Objects.requireNonNull(Objects.requireNonNull(crossbow.getItemMeta()).getLore()).forEach(lore -> {
            if (lore.contains(LanguageManager.messages.pierce.replace("%s", "")))
                pierce.set(Integer.parseInt(lore.substring(2 + LanguageManager.messages.pierce.length()).
                        replace(ChatColor.BLUE.toString(), "")));
        });
        return pierce.get();
    }
}
