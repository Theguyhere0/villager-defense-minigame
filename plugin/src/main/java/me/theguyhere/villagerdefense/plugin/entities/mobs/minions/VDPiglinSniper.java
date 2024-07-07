package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import org.bukkit.Location;

public class VDPiglinSniper extends VDMinion {
	public static final String KEY = "pgsn";

	public VDPiglinSniper(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.piglinSniper,
			LanguageManager.mobLore.piglinSniper,
			IndividualAttackType.PENETRATING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .05);
		pierce = 2;
		setArmorEquipment(true, false, false, false, true);
		setCrossbow();
		setLoot(getValue(arena.getCurrentDifficulty()), .25);
		updateNameTag();
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 7.5)
			return 1;
		else if (difficulty < 9)
			return 2;
		else if (difficulty < 11)
			return 3;
		else if (difficulty < 13.5)
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
				return 450;
			case 6:
				return 500;
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
				return 20;
			case 3:
			case 4:
				return 22;
			case 5:
			case 6:
				return 24;
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
				return 140;
			case 3:
				return 160;
			case 4:
				return 180;
			case 5:
				return 200;
			case 6:
				return 225;
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
				return 140;
			case 2:
				return 185;
			case 3:
				return 245;
			case 4:
				return 310;
			case 5:
				return 360;
			case 6:
				return 440;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
