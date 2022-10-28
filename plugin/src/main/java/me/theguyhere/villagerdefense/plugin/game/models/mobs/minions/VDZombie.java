package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class VDZombie extends VDMinion {
    public static final String KEY = "zomb";

    public VDZombie(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE),
                LanguageManager.mobs.zombie,
                LanguageManager.mobLore.zombie,
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.NORMAL
        );
        ((Zombie) mob).setAdult();
        setHealth(240, 20);
        setArmor(10, 5);
        setToughness(0, .04, 8);
        setDamage(50, 5, .1);
        setModerateAttackSpeed();
        setModerateKnockback();
        setMediumWeight();
        setSlowSpeed();
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(25, 1.15, .2);
        updateNameTag();
    }
}
