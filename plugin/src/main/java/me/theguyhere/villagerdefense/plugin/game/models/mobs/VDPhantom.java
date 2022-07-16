package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDPhantom extends VDMinion {
    public static final String KEY = "phtm";

    protected VDPhantom(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PHANTOM),
                "Phantom",
                "A nuisance of the sky, aiming to draw attention and firepower away from other more capable " +
                        "units.",
                getLevel(arena.getCurrentDifficulty(), 1.25, 2),
                AttackType.NORMAL
        );
        setHealth(180, 20);
        setArmor(8, 2);
        setToughness(.1, .05, 2);
        setDamage(100, 10, .15);
        setVerySlowAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setFarTargetRange();
        setLoot(30, 1.2, .2);
        updateNameTag();
    }
}
