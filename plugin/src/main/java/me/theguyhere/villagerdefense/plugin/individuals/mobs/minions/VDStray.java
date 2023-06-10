package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDStray extends VDMinion {
	public static final String KEY = "stry";

	public VDStray(Arena arena, Location location) {
		super(
			arena,
			(Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.STRAY),
			LanguageManager.mobs.stray,
			LanguageManager.mobLore.stray,
			IndividualAttackType.NORMAL
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setEffectType(PotionEffectType.SLOW);
		effectLevel = getEffectLevel(level);
		effectDuration = getEffectDuration(level);
		setSlowAttackSpeed();
		setLowKnockback();
		setLightWeight();
		setMediumSpeed();
		targetPriority = TargetPriority.PLAYERS;
		setModerateTargetRange();
		setArmorEquipment(true, false, false, true);
		setBow();
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
		else if (difficulty < 16)
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
				return 270;
			case 2:
				return 320;
			case 3:
				return 375;
			case 4:
				return 440;
			case 5:
				return 475;
			case 6:
				return 525;
			case 7:
				return 580;
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
				return 5;
			case 4:
				return 8;
			case 5:
				return 15;
			case 6:
				return 20;
			case 7:
				return 25;
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
				return 85;
			case 2:
				return 110;
			case 3:
				return 135;
			case 4:
				return 150;
			case 5:
				return 165;
			case 6:
				return 180;
			case 7:
				return 200;
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
			case 5:
				return 6;
			case 2:
			case 6:
				return 8;
			case 3:
			case 7:
				return 5;
			case 4:
				return 4;
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
				return 1;
			case 4:
			case 5:
			case 6:
				return 2;
			case 7:
				return 3;
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
		return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 1.5);
	}
}
