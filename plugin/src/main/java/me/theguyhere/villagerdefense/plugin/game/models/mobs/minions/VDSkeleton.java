package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSkeleton extends VDMinion {
    public static final String KEY = "skel";

    public VDSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SKELETON),
                LanguageManager.mobs.skeleton,
                LanguageManager.mobLore.skeleton,
                AttackType.NORMAL
        );
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .1);
        setSlowAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setMediumSpeed();
        setModerateTargetRange();
        setArmorEquipment(true, false, false, true);
        setBow();
        setLoot(getValue(arena.getCurrentDifficulty()), .2);
        updateNameTag();
    }

    /**
     * Returns the proper level for the mob.
     * @param difficulty Arena difficulty.
     * @return The proper level for the mob.
     */
    protected static int getLevel(double difficulty) {
        if (difficulty < 3)
            return 1;
        else if (difficulty < 5)
            return 2;
        else if (difficulty < 7)
            return 3;
        else if (difficulty < 9)
            return 4;
        else if (difficulty < 12)
            return 5;
        else if (difficulty < 15)
            return 6;
        else return 7;
    }

    /**
     * Returns the proper health for the mob.
     * @param level The mob's level.
     * @return The health for the mob.
     */
    protected static int getHealth(int level) {
        switch (level) {
            case 1:
                return 180;
            case 2:
                return 250;
            case 3:
                return 310;
            case 4:
                return 390;
            case 5:
                return 425;
            case 6:
                return 470;
            case 7:
                return 525;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper armor for the mob.
     * @param level The mob's level.
     * @return The armor for the mob.
     */
    protected static int getArmor(int level) {
        switch (level) {
            case 1:
                return 4;
            case 2:
                return 8;
            case 3:
                return 15;
            case 4:
                return 25;
            case 5:
                return 40;
            case 6:
                return 50;
            case 7:
                return 55;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper toughness for the mob.
     * @param level The mob's level.
     * @return The toughness for the mob.
     */
    protected static double getToughness(int level) {
        switch (level) {
            case 4:
                return .03;
            case 5:
                return .08;
            case 6:
                return .12;
            case 7:
                return .18;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper damage for the mob.
     * @param level The mob's level.
     * @return The damage for the mob.
     */
    protected static int getDamage(int level) {
        switch (level) {
            case 1:
                return 70;
            case 2:
                return 85;
            case 3:
                return 100;
            case 4:
                return 115;
            case 5:
                return 130;
            case 6:
                return 150;
            case 7:
                return 160;
            default:
                return 0;
        }
    }

    /**
     * Calculates the value this minion has given arena and wave parameters.
     * @param difficulty Current arena difficulty.
     * @return Value of this minion.
     */
    protected static int getValue(double difficulty) {
        int level = getLevel(difficulty);
        return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 1.2);
    }
}
