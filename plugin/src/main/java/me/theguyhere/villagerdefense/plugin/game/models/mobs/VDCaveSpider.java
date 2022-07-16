package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDCaveSpider extends VDMinion {
    public static final String KEY = "cspd";

    protected VDCaveSpider(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location,
                        EntityType.CAVE_SPIDER),
                "Cave Spider",
                "These little Spiders are even faster and inflict poison, despite being weaker than their " +
                        "counterpart.",
                getLevel(arena.getCurrentDifficulty(), 1.25, 3),
                AttackType.PENETRATING
        );
        setHealth(80, 5, level, 2);
        setArmor(7, 3, level, 2);
        setToughness(.03, .03, level, 2);
        setDamage(6, 2, level, 2, .1);
        setEffectType(PotionEffectType.POISON);
        setEffectLevel(level, true);
        setEffectDuration(5, 1, level, true);
        setVeryFastAttackSpeed();
        setNoneKnockback();
        setVeryLightWeight();
        setVeryFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setLoot(35, 1.2, level, .2);
        updateNameTag();
    }
}
