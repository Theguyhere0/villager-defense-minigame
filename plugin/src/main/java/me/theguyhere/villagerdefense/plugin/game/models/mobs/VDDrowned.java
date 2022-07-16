package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDDrowned extends VDMinion {
    public static final String KEY = "drwd";

    protected VDDrowned(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROWNED),
                "Drowned",
                "This underwater undead seems to despise Villagers more than players, and slows down whoever " +
                        "they strike.",
                getLevel(arena.getCurrentDifficulty(), 1.75, 5),
                AttackType.PENETRATING
        );
        ((Drowned) mob).setAdult();
        setHealth(270, 20);
        setArmor(12, 4);
        setToughness(0, .02, 2);
        setDamage(110, 10, .15);
        setEffectType(PotionEffectType.SLOW);
        setEffectLevel(false);
        setEffectDuration(2, 1, false);
        setSlowAttackSpeed();
        setLowKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.VILLAGERS;
        setModerateTargetRange();
        setArmorEquipment();
        setTrident();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }
}
