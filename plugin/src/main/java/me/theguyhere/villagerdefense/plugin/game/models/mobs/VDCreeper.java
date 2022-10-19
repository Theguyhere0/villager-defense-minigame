package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.common.Utils;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDCreeper extends VDMinion {
    public static final String KEY = "crpr";

    protected VDCreeper(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.CREEPER),
                LanguageManager.mobs.creeper,
                LanguageManager.mobLore.creeper,
                getLevel(arena.getCurrentDifficulty(), 1.5, 2),
                AttackType.NORMAL
        );
        Creeper creeper = (Creeper) mob;
        creeper.setPowered(false);
        setHealth(240, 20);
        setArmor(4, 4);
        setToughness(0, .03, 4);
        setDamage(200, 10, .25);
        setVerySlowAttackSpeed();
        creeper.setMaxFuseTicks(Utils.secondsToTicks(attackSpeed));
        setHighKnockback();
        setLightWeight();
        setSlowSpeed();
        setModerateTargetRange();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }

    public VDCreeper(VDCreeper oldCreeper, Arena arena) {
        super(
                arena,
                (Mob) Objects.requireNonNull(oldCreeper.getEntity().getLocation().getWorld())
                        .spawnEntity(oldCreeper.getEntity().getLocation(), EntityType.CREEPER),
                LanguageManager.mobs.creeper,
                "A crowd control monster keeping defenders away from the front lines.",
                oldCreeper.level,
                AttackType.NORMAL
        );
        Creeper creeper = (Creeper) mob;
        creeper.setPowered(false);
        setHealth(240, 20);
        addDamage(currentHealth - oldCreeper.currentHealth, null);
        damageMap.putAll(oldCreeper.damageMap);
        setArmor(4, 4);
        setToughness(0, .03, 4);
        setDamage(200, 10, .25);
        setVerySlowAttackSpeed();
        creeper.setMaxFuseTicks(Utils.secondsToTicks(attackSpeed));
        setHighKnockback();
        setLightWeight();
        setSlowSpeed();
        setModerateTargetRange();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }
}
