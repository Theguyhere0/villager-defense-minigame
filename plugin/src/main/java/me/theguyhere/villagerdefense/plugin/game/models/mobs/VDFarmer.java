package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDFarmer extends VDVillager {
    public static final String KEY = "frmr";

    protected VDFarmer(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Farmer",
                "This guy literally provides you with the bread and butter.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(150, 15, level, 2);
        setArmor(3, 2, level, 2);
        setToughness(.02, .01, level, 2);
        setMediumWeight(villager);
        setVeryFastSpeed(villager);
        villager.setProfession(Villager.Profession.FARMER);
        updateNameTag();
    }
}
