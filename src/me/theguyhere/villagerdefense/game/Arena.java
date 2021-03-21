package me.theguyhere.villagerdefense.game;

import org.bukkit.inventory.Inventory;

public class Arena {
    private final int arena; // The arena number
    private int villagers; // Villager count
    private int enemies; // Enemy count
    private Inventory shop; // Shop inventory
    private final Tasks task; // The tasks object for the arena
    private boolean ending; // Whether the arena is about to end

    public Arena(int arena, Tasks task) {
        this.arena = arena;
        villagers = 0;
        enemies = 0;
        this.task = task;
    }

    public int getArena() {
        return arena;
    }

    public int getVillagers() {
        return villagers;
    }

    public void incrementVillagers() {
        villagers++;
    }

    public void decrementVillagers() {
        villagers--;
    }

    public int getEnemies() {
        return enemies;
    }

    public void incrementEnemies() {
        enemies++;
    }

    public void decrementEnemies() {
        enemies--;
    }

    public Inventory getShop() {
        return shop;
    }

    public void setShop(Inventory shop) {
        this.shop = shop;
    }

    public Tasks getTask() {
        return task;
    }

    public boolean isEnding() {
        return ending;
    }

    public void flipEnding() {
        ending = !ending;
    }
}
