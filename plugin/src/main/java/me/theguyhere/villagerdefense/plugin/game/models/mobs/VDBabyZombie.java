package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class VDBabyZombie extends VDMinion {
    public static final String KEY = "bzmb";

    protected VDBabyZombie(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE),
                "Baby Zombie",
                "The smaller and faster brother of the Zombie, with overall higher damage capacity but lower " +
                        "health.",
                getLevel(arena.getCurrentDifficulty(), 1, 3),
                AttackType.NORMAL
        );
        ((Zombie) mob).setBaby();
        setHealth(70, 5, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(0, .03, level, 2);
        setDamage(15, 2, level, 2, .1);
        setFastAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(30, 1.15, level, .2);
        updateNameTag();
    }
}
