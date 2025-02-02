package me.theguyhere.villagerdefense.plugin.game.exceptions;

@SuppressWarnings("unused")
public class InvalidNameException extends Exception{
    public InvalidNameException(String message) {
        super(message);
    }

    public InvalidNameException() {
        super();
    }
}
