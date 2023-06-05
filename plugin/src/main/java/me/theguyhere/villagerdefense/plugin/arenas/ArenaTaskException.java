package me.theguyhere.villagerdefense.plugin.arenas;

/**
 * An exception thrown when a task run by an {@link Arena} encounters an error.
 */
@SuppressWarnings("unused")
public class ArenaTaskException extends ArenaException {
    public ArenaTaskException(String msg) {
        super(msg);
    }

    public ArenaTaskException() {
        super();
    }
}
