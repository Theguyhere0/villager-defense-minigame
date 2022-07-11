package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDBlaze extends VDMinion {
    public static final String KEY = "blze";

    protected VDBlaze(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.BLAZE),
                "Blaze",
                "Flaming sorcerers of the air aiming to burn down players, especially ranged players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NONE
        );
        setHealth(150, 15, level, 2);
        setArmor(10, 3, level, 2);
        setToughness(.08, .04, level, 2);
        setDamage(0, 0, level, 2, 0);
        setSlowAttackSpeed();
        setNoneKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.RANGED_PLAYERS;
        setModerateTargetRange();
        setLoot(35, 1.2, level, .1);
        updateNameTag();
    }
}
