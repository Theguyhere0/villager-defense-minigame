package me.theguyhere.villagerdefense.plugin.exceptions;

public class ArenaClosedException extends ArenaException {
    public ArenaClosedException(String msg) {
        super(msg);
    }

    public ArenaClosedException() {
        super();
    }
}
