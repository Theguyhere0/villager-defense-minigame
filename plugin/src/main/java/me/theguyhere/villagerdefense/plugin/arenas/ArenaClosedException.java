package me.theguyhere.villagerdefense.plugin.arenas;

/**
 * An exception thrown whenever an action was prevented due to an {@link Arena} being closed.
 */
@SuppressWarnings("unused")
public class ArenaClosedException extends ArenaException {
    public ArenaClosedException(String msg) {
        super(msg);
    }

    public ArenaClosedException() {
        super();
    }
}
