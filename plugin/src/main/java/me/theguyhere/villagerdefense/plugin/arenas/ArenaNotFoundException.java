package me.theguyhere.villagerdefense.plugin.arenas;

/**
 * An exception thrown when an {@link Arena} cannot be found.
 */
@SuppressWarnings("unused")
public class ArenaNotFoundException extends ArenaException {
    public ArenaNotFoundException(String msg) {
        super(msg);
    }

    public ArenaNotFoundException() {
        super();
    }
}
