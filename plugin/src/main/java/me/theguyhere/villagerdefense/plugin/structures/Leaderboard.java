package me.theguyhere.villagerdefense.plugin.structures;

import lombok.Getter;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.data.GameDataManager;
import me.theguyhere.villagerdefense.plugin.data.PlayerDataManager;
import me.theguyhere.villagerdefense.plugin.data.exceptions.BadDataException;
import me.theguyhere.villagerdefense.plugin.data.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.data.LanguageManager;
import me.theguyhere.villagerdefense.plugin.data.exceptions.NoSuchPathException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public class Leaderboard {
	/** The information for the Leaderboard.*/
	private final Hologram hologram;
	/** The location of the Leaderboard.*/
	private final Location location;

	public Leaderboard(@NotNull String type) throws BadDataException, InvalidLocationException, NoSuchPathException {
		Location location = GameDataManager.getLeaderboardLocation(type);

		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Gather info text
		List<String> info = new ArrayList<>();
		Map<UUID, Integer> mapping = new HashMap<>();

		// Determine leaderboard title
		switch (type) {
			case "totalKills":
				info.add(CommunicationManager.format("&d&l" + LanguageManager.playerStats.totalKills.leaderboard));
				break;
			case "topKills":
				info.add(CommunicationManager.format("&c&l" + LanguageManager.playerStats.topKills.leaderboard));
				break;
			case "totalGems":
				info.add(CommunicationManager.format("&e&l" + LanguageManager.playerStats.totalGems.leaderboard));
				break;
			case "topBalance":
				info.add(CommunicationManager.format("&a&l" + LanguageManager.playerStats.topBalance.leaderboard));
				break;
			case "topWave":
				info.add(CommunicationManager.format("&b&l" + LanguageManager.playerStats.topWave.leaderboard));
				break;
			default:
				info.add("");
		}

		// Gather relevant stats
		for (UUID uuid : PlayerDataManager.getTrackedPlayers()) {
				try {
					mapping.put(uuid, PlayerDataManager.getPlayerStat(uuid, type));
				} catch (IllegalArgumentException ignored) {}
		}

		// Put names and values into the leaderboard
		mapping.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.filter(set -> Bukkit.getOfflinePlayer(set.getKey()).getName() != null)
				.filter(set -> set.getValue() > 0)
				.limit(10).forEachOrdered(set -> {
					try {
						info.add(Bukkit.getOfflinePlayer(set.getKey()).getName() + " - &b" + set.getValue());
					} catch (Exception ignored) {
					}
				});

		for (int i = 1; i < info.size(); i++)
			info.set(i, CommunicationManager.format("&6" + i + ") &f" + info.get(i)));

		// Set location and hologram
		this.location = location;
		this.hologram = info.get(0).isEmpty() ? null : new Hologram(location.clone().add(0, 2.5, 0),
				false, info.toArray(new String[]{}));
	}

    /**
	 * Spawn in the Leaderboard for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
	}

	/**
	 * Spawn in the Leaderboard for a specific player.
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
