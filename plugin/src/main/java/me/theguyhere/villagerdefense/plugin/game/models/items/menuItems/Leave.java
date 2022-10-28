package me.theguyhere.villagerdefense.plugin.game.models.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Leave extends VDMenuItem {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(Material.BARRIER,
                CommunicationManager.format("&c&l" + LanguageManager.messages.leave),
                ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.glow());
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
