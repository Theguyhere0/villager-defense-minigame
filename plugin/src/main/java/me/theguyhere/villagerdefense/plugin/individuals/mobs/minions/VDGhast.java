package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDGhast extends VDMinion {
    public static final String KEY = "ghst";

    public VDGhast(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.GHAST),
                LanguageManager.mobs.ghast,
                LanguageManager.mobLore.ghast,
                IndividualAttackType.NORMAL
        );
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = 0;
        toughness = 0;
        setDamage(getDamage(level), .2);
        setModerateAttackSpeed();
        setHighKnockback();
        setHeavyWeight();
        setMediumSpeed();
        setFarTargetRange();
        setLoot(getValue(arena.getCurrentDifficulty()), .2);
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
        else if (difficulty < 12)
            return 3;
        else if (difficulty < 15)
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
                return 600;
            case 2:
                return 700;
            case 3:
                return 750;
            case 4:
                return 850;
            case 5:
                return 1000;
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
                return 200;
            case 2:
                return 240;
            case 3:
                return 300;
            case 4:
                return 360;
            case 5:
                return 420;
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
        return getValue(getHealth(level), 0, 0, getDamage(level), 1.15);
    }
}
