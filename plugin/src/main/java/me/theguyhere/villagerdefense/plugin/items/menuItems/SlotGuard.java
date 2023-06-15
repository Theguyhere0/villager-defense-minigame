package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class SlotGuard extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		HashMap<NamespacedKey, Boolean> persistentFlags = new HashMap<>();
		persistentFlags.put(INVISIBLE, true);

		return ItemStackBuilder.createItem(
			Material.LIGHT_GRAY_STAINED_GLASS_PANE,
			CommunicationManager.format("&7&l" + LanguageManager.messages.restricted), null, null, null, null, null,
			null, null, persistentFlags
		);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck);
	}
}
