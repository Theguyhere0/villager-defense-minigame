package me.theguyhere.villagerdefense.plugin.game.models.items.eggs;

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

public abstract class PetEgg extends VDEgg {
    @NotNull
    public static ItemStack create(PetEggType type) {
        List<String> lores = new ArrayList<>();

        // Set material
        Material mat;
        switch (type) {
            case DOG:
                mat = Material.WOLF_SPAWN_EGG;
                break;
            default:
                mat = Material.EGG;
        }

        // Set name
        String name;
        switch (type) {
            case DOG:
                name = new ColoredMessage(LanguageManager.mobs.dog).toString();
                break;
            default:
                name = "";
        }

        // Set description
        String description;
        switch (type) {
            case DOG:
                description = LanguageManager.mobLore.dog;
                break;
            default:
                description = "";
        }
        if (!description.isEmpty())
            lores.addAll(CommunicationManager.formatDescriptionList(
                    ChatColor.GRAY, description, Utils.LORE_CHAR_LIMIT));

        // Add space in lore from name
        lores.add("");

        // Set price
        int price;
        switch (type) {
            case DOG:
                price = 80;
                break;
            default: price = -1;
        }
        if (price >= 0)
            lores.add(CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a" +
                    price));

        return ItemManager.createItem(mat, name, lores);
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

    public enum PetEggType {
        DOG,
    }
}
