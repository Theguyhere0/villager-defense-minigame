package me.theguyhere.villagerdefense.plugin.game.models.mobs.villagers;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;

public class VDFletcher extends VDVillager {
    public static final String KEY = "flch";

    public VDFletcher(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                LanguageManager.mobs.fletcher,
                LanguageManager.mobLore.fletcher,
                VDMob.getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(320, 30);
        setArmor(0, 4);
        setToughness(.01, .01, 2);
        setMediumWeight();
        setFastSpeed();
        ((Villager) mob).setProfession(Villager.Profession.FLETCHER);
        updateNameTag();
    }
}
