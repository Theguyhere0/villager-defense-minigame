package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDBlaze extends VDMinion {
    public static final String KEY = "blze";

    public VDBlaze(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.BLAZE),
                LanguageManager.mobs.blaze,
                LanguageManager.mobLore.blaze,
                AttackType.NONE
        );
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setEffectType(PotionEffectType.FIRE_RESISTANCE);
        effectLevel = 1;
        effectDuration = getEffectDuration(level);
        setSlowAttackSpeed();
        setNoneKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.RANGED_PLAYERS;
        setModerateTargetRange();
        setLoot(getValue(arena.getCurrentDifficulty()), .1);
        updateNameTag();
    }

    /**
     * Returns the proper level for the mob.
     * @param difficulty Arena difficulty.
     * @return The proper level for the mob.
     */
    protected static int getLevel(double difficulty) {
        if (difficulty < 5)
            return 1;
        else if (difficulty < 8)
            return 2;
        else if (difficulty < 13)
            return 3;
        else if (difficulty < 16)
            return 4;
        else return 5;
    }

    /**
     * Returns the proper health for the mob.
     * @param level The mob's level.
     * @return The health for the mob.
     */
    protected static int getHealth(int level) {
        switch (level) {
            case 1:
                return 360;
            case 2:
                return 425;
            case 3:
                return 525;
            case 4:
                return 640;
            case 5:
                return 775;
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
                return 15;
            case 2:
                return 25;
            case 3:
                return 35;
            case 4:
                return 50;
            case 5:
                return 65;
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
            case 2:
                return .02;
            case 3:
                return .05;
            case 4:
                return .08;
            case 5:
                return .1;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper effect duration for the mob.
     * @param level The mob's level.
     * @return The effect duration for the mob.
     */
    protected static int getEffectDuration(int level) {
        switch (level) {
            case 1:
                return 5;
            case 2:
                return 8;
            case 3:
                return 12;
            case 4:
                return 15;
            case 5:
                return 20;
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
        return getValue(getHealth(level), getArmor(level), getToughness(level), 8, 20);
    }
}
