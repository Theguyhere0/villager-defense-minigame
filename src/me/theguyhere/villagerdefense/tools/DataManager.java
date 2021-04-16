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
	private FileConfiguration dataConfig = null;
	private File configFile = null;
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

		InputStream defaultStream = plugin.getResource(fileName);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		if (dataConfig == null)
			reloadConfig();
		return dataConfig;
	}
	
	public void saveConfig() {
		if (dataConfig == null || configFile == null)
			return;
		
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
