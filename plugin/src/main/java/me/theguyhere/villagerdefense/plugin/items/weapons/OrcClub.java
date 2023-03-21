package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class OrcClub extends VDWeapon {
    private static final String ORC_CLUB = "orc-club";

    @NotNull
    public static ItemStack create() {
        HashMap<Enchantment, Integer> enchant = new HashMap<>();
        enchant.put(Enchantment.KNOCKBACK, 5);
        HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
        persistentTags.put(ITEM_TYPE_KEY, ORC_CLUB);

        // Set description
        List<String> lores = new ArrayList<>(CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.kits.orc.items.clubDesc, Utils.LORE_CHAR_LIMIT));

        // Create item
        return ItemFactory.createItem(Material.STICK,
                formatName(ChatColor.GREEN, LanguageManager.kits.orc.items.club, Tier.UNIQUE),
                ItemFactory.BUTTON_FLAGS, enchant, lores, null, null, null,
                persistentTags);
    }

    public static boolean matches(ItemStack toCheck) {
        if (toCheck == null)
            return false;
        ItemMeta meta = toCheck.getItemMeta();
        if (meta == null)
            return false;
        String value = meta.getPersistentDataContainer().get(ITEM_TYPE_KEY, PersistentDataType.STRING);
        if (value == null)
            return false;
        return ORC_CLUB.equals(value);
    }
}
