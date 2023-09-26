package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.PiglinBrute;

public class VDBrute extends VDMinion {
	public static final String KEY = "brut";

	public VDBrute(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.brute,
			LanguageManager.mobLore.brute,
			IndividualAttackType.CRUSHING
		);
		((PiglinBrute) mob).setImmuneToZombification(true);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = 0;
		toughness = getToughness(level);
		setDamage(getDamage(level), .15);
		setArmorEquipment(false, true, false, false);
		setAxe();
		setLoot(getValue(arena.getCurrentDifficulty()), .15);
		updateNameTag();
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 8)
			return 1;
		else if (difficulty < 12)
			return 2;
		else if (difficulty < 15)
			return 3;
		else if (difficulty < 18)
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
				return 825;
			case 4:
				return 975;
			case 5:
				return 1150;
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
				return .15;
			case 2:
				return .25;
			case 3:
				return .4;
			case 4:
				return .55;
			case 5:
				return .65;
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
				return 180;
			case 2:
				return 230;
			case 3:
				return 280;
			case 4:
				return 330;
			case 5:
				return 375;
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
				return 250;
			case 2:
				return 375;
			case 3:
				return 570;
			case 4:
				return 870;
			case 5:
				return 1235;
			default:
				return 0;
		}
	}
}
