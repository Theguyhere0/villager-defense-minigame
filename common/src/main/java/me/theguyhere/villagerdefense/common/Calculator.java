package me.theguyhere.villagerdefense.common;

import org.bukkit.Location;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

/**
 * A class that holds common calculation and conversion methods.
 */
public class Calculator {
	// Convert seconds to ticks
	public static int secondsToTicks(double seconds) {
		return (int) (seconds * Constants.SECONDS_TO_TICKS);
	}

	// Convert minutes to seconds
	public static int minutesToSeconds(double minutes) {
		return (int) (minutes * Constants.MINUTES_TO_SECONDS);
	}

	// Convert seconds to milliseconds
	public static int secondsToMillis(double seconds) {
		return (int) (seconds * Constants.SECONDS_TO_MILLIS);
	}

	// Convert milliseconds to seconds
	public static double millisToSeconds(double millis) {
		return millis / Constants.SECONDS_TO_MILLIS;
	}

	// Convert degrees to byte
	public static int degreesToByte(float angle) {
		return (int) (angle / 360f * 256);
	}

	/**
	 * Fetches the next smallest whole number based on some existing whole numbers.
	 *
	 * @param existingWholes Existing whole numbers
	 * @return Next smallest unique whole number
	 */
	public static int nextSmallestUniqueWhole(Collection<Integer> existingWholes) {
		for (int i = 0; i <= existingWholes.size(); i++) {
			if (!existingWholes.contains(i))
				return i;
		}
		// Should never reach here
		return 0;
	}

	/**
	 * Rounds a number to the nearest arbitrary positive integer. If rounding to nearest 0, just performs normal round.
	 *
	 * @param toRound   The number to round
	 * @param toNearest The number to round to
	 * @return The rounded number
	 */
	public static int roundToNearest(double toRound, int toNearest) {
		if (toNearest != 0)
			return (int) (Math.round(toRound / toNearest) * toNearest);
		else return (int) Math.round(toRound);
	}

	public static Location randomCircleAroundLocation(Location location, double radius, boolean safe) {
		Random r = new Random();
		double angle = r.nextDouble(2 * Math.PI);
		Location result = location.add(Math.sin(angle) * radius, 0, Math.cos(angle) * radius);
		if (safe)
			result.setY(Objects
				.requireNonNull(location.getWorld())
				.getHighestBlockYAt(result));
		return result;
	}
}
