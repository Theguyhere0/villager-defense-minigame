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

import java.util.List;

public abstract class GoldenApple extends VDFood {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(Material.GOLDEN_APPLE, null,
                new ColoredMessage(ChatColor.RED, "+160 " + Utils.HP).toString(),
                new ColoredMessage(ChatColor.GOLD, "+50 " + Utils.HP).toString(),
                CommunicationManager.format("&2" + LanguageManager.messages.gems + ": &a800"));
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
        return toCheck.getType() == Material.GOLDEN_APPLE && lore.stream().anyMatch(line -> line.contains(
                new ColoredMessage(ChatColor.RED, "+80 " + Utils.HP).toString()) || line.contains(
                new ColoredMessage(ChatColor.GOLD, "+25 " + Utils.HP).toString()
        ));
    }
}