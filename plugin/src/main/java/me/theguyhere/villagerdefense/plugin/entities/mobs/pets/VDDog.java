package me.theguyhere.villagerdefense.plugin.entities.mobs.pets;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.entities.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.entities.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.entities.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class VDDog extends VDPet {
	public static final String KEY = "doge";

	public VDDog(Arena arena, Location location, VDPlayer owner, int level) {
		super(
			arena,
			(Tameable) NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(Calculator.randomCircleAroundLocation(location, 1.5, true), KEY),
			LanguageManager.mobs.dog,
			LanguageManager.mobLore.dog,
			IndividualAttackType.NORMAL,
			1,
			Material.BONE,
			owner
		);
		hpBarSize = 2;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
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
				.spawnVDMob(Calculator.randomCircleAroundLocation(owner
					.getPlayer()
					.getLocation(), 1.5, true), KEY);
			((Tameable) mob).setOwner(owner.getPlayer());
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
		respawn(true);
	}

	@Override
	public boolean isMaxed() {
		return level == 5;
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
				return 250;
			case 2:
				return 300;
			case 3:
				return 360;
			case 4:
				return 430;
			case 5:
				return 510;
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
				return 12;
			case 2:
				return 15;
			case 3:
				return 17;
			case 4:
				return 20;
			case 5:
				return 22;
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
				return 15;
			case 2:
				return 18;
			case 3:
				return 20;
			case 4:
				return 22;
			case 5:
				return 25;
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
