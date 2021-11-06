package me.theguyhere.villagerdefense.game.displays;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.theguyhere.villagerdefense.Main;
import me.theguyhere.villagerdefense.tools.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Leaderboard {
	private final Main plugin;
	private final Map<String, Hologram> leaderboards = new HashMap<>();

	public Leaderboard(Main plugin) {
		this.plugin = plugin;
	}

	public void createLeaderboard(Player player, String type) {
		// Create hologram
		try {
			addHolo(player.getLocation(), type);
		} catch (Exception e) {
			Utils.debugError("Invalid location for leaderboard " + type, 1);
			Utils.debugInfo("Leaderboard location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the location data for leaderboard " + type + ".", 1);
			return;
		}

		// Save location data
		Utils.setConfigurationLocation(plugin, "leaderboard." + type, player.getLocation());
		plugin.saveArenaData();
	}

	public void refreshLeaderboards() {
		leaderboards.keySet().forEach(this::refreshLeaderboard);
	}

	public void refreshLeaderboard(String type) {
		leaderboards.get(type).delete();
		try {
			addHolo(Objects.requireNonNull(Utils.getConfigLocationNoPitch(plugin, "leaderboard." + type)), type);
		} catch (Exception e) {
			Utils.debugError("Invalid location for leaderboard " + type, 1);
			Utils.debugInfo("Leaderboard location data may be corrupt. If data cannot be manually corrected in " +
					"arenaData.yml, please delete the location data for leaderboard " + type + ".", 1);
		}
	}

	public void removeLeaderboard(String type) {
		leaderboards.get(type).delete();
		leaderboards.remove(type);
	}

	public void addHolo(Location location, String type) {
		// Create hologram
		Location newLocation = location.clone();
		newLocation.setY(newLocation.getY() + 3);
		Hologram holo = HologramsAPI.createHologram(plugin, newLocation);
		if (getHoloText(type) == null)
			return;
		holo.insertTextLine(0, Objects.requireNonNull(getHoloText(type))[0]);
		for (int i = 1; i < Objects.requireNonNull(getHoloText(type)).length; i++)
			holo.appendTextLine(Objects.requireNonNull(getHoloText(type))[i]);

		// Save hologram in map
		leaderboards.put(type, holo);
	}

	public void loadLeaderboards() {
		if (plugin.getArenaData().contains("leaderboard"))
			Objects.requireNonNull(plugin.getArenaData().getConfigurationSection("leaderboard"))
					.getKeys(false).forEach(board -> {
					try {
						addHolo(Objects.requireNonNull(Utils.getConfigLocationNoPitch(plugin, "leaderboard." + board)),
								board);
					} catch (Exception e) {
						Utils.debugError("Invalid location for leaderboard " + board, 1);
						Utils.debugInfo("Leaderboard location data may be corrupt. If data cannot be manually " +
								"corrected in arenaData.yml, please delete the location data for leaderboard " + board +
								".", 1);
					}
			});
	}

	private String[] getHoloText(String type) {
		List<String> info = new ArrayList<>();
		Map<String, Integer> mapping = new HashMap<>();

		// Determine leaderboard title
		switch (type) {
			case "totalKills":
				info.add(Utils.format("&d&lTotal Kills Leaderboard"));
				break;
			case "topKills":
				info.add(Utils.format("&c&lTop Kills Leaderboard"));
				break;
			case "totalGems":
				info.add(Utils.format("&e&lTotal Gems Leaderboard"));
				break;
			case "topBalance":
				info.add(Utils.format("&a&lTop Balance Leaderboard"));
				break;
			case "topWave":
				info.add(Utils.format("&b&lTop Wave Leaderboard"));
				break;
			default:
				return null;
		}

		// Gather relevant stats
		for (String key : Objects.requireNonNull(plugin.getPlayerData().getConfigurationSection(""))
				.getKeys(false)) {
					if (!key.equals("logger") && plugin.getPlayerData().contains(key + "." + type))
						mapping.put(key, plugin.getPlayerData().getInt(key + "." + type));
				}

		// Put names and values into the leaderboard
		mapping.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
				.forEachOrdered(map -> info.add(map.getKey() + " - &b" + map.getValue()));

		for (int i = 1; i < info.size(); i++)
			info.set(i, Utils.format("&6" + i + ") &f" + info.get(i)));

		return info.toArray(new String[]{});
	}
}
