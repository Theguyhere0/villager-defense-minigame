package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDBlaze extends VDMinion {
    public static final String KEY = "blze";

    public VDBlaze(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.BLAZE),
                LanguageManager.mobs.blaze,
                LanguageManager.mobLore.blaze,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NONE
        );
        setHealth(360, 40);
        setArmor(20, 5);
        setToughness(.08, .04, 2);
        setEffectType(PotionEffectType.FIRE_RESISTANCE);
        setEffectDuration(6, 3, false);
        setSlowAttackSpeed();
        setNoneKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.RANGED_PLAYERS;
        setModerateTargetRange();
        setLoot(35, 1.2, .1);
        updateNameTag();
    }
}
