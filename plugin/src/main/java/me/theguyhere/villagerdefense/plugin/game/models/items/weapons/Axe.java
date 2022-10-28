package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Axe extends VDWeapon {
    @NotNull
    public static ItemStack create(AxeType type) {
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Set material
        Material mat;
        switch (type) {
            case T1:
                mat = Material.WOODEN_AXE;
                break;
            case T2:
            case T3:
            case T4:
                mat = Material.STONE_AXE;
                break;
            case T5:
            case T6:
            case T7:
                mat = Material.IRON_AXE;
                break;
            case T8:
            case T9:
                mat = Material.DIAMOND_AXE;
                break;
            case T10:
                mat = Material.NETHERITE_AXE;
                break;
            default:
                mat = Material.GOLDEN_AXE;
        }

        // Set name
        String name;
        switch (type) {
            case T1:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t1.name),
                        "[T1]"
                );
                break;
            case T2:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t2.name),
                        "[T2]"
                );
                break;
            case T3:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t3.name),
                        "[T3]"
                );
                break;
            case T4:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t4.name),
                        "[T4]"
                );
                break;
            case T5:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t5.name),
                        "[T5]"
                );
                break;
            case T6:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t6.name),
                        "[T6]"
                );
                break;
            case T7:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t7.name),
                        "[T7]"
                );
                break;
            case T8:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t8.name),
                        "[T8]"
                );
                break;
            case T9:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t9.name),
                        "[T9]"
                );
                break;
            case T10:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.axes.t10.name),
                        "[T10]"
                );
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case T1:
                description = LanguageManager.itemLore.axes.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.axes.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.axes.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.axes.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.axes.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.axes.t6.description;
                break;
            case T7:
                description = LanguageManager.itemLore.axes.t7.description;
                break;
            case T8:
                description = LanguageManager.itemLore.axes.t8.description;
                break;
            case T9:
                description = LanguageManager.itemLore.axes.t9.description;
                break;
            case T10:
                description = LanguageManager.itemLore.axes.t10.description;
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

        // Set main damage
        int damageLow, damageHigh;
        switch (type) {
            case T1:
                damageLow = 55;
                damageHigh = 80;
                break;
            case T2:
                damageLow = 65;
                damageHigh = 105;
                break;
            case T3:
                damageLow = 75;
                damageHigh = 105;
                break;
            case T4:
                damageLow = 85;
                damageHigh = 110;
                break;
            case T5:
                damageLow = 95;
                damageHigh = 140;
                break;
            case T6:
                damageLow = 105;
                damageHigh = 145;
                break;
            case T7:
                damageLow = 115;
                damageHigh = 155;
                break;
            case T8:
                damageLow = 130;
                damageHigh = 200;
                break;
            case T9:
                damageLow = 145;
                damageHigh = 215;
                break;
            case T10:
                damageLow = 165;
                damageHigh = 250;
                break;
            default:
                damageLow = damageHigh = 0;
        }
        if (damageLow == damageHigh)
            lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
                    Integer.toString(damageLow))));
        else lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
                damageLow + "-" + damageHigh)));

        // Set crit damage
        if (damageLow == damageHigh)
            lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
                    Integer.toString((int) (damageLow * 1.5)))));
        else lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
                (int) (damageLow * 1.25) + "-" + (int) (damageHigh * 1.5))));

        // Set attack speed
        attributes.put(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier(ItemMetaKey.ATTACK_SPEED.name(), -3.2,
                        AttributeModifier.Operation.ADD_NUMBER));
        lores.add(CommunicationManager.format(SPEED, Double.toString(0.8)));

        // Set dummy damage
        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), 0,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        attributes.put(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(ItemMetaKey.DUMMY.name(), 19,
                        AttributeModifier.Operation.ADD_NUMBER));

        // Set durability
        int durability;
        switch (type) {
            case T1:
                durability = 85;
                break;
            case T2:
                durability = 120;
                break;
            case T3:
                durability = 145;
                break;
            case T4:
                durability = 165;
                break;
            case T5:
                durability = 235;
                break;
            case T6:
                durability = 275;
                break;
            case T7:
                durability = 325;
                break;
            case T8:
                durability = 470;
                break;
            case T9:
                durability = 570;
                break;
            case T10:
                durability = 800;
                break;
            default: durability = 0;
        }
        lores.add(CommunicationManager.format(DURABILITY,
                new ColoredMessage(ChatColor.GREEN, Integer.toString(durability)).toString() +
                        new ColoredMessage(ChatColor.WHITE, " / " + durability)));

        // Set price
        int price;
        switch (type) {
            case T1:
                price = 200;
                break;
            case T2:
                price = 255;
                break;
            case T3:
                price = 300;
                break;
            case T4:
                price = 340;
                break;
            case T5:
                price = 415;
                break;
            case T6:
                price = 470;
                break;
            case T7:
                price = 515;
                break;
            case T8:
                price = 650;
                break;
            case T9:
                price = 750;
                break;
            case T10:
                price = 960;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        // Create item
        ItemStack item = ItemManager.createItem(mat, name, ItemManager.BUTTON_FLAGS, null, lores, attributes);
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
        return toCheck.getType().toString().contains("AXE") && lore.stream().anyMatch(line -> line.contains(
                MAIN_DAMAGE.toString().replace("%s", "")));
    }

    public enum AxeType{
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10
    }
}
