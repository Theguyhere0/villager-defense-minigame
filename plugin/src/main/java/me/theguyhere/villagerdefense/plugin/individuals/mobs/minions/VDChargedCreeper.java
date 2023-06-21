package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDChargedCreeper extends VDMinion {
	public static final String KEY = "ccpr";

	public VDChargedCreeper(Arena arena, Location location) {
		super(
			arena,
			(Mob) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.CREEPER),
			LanguageManager.mobs.chargedCreeper,
			LanguageManager.mobLore.chargedCreeper,
			IndividualAttackType.NORMAL
		);
		Creeper creeper = (Creeper) mob;
		creeper.setPowered(true);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .4);
//		setVerySlowAttackSpeed();
//		creeper.setMaxFuseTicks(Calculator.secondsToTicks(attackSpeed));
//		setVeryHighKnockback();
//		setLightWeight();
//		setSlowSpeed();
//		targetPriority = TargetPriority.PLAYERS;
//		setUnboundedTargetRange();
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
		if (difficulty < 5)
			return 1;
		else if (difficulty < 8)
			return 2;
		else if (difficulty < 12)
			return 3;
		else if (difficulty < 15)
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
				return 400;
			case 2:
				return 475;
			case 3:
				return 550;
			case 4:
				return 650;
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
				return 5;
			case 3:
				return 10;
			case 4:
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
			case 2:
				return .05;
			case 3:
				return .1;
			case 4:
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
				return 400;
			case 2:
				return 475;
			case 3:
				return 550;
			case 4:
				return 650;
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
		return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 1.3);
	}
}
