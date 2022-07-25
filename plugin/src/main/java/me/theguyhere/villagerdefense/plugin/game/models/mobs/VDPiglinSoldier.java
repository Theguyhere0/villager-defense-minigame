package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;

import java.util.Objects;

public class VDPiglinSoldier extends VDMinion {
    public static final String KEY = "pgsd";

    protected VDPiglinSoldier(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PIGLIN),
                LanguageManager.mobs.piglinSoldier,
                "A highly mobile swordsman that strikes hard and fast, with a hatred toward players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NORMAL
        );
        Piglin piglinSoldier = (Piglin) mob;
        piglinSoldier.setAdult();
        piglinSoldier.setImmuneToZombification(true);
        setHealth(425, 45);
        setArmor(20, 5);
        setToughness(.05, .05, 2);
        setDamage(80, 6, .2);
        setModerateAttackSpeed();
        setHighKnockback();
        setMediumWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(45, 1.2, .25);
        updateNameTag();
    }
}
