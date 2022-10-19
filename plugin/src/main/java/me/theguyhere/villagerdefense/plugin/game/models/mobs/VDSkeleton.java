package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSkeleton extends VDMinion {
    public static final String KEY = "skel";

    protected VDSkeleton(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SKELETON),
                LanguageManager.mobs.skeleton,
                LanguageManager.mobLore.skeleton,
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        setHealth(180, 15);
        setArmor(4, 2);
        setToughness(0, .05, 8);
        setDamage(70, 7, .1);
        setSlowAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setMediumSpeed();
        setModerateTargetRange();
        setArmorEquipment();
        setBow();
        setLoot(25, 1.15, .2);
        updateNameTag();
    }
}
