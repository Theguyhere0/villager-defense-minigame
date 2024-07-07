package me.theguyhere.villagerdefense.plugin.individuals.mobs.pets;

import me.theguyhere.villagerdefense.common.Calculator;
import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class VDHorse extends VDPet {
	public static final String KEY = "hors";

	public VDHorse(Arena arena, Location location, VDPlayer owner, int level) {
		super(
			arena,
			(Tameable) NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(Calculator.randomCircleAroundLocation(location, 1.5, true), KEY),
			LanguageManager.mobs.horse,
			LanguageManager.mobLore.horse,
			IndividualAttackType.CRUSHING,
			3,
			Material.SADDLE,
			owner
		);
		((Horse) mob)
			.getInventory()
			.setSaddle(new ItemStack(Material.SADDLE));
		hpBarSize = 2;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		((Horse) mob)
			.getInventory()
			.setArmor(getDisplayArmor(level));
		toughness = getToughness(level);
		setDamage(getDamage(level), .2);
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
				return VDEgg.create(2, VDEgg.EggType.HORSE);
			case 2:
				return VDEgg.create(3, VDEgg.EggType.HORSE);
			case 3:
				return VDEgg.create(4, VDEgg.EggType.HORSE);
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
		return level == 4;
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
				return 450;
			case 2:
				return 550;
			case 3:
				return 675;
			case 4:
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
	public static int getArmor(int level) {
		switch (level) {
			case 1:
				return 15;
			case 2:
				return 25;
			case 3:
				return 35;
			case 4:
				return 45;
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
				return 20;
			case 2:
				return 22;
			case 3:
				return 24;
			case 4:
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
				return 75;
			case 2:
				return 90;
			case 3:
				return 115;
			case 4:
				return 140;
			default:
				return 0;
		}
	}

	public static ItemStack getDisplayArmor(int level) {
		switch (level) {
			case 2:
				return new ItemStack(Material.LEATHER_HORSE_ARMOR);
			case 3:
				return new ItemStack(Material.IRON_HORSE_ARMOR);
			case 4:
				return new ItemStack(Material.DIAMOND_HORSE_ARMOR);
			default:
				return null;
		}
	}

	public static double getDamageBoost(int level) {
		switch (level) {
			case 1:
				return .10;
			case 2:
				return .20;
			case 3:
				return .30;
			case 4:
				return .40;
			default:
				return 0;
		}
	}
}
