package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDWitherSkeleton extends VDMinion {
    public static final String KEY = "wskl";

    protected VDWitherSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld())
                        .spawnEntity(location, EntityType.WITHER_SKELETON),
                "Wither Skeleton",
                "These corrupted Skeletons have weak hits, but strike fast and wither their victims.",
                getLevel(arena.getCurrentDifficulty(), 1.25, 4),
                AttackType.NORMAL
        );
        setHealth(100, 8, level, 2);
        setArmor(2, 2, level, 2);
        setToughness(.1, .05, level, 2);
        setDamage(12, 2, level, 2, .1);
        setEffectType(PotionEffectType.WITHER);
        setEffectLevel(level, true);
        setEffectDuration(2, 1, level, true);
        setVeryFastAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setScythe();
        setLoot(35, 1.2, level, .2);
        updateNameTag();
    }
}
