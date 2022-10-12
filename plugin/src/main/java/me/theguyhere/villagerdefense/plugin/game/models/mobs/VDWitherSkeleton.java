package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDWitherSkeleton extends VDMinion {
    public static final String KEY = "wskl";

    protected VDWitherSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld())
                        .spawnEntity(location, EntityType.WITHER_SKELETON),
                LanguageManager.mobs.witherSkeleton,
                LanguageManager.mobLore.witherSkeleton,
                getLevel(arena.getCurrentDifficulty(), 1.25, 4),
                AttackType.NORMAL
        );
        setHealth(250, 25);
        setArmor(4, 3);
        setToughness(.1, .05, 2);
        setDamage(25, 3, .1);
        setEffectType(PotionEffectType.WITHER);
        setEffectLevel(true);
        setEffectDuration(2, 1, true);
        setVeryFastAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setScythe();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }
}
