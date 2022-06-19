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
	private FileConfiguration dataConfig;
	private File configFile;
	private final String fileName;

	public DataManager(String fileName) {
		this.fileName = fileName;

		// Saves/initializes the config
		saveDefaultConfig();
	}

	public void reloadConfig() {
		// Create config file object
		if (configFile == null)
			configFile = new File(Main.plugin.getDataFolder().getPath(), fileName);

		// Refresh file configuration object
		dataConfig = YamlConfiguration.loadConfiguration(configFile);

		// Write data into default file
		InputStream defaultStream = Main.plugin.getResource(fileName);
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
			Main.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
		}
	}
	
	private void saveDefaultConfig() {
		// Create config file object
		if (configFile == null)
			configFile = new File(Main.plugin.getDataFolder().getPath(), fileName);

		// Save default if file doesn't exist
		if (!configFile.exists())
			Main.plugin.saveResource(fileName, false);
	}

	// Sets the location data to a configuration path
	public static void setConfigurationLocation(String path, Location location) {
		if (location == null)
			Main.getArenaData().set(path, null);
		else {
			Main.getArenaData().set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
			Main.getArenaData().set(path + ".x", location.getX());
			Main.getArenaData().set(path + ".y", location.getY());
			Main.getArenaData().set(path + ".z", location.getZ());
			Main.getArenaData().set(path + ".pitch", location.getPitch());
			Main.getArenaData().set(path + ".yaw", location.getYaw());
		}
		Main.saveArenaData();
	}

	// Gets location data from a configuration path
	public static Location getConfigLocation(String path) {
		try {
			return new Location(
					Bukkit.getWorld(Objects.requireNonNull(Main.getArenaData().getString(path + ".world"))),
					Main.getArenaData().getDouble(path + ".x"),
					Main.getArenaData().getDouble(path + ".y"),
					Main.getArenaData().getDouble(path + ".z"),
					Float.parseFloat(Objects.requireNonNull(Main.getArenaData().get(path + ".yaw")).toString()),
					Float.parseFloat(Objects.requireNonNull(Main.getArenaData().get(path + ".pitch")).toString())
			);
		} catch (Exception e) {
			CommunicationManager.debugError("Error getting location " + path + " from yaml", 2,
					!Main.releaseMode, e);
			return null;
		}
	}

	// Gets location data without pitch or yaw
	public static Location getConfigLocationNoRotation(String path) {
		try {
			Location location = getConfigLocation(path);
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
	public static Location getConfigLocationNoPitch(String path) {
		try {
			Location location = getConfigLocation(path);
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
	public static void centerConfigLocation(String path) {
		try {
			Location location = getConfigLocation(path);
			assert location != null;
			if (location.getX() > 0)
				location.setX(((int) location.getX()) + .5);
			else location.setX(((int) location.getX()) - .5);
			if (location.getZ() > 0)
				location.setZ(((int) location.getZ()) + .5);
			else location.setZ(((int) location.getZ()) - .5);
			setConfigurationLocation(path, location);
			Main.saveArenaData();
		} catch (Exception ignored) {
			CommunicationManager.debugError("Something went wrong centering!", 1);
		}
	}

	// Gets a map of locations from a configuration path
	public static Map<Integer, Location> getConfigLocationMap(String path) {
		Map<Integer, Location> locations = new HashMap<>();
		try {
			Objects.requireNonNull(Main.getArenaData().getConfigurationSection(path)).getKeys(false)
					.forEach(num -> {
						try {
							locations.put(Integer.parseInt(num),
									getConfigLocationNoRotation(path + "." + num));
						} catch (Exception e) {
							CommunicationManager.debugError(
									"An error occurred retrieving a location from section %s", 1, path);
						}
					});
		} catch (Exception e) {
			CommunicationManager.debugError("Section %s is invalid.", 1, path);
		}
		return locations;
	}
}
