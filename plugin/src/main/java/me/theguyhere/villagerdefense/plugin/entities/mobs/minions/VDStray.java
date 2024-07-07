package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

public class VDStray extends VDMinion {
	public static final String KEY = "stry";

	public VDStray(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.stray,
			LanguageManager.mobLore.stray,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setEffectType(PotionEffectType.SLOW);
		effectLevel = getEffectLevel(level);
		effectDuration = getEffectDuration(level);
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
		if (difficulty < 5)
			return 1;
		else if (difficulty < 6)
			return 2;
		else if (difficulty < 7.5)
			return 3;
		else if (difficulty < 9.5)
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
				return 270;
			case 2:
				return 320;
			case 3:
				return 375;
			case 4:
				return 440;
			case 5:
				return 475;
			case 6:
				return 525;
			case 7:
				return 580;
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
				return 85;
			case 2:
				return 110;
			case 3:
				return 135;
			case 4:
				return 150;
			case 5:
				return 165;
			case 6:
				return 180;
			case 7:
				return 200;
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
			case 5:
				return 6;
			case 2:
			case 6:
				return 8;
			case 3:
			case 7:
				return 5;
			case 4:
				return 4;
			default:
				return 0;
		}
	}

	/**
	 * Returns the proper effect level for the mob.
	 *
	 * @param level The mob's level.
	 * @return The effect level for the mob.
	 */
	protected static int getEffectLevel(int level) {
		switch (level) {
			case 1:
			case 2:
			case 3:
				return 1;
			case 4:
			case 5:
			case 6:
				return 2;
			case 7:
				return 3;
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
				return 110;
			case 2:
				return 160;
			case 3:
				return 215;
			case 4:
				return 270;
			case 5:
				return 315;
			case 6:
				return 370;
			case 7:
				return 445;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
