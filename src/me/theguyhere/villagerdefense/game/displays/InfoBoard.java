package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InfoBoard {
	private final Main plugin;
	private final Utils utils;
	private final String[] HOLO_TEXT = {Utils.format("&bLeft click &eportal for arena info"),
			Utils.format("&bRight click &aportal to enter arena"),
			Utils.format("&6Use &b/vd stats &6to check personal stats"),
			Utils.format("&6Use &b/vd kits &6to buy kits using crystals"),
			Utils.format("&6Use &b/vd select &6before a game to select a kit"),
			Utils.format("&6Use &b/vd help &6to go to plugin wiki")};
	private final Hologram[] boards = new Hologram[8];
	
	public InfoBoard(Main plugin) {
		this.plugin = plugin;
		utils = new Utils(plugin);
	}

	public void createInfoBoard(Player player, int slot) {
		// Create hologram
		addHolo(player.getLocation(), slot);

		// Save location data
		utils.setConfigurationLocation("infoBoard." + slot, player.getLocation());
		plugin.saveArenaData();
	}

	public void refreshInfoBoard(int slot) {
		if (boards[slot] != null) {
			boards[slot].delete();
			boards[slot] = null;
			Location location = utils.getConfigLocationNoPitch("infoBoard." + slot);
			addHolo(location, slot);
		}
	}

	public void removeInfoBoard(int slot) {
		boards[slot].delete();
		boards[slot] = null;
	}

	public void addHolo(Location location, int slot) {
		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 2);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		holo.insertTextLine(0, HOLO_TEXT[0]);
		for (int i = 1; i < HOLO_TEXT.length; i++)
			holo.appendTextLine(HOLO_TEXT[i]);

		// Save hologram in array
		boards[slot] = holo;
	}

	public void loadInfoBoards() {
		if (plugin.getArenaData().contains("infoBoard"))
			plugin.getArenaData().getConfigurationSection("infoBoard").getKeys(false).forEach(board -> {
				Location location = utils.getConfigLocationNoPitch("infoBoard." + board);
				if (location != null)
					addHolo(location, Integer.parseInt(board));
			});
	}
}
