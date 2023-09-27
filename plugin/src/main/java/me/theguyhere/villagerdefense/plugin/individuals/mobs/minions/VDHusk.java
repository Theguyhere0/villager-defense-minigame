package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

public class VDHusk extends VDMinion {
	public static final String KEY = "husk";

	public VDHusk(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.husk,
			LanguageManager.mobLore.husk,
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
		setArmorEquipment(true, true, false, false);
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
		else if (difficulty < 5)
			return 2;
		else if (difficulty < 7)
			return 3;
		else if (difficulty < 9)
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
				return 300;
			case 2:
				return 375;
			case 3:
				return 450;
			case 4:
				return 550;
			case 5:
				return 600;
			case 6:
				return 675;
			case 7:
				return 730;
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
				return 15;
			case 4:
				return 20;
			case 5:
				return 30;
			case 6:
				return 35;
			case 7:
				return 40;
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
				return 60;
			case 2:
				return 80;
			case 3:
				return 90;
			case 4:
				return 105;
			case 5:
				return 120;
			case 6:
				return 130;
			case 7:
				return 145;
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
				return 6;
			case 2:
			case 5:
				return 8;
			case 3:
			case 7:
				return 12;
			case 4:
				return 15;
			case 6:
				return 10;
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
				return 55;
			case 2:
				return 90;
			case 3:
				return 120;
			case 4:
				return 170;
			case 5:
				return 220;
			case 6:
				return 270;
			case 7:
				return 330;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
