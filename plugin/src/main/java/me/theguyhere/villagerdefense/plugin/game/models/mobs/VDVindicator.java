package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vindicator;

import java.util.Objects;

public class VDVindicator extends VDMinion {
    private final Vindicator vindicator;
    public static final String KEY = "vind";

    protected VDVindicator(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VINDICATOR),
                "Vindicator",
                "A Pillager with a deep disgust towards golems. opting to fight in close quarters instead of at " +
                        "a distance.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NORMAL
        );
        vindicator = (Vindicator) minion;
        vindicator.setPatrolLeader(false);
        vindicator.setCanJoinRaid(false);
        setHealth(240, 20, level, 2);
        setArmor(15, 3, level, 2);
        setToughness(.05, .04, level, 2);
        setDamage(55, 5, level, 2, .1);
        setSlowAttackSpeed();
        setModerateKnockback(vindicator);
        setMediumWeight(vindicator);
        setMediumLandSpeed(vindicator);
        // TODO: Set and implement target priority
        // TODO: Set visual armor and weapons
        setLoot(50, 1.2, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return vindicator;
    }
}
