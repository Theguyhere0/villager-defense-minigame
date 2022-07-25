package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDGhast extends VDMinion {
    public static final String KEY = "ghst";

    protected VDGhast(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.GHAST),
                LanguageManager.mobs.ghast,
                "The giant of the sky, with no armor but ridiculous health and explosive attacks.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.NORMAL
        );
        setHealth(1250, 75);
        setDamage(200, 20, .2);
        setModerateAttackSpeed();
        setHighKnockback();
        setHeavyWeight();
        setMediumSpeed();
        setFarTargetRange();
        setLoot(60, 1.25, .2);
        updateNameTag();
    }
}
