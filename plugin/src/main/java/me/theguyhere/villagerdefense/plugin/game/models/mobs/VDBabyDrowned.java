package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDBabyDrowned extends VDMinion {
    public static final String KEY = "bdwd";

    protected VDBabyDrowned(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROWNED),
                "Baby Drowned",
                "The smaller and more nimble counterpart to the Drowned seems to dislike players more and opts " +
                        "to inflict weakness instead.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 5),
                AttackType.PENETRATING
        );
        ((Drowned) mob).setBaby();
        setHealth(80, 10, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(0, .02, level, 2);
        setDamage(40, 4, level, 2, .15);
        setSlowAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setTrident();
        setLoot(35, 1.2, level, .1);
        updateNameTag();
    }
}
