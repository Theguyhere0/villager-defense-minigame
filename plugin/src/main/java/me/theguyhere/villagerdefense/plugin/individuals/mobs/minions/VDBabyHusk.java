package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDBabyHusk extends VDMinion {
    public static final String KEY = "bhsk";

    public VDBabyHusk(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HUSK),
                LanguageManager.mobs.babyHusk,
                LanguageManager.mobLore.babyHusk,
                IndividualAttackType.NORMAL
        );
        ((Husk) mob).setBaby();
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .1);
        setEffectType(PotionEffectType.HUNGER);
        effectLevel = getEffectLevel(level);
        effectDuration = getEffectDuration(level);
        setFastAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment(false, true, true, false);
        setSword();
        setLoot(getValue(arena.getCurrentDifficulty()), .2);
        updateNameTag();
    }

    /**
     * Returns the proper level for the mob.
     * @param difficulty Arena difficulty.
     * @return The proper level for the mob.
     */
    protected static int getLevel(double difficulty) {
        if (difficulty < 4)
            return 1;
        else if (difficulty < 6)
            return 2;
        else if (difficulty < 9)
            return 3;
        else if (difficulty < 12)
            return 4;
        else if (difficulty < 14)
            return 5;
        else if (difficulty < 16)
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
                return 200;
            case 2:
                return 240;
            case 3:
                return 280;
            case 4:
                return 320;
            case 5:
                return 360;
            case 6:
                return 390;
            case 7:
                return 420;
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
                return 5;
            case 2:
                return 15;
            case 3:
                return 25;
            case 4:
                return 35;
            case 5:
                return 45;
            case 6:
                return 55;
            case 7:
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
                return 40;
            case 2:
                return 50;
            case 3:
                return 55;
            case 4:
                return 65;
            case 5:
                return 75;
            case 6:
                return 90;
            case 7:
                return 105;
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
                return 3;
            case 2:
            case 5:
                return 4;
            case 3:
            case 7:
                return 6;
            case 4:
                return 8;
            case 6:
                return 5;
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
        return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 2.6);
    }
}
