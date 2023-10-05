package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDSilverfish extends VDMinion {
	public static final String KEY = "slvr";

	public VDSilverfish(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.silverfish,
			LanguageManager.mobLore.silverfish,
			IndividualAttackType.PENETRATING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
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
		if (difficulty < 9.5)
			return 1;
		else if (difficulty < 10.5)
			return 2;
		else if (difficulty < 12)
			return 3;
		else if (difficulty < 13.5)
			return 4;
		else if (difficulty < 15.5)
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
				return 90;
			case 2:
				return 110;
			case 3:
				return 135;
			case 4:
				return 160;
			case 5:
				return 180;
			case 6:
				return 200;
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
			case 2:
				return 45;
			case 3:
			case 4:
				return 48;
			case 5:
			case 6:
				return 51;
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
			case 2:
			case 3:
				return 1;
			case 4:
			case 5:
				return 2;
			case 6:
				return 3;
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
				return 12;
			case 2:
				return 15;
			case 3:
				return 18;
			case 4:
				return 22;
			case 5:
				return 26;
			case 6:
				return 30;
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
				return 35;
			case 2:
				return 45;
			case 3:
				return 55;
			case 4:
				return 70;
			case 5:
				return 90;
			case 6:
				return 105;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
