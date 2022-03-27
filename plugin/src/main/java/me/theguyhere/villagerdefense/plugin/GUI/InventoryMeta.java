package me.theguyhere.villagerdefense.plugin.GUI;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryMeta implements InventoryHolder {
    private final InventoryType type;
    private final int integer1;
    private final int integer2;

    public InventoryMeta(InventoryType type) {
        this(type, 0, 0);
    }

    public InventoryMeta(InventoryType type, int integer1) {
        this(type, integer1, 0);
    }

    public InventoryMeta(InventoryType type, int integer1, int integer2) {
        this.type = type;
        this.integer1 = integer1;
        this.integer2 = integer2;
    }

    public InventoryType getType() {
        return type;
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
