package me.theguyhere.villagerdefense.GUI;

import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryItems {
    //	"No" button
    public static ItemStack no() {
        return Utils.createItem(Material.RED_CONCRETE, Utils.format("&c&lNO"));
    }

    //	"Yes" button
    public static ItemStack yes() {
        return Utils.createItem(Material.LIME_CONCRETE, Utils.format("&a&lYES"));
    }

    //	"Exit" button
    public static ItemStack exit() {
        return Utils.createItem(Material.BARRIER, Utils.format("&c&lEXIT"));
    }

    //	"Remove x" button
    public static ItemStack remove(String x) {
        return Utils.createItem(Material.LAVA_BUCKET, Utils.format("&4&lREMOVE " + x));
    }

    //	"Create x" button
    public static ItemStack create(String x) {
        return Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&a&lCreate " + x));
    }

    //	"Relocate x" button
    public static ItemStack relocate(String x) {
        return Utils.createItem(Material.END_PORTAL_FRAME, Utils.format("&a&lRelocate " + x));
    }

    //	"Teleport to x" button
    public static ItemStack teleport(String x) {
        return Utils.createItem(Material.ENDER_PEARL, Utils.format("&9&lTeleport to " + x));
    }

    //	"Center x" button
    public static ItemStack center(String x) {
        return Utils.createItem(Material.TARGET, Utils.format("&f&lCenter " + x),
                Utils.format("&7Center the x and z coordinates"));
    }
}
