package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.Vindicator;

public class VDVindicator extends VDMinion {
	public static final String KEY = "vind";

	public VDVindicator(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent().getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.vindicator,
			LanguageManager.mobLore.vindicator,
			IndividualAttackType.CRUSHING
		);
		Vindicator vindicator = (Vindicator) mob;
		vindicator.setPatrolLeader(false);
		vindicator.setCanJoinRaid(false);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = 0;
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setAxe();
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
		if (difficulty < 8)
			return 1;
		else if (difficulty < 12)
			return 2;
		else if (difficulty < 15)
			return 3;
		else if (difficulty < 18)
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
				return 550;
			case 2:
				return 650;
			case 3:
				return 750;
			case 4:
				return 850;
			case 5:
				return 950;
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
			case 1:
				return .05;
			case 2:
				return .1;
			case 3:
				return .15;
			case 4:
				return .25;
			case 5:
				return .3;
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
				return 125;
			case 2:
				return 165;
			case 3:
				return 210;
			case 4:
				return 250;
			case 5:
				return 300;
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
				return 145;
			case 2:
				return 220;
			case 3:
				return 315;
			case 4:
				return 435;
			case 5:
				return 580;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
