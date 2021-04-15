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
		if (this.configFile == null)
			this.configFile = new File(this.plugin.getDataFolder(), fileName);
		
		this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
		
		InputStream defaultStream = this.plugin.getResource(fileName);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		if (this.dataConfig == null)
			reloadConfig();
		return this.dataConfig;
	}
	
	public void saveConfig() {
		if (this.dataConfig == null || this.configFile == null)
			return;
		
		try {
			this.getConfig().save(this.configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
		}
	}
	
	public void saveDefaultConfig() {
		if (this.configFile == null)
			this.configFile = new File(this.plugin.getDataFolder(), fileName);
		
		if (!this.configFile.exists()) {
			this.plugin.saveResource(fileName, false);
		}
	}
}
