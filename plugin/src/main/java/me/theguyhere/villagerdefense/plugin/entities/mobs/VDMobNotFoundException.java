package me.theguyhere.villagerdefense.plugin.entities.mobs;

/**
 * An exception thrown when a {@link VDMob} cannot be found.
 */
@SuppressWarnings("unused")
public class VDMobNotFoundException extends Exception {
	public VDMobNotFoundException(String msg) {
		super(msg);
	}

	public VDMobNotFoundException() {
		super();
	}
}
