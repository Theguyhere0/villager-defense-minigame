package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDGhast extends VDMinion {
	public static final String KEY = "ghst";

	public VDGhast(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.ghast,
			LanguageManager.mobLore.ghast,
			IndividualAttackType.CRUSHING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		setDamage(getDamage(level), .2);
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
		if (difficulty < 11.5)
			return 1;
		else if (difficulty < 12.5)
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
				return 600;
			case 2:
				return 700;
			case 3:
				return 750;
			case 4:
				return 850;
			case 5:
				return 1000;
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
				return 200;
			case 2:
				return 240;
			case 3:
				return 300;
			case 4:
				return 360;
			case 5:
				return 420;
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
				return 280;
			case 2:
				return 375;
			case 3:
				return 480;
			case 4:
				return 620;
			case 5:
				return 820;
			default:
				return Integer.MAX_VALUE;
		}
	}
}