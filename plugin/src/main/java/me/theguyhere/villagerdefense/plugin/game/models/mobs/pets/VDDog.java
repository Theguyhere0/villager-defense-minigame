package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Wolf;

import java.util.Objects;

public class VDDog extends VDPet {
    public VDDog(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.WOLF),
                LanguageManager.mobs.dog,
                LanguageManager.mobLore.dog,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        ((Wolf) mob).setAdult();
        setHealth(360, 30);
        setArmor(10, 5);
        setToughness(0, .03, 2);
        setDamage(40, 5, .10);
        setModerateAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        updateNameTag();
    }
}
