package me.theguyhere.villagerdefense.plugin.individuals.mobs.minions;

import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.individuals.IndividualAttackType;
import me.theguyhere.villagerdefense.plugin.background.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDCreeper extends VDMinion {
    public static final String KEY = "crpr";

    public VDCreeper(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.CREEPER),
                LanguageManager.mobs.creeper,
                LanguageManager.mobLore.creeper,
                IndividualAttackType.NORMAL
        );
        Creeper creeper = (Creeper) mob;
        creeper.setPowered(false);
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .25);
        setVerySlowAttackSpeed();
        creeper.setMaxFuseTicks(Utils.secondsToTicks(attackSpeed));
        setHighKnockback();
        setLightWeight();
        setSlowSpeed();
        setModerateTargetRange();
        setLoot(getValue(arena.getCurrentDifficulty()), .2);
        updateNameTag();
    }

    public VDCreeper(VDCreeper oldCreeper, Arena arena) {
        super(
                arena,
                (Mob) Objects.requireNonNull(oldCreeper.getEntity().getLocation().getWorld())
                        .spawnEntity(oldCreeper.getEntity().getLocation(), EntityType.CREEPER),
                LanguageManager.mobs.creeper,
                "A crowd control monster keeping defenders away from the front lines.",
                IndividualAttackType.NORMAL
        );
        Creeper creeper = (Creeper) mob;
        creeper.setPowered(false);
        level = getLevel(arena.getCurrentDifficulty());
        setHealth(getHealth(level));
        addDamage(currentHealth - oldCreeper.currentHealth, null);
        damageMap.putAll(oldCreeper.damageMap);
        armor = getArmor(level);
        toughness = getToughness(level);
        setDamage(getDamage(level), .25);
        setVerySlowAttackSpeed();
        creeper.setMaxFuseTicks(Utils.secondsToTicks(attackSpeed));
        setHighKnockback();
        setLightWeight();
        setSlowSpeed();
        setModerateTargetRange();
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
                return 240;
            case 2:
                return 300;
            case 3:
                return 360;
            case 4:
                return 420;
            case 5:
                return 475;
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
                return 12;
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
                return .1;
            case 5:
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
                return 200;
            case 2:
                return 250;
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
        return getValue(getHealth(level), getArmor(level), getToughness(level), getDamage(level), 1.25);
    }
}
