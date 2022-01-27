package me.theguyhere.villagerdefense.tools;

import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.game.models.players.PlayerStatus;
import me.theguyhere.villagerdefense.game.models.players.VDPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("SpellCheckingInspection")
public class Utils {
    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int SECONDS_TO_MILLIS = 1000;

    private static final Logger log = Logger.getLogger("Minecraft");

    /** Flags for creating normal items with enchants and/or lore.*/
    public static final boolean[] NORMAL_FLAGS = {false, false};
    /** Flags for creating items with hidden enchants.*/
    public static final boolean[] HIDE_ENCHANT_FLAGS = {true, false};
    /** Flags for creating items with hidden enchants and attributes, mostly for buttons.*/
    public static final boolean[] BUTTON_FLAGS = {true, true};

    // Dummy enchant for glowing buttons
    public static HashMap<Enchantment, Integer> glow() {
        HashMap<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DURABILITY, 1);
        return enchants;
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
    public static ItemStack createItem(Material matID, String dispName, List<String> lores, String... moreLores) {
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
        lores.addAll(Arrays.asList(moreLores));
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
        assert item != null;
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
                                       List<String> lores,
                                       String... moreLores) {
        // Create ItemStack
        ItemStack item = createItem(matID, dispName, lores, moreLores);
        assert item != null;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

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
        assert meta != null;

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
        if (!item.hasItemMeta() || !Objects.requireNonNull(item.getItemMeta()).hasLore())
            return item;

        // Remove last lore and return
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        assert lore != null;
        lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    // Gives item to player if possible, otherwise drops at feet
    public static void giveItem(Player player, ItemStack item, String message) {
        // Inventory is full
        if (player.getInventory().firstEmpty() == -1 && (player.getInventory().first(item.getType()) == -1 ||
                (player.getInventory().all(new ItemStack(item.getType(), item.getMaxStackSize())).size() ==
                        player.getInventory().all(item.getType()).size()) &&
                        player.getInventory().all(item.getType()).size() != 0)) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(notify(message));
        }

        // Add item to inventory
        else player.getInventory().addItem(item);
    }

    // Prepares and teleports a player into adventure mode
    public static void teleAdventure(Player player, @NotNull Location location) {
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.setFireTicks(0);
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;

        if (!maxHealth.getModifiers().isEmpty())
            maxHealth.getModifiers().forEach(maxHealth::removeModifier);
        player.setHealth(maxHealth.getValue());
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
        player.setGlowing(false);
    }

    // Prepares and teleports a player into spectator mode
    public static void teleSpectator(Player player, @NotNull Location location) {
        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth == null)
            return;

