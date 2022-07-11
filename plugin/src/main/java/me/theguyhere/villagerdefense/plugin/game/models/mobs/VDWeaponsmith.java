package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDWeaponsmith extends VDVillager {
    public static final String KEY = "wepn";

    protected VDWeaponsmith(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Weaponsmith",
                "While they don’t have the heart to use them, they’ll produce weapons of mass destruction for " +
                        "you.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(150, 15, level, 2);
        setArmor(5, 2, level, 2);
        setToughness(.02, .01, level, 2);
        setMediumWeight(villager);
        setVeryFastSpeed(villager);
        villager.setProfession(Villager.Profession.WEAPONSMITH);
        updateNameTag();
    }
}
