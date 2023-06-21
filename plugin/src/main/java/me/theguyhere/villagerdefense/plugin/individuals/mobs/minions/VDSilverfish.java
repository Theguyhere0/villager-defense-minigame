package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSilverfish extends VDMinion {
	public static final String KEY = "slvr";

	public VDSilverfish(Arena arena, Location location) {
		super(
			arena,
			(Mob) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.SILVERFISH),
			LanguageManager.mobs.silverfish,
			LanguageManager.mobLore.silverfish,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
//		setVeryFastAttackSpeed();
//		setLowKnockback();
//		setVeryLightWeight();
//		setMediumSpeed();
//		targetPriority = TargetPriority.PETS_GOLEMS;
//		setModerateTargetRange();
		setLoot(getValue(arena.getCurrentDifficulty()), .1);
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
		else if (difficulty < 6)
			return 2;
		else if (difficulty < 9)
			return 3;
		else if (difficulty < 11)
			return 4;
		else if (difficulty < 13)
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
				return 90;
			case 2:
				return 110;
			case 3:
				return 135;
			case 4:
				return 160;
			case 5:
				return 180;
			case 6:
				return 200;
			case 7:
				return 225;
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
				return 25;
			case 2:
				return 35;
			case 3:
				return 50;
			case 4:
				return 60;
			case 5:
				return 75;
			case 6:
				return 85;
			case 7:
				return 100;
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
				return .08;
			case 7:
				return .1;
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
				return 12;
			case 2:
				return 15;
			case 3:
				return 18;
			case 4:
				return 22;
			case 5:
				return 26;
			case 6:
				return 30;
			case 7:
				return 35;
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
		return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 3);
	}
}
