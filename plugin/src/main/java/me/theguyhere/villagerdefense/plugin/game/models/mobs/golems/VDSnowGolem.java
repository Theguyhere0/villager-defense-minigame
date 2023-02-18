package me.theguyhere.villagerdefense.plugin.game.models.mobs.golems;

import me.theguyhere.villagerdefense.common.CommunicationManager;
import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.items.eggs.VDEgg;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.inventories.Buttons;
import me.theguyhere.villagerdefense.plugin.tools.ItemManager;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Golem;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class VDSnowGolem extends VDGolem {
    public VDSnowGolem(Arena arena, Location location, int level) {
        super(
                arena,
                (Golem) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SNOWMAN),
                LanguageManager.mobs.snowGolem,
                LanguageManager.mobLore.snowGolem,
                AttackType.NORMAL,
                Material.SNOWBALL
        );
        hpBarSize = 3;
        this.level = level;
        setHealth(getHealth(level));
        armor = 0;
        toughness = getToughness(level);
        setDamage(getDamage(level), .1);
        setSlowAttackSpeed();
        setLowKnockback();
        setHeavyWeight();
        setSlowSpeed();
        setModerateTargetRange();
        updateNameTag();
    }

    @Override
    public VDGolem respawn(Arena arena, Location location) {
        return new VDSnowGolem(arena, location, level);
    }

    @Override
    public ItemStack createDisplayButton() {
        return ItemManager.createItem(buttonMat, mob.getCustomName(), CommunicationManager.formatDescriptionList(
                ChatColor.GRAY, LanguageManager.messages.golemButton, Utils.LORE_CHAR_LIMIT));
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
                return Buttons.noUpgrade();
        }
    }

    @Override
    public void incrementLevel() {
        level++;
        setHealth(getHealth(level));
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
     * @param level The mob's level.
     * @return The toughness for the mob.
     */
    public static double getToughness(int level) {
        switch (level) {
            case 1:
                return .05;
            case 2:
                return .1;
            case 3:
                return .15;
            case 4:
                return .2;
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
                return 60;
            case 2:
                return 70;
            case 3:
                return 85;
            case 4:
                return 100;
            default:
                return 0;
        }
    }
}
