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

public class VDSnowGolem extends VDGolem {
	public static final String KEY = "sngl";

	public VDSnowGolem(Arena arena, Location location, int level) {
		super(
			arena,
			(Golem) NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.snowGolem,
			LanguageManager.mobLore.snowGolem,
			IndividualAttackType.PENETRATING,
			Material.SNOWBALL,
			location
		);
		hpBarSize = 3;
		this.level = level;
		setHealth(getHealth(level));
		toughness = getToughness(level);
		setDamage(getDamage(level), .1);
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
				return VDEgg.create(2, VDEgg.EggType.SNOW_GOLEM);
			case 2:
				return VDEgg.create(3, VDEgg.EggType.SNOW_GOLEM);
			case 3:
				return VDEgg.create(4, VDEgg.EggType.SNOW_GOLEM);
			default:
				return InventoryButtons.noUpgrade();
		}
	}

	@Override
	public void incrementLevel() {
		level++;
		respawn(true);
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
				return 750;
			case 2:
				return 900;
			case 3:
				return 1100;
			case 4:
				return 1300;
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
	public static int getToughness(int level) {
		switch (level) {
			case 1:
				return 40;
			case 2:
				return 45;
			case 3:
				return 50;
			case 4:
				return 55;
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
				return 120;
			case 2:
				return 140;
			case 3:
				return 170;
			case 4:
				return 200;
			default:
				return 0;
		}
	}
}
