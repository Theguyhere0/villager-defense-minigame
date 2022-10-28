package me.theguyhere.villagerdefense.plugin.game.models.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class CrystalConverter extends VDMenuItem {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(Material.DIAMOND,
                CommunicationManager.format("&b&l" + String.format(LanguageManager.names.crystalConverter,
                        LanguageManager.names.crystal)), ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.glow());
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
