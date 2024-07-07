package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.common.ColoredMessage;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoBoard {
	/**
	 * The information for the InfoBoard.
	 */
	private final Hologram hologram;
	/**
	 * The location of the InfoBoard.
	 */
	private final Location location;

	public InfoBoard(@NotNull Location location) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Gather info text
		String[] text = {new ColoredMessage(ChatColor.YELLOW, LanguageManager.messages.info1).toString(),
			new ColoredMessage(ChatColor.GREEN, LanguageManager.messages.info2).toString(),
			CommunicationManager.format(
				infoMessage(LanguageManager.messages.info3),
				replaceMessage("/vd stats")
			),
			CommunicationManager.format(infoMessage(LanguageManager.messages.info4),
				replaceMessage("/vd kits"), replaceMessage(LanguageManager.names.crystals)
			),
			CommunicationManager.format(
				infoMessage(LanguageManager.messages.info5),
				replaceMessage("/vd help")
			),
			CommunicationManager.format(
				infoMessage(LanguageManager.messages.info6),
				replaceMessage("/vd leave")
			)};

		// Set location and hologram
		this.location = location;
		this.hologram = new Hologram(location
			.clone()
			.add(0, .5, 0), text);
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * Spawn in the InfoBoard for every online player.
	 */
	public void displayForOnline() {
		hologram.displayForOnline();
	}

	/**
	 * Spawn in the InfoBoard for a specific player.
	 *
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

	private static ColoredMessage infoMessage(String msg) {
		return new ColoredMessage(ChatColor.GOLD, msg);
	}

	private static ColoredMessage replaceMessage(String msg) {
		return new ColoredMessage(ChatColor.AQUA, msg);
	}
}
