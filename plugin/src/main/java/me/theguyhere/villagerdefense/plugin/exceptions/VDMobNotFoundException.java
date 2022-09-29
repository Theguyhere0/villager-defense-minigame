package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;

/**
 * An exception thrown when a {@link VDMob} cannot be found.
 */
@SuppressWarnings("unused")
public class VDMobNotFoundException extends Exception{
    public VDMobNotFoundException(String msg) {
        super(msg);
    }

    public VDMobNotFoundException() {
        super();
    }
}
