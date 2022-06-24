package me.theguyhere.villagerdefense.plugin.exceptions;

public class InvalidArenaNameException extends ArenaException{
    public InvalidArenaNameException(String msg) {
        super(msg);
    }

    public InvalidArenaNameException() {
        super();
    }
}
