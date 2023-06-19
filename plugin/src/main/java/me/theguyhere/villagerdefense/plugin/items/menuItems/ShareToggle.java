package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ShareToggle extends VDMenuItem {
	@NotNull
	public static ItemStack create(boolean sharing) {
		return new ItemStackBuilder(
			Material.DISPENSER,
			CommunicationManager.format("&b&l" + LanguageManager.names.effectShare + ": " +
				getToggleStatus(sharing))
		)
			.setHideEnchantFlags()
			.setGlowingIfTrue(true)
			.build();
	}

	public static boolean matches(ItemStack toCheck) {
		return create(true).equals(toCheck) || create(false).equals(toCheck);
	}
}
