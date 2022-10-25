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

public abstract class Scythe extends VDWeapon {
    @NotNull
    public static ItemStack create(ScytheType type) {
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Set material
        Material mat;
        switch (type) {
            case REAPER1:
            case T1:
                mat = Material.WOODEN_HOE;
                break;
            case REAPER2:
            case T2:
            case T3:
            case T4:
                mat = Material.STONE_HOE;
                break;
            case REAPER3:
            case T5:
            case T6:
            case T7:
                mat = Material.IRON_HOE;
                break;
            case T8:
            case T9:
                mat = Material.DIAMOND_HOE;
                break;
            case T10:
                mat = Material.NETHERITE_HOE;
                break;
            default:
                mat = Material.GOLDEN_HOE;
        }

        // Set name
        String name;
        switch (type) {
            case REAPER1:
            case REAPER2:
            case REAPER3:
                name = new ColoredMessage(ChatColor.GREEN, LanguageManager.kits.reaper.items.scythe).toString();
                break;
            case T1:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t1.name),
                        "[T1]"
                );
                break;
            case T2:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t2.name),
                        "[T2]"
                );
                break;
            case T3:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t3.name),
                        "[T3]"
                );
                break;
            case T4:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t4.name),
                        "[T4]"
                );
                break;
            case T5:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t5.name),
                        "[T5]"
                );
                break;
            case T6:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t6.name),
                        "[T6]"
                );
                break;
            case T7:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t7.name),
                        "[T7]"
                );
                break;
            case T8:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t8.name),
                        "[T8]"
                );
                break;
            case T9:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t9.name),
                        "[T9]"
                );
                break;
            case T10:
                name = CommunicationManager.format(
                        new ColoredMessage(LanguageManager.itemLore.scythes.t10.name),
                        "[T10]"
                );
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case REAPER1:
            case REAPER2:
            case REAPER3:
                description = LanguageManager.kits.reaper.items.scytheDesc;
                break;
            case T1:
                description = LanguageManager.itemLore.scythes.t1.description;
                break;
            case T2:
                description = LanguageManager.itemLore.scythes.t2.description;
                break;
            case T3:
                description = LanguageManager.itemLore.scythes.t3.description;
                break;
            case T4:
                description = LanguageManager.itemLore.scythes.t4.description;
                break;
            case T5:
                description = LanguageManager.itemLore.scythes.t5.description;
                break;
            case T6:
                description = LanguageManager.itemLore.scythes.t6.description;
                break;
            case T7:
                description = LanguageManager.itemLore.scythes.t7.description;
                break;
            case T8:
                description = LanguageManager.itemLore.scythes.t8.description;
                break;
            case T9:
                description = LanguageManager.itemLore.scythes.t9.description;
                break;
            case T10:
                description = LanguageManager.itemLore.scythes.t10.description;
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
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_PENETRATING));

        // Set main damage
        int damageLow, damageHigh;
        switch (type) {
            case REAPER1:
                damageLow = damageHigh = 20;
                break;
            case REAPER2:
                damageLow = damageHigh = 25;
                break;
            case REAPER3:
                damageLow = damageHigh = 30;
                break;
            case T1:
                damageLow = 16;
                damageHigh = 25;
                break;
            case T2:
                damageLow = 19;
                damageHigh = 32;
                break;
            case T3:
                damageLow = 21;
                damageHigh = 33;
                break;
            case T4:
                damageLow = 24;
                damageHigh = 33;
                break;
            case T5:
                damageLow = 26;
                damageHigh = 40;
                break;
            case T6:
                damageLow = 29;
                damageHigh = 42;
                break;
            case T7:
                damageLow = 33;
                damageHigh = 44;
                break;
            case T8:
                damageLow = 35;
                damageHigh = 53;
                break;
            case T9:
                damageLow = 40;
                damageHigh = 56;
                break;
            case T10:
                damageLow = 45;
                damageHigh = 65;
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
                    Integer.toString((int) (damageLow * 1.4)))));
        else lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
                (int) (damageLow * 1.4) + "-" + (int) (damageHigh * 1.4))));

        // Set attack speed
        attributes.put(Attribute.GENERIC_ATTACK_SPEED,
                new AttributeModifier(ItemMetaKey.ATTACK_SPEED.name(), -.5,
                        AttributeModifier.Operation.ADD_NUMBER));
        lores.add(CommunicationManager.format(SPEED, Double.toString(3.5)));

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
                durability = 200;
                break;
            case T2:
                durability = 275;
                break;
            case T3:
                durability = 320;
                break;
            case T4:
                durability = 370;
                break;
            case T5:
                durability = 540;
                break;
            case T6:
                durability = 620;
                break;
            case T7:
                durability = 740;
                break;
            case T8:
                durability = 1050;
                break;
            case T9:
                durability = 1300;
                break;
            case T10:
                durability = 1800;
                break;
            default: durability = 0;
        }
        if (durability > 0)
            lores.add(CommunicationManager.format(DURABILITY,
                    new ColoredMessage(ChatColor.GREEN, Integer.toString(durability)).toString() +
                            new ColoredMessage(ChatColor.WHITE, " / " + durability)));

        // Set price
        int price;
        switch (type) {
            case T1:
                price = 190;
                break;
            case T2:
                price = 245;
                break;
            case T3:
                price = 380;
                break;
            case T4:
                price = 330;
                break;
            case T5:
                price = 415;
                break;
            case T6:
                price = 460;
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
                price = 970;
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
        return toCheck.getType().toString().contains("HOE") && lore.stream().anyMatch(line -> line.contains(
                MAIN_DAMAGE.toString().replace("%s", "")));
    }

    public enum ScytheType{
        REAPER1,
        REAPER2,
        REAPER3,
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
