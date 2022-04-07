package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InventoryMeta implements InventoryHolder {
    private final @NotNull InventoryID inventoryID;
    private final @NotNull InventoryType type;
    private final Player player;
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

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, Player player) {
        this(inventoryID, type, player, null, 0);
    }

    public InventoryMeta(@NotNull InventoryID inventoryID, @NotNull InventoryType type, Player player, Arena arena) {
        this(inventoryID, type, player, arena, 0);
    }

    public InventoryMeta(
            @NotNull InventoryID inventoryID,
            @NotNull InventoryType type,
            Player player,
            Arena arena,
            int id
    ) {
        this(inventoryID, type, player, arena, id, 1);
    }

    public InventoryMeta(
            @NotNull InventoryID inventoryID,
            @NotNull InventoryType type,
            Player player,
            Arena arena,
            int id,
            int page
    ) {
        this.inventoryID = inventoryID;
        this.type = type;
        this.player = player;
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

    public Player getPlayer() {
        return player;
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
