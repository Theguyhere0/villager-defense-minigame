package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDSkeleton extends VDMinion {
	public static final String KEY = "skel";

	public VDSkeleton(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.skeleton,
			LanguageManager.mobLore.skeleton,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setArmorEquipment(true, false, false, true, false);
		setBow();
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
		else if (difficulty < 8.5)
			return 4;
		else if (difficulty < 11)
			return 5;
		else if (difficulty < 14)
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
			case 7:
				return 480;
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
				return 15;
			case 2:
			case 3:
				return 16;
			case 4:
			case 5:
				return 18;
			case 6:
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
	protected static int getToughness(int level) {
		switch (level) {
			case 1:
			case 2:
				return 15;
			case 3:
			case 4:
				return 16;
			case 5:
			case 6:
				return 18;
			case 7:
				return 20;
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
				return 70;
			case 2:
				return 90;
			case 3:
				return 110;
			case 4:
				return 125;
			case 5:
				return 135;
			case 6:
				return 160;
			case 7:
				return 175;
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
				return 70;
			case 2:
				return 100;
			case 3:
				return 150;
			case 4:
				return 200;
			case 5:
				return 235;
			case 6:
				return 290;
			case 7:
				return 340;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
