package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import org.bukkit.Location;

public class VDCreeper extends VDMinion {
	public static final String KEY = "crpr";

	public VDCreeper(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.creeper,
			LanguageManager.mobLore.creeper,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .25);
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
		if (difficulty < 4)
			return 1;
		else if (difficulty < 7)
			return 2;
		else if (difficulty < 10)
			return 3;
		else if (difficulty < 13)
			return 4;
		else if (difficulty < 16)
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
				return 240;
			case 2:
				return 300;
			case 3:
				return 360;
			case 4:
				return 420;
			case 5:
				return 470;
			case 6:
				return 525;
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
				return 30;
			case 3:
			case 4:
				return 32;
			case 5:
			case 6:
				return 35;
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
				return 45;
			case 2:
			case 3:
				return 47;
			case 4:
			case 5:
				return 50;
			case 6:
				return 54;
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
				return 250;
			case 3:
				return 300;
			case 4:
				return 360;
			case 5:
				return 420;
			case 6:
				return 480;
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
				return 185;
			case 2:
				return 265;
			case 3:
				return 350;
			case 4:
				return 470;
			case 5:
				return 585;
			case 6:
				return 735;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
