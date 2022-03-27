package me.theguyhere.villagerdefense.plugin.inventories;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class InventoryMeta implements InventoryHolder {
    private final InventoryType type;
    private final Player player;
    private final Arena arena;
    private final int id;

    public InventoryMeta(InventoryType type) {
        this(type, null, 0);
    }

    public InventoryMeta(InventoryType type, Arena arena) {
        this(type, arena, 0);
    }

    public InventoryMeta(InventoryType type, int id) {
        this(type, null, id);
    }

    public InventoryMeta(InventoryType type, Arena arena, int id) {
        this(type, null, arena, id);
    }

    public InventoryMeta(InventoryType type, Player player) {
        this(type, player, null, 0);
    }

    public InventoryMeta(InventoryType type, Player player, Arena arena) {
        this(type, player, arena, 0);
    }

    public InventoryMeta(InventoryType type, Player player, Arena arena, int id) {
        this.type = type;
        this.player = player;
        this.arena = arena;
        this.id = id;
    }

    public InventoryType getType() {
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

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, 0);
    }
}
