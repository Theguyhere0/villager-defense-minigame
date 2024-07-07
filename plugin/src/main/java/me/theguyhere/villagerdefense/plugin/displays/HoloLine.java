package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.nms.common.entities.TextPacketEntity;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.game.PlayerManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A holographic text line. Not meant to be used on its own.
 */
public class HoloLine {
	/**
	 * The text to display.
	 */
	private final String text;
	/**
	 * The location of the HoloLine.
	 */
	private final Location location;
	/**
	 * Text packet entity representing this HoloLine.
	 */
	private final TextPacketEntity mainEntity;
	/**
	 * A second text packet entity to darken the background of the HoloLine.
	 */
	private final TextPacketEntity secondaryEntity;


	public HoloLine(String text, @NotNull Location location) throws InvalidLocationException {
		this.text = text;

		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Set location and packet entity
		this.location = location;
		this.mainEntity = NMSVersion
			.getCurrent()
			.getNmsManager()
			.newTextPacketEntity();
		this.secondaryEntity = NMSVersion
			.getCurrent()
			.getNmsManager()
			.newTextPacketEntity();
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * Spawn in the HoloLine for every online player.
	 */
	public void displayForOnline() {
		PlayerManager.sendLocationPacketToOnline(
			mainEntity.newSpawnPackets(location, text),
			location.getWorld()
		);
		PlayerManager.sendLocationPacketToOnline(
			secondaryEntity.newSpawnPackets(location, text),
			location.getWorld()
		);
	}

	/**
	 * Spawn in the HoloLine for a specific player.
	 *
	 * @param player - The player to display the HoloLine for.
	 */
	public void displayForPlayer(Player player) {
		// Only display if player is in the same world
		if (player
			.getWorld()
			.equals(location.getWorld())) {
			mainEntity
				.newSpawnPackets(location, text)
				.sendTo(player);
			secondaryEntity
				.newSpawnPackets(location, text)
				.sendTo(player);
		}
	}

	/**
	 * Stop displaying the HoloLine for every online player.
	 */
	public void remove() {
		PlayerManager.sendPacketToOnline(mainEntity.newDestroyPackets());
		PlayerManager.sendPacketToOnline(secondaryEntity.newDestroyPackets());
	}
}
