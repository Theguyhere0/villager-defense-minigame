package me.theguyhere.villagerdefense.plugin.guis;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
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
	private final boolean clickEnabled;
	private final boolean dragEnabled;

	private InventoryMeta(InventoryMetaBuilder builder) {
		this.inventoryID = builder.inventoryID;
		this.type = builder.type;
		this.playerID = builder.playerID;
		this.arena = builder.arena;
		this.id = builder.id;
		this.page = builder.page;
		this.clickEnabled = builder.clickEnabled;
		this.dragEnabled = builder.dragEnabled;
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

	public boolean isClickEnabled() {
		return clickEnabled;
	}

	public boolean isDragEnabled() {
		return dragEnabled;
	}

	public static class InventoryMetaBuilder {
		private final @NotNull InventoryID inventoryID;
		private final @NotNull InventoryType type;
		private UUID playerID = null;
		private Arena arena = null;
		private int id = 0;
		private int page = 1;
		private boolean clickEnabled = false;
		private boolean dragEnabled = false;

		public InventoryMetaBuilder(@NotNull InventoryID inventoryID, @NotNull InventoryType type) {
			this.inventoryID = inventoryID;
			this.type = type;
		}

		public InventoryMetaBuilder setPlayerID(UUID playerID) {
			this.playerID = playerID;
			return this;
		}

		public InventoryMetaBuilder setArena(Arena arena) {
			this.arena = arena;
			return this;
		}

		public InventoryMetaBuilder setID(int id) {
			this.id = id;
			return this;
		}

		public InventoryMetaBuilder setPage(int page) {
			this.page = page;
			return this;
		}

		public InventoryMetaBuilder setClickEnabled() {
			this.clickEnabled = true;
			return this;
		}

		public InventoryMetaBuilder setDragEnabled() {
			this.dragEnabled = true;
			return this;
		}

		public InventoryMeta build() {
			return new InventoryMeta(this);
		}
	}

	@Override
	public @NotNull Inventory getInventory() {
		return Bukkit.createInventory(this, 0);
	}
}
