package me.theguyhere.villagerdefense.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Logging class to standardize console messages from the plugin.
 *
 * NOT MEANT TO BE USED DIRECTLY BY OTHER CLASSES.
 */
class Log {
    static void warning(String msg) {
        Bukkit.getConsoleSender().sendMessage("[VillagerDefense] " + ChatColor.RED + msg);
    }

    static void info(String msg) {
        Bukkit.getConsoleSender().sendMessage("[VillagerDefense] " + msg);
    }

    static void confirm(String msg) {
        Bukkit.getConsoleSender().sendMessage("[VillagerDefense] " + ChatColor.GREEN + msg);
    }
}
