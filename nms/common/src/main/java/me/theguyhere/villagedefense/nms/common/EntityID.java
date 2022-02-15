package me.theguyhere.villagedefense.nms.common;

import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.UUID;

public class EntityID {

    // Lazy initialization
    private @Nullable Integer numericID;
    private @Nullable UUID uuid;

    public int getNumericID() {
        if (numericID == null) {
            numericID = new Random().nextInt();
        }
        return numericID;
    }

    public UUID getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    public boolean hasInitializedNumericID() {
        return numericID != null;
    }
}
