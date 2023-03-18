package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class BoostToggle extends VDMenuItem {
    @NotNull
    public static ItemStack create(boolean boosted) {
        return ItemManager.createItem(Material.FIREWORK_ROCKET,
                CommunicationManager.format("&b&l" + LanguageManager.names.boosts + ": " +
                        getToggleStatus(boosted)),
                ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.glow());
    }

    public static boolean matches(ItemStack toCheck) {
        return create(true).equals(toCheck) || create(false).equals(toCheck);
    }
}
