package me.theguyhere.villagerdefense.plugin.individuals.mobs.pets;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.items.eggs.VDEgg;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.individuals.players.VDPlayer;
import me.theguyhere.villagerdefense.plugin.inventories.InventoryButtons;
import me.theguyhere.villagerdefense.plugin.managers.ItemManager;
import me.theguyhere.villagerdefense.plugin.managers.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDHorse extends VDPet {
    public VDHorse(Arena arena, Location location, VDPlayer owner, int level) {
        super(
                arena,
                (Tameable) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HORSE),
                LanguageManager.mobs.horse,
                LanguageManager.mobLore.horse,
                IndividualAttackType.NORMAL,
                3,
                Material.SADDLE,
                owner
        );
        ((Horse) mob).setAdult();
        ((Horse) mob).getInventory().setSaddle(new ItemStack(Material.SADDLE));
        hpBarSize = 2;
        this.level = level;
        setHealth(getHealth(level));
        armor = getArmor(level);
        ((Horse) mob).getInventory().setArmor(getDisplayArmor(level));
        toughness = getToughness(level);
        setDamage(getDamage(level), .2);
        setModerateAttackSpeed();
        setHighKnockback();
        setHeavyWeight();
        setMediumSpeed();
        updateNameTag();
    }

    @Override
    public VDPet respawn(Arena arena, Location location) {
        return new VDHorse(arena, location, owner, level);
    }

    @Override
    public ItemStack createDisplayButton() {
        return ItemManager.createItem(buttonMat, mob.getCustomName(), CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.messages.petButton, Utils.LORE_CHAR_LIMIT));
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
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .2);
        ((Horse) mob).getInventory().setArmor(getDisplayArmor(level));
        updateNameTag();
    }

    /**
     * Returns the proper health for the mob.
     * @param level The mob's level.
     * @return The health for the mob.
     */
    public static int getHealth(int level) {
        switch (level) {
            case 1:
                return 375;
            case 2:
                return 450;
            case 3:
                return 550;
            case 4:
                return 600;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper armor for the mob.
     * @param level The mob's level.
     * @return The armor for the mob.
     */
    public static int getArmor(int level) {
        switch (level) {
            case 1:
                return 5;
            case 2:
                return 15;
            case 3:
                return 25;
            case 4:
                return 30;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper toughness for the mob.
     * @param level The mob's level.
     * @return The toughness for the mob.
     */
    public static double getToughness(int level) {
        switch (level) {
            case 1:
                return .02;
            case 2:
                return .05;
            case 3:
                return .08;
            case 4:
                return .12;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper damage for the mob.
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
                return .15;
            case 3:
                return .25;
            case 4:
                return .35;
            default:
                return 0;
        }
    }
}
