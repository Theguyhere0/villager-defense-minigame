package me.theguyhere.villagerdefense.plugin.entities;

import java.util.UUID;

/**
 * A data structure representing all intelligent agents in the game.
 */
public abstract class VDEntity {
	/**
	 * Bidirectional linkage.
	 * Not customizable.
	 */
	private final int arenaId;
	/**
	 * Unique tracker.
	 * Not customizable.
	 */
	private final UUID id;
	/**
	 * Greatest possible health. Defined for each level.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected int maxHealth;
	/**
	 * Health points.
	 * Not customizable.
	 * Modified mechanics only.
	 */
	protected int currentHealth;
	/**
	 * Relative size of health bar.
	 * Customizable plugin-wide for minions. Not customizable for others.
	 * Modified mechanics only.
	 */
	protected int hpBarSize;
	/**
	 * Armor points. Defined for each level.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected int armor;
	/**
	 * Toughness points. Defined for each level.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected int toughness;
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
	 * Proportion of damage to fire negated. Static or defined for each boss stage.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected double fireResistance;
	/**
	 * Proportion of damage to fall negated. Static or defined for each boss stage.
	 * Customizable plugin-wide.
	 * Modified mechanics only.
	 */
	protected double fallResistance;
	/**
	 * Side of conflict.
	 * Not customizable.
	 */
	protected VDTeam team;

	protected VDEntity(int arenaId) {
		this.arenaId = arenaId;
		id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}
}
