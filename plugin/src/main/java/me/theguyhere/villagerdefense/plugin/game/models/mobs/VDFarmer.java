package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
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
                LanguageManager.mobs.farmer,
                "This guy literally provides you with the bread and butter.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(350, 30);
        setArmor(5, 4);
        setToughness(.02, .01, 2);
        setMediumWeight();
        setVeryFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.FARMER);
        updateNameTag();
    }
}
