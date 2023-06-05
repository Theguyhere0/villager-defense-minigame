package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryButtons {
    // "No" button
    @NotNull
    public static ItemStack no() {
        return ItemFactory.createItem(Material.RED_CONCRETE, CommunicationManager.format("&c&lNO"));
    }

    // "Yes" button
    @NotNull
    public static ItemStack yes() {
        return ItemFactory.createItem(Material.LIME_CONCRETE, CommunicationManager.format("&a&lYES"));
    }

    // "Exit" button
    @NotNull
    public static ItemStack exit() {
        return ItemFactory.createItem(Material.BARRIER, CommunicationManager.format("&c&l" +
                LanguageManager.messages.exit));
    }

    // "Remove x" button
    @NotNull
    public static ItemStack remove(String x) {
        return ItemFactory.createItem(Material.LAVA_BUCKET, CommunicationManager.format("&4&lREMOVE " + x));
    }

    // "Create x" button
    @NotNull
    public static ItemStack create(String x) {
        return ItemFactory.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&a&lCreate " + x));
    }

    // "Relocate x" button
    @NotNull
    public static ItemStack relocate(String x) {
        return ItemFactory.createItem(Material.END_PORTAL_FRAME, CommunicationManager.format("&a&lRelocate " + x));
    }

    // "Teleport to x" button
    @NotNull
    public static ItemStack teleport(String x) {
        return ItemFactory.createItem(Material.ENDER_PEARL, CommunicationManager.format("&9&lTeleport to " + x));
    }

    // "Center x" button
    @NotNull
    public static ItemStack center(String x) {
        return ItemFactory.createItem(
                Material.TARGET,
                CommunicationManager.format("&f&lCenter " + x),
                new ColoredMessage(ChatColor.GRAY,"Center the x and z coordinates").toString()
        );
    }

    // "New x" button
    @NotNull
    public static ItemStack newAdd(String x) {
        return ItemFactory.createItem(Material.NETHER_STAR, CommunicationManager.format("&a&lNew " + x));
    }

    // "Previous Page" button
    @NotNull
    public static ItemStack previousPage() {
        return ItemFactory.createItem(Material.PRISMARINE_SHARD, CommunicationManager.format("&e&lPrevious Page"));
    }

    // "Next Page" button
    @NotNull
    public static ItemStack nextPage() {
        return ItemFactory.createItem(Material.FEATHER, CommunicationManager.format("&d&lNext Page"));
    }

    // "No upgrade" button
    @NotNull
    public static ItemStack noUpgrade() {
        return ItemFactory.createItem(Material.RED_STAINED_GLASS_PANE,
                new ColoredMessage(ChatColor.DARK_RED, LanguageManager.messages.noUpgrades).toString(),
                ItemFactory.BUTTON_FLAGS, null);
    }
}
