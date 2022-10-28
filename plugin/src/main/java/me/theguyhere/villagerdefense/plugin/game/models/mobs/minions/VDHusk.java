package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDHusk extends VDMinion {
    public static final String KEY = "husk";

    public VDHusk(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HUSK),
                LanguageManager.mobs.husk,
                LanguageManager.mobLore.husk,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1, 2),
                AttackType.NORMAL
        );
        ((Husk) mob).setAdult();
        setHealth(300, 30);
        setArmor(15, 5);
        setToughness(.05, .04, 2);
        setDamage(60, 6, .1);
        setEffectType(PotionEffectType.HUNGER);
        setEffectLevel(true);
        setEffectDuration(4, 2, true);
        setModerateAttackSpeed();
        setModerateKnockback();
        setMediumWeight();
        setSlowSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(30, 1.2, .2);
        updateNameTag();
    }
}
