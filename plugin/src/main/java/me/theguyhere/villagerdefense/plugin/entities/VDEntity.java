package me.theguyhere.villagerdefense.plugin.entities;

import lombok.Getter;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A data structure representing all intelligent agents in the game.
 */
public abstract class VDEntity {
	/**
	 * Bidirectional linkage to the arena this entity is part of.
	 * Not customizable.
	 */
	@Getter
	@NotNull
	protected final Arena arena;
	/**
	 * Unique tracker.
	 * Not customizable.
	 */
	@Getter
	@NotNull
	protected final UUID id;
	/**
	 * Relative size of health bar.
	 * Customizable plugin-wide for minions. Not customizable for others.
	 */
	protected final int healthBarSize;
	/**
	 * The most amount of health this entity can have over the course of its life. Defined for each level.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected int maxHealth;
	/**
	 * The amount of damage this entity can take before it dies.
	 * Not customizable.
	 * Modified mechanics only.
	 */
	protected int currentHealth;
	/**
	 * The amount of damage this entity can take before it affects its health.
	 * Not customizable.
	 * Modified mechanics only.
	 */
	protected int absorption;
	/**
	 * A type of resistance to attack damage. Defined for each level.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected int armor;
	/**
	 * A type of resistance to attack damage. Defined for each level.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected double toughness;
	/**
	 * Proportion of damage from fire negated. Defined for each boss stage.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected double fireResistance;
	/**
	 * Proportion of damage from fall negated. Defined for each boss stage.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected double fallResistance;
	/**
	 * Resistance to knockback. Defined for each level.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected double weight;
	/**
	 * Travel speed in blocks per second. Defined for each level.
	 * Customizable plugin-wide for pets, golems, summons, minions, and bosses. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected double moveSpeed;
	/**
	 * Side of conflict.
	 * Not customizable.
	 */
	@Getter
	protected Team team;

	/**
	 * Modified mechanics constructor.
	 * @param arena The arena this entity is part of.
	 * @param healthBarSize Self-explanatory.
	 * @param maxHealth Self-explanatory.
	 */
	protected VDEntity(@NotNull Arena arena, int healthBarSize, int maxHealth) {
		this.arena = arena;
		id = UUID.randomUUID();
		this.healthBarSize = healthBarSize;
		this.maxHealth = maxHealth;
	}

	/**
	 * Vanilla mechanics constructor.
	 * @param arena The arena this entity is part of.
	 * @param healthBarSize Self-explanatory.
	 */
	protected VDEntity(@NotNull Arena arena, int healthBarSize) {
		this(arena, healthBarSize, 0);
	}

	/**
	 * Tags for which team an entity belongs on.
	 */
	@Getter
	public enum Team {
		VILLAGER("villager"),
		MONSTER("monster");

		@NotNull
		private final String value;

		Team(@NotNull String value) {
			this.value = value;
		}
	}
}
