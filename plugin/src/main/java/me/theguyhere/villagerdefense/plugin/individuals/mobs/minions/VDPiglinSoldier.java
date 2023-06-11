package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;

import java.util.Objects;

public class VDPiglinSoldier extends VDMinion {
	public static final String KEY = "pgsd";

	public VDPiglinSoldier(Arena arena, Location location) {
		super(
			arena,
			(Mob) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.PIGLIN),
			LanguageManager.mobs.piglinSoldier,
			LanguageManager.mobLore.piglinSoldier,
			IndividualAttackType.CRUSHING
		);
		Piglin piglinSoldier = (Piglin) mob;
		piglinSoldier.setAdult();
		piglinSoldier.setImmuneToZombification(true);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .2);
		setModerateAttackSpeed();
		setHighKnockback();
		setMediumWeight();
		setMediumSpeed();
		targetPriority = TargetPriority.PLAYERS;
		setModerateTargetRange();
		setArmorEquipment(false, false, true, false);
		setSword();
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
			case 7:
				return 1125;
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
			case 1:
				return .05;
			case 2:
				return .1;
			case 3:
				return .15;
			case 4:
				return .2;
			case 5:
				return .3;
			case 6:
				return .35;
			case 7:
				return .4;
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
		return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), .95);
	}
}
