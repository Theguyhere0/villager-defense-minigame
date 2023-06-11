package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pillager;

import java.util.Objects;

public class VDPillager extends VDMinion {
	public static final String KEY = "pill";

	public VDPillager(Arena arena, Location location) {
		super(
			arena,
			(Mob) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.PILLAGER),
			LanguageManager.mobs.pillager,
			LanguageManager.mobLore.pillager,
			IndividualAttackType.NORMAL
		);
		Pillager pillager = (Pillager) mob;
		pillager.setPatrolLeader(false);
		pillager.setCanJoinRaid(false);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		pierce = 1;
		setSlowAttackSpeed();
		setNoneKnockback();
		setMediumWeight();
		setMediumSpeed();
		targetPriority = TargetPriority.VILLAGERS;
		setFarTargetRange();
		setCrossbow();
		setLoot(getValue(arena.getCurrentDifficulty()), .3);
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
				return 5;
			case 3:
				return 10;
			case 4:
				return 15;
			case 5:
				return 25;
			case 6:
				return 30;
			case 7:
				return 35;
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
				return 110;
			case 2:
				return 125;
			case 3:
				return 145;
			case 4:
				return 165;
			case 5:
				return 185;
			case 6:
				return 210;
			case 7:
				return 235;
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
		return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 2.4);
	}
}
