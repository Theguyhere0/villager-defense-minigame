package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;

import java.util.Objects;

public class VDSpider extends VDMinion {
    private final Spider spider;
    public static final String KEY = "spid";

    protected VDSpider(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SPIDER),
                "Spider",
                "A fast-moving, fast-attacking, armor-penetrating, wall-scaling monster.",
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.PENETRATING
        );
        spider = (Spider) minion;
        setHealth(80, 8, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(.08, .04, level, 2);
        setDamage(8, 2, level, 2, .1);
        setFastAttackSpeed();
        setNoneKnockback(spider);
        setLightWeight(spider);
        setFastLandSpeed(spider);
        // TODO: Set and implement target priority
        // TODO: Set visual armor and weapons
        setLoot(25, 1.15, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return spider;
    }
}
