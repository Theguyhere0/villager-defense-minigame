package me.theguyhere.villagerdefense.plugin.tools;

import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int SECONDS_TO_MILLIS = 1000;

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
            CommunicationManager.debugError("Error getting location " + path + " from yaml", 2);
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
                            CommunicationManager.debugError("An error occurred retrieving a location from section " + path, 1);
                        }
                    });
        } catch (Exception e) {
            CommunicationManager.debugError("Section " + path + " is invalid.", 1);
        }
        return locations;
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

        return CommunicationManager.format(toFormat +
                new String(new char[healthBars]).replace("\0", "\u2592") +
                new String(new char[size - healthBars]).replace("\0", "  "));
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
