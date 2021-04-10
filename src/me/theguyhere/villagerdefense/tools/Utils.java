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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

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
        if (flags != null && flags[0])
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set attribute flag
        if (flags != null && flags[1])
            meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    // Creates an ItemStack that has potion meta
    public static ItemStack createPotionItem(Material matID, PotionData potionData, String dispName, String ... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();
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

    // Creates an ItemStack using material, amount, name, and lore
    public static ItemStack createItems(Material matID, int amount, String dispName, String ... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID, amount);
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

    // Creates an ItemStack of multiple items that has potion meta
    public static ItemStack createPotionItems(Material matID,
                                              PotionData potionData,
                                              int amount,
                                              String dispName,
                                              String ... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID, amount);
        ItemMeta meta = item.getItemMeta();
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

    // Gives item to player if possible, otherwise drops at feet
    public static void giveItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1 && (player.getInventory().first(item.getType()) == -1 ||
                (player.getInventory().all(new ItemStack(item.getType(), item.getMaxStackSize())).size() ==
                        player.getInventory().all(item.getType()).size()) &&
                        player.getInventory().all(item.getType()).size() != 0)) {

            // Inventory is full
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(notify("&cYour inventory is full!"));
        } else {
            player.getInventory().addItem(item);
        }
    }

    // Prepares a player to teleport into adventure mode
    public static void prepTeleAdventure(Player player) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setLevel(0);
        player.setFallDistance(0);
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

    // Sets the location data to a configuration path
    public void setConfigurationLocation(String path, Location location) {
        plugin.getData().set(path + ".world", location.getWorld().getName());
        plugin.getData().set(path + ".x", location.getX());
        plugin.getData().set(path + ".y", location.getY());
        plugin.getData().set(path + ".z", location.getZ());
        plugin.getData().set(path + ".pitch", location.getPitch());
        plugin.getData().set(path + ".yaw", location.getYaw());
        plugin.saveData();
    }

    // Gets location data from a configuration path
    public Location getConfigLocation(String path) {
        try {
            return new Location(
                Bukkit.getWorld(plugin.getData().getString(path + ".world")),
                plugin.getData().getDouble(path + ".x"),
                plugin.getData().getDouble(path + ".y"),
                plugin.getData().getDouble(path + ".z"),
                Float.parseFloat(plugin.getData().get(path + ".yaw").toString()),
                Float.parseFloat(plugin.getData().get(path + ".pitch").toString())
            );
        } catch (Exception e) {
            return null;
        }
    }

    // Gets location data without pitch or yaw
    public Location getConfigLocationNoRotation(String path) {
        try {
            Location location = getConfigLocation(path);
            location.setPitch(0);
            location.setYaw(0);
            return location;
        } catch (Exception e) {
            return null;
        }
    }

    // Gets location data without pitch
    public Location getConfigLocationNoPitch(String path) {
        try {
            Location location = getConfigLocation(path);
            location.setPitch(0);
            return location;
        } catch (Exception e) {
            return null;
        }
    }

    // Centers location data
    public void centerConfigLocation(String path) {
        try {
            Location location = getConfigLocation(path);
            if (location.getX() > 0)
                location.setX(((int) location.getX()) + .5);
            else location.setX(((int) location.getX()) - .5);
            if (location.getZ() > 0)
                location.setZ(((int) location.getZ()) + .5);
            else location.setZ(((int) location.getZ()) - .5);
            setConfigurationLocation(path, location);
            plugin.saveData();
        } catch (Exception ignored) {
        }
    }

    // Gets a list of locations from a configuration path
    public List<Location> getConfigLocationList(String path) {
        List<Location> locations = new ArrayList<>();
        for (int num = 0; num < 9; num++)
            locations.add(getConfigLocationNoRotation(path + "." + num));
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
        if (healthBars < 0) healthBars = 0;

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
