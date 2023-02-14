package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.Main;
import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDDog extends VDPet {
    public VDDog(Arena arena, Location location, Player owner, int level) {
        super(
                arena,
                (Tameable) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.WOLF),
                LanguageManager.mobs.dog,
                LanguageManager.mobLore.dog,
                AttackType.NORMAL,
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
    public ItemStack createButton() {
        return ItemManager.createItem(buttonMat, mob.getCustomName(), CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.messages.petButton, Utils.LORE_CHAR_LIMIT));
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
     * @param level The mob's level.
     * @return The health for the mob.
     */
    public static int getHealth(int level) {
        switch (level) {
            case 1:
                return 360;
            case 2:
                return 420;
            case 3:
                return 480;
            case 4:
                return 550;
            case 5:
                return 640;
            case 6:
                return 740;
            case 7:
                return 850;
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
            case 2:
                return 2;
            case 3:
                return 5;
            case 4:
                return 8;
            case 5:
                return 12;
            case 6:
                return 15;
            case 7:
                return 20;
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
            case 4:
                return .02;
            case 5:
                return .05;
            case 6:
                return .1;
            case 7:
                return .15;
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
                return 50;
            case 2:
                return 65;
            case 3:
                return 75;
            case 4:
                return 90;
            case 5:
                return 100;
            case 6:
                return 115;
            case 7:
                return 125;
            default:
                return 0;
        }
    }
}
