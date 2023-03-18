package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class OrcClub extends VDWeapon {
    @NotNull
    public static ItemStack create() {
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.KNOCKBACK, 5);

        // Set description
        List<String> lores = new ArrayList<>(CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.kits.orc.items.clubDesc, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Create item
        return ItemManager.createItem(Material.STICK,
                formatName(ChatColor.GREEN, LanguageManager.kits.orc.items.club, Tier.UNIQUE),
                ItemManager.BUTTON_FLAGS, enchant, lores);
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
        return toCheck.getType() == Material.STICK &&
                formatName(ChatColor.GREEN, LanguageManager.kits.orc.items.club, Tier.UNIQUE)
                        .equals(meta.getDisplayName());
    }
}