        if (!maxHealth.getModifiers().isEmpty())
            maxHealth.getModifiers().forEach(maxHealth::removeModifier);
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
        player.setGlowing(false);
    }

    // Sets the location data to a configuration path
    public static void setConfigurationLocation(Main plugin, String path, Location location) {
        if (location == null)
            plugin.getArenaData().set(path, null);
        else {
            plugin.getArenaData().set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
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
            return new Location(
                Bukkit.getWorld(Objects.requireNonNull(plugin.getArenaData().getString(path + ".world"))),
                plugin.getArenaData().getDouble(path + ".x"),
                plugin.getArenaData().getDouble(path + ".y"),
                plugin.getArenaData().getDouble(path + ".z"),
                Float.parseFloat(Objects.requireNonNull(plugin.getArenaData().get(path + ".yaw")).toString()),
                Float.parseFloat(Objects.requireNonNull(plugin.getArenaData().get(path + ".pitch")).toString())
            );
        } catch (Exception e) {
            debugError("Error getting location " + path + " from yaml", 2);
            return null;
        }
    }

    // Gets location data without pitch or yaw
    public static Location getConfigLocationNoRotation(Main plugin, String path) {
        try {
            Location location = getConfigLocation(plugin, path);
            assert location != null;
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
            assert location != null;
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
            assert location != null;
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

    // Gets a map of locations from a configuration path
    public static Map<Integer, Location> getConfigLocationMap(Main plugin, String path) {
        Map<Integer, Location> locations = new HashMap<>();
        try {
            Objects.requireNonNull(plugin.getArenaData().getConfigurationSection(path)).getKeys(false)
                    .forEach(num -> {
                        try {
                            locations.put(Integer.parseInt(num),
                                    getConfigLocationNoRotation(plugin, path + "." + num));
                        } catch (Exception e) {
                            debugError("An error occurred retrieving a location from section " + path, 1);
                        }
                    });
        } catch (Exception e) {
            debugError("Section " + path + " is invalid.", 1);
        }
        return locations;
    }

    // Clears the arena
    public static void clear(Location corner1, Location corner2) {
        Collection<Entity> ents;

        // Get all entities near spawn
        try {
            ents = Objects.requireNonNull(corner1.getWorld()).getNearbyEntities(BoundingBox.of(corner1, corner2));
        } catch (Exception e) {
            return;
        }

        // Clear the arena for living entities
        ents.forEach(ent -> {
            if (ent instanceof LivingEntity && !(ent instanceof Player))
                ent.remove();
        });

        // Clear the arena for items and experience orbs
        ents.forEach(ent -> {
            if (ent instanceof Item || ent instanceof ExperienceOrb) ent.remove();
        });
    }

    // Convert seconds to ticks
    public static int secondsToTicks(double seconds) {
        return (int) (seconds * SECONDS_TO_TICKS);
    }

    // Convert minutes to seconds
    public static int minutesToSeconds(double minutes) {
        return (int) (minutes * MINUTES_TO_SECONDS);
    }

    // Convert seconds to milliseconds
    public static int secondsToMillis(double seconds) {
        return (int) (seconds * SECONDS_TO_MILLIS);
    }

    // Convert milliseconds to seconds
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
        return player.getNearbyEntities(range, range, range).stream().filter(Objects::nonNull)
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent).collect(Collectors.toList());
    }

    // Get nearby allies
    public static List<LivingEntity> getNearbyAllies(Player player, double range) {
        return player.getNearbyEntities(range, range, range).stream().filter(Objects::nonNull)
                .filter(ent -> ent instanceof Villager ||
                ent instanceof Wolf || ent instanceof IronGolem).map(ent -> (LivingEntity) ent)
                .collect(Collectors.toList());
    }

    // Get wolves
    public static List<Wolf> getPets(Player player) {
        return player.getNearbyEntities(150, 50, 150).stream().filter(Objects::nonNull)
                .filter(ent -> ent instanceof Wolf)
                .map(ent -> (Wolf) ent).filter(wolf -> Objects.equals(wolf.getOwner(), player))
                .collect(Collectors.toList());
    }

    // Get nearby monsters
    public static List<LivingEntity> getNearbyMonsters(Player player, double range) {
        return player.getNearbyEntities(range, range, range).stream().filter(Objects::nonNull)
                .filter(ent -> ent instanceof Monster ||
                ent instanceof Slime || ent instanceof Hoglin || ent instanceof Phantom).map(ent -> (LivingEntity) ent)
                .collect(Collectors.toList());
    }

    public static void debugError(String msg, int debugLevel) {
        if (Main.getDebugLevel() >= debugLevel)
            log.log(Level.WARNING,"[VillagerDefense] " + msg);
    }

    public static void debugInfo(String msg, int debugLevel) {
        if (Main.getDebugLevel() >= debugLevel)
            log.info("[VillagerDefense] " + msg);
    }

    public static void fakeDeath(VDPlayer vdPlayer) {
        Player player = vdPlayer.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        player.closeInventory();
        vdPlayer.setStatus(PlayerStatus.GHOST);
        player.setFallDistance(0);
        player.setGlowing(false);
        player.setVelocity(new Vector());
    }

    /**
     * This method uses a regex to get the NMS package part that changes with every update.
     * Example: v1_13_R2
     * @return the NMS package part or null if not found.
     */
    public static String extractNMSVersion() {
        Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}
