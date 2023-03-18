package me.theguyhere.villagerdefense.plugin.exceptions;

import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;

/**
 * An exception thrown when the spawn table key doesn't correspond to any {@link VDMob}.
 */
@SuppressWarnings("unused")
public class InvalidVDMobKeyException extends Exception {
    public InvalidVDMobKeyException(String msg) {
        super(msg);
    }

    public InvalidVDMobKeyException() {
        super();
    }
}
