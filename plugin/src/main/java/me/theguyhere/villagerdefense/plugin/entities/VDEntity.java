package me.theguyhere.villagerdefense.plugin.entities;

import java.util.UUID;

public abstract class VDEntity {
	/**
	 * Positive integer base value defined for each level. Customizable plugin-wide.
	 */
	protected int maxHealth = 0;
	/**
	 * Positive integer tracker value. Not customizable.
	 */
	protected int currentHealth = 0;
	/**
	 * Positive integer base value defined for each level. Customizable plugin-wide.
	 */
	protected int armor = 0;
	/**
	 * Positive integer base value defined for each level. Customizable plugin-wide.
	 */
	protected int toughness = 0;
	/**
	 * Double between 0 and 1 inclusive, static or defined for each boss stage. Customizable plugin-wide.
	 */
	protected double fireResistance = 0;
	/**
	 * Double between 0 and 1 inclusive, static or defined for each boss stage. Customizable plugin-wide.
	 */
	protected double fallResistance = 0;
	/**
	 * Bidirectional linkage. Not customizable.
	 */
	private final int arenaId;
	/**
	 * Unique tracker. Not customizable.
	 */
	private final UUID id;

	protected VDEntity(int arenaId, UUID id) {
		this.arenaId = arenaId;
		this.id = id;
	}

	public UUID getId() {
		return id;
	}
}
