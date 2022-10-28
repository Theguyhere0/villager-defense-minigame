package me.theguyhere.villagerdefense.plugin.game.models.mobs.villagers;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Objects;
import java.util.Random;

public class VDNormalVillager extends VDVillager {
    public static final String KEY = "nrml";
    private static final Villager.Profession[] NORMALS = {Villager.Profession.ARMORER, Villager.Profession.BUTCHER,
            Villager.Profession.FARMER, Villager.Profession.CLERIC, Villager.Profession.CARTOGRAPHER,
            Villager.Profession.FISHERMAN, Villager.Profession.NITWIT, Villager.Profession.LIBRARIAN,
            Villager.Profession.LEATHERWORKER, Villager.Profession.MASON, Villager.Profession.NONE,
            Villager.Profession.SHEPHERD, Villager.Profession.TOOLSMITH, Villager.Profession.WEAPONSMITH};

    public VDNormalVillager(Arena arena, Location location) {
        super(
                arena,
                (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER),
                LanguageManager.mobs.villager,
                LanguageManager.mobLore.villager,
                VDMob.getLevel(arena.getCurrentDifficulty(), 2, 0)
        );
        setHealth(350, 30);
        setArmor(0, 3);
        setToughness(0, .01, 2);
        setMediumWeight();
        setVeryFastSpeed();
        ((Villager) mob).setProfession(NORMALS[(new Random()).nextInt(NORMALS.length)]);
        updateNameTag();
    }
}
