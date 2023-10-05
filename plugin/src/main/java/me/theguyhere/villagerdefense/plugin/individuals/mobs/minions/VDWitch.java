package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class VDWitch extends VDMinion {
	public static final String KEY = "wtch";

	public VDWitch(Arena arena, Location location) {
		super(
			arena,
			NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.witch,
			LanguageManager.mobLore.witch,
			IndividualAttackType.NONE
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		effectLevel = getEffectLevel(level);
		effectDuration = getEffectDuration(level);
		setLoot(getValue(arena.getCurrentDifficulty()), .15);
		updateNameTag();
	}

	@Override
	public PotionEffect dealEffect() {
		PotionEffectType type;
		Random r = new Random();
		switch (r.nextInt(3)) {
			case 1:
				type = PotionEffectType.WEAKNESS;
				break;
			case 2:
				type = PotionEffectType.SLOW;
				break;
			default:
				type = PotionEffectType.POISON;
		}

		return new PotionEffect(type, Calculator.secondsToTicks(effectDuration), effectLevel - 1);
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 10.5)
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
				return 660;
			case 3:
				return 730;
			case 4:
				return 800;
			case 5:
				return 900;
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
			case 3:
			case 4:
			case 5:
				return 2;
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
			case 2:
			case 3:
				return 1;
			case 4:
				return 2;
			case 5:
				return 4;
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
				return 6;
			case 2:
				return 8;
			case 3:
				return 12;
			case 4:
				return 5;
			case 5:
				return 7;
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
				return 2;
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
				return 310;
			case 2:
				return 340;
			case 3:
				return 380;
			case 4:
				return 420;
			case 5:
				return 480;
			default:
				return Integer.MAX_VALUE;
		}
	}
}
