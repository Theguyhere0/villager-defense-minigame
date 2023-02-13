package me.theguyhere.villagerdefense.plugin.game.models.items.food;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class FarmerCarrot extends VDFood {
    @NotNull
    public static ItemStack create() {
        // Set description
        List<String> lores = new ArrayList<>(CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.kits.farmer.items.carrotDesc, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set health heal
        int health = 15;
        lores.add(new ColoredMessage(ChatColor.RED, "+" + health + " " + Utils.HP).toString());

        // Set hunger heal
        int hunger = 3;
        lores.add(new ColoredMessage(ChatColor.BLUE, "+" + hunger + " " + Utils.HUNGER).toString());

        return ItemManager.setAmount(ItemManager.createItem(Material.CARROT,
                formatName(ChatColor.GREEN, LanguageManager.kits.farmer.items.carrot, Tier.UNIQUE), lores), 3);
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
        return lore.stream().anyMatch(line -> line.contains(Utils.HP) || line.contains(Utils.HUNGER));
    }
}
