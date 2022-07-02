package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;

/**
 * An exception thrown when an {@link VDMob} cannot be found.
 */
public class VDMobNotFoundException extends Exception{
    public VDMobNotFoundException(String str) {
        super(str);
    }

    public VDMobNotFoundException() {
        super();
    }
}
