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

public class VDBabyHusk extends VDMinion {
    public static final String KEY = "bhsk";

    public VDBabyHusk(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.HUSK),
                LanguageManager.mobs.babyHusk,
                LanguageManager.mobLore.babyHusk,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1, 3),
                AttackType.NORMAL
        );
        ((Husk) mob).setBaby();
        setHealth(200, 20);
        setArmor(12, 4);
        setToughness(.05, .03, 2);
        setDamage(40, 4, .1);
        setEffectType(PotionEffectType.HUNGER);
        setEffectLevel(true);
        setEffectDuration(2, 1, true);
        setFastAttackSpeed();
        setLowKnockback();
        setLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setSword();
        setLoot(35, 1.2, .2);
        updateNameTag();
    }
}
