package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * Thrown when a task run by an arena encounters an error.
 */
public class ArenaTaskException extends ArenaException {
    public ArenaTaskException(String msg) {
        super(msg);
    }

    public ArenaTaskException() {
        super();
    }
}
