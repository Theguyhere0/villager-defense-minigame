package me.theguyhere.villagerdefense.plugin.background.exceptions;

public class InvalidLocationException extends Exception {
    public InvalidLocationException(String msg) {
        super(msg);
    }

    public InvalidLocationException() {
        super();
    }
}
