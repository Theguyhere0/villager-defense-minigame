package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class VDBabyDrowned extends VDMinion {
    private final Drowned babyDrowned;
    public static final String KEY = "bdwd";

    protected VDBabyDrowned(Arena arena, Location location) {
        super(
                arena,
                (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROWNED),
                "Baby Drowned",
                "The smaller and more nimble counterpart to the Drowned seems to dislike players more and opts " +
                        "to inflict weakness instead.",
                getLevel(arena.getCurrentDifficulty(), 1.5, 5),
                AttackType.PENETRATING
        );
        babyDrowned = (Drowned) minion;
        babyDrowned.setBaby();
        setHealth(80, 10, level, 2);
        setArmor(4, 2, level, 2);
        setToughness(0, .02, level, 2);
        setDamage(40, 4, level, 2, .15);
        setSlowAttackSpeed();
        setModerateKnockback(babyDrowned);
        setLightWeight(babyDrowned);
        setFastSpeed(babyDrowned);
        // TODO: Set and implement target priority
        setArmorEquipment();
        setTrident();
        setLoot(35, 1.2, level, .1);
        updateNameTag();
    }

    @Override
    public LivingEntity getEntity() {
        return babyDrowned;
    }
}
