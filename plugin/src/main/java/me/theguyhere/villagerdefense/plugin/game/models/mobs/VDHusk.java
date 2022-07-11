package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDHusk extends VDMinion {
    public static final String KEY = "husk";

    protected VDHusk(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HUSK),
                "Husk",
                "A tougher version of the Zombie that inflicts hunger and targets players.",
                getLevel(arena.getCurrentDifficulty(), 1, 2),
                AttackType.NORMAL
        );
        ((Husk) mob).setAdult();
        setHealth(140, 15, level, 2);
        setArmor(10, 3, level, 2);
        setToughness(.05, .04, level, 2);
        setDamage(25, 3, level, 2, .1);
        setModerateAttackSpeed();
        setModerateKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(30, 1.2, level, .2);
        updateNameTag();
    }
}
