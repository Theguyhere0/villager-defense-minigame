package me.theguyhere.villagerdefense.plugin.data.exceptions;

public class UpdateFailedException extends Exception {
    public UpdateFailedException(String message) {
        super(message);
    }

    public UpdateFailedException() {
      super();
    }
}
