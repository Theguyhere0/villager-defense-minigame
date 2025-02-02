package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.items.ItemManager;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryButtons {
    // "No" button
    public static ItemStack no() {
        return ItemManager.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&lNO"));
    }

    // "Yes" button
    public static ItemStack yes() {
        return ItemManager.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&lYES"));
    }

    // "Exit" button
    public static ItemStack exit() {
        return ItemManager.createItem(Material.BARRIER, CommunicationManager.format("&c&l" +
                LanguageManager.messages.exit));
    }

    // "Remove x" button
    public static ItemStack remove(String x) {
        return ItemManager.createItem(Material.LAVA_BUCKET, CommunicationManager.format("&4&lREMOVE " + x));
    }

    // "Create x" button
    public static ItemStack create(String x) {
        return ItemManager.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&a&lCreate " + x));
    }

    // "Relocate x" button
    public static ItemStack relocate(String x) {
        return ItemManager.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&a&lRelocate " + x));
    }

    // "Teleport to x" button
    public static ItemStack teleport(String x) {
        return ItemManager.createItem(Material.ENDER_PEARL, CommunicationManager.format("&9&lTeleport to " + x));
    }

    // "Center x" button
    public static ItemStack center(String x) {
        return ItemManager.createItem(
                Material.TARGET,
                CommunicationManager.format("&f&lCenter " + x),
                CommunicationManager.format("&7Center the x and z coordinates")
        );
    }

    // "New x" button
    public static ItemStack newAdd(String x) {
        return ItemManager.createItem(Material.NETHER_STAR, CommunicationManager.format("&a&lNew " + x));
    }

    // "Previous Page" button
    public static ItemStack previousPage() {
        return ItemManager.createItem(Material.PRISMARINE_SHARD, CommunicationManager.format("&e&lPrevious Page"));
    }

    // "Next Page" button
    public static ItemStack nextPage() {
        return ItemManager.createItem(Material.FEATHER, CommunicationManager.format("&d&lNext Page"));
    }
}
