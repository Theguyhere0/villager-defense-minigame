package me.theguyhere.villagerdefense.tools;

import me.theguyhere.villagerdefense.Main;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class Utils {
    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int SECONDS_TO_MILLIS = 1000;

    // Formats chat text
    public static String format(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    // Formats plugin notifications
    public static String notify(String msg) {
        return format("&2VD: &f" + msg);
    }

    // Creates an ItemStack using only material, name, and lore
    public static ItemStack createItem(Material matID, String dispName, String... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();

        // Check for null meta
        if (meta == null)
            return null;

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

    // Creates an ItemStack using only material, name, and lore list
    public static ItemStack createItem(Material matID, String dispName, List<String> lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();

        // Check for null meta
        if (meta == null)
            return null;

        // Set name
        if (!(dispName == null))
            meta.setDisplayName(dispName);

        // Set lore
        meta.setLore(lores);
        item.setItemMeta(meta);

        return item;
    }

    // Creates an ItemStack using material, name, enchants, flags, and lore
    public static ItemStack createItem(Material matID,
                                       String dispName,
                                       boolean[] flags,
                                       HashMap<Enchantment, Integer> enchants,
                                       String... lores) {
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores);
        ItemMeta meta = item.getItemMeta();

        // Check for null meta
        if (meta == null)
            return null;

        // Set enchants
        if (!(enchants == null))
            enchants.forEach((k, v) -> meta.addEnchant(k, v, true));
        if (flags != null && flags[0])
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set attribute flag
        if (flags != null && flags[1])
            meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    // Creates an ItemStack using material, name, enchants, flags, and lore list
    public static ItemStack createItem(Material matID,
                                       String dispName,
                                       boolean[] flags,
                                       HashMap<Enchantment, Integer> enchants,
                                       List<String> lores) {
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores);
        ItemMeta meta = item.getItemMeta();

        // Set enchants
        if (!(enchants == null))
            enchants.forEach((k, v) -> meta.addEnchant(k, v, true));
        if (flags != null && flags[0])
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Set attribute flag
        if (flags != null && flags[1])
            meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);

        return item;
    }

    // Makes an item unbreakable
    public static ItemStack makeUnbreakable(ItemStack item) {
        ItemStack newItem = item.clone();
        ItemMeta meta = newItem.getItemMeta();
        if (item.getType().getMaxDurability() == 0)
            return item;
        try {
            meta.setUnbreakable(true);
            newItem.setItemMeta(meta);
            return newItem;
        } catch (Exception e) {
            return item;
        }
    }

    // Make an item into a splash potion
    public static ItemStack makeSplash(ItemStack item) {
        ItemStack newItem = item.clone();
        if (newItem.getType() == Material.POTION)
            newItem.setType(Material.SPLASH_POTION);
        return newItem;
    }

    // Creates an ItemStack that has potion meta
    public static ItemStack createPotionItem(Material matID, PotionData potionData, String dispName, String... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID);
        ItemMeta meta = item.getItemMeta();
        PotionMeta pot = (PotionMeta) meta;

        // Check for null meta
        if (meta == null)
            return null;

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
    public static ItemStack createItems(Material matID, int amount, String dispName, String... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID, amount);
        ItemMeta meta = item.getItemMeta();

        // Check for null meta
        if (meta == null)
            return null;

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
                                              String... lores) {
        // Create ItemStack
        ItemStack item = new ItemStack(matID, amount);
        ItemMeta meta = item.getItemMeta();
        PotionMeta pot = (PotionMeta) meta;

        // Check for null meta
        if (meta == null)
            return null;

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
    public static ItemStack removeLastLore(ItemStack itemStack) {
        ItemStack item = itemStack.clone();

        // Check for lore
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore())
            return item;

        // Remove last lore and return
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    // Gives item to player if possible, otherwise drops at feet
    public static void giveItem(Player player, ItemStack item, String message) {
        if (player.getInventory().firstEmpty() == -1 && (player.getInventory().first(item.getType()) == -1 ||
                (player.getInventory().all(new ItemStack(item.getType(), item.getMaxStackSize())).size() ==
                        player.getInventory().all(item.getType()).size()) &&
                        player.getInventory().all(item.getType()).size() != 0)) {
            // Inventory is full
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(notify(message));
        } else player.getInventory().addItem(item);
    }

    // Prepares and teleports a player into adventure mode
    public static void teleAdventure(Player player, Location location) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        if (!player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().isEmpty())
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().forEach(attribute ->
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(attribute));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setInvulnerable(false);
        player.getInventory().clear();
        player.teleport(location);
        player.setGameMode(GameMode.ADVENTURE);
    }

    // Prepares and teleports a player into spectator mode
    public static void teleSpectator(Player player, Location location) {
        // Check for null attribute
        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
            return;

        if (!player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().isEmpty())
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().forEach(attribute ->
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(attribute));
//        setFalseSpectator(player);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SPECTATOR);
        player.closeInventory();
        player.teleport(location);
    }

    // Sets the player into false spectator mode for spectating or death
    public static void setFalseSpectator(Player player) {
        player.setInvulnerable(true);
        player.setInvisible(true);
        player.setAllowFlight(true);
        player.setCanPickupItems(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.closeInventory();
    }

    // Reverts false spectator mode
    public static void undoFalseSpectator(Player player) {
        player.setInvulnerable(false);
        player.setInvisible(false);
        player.setAllowFlight(false);
        player.setCanPickupItems(true);
    }

    // Sets the location data to a configuration path
    public static void setConfigurationLocation(Main plugin, String path, Location location) {
        if (location == null)
            plugin.getArenaData().set(path, location);
        else {
            plugin.getArenaData().set(path + ".world", location.getWorld().getName());
            plugin.getArenaData().set(path + ".x", location.getX());
            plugin.getArenaData().set(path + ".y", location.getY());
            plugin.getArenaData().set(path + ".z", location.getZ());
            plugin.getArenaData().set(path + ".pitch", location.getPitch());
            plugin.getArenaData().set(path + ".yaw", location.getYaw());
        }
        plugin.saveArenaData();
    }

    // Gets location data from a configuration path
    public static Location getConfigLocation(Main plugin, String path) {
        try {
            plugin.debugInfo(path + " : " + Bukkit.getWorld(plugin.getArenaData().getString(path + ".world")), 2);
            return new Location(
                Bukkit.getWorld(plugin.getArenaData().getString(path + ".world")),
                plugin.getArenaData().getDouble(path + ".x"),
                plugin.getArenaData().getDouble(path + ".y"),
                plugin.getArenaData().getDouble(path + ".z"),
                Float.parseFloat(plugin.getArenaData().get(path + ".yaw").toString()),
                Float.parseFloat(plugin.getArenaData().get(path + ".pitch").toString())
            );
        } catch (Exception e) {
            plugin.debugError("Error getting location from yaml", 2);
            return null;
        }
    }

    // Gets location data without pitch or yaw
    public static Location getConfigLocationNoRotation(Main plugin, String path) {
        try {
            Location location = getConfigLocation(plugin, path);
            location.setPitch(0);
            location.setYaw(0);
            return location;
        } catch (Exception e) {
            return null;
        }
    }

    // Gets location data without pitch
    public static Location getConfigLocationNoPitch(Main plugin, String path) {
        try {
            Location location = getConfigLocation(plugin, path);
            location.setPitch(0);
            return location;
        } catch (Exception e) {
            return null;
        }
    }

    // Centers location data
    public static void centerConfigLocation(Main plugin, String path) {
        try {
            Location location = getConfigLocation(plugin, path);
            if (location.getX() > 0)
                location.setX(((int) location.getX()) + .5);
            else location.setX(((int) location.getX()) - .5);
            if (location.getZ() > 0)
                location.setZ(((int) location.getZ()) + .5);
            else location.setZ(((int) location.getZ()) - .5);
            setConfigurationLocation(plugin, path, location);
            plugin.saveArenaData();
        } catch (Exception ignored) {
        }
    }

    // Gets a list of locations from a configuration path
    public static List<Location> getConfigLocationList(Main plugin, String path) {
        List<Location> locations = new ArrayList<>();
        for (int num = 0; num < 9; num++)
            locations.add(getConfigLocationNoRotation(plugin, path + "." + num));
        return locations;
    }

    // Clears the arena
    public static void clear(Location corner1, Location corner2) {
        Collection<Entity> ents;

        // Get all entities near spawn
        try {
            ents = corner1.getWorld().getNearbyEntities(BoundingBox.of(corner1, corner2));
        } catch (Exception e) {
            return;
        }

        // Clear the arena for living entities
        ents.forEach(ent -> {
            if (ent instanceof LivingEntity && !(ent instanceof Player) && ent.hasMetadata("VD"))
                ent.remove();
        });

        // Clear the arena for items and experience orbs
        ents.forEach(ent -> {
            if (ent instanceof Item || ent instanceof ExperienceOrb) ent.remove();
        });
    }

    // Converts seconds to ticks
    public static int secondsToTicks(double seconds) {
        return (int) (seconds * SECONDS_TO_TICKS);
    }

    // Converts minutes to seconds
    public static int minutesToSeconds(double minutes) {
        return (int) (minutes * MINUTES_TO_SECONDS);
    }

    // Converts seconds to milliseconds
    public static int secondsToMillis(double seconds) {
        return (int) (seconds * SECONDS_TO_MILLIS);
    }

    // Converts milliseconds to seconds
    public static double millisToSeconds(double millis) {
        return millis / SECONDS_TO_MILLIS;
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

    // Get nearby players
    public static List<Player> getNearbyPlayers(Player player, double range) {
        return player.getNearbyEntities(range, range, range).stream().filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent).collect(Collectors.toList());
    }

    // Get nearby allies
    public static List<LivingEntity> getNearbyAllies(Player player, double range) {
        return player.getNearbyEntities(range, range, range).stream().filter(ent -> ent instanceof Villager ||
                ent instanceof Wolf || ent instanceof IronGolem).map(ent -> (LivingEntity) ent)
                .collect(Collectors.toList());
    }

    // Get wolves
    public static List<Wolf> getPets(Player player) {
        return player.getNearbyEntities(150, 50, 150).stream().filter(ent -> ent instanceof Wolf)
                .map(ent -> (Wolf) ent).filter(wolf -> Objects.equals(wolf.getOwner(), player))
                .collect(Collectors.toList());
    }

    // Get nearby monsters
    public static List<LivingEntity> getNearbyMonsters(Player player, double range) {
        return player.getNearbyEntities(range, range, range).stream().filter(ent -> ent instanceof Monster ||
                ent instanceof Slime || ent instanceof Hoglin || ent instanceof Phantom).map(ent -> (LivingEntity) ent)
                .collect(Collectors.toList());
    }
}
