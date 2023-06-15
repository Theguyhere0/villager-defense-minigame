package me.theguyhere.villagerdefense.plugin.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class to manage {@link ItemStack} manipulations.
 */
public class ItemStackBuilder {
	/**
	 * Flags for creating normal items with enchants and/or lore.
	 */
	public static final boolean[] NORMAL_FLAGS = {false, false};
	/**
	 * Flags for creating items with hidden enchants.
	 */
	public static final boolean[] HIDE_ENCHANT_FLAGS = {true, false};
	/**
	 * Flags for creating items with hidden enchants and attributes, mostly for buttons.
	 */
	public static final boolean[] BUTTON_FLAGS = {true, true};

	private final Material matID;
	private final String dispName;
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

	public ItemStackBuilder setFlags(boolean @NotNull [] flags) {
		this.flags = flags;
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
		ItemStack item = new ItemStack(matID);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

		// Set name
		if (dispName != null)
			meta.setDisplayName(dispName);

		// Set lore
		meta.setLore(lores);
		item.setItemMeta(meta);

		// Set persistent data and tags
		if (persistentData != null)
			persistentData.forEach((namespacedKey, integer) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.INTEGER, integer));
		if (persistentData2 != null)
			persistentData2.forEach((namespacedKey, doubleVal) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.DOUBLE, doubleVal));
		if (persistentTags != null)
			persistentTags.forEach((namespacedKey, string) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.STRING, string));
		if (persistentFlags != null)
			persistentFlags.forEach((namespacedKey, bool) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.BOOLEAN, bool));

		// Set enchants
		if (!(enchants == null))
			enchants.forEach((k, v) -> meta.addEnchant(k, v, true));
		if (flags != null && flags[0])
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		// Set attribute flag
		if (flags != null && flags[1])
			meta.addItemFlags(ItemFlag.values());

		// Set attribute mods
		meta.setAttributeModifiers(attributes);

		// Build
		item.setItemMeta(meta);
		return item;
	}

	// Dummy enchant for glowing buttons
	@NotNull
	public static HashMap<Enchantment, Integer> glow() {
		HashMap<Enchantment, Integer> enchants = new HashMap<>();
		enchants.put(Enchantment.DURABILITY, 1);
		return enchants;
	}

	// Creates an ItemStack using only material, name, and lore list
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		List<String> lores,
		String... moreLores
	) {
		// Create ItemStack
		ItemStack item = new ItemStack(matID);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

		// Set name
		if (!(dispName == null))
			meta.setDisplayName(dispName);

		// Set lore
		if (lores != null)
			lores.addAll(Arrays.asList(moreLores));
		meta.setLore(lores);
		item.setItemMeta(meta);

		return item;
	}

	// Creates an ItemStack using material, name, enchants, flags, and lore
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		boolean[] flags,
		HashMap<Enchantment, Integer> enchants,
		String... lores
	) {
		return createItem(matID, dispName, flags, enchants, Arrays.asList(lores));
	}

	// Creates an ItemStack using material, name, enchants, flags, and lore list
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		boolean[] flags,
		HashMap<Enchantment, Integer> enchants,
		List<String> lores
	) {
		return createItem(matID, dispName, flags, enchants, lores, null);
	}

	// Creates an ItemStack using material, name, enchants, flags, lore list, and attribute mod map
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		boolean[] flags,
		HashMap<Enchantment, Integer> enchants,
		List<String> lores,
		Multimap<Attribute, AttributeModifier> attributes
	) {
		return createItem(matID, dispName, flags, enchants, lores, attributes, null, null,
			null
		);
	}

	// Creates an ItemStack using material, name, enchants, flags, lore list, and attribute mod map
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		boolean[] flags,
		HashMap<Enchantment, Integer> enchants,
		List<String> lores,
		Multimap<Attribute, AttributeModifier> attributes,
		HashMap<NamespacedKey, Integer> persistentData,
		HashMap<NamespacedKey, Double> persistentData2,
		HashMap<NamespacedKey, String> persistentTags
	) {
		return createItem(matID, dispName, flags, enchants, lores, attributes, persistentData, persistentData2,
			persistentTags, null);
	}
	// Creates an ItemStack using material, name, enchants, flags, lore list, and attribute mod map
	@NotNull
	public static ItemStack createItem(
		Material matID,
		String dispName,
		boolean[] flags,
		HashMap<Enchantment, Integer> enchants,
		List<String> lores,
		Multimap<Attribute, AttributeModifier> attributes,
		HashMap<NamespacedKey, Integer> persistentData,
		HashMap<NamespacedKey, Double> persistentData2,
		HashMap<NamespacedKey, String> persistentTags,
		HashMap<NamespacedKey, Boolean> persistentFlags
	) {
		// Create ItemStack
		ItemStack item = createItem(matID, dispName, lores);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

		// Set persistent data and tags
		if (persistentData != null)
			persistentData.forEach((namespacedKey, integer) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.INTEGER, integer));
		if (persistentData2 != null)
			persistentData2.forEach((namespacedKey, doubleVal) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.DOUBLE, doubleVal));
		if (persistentTags != null)
			persistentTags.forEach((namespacedKey, string) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.STRING, string));
		if (persistentFlags != null)
			persistentFlags.forEach((namespacedKey, bool) ->
				meta
					.getPersistentDataContainer()
					.set(namespacedKey, PersistentDataType.BOOLEAN, bool));

		// Set enchants
		if (!(enchants == null))
			enchants.forEach((k, v) -> meta.addEnchant(k, v, true));
		if (flags != null && flags[0])
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		// Set attribute flag
		if (flags != null && flags[1])
			meta.addItemFlags(ItemFlag.values());

		// Set attribute mods
		meta.setAttributeModifiers(attributes);

		// Build
		item.setItemMeta(meta);
		return item;
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

	// Set the amount of an item
	@NotNull
	public static ItemStack setAmount(ItemStack item, int amount) {
		ItemStack newItem = item.clone();
		newItem.setAmount(Math.max(amount, 0));
		return newItem;
	}

	// Creates an ItemStack that has potion meta
	@NotNull
	public static ItemStack createPotionItem(Material matID, PotionData potionData, String dispName, String... lores) {
		return createPotionItems(matID, potionData, 1, dispName, lores);
	}

	// Creates an ItemStack using material, amount, name, and lore
	@NotNull
	public static ItemStack createItems(Material matID, int amount, String dispName, String... lores) {
		// Create ItemStack
		ItemStack item = new ItemStack(matID, amount);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

		// Set name
		if (!(dispName == null))
			meta.setDisplayName(dispName);

		// Set lore
		List<String> lore = new ArrayList<>();
		Collections.addAll(lore, lores);
		meta.setLore(lore);
		item.setItemMeta(meta);

		return item;
	}

	// Creates an ItemStack of multiple items that has potion meta
	@NotNull
	public static ItemStack createPotionItems(
		Material matID,
		PotionData potionData,
		int amount,
		String dispName,
		String... lores
	) {
		// Create ItemStack
		ItemStack item = new ItemStack(matID, amount);
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		PotionMeta pot = (PotionMeta) meta;

		// Set name
		if (!(dispName == null))
			meta.setDisplayName(dispName);

		// Set lore
		List<String> lore = new ArrayList<>();
		Collections.addAll(lore, lores);
		meta.setLore(lore);

		// Set potion data
		pot.setBasePotionData(potionData);
		item.setItemMeta(meta);

		return item;
	}

	@NotNull
	public static ItemStack buildNothing() {
		return new ItemStack(Material.AIR);
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
}
