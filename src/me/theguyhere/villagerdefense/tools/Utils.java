package me.theguyhere.villagerdefense.tools;

import me.theguyhere.villagerdefense.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class Utils {
    private final Main plugin;

    private static final int SECONDS_TO_TICKS = 20;

    public Utils(Main plugin) {
        this.plugin = plugin;
    }

    // Formats chat text
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    // Formats plugin notifications
    public static String notify(String msg) {
        return format("&2VD: &f" + msg);
    }

    // Creates an ItemStack using only material, name, and lore
    public static ItemStack createItem(Material matID, String dispName, String ... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();

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

    // Creates an ItemStack using material, name, enchants, flags, and lore
    public static ItemStack createItem(Material matID,
                                       String dispName,
                                       boolean[] flags,
                                       HashMap<Enchantment, Integer> enchants,
                                       String ... lores) {
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores);
        ItemMeta meta = item.getItemMeta();

        // Set enchants
        if (!(enchants == null))
            enchants.forEach((k, v) -> meta.addEnchant(k, v, false));
        if (flags[0])
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set attribute flag
        if (flags[1])
            meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    // Prepares a player to teleport into adventure mode
    public static void prepTeleAdventure(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
    }

    // Prepares a player to teleport into spectator mode
    public static void prepTeleSpectator(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
    }

    // Gets location data from a configuration path
    public Location getConfigLocation(String path) {
        try {
            return new Location(Bukkit.getWorld(plugin.getData().getString(path + ".world")),
                    plugin.getData().getDouble(path + ".x"), plugin.getData().getDouble(path + ".y"),
                    plugin.getData().getDouble(path + ".z"));
        } catch (Exception e) {
            return null;
        }
    }

    // Gets a list of locations from a configuration path
    public List<Location> getConfigLocationList(String path) {
        List<Location> locations = new ArrayList<>();
        for (int num = 0; num < 9; num++) {
            try {
                locations.add(new Location(
                        Bukkit.getWorld(plugin.getData().getString(path + "." + num + ".world")),
                        plugin.getData().getDouble(path + "." + num + ".x"),
                        plugin.getData().getDouble(path + "." + num + ".y"),
                        plugin.getData().getDouble(path + "." + num + ".z")));
            } catch (Exception ignored) {
                locations.add(null);
            }
        }
        return locations;
    }

    // Clears the arena
    public static void clear(Location location) {
        Collection<Entity> ents;
        try {
            // Get all entities near spawn
            ents = location.getWorld().getNearbyEntities(location, 200, 200, 100);
        } catch (Exception e) {
            return;
        }
        // Clear the arena for living entities
        ents.forEach(ent -> {
            if (ent instanceof LivingEntity && !(ent instanceof Player))
                if (ent.hasMetadata("VD")) ((LivingEntity) ent).setHealth(0);
        });

        // Clear the arena for items
        ents.forEach(ent -> {
            if (ent instanceof Item) ent.remove();
        });
    }

    // Converts integer seconds to ticks
    public static int secondsToTicks(int seconds) {
        return seconds * SECONDS_TO_TICKS;
    }

    // Converts double seconds to ticks
    public static int secondsToTicks(double seconds) {
        return (int) seconds * SECONDS_TO_TICKS;
    }

    // Returns a formatted health bar
    public static String healthBar(double max, double remaining, int size) {
        String toFormat;
        double healthLeft = remaining / max;
        int healthBars = (int) (healthLeft * size + .99);

        if (healthLeft > .5)
            toFormat = "&a";
        else if (healthLeft > .25)
            toFormat = "&e";
        else toFormat = "&c";

        return format(toFormat +
                new String(new char[healthBars]).replace("\0", "\u2592") +
                new String(new char[size - healthBars]).replace("\0", "  "));
    }
}
