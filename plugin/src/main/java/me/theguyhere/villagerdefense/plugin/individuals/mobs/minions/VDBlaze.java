package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

public class VDBlaze extends VDMinion {
	public static final String KEY = "blze";

	public VDBlaze(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.blaze,
			LanguageManager.mobLore.blaze,
			IndividualAttackType.NONE
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		setEffectType(PotionEffectType.FIRE_RESISTANCE);
		effectLevel = 1;
		effectDuration = getEffectDuration(level);
		setLoot(getValue(arena.getCurrentDifficulty()), .1);
		updateNameTag();
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 10)
			return 1;
		else if (difficulty < 12)
			return 2;
		else if (difficulty < 14)
			return 3;
		else if (difficulty < 16.5)
			return 4;
		else return 5;
	}

	/**
	 * Returns the proper health for the mob.
	 *
	 * @param level The mob's level.
	 * @return The health for the mob.
	 */
	protected static int getHealth(int level) {
		switch (level) {
			case 1:
				return 360;
			case 2:
				return 425;
			case 3:
				return 525;
			case 4:
				return 640;
			case 5:
				return 775;
			default:
				return 0;
		}
	}

	/**
	 * Returns the proper armor for the mob.
	 *
	 * @param level The mob's level.
	 * @return The armor for the mob.
	 */
	protected static int getArmor(int level) {
		switch (level) {
			case 1:
				return 50;
			case 2:
				return 53;
			case 3:
				return 56;
			case 4:
				return 60;
			case 5:
				return 65;
			default:
				return 0;
		}
	}

	/**
	 * Returns the proper effect duration for the mob.
	 *
	 * @param level The mob's level.
	 * @return The effect duration for the mob.
	 */
	protected static int getEffectDuration(int level) {
		switch (level) {
			case 1:
				return 5;
			case 2:
				return 8;
			case 3:
				return 12;
			case 4:
				return 15;
			case 5:
				return 20;
			default:
				return 0;
		}
	}

	/**
	 * Calculates the value this minion has given arena and wave parameters.
	 *
	 * @param difficulty Current arena difficulty.
	 * @return Value of this minion.
	 */
	protected static int getValue(double difficulty) {
		int level = getLevel(difficulty);
		switch (level) {
			case 1:
				return 220;
			case 2:
				return 255;
			case 3:
				return 300;
			case 4:
				return 355;
			case 5:
				return 420;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
