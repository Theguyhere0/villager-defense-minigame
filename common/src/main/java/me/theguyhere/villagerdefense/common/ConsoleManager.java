package me.theguyhere.villagerdefense.common;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * A class to standardize console messages from the plugin.
 * <p>
 * NOT MEANT TO BE USED DIRECTLY BY OTHER CLASSES.
 */
class ConsoleManager {
	static void warning(String msg) {
		Bukkit
			.getConsoleSender()
			.sendMessage("[VillagerDefense] " + ChatColor.RED + msg);
	}

	static void info(String msg) {
		Bukkit
			.getConsoleSender()
			.sendMessage("[VillagerDefense] " + msg);
	}

	static void confirm(String msg) {
		Bukkit
			.getConsoleSender()
			.sendMessage("[VillagerDefense] " + ChatColor.GREEN + msg);
	}
}
