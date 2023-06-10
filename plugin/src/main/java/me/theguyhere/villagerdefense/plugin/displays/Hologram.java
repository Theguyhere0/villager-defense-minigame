package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Floating text displayed on client side. Can have multiple lines.
 */
public class Hologram {
	/**
	 * Text to be displayed by the Hologram.
	 */
	private final HoloLine[] lines;
	/**
	 * The location of the Hologram.
	 */
	private final Location location;
	/**
	 * Whether the lines will be aligned to the bottom or not.
	 */
	private final boolean bottomAligned;

	public Hologram(@NotNull Location location, String... lines) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Set location, lines, and bottom align
		this.location = location;
		this.lines = new HoloLine[lines.length];
		for (int i = 0; i < lines.length; i++)
			this.lines[i] = new HoloLine(lines[i], location.clone().add(0, (lines.length - i - 1) * .25, 0));
		bottomAligned = true;
	}

	public Hologram(@NotNull Location location, boolean bottomAligned, String... lines) throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Set location, lines, and bottom align
		this.location = location;
		this.bottomAligned = bottomAligned;
		this.lines = new HoloLine[lines.length];
		if (bottomAligned)
			for (int i = 0; i < lines.length; i++)
				this.lines[i] = new HoloLine(lines[i], location.clone().add(0, (lines.length - i - 1) * .25, 0));
		else
			for (int i = 0; i < lines.length; i++)
				this.lines[i] = new HoloLine(lines[i], location.clone().subtract(0, i * .25, 0));
	}

	public Location getLocation() {
		return location;
	}

	public boolean isBottomAligned() {
		return bottomAligned;
	}

	public HoloLine[] getLines() {
		return lines;
	}

	/**
	 * Spawn in the Hologram for every online player.
	 */
	public void displayForOnline() {
		Arrays.stream(lines).forEach(HoloLine::displayForOnline);
	}

	/**
	 * Spawn in the Hologram for a specific player.
	 *
	 * @param player - The player to display the Hologram for.
	 */
	public void displayForPlayer(Player player) {
		Arrays.stream(lines).forEach(holoLine -> holoLine.displayForPlayer(player));
	}

	/**
	 * Stop displaying the Hologram for every online player.
	 */
	public void remove() {
		Arrays.stream(lines).forEach(HoloLine::remove);
	}
}
