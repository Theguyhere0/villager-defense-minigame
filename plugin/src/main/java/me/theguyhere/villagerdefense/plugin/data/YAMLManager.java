package me.theguyhere.villagerdefense.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class YAMLManager {
	private FileConfiguration dataConfig;
	private File configFile;
	private final String fileName;

	YAMLManager(String fileName) {
		this.fileName = fileName;

		// Saves/initializes the config
		saveDefaultConfig();
	}

	private void reloadConfig() {
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

	private @NotNull FileConfiguration getConfig() {
		// Get current config, otherwise set default and return that
		if (dataConfig == null) {
			reloadConfig();
		}
		return dataConfig;
	}

	private void saveConfig() {
		// Ignore null files
		if (dataConfig == null || configFile == null)
			return;

		// Try saving
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			CommunicationManager.debugError(
				CommunicationManager.DebugLevel.QUIET, "Could not save config to " + configFile,
				false,
				e
			);
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

	boolean hasPath(String path) {
		return getConfig().contains(path);
	}

	/**
	 * Reads boolean data from a configuration path.
	 * @param path Data path
	 * @return Boolean data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	boolean getBoolean(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getBoolean(path);
	}

	/**
	 * Reads boolean data from a configuration path. Converts integer data to true for values greater than 0,
	 * otherwise false.
	 * @param path Data path
	 * @return Boolean data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	boolean getSoftBoolean(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		if (getConfig().isInt(path)) {
			return getConfig().getInt(path) > 0;
		}
		else {
			return getConfig().getBoolean(path);
		}
	}

	void setBoolean(String path, boolean value) {
		getConfig().set(path, value);
		saveConfig();
	}

	/**
	 * Reads integer data from a configuration path.
	 * @param path Data path
	 * @return Integer data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	int getInteger(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getInt(path);
	}

	/**
	 * Reads integer data from a configuration path. Converts boolean data to 1 for true and 0 for false.
	 * @param path Data path
	 * @return Integer data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	int getSoftInteger(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		if (getConfig().isBoolean(path)) {
			return getConfig().getBoolean(path) ? 1 : 0;
		}
		return getConfig().getInt(path);
	}

	void setInteger(String path, int value) {
		getConfig().set(path, value);
		saveConfig();
	}

	/**
	 * Reads double data from a configuration path.
	 * @param path Data path
	 * @return Double data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	double getDouble(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getDouble(path);
	}

	void setDouble(String path, double value) {
		getConfig().set(path, value);
		saveConfig();
	}

	/**
	 * Reads string data from a configuration path.
	 * @param path Data path
	 * @return String data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	String getString(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getString(path);
	}

	void setString(String path, String value) {
		getConfig().set(path, value);
		saveConfig();
	}

	/**
	 * Reads item data from a configuration path.
	 * @param path Data path
	 * @return Item data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	ItemStack getItemStack(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getItemStack(path);
	}

	void setItemStack(String path, ItemStack itemStack) {
		getConfig().set(path, itemStack);
		saveConfig();
	}

	/**
	 * Reads location data from a configuration path.
	 * @param path Location path
	 * @return Location
	 * @throws BadDataException Thrown if the data could not be retrieved but the path exists
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull Location getConfigLocation(String path) throws BadDataException, NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		try {
			return new Location(
				Bukkit.getWorld(Objects.requireNonNull(getConfig().getString(path + ".world"))),
				getConfig().getDouble(path + ".x"),
				getConfig().getDouble(path + ".y"),
				getConfig().getDouble(path + ".z"),
				Float.parseFloat(Objects.requireNonNull(getConfig().get(path + ".yaw")).toString()),
				Float.parseFloat(Objects.requireNonNull(getConfig().get(path + ".pitch")).toString())
			);
		} catch (NullPointerException e) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, "Error getting location " + path + " from yaml",
				!Main.releaseMode, e);
			throw new BadDataException();
		}
	}

	/**
	 * Reads location data without pitch.
	 * @param path Location path
	 * @return Location
	 * @throws BadDataException Thrown if the data could not be retrieved but the path exists
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull Location getConfigLocationNoPitch(String path) throws BadDataException, NoSuchPathException {
		Location location = getConfigLocation(path);
		location.setPitch(0);
		return location;
	}

	/**
	 * Reads location data without pitch or yaw.
	 * @param path Location path
	 * @return Location
	 * @throws BadDataException Thrown if the data could not be retrieved but the path exists
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull Location getConfigLocationNoRotation(String path) throws BadDataException, NoSuchPathException {
		Location location = getConfigLocation(path);
		location.setPitch(0);
		location.setYaw(0);
		return location;
	}

	/**
	 * Reads a map of locations from a configuration path.
	 * @param path Location map path
	 * @return Map of locations
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull Map<Integer, Location> getConfigLocationMap(String path) throws NoSuchPathException {
		Map<Integer, Location> locations = new HashMap<>();
		try {
			Objects.requireNonNull(getConfig().getConfigurationSection(path)).getKeys(false)
				.forEach(num -> {
					try {
						locations.put(Integer.parseInt(num),
							getConfigLocationNoRotation(path + "." + num));
					} catch (Exception e) {
						CommunicationManager.debugError(
							CommunicationManager.DebugLevel.NORMAL, "An error occurred retrieving a location from section %s",
							path);
					}
				});
		} catch (NullPointerException e) {
			CommunicationManager.debugError(CommunicationManager.DebugLevel.NORMAL, "Section %s is invalid.",
				path);
			throw new NoSuchPathException();
		}
		return locations;
	}

	/**
	 * Writes the location data to a configuration path.
	 * @param path Location path
	 * @param location Location data
	 */
	void setConfigLocation(String path, Location location) {
		// Erase path if null...
		if (location == null) {
			getConfig().set(path, null);
		}

		// ...otherwise write in new location data
		else {
			getConfig().set(path + ".world", Objects.requireNonNull(location.getWorld()).getName());
			getConfig().set(path + ".x", location.getX());
			getConfig().set(path + ".y", location.getY());
			getConfig().set(path + ".z", location.getZ());
			getConfig().set(path + ".pitch", location.getPitch());
			getConfig().set(path + ".yaw", location.getYaw());
		}

		// Save write
		saveConfig();
	}

	/**
	 * Centers the stored location in the file to the center of the block.
	 * @param path The path
	 * @throws BadDataException Thrown if the data could not be retrieved but the path exists
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	void centerConfigLocation(String path) throws BadDataException, NoSuchPathException {
		Location location = getConfigLocation(path);

		// Center x
		if (location.getX() > 0) {
			location.setX(((int) location.getX()) + .5);
		}
		else {
			location.setX(((int) location.getX()) - .5);
		}

		// Center z
		if (location.getZ() > 0) {
			location.setZ(((int) location.getZ()) + .5);
		}
		else {
			location.setZ(((int) location.getZ()) - .5);
		}

		// Write and save
		setConfigLocation(path, location);
		saveConfig();
	}

	void delete(String path) {
		getConfig().set(path, null);
		saveConfig();
	}

	/**
	 * Reads a list of strings from a configuration path.
	 * @param path Data path
	 * @return String list data
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull List<String> getStringList(String path) throws NoSuchPathException {
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		return getConfig().getStringList(path);
	}

	void setStringList(String path, List<String> value) {
		getConfig().set(path, value);
		saveConfig();
	}

	/**
	 * Reads the keys from a configuration section.
	 * @param path Section path
	 * @return Configuration section keys
	 * @throws BadDataException Thrown if path doesn't lead to a configuration section
	 * @throws NoSuchPathException Thrown if the path doesn't exist
	 */
	@NotNull Set<String> getKeys(String path) throws BadDataException, NoSuchPathException{
		if (!hasPath(path)) {
			throw new NoSuchPathException();
		}
		try {
			return Objects.requireNonNull(getConfig().getConfigurationSection(path)).getKeys(false);
		} catch (NullPointerException e) {
			throw new BadDataException();
		}
	}

	/**
	 * Copies all the data from one path to another path.
	 * @param source Path to original data
	 * @param target Target path to copy into
	 */
	void copyPath(@NotNull String source, @NotNull String target) throws NoSuchPathException {
		if (!hasPath(source)) {
			throw new NoSuchPathException();
		}
		getConfig().set(target, getConfig().get(source));
		saveConfig();
	}

	/**
	 * Swaps all the data from one path to another path.
	 * @param oldPath Path to original data
	 * @param newPath New path for data
	 */
	void swapPath(@NotNull String oldPath, @NotNull String newPath) throws NoSuchPathException {
		if (!hasPath(oldPath)) {
			throw new NoSuchPathException();
		}
		getConfig().set(newPath, getConfig().get(oldPath));
		getConfig().set(oldPath, null);
		saveConfig();
	}
}
