package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class VDZombie extends VDMob {
    private final Zombie zombie;

    public VDZombie(Arena arena, Location location) {
        super(
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        zombie = (Zombie) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE);
        id = zombie.getUniqueId();
        zombie.setAdult();
        setHealth(100, 10, level, 2);
        setArmor(5, 3, level, 2);
        setToughness(0, .04, level, 8);
        setDamage(20, 3, level, 2, .1);
        setModerateAttackSpeed();
        setModerateKnockback(zombie);
        setMediumWeight(zombie);
        setSlowLandSpeed(zombie);
        // TODO: Set and implement target priority
        // TODO: Set visual armor and weapons
        setLoot(25, 1.15, level, 2, .2);
        setMinion(arena, zombie, "Zombie");
    }

    @Override
    public LivingEntity getEntity() {
        return zombie;
    }
}
