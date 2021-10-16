package me.theguyhere.villagerdefense.game.models.arenas;

public class InvalidNameException extends Exception{
    public InvalidNameException(String message) {
        super(message);
    }

    public InvalidNameException() {
        super();
    }
}
