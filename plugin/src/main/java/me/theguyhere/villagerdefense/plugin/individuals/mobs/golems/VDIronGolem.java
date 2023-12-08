package me.theguyhere.villagerdefense.plugin.individuals.mobs.golems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;

public class VDIronGolem extends VDGolem {
	public static final String KEY = "irgl";

	public VDIronGolem(Arena arena, Location location, int level) {
		super(
			arena,
			(Golem) NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.ironGolem,
			LanguageManager.mobLore.ironGolem,
			IndividualAttackType.NORMAL,
			Material.IRON_INGOT
		);
		hpBarSize = 3;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		setDamage(getDamage(level), .15);
		updateNameTag();
	}

	@Override
	public VDGolem respawn(Arena arena, Location location) {
		return new VDIronGolem(arena, location, level);
	}

	@Override
	public ItemStack createDisplayButton() {
		return new ItemStackBuilder(buttonMat, mob.getCustomName())
			.setLores(CommunicationManager.formatDescriptionArr(
				ChatColor.GRAY, LanguageManager.messages.golemButton, Constants.LORE_CHAR_LIMIT))
			.build();
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
				return 35;
			case 2:
				return 40;
			case 3:
				return 45;
			case 4:
				return 50;
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
				return 200;
			case 2:
				return 225;
			case 3:
				return 260;
			case 4:
				return 300;
			default:
				return 0;
		}
	}
}
