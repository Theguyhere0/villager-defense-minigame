package me.theguyhere.villagerdefense.plugin.data.exceptions;

public class InvalidLocationException extends Exception {
    public InvalidLocationException(String msg) {
        super(msg);
    }

    public InvalidLocationException() {
        super();
    }
}
