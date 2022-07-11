package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSkeleton extends VDMinion {
    public static final String KEY = "skel";

    protected VDSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SKELETON),
                "Skeleton",
                "A mediocre ranged monster that attacks indiscriminately.",
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        setHealth(80, 8, level, 2);
        setArmor(2, 2, level, 2);
        setToughness(0, .05, level, 8);
        setDamage(30, 4, level, 2, .1);
        setSlowAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setMediumSpeed();
        setModerateTargetRange();
        setArmorEquipment();
        setBow();
        setLoot(25, 1.15, level, .2);
        updateNameTag();
    }
}
