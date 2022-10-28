package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSilverfish extends VDMinion {
    public static final String KEY = "slvr";

    public VDSilverfish(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SILVERFISH),
                LanguageManager.mobs.silverfish,
                LanguageManager.mobLore.silverfish,
                getLevel(arena.getCurrentDifficulty(), 1, 4),
                AttackType.NORMAL
        );
        setHealth(90, 10);
        setArmor(2, 1);
        setToughness(.05, .05, 2);
        setDamage(12, 2, .1);
        setVeryFastAttackSpeed();
        setLowKnockback();
        setVeryLightWeight();
        setMediumSpeed();
        targetPriority = TargetPriority.PETS_GOLEMS;
        setModerateTargetRange();
        setLoot(20, 1.15, .1);
        updateNameTag();
    }
}
