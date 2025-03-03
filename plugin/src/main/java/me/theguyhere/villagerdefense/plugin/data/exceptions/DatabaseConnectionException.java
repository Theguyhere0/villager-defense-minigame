package me.theguyhere.villagerdefense.plugin.data.exceptions;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException() {
        super();
    }
}
