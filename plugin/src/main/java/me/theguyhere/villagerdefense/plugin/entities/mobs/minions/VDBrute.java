package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import org.bukkit.Location;

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
			Attacker.AttackType.CRUSHING
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .15);
		setArmorEquipment(false, true, false, false, true);
		setAxe(true);
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
		if (difficulty < 10)
			return 1;
		else if (difficulty < 12)
			return 2;
		else if (difficulty < 14)
			return 3;
		else if (difficulty < 16.5)
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
				return 16;
			case 2:
				return 18;
			case 3:
			case 4:
				return 20;
			case 5:
				return 22;
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
				return 270;
			case 2:
				return 380;
			case 3:
				return 520;
			case 4:
				return 695;
			case 5:
				return 910;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
