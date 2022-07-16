package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSilverfish extends VDMinion {
    public static final String KEY = "slvr";

    protected VDSilverfish(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SILVERFISH),
                "Silverfish",
                "A little ground parasite that golems and pets are especially susceptible to.",
                getLevel(arena.getCurrentDifficulty(), 1, 4),
                AttackType.NORMAL
        );
        setHealth(90, 10);
        setArmor(2, 1);
        setToughness(.05, .05, 2);
        setDamage(12, 2, .1);
        setVeryFastAttackSpeed();
        setLowKnockback();
        setVeryLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PETS_GOLEMS;
        setModerateTargetRange();
        setLoot(20, 1.15, .1);
        updateNameTag();
    }
}
