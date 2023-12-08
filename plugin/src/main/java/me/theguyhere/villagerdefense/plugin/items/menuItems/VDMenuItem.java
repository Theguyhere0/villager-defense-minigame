package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.inventory.ItemStack;

public abstract class VDMenuItem extends VDItem {
	public static boolean matches(ItemStack toCheck) {
		return Shop.matches(toCheck) || KitSelector.matches(toCheck) || ChallengeSelector.matches(toCheck) ||
			BoostToggle.matches(toCheck) || CrystalConverter.matches(toCheck) || Leave.matches(toCheck) ||
			SlotGuard.matches(toCheck);
	}

	// Easy way to get a string for a toggle status
	protected static String getToggleStatus(boolean status) {
		String toggle;
		if (status)
			toggle = "&a&l" + LanguageManager.messages.onToggle;
		else toggle = "&c&l" + LanguageManager.messages.offToggle;
		return toggle;
	}
}
