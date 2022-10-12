package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDCaveSpider extends VDMinion {
    public static final String KEY = "cspd";

    protected VDCaveSpider(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location,
                        EntityType.CAVE_SPIDER),
                LanguageManager.mobs.caveSpider,
                LanguageManager.mobLore.caveSpider,
                getLevel(arena.getCurrentDifficulty(), 1.25, 3),
                AttackType.PENETRATING
        );
        setHealth(200, 12);
        setArmor(15, 5);
        setToughness(.03, .03, 2);
        setDamage(15, 3, .1);
        setEffectType(PotionEffectType.POISON);
        setEffectLevel(true);
        setEffectDuration(5, 1, true);
        setVeryFastAttackSpeed();
        setNoneKnockback();
        setVeryLightWeight();
        setVeryFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }
}
