package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class InfoBoard {
	private final Main plugin;
	private final Hologram[] boards = new Hologram[8];
	
	public InfoBoard(Main plugin) {
		this.plugin = plugin;
	}

	public void createInfoBoard(Player player, int slot) {
		// Create hologram
		try {
			addHolo(player.getLocation(), slot);
		} catch (Exception e) {
			Utils.debugError("Invalid location for info board " + slot, 1);
			Utils.debugInfo("Info board location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the location data for info board " + slot + ".", 1);
		}

		// Save location data
		Utils.setConfigurationLocation(plugin, "infoBoard." + slot, player.getLocation());
		plugin.saveArenaData();
	}

	public void refreshInfoBoard(int slot) {
		if (boards[slot] != null) {
			boards[slot].delete();
			boards[slot] = null;
			Location location = Utils.getConfigLocationNoPitch(plugin, "infoBoard." + slot);
			try {
				addHolo(location, slot);
			} catch (Exception e) {
				Utils.debugError("Invalid location for info board " + slot, 1);
				Utils.debugInfo("Info board location data may be corrupt. If data cannot be manually corrected in " +
						"arenaData.yml, please delete the location data for info board " + slot + ".", 1);
			}
		}
	}

	public void removeInfoBoard(int slot) {
		boards[slot].delete();
		boards[slot] = null;
	}

	public void addHolo(Location location, int slot) {
		FileConfiguration language = plugin.getLanguageData();
		String[] text = {Utils.format(language.getString("info1")),
				Utils.format(language.getString("info2")),
				Utils.format(language.getString("info3")),
				Utils.format(language.getString("info4")),
				Utils.format(language.getString("info5")),
				Utils.format(language.getString("info6"))};

		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 2);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		holo.insertTextLine(0, text[0]);
		for (int i = 1; i < text.length; i++)
			holo.appendTextLine(text[i]);

		// Save hologram in array
		boards[slot] = holo;
	}

	public void loadInfoBoards() {
		if (plugin.getArenaData().contains("infoBoard"))
			plugin.getArenaData().getConfigurationSection("infoBoard").getKeys(false).forEach(board -> {
				try {
					addHolo(Utils.getConfigLocationNoPitch(plugin, "infoBoard." + board), Integer.parseInt(board));
				} catch (Exception e) {
					Utils.debugError("Invalid location for info board " + board, 1);
					Utils.debugInfo("Info board location data may be corrupt. If data cannot be manually " +
							"corrected in " +
							"arenaData.yml, please delete the location data for info board " + board + ".", 1);
				}
			});
	}
}
