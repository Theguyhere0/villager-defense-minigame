package me.theguyhere.villagerdefense.plugin.displays;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Constructor for floating one-line text with a limited lifetime to indicate temporary notifications.
 */
public class Popup {
	public static void create(@NotNull Location location, String text, double life, Player... players)
		throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		// Display
		HoloLine display = new HoloLine(text, location);
		for (Player player : players) {
			display.displayForPlayer(player);
		}

		// Schedule end of life
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, display::remove, Calculator.secondsToTicks(life));
	}
}
