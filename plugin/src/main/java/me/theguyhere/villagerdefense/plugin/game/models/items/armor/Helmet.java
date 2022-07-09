package me.theguyhere.villagerdefense.plugin.game.models.items.armor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.game.models.items.ItemMetaKey;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Helmet extends VDArmor{
    @NotNull
    public static ItemStack create(double difficulty) {
        int level = getLevel(difficulty);
        Material mat;
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Set material
        switch ((level - 1) / 8) {
            case 0:
                mat = Material.LEATHER_HELMET;
                break;
            case 1:
                mat = Material.CHAINMAIL_HELMET;
                break;
            case 2:
                mat = Material.IRON_HELMET;
                break;
            case 3:
                mat = Material.DIAMOND_HELMET;
                break;
            default:
                mat = Material.NETHERITE_HELMET;
        }

        // Add space in lore from name
        lores.add("");

        // Set armor
        int armor = 1 + level;
        lores.add(CommunicationManager.format(ARMOR, new ColoredMessage(ChatColor.AQUA, Integer.toString(armor))));

        // Set toughness
        int toughness = Math.max((level - 12)/ 2, 0);
        if (toughness > 0)
            lores.add(CommunicationManager.format(TOUGHNESS, new ColoredMessage(ChatColor.DARK_AQUA,
                    toughness + "%")));

        // Set weight
        int weight = ((level - 1) / 8) * 5;
        lores.add(CommunicationManager.format(WEIGHT, new ColoredMessage(ChatColor.DARK_PURPLE,
                Integer.toString(weight))));
        attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), 0,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributes.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), weight * .01,
                        AttributeModifier.Operation.ADD_NUMBER));

        // Set price
        int price = (int) (75 + 45 * level * Math.pow(Math.E, (level - 1) / 50d));
        lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                price));

        // Set name, make unbreakable, and return
        return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
                        new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.helmet), Integer.toString(level)),
                ItemManager.BUTTON_FLAGS, null, lores, attributes));
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
