package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.DataManager;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Leaderboard {
	/**
	 * The information for the Leaderboard.
	 */
	private final Hologram hologram;
	/**
	 * The location of the Leaderboard.
	 */
	private final Location location;

	// Leaderboard id constants
	public static final String TOP_BALANCE = "topBalance";
	public static final String TOP_KILLS = "topKills";
	public static final String TOP_WAVE = "topWave";
	public static final String TOTAL_GEMS = "totalGems";
	public static final String TOTAL_KILLS = "totalKills";

	public Leaderboard(@NotNull String id) throws InvalidLocationException {
		Location location = Objects.requireNonNull(DataManager.getConfigLocationNoPitch("leaderboard." + id));

		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Gather info text
		List<String> info = new ArrayList<>();
		Map<String, Integer> mapping = new HashMap<>();

		// Determine leaderboard title
		switch (id) {
			case TOTAL_KILLS:
				info.add(CommunicationManager.format("&d&l" + LanguageManager.playerStats.totalKills.leaderboard));
				break;
			case TOP_KILLS:
				info.add(CommunicationManager.format("&c&l" + LanguageManager.playerStats.topKills.leaderboard));
				break;
			case TOTAL_GEMS:
				info.add(CommunicationManager.format("&e&l" + LanguageManager.playerStats.totalGems.leaderboard));
				break;
			case TOP_BALANCE:
				info.add(CommunicationManager.format("&a&l" + LanguageManager.playerStats.topBalance.leaderboard));
				break;
			case TOP_WAVE:
				info.add(CommunicationManager.format("&b&l" + LanguageManager.playerStats.topWave.leaderboard));
				break;
			default:
				info.add("");
		}

		// Gather relevant stats
		for (String key : Objects
			.requireNonNull(Main
				.getPlayerData()
				.getConfigurationSection(""))
			.getKeys(false)) {
			if (!key.equals("logger") && Main
				.getPlayerData()
				.contains(key + "." + id))
				mapping.put(key, Main
					.getPlayerData()
					.getInt(key + "." + id));
		}

		// Put names and values into the leaderboard
		mapping
			.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
			.filter(set -> Bukkit
				.getOfflinePlayer(UUID.fromString(set.getKey()))
				.getName() != null)
			.filter(set -> set.getValue() > 0)
			.limit(10)
			.forEachOrdered(set -> {
				try {
					info.add(Bukkit
						.getOfflinePlayer(UUID.fromString(set.getKey()))
						.getName() +
						" - &b" + set.getValue());
				}
				catch (Exception ignored) {
				}
			});

		for (int i = 1; i < info.size(); i++)
			info.set(i, CommunicationManager.format("&6" + i + ") &f" + info.get(i)));

		// Set location and hologram
		this.location = location;
		this.hologram = info
			.get(0)
			.isEmpty() ? null : new Hologram(location
			.clone()
			.add(0, 2.5, 0),
			false, info.toArray(new String[]{})
		);
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * Spawn in the Leaderboard for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
	}

	/**
	 * Spawn in the Leaderboard for a specific player.
	 *
	 * @param player - The player to display the Leaderboard for.
	 */
	public void displayForPlayer(Player player) {
		hologram.displayForPlayer(player);
	}

	/**
	 * Stop displaying the Leaderboard for every online player.
	 */
	public void remove() {
		hologram.remove();
	}
}
