package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Stray;

import java.util.Objects;

public class VDStray extends VDMinion {
    private final Stray stray;
    public static final String KEY = "stry";

    protected VDStray(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.STRAY),
                "Stray",
                "A deep frozen cousin of the Skeleton that slows targets and seeks out players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 3),
                AttackType.NORMAL
        );
        stray = (Stray) minion;
        setHealth(110, 10, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(.05, .05, level, 0);
        setDamage(35, 4, level, 2, .1);
        setSlowAttackSpeed();
        setLowKnockback(stray);
        setLightWeight(stray);
        setMediumSpeed(stray);
        // TODO: Set and implement target priority
        setArmorEquipment();
        setBow();
        setLoot(35, 1.1, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return stray;
    }
}
