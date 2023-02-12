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

public class VDWitherSkeleton extends VDMinion {
    public static final String KEY = "wskl";

    public VDWitherSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld())
                        .spawnEntity(location, EntityType.WITHER_SKELETON),
                LanguageManager.mobs.witherSkeleton,
                LanguageManager.mobLore.witherSkeleton,
                AttackType.NORMAL
        );
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .1);
        setEffectType(PotionEffectType.WITHER);
        effectLevel = getEffectLevel(level);
        effectDuration = getEffectDuration(level);
        setVeryFastAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment(true, false, false, true);
        setScythe();
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
                return 300;
            case 2:
                return 375;
            case 3:
                return 450;
            case 4:
                return 550;
            case 5:
                return 600;
            case 6:
                return 675;
            case 7:
                return 730;
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
            case 2:
                return 2;
            case 3:
                return 5;
            case 4:
                return 8;
            case 5:
                return 15;
            case 6:
                return 20;
            case 7:
                return 25;
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
    protected static int getDamage(int level) {
        switch (level) {
            case 1:
                return 35;
            case 2:
                return 45;
            case 3:
                return 55;
            case 4:
                return 60;
            case 5:
                return 70;
            case 6:
                return 80;
            case 7:
                return 85;
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
                return 6;
            case 2:
            case 5:
                return 8;
            case 3:
            case 7:
                return 12;
            case 4:
                return 15;
            case 6:
                return 10;
            default:
                return 0;
        }
    }

    /**
     * Returns the proper effect level for the mob.
     * @param level The mob's level.
     * @return The effect level for the mob.
     */
    protected static int getEffectLevel(int level) {
        switch (level) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 1;
            case 5:
            case 6:
            case 7:
                return 2;
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
        return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 1.75);
    }
}
