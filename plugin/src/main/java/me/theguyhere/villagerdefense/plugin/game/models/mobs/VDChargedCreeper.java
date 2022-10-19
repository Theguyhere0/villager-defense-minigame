package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDChargedCreeper extends VDMinion {
    public static final String KEY = "ccpr";

    protected VDChargedCreeper(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.CREEPER),
                LanguageManager.mobs.chargedCreeper,
                LanguageManager.mobLore.chargedCreeper,
                getLevel(arena.getCurrentDifficulty(), 1.75, 5),
                AttackType.NORMAL
        );
        Creeper creeper = (Creeper) mob;
        creeper.setPowered(true);
        setHealth(400, 50);
        setArmor(4, 4);
        setToughness(0, .03, 3);
        setDamage(400, 50, .4);
        setVerySlowAttackSpeed();
        creeper.setMaxFuseTicks(Utils.secondsToTicks(attackSpeed));
        setVeryHighKnockback();
        setLightWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setUnboundedTargetRange();
        setLoot(100, 1.4, .25);
        updateNameTag();
    }
}
