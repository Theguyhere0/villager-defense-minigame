package me.theguyhere.villagerdefense.plugin.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataManager {
	
	private final Main plugin;
	private FileConfiguration dataConfig;
	private File configFile;
	private final String fileName;

	public DataManager(Main plugin, String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;

		// Saves/initializes the config
		saveDefaultConfig();
	}

	public void reloadConfig() {
		// Create config file object
		if (configFile == null)
			configFile = new File(plugin.getDataFolder().getPath(), fileName);

		// Refresh file configuration object
		dataConfig = YamlConfiguration.loadConfiguration(configFile);

		// Write data into default file
		InputStream defaultStream = plugin.getResource(fileName);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		// Get current config, otherwise set default and return that
		if (dataConfig == null)
			reloadConfig();
		return dataConfig;
	}
	
	public void saveConfig() {
		// Ignore null files
		if (dataConfig == null || configFile == null)
			return;

		// Try saving
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
	
	private void saveDefaultConfig() {
		// Create config file object
		if (configFile == null)
			configFile = new File(plugin.getDataFolder().getPath(), fileName);

		// Save default if file doesn't exist
		if (!configFile.exists())
			plugin.saveResource(fileName, false);
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
			CommunicationManager.debugError("Error getting location " + path + " from yaml", 2,
					!Main.releaseMode, e);
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
			CommunicationManager.debugError("Error getting location " + path + " from yaml", 2,
					!Main.releaseMode, e);
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
			CommunicationManager.debugError("Error getting location " + path + " from yaml", 2,
					!Main.releaseMode, e);
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
			CommunicationManager.debugError("Something went wrong centering!", 1);
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
							CommunicationManager.debugError("An error occurred retrieving a location from section "
									+ path, 1);
						}
					});
		} catch (Exception e) {
			CommunicationManager.debugError("Section " + path + " is invalid.", 1);
		}
		return locations;
	}
}
