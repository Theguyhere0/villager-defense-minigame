package me.theguyhere.villagerdefense;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Utils {
    //	Formats chat text
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    // Creates an ItemStack using only material, name, and lore
    public static ItemStack createItem(Material matID, String dispName, String ... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();

        // Set name
        if (!(dispName == null))
            meta.setDisplayName(dispName);

        // Set lore
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, lores);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    // Creates an ItemStack using only material, name, and lore
    public static ItemStack createItem(Material matID,
                                       String dispName,
                                       boolean enchantToggle,
                                       HashMap<Enchantment, Integer> enchants,
                                       String ... lores) {
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores);
        ItemMeta meta = item.getItemMeta();

        // Set enchants
        enchants.forEach((k, v) -> meta.addEnchant(k, v, false));
        if (enchantToggle)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

}
