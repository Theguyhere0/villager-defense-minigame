package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

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

public abstract class Scythe extends VDWeapon {
    @NotNull
    public static ItemStack create(double difficulty) {
        int level = Math.max(getLevel(difficulty) * 2 - 4, 1);
        Material mat;
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Set material
        switch ((level - 1) / 8) {
            case 0:
                mat = Material.WOODEN_HOE;
                break;
            case 1:
                mat = Material.STONE_HOE;
                break;
            case 2:
                mat = Material.IRON_HOE;
                break;
            case 3:
                mat = Material.DIAMOND_HOE;
                break;
            default:
                mat = Material.NETHERITE_HOE;
        }

        // Add space in lore from name
        lores.add("");

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_PENETRATING));

        // Set main damage
        int damageLow = 16 + 4 * ((level - 1) / 2);
        int damageHigh = 30 + 6 * (level / 2);
        lores.add(CommunicationManager.format(MAIN_DAMAGE, new ColoredMessage(ChatColor.RED,
                damageLow + "-" + damageHigh)));

        // Set crit damage
        lores.add(CommunicationManager.format(CRIT_DAMAGE, new ColoredMessage(ChatColor.DARK_PURPLE,
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

        // Set price
        int price = (int) (190 + 60 * level * Math.pow(Math.E, (level - 1) / 50d));
        lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                price));

        // Set name, make unbreakable, and return
        return ItemManager.makeUnbreakable(ItemManager.createItem(mat, CommunicationManager.format(
                        new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.scythe), Integer.toString(level)),
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
        return toCheck.getType().toString().contains("HOE") && lore.stream().anyMatch(line -> line.contains(
                MAIN_DAMAGE.toString().replace("%s", "")));
    }
}
