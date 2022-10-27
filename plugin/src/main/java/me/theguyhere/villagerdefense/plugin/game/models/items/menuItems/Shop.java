package me.theguyhere.villagerdefense.plugin.game.models.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Shop extends VDMenuItem {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(
                Material.EMERALD,
                CommunicationManager.format("&2&l" + LanguageManager.names.itemShop),
                ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.glow(),
                CommunicationManager.format("&7&o" + String.format(LanguageManager.messages.itemShopDesc, "5"))
        );
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
