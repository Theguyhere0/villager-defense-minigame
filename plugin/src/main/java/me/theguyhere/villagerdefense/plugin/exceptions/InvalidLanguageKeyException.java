package me.theguyhere.villagerdefense.plugin.exceptions;

public class InvalidLanguageKeyException extends Exception {
    public InvalidLanguageKeyException(String msg) {
        super(msg);
    }

    public InvalidLanguageKeyException() {
        super();
    }
}
