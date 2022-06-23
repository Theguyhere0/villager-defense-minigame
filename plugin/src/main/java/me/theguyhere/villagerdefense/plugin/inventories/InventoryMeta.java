package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InventoryMeta implements InventoryHolder {
    private final @NotNull InventoryID inventoryID;
    private final @NotNull InventoryType type;
    private final UUID playerID;
    private final Arena arena;
    private final int id;
    private final int page;

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type) {
        this(inventoryID, type, null, 0);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, int page) {
        this(inventoryID, type, null, null, 0, page);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, Arena arena) {
        this(inventoryID, type, arena, 0);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, int page, Arena arena) {
        this(inventoryID, type, null, arena, 0, page);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, Arena arena, int id) {
        this(inventoryID, type, null, arena, id);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, UUID playerID) {
        this(inventoryID, type, playerID, null, 0);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, int page, UUID playerID) {
        this(inventoryID, type, playerID, null, 0, page);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, UUID playerID, Arena arena) {
        this(inventoryID, type, playerID, arena, 0);
    }

    public InventoryMeta(
            @NotNull InventoryID inventoryID,
            @NotNull InventoryType type,
            UUID playerID,
            Arena arena,
            int id
    ) {
        this(inventoryID, type, playerID, arena, id, 1);
    }

    public InventoryMeta(
            @NotNull InventoryID inventoryID,
            @NotNull InventoryType type,
            UUID playerID,
            Arena arena,
            int id,
            int page
    ) {
        this.inventoryID = inventoryID;
        this.type = type;
        this.playerID = playerID;
        this.arena = arena;
        this.id = id;
        this.page = page;
    }

    public @NotNull InventoryID getInventoryID() {
        return inventoryID;
    }

    public @NotNull InventoryType getType() {
        return type;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public Arena getArena() {
        return arena;
    }

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 0);
    }
}
