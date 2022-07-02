package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Spider;

import java.util.Objects;

public class VDSpider extends VDMob {
    private final Spider spider;

    public VDSpider(Arena arena, Location location) {
        super(
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.PENETRATING
        );
        spider = (Spider) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SPIDER);
        id = spider.getUniqueId();
        setHealth(100, 10, level, 2);
        setArmor(5, 2, level, 2);
        setToughness(.08, .04, level, 2);
        setDamage(10, 3, level, 2, .1);
        setFastAttackSpeed();
        setNoneKnockback(spider);
        setLightWeight(spider);
        setFastLandSpeed(spider);
        // TODO: Set and implement target priority
        // TODO: Set visual armor and weapons
        setLoot(25, 1.15, level, 2, .2);
        setMinion(arena, spider, "Spider");
    }

    @Override
    public LivingEntity getEntity() {
        return spider;
    }
}
