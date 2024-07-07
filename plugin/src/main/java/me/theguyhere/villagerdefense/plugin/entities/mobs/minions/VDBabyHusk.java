package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

public class VDBabyHusk extends VDMinion {
	public static final String KEY = "bhsk";

	public VDBabyHusk(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.babyHusk,
			LanguageManager.mobLore.babyHusk,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setEffectType(PotionEffectType.HUNGER);
		effectLevel = getEffectLevel(level);
		effectDuration = getEffectDuration(level);
		setArmorEquipment(false, true, true, false, false);
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
		if (difficulty < 3.8)
			return 1;
		else if (difficulty < 5.5)
			return 2;
		else if (difficulty < 7.5)
			return 3;
		else if (difficulty < 10)
			return 4;
		else if (difficulty < 12.5)
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
				return 200;
			case 2:
				return 240;
			case 3:
				return 280;
			case 4:
				return 320;
			case 5:
				return 360;
			case 6:
				return 390;
			case 7:
				return 420;
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
				return 30;
			case 3:
			case 4:
				return 32;
			case 5:
				return 35;
			case 6:
				return 37;
			case 7:
				return 40;
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
				return 40;
			case 2:
				return 50;
			case 3:
				return 55;
			case 4:
				return 65;
			case 5:
				return 75;
			case 6:
				return 90;
			case 7:
				return 105;
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
				return 3;
			case 2:
			case 5:
				return 4;
			case 3:
			case 7:
				return 6;
			case 4:
				return 8;
			case 6:
				return 5;
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
			case 4:
				return 1;
			case 5:
			case 6:
			case 7:
				return 2;
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
				return 75;
			case 2:
				return 105;
			case 3:
				return 135;
			case 4:
				return 180;
			case 5:
				return 230;
			case 6:
				return 290;
			case 7:
				return 360;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
