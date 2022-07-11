package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class VDBlaze extends VDMinion {
    private final Blaze blaze;
    public static final String KEY = "blze";

    protected VDBlaze(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.BLAZE),
                "Blaze",
                "Flaming sorcerers of the air aiming to burn down players, especially ranged players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NONE
        );
        blaze = (Blaze) minion;
        setHealth(150, 15, level, 2);
        setArmor(10, 3, level, 2);
        setToughness(.08, .04, level, 2);
        setDamage(0, 0, level, 2, 0);
        setSlowAttackSpeed();
        setNoneKnockback(blaze);
        setMediumWeight(blaze);
        setSlowSpeed(blaze);
        // TODO: Set and implement target priority
        setLoot(35, 1.2, level, .1);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return blaze;
    }
}
