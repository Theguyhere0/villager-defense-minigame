package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Vindicator;

import java.util.Objects;

public class VDVindicator extends VDMinion {
    public static final String KEY = "vind";

    protected VDVindicator(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VINDICATOR),
                "Vindicator",
                "A Pillager with a deep disgust towards golems. opting to fight in close quarters instead of at " +
                        "a distance.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NORMAL
        );
        Vindicator vindicator = (Vindicator) mob;
        vindicator.setPatrolLeader(false);
        vindicator.setCanJoinRaid(false);
        setHealth(550, 50);
        setArmor(35, 7);
        setToughness(.05, .04, 2);
        setDamage(125, 10, .1);
        setSlowAttackSpeed();
        setModerateKnockback();
        setMediumWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.GOLEMS;
        setModerateTargetRange();
        setAxe();
        setLoot(50, 1.2, .2);
        updateNameTag();
    }
}
