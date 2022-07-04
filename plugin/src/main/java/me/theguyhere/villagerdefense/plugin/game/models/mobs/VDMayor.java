package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDMayor extends VDVillager {
    public static final String KEY = "myer";

    protected VDMayor(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Mayor",
                "",
                getLevel(arena.getCurrentDifficulty(), 1, 0)
        );
        setHealth(120, 10, level, 2);
        setArmor(0, 1, level, 2);
        setToughness(0, .01, level, 2);
        setMediumWeight(villager);
        setVeryFastLandSpeed(villager);
        // TODO: Set appearance
        updateNameTag();
    }
}
