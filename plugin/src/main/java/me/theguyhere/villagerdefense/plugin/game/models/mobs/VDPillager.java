package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Pillager;

import java.util.Objects;

public class VDPillager extends VDMinion {
    public static final String KEY = "pill";

    protected VDPillager(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PILLAGER),
                "Pillager",
                "These corrupted Villagers wield heavy piercing crossbows and are hell-bent on hunting down " +
                        "Villagers.",
                getLevel(arena.getCurrentDifficulty(), 1.75, 4),
                AttackType.NORMAL
        );
        Pillager pillager = (Pillager) mob;
        pillager.setPatrolLeader(false);
        pillager.setCanJoinRaid(false);
        setHealth(240, 20);
        setArmor(12, 4);
        setToughness(0, .04, 4);
        setDamage(110, 10, .1);
        pierce = 1;
        setSlowAttackSpeed();
        setNoneKnockback();
        setMediumWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.VILLAGERS;
        setFarTargetRange();
        setCrossbow();
        setLoot(35, 1.3, .3);
        updateNameTag();
    }
}
