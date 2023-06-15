package me.theguyhere.villagerdefense.plugin.individuals.mobs.pets;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDDog extends VDPet {
	public VDDog(Arena arena, Location location, VDPlayer owner, int level) {
		super(
			arena,
			(Tameable) Objects
				.requireNonNull(location.getWorld())
				.spawnEntity(location, EntityType.WOLF),
			LanguageManager.mobs.dog,
			LanguageManager.mobLore.dog,
			IndividualAttackType.NORMAL,
			1,
			Material.BONE,
			owner
		);
		((Wolf) mob).setAdult();
		hpBarSize = 2;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		setModerateAttackSpeed();
		setLowKnockback();
		setLightWeight();
		setFastSpeed();
		setModerateTargetRange();
		updateNameTag();
	}

	@Override
	public VDPet respawn(Arena arena, Location location) {
		return new VDDog(arena, location, owner, level);
	}

	@Override
	public ItemStack createDisplayButton() {
		return new ItemStackBuilder(buttonMat, mob.getCustomName())
			.setLores(CommunicationManager.formatDescriptionArr(
				ChatColor.GRAY, LanguageManager.messages.petButton, Constants.LORE_CHAR_LIMIT))
			.build();
	}

	@Override
	public ItemStack createUpgradeButton() {
		switch (level) {
			case 1:
				return VDEgg.create(2, VDEgg.EggType.DOG);
			case 2:
				return VDEgg.create(3, VDEgg.EggType.DOG);
			case 3:
				return VDEgg.create(4, VDEgg.EggType.DOG);
			case 4:
				return VDEgg.create(5, VDEgg.EggType.DOG);
			default:
				return InventoryButtons.noUpgrade();
		}
	}

	@Override
	public void incrementLevel() {
		level++;
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
		updateNameTag();
	}

	/**
	 * Returns the proper health for the mob.
	 *
	 * @param level The mob's level.
	 * @return The health for the mob.
	 */
	public static int getHealth(int level) {
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
	public static int getArmor(int level) {
		switch (level) {
			case 2:
				return 2;
			case 3:
				return 5;
			case 4:
				return 8;
			case 5:
				return 12;
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
	public static double getToughness(int level) {
		switch (level) {
			case 4:
				return .02;
			case 5:
				return .05;
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
	public static int getDamage(int level) {
		switch (level) {
			case 1:
				return 30;
			case 2:
				return 35;
			case 3:
				return 40;
			case 4:
				return 45;
			case 5:
				return 50;
			default:
				return 0;
		}
	}
}
