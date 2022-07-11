package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;

import java.util.Objects;

public class VDPhantom extends VDMinion {
    private final Phantom phantom;
    public static final String KEY = "phtm";

    protected VDPhantom(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PHANTOM),
                "Phantom",
                "A nuisance of the sky, aiming to draw attention and firepower away from other more capable " +
                        "units.",
                getLevel(arena.getCurrentDifficulty(), 1.25, 2),
                AttackType.NORMAL
        );
        phantom = (Phantom) minion;
        setHealth(75, 10, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(.1, .05, level, 2);
        setDamage(45, 5, level, 2, .15);
        setSlowAttackSpeed();
        setModerateKnockback(phantom);
        setLightWeight(phantom);
        setMediumSpeed(phantom);
        // TODO: Set and implement target priority
        setLoot(30, 1.2, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return phantom;
    }
}
