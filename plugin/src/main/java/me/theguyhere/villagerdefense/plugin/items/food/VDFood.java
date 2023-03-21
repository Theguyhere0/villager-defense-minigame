package me.theguyhere.villagerdefense.plugin.items.food;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.items.VDItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class VDFood extends VDItem {
    public static final NamespacedKey HEALTH_KEY = new NamespacedKey(Main.plugin, "health");
    public static final NamespacedKey ABSORPTION_KEY = new NamespacedKey(Main.plugin, "absorption");
    public static final NamespacedKey HUNGER_KEY = new NamespacedKey(Main.plugin, "hunger");

    public static boolean matches(ItemStack toCheck) {
        return ShopFood.matches(toCheck) || FarmerCarrot.matches(toCheck);
    }
}
