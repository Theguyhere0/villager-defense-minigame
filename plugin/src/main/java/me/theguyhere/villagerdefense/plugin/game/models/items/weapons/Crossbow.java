package me.theguyhere.villagerdefense.plugin.game.models.items.weapons;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
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

public abstract class Crossbow extends VDWeapon {
    @NotNull
    public static ItemStack create(double difficulty) {
        int level = Math.max(getLevel(difficulty) - 2, 1);
        List<String> lores = new ArrayList<>();
        Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();

        // Add space in lore from name
        lores.add("");

        // Set attack type
        lores.add(CommunicationManager.format(ATTACK_TYPE, ATTACK_TYPE_NORMAL));

        // Set main damage
        int damageLow = 2 + (level - 1) / 2;
        int damageHigh = 3 + level / 2;
        lores.add(CommunicationManager.format(RANGE_DAMAGE, new ColoredMessage(ChatColor.DARK_AQUA,
                String.format(LanguageManager.messages.perBlock, damageLow + "-" + damageHigh))));

        // Note attack speed (can't really change it)
        lores.add(CommunicationManager.format(SPEED, Double.toString(1)));

        // Set price
        int price = (int) (185 + 55 * level * Math.pow(Math.E, (level - 1) / 50d));
        lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                price));

        // Set name, make unbreakable, and return
        return ItemManager.makeUnbreakable(ItemManager.createItem(Material.CROSSBOW, CommunicationManager.format(
                        new ColoredMessage(ChatColor.GRAY, LanguageManager.messages.bow), Integer.toString(level)),
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
        return toCheck.getType() == Material.CROSSBOW && lore.stream().anyMatch(line -> line.contains(
                RANGE_DAMAGE.toString().replace("%s", "")));
    }
}
