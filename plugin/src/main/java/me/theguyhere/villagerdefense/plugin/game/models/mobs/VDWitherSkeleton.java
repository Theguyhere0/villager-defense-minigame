package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.WitherSkeleton;

import java.util.Objects;

public class VDWitherSkeleton extends VDMinion {
    private final WitherSkeleton witherSkeleton;
    public static final String KEY = "wskl";

    protected VDWitherSkeleton(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld())
                        .spawnEntity(location, EntityType.WITHER_SKELETON),
                "Wither Skeleton",
                "These corrupted Skeletons have weak hits, but strike fast and wither their victims.",
                getLevel(arena.getCurrentDifficulty(), 1.25, 4),
                AttackType.NORMAL
        );
        witherSkeleton = (WitherSkeleton) minion;
        setHealth(100, 8, level, 2);
        setArmor(2, 2, level, 2);
        setToughness(.1, .05, level, 2);
        setDamage(12, 2, level, 2, .1);
        setVeryFastAttackSpeed();
        setModerateKnockback(witherSkeleton);
        setLightWeight(witherSkeleton);
        setMediumLandSpeed(witherSkeleton);
        // TODO: Set and implement target priority
        // TODO: Set visual armor and weapons
        setLoot(35, 1.2, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return witherSkeleton;
    }
}
