package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;

import java.util.Objects;

public class VDPiglinSoldier extends VDMinion {
    private final Piglin piglinSoldier;
    public static final String KEY = "pgsd";

    protected VDPiglinSoldier(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PIGLIN),
                "Piglin Soldier",
                "A highly mobile swordsman that strikes hard and fast, with a hatred toward players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NORMAL
        );
        piglinSoldier = (Piglin) minion;
        piglinSoldier.setAdult();
        piglinSoldier.setImmuneToZombification(true);
        setHealth(180, 20, level, 2);
        setArmor(10, 3, level, 2);
        setToughness(.05, .05, level, 2);
        setDamage(40, 3, level, 2, .2);
        setModerateAttackSpeed();
        setHighKnockback(piglinSoldier);
        setMediumWeight(piglinSoldier);
        setMediumSpeed(piglinSoldier);
        // TODO: Set and implement target priority
        setArmorEquipment();
        setSword();
        setLoot(45, 1.2, level, .25);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return piglinSoldier;
    }
}
