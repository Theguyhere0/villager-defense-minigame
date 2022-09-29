package me.theguyhere.villagerdefense.plugin.exceptions;

/**
 * An exception thrown whenever the language file key doesn't exist in the current language file.
 */
@SuppressWarnings("unused")
public class InvalidLanguageKeyException extends Exception {
    public InvalidLanguageKeyException(String msg) {
        super(msg);
    }

    public InvalidLanguageKeyException() {
        super();
    }
}
