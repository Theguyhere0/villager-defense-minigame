package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDStray extends VDMinion {
    public static final String KEY = "stry";

    protected VDStray(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.STRAY),
                LanguageManager.mobs.stray,
                "A deep frozen cousin of the Skeleton that slows targets and seeks out players.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 3),
                AttackType.NORMAL
        );
        setHealth(275, 25);
        setArmor(8, 4);
        setToughness(.05, .05, 0);
        setDamage(85, 9, .1);
        setEffectType(PotionEffectType.SLOW);
        setEffectLevel(true);
        setEffectDuration(4, 2, true);
        setSlowAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setBow();
        setLoot(35, 1.1, .2);
        updateNameTag();
    }
}
