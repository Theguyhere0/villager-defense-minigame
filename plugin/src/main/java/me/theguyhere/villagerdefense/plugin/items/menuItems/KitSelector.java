package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class KitSelector extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		return ItemFactory.createItem(
			Material.CHEST,
			CommunicationManager.format("&9&l" + LanguageManager.names.kitSelection)
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck);
	}
}
