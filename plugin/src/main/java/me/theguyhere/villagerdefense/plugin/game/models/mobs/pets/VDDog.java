package me.theguyhere.villagerdefense.plugin.game.models.mobs.pets;

import me.theguyhere.villagerdefense.plugin.exceptions.ArenaNotFoundException;
import me.theguyhere.villagerdefense.plugin.game.managers.GameManager;
import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.Objects;

public class VDDog extends VDPet {
    public VDDog(Arena arena, Location location, Player owner) {
        super(
                arena,
                (Tameable) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.WOLF),
                LanguageManager.mobs.dog,
                LanguageManager.mobLore.dog,
                AttackType.NORMAL,
                1,
                owner
        );
        ((Wolf) mob).setAdult();
        hpBarSize = 2;
//        setHealth(360, 30);
//        setArmor(10, 5);
//        setToughness(0, .03, 2);
//        setDamage(40, 5, .10);
        setModerateAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        updateNameTag();
    }

    @Override
    public VDPet respawn(Location location) {
        try {
            return new VDDog(GameManager.getArena(gameID), location, owner);
        } catch (ArenaNotFoundException e) {
            return null;
        }
    }
}
