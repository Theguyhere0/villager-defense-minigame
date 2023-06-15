package me.theguyhere.villagerdefense.plugin.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class to manage {@link ItemStack} manipulations.
 */
public class ItemStackBuilder {
	private final Material matID;
	private final String dispName;
	private PotionData potionData;
	private int amount = 1;
	private boolean[] flags = {};
	private HashMap<Enchantment, Integer> enchants = new HashMap<>();
	private final List<String> lores = new ArrayList<>();
	private Multimap<Attribute, AttributeModifier> attributes = ArrayListMultimap.create();
	private HashMap<NamespacedKey, Integer> persistentData = new HashMap<>();
	private HashMap<NamespacedKey, Double> persistentData2 = new HashMap<>();
	private HashMap<NamespacedKey, String> persistentTags = new HashMap<>();
	private HashMap<NamespacedKey, Boolean> persistentFlags = new HashMap<>();

	public ItemStackBuilder(@NotNull Material matID, String dispName) {
		this.matID = matID;
		this.dispName = dispName;
	}

	public ItemStackBuilder setPotionData(@NotNull PotionData potionData) {
		this.potionData = potionData;
		return this;
	}

	public ItemStackBuilder setAmount(int amount) {
		if (amount > 0)
			this.amount = amount;
		return this;
	}

	/**
	 * Flags for creating items with hidden enchants and attributes, mostly for buttons.
	 */
	public ItemStackBuilder setButtonFlags() {
		this.flags = new boolean[]{true, true};
		return this;
	}

	/**
	 * Flags for creating items with hidden enchants.
	 */
	public ItemStackBuilder setHideEnchantFlags() {
		this.flags = new boolean[]{true, false};
		return this;
	}

	public ItemStackBuilder setEnchants(@NotNull HashMap<Enchantment, Integer> enchants) {
		this.enchants = enchants;
		return this;
	}

	public ItemStackBuilder setGlowingIfTrue(boolean condition) {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);

		if (condition)
			this.enchants = enchants;
		return this;
	}

	public ItemStackBuilder setLores(String... lores) {
		Collections.addAll(this.lores, lores);
		return this;
	}

	public ItemStackBuilder setAttributes(@NotNull Multimap<Attribute, AttributeModifier> attributes) {
		this.attributes = attributes;
		return this;
	}

	public ItemStackBuilder setPersistentData(@NotNull HashMap<NamespacedKey, Integer> persistentData) {
		this.persistentData = persistentData;
		return this;
	}

	public ItemStackBuilder setPersistentData2(@NotNull HashMap<NamespacedKey, Double> persistentData2) {
		this.persistentData2 = persistentData2;
		return this;
	}

	public ItemStackBuilder setPersistentTags(@NotNull HashMap<NamespacedKey, String> persistentTags) {
		this.persistentTags = persistentTags;
		return this;
	}

	public ItemStackBuilder setPersistentFlags(@NotNull HashMap<NamespacedKey, Boolean> persistentFlags) {
		this.persistentFlags = persistentFlags;
		return this;
	}

	public ItemStack build() {
		// Create ItemStack
		ItemStack item = new ItemStack(matID, amount);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

		// Set name
		if (dispName != null)
			meta.setDisplayName(dispName);

		// Set lore
		meta.setLore(lores);
		item.setItemMeta(meta);

		// Set potion data
		if (potionData != null)
			((PotionMeta) meta).setBasePotionData(potionData);

		// Set persistent data and tags
		persistentData.forEach((namespacedKey, integer) ->
			meta
				.getPersistentDataContainer()
				.set(namespacedKey, PersistentDataType.INTEGER, integer));
		persistentData2.forEach((namespacedKey, doubleVal) ->
			meta
				.getPersistentDataContainer()
				.set(namespacedKey, PersistentDataType.DOUBLE, doubleVal));
		persistentTags.forEach((namespacedKey, string) ->
			meta
				.getPersistentDataContainer()
				.set(namespacedKey, PersistentDataType.STRING, string));
		persistentFlags.forEach((namespacedKey, bool) ->
			meta
				.getPersistentDataContainer()
				.set(namespacedKey, PersistentDataType.BOOLEAN, bool));

		// Set enchants
		enchants.forEach((k, v) -> meta.addEnchant(k, v, true));
		if (flags.length > 0 && flags[0])
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		// Set attribute flag
		if (flags.length > 1 && flags[1])
			meta.addItemFlags(ItemFlag.values());

		// Set attribute mods
		meta.setAttributeModifiers(attributes);

		// Build
		item.setItemMeta(meta);
		return item;
	}

	@NotNull
	public static ItemStack buildNothing() {
		return new ItemStack(Material.AIR);
	}

	// Makes an item unbreakable
	@NotNull
	public static ItemStack makeUnbreakable(@NotNull ItemStack item) {
		ItemStack newItem = item.clone();
		ItemMeta meta = newItem.getItemMeta();

		if (item
			.getType()
			.getMaxDurability() == 0)
			return item;
		try {
			Objects
				.requireNonNull(meta)
				.setUnbreakable(true);
			newItem.setItemMeta(meta);
			return removeLastLore(newItem);
		}
		catch (Exception e) {
			return item;
		}
	}

	// Rename an item
	@NotNull
	public static ItemStack rename(ItemStack item, String newName) {
		ItemStack newItem = item.clone();
		ItemMeta meta = newItem.getItemMeta();

		if (meta != null)
			meta.setDisplayName(newName);
		newItem.setItemMeta(meta);
		return newItem;
	}

	// Remove last lore on the list
	@NotNull
	public static ItemStack removeLastLore(@NotNull ItemStack itemStack) {
		ItemStack item = itemStack.clone();

		// Check for lore
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		List<String> lore = Objects.requireNonNull(meta.getLore());

		// Remove last lore and return
		lore.remove(lore.size() - 1);
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	// Set player a head belongs to
	@NotNull
	public static ItemStack setHeadOwner(@NotNull ItemStack itemStack, Player player) {
		if (itemStack.getType() != Material.PLAYER_HEAD)
			return itemStack;

		ItemStack item = itemStack.clone();
		SkullMeta meta = Objects.requireNonNull((SkullMeta) item.getItemMeta());
		if (player == null)
			meta.setOwnerProfile(null);
		else meta.setOwnerProfile(player.getPlayerProfile());
		item.setItemMeta(meta);
		return item;
	}
}
