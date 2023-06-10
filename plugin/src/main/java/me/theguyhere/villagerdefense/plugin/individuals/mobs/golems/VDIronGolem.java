package me.theguyhere.villagerdefense.plugin.individuals.mobs.golems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.game.ItemFactory;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDIronGolem extends VDGolem {
	public VDIronGolem(Arena arena, Location location, int level) {
		super(
			arena,
			(Golem) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.IRON_GOLEM),
			LanguageManager.mobs.ironGolem,
			LanguageManager.mobLore.ironGolem,
			IndividualAttackType.NORMAL,
			Material.IRON_INGOT
		);
		hpBarSize = 3;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = 0;
		setDamage(getDamage(level), .15);
		setSlowAttackSpeed();
		setVeryHighKnockback();
		setVeryHeavyWeight();
		setSlowSpeed();
		setModerateTargetRange();
		updateNameTag();
	}

	@Override
	public VDGolem respawn(Arena arena, Location location) {
		return new VDIronGolem(arena, location, level);
	}

	@Override
	public ItemStack createDisplayButton() {
		return ItemFactory.createItem(buttonMat, mob.getCustomName(), CommunicationManager.formatDescriptionList(
			ChatColor.GRAY, LanguageManager.messages.golemButton, Constants.LORE_CHAR_LIMIT));
	}

	@Override
	public ItemStack createUpgradeButton() {
		switch (level) {
			case 1:
				return VDEgg.create(2, VDEgg.EggType.IRON_GOLEM);
			case 2:
				return VDEgg.create(3, VDEgg.EggType.IRON_GOLEM);
			case 3:
				return VDEgg.create(4, VDEgg.EggType.IRON_GOLEM);
			default:
				return InventoryButtons.noUpgrade();
		}
	}

	@Override
	public void incrementLevel() {
		level++;
		setHealth(getHealth(level));
		armor = getArmor(level);
		setDamage(getDamage(level), .15);
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
				return 1200;
			case 2:
				return 1500;
			case 3:
				return 1800;
			case 4:
				return 2000;
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
			case 1:
				return 10;
			case 2:
				return 20;
			case 3:
				return 25;
			case 4:
				return 30;
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
				return 180;
			case 2:
				return 200;
			case 3:
				return 225;
			case 4:
				return 250;
			default:
				return 0;
		}
	}
}
