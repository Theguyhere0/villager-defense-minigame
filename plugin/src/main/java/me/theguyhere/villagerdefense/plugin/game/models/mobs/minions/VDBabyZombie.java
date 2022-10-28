package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;

import java.util.Objects;

public class VDBabyZombie extends VDMinion {
    public static final String KEY = "bzmb";

    public VDBabyZombie(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.ZOMBIE),
                LanguageManager.mobs.babyZombie,
                LanguageManager.mobLore.babyZombie,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1, 3),
                AttackType.NORMAL
        );
        ((Zombie) mob).setBaby();
        setHealth(160, 10);
        setArmor(6, 3);
        setToughness(0, .03, 2);
        setDamage(35, 3, .1);
        setFastAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(30, 1.15, .2);
        updateNameTag();
    }
}
