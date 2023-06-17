package me.theguyhere.villagerdefense.plugin.items;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
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
import java.util.stream.Collectors;

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
	private List<String> lores = new ArrayList<>();
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

	public ItemStackBuilder setLores(LoreBuilder constructor) {
		lores = constructor.build();
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
		List<String> lore = meta.getLore();
		if (lore == null)
			return itemStack;

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

	// Removes information about stats before an upgrade
	@NotNull
	public static ItemStack removeUpgradeInfo(@NotNull ItemStack itemStack) {
		ItemStack item = itemStack.clone();

		// Check for lore
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		List<String> lore = meta.getLore();
		List<String> newLore = new ArrayList<>();
		if (lore == null)
			return itemStack;

		// Run through lore to remove upgrade info
		lore.forEach(s -> {
			String formatted = s;
			while (formatted.contains(Constants.UPGRADE)) {
				String before = formatted.substring(0, formatted.indexOf(Constants.UPGRADE));
				before = before.substring(0, Math.max(before.lastIndexOf(" ") + 1,
					before.lastIndexOf(ChatColor.COLOR_CHAR) + 2));
				formatted = before + formatted.substring(
					formatted.indexOf(Constants.UPGRADE) + Constants.UPGRADE.length());
			}
			newLore.add(formatted);
		});
		meta.setLore(newLore);
		item.setItemMeta(meta);

		return item;
	}

	// Modify the cooldown of an item
	@NotNull
	public static ItemStack modifyCooldown(ItemStack itemStack, double modifier) {
		ItemStack item = itemStack.clone();
		ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
		List<String> lore = meta.getLore();

		// Ignore if no lore
		if (lore == null)
			return itemStack;

		List<Double> cooldowns = new ArrayList<>();
		int index = 0;
		for (int i = 0; i < lore.size(); i++) {
			if (lore
				.get(i)
				.contains(LanguageManager.messages.cooldown
					.replace("%s", ""))) {
				cooldowns = Arrays
					.stream(lore
						.get(i)
						.substring(2 + LanguageManager.messages.cooldown.length())
						.replace(ChatColor.BLUE.toString(), "")
						.replace(LanguageManager.messages.seconds.substring(3), "")
						.split(Constants.UPGRADE))
					.map(Double::parseDouble).collect(Collectors.toList());
				index = i;
			}
		}

		if (index == 0 || cooldowns.size() == 0)
			return itemStack;

		if (cooldowns.size() == 1)
			lore.set(index, lore
				.get(index)
				.replace(Double.toString(cooldowns.get(0)), String.format("%.2f", cooldowns.get(0) * modifier)));
		else if (cooldowns.size() == 2)
			lore.set(index, lore
				.get(index)
				.replace(Double.toString(cooldowns.get(0)), String.format("%.2f", cooldowns.get(0) * modifier))
				.replace(Double.toString(cooldowns.get(1)), String.format("%.2f", cooldowns.get(1) * modifier)));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
