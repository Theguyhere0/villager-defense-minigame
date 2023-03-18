package me.theguyhere.villagerdefense.plugin.items.armor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
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

public abstract class Helmet extends VDArmor{
    @NotNull
    public static ItemStack create(Tier tier) {
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
        HashMap<Enchantment, Integer> enchant = new HashMap<>();

        // Set material
        Material mat;
        switch (tier) {
            case T1:
                mat = Material.LEATHER_HELMET;
                break;
            case T2:
                mat = Material.CHAINMAIL_HELMET;
                break;
            case T3:
                mat = Material.IRON_HELMET;
                break;
            case T4:
                mat = Material.DIAMOND_HELMET;
                break;
            case T5:
                mat = Material.NETHERITE_HELMET;
                break;
            case T6:
                mat = Material.NETHERITE_HELMET;
                enchant.put(Enchantment.DURABILITY, 3);
                break;
            default:
                mat = Material.GOLDEN_HELMET;
        }

        // Set name
        String name;
        switch (tier) {
            case T1:
                name = formatName(LanguageManager.itemLore.helmets.t1.name, tier);
                break;
            case T2:
                name = formatName(LanguageManager.itemLore.helmets.t2.name, tier);
                break;
            case T3:
                name = formatName(LanguageManager.itemLore.helmets.t3.name, tier);
                break;
            case T4:
                name = formatName(LanguageManager.itemLore.helmets.t4.name, tier);
                break;
            case T5:
                name = formatName(LanguageManager.itemLore.helmets.t5.name, tier);
                break;
            case T6:
                name = formatName(LanguageManager.itemLore.helmets.t6.name, tier);
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (tier) {
            case T1:
                description = LanguageManager.itemLore.helmets.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.helmets.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.helmets.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.helmets.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.helmets.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.helmets.t6.description;
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
                armor = 2;
                break;
            case T2:
                armor = 5;
                break;
            case T3:
                armor = 8;
                break;
            case T4:
                armor = 10;
                break;
            case T5:
                armor = 14;
                break;
            case T6:
                armor = 16;
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
                toughness = 1;
                break;
            case T4:
                toughness = 3;
                break;
            case T5:
                toughness = 5;
                break;
            case T6:
                toughness = 8;
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
                price = 130;
                break;
            case T2:
                price = 215;
                break;
            case T3:
                price = 310;
                break;
            case T4:
                price = 385;
                break;
            case T5:
                price = 555;
                break;
            case T6:
                price = 715;
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
        return toCheck.getType().toString().contains("HELMET") && lore.stream().anyMatch(line -> line.contains(
                ARMOR.toString().replace("%s", "")));
    }
}
