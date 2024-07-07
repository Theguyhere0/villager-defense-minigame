package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import org.bukkit.Location;

public class VDZombie extends VDMinion {
	public static final String KEY = "zomb";

	public VDZombie(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.zombie,
			LanguageManager.mobLore.zombie,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setArmorEquipment(true, true, false, false, false);
		setSword(false);
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
				return 240;
			case 2:
				return 300;
			case 3:
				return 360;
			case 4:
				return 420;
			case 5:
				return 450;
			case 6:
				return 500;
			case 7:
				return 550;
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
			case 3:
				return 1;
			case 4:
			case 5:
				return 2;
			case 6:
				return 3;
			case 7:
				return 4;
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
				return 25;
			case 3:
			case 4:
				return 27;
			case 5:
				return 30;
			case 6:
				return 32;
			case 7:
				return 35;
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
				return 50;
			case 2:
				return 65;
			case 3:
				return 75;
			case 4:
				return 90;
			case 5:
				return 100;
			case 6:
				return 115;
			case 7:
				return 125;
			default:
				return 0;
		}
	}

	/**
	 * Returns the value this minion has given arena and wave parameters.
	 *
	 * @param difficulty Current arena difficulty.
	 * @return Value of this minion.
	 */
	protected static int getValue(double difficulty) {
		int level = getLevel(difficulty);
		switch (level) {
			case 1:
				return 40;
			case 2:
				return 65;
			case 3:
				return 85;
			case 4:
				return 115;
			case 5:
				return 135;
			case 6:
				return 170;
			case 7:
				return 205;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
