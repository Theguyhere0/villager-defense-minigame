package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDVaultKeeper extends VDVillager {
    public static final String KEY = "keep";

    protected VDVaultKeeper(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                "Vault Keeper",
                "If you need a safe, this villager’s got you covered.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(350, 30);
        setArmor(0, 4);
        setToughness(.03, .01, 2);
        setMediumWeight();
        setVeryFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.LIBRARIAN);
        updateNameTag();
    }
}
