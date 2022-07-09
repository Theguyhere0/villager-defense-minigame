package me.theguyhere.villagerdefense.plugin.game.models.items.abilities;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MessengerAbility extends VDAbility {
    @NotNull
    public static ItemStack create() {
        return ItemManager.createItem(
                Material.BLUE_DYE,
                new ColoredMessage(
                        ChatColor.LIGHT_PURPLE,
                        LanguageManager.kits.messenger.name + " " + LanguageManager.names.essence
                ).toString(),
                ItemManager.HIDE_ENCHANT_FLAGS,
                ItemManager.dummyEnchant(),
                new ColoredMessage(LanguageManager.messages.rightClick).toString()
        );
    }

    public static boolean matches(ItemStack toCheck) {
        return create().equals(toCheck);
    }
}
