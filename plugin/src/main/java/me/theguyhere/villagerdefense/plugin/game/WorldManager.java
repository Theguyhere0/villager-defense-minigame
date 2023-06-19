package me.theguyhere.villagerdefense.plugin.game;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class to manage world manipulations.
 */
public class WorldManager {
	/**
	 * Clears the bounding region of all living entities excluding players, as well as experience orbs.
	 *
	 * @param corner1 First bounding corner
	 * @param corner2 Second bounding corner
	 */
	public static void clear(Location corner1, Location corner2) {
		Collection<Entity> ents;

		// Get all entities near spawn
		try {
			ents = Objects
				.requireNonNull(corner1.getWorld())
				.getNearbyEntities(BoundingBox.of(corner1, corner2));
		}
		catch (Exception e) {
			return;
		}

		// Clear the arena for living entities
		ents.forEach(ent -> {
			if (ent instanceof LivingEntity && !(ent instanceof Player))
				ent.remove();
		});

		// Clear the arena for items and experience orbs
		ents.forEach(ent -> {
			if (ent instanceof Item || ent instanceof ExperienceOrb) ent.remove();
		});
	}

	// Get nearby players
	public static List<Player> getNearbyPlayers(Player player, double range) {
		return player
			.getNearbyEntities(range, range, range)
			.stream()
			.filter(Objects::nonNull)
			.filter(ent -> ent instanceof Player)
			.map(ent -> (Player) ent)
			.collect(Collectors.toList());
	}

	// Get nearby allies
	public static List<LivingEntity> getNearbyAllies(Player player, double range) {
		return player
			.getNearbyEntities(range, range, range)
			.stream()
			.filter(Objects::nonNull)
			.filter(ent -> ent instanceof Villager ||
				ent instanceof Wolf || ent instanceof IronGolem)
			.map(ent -> (LivingEntity) ent)
			.collect(Collectors.toList());
	}

	// Get wolves
	public static List<Wolf> getPets(Player player) {
		return player
			.getNearbyEntities(150, 50, 150)
			.stream()
			.filter(Objects::nonNull)
			.filter(ent -> ent instanceof Wolf)
			.map(ent -> (Wolf) ent)
			.filter(wolf -> Objects.equals(wolf.getOwner(), player))
			.collect(Collectors.toList());
	}

	// Get nearby monsters
	public static List<LivingEntity> getNearbyMonsters(Player player, double range) {
		return player
			.getNearbyEntities(range, range, range)
			.stream()
			.filter(Objects::nonNull)
			.filter(ent -> ent instanceof Monster ||
				ent instanceof Slime || ent instanceof Hoglin || ent instanceof Phantom)
			.map(ent -> (LivingEntity) ent)
			.collect(Collectors.toList());
	}
}
