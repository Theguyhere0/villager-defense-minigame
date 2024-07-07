package me.theguyhere.villagerdefense.plugin.individuals.mobs.golems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
			Material.IRON_INGOT,
			location
		);
		hpBarSize = 3;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		setDamage(getDamage(level), .15);
		updateNameTag();
	}

	@Override
	public void respawn(boolean forced) {
		if (forced)
			mob.remove();

		if (forced || mob.isDead()) {
			mob = NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(home, KEY);
			id = mob.getUniqueId();
			PersistentDataContainer dataContainer = mob.getPersistentDataContainer();
			dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
			dataContainer.set(TEAM, PersistentDataType.STRING, IndividualTeam.VILLAGER.getValue());
			mob.setRemoveWhenFarAway(false);
			mob.setHealth(2);
			mob.setCustomNameVisible(true);
		}
	}

	@Override
	public boolean isMaxed() {
		return level == 4;
	}

	@Override
	public ItemStack createDisplayButton() {
		return new ItemStackBuilder(buttonMat, getName())
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
		respawn(true);
		switch (level) {
			case 2:
				mob.setMaximumAir(301);
				break;
			case 3:
				mob.setMaximumAir(302);
				break;
			case 4:
				mob.setMaximumAir(303);
				break;
			default:
				mob.setMaximumAir(300);
		}
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
