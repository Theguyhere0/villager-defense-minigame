package me.theguyhere.villagerdefense.plugin.game.displays;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.exceptions.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoBoard {
	/** The information for the InfoBoard.*/
	private final Hologram hologram;
	/** The location of the InfoBoard.*/
	private final Location location;

	public InfoBoard(@NotNull Location location) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Gather info text
		String[] text = {CommunicationManager.format(ChatColor.YELLOW, LanguageManager.messages.info1),
				CommunicationManager.format(ChatColor.GREEN, LanguageManager.messages.info2),
				CommunicationManager.format(ChatColor.GOLD, LanguageManager.messages.info3, ChatColor.AQUA,
						"/vd stats"),
				CommunicationManager.format(ChatColor.GOLD, LanguageManager.messages.info4, ChatColor.AQUA,
						"/vd kits"),
				CommunicationManager.format(ChatColor.GOLD, LanguageManager.messages.info5, ChatColor.AQUA,
						"/vd help"),
				CommunicationManager.format(ChatColor.GOLD, LanguageManager.messages.info6, ChatColor.AQUA,
						"/vd leave")};

		// Set location and hologram
		this.location = location;
		this.hologram = new Hologram(location.clone().add(0, .5, 0), text);
	}

	public Location getLocation() {
		return location;
	}

	public Hologram getHologram() {
		return hologram;
	}

	/**
	 * Spawn in the InfoBoard for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
	}

	/**
	 * Spawn in the InfoBoard for a specific player.
	 * @param player - The player to display the InfoBoard for.
	 */
	public void displayForPlayer(Player player) {
		hologram.displayForPlayer(player);
	}

	/**
	 * Stop displaying the InfoBoard for every online player.
	 */
	public void remove() {
		hologram.remove();
	}
}
