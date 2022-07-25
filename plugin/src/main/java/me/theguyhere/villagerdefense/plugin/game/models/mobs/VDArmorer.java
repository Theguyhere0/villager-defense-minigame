package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
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
                LanguageManager.mobs.armorer,
                "Your body is this villager’s number one priority. With consent, of course.",
                getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(360, 30);
        setArmor(20, 4);
        setToughness(.02, .01, 2);
        setHeavyWeight();
        setFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.ARMORER);
        updateNameTag();
    }
}
