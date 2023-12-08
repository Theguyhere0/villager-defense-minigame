package me.theguyhere.villagerdefense.plugin.individuals.mobs.pets;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Constants;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import me.theguyhere.villagerdefense.plugin.background.NMSVersion;
import me.theguyhere.villagerdefense.plugin.guis.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

public class VDCat extends VDPet {
	public static final String KEY = "kitn";

	public VDCat(Arena arena, Location location, VDPlayer owner, int level) {
		super(
			arena,
			(Tameable) NMSVersion
				.getCurrent()
				.getNmsManager()
				.spawnVDMob(location, KEY),
			LanguageManager.mobs.cat,
			LanguageManager.mobLore.cat,
			IndividualAttackType.NONE,
			2,
			Material.SALMON,
			owner
		);
		hpBarSize = 2;
		this.level = level;
		setHealth(getHealth(level));
		armor = getArmor(level);
		toughness = getToughness(level);
		updateNameTag();
	}

	@Override
	public VDPet respawn(Arena arena, Location location) {
		return new VDCat(arena, location, owner, level);
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
				return VDEgg.create(2, VDEgg.EggType.CAT);
			case 2:
				return VDEgg.create(3, VDEgg.EggType.CAT);
			case 3:
				return VDEgg.create(4, VDEgg.EggType.CAT);
			case 4:
				return VDEgg.create(5, VDEgg.EggType.CAT);
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
				return 160;
			case 2:
				return 200;
			case 3:
				return 240;
			case 4:
				return 290;
			case 5:
				return 350;
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
			case 3:
			case 4:
				return 1;
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
	public static int getToughness(int level) {
		switch (level) {
			case 1:
				return 20;
			case 2:
				return 22;
			case 3:
				return 24;
			case 4:
				return 27;
			case 5:
				return 30;
			default:
				return 0;
		}
	}

	public static int getHeal(int level) {
		switch (level) {
			case 1:
				return 5;
			case 2:
				return 7;
			case 3:
				return 10;
			case 4:
				return 15;
			case 5:
				return 20;
			default:
				return 0;
		}
	}
}
