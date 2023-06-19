package me.theguyhere.villagerdefense.plugin.background;

import org.bukkit.Location;

/**
 * An exception thrown whenever a proper {@link Location} cannot be created.
 */
@SuppressWarnings("unused")
public class InvalidLocationException extends Exception {
	public InvalidLocationException(String msg) {
		super(msg);
	}

	public InvalidLocationException() {
		super();
	}
}
