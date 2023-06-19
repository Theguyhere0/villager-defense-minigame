package me.theguyhere.villagerdefense.plugin.individuals.mobs.villagers;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;
import java.util.Random;

public class VDNormalVillager extends VDVillager {
	private static final Villager.Profession[] NORMALS = {Villager.Profession.ARMORER, Villager.Profession.BUTCHER,
		Villager.Profession.FARMER, Villager.Profession.CLERIC, Villager.Profession.CARTOGRAPHER,
		Villager.Profession.FISHERMAN, Villager.Profession.NITWIT, Villager.Profession.LIBRARIAN,
		Villager.Profession.LEATHERWORKER, Villager.Profession.MASON, Villager.Profession.NONE,
		Villager.Profession.SHEPHERD, Villager.Profession.TOOLSMITH, Villager.Profession.WEAPONSMITH,
		Villager.Profession.FLETCHER};

	public VDNormalVillager(Arena arena, Location location) {
		super(
			arena,
			(Villager) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.VILLAGER),
			LanguageManager.mobs.villager,
			LanguageManager.mobLore.villager
		);
		level = getLevel(arena.getCurrentDifficulty());
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setMediumWeight();
		setVeryFastSpeed();
		((Villager) mob).setProfession(NORMALS[(new Random()).nextInt(NORMALS.length)]);
		updateNameTag();
	}

	/**
	 * Returns the proper level for the mob.
	 *
	 * @param difficulty Arena difficulty.
	 * @return The proper level for the mob.
	 */
	protected static int getLevel(double difficulty) {
		if (difficulty < 2)
			return 1;
		else if (difficulty < 4)
			return 2;
		else if (difficulty < 6)
			return 3;
		else if (difficulty < 9)
			return 4;
		else if (difficulty < 12)
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
				return 350;
			case 2:
				return 425;
			case 3:
				return 500;
			case 4:
				return 575;
			case 5:
				return 650;
			case 6:
				return 725;
			case 7:
				return 800;
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
				return 9;
			case 6:
				return 12;
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
}
