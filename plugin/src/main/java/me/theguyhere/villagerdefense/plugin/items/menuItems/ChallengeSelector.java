package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ChallengeSelector extends VDMenuItem {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(Material.NETHER_STAR,
                CommunicationManager.format("&9&l" + LanguageManager.names.challengeSelection),
                ItemManager.HIDE_ENCHANT_FLAGS, ItemManager.glow());
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
