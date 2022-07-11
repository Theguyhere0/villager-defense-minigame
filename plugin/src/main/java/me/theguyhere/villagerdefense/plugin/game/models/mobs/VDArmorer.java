package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDArmorer extends VDVillager {
    public static final String KEY = "armr";

    protected VDArmorer(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Armorer",
                "Your body is this villager’s number one priority. With consent, of course.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(150, 15, level, 2);
        setArmor(8, 2, level, 2);
        setToughness(.02, .01, level, 2);
        setHeavyWeight();
        setFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.ARMORER);
        updateNameTag();
    }
}
