package me.theguyhere.villagerdefense.plugin.game.models.mobs.minions;

import me.theguyhere.villagerdefense.plugin.game.models.arenas.Arena;
import me.theguyhere.villagerdefense.plugin.game.models.mobs.AttackType;
import me.theguyhere.villagerdefense.plugin.tools.LanguageManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Objects;

public class VDSpider extends VDMinion {
    public static final String KEY = "spid";

    public VDSpider(Arena arena, Location location) {
        super(
                arena,
                (Mob) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.SPIDER),
                LanguageManager.mobs.spider,
                LanguageManager.mobLore.spider,
                getLevel(arena.getCurrentDifficulty(), 1, 0),
                AttackType.PENETRATING
        );
        setHealth(200, 20);
        setArmor(10, 3);
        setToughness(.08, .04, 2);
        setDamage(20, 5, .1);
        setFastAttackSpeed();
        setNoneKnockback();
        setLightWeight();
        setFastSpeed();
        setModerateTargetRange();
        setLoot(25, 1.15, .2);
        updateNameTag();
    }
}
