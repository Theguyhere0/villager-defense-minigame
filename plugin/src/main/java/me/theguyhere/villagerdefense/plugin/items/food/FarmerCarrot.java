package me.theguyhere.villagerdefense.plugin.items.food;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class FarmerCarrot extends VDFood {
    private static final String FARMER_CARROT = "farmer-carrot";

    @NotNull
    public static ItemStack create() {
        HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
        HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
        persistentTags.put(ITEM_TYPE_KEY, FARMER_CARROT);

        // Set description
        List<String> lores = new ArrayList<>(CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.kits.farmer.items.carrotDesc, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set health heal
        int health = 15;
        persistentData.put(HEALTH_KEY, health);
        lores.add(new ColoredMessage(ChatColor.RED, "+" + health + " " + Utils.HP).toString());

        // Set hunger heal
        int hunger = 2;
        persistentData.put(HUNGER_KEY, health);
        lores.add(new ColoredMessage(ChatColor.BLUE, "+" + hunger + " " + Utils.HUNGER).toString());

        return ItemFactory.setAmount(ItemFactory.createItem(Material.CARROT,
                VDItem.formatName(ChatColor.GREEN, LanguageManager.kits.farmer.items.carrot, VDItem.Tier.UNIQUE),
                ItemFactory.BUTTON_FLAGS, null, lores, null, persistentData, null,
                persistentTags), 3);
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
        return FARMER_CARROT.equals(value);
    }
}
