package me.theguyhere.villagerdefense.common;

/**
 * A class that holds common constants.
 */
public class Constants {
	// Package-private constants
	static final int SECONDS_TO_TICKS = 20;
	static final int MINUTES_TO_SECONDS = 60;
	static final int SECONDS_TO_MILLIS = 1000;

	// Parameter constants
	public static final int LORE_CHAR_LIMIT = 30;
	public static final int BORDER_SIZE = 1000000;

	// Mob attribute constants
	public static final double ATTACK_SPEED_VERY_SLOW = 3;
	public static final double ATTACK_SPEED_SLOW = 2;
	public static final double ATTACK_SPEED_MODERATE = 1;
	public static final double ATTACK_SPEED_FAST = 0.6;
	public static final double ATTACK_SPEED_VERY_FAST = 0.3;
	public static final double ATTACK_SPEED_RANGED_MULTIPLIER = 1.5;

	public static final int TARGET_RANGE_CLOSE = 16;
	public static final int TARGET_RANGE_MODERATE = 24;
	public static final int TARGET_RANGE_FAR = 40;
	public static final int TARGET_RANGE_UNBOUNDED = 100;
	public static final double BOW_ATTACK_RANGE_MULTIPLIER = 0.75;
	public static final double CROSSBOW_ATTACK_RANGE_MULTIPLIER = 0.5;

	public static final double SPEED_VERY_SLOW = 0.15;
	public static final double SPEED_SLOW = 0.2;
	public static final double SPEED_MEDIUM = 0.25;
	public static final double SPEED_FAST = 0.3;
	public static final double SPEED_VERY_FAST = 0.35;

	public static final double KNOCKBACK_NONE = 0;
	public static final double KNOCKBACK_LOW = 0.25;
	public static final double KNOCKBACK_MODERATE = 0.75;
	public static final double KNOCKBACK_HIGH = 1.25;
	public static final double KNOCKBACK_VERY_HIGH = 2.5;

	public static final double WEIGHT_VERY_LIGHT = 0;
	public static final double WEIGHT_LIGHT = 0.1;
	public static final double WEIGHT_MEDIUM = 0.25;
	public static final double WEIGHT_HEAVY = 0.4;
	public static final double WEIGHT_VERY_HEAVY = 0.7;

	// Symbol constants
	public static final String HP = "❤";
	public static final String HUNGER = "☕";
	public static final String ARMOR = "⛨";
	public static final String TOUGH = "❖";
	public static final String DAMAGE = "⚔";
	public static final String GEM = "♦";
	public static final String EXP = "★";
	public static final String HP_BAR = "▌";
	public static final String BLOCK = "■";
	public static final String ARROW = "➶";
	public static final String UPGRADE = "→";
}
