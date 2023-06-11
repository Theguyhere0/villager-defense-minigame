package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class BoostToggle extends VDMenuItem {
	@NotNull
	public static ItemStack create(boolean boosted) {
		return ItemFactory.createItem(Material.FIREWORK_ROCKET,
			CommunicationManager.format("&b&l" + LanguageManager.names.boosts + ": " +
				getToggleStatus(boosted)),
			ItemFactory.HIDE_ENCHANT_FLAGS, ItemFactory.glow()
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create(true).equals(toCheck) || create(false).equals(toCheck);
	}
}
