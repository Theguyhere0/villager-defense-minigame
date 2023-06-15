package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class CrystalConverter extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		return ItemStackBuilder.createItem(Material.DIAMOND,
			CommunicationManager.format("&b&l" + String.format(
				LanguageManager.names.crystalConverter,
				LanguageManager.names.crystal
			)), ItemStackBuilder.HIDE_ENCHANT_FLAGS, ItemStackBuilder.glow()
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck);
	}
}
