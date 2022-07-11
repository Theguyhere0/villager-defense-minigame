package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSpider extends VDMinion {
    public static final String KEY = "spid";

    protected VDSpider(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SPIDER),
                "Spider",
                "A fast-moving, fast-attacking, armor-penetrating, wall-scaling monster.",
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.PENETRATING
        );
        setHealth(80, 8, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(.08, .04, level, 2);
        setDamage(8, 2, level, 2, .1);
        setFastAttackSpeed();
        setNoneKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        setLoot(25, 1.15, level, .2);
        updateNameTag();
    }
}
