package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.TargetPriority;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.VDMob;
import org.bukkit.Location;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class VDBabyDrowned extends VDMinion {
    public static final String KEY = "bdwd";

    protected VDBabyDrowned(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROWNED),
                "Baby Drowned",
                "The smaller and more nimble counterpart to the Drowned seems to dislike players more and opts " +
                        "to inflict weakness instead.",
                VDMob.getLevel(arena.getCurrentDifficulty(), 1.5, 5),
                AttackType.PENETRATING
        );
        ((Drowned) mob).setBaby();
        setHealth(200, 20);
        setArmor(8, 3);
        setToughness(0, .02, 2);
        setDamage(90, 10, .15);
        setEffectType(PotionEffectType.WEAKNESS);
        setEffectLevel(true);
        setEffectDuration(4, 2, true);
        setSlowAttackSpeed();
        setModerateKnockback();
        setLightWeight();
        setFastSpeed();
        targetPriority = TargetPriority.PLAYERS;
        setModerateTargetRange();
        setArmorEquipment();
        setTrident();
        setLoot(35, 1.2, .1);
        updateNameTag();
    }
}
