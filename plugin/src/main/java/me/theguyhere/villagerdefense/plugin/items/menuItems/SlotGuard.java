package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class SlotGuard extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		return ItemFactory.createItem(
			Material.LIGHT_GRAY_STAINED_GLASS_PANE,
			CommunicationManager.format("&7&l" + LanguageManager.messages.restricted)
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck);
	}
}
