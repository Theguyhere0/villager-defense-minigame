package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualTeam;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.InvalidVDMobKeyException;
import me.theguyhere.villagerdefense.plugin.individuals.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.items.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

/**
 * The monsters of Villager Defense.
 */
public abstract class VDMinion extends VDMob {
	protected VDMinion(Arena arena, Mob minion, String name, String lore, IndividualAttackType attackType) {
		super(lore, attackType);
		mob = minion;
		id = minion.getUniqueId();
		Main.getMonstersTeam().addEntry(id.toString());
		PersistentDataContainer dataContainer = minion.getPersistentDataContainer();
		dataContainer.set(ARENA_ID, PersistentDataType.INTEGER, arena.getId());
		dataContainer.set(TEAM, PersistentDataType.STRING, IndividualTeam.MONSTER.getValue());
		wave = arena.getCurrentWave();
		this.name = name;
		hpBarSize = 2;
		minion.setRemoveWhenFarAway(false);
		minion.setCanPickupItems(false);
		if (minion.isInsideVehicle())
			Objects
				.requireNonNull(minion.getVehicle())
				.remove();
		for (Entity passenger : minion.getPassengers())
			passenger.remove();
		minion.setHealth(2);
		Objects
			.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH))
			.addModifier(new AttributeModifier(
				"custom",
				2 - Objects
					.requireNonNull(mob.getAttribute(Attribute.GENERIC_MAX_HEALTH))
					.getBaseValue(),
				AttributeModifier.Operation.ADD_NUMBER
			));
		minion.setCustomNameVisible(true);
	}

	@Override
	protected void updateNameTag() {
		super.updateNameTag(ChatColor.RED);
	}

	protected void setArmorEquipment(boolean helmet, boolean chestplate, boolean leggings, boolean boots, boolean offset) {
		EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());

		// Helmet
		if (helmet) {
			ItemStack armor;
			switch (level + (offset ? 1 : 0)) {
				case 2:
					if (!offset)
						armor = new ItemStack(Material.LEATHER_HELMET);
					else armor = new ItemStack(Material.AIR);
					break;
				case 3:
					armor = new ItemStack(Material.CHAINMAIL_HELMET);
					break;
				case 4:
					armor = new ItemStack(Material.IRON_HELMET);
					break;
				case 5:
					armor = new ItemStack(Material.DIAMOND_HELMET);
					break;
				case 6:
					armor = new ItemStack(Material.NETHERITE_HELMET);
					break;
				case 7:
					armor = new ItemStackBuilder(Material.NETHERITE_HELMET, "")
						.setGlowingIfTrue(true)
						.build();
					break;
				default:
					armor = new ItemStack(Material.AIR);
			}
			equipment.setHelmet(armor);
		}

		// Chestplate
		if (chestplate) {
			ItemStack armor;
			switch (level) {
				case 2:
					armor = new ItemStack(Material.LEATHER_CHESTPLATE);
					break;
				case 3:
					armor = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
					break;
				case 4:
					armor = new ItemStack(Material.IRON_CHESTPLATE);
					break;
				case 5:
					armor = new ItemStack(Material.DIAMOND_CHESTPLATE);
					break;
				case 6:
					armor = new ItemStack(Material.NETHERITE_CHESTPLATE);
					break;
				case 7:
					armor = new ItemStackBuilder(Material.NETHERITE_CHESTPLATE, "")
						.setGlowingIfTrue(true)
						.build();
					break;
				default:
					armor = new ItemStack(Material.AIR);
			}
			equipment.setChestplate(armor);
		}

		// Leggings
		if (leggings) {
			ItemStack armor;
			switch (level) {
				case 2:
					armor = new ItemStack(Material.LEATHER_LEGGINGS);
					break;
				case 3:
					armor = new ItemStack(Material.CHAINMAIL_LEGGINGS);
					break;
				case 4:
					armor = new ItemStack(Material.IRON_LEGGINGS);
					break;
				case 5:
					armor = new ItemStack(Material.DIAMOND_LEGGINGS);
					break;
				case 6:
					armor = new ItemStack(Material.NETHERITE_LEGGINGS);
					break;
				case 7:
					armor = new ItemStackBuilder(Material.NETHERITE_LEGGINGS, "")
						.setGlowingIfTrue(true)
						.build();
					break;
				default:
					armor = new ItemStack(Material.AIR);
			}
			equipment.setLeggings(armor);
		}

		// Boots
		if (boots) {
			ItemStack armor;
			switch (level) {
				case 2:
					armor = new ItemStack(Material.LEATHER_BOOTS);
					break;
				case 3:
					armor = new ItemStack(Material.CHAINMAIL_BOOTS);
					break;
				case 4:
					armor = new ItemStack(Material.IRON_BOOTS);
					break;
				case 5:
					armor = new ItemStack(Material.DIAMOND_BOOTS);
					break;
				case 6:
					armor = new ItemStack(Material.NETHERITE_BOOTS);
					break;
				case 7:
					armor = new ItemStackBuilder(Material.NETHERITE_BOOTS, "")
						.setGlowingIfTrue(true)
						.build();
					break;
				default:
					armor = new ItemStack(Material.AIR);
			}
			equipment.setBoots(armor);
		}
	}

	protected void setSword(boolean offset) {
		EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
		ItemStack item;

		switch (level + (offset ? 1 : 0)) {
			case 2:
				item = new ItemStack(Material.WOODEN_SWORD);
				break;
			case 3:
				item = new ItemStack(Material.STONE_SWORD);
				break;
			case 4:
				item = new ItemStack(Material.IRON_SWORD);
				break;
			case 5:
				item = new ItemStack(Material.DIAMOND_SWORD);
				break;
			case 6:
				item = new ItemStack(Material.NETHERITE_SWORD);
				break;
			case 7:
				item = new ItemStackBuilder(Material.NETHERITE_SWORD, "")
					.setGlowingIfTrue(true)
					.build();
				break;
			default:
				item = new ItemStack(Material.AIR);
		}
		equipment.setItemInMainHand(item);
	}

	protected void setAxe(boolean offset) {
		EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
		ItemStack item;

		switch (level + (offset ? 1 : 0)) {
			case 2:
				item = new ItemStack(Material.WOODEN_AXE);
				break;
			case 3:
				item = new ItemStack(Material.STONE_AXE);
				break;
			case 4:
				item = new ItemStack(Material.IRON_AXE);
				break;
			case 5:
				item = new ItemStack(Material.DIAMOND_AXE);
				break;
			case 6:
				item = new ItemStack(Material.NETHERITE_AXE);
				break;
			case 7:
				item = new ItemStackBuilder(Material.NETHERITE_AXE, "")
					.setGlowingIfTrue(true)
					.build();
				break;
			default:
				item = new ItemStack(Material.AIR);
		}
		equipment.setItemInMainHand(item);
	}

	protected void setScythe(boolean offset) {
		EntityEquipment equipment = Objects.requireNonNull(mob.getEquipment());
		ItemStack item;

		switch (level + (offset ? 1 : 0)) {
			case 2:
				item = new ItemStack(Material.WOODEN_HOE);
				break;
			case 3:
				item = new ItemStack(Material.STONE_HOE);
				break;
			case 4:
				item = new ItemStack(Material.IRON_HOE);
				break;
			case 5:
				item = new ItemStack(Material.DIAMOND_HOE);
				break;
			case 6:
				item = new ItemStack(Material.NETHERITE_HOE);
				break;
			case 7:
				item = new ItemStackBuilder(Material.NETHERITE_HOE, "")
					.setGlowingIfTrue(true)
					.build();
				break;
			default:
				item = new ItemStack(Material.AIR);
		}
		equipment.setItemInMainHand(item);
	}

	protected void setBow() {
		if (level == 7)
			Objects
				.requireNonNull(mob.getEquipment())
				.setItemInMainHand(
					new ItemStackBuilder(Material.BOW, "")
						.setGlowingIfTrue(true)
						.build()
				);
		else Objects
			.requireNonNull(mob.getEquipment())
			.setItemInMainHand(new ItemStack(Material.BOW));
	}

	protected void setCrossbow() {
		if (level == 7)
			Objects
				.requireNonNull(mob.getEquipment())
				.setItemInMainHand(
					new ItemStackBuilder(Material.CROSSBOW, "")
						.setGlowingIfTrue(true)
						.build()
				);
		else Objects
			.requireNonNull(mob.getEquipment())
			.setItemInMainHand(new ItemStack(Material.CROSSBOW));
	}

	public static VDMinion of(String key, Arena arena, Location ground, Location air) throws InvalidVDMobKeyException {
		switch (key) {
			case VDZombie.KEY:
				return new VDZombie(arena, ground);
			case VDBabyZombie.KEY:
				return new VDBabyZombie(arena, ground);
			case VDHusk.KEY:
				return new VDHusk(arena, ground);
			case VDBabyHusk.KEY:
				return new VDBabyHusk(arena, ground);
			case VDWitherSkeleton.KEY:
				return new VDWitherSkeleton(arena, ground);
			case VDPiglinSoldier.KEY:
				return new VDPiglinSoldier(arena, ground);
			case VDPiglinSniper.KEY:
				return new VDPiglinSniper(arena, ground);
			case VDBrute.KEY:
				return new VDBrute(arena, ground);
			case VDVindicator.KEY:
				return new VDVindicator(arena, ground);
			case VDSkeleton.KEY:
				return new VDSkeleton(arena, ground);
			case VDStray.KEY:
				return new VDStray(arena, ground);
			case VDPillager.KEY:
				return new VDPillager(arena, ground);
			case VDPhantom.KEY:
				return new VDPhantom(arena, air);
			case VDBlaze.KEY:
				return new VDBlaze(arena, air);
			case VDGhast.KEY:
				return new VDGhast(arena, air);
			case VDCreeper.KEY:
				return new VDCreeper(arena, ground);
			case VDChargedCreeper.KEY:
				return new VDChargedCreeper(arena, ground);
			case VDWitch.KEY:
				return new VDWitch(arena, ground);
			case VDSpider.KEY:
				return new VDSpider(arena, ground);
			case VDCaveSpider.KEY:
				return new VDCaveSpider(arena, ground);
			case VDSilverfish.KEY:
				return new VDSilverfish(arena, ground);
			default:
				throw new InvalidVDMobKeyException();
		}
	}

	public static int getValueOf(String key, Arena arena) throws InvalidVDMobKeyException {
		switch (key) {
			case VDZombie.KEY:
				return VDZombie.getValue(arena.getCurrentDifficulty());
			case VDBabyZombie.KEY:
				return VDBabyZombie.getValue(arena.getCurrentDifficulty());
			case VDHusk.KEY:
				return VDHusk.getValue(arena.getCurrentDifficulty());
			case VDBabyHusk.KEY:
				return VDBabyHusk.getValue(arena.getCurrentDifficulty());
			case VDWitherSkeleton.KEY:
				return VDWitherSkeleton.getValue(arena.getCurrentDifficulty());
			case VDPiglinSoldier.KEY:
				return VDPiglinSoldier.getValue(arena.getCurrentDifficulty());
			case VDPiglinSniper.KEY:
				return VDPiglinSniper.getValue(arena.getCurrentDifficulty());
			case VDBrute.KEY:
				return VDBrute.getValue(arena.getCurrentDifficulty());
			case VDVindicator.KEY:
				return VDVindicator.getValue(arena.getCurrentDifficulty());
			case VDSkeleton.KEY:
				return VDSkeleton.getValue(arena.getCurrentDifficulty());
			case VDStray.KEY:
				return VDStray.getValue(arena.getCurrentDifficulty());
			case VDPillager.KEY:
				return VDPillager.getValue(arena.getCurrentDifficulty());
			case VDPhantom.KEY:
				return VDPhantom.getValue(arena.getCurrentDifficulty());
			case VDBlaze.KEY:
				return VDBlaze.getValue(arena.getCurrentDifficulty());
			case VDGhast.KEY:
				return VDGhast.getValue(arena.getCurrentDifficulty());
			case VDCreeper.KEY:
				return VDCreeper.getValue(arena.getCurrentDifficulty());
			case VDChargedCreeper.KEY:
				return VDChargedCreeper.getValue(arena.getCurrentDifficulty());
			case VDWitch.KEY:
				return VDWitch.getValue(arena.getCurrentDifficulty());
			case VDSpider.KEY:
				return VDSpider.getValue(arena.getCurrentDifficulty());
			case VDCaveSpider.KEY:
				return VDCaveSpider.getValue(arena.getCurrentDifficulty());
			case VDSilverfish.KEY:
				return VDSilverfish.getValue(arena.getCurrentDifficulty());
			default:
				throw new InvalidVDMobKeyException();
		}
	}
}
