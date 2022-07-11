package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

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
        setHealth(120, 10, level, 2);
        setArmor(5, 3, level, 2);
        setToughness(0, .02, level, 2);
        setDamage(50, 5, level, 2, .15);
        setSlowAttackSpeed();
        setLowKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.VILLAGERS;
        setModerateTargetRange();
        setArmorEquipment();
        setTrident();
        setLoot(35, 1.2, level, .2);
        updateNameTag();
    }
}
