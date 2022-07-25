package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
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
                LanguageManager.mobs.mayor,
                "Like all politicians, this villager is quite weak but has the power to provide you with just " +
                        "what you need.",
                getLevel(arena.getCurrentDifficulty(), 1, 5)
        );
        setHealth(250, 20);
        setArmor(0, 2);
        setToughness(0, .01, 2);
        setMediumWeight();
        setVeryFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.NITWIT);
        updateNameTag();
    }
}
