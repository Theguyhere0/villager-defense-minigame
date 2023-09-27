package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDSpider extends VDMinion {
	public static final String KEY = "spid";

	public VDSpider(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent().getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.spider,
			LanguageManager.mobLore.spider,
			IndividualAttackType.PENETRATING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
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
		if (difficulty < 2)
			return 1;
		else if (difficulty < 4)
			return 2;
		else if (difficulty < 6)
			return 3;
		else if (difficulty < 8)
			return 4;
		else if (difficulty < 10)
			return 5;
		else if (difficulty < 13)
			return 6;
		else return 7;
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
				return 200;
			case 2:
				return 230;
			case 3:
				return 265;
			case 4:
				return 300;
			case 5:
				return 330;
			case 6:
				return 360;
			case 7:
				return 400;
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
			case 2:
				return 2;
			case 3:
				return 5;
			case 4:
				return 8;
			case 5:
				return 10;
			case 6:
				return 15;
			case 7:
				return 20;
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
	protected static double getToughness(int level) {
		switch (level) {
			case 4:
				return .02;
			case 5:
				return .05;
			case 6:
				return .08;
			case 7:
				return .1;
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
				return 25;
			case 2:
				return 30;
			case 3:
				return 35;
			case 4:
				return 40;
			case 5:
				return 50;
			case 6:
				return 55;
			case 7:
				return 60;
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
				return 50;
			case 2:
				return 70;
			case 3:
				return 95;
			case 4:
				return 120;
			case 5:
				return 160;
			case 6:
				return 200;
			case 7:
				return 245;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
