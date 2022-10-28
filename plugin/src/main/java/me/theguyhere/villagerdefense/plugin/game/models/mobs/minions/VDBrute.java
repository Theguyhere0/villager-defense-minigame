package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.PiglinBrute;

import java.util.Objects;

public class VDBrute extends VDMinion {
    public static final String KEY = "brut";

    public VDBrute(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location,
                        EntityType.PIGLIN_BRUTE),
                LanguageManager.mobs.brute,
                LanguageManager.mobLore.brute,
                VDMob.getLevel(arena.getCurrentDifficulty(), 1.5, 4),
                AttackType.PENETRATING
        );
        ((PiglinBrute) mob).setImmuneToZombification(true);
        setHealth(630, 60);
        setArmor(40, 8);
        setToughness(.05, .03, 2);
        setDamage(180, 15, .15);
        setSlowAttackSpeed();
        setLowKnockback();
        setHeavyWeight();
        setFastSpeed();
        targetPriority = TargetPriority.MELEE_PLAYERS;
        setCloseTargetRange();
        setArmorEquipment();
        setAxe();
        setLoot(50, 1.2, .15);
        updateNameTag();
    }
}
