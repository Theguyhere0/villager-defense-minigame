package me.theguyhere.villagerdefense.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import me.theguyhere.villagerdefense.Main;
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
}
