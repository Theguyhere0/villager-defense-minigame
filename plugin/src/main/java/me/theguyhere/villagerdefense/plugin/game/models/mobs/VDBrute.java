package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PiglinBrute;

import java.util.Objects;

public class VDBrute extends VDMinion {
    public static final String KEY = "brut";

    protected VDBrute(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location,
                        EntityType.PIGLIN_BRUTE),
                "Brute",
                "The uncivilized uncle of Piglin who is  tougher, faster, and wields an axe. Hates players but " +
                        "specifically targets melee fighters.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.PENETRATING
        );
        ((PiglinBrute) mob).setImmuneToZombification(true);
        setHealth(250, 25, level, 2);
        setArmor(18, 4, level, 2);
        setToughness(.05, .03, level, 2);
        setDamage(75, 5, level, 2, .15);
        setSlowAttackSpeed();
        setLowKnockback();
        setHeavyWeight();
        setFastSpeed();
        targetPriority = TargetPriority.MELEE_PLAYERS;
        setCloseTargetRange();
        setArmorEquipment();
        setAxe();
        setLoot(50, 1.2, level, .15);
        updateNameTag();
    }
}
