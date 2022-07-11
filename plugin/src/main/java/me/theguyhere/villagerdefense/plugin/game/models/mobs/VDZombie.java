package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class VDZombie extends VDMinion {
    private final Zombie zombie;
    public static final String KEY = "zomb";

    protected VDZombie(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE),
                "Zombie",
                "A pretty average monster that moves slowly and attacks indiscriminately.",
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        zombie = (Zombie) minion;
        zombie.setAdult();
        setHealth(100, 10, level, 2);
        setArmor(5, 3, level, 2);
        setToughness(0, .04, level, 8);
        setDamage(20, 3, level, 2, .1);
        setModerateAttackSpeed();
        setModerateKnockback(zombie);
        setMediumWeight(zombie);
        setSlowSpeed(zombie);
        // TODO: Set and implement target priority
        setArmorEquipment();
        setSword();
        setLoot(25, 1.15, level, .2);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return zombie;
    }
}
