package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDStray extends VDMinion {
    public static final String KEY = "stry";

    protected VDStray(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.STRAY),
                "Stray",
                "A deep frozen cousin of the Skeleton that slows targets and seeks out players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 3),
                AttackType.NORMAL
        );
        setHealth(110, 10, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(.05, .05, level, 0);
        setDamage(35, 4, level, 2, .1);
        setEffectType(PotionEffectType.SLOW);
        setEffectLevel(level, true);
        setEffectDuration(4, 2, level, true);
        setSlowAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setBow();
        setLoot(35, 1.1, level, .2);
        updateNameTag();
    }
}
