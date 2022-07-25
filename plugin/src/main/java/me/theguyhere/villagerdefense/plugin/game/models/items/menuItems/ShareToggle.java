package me.theguyhere.villagerdefense.plugin.game.models.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ShareToggle extends VDMenuItem {
    @NotNull
    public static ItemStack create(boolean sharing) {
        return ItemManager.createItem(Material.DISPENSER,
                CommunicationManager.format("&b&l" + LanguageManager.names.effectShare + ": " +
                        getToggleStatus(sharing)),
                ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.dummyEnchant());
    }

    public static boolean matches(ItemStack toCheck) {
        return create(true).equals(toCheck) || create(false).equals(toCheck);
    }
}
