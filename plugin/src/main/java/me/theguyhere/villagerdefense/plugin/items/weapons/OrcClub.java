package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public abstract class OrcClub extends VDWeapon {
	private static final String ORC_CLUB = "orc-club";

	@NotNull
	public static ItemStack create() {
		HashMap<Enchantment, Integer> enchant = new HashMap<>();
		enchant.put(Enchantment.KNOCKBACK, 5);
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, ORC_CLUB);

		// Set description
		LoreBuilder loreBuilder = new LoreBuilder();
		loreBuilder.addDescription(LanguageManager.kits.orc.items.clubDesc);

		// Create item
		return new ItemStackBuilder(
			Material.STICK,
			formatName(ChatColor.GREEN, LanguageManager.kits.orc.items.club, Tier.UNIQUE)
		)
			.setLores(loreBuilder)
			.setButtonFlags()
			.setEnchants(enchant)
			.setPersistentTags(persistentTags)
			.build();
	}

	public static boolean matches(ItemStack toCheck) {
		if (toCheck == null)
			return false;
		ItemMeta meta = toCheck.getItemMeta();
		if (meta == null)
			return false;
		String value = meta
			.getPersistentDataContainer()
			.get(ITEM_TYPE_KEY, PersistentDataType.STRING);
		if (value == null)
			return false;
		return ORC_CLUB.equals(value);
	}
}
