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
                "Like all politicians, this villager is quite weak but has the power to provide you with just " +
                        "what you need.",
                getLevel(arena.getCurrentDifficulty(), 1, 5)
        );
        setHealth(120, 10, level, 2);
        setArmor(0, 1, level, 2);
        setToughness(0, .01, level, 2);
        setMediumWeight(villager);
        setVeryFastLandSpeed(villager);
        villager.setProfession(Villager.Profession.NITWIT);
        updateNameTag();
    }
}
