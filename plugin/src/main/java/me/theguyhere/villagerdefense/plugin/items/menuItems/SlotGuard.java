package me.theguyhere.villagerdefense.plugin.items.menuItems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class SlotGuard extends VDMenuItem {
	@NotNull
	public static ItemStack create() {
		HashMap<NamespacedKey, Boolean> persistentFlags = new HashMap<>();
		persistentFlags.put(INVISIBLE, true);

		return new ItemStackBuilder(
			Material.LIGHT_GRAY_STAINED_GLASS_PANE,
			CommunicationManager.format("&7&l" + LanguageManager.messages.restricted)
		)
			.setPersistentFlags(persistentFlags)
			.build();
	}

	@NotNull
	public static ItemStack createForHelmet(Player player) {
		HashMap<NamespacedKey, Boolean> persistentFlags = new HashMap<>();
		persistentFlags.put(INVISIBLE, true);

		return ItemStackBuilder.setHeadOwner(new ItemStackBuilder(
			Material.PLAYER_HEAD,
			CommunicationManager.format("&7&l" + LanguageManager.messages.restricted)
		)
			.setPersistentFlags(persistentFlags)
			.build(), player);
	}

	public static boolean matches(ItemStack toCheck) {
		return create().equals(toCheck) ||
			toCheck != null && createForHelmet(null).equals(ItemStackBuilder.setHeadOwner(
				toCheck,
				null
			));
	}
}
