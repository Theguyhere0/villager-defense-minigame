package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;

import java.util.Objects;

public class VDPiglinSniper extends VDMinion {
    public static final String KEY = "pgsn";

    protected VDPiglinSniper(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.PIGLIN),
                LanguageManager.mobs.piglinSniper,
                "A long-ranged sniper shooting heavy bolts that both penetrate armor and pierce through targets, " +
                        "aiming for players and especially ranged players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.PENETRATING
        );
        Piglin piglinSniper = (Piglin) mob;
        piglinSniper.setAdult();
        piglinSniper.setImmuneToZombification(true);
        setHealth(270, 20);
        setArmor(10, 4);
        setToughness(.1, .05, 2);
        setDamage(125, 10, .05);
        pierce = 2;
        setSlowAttackSpeed();
        setModerateKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.RANGED_PLAYERS;
        setFarTargetRange();
        setArmorEquipment();
        setCrossbow();
        setLoot(45, 1.2, .25);
        updateNameTag();
    }
}
