package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDVex extends VDMinion {
    public static final String KEY = "vexx";

    protected VDVex(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VEX),
                LanguageManager.mobs.vex,
                LanguageManager.mobLore.vex,
                getLevel(arena.getCurrentDifficulty(), 1, 4),
                AttackType.NORMAL
        );
        setHealth(120, 12);
        setArmor(5, 2);
        setToughness(0, .02, 2);
        setDamage(25, 5, .1);
        setFastAttackSpeed();
        setNoneKnockback();
        setVeryLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PETS_GOLEMS_PLAYERS;
        setModerateTargetRange();
        setSword();
        setLoot(30, 1.2, .1);
        updateNameTag();
    }
}
