package me.theguyhere.villagerdefense.plugin.tools;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class to manage {@link ItemStack} manipulations.
 */
public class ItemManager {
    /** Flags for creating normal items with enchants and/or lore.*/
    public static final boolean[] NORMAL_FLAGS = {false, false};
    /** Flags for creating items with hidden enchants.*/
    public static final boolean[] HIDE_ENCHANT_FLAGS = {true, false};
    /** Flags for creating items with hidden enchants and attributes, mostly for buttons.*/
    public static final boolean[] BUTTON_FLAGS = {true, true};

    public static HashMap<Enchantment, Integer> dummyEnchant() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return enchants;
    }

    // Creates an ItemStack using only material, name, and lore
    @NotNull
    public static ItemStack createItem(Material matID, String dispName, String... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

        // Set name
        if (dispName != null)
            meta.setDisplayName(dispName);

        // Set lore
        List<String> lore = new ArrayList<>();
        Collections.addAll(lore, lores);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
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
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());

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

        item.setItemMeta(meta);

        return item;
    }

    // Makes an item unique so it becomes unstackable
    @NotNull
    public static ItemStack makeUnique(@NotNull ItemStack item) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();

        Random r = new Random();
        try {
            Objects.requireNonNull(meta).setCustomModelData(r.nextInt());
            newItem.setItemMeta(meta);
            return newItem;
        } catch (Exception e) {
            return item;
        }
    }

    // Makes an item unbreakable
    @NotNull
    public static ItemStack makeUnbreakable(@NotNull ItemStack item) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();

        if (item.getType().getMaxDurability() == 0)
            return item;
        try {
            Objects.requireNonNull(meta).setUnbreakable(true);
            newItem.setItemMeta(meta);
            return removeLastLore(newItem);
        } catch (Exception e) {
            return item;
        }
    }

    // Make an item into a splash potion
    @NotNull
    public static ItemStack makeSplash(ItemStack item) {
        ItemStack newItem = item.clone();
        if (newItem.getType() == Material.POTION)
            newItem.setType(Material.SPLASH_POTION);
        return newItem;
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
    public static ItemStack createPotionItems(Material matID,
                                              PotionData potionData,
                                              int amount,
                                              String dispName,
                                              String... lores) {
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

    // Dummy enchant for glowing buttons
    @NotNull
    public static HashMap<Enchantment, Integer> glow() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return enchants;
    }
}
