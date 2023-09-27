package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.Piglin;

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
		Piglin piglinSniper = (Piglin) mob;
		piglinSniper.setAdult();
		piglinSniper.setImmuneToZombification(true);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .05);
		pierce = 2;
		setArmorEquipment(true, false, false, false);
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
		if (difficulty < 4)
			return 1;
		else if (difficulty < 7)
			return 2;
		else if (difficulty < 10)
			return 3;
		else if (difficulty < 12)
			return 4;
		else if (difficulty < 14)
			return 5;
		else if (difficulty < 17)
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
				return 2;
			case 3:
				return 4;
			case 4:
				return 6;
			case 5:
				return 8;
			case 6:
				return 10;
			case 7:
				return 15;
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
				return .1;
			case 2:
				return .15;
			case 3:
				return .25;
			case 4:
				return .35;
			case 5:
				return .45;
			case 6:
				return .5;
			case 7:
				return .6;
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
			case 7:
				return 250;
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
				return 225;
			case 4:
				return 315;
			case 5:
				return 400;
			case 6:
				return 510;
			case 7:
				return 675;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
