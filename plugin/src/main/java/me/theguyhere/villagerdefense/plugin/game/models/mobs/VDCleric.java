package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDCleric extends VDVillager {
    public static final String KEY = "clrc";

    protected VDCleric(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Cleric",
                "Coming from an eastern family, this villager studied 25 hours a day to become a Cleric.",
                getLevel(arena.getCurrentDifficulty(), 3, 0)
        );
        setHealth(200, 20, level, 2);
        setArmor(0, 1, level, 2);
        setToughness(.04, .02, level, 2);
        setVerySlowAttackSpeed();
        setMediumWeight();
        setFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.CLERIC);
        updateNameTag();
    }
}
