package me.theguyhere.villagerdefense.plugin.items.weapons;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class OrcClub extends VDWeapon {
	private static final String ORC_CLUB = "orc-club";

	@NotNull
	public static ItemStack create() {
		HashMap<Enchantment, Integer> enchant = new HashMap<>();
		enchant.put(Enchantment.KNOCKBACK, 5);
		HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
		persistentTags.put(ITEM_TYPE_KEY, ORC_CLUB);

		// Set description
		List<String> lores = new ArrayList<>(CommunicationManager.formatDescriptionList(
			ChatColor.GRAY, LanguageManager.kits.orc.items.clubDesc, Constants.LORE_CHAR_LIMIT));

		// Create item
		return new ItemStackBuilder(
			Material.STICK,
			formatName(ChatColor.GREEN, LanguageManager.kits.orc.items.club, Tier.UNIQUE)
		)
			.setLores(lores.toArray(new String[0]))
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