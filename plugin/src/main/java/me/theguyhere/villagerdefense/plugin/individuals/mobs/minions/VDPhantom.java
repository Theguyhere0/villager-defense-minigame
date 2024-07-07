package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDPhantom extends VDMinion {
	public static final String KEY = "phtm";

	public VDPhantom(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.phantom,
			LanguageManager.mobLore.phantom,
			IndividualAttackType.SLASHING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .15);
		setLoot(getValue(arena.getCurrentDifficulty()), .2);
		updateNameTag();
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 6.5)
			return 1;
		else if (difficulty < 8)
			return 2;
		else if (difficulty < 10)
			return 3;
		else if (difficulty < 12)
			return 4;
		else if (difficulty < 14.5)
			return 5;
		else return 6;
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
				return 180;
			case 2:
				return 225;
			case 3:
				return 300;
			case 4:
				return 360;
			case 5:
				return 400;
			case 6:
				return 440;
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
			case 4:
			case 5:
			case 6:
				return 3;
			default:
				return 0;
		}
	}

	/**
	 * Returns the proper toughness for the mob.
	 *
	 * @param level The mob's level.
	 * @return The toughness for the mob.
	 */
	protected static int getToughness(int level) {
		switch (level) {
			case 1:
				return 42;
			case 2:
				return 44;
			case 3:
			case 4:
				return 46;
			case 5:
				return 48;
			case 6:
				return 50;
			default:
				return 0;
		}
	}

	/**
	 * Returns the proper damage for the mob.
	 *
	 * @param level The mob's level.
	 * @return The damage for the mob.
	 */
	protected static int getDamage(int level) {
		switch (level) {
			case 1:
				return 75;
			case 2:
				return 85;
			case 3:
				return 100;
			case 4:
				return 115;
			case 5:
				return 125;
			case 6:
				return 135;
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
				return 115;
			case 2:
				return 160;
			case 3:
				return 240;
			case 4:
				return 330;
			case 5:
				return 395;
			case 6:
				return 470;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
