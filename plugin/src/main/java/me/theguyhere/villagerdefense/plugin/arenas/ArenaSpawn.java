package me.theguyhere.villagerdefense.plugin.arenas;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.plugin.background.InvalidLocationException;
import me.theguyhere.villagerdefense.plugin.displays.SpawnIndicator;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class ArenaSpawn {
	/**
	 * Location of the arena spawn.
	 */
	@NotNull
	private final Location location;
	/**
	 * Type of arena spawn.
	 */
	@NotNull
	private final ArenaSpawnType spawnType;
	/**
	 * The spawn indicator for this spawn.
	 */
	@NotNull
	private final SpawnIndicator spawnIndicator;
	/**
	 * ID of the arena spawn.
	 */
	private final int id;
	/**
	 * Whether the spawn indicator is on or not.
	 */
	private boolean on = false;

	public ArenaSpawn(@NotNull Location location, @NotNull ArenaSpawnType spawnType, int id)
		throws InvalidLocationException {
		// Check for null world
		if (location.getWorld() == null)
			throw new InvalidLocationException("Location world cannot be null!");

		this.location = location;
		this.spawnType = spawnType;
		this.id = id;
		spawnIndicator = new SpawnIndicator(location, getName());
	}

	public @NotNull Location getLocation() {
		return location.clone();
	}

	public @NotNull ArenaSpawnType getSpawnType() {
		return spawnType;
	}

	public @NotNull SpawnIndicator getSpawnIndicator() {
		return spawnIndicator;
	}

	public int getId() {
		return id;
	}

	public boolean isOn() {
		return on;
	}

	public void turnOnIndicator() {
		if (!on) {
			spawnIndicator.displayForOnline();
			on = true;
		}
	}

	public void turnOffIndicator() {
		if (on) {
			spawnIndicator.remove();
			on = false;
		}
	}

	private String getName() {
		switch (spawnType) {
			case PLAYER:
				return CommunicationManager.format("&6Player Spawn");
			case MONSTER_AIR:
				return CommunicationManager.format("&3Monster Spawn (Air) " + id);
			case MONSTER_ALL:
				return CommunicationManager.format("&3Monster Spawn (All) " + id);
			case MONSTER_GROUND:
				return CommunicationManager.format("&3Monster Spawn (Ground) " + id);
			case VILLAGER:
				return CommunicationManager.format("&aVillager Spawn " + id);
			default:
				return null;
		}
	}
}
