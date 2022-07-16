package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDBabyHusk extends VDMinion {
    public static final String KEY = "bhsk";

    protected VDBabyHusk(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HUSK),
                "Baby Husk",
                "The smaller and faster brother of the Husk, with overall higher damage capacity but lower " +
                        "health.",
                getLevel(arena.getCurrentDifficulty(), 1, 3),
                AttackType.NORMAL
        );
        ((Husk) mob).setBaby();
        setHealth(90, 8, level, 2);
        setArmor(6, 2, level, 2);
        setToughness(.05, .03, level, 2);
        setDamage(20, 2, level, 2, .1);
        setEffectType(PotionEffectType.HUNGER);
        setEffectLevel(level, true);
        setEffectDuration(2, 1, level, true);
        setFastAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(35, 1.2, level, .2);
        updateNameTag();
    }
}
