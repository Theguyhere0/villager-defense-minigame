package me.theguyhere.villagerdefense.game.models;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryMeta implements InventoryHolder {
    private String string;
    private final int integer1;
    private int integer2;

    public InventoryMeta(int integer1) {
        this.integer1 = integer1;
    }

    public InventoryMeta(int integer1, int integer2) {
        this.integer1 = integer1;
        this.integer2 = integer2;
    }

    public InventoryMeta(String string, int integer1) {
        this.string = string;
        this.integer1 = integer1;
    }

    public String getString() {
        return string;
    }

    public int getInteger1() {
        return integer1;
    }

    public int getInteger2() {
        return integer2;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
