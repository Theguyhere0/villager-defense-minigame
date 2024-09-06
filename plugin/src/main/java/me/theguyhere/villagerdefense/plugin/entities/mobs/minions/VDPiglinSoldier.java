package me.theguyhere.villagerdefense.plugin.entities.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.entities.Attacker;
import org.bukkit.Location;
import org.bukkit.entity.Piglin;

public class VDPiglinSoldier extends VDMinion {
	public static final String KEY = "pgsd";

	public VDPiglinSoldier(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.piglinSoldier,
			LanguageManager.mobLore.piglinSoldier,
			Attacker.AttackType.SLASHING
		);
		Piglin piglinSoldier = (Piglin) mob;
		piglinSoldier.setAdult();
		piglinSoldier.setImmuneToZombification(true);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .2);
		setArmorEquipment(false, false, true, false, true);
		setSword(true);
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
				return 425;
			case 2:
				return 525;
			case 3:
				return 650;
			case 4:
				return 750;
			case 5:
				return 875;
			case 6:
				return 1000;
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
				return 35;
			case 2:
			case 3:
				return 37;
			case 4:
			case 5:
				return 40;
			case 6:
				return 42;
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
				return 35;
			case 3:
			case 4:
				return 38;
			case 5:
			case 6:
				return 41;
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
				return 80;
			case 2:
				return 100;
			case 3:
				return 120;
			case 4:
				return 150;
			case 5:
				return 165;
			case 6:
				return 190;
			case 7:
				return 210;
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
				return 130;
			case 2:
				return 185;
			case 3:
				return 260;
			case 4:
				return 355;
			case 5:
				return 445;
			case 6:
				return 560;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
