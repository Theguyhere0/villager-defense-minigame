package me.theguyhere.villagerdefense.plugin.game.models.items.armor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Leggings extends VDArmor{
    @NotNull
    public static ItemStack create(Tier tier) {
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        HashMap<Enchantment, Integer> enchant = new HashMap<>();

        // Set material
        Material mat;
        switch (tier) {
            case T1:
                mat = Material.LEATHER_LEGGINGS;
                break;
            case T2:
                mat = Material.CHAINMAIL_LEGGINGS;
                break;
            case T3:
                mat = Material.IRON_LEGGINGS;
                break;
            case T4:
                mat = Material.DIAMOND_LEGGINGS;
                break;
            case T5:
                mat = Material.NETHERITE_LEGGINGS;
                break;
            case T6:
                mat = Material.NETHERITE_LEGGINGS;
                enchant.put(Enchantment.DURABILITY, 3);
                break;
            default:
                mat = Material.GOLDEN_LEGGINGS;
        }

        // Set name
        String name;
        switch (tier) {
            case T1:
                name = formatName(LanguageManager.itemLore.leggings.t1.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.leggings.t2.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.leggings.t3.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.leggings.t4.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.leggings.t5.name, tier);
                break;
            case T6:
                name = formatName(LanguageManager.itemLore.leggings.t6.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (tier) {
            case T1:
                description = LanguageManager.itemLore.leggings.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.leggings.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.leggings.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.leggings.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.leggings.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.leggings.t6.description;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set armor
        int armor;
        switch (tier) {
            case T1:
                armor = 3;
                break;
            case T2:
                armor = 6;
                break;
            case T3:
                armor = 9;
                break;
            case T4:
                armor = 12;
                break;
            case T5:
                armor = 17;
                break;
            case T6:
                armor = 20;
                break;
            default:
                armor = 0;
        }
        if (armor > 0)
            lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));

        // Set toughness
        int toughness;
        switch (tier) {
            case T3:
                toughness = 2;
                break;
            case T4:
                toughness = 5;
                break;
            case T5:
                toughness = 7;
                break;
            case T6:
                toughness = 9;
                break;
            default:
                toughness = 0;
        }
        if (toughness > 0)
            lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
                    toughness + "%")));

        // Set weight
        int weight;
        switch (tier) {
            case T1:
                weight = 1;
                break;
            case T2:
                weight = 2;
                break;
            case T3:
                weight = 3;
                break;
            case T4:
                weight = 4;
                break;
            case T5:
            case T6:
                weight = 5;
                break;
            default:
                weight = 0;
        }
        lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
                Integer.toString(weight))));
        attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), 0,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), weight * .01,
                        AttributeModifier.Operation.ADD_NUMBER));

        // Set durability
        int durability;
        switch (tier) {
            case T1:
                durability = 85;
                break;
            case T2:
                durability = 145;
                break;
            case T3:
                durability = 225;
                break;
            case T4:
                durability = 310;
                break;
            case T5:
                durability = 425;
                break;
            case T6:
                durability = 475;
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
                price = 160;
                break;
            case T2:
                price = 260;
                break;
            case T3:
                price = 380;
                break;
            case T4:
                price = 470;
                break;
            case T5:
                price = 680;
                break;
            case T6:
                price = 880;
                break;
            default: price = -1;
        }
        if (price >= 0) {
            lores.add("");
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));
        }

        // Create item
        ItemStack item = ItemManager.createItem(mat, name, ItemManager.BUTTON_FLAGS, enchant, lores, attributes);
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
        return toCheck.getType().toString().contains("LEGGINGS") && lore.stream().anyMatch(line -> line.contains(
                ARMOR.toString().replace("%s", "")));
    }
}
