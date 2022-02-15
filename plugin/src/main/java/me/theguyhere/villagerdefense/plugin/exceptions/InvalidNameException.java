package me.theguyhere.villagerdefense.plugin.exceptions;

public class InvalidNameException extends Exception{
    public InvalidNameException(String message) {
        super(message);
    }

    public InvalidNameException() {
        super();
    }
}
