package me.theguyhere.villagerdefense.plugin.game.models.mobs;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
import java.util.Random;

public class VDWitch extends VDMinion {
    public static final String KEY = "wtch";

    protected VDWitch(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.WITCH),
                LanguageManager.mobs.witch,
                "This brewery master can poison, slow, or weaken its targets and will target anyone that’s not " +
                        "a monster.",
                getLevel(arena.getCurrentDifficulty(), 2, 4),
                AttackType.NONE
        );
        Witch witch = (Witch) mob;
        witch.setPatrolLeader(false);
        witch.setCanJoinRaid(false);
        setHealth(600, 35);
        setArmor(10, 4);
        setToughness(.05, .02, 2);
        setEffectLevel(true);
        setEffectDuration(4, 2, true);
        setVerySlowAttackSpeed();
        setNoneKnockback();
        setMediumWeight();
        // Ignore speed set because constant drinking
        setModerateTargetRange();
        setLoot(50, 1.2, .15);
        updateNameTag();
    }

    @Override
    public PotionEffect dealEffect() {
        PotionEffectType type;
        Random r = new Random();
        switch (r.nextInt(3)) {
            case 1:
                type = PotionEffectType.WEAKNESS;
                break;
            case 2:
                type = PotionEffectType.SLOW;
                break;
            default:
                type = PotionEffectType.POISON;
        }

        return new PotionEffect(type, effectDuration, effectLevel - 1);
    }
}
