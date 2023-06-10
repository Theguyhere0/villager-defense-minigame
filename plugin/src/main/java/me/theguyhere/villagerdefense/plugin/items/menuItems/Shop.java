package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Shop extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		return ItemFactory.createItem(
			Material.EMERALD,
			CommunicationManager.format("&2&l" + LanguageManager.names.itemShop),
			ItemFactory.HIDE_ENCHANT_FLAGS, ItemFactory.glow(),
			CommunicationManager.format("&7&o" + LanguageManager.messages.itemShopDesc)
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck);
	}
}
