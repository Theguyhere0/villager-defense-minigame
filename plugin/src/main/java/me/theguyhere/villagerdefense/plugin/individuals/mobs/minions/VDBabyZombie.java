package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;

public class VDBabyZombie extends VDMinion {
	public static final String KEY = "bzmb";

	public VDBabyZombie(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.babyZombie,
			LanguageManager.mobLore.babyZombie,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setArmorEquipment(false, true, true, false);
		setSword();
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
		if (difficulty < 3)
			return 1;
		else if (difficulty < 6)
			return 2;
		else if (difficulty < 8)
			return 3;
		else if (difficulty < 10)
			return 4;
		else if (difficulty < 12)
			return 5;
		else if (difficulty < 15)
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
				return 160;
			case 2:
				return 200;
			case 3:
				return 240;
			case 4:
				return 275;
			case 5:
				return 300;
			case 6:
				return 335;
			case 7:
				return 370;
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
				return 5;
			case 2:
				return 10;
			case 3:
				return 20;
			case 4:
				return 30;
			case 5:
				return 45;
			case 6:
				return 55;
			case 7:
				return 60;
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
				return .1;
			case 7:
				return .15;
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
				return 35;
			case 2:
				return 45;
			case 3:
				return 50;
			case 4:
				return 60;
			case 5:
				return 70;
			case 6:
				return 85;
			case 7:
				return 95;
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
				return 75;
			case 3:
				return 110;
			case 4:
				return 150;
			case 5:
				return 210;
			case 6:
				return 285;
			case 7:
				return 350;
			default:
				return 0;
		}
	}
}
